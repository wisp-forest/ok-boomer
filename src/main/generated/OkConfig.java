package io.wispforest.okboomer;

import io.wispforest.owo.config.ConfigWrapper;
import io.wispforest.owo.config.Option;
import io.wispforest.owo.util.Observable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class OkConfig extends ConfigWrapper<io.wispforest.okboomer.OkBoomerConfigModel> {

    private static final Option.Key USECINEMATICCAMERA = new Option.Key("useCinematicCamera");
    private static final Option.Key ENABLESCREENBOOMING = new Option.Key("enableScreenBooming");
    private static final Option.Key BOOMTRANSITION = new Option.Key("boomTransition");
    private static final Option.Key BOOMTRANSITIONSPEED = new Option.Key("boomTransitionSpeed");
    private static final Option.Key BOOMSCROLLSENSITIVITY = new Option.Key("boomScrollSensitivity");
    private static final Option.Key BOOMLIMITS_ENABLELIMITS = new Option.Key("boomLimits.enableLimits");
    private static final Option.Key BOOMLIMITS_ALLOWBOOMINGOUT = new Option.Key("boomLimits.allowBoomingOut");
    private static final Option.Key BOOMLIMITS_MAXBOOM = new Option.Key("boomLimits.maxBoom");
    private static final Option.Key BOOMLIMITS_MAXSCREENBOOM = new Option.Key("boomLimits.maxScreenBoom");

    private OkConfig() {
        super(io.wispforest.okboomer.OkBoomerConfigModel.class);
    }

    public static OkConfig createAndLoad() {
        var wrapper = new OkConfig();
        wrapper.load();
        return wrapper;
    }

    public boolean useCinematicCamera() {
        return instance.useCinematicCamera;
    }

    public void useCinematicCamera(boolean value) {
        instance.useCinematicCamera = value;
        options.get(USECINEMATICCAMERA).synchronizeWithBackingField();
    }

    public boolean enableScreenBooming() {
        return instance.enableScreenBooming;
    }

    public void enableScreenBooming(boolean value) {
        instance.enableScreenBooming = value;
        options.get(ENABLESCREENBOOMING).synchronizeWithBackingField();
    }

    public boolean boomTransition() {
        return instance.boomTransition;
    }

    public void boomTransition(boolean value) {
        instance.boomTransition = value;
        options.get(BOOMTRANSITION).synchronizeWithBackingField();
    }

    public float boomTransitionSpeed() {
        return instance.boomTransitionSpeed;
    }

    public void boomTransitionSpeed(float value) {
        instance.boomTransitionSpeed = value;
        options.get(BOOMTRANSITIONSPEED).synchronizeWithBackingField();
    }

    public float boomScrollSensitivity() {
        return instance.boomScrollSensitivity;
    }

    public void boomScrollSensitivity(float value) {
        instance.boomScrollSensitivity = value;
        options.get(BOOMSCROLLSENSITIVITY).synchronizeWithBackingField();
    }

    public final BoomLimits boomLimits = new BoomLimits();
    public class BoomLimits {
        public boolean enableLimits() {
            return instance.boomLimits.enableLimits;
        }

        public void enableLimits(boolean value) {
            instance.boomLimits.enableLimits = value;
            options.get(BOOMLIMITS_ENABLELIMITS).synchronizeWithBackingField();
        }

        public boolean allowBoomingOut() {
            return instance.boomLimits.allowBoomingOut;
        }

        public void allowBoomingOut(boolean value) {
            instance.boomLimits.allowBoomingOut = value;
            options.get(BOOMLIMITS_ALLOWBOOMINGOUT).synchronizeWithBackingField();
        }

        public int maxBoom() {
            return instance.boomLimits.maxBoom;
        }

        public void maxBoom(int value) {
            instance.boomLimits.maxBoom = value;
            options.get(BOOMLIMITS_MAXBOOM).synchronizeWithBackingField();
        }

        public int maxScreenBoom() {
            return instance.boomLimits.maxScreenBoom;
        }

        public void maxScreenBoom(int value) {
            instance.boomLimits.maxScreenBoom = value;
            options.get(BOOMLIMITS_MAXSCREENBOOM).synchronizeWithBackingField();
        }

    }

}

