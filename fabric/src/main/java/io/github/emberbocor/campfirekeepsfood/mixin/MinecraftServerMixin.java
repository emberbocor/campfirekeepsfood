package io.github.emberbocor.campfirekeepsfood.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.emberbocor.campfirekeepsfood.config.ServerConfig;
import net.minecraft.server.MinecraftServer;

@Mixin(MinecraftServer.class)
abstract class MinecraftServerMixin {
    @Inject(method = "runServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;initServer()Z"))
    private void campfirekeepsfood$loadConfig(CallbackInfo ci) {
        ServerConfig.load();
    }
}
