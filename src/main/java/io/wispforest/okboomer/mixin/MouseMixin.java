package io.wispforest.okboomer.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import io.wispforest.okboomer.OkBoomer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.gui.Click;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(value = Mouse.class, priority = 500)
public class MouseMixin {

    @Unique private static final Vector3f boom$mouseVec = new Vector3f();

    @ModifyVariable(method = "onMouseButton", at = @At(value = "STORE"), ordinal = 0)
    private Click transformMouseDownCoordinates(Click click) {
        boom$mouseVec.set(click.x(), click.y(), 1);
        boom$mouseVec.mul(OkBoomer.mouseTransform);

        return new Click(boom$mouseVec.x, boom$mouseVec.y, click.buttonInfo());
    }

    @ModifyArgs(method = "onMouseScroll", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;mouseScrolled(DDDD)Z"))
    private void transformMouseScrollCoordinates(Args args) {
        boom$mouseVec.set(args.<Number>get(0).floatValue(), args.<Number>get(1).floatValue(), 1);
        boom$mouseVec.mul(OkBoomer.mouseTransform);

        args.set(0, ((Number) boom$mouseVec.x).doubleValue());
        args.set(1, ((Number) boom$mouseVec.y).doubleValue());
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
