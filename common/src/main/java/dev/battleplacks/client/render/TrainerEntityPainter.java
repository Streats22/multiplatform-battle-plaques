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
    private static final float SCALE = 38f;

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

    /** Same pose maths as InventoryScreen.renderEntityInInventoryFollowsMouse (1.21.1). */
    void paint(CardContext c, LivingEntity e, int x, int feetY, Stance stance) {
        float turn = (c.left() ? 1f : -1f) * stance.facing;
        float oBody = e.yBodyRot, oYaw = e.getYRot(), oPitch = e.getXRot(), oHead = e.yHeadRot, oHeadO = e.yHeadRotO;
        Pose oPose = e.getPose();

        e.yBodyRot = 180f + turn;
        e.setYRot(180f + turn);
        e.setXRot(0f);
        e.yHeadRot = e.getYRot();
        e.yHeadRotO = e.getYRot();
        if (stance.crouch) e.setPose(Pose.CROUCHING);

        // TODO(visual): confirm the feet land on the baseline and the lean direction; flip the sign if needed.
        Quaternionf pose = new Quaternionf().rotateZ((float) Math.PI).rotateX((float) Math.toRadians(stance.lean));
        InventoryScreen.renderEntityInInventory(c.g(), x, feetY - e.getBbHeight() / 2f * SCALE, SCALE,
                new Vector3f(0f, 0f, 0f), pose, new Quaternionf().rotateX(-0.1f), e);

        e.yBodyRot = oBody;
        e.setYRot(oYaw);
        e.setXRot(oPitch);
        e.yHeadRot = oHead;
        e.yHeadRotO = oHeadO;
        e.setPose(oPose);
    }
}
