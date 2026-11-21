package io.github.emberbocor.campfirekeepsfood;

import java.util.Arrays;
import java.util.Optional;

import io.github.emberbocor.campfirekeepsfood.config.ServerConfig;
import io.github.emberbocor.campfirekeepsfood.mixin.CampfireBlockEntityAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public final class CookedFood {
    /** Value of a slot in {@code CampfireBlockEntity#cookingTime} that holds a finished result. Saved with the campfire. */
    private static final int COOKED = Integer.MIN_VALUE;
    private static final String STAGES_TAG = CampfireKeepsFood.MODID + ":CookingStages";

    private CookedFood() {
    }

    public static boolean staysOnCampfire(ServerLevel level, ItemStack result) {
        return ServerConfig.settings().cookInStages() || recipe(level, result).isEmpty();
    }

    public static boolean isCooked(CampfireBlockEntity blockEntity, ItemStack stack) {
        return cookingTime(blockEntity)[blockEntity.getItems().indexOf(stack)] == COOKED;
    }

    /** Keeps a finished result in its slot: cooks it as the next stage, or marks it cooked. */
    public static void keep(ServerLevel level, CampfireBlockEntity blockEntity, int slot, ItemStack result) {
        ServerConfig.Settings settings = ServerConfig.settings();
        int[] stages = stages(blockEntity);
        stages[slot]++;
        Optional<RecipeHolder<CampfireCookingRecipe>> nextStage = settings.cookInStages() && stages[slot] < settings.maxStages()
                ? recipe(level, result) : Optional.empty();

        blockEntity.getItems().set(slot, result);
        ((CampfireBlockEntityAccessor) blockEntity).campfirekeepsfood$getCookingProgress()[slot] = 0;
        cookingTime(blockEntity)[slot] = nextStage.map(recipe -> recipe.value().cookingTime()).orElse(COOKED);
    }

    public static void thrownOff(CampfireBlockEntity blockEntity, int slot) {
        stages(blockEntity)[slot] = 0;
    }

    public static boolean takeAll(Level level, BlockPos pos, BlockState state, CampfireBlockEntity blockEntity, Player player) {
        int[] cookingProgress = ((CampfireBlockEntityAccessor) blockEntity).campfirekeepsfood$getCookingProgress();
        int[] cookingTime = cookingTime(blockEntity);
        int[] stages = stages(blockEntity);
        NonNullList<ItemStack> items = blockEntity.getItems();
        boolean took = false;

        for (int slot = 0; slot < items.size(); slot++) {
            if ((cookingTime[slot] == COOKED || stages[slot] > 0) && !items.get(slot).isEmpty()) {
                player.getInventory().placeItemBackInInventory(items.set(slot, ItemStack.EMPTY));
                cookingProgress[slot] = 0;
                cookingTime[slot] = 0;
                stages[slot] = 0;
                took = true;
            }
        }

        if (took) {
            level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F,
                    ((player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
            level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, state));
            blockEntity.setChanged();
            level.sendBlockUpdated(pos, state, state, 3);
        }

        return took;
    }

    /** Called before new food goes in: vanilla code (commands, {@code clearContent}) can empty a slot without resetting its stage. */
    public static void resetEmptySlots(int[] stages, NonNullList<ItemStack> items) {
        for (int slot = 0; slot < items.size(); slot++) {
            if (items.get(slot).isEmpty()) {
                stages[slot] = 0;
            }
        }
    }

    public static void loadStages(int[] stages, ValueInput input) {
        Arrays.fill(stages, 0);
        input.getIntArray(STAGES_TAG).ifPresent(saved -> System.arraycopy(saved, 0, stages, 0, Math.min(stages.length, saved.length)));
    }

    public static void saveStages(int[] stages, ValueOutput output) {
        output.putIntArray(STAGES_TAG, stages.clone());
    }

    private static Optional<RecipeHolder<CampfireCookingRecipe>> recipe(ServerLevel level, ItemStack stack) {
        return level.recipeAccess().getRecipeFor(RecipeType.CAMPFIRE_COOKING, new SingleRecipeInput(stack), level);
    }

    private static int[] cookingTime(CampfireBlockEntity blockEntity) {
        return ((CampfireBlockEntityAccessor) blockEntity).campfirekeepsfood$getCookingTime();
    }

    private static int[] stages(CampfireBlockEntity blockEntity) {
        return ((CookingStages) blockEntity).campfirekeepsfood$getCookingStages();
    }
}
