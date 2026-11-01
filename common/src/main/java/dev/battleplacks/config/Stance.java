package dev.battleplacks.config;

/**
 * Pose of the 3D trainer. Facing is degrees turned towards the middle of the screen (0 = facing the camera),
 * lean is degrees tilted forward.
 */
public enum Stance {
    STANDING(35f, 0f, false),
    LEANING(35f, 14f, false),
    CROUCHING(35f, 0f, true),
    PROFILE(80f, 0f, false),
    FRONT(0f, 0f, false);

    public final float facing;
    public final float lean;
    public final boolean crouch;

    Stance(float facing, float lean, boolean crouch) {
        this.facing = facing;
        this.lean = lean;
        this.crouch = crouch;
    }
}
