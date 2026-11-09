package io.github.emberbocor.campfirekeepsfood;

import io.github.emberbocor.campfirekeepsfood.mixin.CampfireBlockEntityAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

public final class CookedFood {
    /** Value of a slot in {@code CampfireBlockEntity#cookingTime} that holds a finished result. Saved with the campfire. */
    private static final int COOKED = Integer.MIN_VALUE;

    private CookedFood() {
    }

    public static boolean staysOnCampfire(ServerLevel level, ItemStack result) {
        return level.recipeAccess().getRecipeFor(RecipeType.CAMPFIRE_COOKING, new SingleRecipeInput(result), level).isEmpty();
    }

    public static boolean isCooked(CampfireBlockEntity blockEntity, ItemStack stack) {
        return cookingTime(blockEntity)[blockEntity.getItems().indexOf(stack)] == COOKED;
    }

    public static void keep(CampfireBlockEntity blockEntity, int slot, ItemStack result) {
        blockEntity.getItems().set(slot, result);
        ((CampfireBlockEntityAccessor) blockEntity).campfirekeepsfood$getCookingProgress()[slot] = 0;
        cookingTime(blockEntity)[slot] = COOKED;
    }

    public static boolean takeAll(Level level, BlockPos pos, BlockState state, CampfireBlockEntity blockEntity, Player player) {
        int[] cookingTime = cookingTime(blockEntity);
        NonNullList<ItemStack> items = blockEntity.getItems();
        boolean took = false;

        for (int slot = 0; slot < items.size(); slot++) {
            if (cookingTime[slot] == COOKED && !items.get(slot).isEmpty()) {
                player.getInventory().placeItemBackInInventory(items.set(slot, ItemStack.EMPTY));
                cookingTime[slot] = 0;
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

    private static int[] cookingTime(CampfireBlockEntity blockEntity) {
        return ((CampfireBlockEntityAccessor) blockEntity).campfirekeepsfood$getCookingTime();
    }
}
