package io.wispforest.okboomer.mixin;

import io.wispforest.okboomer.OkBoomer;
import net.minecraft.client.gui.DrawContext;
import org.joml.Matrix3x2fStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DrawContext.class)
public class DrawContextMixin {

    @Shadow
    @Final
    private Matrix3x2fStack matrices;

    @Inject(method = "enableScissor", at = @At("HEAD"))
    private void injectBoomerIntoScissor(int x1, int y1, int x2, int y2, CallbackInfo ci) {
        this.matrices.pushMatrix();
        this.matrices.mul(OkBoomer.renderTransform);
    }

    @Inject(method = "enableScissor", at = @At("RETURN"))
    private void uninjectBoomerIntoScissor(int x1, int y1, int x2, int y2, CallbackInfo ci) {
        this.matrices.popMatrix();
    }
}
