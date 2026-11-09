package io.github.emberbocor.campfirekeepsfood.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import io.github.emberbocor.campfirekeepsfood.CookedFood;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(CampfireBlockEntity.class)
abstract class CampfireBlockEntityMixin {
    private int campfirekeepsfood$slot;

    @ModifyVariable(method = "cookTick", at = @At("STORE"), ordinal = 0)
    private static ItemStack campfirekeepsfood$skipCookedSlots(ItemStack stack, ServerLevel level, BlockPos pos, BlockState state,
            CampfireBlockEntity blockEntity) {
        ((CampfireBlockEntityMixin) (Object) blockEntity).campfirekeepsfood$slot = blockEntity.getItems().indexOf(stack);
        return CookedFood.isCooked(blockEntity, stack) ? ItemStack.EMPTY : stack;
    }

    @ModifyArg(method = "cookTick", index = 4, at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/Containers;dropItemStack(Lnet/minecraft/world/level/Level;DDDLnet/minecraft/world/item/ItemStack;)V"))
    private static ItemStack campfirekeepsfood$dropNothingWhenKept(Level level, double x, double y, double z, ItemStack result) {
        return CookedFood.staysOnCampfire((ServerLevel) level, result) ? ItemStack.EMPTY : result;
    }

    @ModifyVariable(method = "cookTick", ordinal = 1, at = @At(value = "INVOKE",
            target = "Lnet/minecraft/server/level/ServerLevel;sendBlockUpdated(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/state/BlockState;I)V"))
    private static ItemStack campfirekeepsfood$keepResult(ItemStack result, ServerLevel level, BlockPos pos, BlockState state,
            CampfireBlockEntity blockEntity) {
        if (!result.isEmpty()) {
            CookedFood.keep(blockEntity, ((CampfireBlockEntityMixin) (Object) blockEntity).campfirekeepsfood$slot, result);
        }
        return result;
    }
}
