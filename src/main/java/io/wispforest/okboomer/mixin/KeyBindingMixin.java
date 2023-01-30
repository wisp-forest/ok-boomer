package io.wispforest.okboomer.mixin;

import io.wispforest.okboomer.OkBoomer;
import net.minecraft.client.option.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(KeyBinding.class)
public class KeyBindingMixin {

    @Inject(method = "equals", at = @At("HEAD"), cancellable = true)
    private void markCompatible(KeyBinding other, CallbackInfoReturnable<Boolean> cir) {
        if (other != OkBoomer.BOOM_BINDING && (Object) this != OkBoomer.BOOM_BINDING) return;
        cir.setReturnValue(false);
    }

}
