package io.wispforest.okboomer;

import io.wispforest.owo.config.annotation.Config;
import io.wispforest.owo.config.annotation.Modmenu;
import io.wispforest.owo.config.annotation.Nest;
import io.wispforest.owo.config.annotation.RangeConstraint;

@Modmenu(modId = "ok-boomer")
@Config(wrapperName = "OkConfig", name = "ok-boomer")
public class OkBoomerConfigModel {

    public boolean useCinematicCamera = false;

    public boolean enableScreenBooming = true;

    public boolean boomTransition = true;

    @RangeConstraint(min = .25, max = 5)
    public float boomTransitionSpeed = 1;

    @RangeConstraint(min = .1, max = 5)
    public float boomScrollSensitivity = 1;

    @Nest
    public BoomLimits boomLimits = new BoomLimits();

    public static class BoomLimits {

        public boolean enableLimits = true;

        public boolean allowBoomingOut = false;

        @RangeConstraint(min = 100, max = 5000)
        public int maxBoom = 100;

        @RangeConstraint(min = 5, max = 25)
        public int maxScreenBoom = 5;
    }

}
