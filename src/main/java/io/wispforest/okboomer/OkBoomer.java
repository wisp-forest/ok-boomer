package io.wispforest.okboomer;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.math.Matrix4f;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class OkBoomer implements ClientModInitializer {

    public static final io.wispforest.okboomer.OkConfig CONFIG = io.wispforest.okboomer.OkConfig.createAndLoad();

    public static double boomDivisor = 1;
    public static boolean booming = false;

    public static double screenBoom = 1;
    public static float screenRotation = 0;
    public static boolean currentlyScreenBooming = false;

    public static boolean currentlyRotatIng = false;

    public static Matrix4f mouseTransform = new Matrix4f();

    static {
        mouseTransform.loadIdentity();
    }

    private static boolean smoothCameraRestoreValue = false;

    public static final KeyBinding BOOM_BINDING = KeyBindingHelper.registerKeyBinding(
            new KeyBinding("key.ok-boomer.boom", GLFW.GLFW_KEY_C, KeyBinding.MISC_CATEGORY)
    );

    public static final KeyBinding ROTAT_BINDING = KeyBindingHelper.registerKeyBinding(
            new KeyBinding("key.ok-boomer.rotat", GLFW.GLFW_KEY_UNKNOWN, KeyBinding.MISC_CATEGORY)
    );

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            currentlyScreenBooming = OkBoomer.CONFIG.enableScreenBooming()
                    && KeyBindingHelper.getBoundKeyOf(BOOM_BINDING).getCode() > 0
                    && InputUtil.isKeyPressed(client.getWindow().getHandle(), KeyBindingHelper.getBoundKeyOf(BOOM_BINDING).getCode())
                    && (Screen.hasAltDown() || currentlyScreenBooming);

            currentlyRotatIng = KeyBindingHelper.getBoundKeyOf(ROTAT_BINDING).getCode() > 0
                    && InputUtil.isKeyPressed(client.getWindow().getHandle(), KeyBindingHelper.getBoundKeyOf(ROTAT_BINDING).getCode())
                    && (Screen.hasAltDown() || currentlyRotatIng);

            if (booming != BOOM_BINDING.isPressed()) {
                boolean nowBooming = false;
                while (BOOM_BINDING.wasPressed()) {
                    nowBooming = true;
                }

                if (booming) {
                    boomDivisor = 1;
                    client.options.smoothCameraEnabled = smoothCameraRestoreValue;
                } else {
                    boomDivisor = 7.5;
                    smoothCameraRestoreValue = client.options.smoothCameraEnabled;

                    if (CONFIG.useCinematicCamera()) {
                        client.options.smoothCameraEnabled = true;
                    }
                }

                booming = nowBooming;
            }
        });
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
