package io.github.emberbocor.campfirekeepsfood.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import io.github.emberbocor.campfirekeepsfood.CookedFood;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(CampfireBlockEntity.class)
abstract class CampfireBlockEntityMixin {
    @ModifyVariable(method = "cookTick", at = @At("STORE"), ordinal = 0)
    private static ItemStack campfirekeepsfood$skipCookedSlots(ItemStack stack, Level level, BlockPos pos, BlockState state,
            CampfireBlockEntity blockEntity) {
        return CookedFood.isCooked(blockEntity, stack) ? ItemStack.EMPTY : stack;
    }

    @ModifyArg(method = "cookTick", index = 4, at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/Containers;dropItemStack(Lnet/minecraft/world/level/Level;DDDLnet/minecraft/world/item/ItemStack;)V"))
    private static ItemStack campfirekeepsfood$dropNothingWhenKept(Level level, double x, double y, double z, ItemStack result) {
        return CookedFood.staysOnCampfire(level, result) ? ItemStack.EMPTY : result;
    }

    @Inject(method = "cookTick", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/Level;sendBlockUpdated(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/state/BlockState;I)V"))
    private static void campfirekeepsfood$keepResult(Level level, BlockPos pos, BlockState state, CampfireBlockEntity blockEntity,
            CallbackInfo ci, boolean changed, int slot, ItemStack ingredient, SingleRecipeInput input, ItemStack result) {
        if (CookedFood.staysOnCampfire(level, result)) {
            CookedFood.keep(blockEntity, slot, result);
        }
    }
}
