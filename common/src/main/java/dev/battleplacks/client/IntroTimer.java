package dev.battleplacks.client;

import java.util.Optional;
import java.util.UUID;

/** Tracks how long the current battle has been running, so the optional intro plays once per battle. */
public final class IntroTimer {
    private UUID current;
    private long startNanos;

    /** Progress 0..1 while the intro plays for this battle, or -1 when it is not playing. */
    public float progress(Optional<UUID> battle, float seconds) {
        if (battle.isEmpty()) {
            current = null;
            return -1f;
        }
        if (!battle.get().equals(current)) {
            current = battle.get();
            startNanos = System.nanoTime();
        }
        float p = (System.nanoTime() - startNanos) / 1e9f / Math.max(0.5f, seconds);
        return p >= 1f ? -1f : p;
    }
}
