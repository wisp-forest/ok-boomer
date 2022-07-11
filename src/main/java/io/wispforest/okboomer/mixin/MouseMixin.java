package io.wispforest.okboomer.mixin;

import io.wispforest.okboomer.OkBoomer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public class MouseMixin {

    @Inject(method = "onMouseScroll", at = @At("HEAD"), cancellable = true)
    private void scrollBoomer(long window, double horizontal, double vertical, CallbackInfo ci) {
        if (OkBoomer.currentlyScreenBooming && MinecraftClient.getInstance().currentScreen != null) {
            OkBoomer.screenBoom = Math.min(
                    Math.max(
                            OkBoomer.minBoom(),
                            OkBoomer.screenBoom + vertical * .2 * OkBoomer.screenBoom
                    ),
                    OkBoomer.maxScreenBoom()
            );
            ci.cancel();
        } else if (OkBoomer.booming) {
            OkBoomer.boomDivisor = Math.min(
                    Math.max(
                            OkBoomer.minBoom(),
                            OkBoomer.boomDivisor + vertical * (OkBoomer.boomDivisor / 10) * OkBoomer.CONFIG.boomScrollSensitivity()
                    ),
                    OkBoomer.maxBoom()
            );
            ci.cancel();
        }
    }

    @ModifyArg(method = "updateMouse", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;changeLookDirection(DD)V"), index = 0)
    private double boomSensitivityX(double x) {
        if (!OkBoomer.booming) return x;
        return x / OkBoomer.boomDivisor;
    }

    @ModifyArg(method = "updateMouse", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;changeLookDirection(DD)V"), index = 1)
    private double boomSensitivityY(double y) {
        if (!OkBoomer.booming) return y;
        return y / OkBoomer.boomDivisor;
    }

}
