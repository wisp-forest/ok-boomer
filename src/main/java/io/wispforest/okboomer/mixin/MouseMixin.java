package io.wispforest.okboomer.mixin;

import io.wispforest.okboomer.OkBoomer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.util.math.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(Mouse.class)
public class MouseMixin {

    @Unique private static final Vector4f boom$mouseVec = new Vector4f();

    @ModifyArgs(method = "method_1611", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;mouseClicked(DDI)Z"))
    private static void transformMouseDownCoordinates(Args args) {
        boom$mouseVec.set(args.<Number>get(0).floatValue(), args.<Number>get(1).floatValue(), 0, 1);
        boom$mouseVec.transform(OkBoomer.mouseTransform);

        args.set(0, ((Number) boom$mouseVec.getX()).doubleValue());
        args.set(1, ((Number) boom$mouseVec.getY()).doubleValue());
    }

    @ModifyArgs(method = "method_1605", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;mouseReleased(DDI)Z"))
    private static void transformMouseUpCoordinates(Args args) {
        boom$mouseVec.set(args.<Number>get(0).floatValue(), args.<Number>get(1).floatValue(), 0, 1);
        boom$mouseVec.transform(OkBoomer.mouseTransform);

        args.set(0, ((Number) boom$mouseVec.getX()).doubleValue());
        args.set(1, ((Number) boom$mouseVec.getY()).doubleValue());
    }

    @ModifyArgs(method = "onMouseScroll", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;mouseScrolled(DDD)Z"))
    private void transformMouseScrollCoordinates(Args args) {
        boom$mouseVec.set(args.<Number>get(0).floatValue(), args.<Number>get(1).floatValue(), 0, 1);
        boom$mouseVec.transform(OkBoomer.mouseTransform);

        args.set(0, ((Number) boom$mouseVec.getX()).doubleValue());
        args.set(1, ((Number) boom$mouseVec.getY()).doubleValue());
    }

    @Inject(method = "onMouseScroll", at = @At("HEAD"), cancellable = true)
    private void scrollBoomer(long window, double horizontal, double vertical, CallbackInfo ci) {
        if (MinecraftClient.getInstance().currentScreen != null) {
            if (OkBoomer.currentlyRotatIng) {
                OkBoomer.screenRotation += vertical;
                ci.cancel();
            } else if (OkBoomer.currentlyScreenBooming) {
                OkBoomer.screenBoom = Math.min(
                        Math.max(
                                OkBoomer.minBoom(),
                                OkBoomer.screenBoom + vertical * .2 * OkBoomer.screenBoom
                        ),
                        OkBoomer.maxScreenBoom()
                );
                ci.cancel();
            }
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
