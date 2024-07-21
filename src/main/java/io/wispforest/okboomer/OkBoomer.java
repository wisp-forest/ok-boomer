package io.wispforest.okboomer;

import io.wispforest.okboomer.mixin.MouseAccessor;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class OkBoomer implements ClientModInitializer {

    public static final io.wispforest.okboomer.OkConfig CONFIG = io.wispforest.okboomer.OkConfig.createAndLoad();

    public static double boomDivisor = 1;
    public static double prevBoomDivisor = CONFIG.defaultBoom();
    public static boolean booming = false;

    public static double screenBoom = 1;
    public static float screenRotation = 0;
    public static boolean currentlyScreenBooming = false;

    public static boolean currentlyRotatIng = false;

    public static Matrix4f mouseTransform = new Matrix4f().identity();

    private static boolean smoothCameraRestoreValue = false;

    public static final KeyBinding BOOM_BINDING = KeyBindingHelper.registerKeyBinding(
            new KeyBinding("key.ok-boomer.boom", GLFW.GLFW_KEY_C, KeyBinding.MISC_CATEGORY)
    );

    public static final KeyBinding SCREEN_BOOM_BINDING = KeyBindingHelper.registerKeyBinding(
            new KeyBinding("key.ok-boomer.screen_boom", GLFW.GLFW_KEY_UNKNOWN, KeyBinding.MISC_CATEGORY)
    );

    public static final KeyBinding ROTAT_BINDING = KeyBindingHelper.registerKeyBinding(
            new KeyBinding("key.ok-boomer.rotat", GLFW.GLFW_KEY_UNKNOWN, KeyBinding.MISC_CATEGORY)
    );

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (SCREEN_BOOM_BINDING.isUnbound()) {
                currentlyScreenBooming = OkBoomer.CONFIG.enableScreenBooming()
                        && isPressed(BOOM_BINDING)
                        && ((Screen.hasControlDown() && Screen.hasShiftDown()) || currentlyScreenBooming);
            } else {
                currentlyScreenBooming = OkBoomer.CONFIG.enableScreenBooming() && isPressed(SCREEN_BOOM_BINDING);
            }

            currentlyRotatIng = isPressed(ROTAT_BINDING)
                    && ((Screen.hasControlDown() && Screen.hasShiftDown()) || currentlyRotatIng);

            boolean nowBooming = isPressed(BOOM_BINDING)
                    && client.currentScreen == null;

            if (booming != nowBooming) {
                if (booming) {
                    prevBoomDivisor = boomDivisor;
                    boomDivisor = 1;
                    client.options.smoothCameraEnabled = smoothCameraRestoreValue;
                } else {
                    if (CONFIG.resumeBoom()) {
                        boomDivisor = prevBoomDivisor;
                    } else {
                        boomDivisor = CONFIG.defaultBoom();
                    }
                    smoothCameraRestoreValue = client.options.smoothCameraEnabled;

                    if (CONFIG.useCinematicCamera()) {
                        client.options.smoothCameraEnabled = true;
                    }
                }

                booming = nowBooming;
            }
        });
    }

    private static boolean isPressed(KeyBinding binding) {
        var boundKey = KeyBindingHelper.getBoundKeyOf(binding);
        if (boundKey.getCode() < 0) return false;

        var windowHandle = MinecraftClient.getInstance().getWindow().getHandle();
        if (boundKey.getCategory() == InputUtil.Type.KEYSYM) {
            return InputUtil.isKeyPressed(windowHandle, boundKey.getCode());
        }

        if (boundKey.getCategory() == InputUtil.Type.MOUSE) {
            return ((MouseAccessor) MinecraftClient.getInstance().mouse).boom$getActiveButton() == boundKey.getCode();
        }

        return false;
    }

    public static int minBoom() {
        return CONFIG.boomLimits.allowBoomingOut() ? 0 : 1;
    }

    public static int maxBoom() {
        return CONFIG.boomLimits.enableLimits() ? CONFIG.boomLimits.maxBoom() : Integer.MAX_VALUE;
    }

    public static int maxScreenBoom() {
        return CONFIG.boomLimits.enableLimits() ? CONFIG.boomLimits.maxScreenBoom() : Integer.MAX_VALUE;
    }
}
