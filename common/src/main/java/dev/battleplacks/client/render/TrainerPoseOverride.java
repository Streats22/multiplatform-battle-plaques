package dev.battleplacks.client.render;

import dev.battleplacks.config.ArmPose;
import dev.battleplacks.config.HeadPose;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;

/**
 * The arm and head pose for the trainer being drawn in the overlay right now. TrainerEntityPainter sets it only for
 * the duration of its own draw call (render thread), and HumanoidModelMixin applies it at the end of setupAnim, so
 * trainers in the world are never affected. Angles are radians on vanilla's HumanoidModel parts: negative xRot swings
 * an arm forward and up, positive zRot swings the right arm outwards (the left arm is mirrored).
 */
public final class TrainerPoseOverride {
    private static ArmPose arms;
    private static HeadPose head;

    private TrainerPoseOverride() {}

    static void begin(ArmPose armPose, HeadPose headPose) {
        arms = armPose;
        head = headPose;
    }

    static void end() {
        arms = null;
        head = null;
    }

    /** Called from HumanoidModelMixin after vanilla has posed the model. Does nothing outside the overlay. */
    public static void apply(HumanoidModel<?> model, float ageInTicks) {
        if (arms == null || head == null) return;
        applyArms(model.rightArm, model.leftArm, ageInTicks);
        model.head.xRot += head.pitch;
        model.head.zRot += head.roll;
        model.hat.copyFrom(model.head); // vanilla copied the head into the hat layer before we moved it
    }

    private static void applyArms(ModelPart right, ModelPart left, float age) {
        switch (arms) {
            case NATURAL -> {}
            case WAVE -> {
                set(right, 0f, 0f, 2.6f + Mth.sin(age * 0.35f) * 0.3f);
                set(left, 0f, 0f, -0.1f);
            }
            case POINT -> {
                set(right, -1.5f, -0.15f, 0f);
                set(left, 0.1f, 0f, -0.1f);
            }
            case HANDS_ON_HIPS -> {
                set(right, 0.35f, 0f, 0.65f);
                set(left, 0.35f, 0f, -0.65f);
            }
            case ARMS_CROSSED -> {
                set(right, -0.95f, 0.65f, 0f);
                set(left, -0.95f, -0.65f, 0f);
            }
            case CHEER -> {
                set(right, -2.9f, 0f, 0.25f);
                set(left, -2.9f, 0f, -0.25f);
            }
            case READY_TO_THROW -> {
                set(right, -2.5f, 0f, 0.35f);
                set(left, -0.7f, 0.3f, 0f);
            }
        }
    }

    private static void set(ModelPart part, float x, float y, float z) {
        part.xRot = x;
        part.yRot = y;
        part.zRot = z;
    }
}
