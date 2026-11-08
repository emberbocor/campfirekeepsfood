package io.github.emberbocor.campfirekeepsfood.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.emberbocor.campfirekeepsfood.CookedFood;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

@Mixin(CampfireBlock.class)
abstract class CampfireBlockMixin {
    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void campfirekeepsfood$takeCookedFood(BlockState state, Level level, BlockPos pos, Player player,
            InteractionHand hand, BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> cir) {
        if (!level.isClientSide && hand == InteractionHand.MAIN_HAND && player.getItemInHand(hand).isEmpty()
                && level.getBlockEntity(pos) instanceof CampfireBlockEntity blockEntity
                && CookedFood.takeAll(level, pos, state, blockEntity, player)) {
            cir.setReturnValue(InteractionResult.SUCCESS);
        }
    }
}
