package dev.battleplacks.config;

/**
 * How the 3D trainer holds their head; the closest thing to a facial expression, since a Minecraft face is painted
 * into the skin. Pitch is radians down (negative looks up), roll is radians tilted sideways.
 */
public enum HeadPose {
    STRAIGHT(0f, 0f),
    CONFIDENT(-0.25f, 0f),
    CURIOUS(0f, 0.3f),
    DETERMINED(0.3f, 0f),
    COCKY(-0.2f, -0.2f);

    public final float pitch;
    public final float roll;

    HeadPose(float pitch, float roll) {
        this.pitch = pitch;
        this.roll = roll;
    }
}
