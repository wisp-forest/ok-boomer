package io.wispforest.okboomer.mixin;

import io.wispforest.okboomer.OkBoomer;
import net.minecraft.client.gui.render.GuiRenderer;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(GuiRenderer.class)
public class GuiRendererMixin {

    @ModifyArg(method = "renderPreparedDraws", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gl/DynamicUniforms;write(Lorg/joml/Matrix4fc;Lorg/joml/Vector4fc;Lorg/joml/Vector3fc;Lorg/joml/Matrix4fc;)Lcom/mojang/blaze3d/buffers/GpuBufferSlice;"), index = 0)
    public Matrix4fc injectRenderTransform(Matrix4fc modelView) {
        var transform = new Matrix4f();
        transform.mul(modelView);
        transform.mul(OkBoomer.renderTransform);

        return transform;
    }

}
