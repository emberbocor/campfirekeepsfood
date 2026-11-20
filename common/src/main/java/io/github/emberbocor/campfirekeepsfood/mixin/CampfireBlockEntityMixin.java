package io.github.emberbocor.campfirekeepsfood.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.emberbocor.campfirekeepsfood.CookedFood;
import io.github.emberbocor.campfirekeepsfood.CookingStages;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(CampfireBlockEntity.class)
abstract class CampfireBlockEntityMixin implements CookingStages {
    private int campfirekeepsfood$slot;

    private final int[] campfirekeepsfood$cookingStages = new int[4];

    @Override
    public int[] campfirekeepsfood$getCookingStages() {
        return campfirekeepsfood$cookingStages;
    }

    @ModifyVariable(method = "cookTick", at = @At("STORE"), ordinal = 0)
    private static ItemStack campfirekeepsfood$skipCookedSlots(ItemStack stack, Level level, BlockPos pos, BlockState state,
            CampfireBlockEntity blockEntity) {
        ((CampfireBlockEntityMixin) (Object) blockEntity).campfirekeepsfood$slot = blockEntity.getItems().indexOf(stack);
        return CookedFood.isCooked(blockEntity, stack) ? ItemStack.EMPTY : stack;
    }

    @ModifyArg(method = "cookTick", index = 4, at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/Containers;dropItemStack(Lnet/minecraft/world/level/Level;DDDLnet/minecraft/world/item/ItemStack;)V"))
    private static ItemStack campfirekeepsfood$dropNothingWhenKept(Level level, double x, double y, double z, ItemStack result) {
        return CookedFood.staysOnCampfire(level, result) ? ItemStack.EMPTY : result;
    }

    @ModifyVariable(method = "cookTick", ordinal = 1, at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/Level;sendBlockUpdated(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/state/BlockState;I)V"))
    private static ItemStack campfirekeepsfood$keepResult(ItemStack result, Level level, BlockPos pos, BlockState state,
            CampfireBlockEntity blockEntity) {
        int slot = ((CampfireBlockEntityMixin) (Object) blockEntity).campfirekeepsfood$slot;
        if (result.isEmpty()) {
            CookedFood.thrownOff(blockEntity, slot);
        } else {
            CookedFood.keep(level, blockEntity, slot, result);
        }
        return result;
    }

    @Inject(method = "placeFood", at = @At("HEAD"))
    private void campfirekeepsfood$resetEmptySlots(Entity entity, ItemStack food, int cookTime, CallbackInfoReturnable<Boolean> cir) {
        CookedFood.resetEmptySlots(campfirekeepsfood$cookingStages, ((CampfireBlockEntity) (Object) this).getItems());
    }

    @Inject(method = "load", at = @At("TAIL"))
    private void campfirekeepsfood$loadCookingStages(CompoundTag tag, CallbackInfo ci) {
        CookedFood.loadStages(campfirekeepsfood$cookingStages, tag);
    }

    @Inject(method = "saveAdditional", at = @At("TAIL"))
    private void campfirekeepsfood$saveCookingStages(CompoundTag tag, CallbackInfo ci) {
        CookedFood.saveStages(campfirekeepsfood$cookingStages, tag);
    }
}
