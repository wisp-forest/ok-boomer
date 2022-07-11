package io.wispforest.okboomer.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import io.wispforest.okboomer.OkBoomer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    private double boom$lastBoomDivisor = OkBoomer.boomDivisor;

    private float boom$lastScreenBoom = (float) OkBoomer.screenBoom;
    private float boom$lastMouseX = 0, boom$lastMouseY = 0;
    private boolean boom$screenBoomEnabled = false;

    @Inject(method = "getFov", at = @At("RETURN"), cancellable = true)
    private void injectBoomer(Camera camera, float tickDelta, boolean changingFov, CallbackInfoReturnable<Double> cir) {
        cir.setReturnValue(cir.getReturnValueD() / this.boom$lastBoomDivisor);

        if (OkBoomer.CONFIG.boomTransition()) {
            this.boom$lastBoomDivisor += .45 * (OkBoomer.boomDivisor - this.boom$lastBoomDivisor) * boom$interpolator();
        } else {
            this.boom$lastBoomDivisor = OkBoomer.boomDivisor;
        }
    }

    @Inject(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screen/Screen;render(Lnet/minecraft/client/util/math/MatrixStack;IIF)V"
            ),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    @SuppressWarnings("InvalidInjectorMethodSignature")
    private void injectScreenBoomer(float tickDelta, long startTime, boolean tick, CallbackInfo ci, int mouseX, int mouseY) {
        if (OkBoomer.currentlyScreenBooming != this.boom$screenBoomEnabled) {
            if (this.boom$screenBoomEnabled) {
                OkBoomer.screenBoom = 1;
            } else {
                OkBoomer.screenBoom = 2;
            }

            this.boom$screenBoomEnabled = OkBoomer.currentlyScreenBooming;
        }

        RenderSystem.getModelViewStack().push();
        RenderSystem.getModelViewStack().translate(this.boom$lastMouseX, this.boom$lastMouseY, 0);
        RenderSystem.getModelViewStack().scale(this.boom$lastScreenBoom, this.boom$lastScreenBoom, 1);
        RenderSystem.getModelViewStack().translate(-this.boom$lastMouseX, -this.boom$lastMouseY, 0);
        RenderSystem.applyModelViewMatrix();

        if (OkBoomer.CONFIG.boomTransition()) {
            this.boom$lastScreenBoom += .45 * (OkBoomer.screenBoom - this.boom$lastScreenBoom) * boom$interpolator();
            this.boom$lastMouseX += .65 * (mouseX - this.boom$lastMouseX) * MinecraftClient.getInstance().getLastFrameDuration();
            this.boom$lastMouseY += .65 * (mouseY - this.boom$lastMouseY) * MinecraftClient.getInstance().getLastFrameDuration();
        } else {
            this.boom$lastScreenBoom = (float) OkBoomer.screenBoom;
            this.boom$lastMouseX = mouseX;
            this.boom$lastMouseY = mouseY;
        }
    }

    @Inject(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screen/Screen;render(Lnet/minecraft/client/util/math/MatrixStack;IIF)V",
                    shift = At.Shift.AFTER
            )
    )
    private void uninjectScreenBoomer(float tickDelta, long startTime, boolean tick, CallbackInfo ci) {
        RenderSystem.getModelViewStack().pop();
        RenderSystem.applyModelViewMatrix();
    }

    private static float boom$interpolator() {
        return MinecraftClient.getInstance().getLastFrameDuration() * OkBoomer.CONFIG.boomTransitionSpeed();
    }

}
