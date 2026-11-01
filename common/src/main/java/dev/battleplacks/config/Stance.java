package dev.battleplacks.config;

/**
 * Pose of the 3D trainer. Facing is degrees turned towards the middle of the screen (0 = facing the camera),
 * lean is degrees tilted forward. CUSTOM takes both from the config (customFacing, customLean).
 */
public enum Stance {
    STANDING(35f, 0f, false),
    LEANING(40f, 20f, false),
    HERO(20f, 8f, false),
    CROUCHING(35f, 0f, true),
    PROFILE(80f, 0f, false),
    OVER_SHOULDER(130f, 0f, false),
    FRONT(0f, 0f, false),
    CUSTOM(35f, 0f, false);

    public final float facing;
    public final float lean;
    public final boolean crouch;

    Stance(float facing, float lean, boolean crouch) {
        this.facing = facing;
        this.lean = lean;
        this.crouch = crouch;
    }

    public float facing(PlaqueConfig cfg) { return this == CUSTOM ? cfg.customFacing : facing; }

    public float lean(PlaqueConfig cfg) { return this == CUSTOM ? cfg.customLean : lean; }
}
