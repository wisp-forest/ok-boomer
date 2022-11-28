package io.wispforest.okboomer.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import io.wispforest.okboomer.OkBoomer;
import io.wispforest.owo.ui.core.Color;
import io.wispforest.owo.ui.util.Drawer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {

    @Unique private double boom$lastBoomDivisor = OkBoomer.boomDivisor;

    @Unique private float boom$lastScreenBoom = (float) OkBoomer.screenBoom;
    @Unique private float boom$lastMouseX = 0, boom$lastMouseY = 0;
    @Unique private boolean boom$screenBoomEnabled = false;

    @Unique private final MatrixStack boom$rotat = new MatrixStack();
    @Unique private final Vector4f boom$mouseVec = new Vector4f();

    @ModifyVariable(method = "getFov", at = @At(value = "RETURN", shift = At.Shift.BEFORE), ordinal = 0)
    private double injectBoomer(double fov) {
        if (OkBoomer.CONFIG.boomTransition()) {
            this.boom$lastBoomDivisor += .45 * (OkBoomer.boomDivisor - this.boom$lastBoomDivisor) * boom$interpolator();
        } else {
            this.boom$lastBoomDivisor = OkBoomer.boomDivisor;
        }

        return fov / this.boom$lastBoomDivisor;
    }

    @Inject(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/MinecraftClient;getLastFrameDuration()F",
                    ordinal = 1
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

        final var modelViewStack = RenderSystem.getModelViewStack();
        modelViewStack.push();

        modelViewStack.translate(this.boom$lastMouseX, this.boom$lastMouseY, 0);
        modelViewStack.scale(this.boom$lastScreenBoom, this.boom$lastScreenBoom, 1);
        modelViewStack.translate(-this.boom$lastMouseX, -this.boom$lastMouseY, 0);

        var window = MinecraftClient.getInstance().getWindow();
        this.boom$rotat.loadIdentity();
        this.boom$rotat.translate(window.getScaledWidth() / 2f, window.getScaledHeight() / 2f, 0);
        this.boom$rotat.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(OkBoomer.screenRotation));
        this.boom$rotat.translate(window.getScaledWidth() / -2f, window.getScaledHeight() / -2f, 0);

        modelViewStack.multiplyPositionMatrix(this.boom$rotat.peek().getPositionMatrix());

        this.boom$rotat.peek().getPositionMatrix().invert();
        OkBoomer.mouseTransform = this.boom$rotat.peek().getPositionMatrix();

        RenderSystem.applyModelViewMatrix();

        if (OkBoomer.CONFIG.boomTransition()) {
            this.boom$lastScreenBoom += .45 * (OkBoomer.screenBoom - this.boom$lastScreenBoom) * boom$interpolator();
            this.boom$lastMouseX += .65 * (mouseX - this.boom$lastMouseX) * MinecraftClient.getInstance().getLastFrameDuration();
            this.boom$lastMouseY += .65 * (mouseY - this.boom$lastMouseY) * MinecraftClient.getInstance().getLastFrameDuration();

            this.boom$lastScreenBoom = boom$nudge(this.boom$lastScreenBoom, 1);
            this.boom$lastMouseX = boom$nudge(this.boom$lastMouseX, mouseX);
            this.boom$lastMouseY = boom$nudge(this.boom$lastMouseY, mouseY);
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
                    target = "Lnet/minecraft/client/gui/screen/Screen;renderWithTooltip(Lnet/minecraft/client/util/math/MatrixStack;IIF)V",
                    shift = At.Shift.AFTER
            ),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    @SuppressWarnings("InvalidInjectorMethodSignature")
    private void bottomText(float tickDelta, long startTime, boolean tick, CallbackInfo ci, int i, int j, MatrixStack matrixStack) {
        if (OkBoomer.CONFIG.iDoNotEndorseTomfoolery()) return;

        var client = MinecraftClient.getInstance();
        var window = client.getWindow();
        var textRenderer = client.textRenderer;

        Drawer.fill(matrixStack,
                0,
                0,
                window.getScaledWidth(),
                (-textRenderer.fontHeight - 2) * 3,
                Color.BLACK.argb()
        );

        Drawer.fill(matrixStack,
                0,
                window.getScaledHeight(),
                window.getScaledWidth(),
                window.getScaledHeight() + (textRenderer.fontHeight + 2) * 3,
                Color.BLACK.argb()
        );

        var bottom_text = "Bottom Text";
        var oneRotat = Math.abs(OkBoomer.screenRotation % 360f);

        if (oneRotat > 22.5 + 0) bottom_text = "Corner Text";
        if (oneRotat > 22.5 + 45) bottom_text = "Side Text";
        if (oneRotat > 22.5 + 90) bottom_text = "Corner Text";
        if (oneRotat > 22.5 + 135) bottom_text = "Top Text";
        if (oneRotat > 22.5 + 180) bottom_text = "Corner Text";
        if (oneRotat > 22.5 + 225) bottom_text = "Side Text";
        if (oneRotat > 22.5 + 270) bottom_text = "Corner Text";
        if (oneRotat > 22.5 + 315) bottom_text = "Bottom Text";

        float factor = window.getScaledWidth() / (textRenderer.getWidth(bottom_text) + 2f);
        matrixStack.push();
        matrixStack.scale(factor, 3, 1);
        textRenderer.draw(matrixStack, bottom_text, 1, (window.getScaledHeight() + 6) / 3f, Color.WHITE.argb());
        matrixStack.pop();
    }

    @ModifyArgs(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;renderWithTooltip(Lnet/minecraft/client/util/math/MatrixStack;IIF)V"))
    private void transformMouse(Args args) {
        this.boom$mouseVec.set(args.<Number>get(1).floatValue(), args.<Number>get(2).floatValue(), 0, 1);
        this.boom$mouseVec.mul(OkBoomer.mouseTransform);

        args.set(1, ((Number) this.boom$mouseVec.x).intValue());
        args.set(2, ((Number) this.boom$mouseVec.y).intValue());
    }

    @Inject(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screen/Screen;renderWithTooltip(Lnet/minecraft/client/util/math/MatrixStack;IIF)V",
                    shift = At.Shift.AFTER
            )
    )
    private void uninjectScreenBoomer(float tickDelta, long startTime, boolean tick, CallbackInfo ci) {
        RenderSystem.getModelViewStack().pop();
        RenderSystem.applyModelViewMatrix();
    }

    private static float boom$nudge(float value, float to) {
        return Math.abs(to - value) < 0.005 ? to : value;
    }

    private static float boom$interpolator() {
        return MinecraftClient.getInstance().getLastFrameDuration() * OkBoomer.CONFIG.boomTransitionSpeed();
    }

}
