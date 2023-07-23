package io.wispforest.okboomer.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.util.math.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(DrawableHelper.class)
public class DrawableHelperMixin {

    @ModifyArgs(method = "enableScissor", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;enableScissor(IIII)V"))
    private static void pushScissors(Args args) {
        int x = args.<Integer>get(0), y = args.<Integer>get(1);
        int width = args.<Integer>get(2), height = args.<Integer>get(3);

        var root = new Vector4f(x, y, 0, 1);
        var end = new Vector4f(x + width, y + height, 0, 1);

        root.transform(RenderSystem.getModelViewMatrix());
        end.transform(RenderSystem.getModelViewMatrix());

        args.set(0, (int) root.getX());
        args.set(1, (int) root.getY());

        args.set(2, (int) Math.ceil(end.getX() - root.getX()));
        args.set(3, (int) Math.ceil(end.getY() - root.getY()));
    }

}
