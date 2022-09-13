package io.wispforest.okboomer;

import io.wispforest.owo.config.ConfigWrapper;
import io.wispforest.owo.config.Option;
import io.wispforest.owo.util.Observable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class OkConfig extends ConfigWrapper<io.wispforest.okboomer.OkBoomerConfigModel> {

    private final Option<java.lang.Boolean> useCinematicCamera = this.optionForKey(new Option.Key("useCinematicCamera"));
    private final Option<java.lang.Boolean> enableScreenBooming = this.optionForKey(new Option.Key("enableScreenBooming"));
    private final Option<java.lang.Boolean> boomTransition = this.optionForKey(new Option.Key("boomTransition"));
    private final Option<java.lang.Float> boomTransitionSpeed = this.optionForKey(new Option.Key("boomTransitionSpeed"));
    private final Option<java.lang.Float> boomScrollSensitivity = this.optionForKey(new Option.Key("boomScrollSensitivity"));
    private final Option<java.lang.Boolean> boomLimits_enableLimits = this.optionForKey(new Option.Key("boomLimits.enableLimits"));
    private final Option<java.lang.Boolean> boomLimits_allowBoomingOut = this.optionForKey(new Option.Key("boomLimits.allowBoomingOut"));
    private final Option<java.lang.Integer> boomLimits_maxBoom = this.optionForKey(new Option.Key("boomLimits.maxBoom"));
    private final Option<java.lang.Integer> boomLimits_maxScreenBoom = this.optionForKey(new Option.Key("boomLimits.maxScreenBoom"));

    private OkConfig() {
        super(io.wispforest.okboomer.OkBoomerConfigModel.class);
    }

    public static OkConfig createAndLoad() {
        var wrapper = new OkConfig();
        wrapper.load();
        return wrapper;
    }

    public boolean useCinematicCamera() {
        return useCinematicCamera.value();
    }

    public void useCinematicCamera(boolean value) {
        instance.useCinematicCamera = value;
        useCinematicCamera.synchronizeWithBackingField();
    }

    public boolean enableScreenBooming() {
        return enableScreenBooming.value();
    }

    public void enableScreenBooming(boolean value) {
        instance.enableScreenBooming = value;
        enableScreenBooming.synchronizeWithBackingField();
    }

    public boolean boomTransition() {
        return boomTransition.value();
    }

    public void boomTransition(boolean value) {
        instance.boomTransition = value;
        boomTransition.synchronizeWithBackingField();
    }

    public float boomTransitionSpeed() {
        return boomTransitionSpeed.value();
    }

    public void boomTransitionSpeed(float value) {
        instance.boomTransitionSpeed = value;
        boomTransitionSpeed.synchronizeWithBackingField();
    }

    public float boomScrollSensitivity() {
        return boomScrollSensitivity.value();
    }

    public void boomScrollSensitivity(float value) {
        instance.boomScrollSensitivity = value;
        boomScrollSensitivity.synchronizeWithBackingField();
    }

    public final BoomLimits boomLimits = new BoomLimits();
    public class BoomLimits {
        public boolean enableLimits() {
            return boomLimits_enableLimits.value();
        }

        public void enableLimits(boolean value) {
            instance.boomLimits.enableLimits = value;
            boomLimits_enableLimits.synchronizeWithBackingField();
        }

        public boolean allowBoomingOut() {
            return boomLimits_allowBoomingOut.value();
        }

        public void allowBoomingOut(boolean value) {
            instance.boomLimits.allowBoomingOut = value;
            boomLimits_allowBoomingOut.synchronizeWithBackingField();
        }

        public int maxBoom() {
            return boomLimits_maxBoom.value();
        }

        public void maxBoom(int value) {
            instance.boomLimits.maxBoom = value;
            boomLimits_maxBoom.synchronizeWithBackingField();
        }

        public int maxScreenBoom() {
            return boomLimits_maxScreenBoom.value();
        }

        public void maxScreenBoom(int value) {
            instance.boomLimits.maxScreenBoom = value;
            boomLimits_maxScreenBoom.synchronizeWithBackingField();
        }

    }

}

