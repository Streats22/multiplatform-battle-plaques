package dev.battleplacks.client.render;

import dev.battleplacks.config.Stance;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Optional;
import java.util.UUID;

/** Finds a trainer's entity and draws it in 3D in the chosen {@link Stance}. */
final class TrainerEntityPainter {
    /** Players by UUID, then any rendered living entity (RCT trainers are ordinary entities). */
    Optional<LivingEntity> find(UUID id) {
        var level = Minecraft.getInstance().level;
        if (level == null || id == null) return Optional.empty();
        var player = level.getPlayerByUUID(id);
        if (player != null) return Optional.of(player);
        for (Entity e : level.entitiesForRendering()) {
            if (e instanceof LivingEntity living && id.equals(e.getUUID())) return Optional.of(living);
        }
        return Optional.empty();
    }

    /** Draws the entity with its feet on {@code feetY}, posed as configured for its side. */
    void paintStandingOn(CardContext c, LivingEntity e, int x, int feetY, float scale, boolean own) {
        paint(c, e, x, feetY - e.getBbHeight() / 2f * scale, scale, own);
    }

    /** Draws the entity with the top of its head at {@code topY}; whatever is below the clip is simply cut off. */
    void paintHangingFrom(CardContext c, LivingEntity e, int x, int topY, float scale, boolean own) {
        paint(c, e, x, topY + e.getBbHeight() / 2f * scale, scale, own);
    }

    /**
     * Same pose maths as InventoryScreen.renderEntityInInventoryFollowsMouse (1.21.1): the point (x, centreY) is
     * the middle of the entity, and the translate vector lifts the model by half its height. The stance turns and
     * leans the whole body here; arms and head are posed by {@link TrainerPoseOverride} while the model is drawn.
     */
    private void paint(CardContext c, LivingEntity e, int x, float centreY, float scale, boolean own) {
        Stance stance = c.cfg().stanceFor(own);
        float turn = (c.left() ? 1f : -1f) * stance.facing(c.cfg());
        float oBody = e.yBodyRot, oYaw = e.getYRot(), oPitch = e.getXRot(), oHead = e.yHeadRot, oHeadO = e.yHeadRotO;
        Pose oPose = e.getPose();

        e.yBodyRot = 180f + turn;
        e.setYRot(180f + turn);
        e.setXRot(0f);
        e.yHeadRot = e.getYRot();
        e.yHeadRotO = e.getYRot();
        if (stance.crouch) e.setPose(Pose.CROUCHING);

        // TODO(visual): lean direction; flip the sign if the trainer tilts backwards.
        Quaternionf pose = new Quaternionf().rotateZ((float) Math.PI).rotateX((float) Math.toRadians(stance.lean(c.cfg())));
        TrainerPoseOverride.begin(c.cfg().armsFor(own), c.cfg().headFor(own));
        try {
            InventoryScreen.renderEntityInInventory(c.g(), x, centreY, scale,
                    new Vector3f(0f, e.getBbHeight() / 2f + 0.0625f, 0f), pose, new Quaternionf().rotateX(-0.1f), e);
        } finally {
            TrainerPoseOverride.end();
        }

        e.yBodyRot = oBody;
        e.setYRot(oYaw);
        e.setXRot(oPitch);
        e.yHeadRot = oHead;
        e.yHeadRotO = oHeadO;
        e.setPose(oPose);
    }
}
