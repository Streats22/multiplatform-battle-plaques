package dev.battleplacks.client;

import java.util.Optional;
import java.util.UUID;

/** Seconds since the current battle was first seen, so the intro and the strip fade can run once per battle. */
public final class BattleClock {
    private UUID current;
    private long startNanos;

    /** Seconds elapsed in this battle, or -1 when there is no battle. */
    public float elapsedSeconds(Optional<UUID> battle) {
        if (battle.isEmpty()) {
            current = null;
            return -1f;
        }
        if (!battle.get().equals(current)) {
            current = battle.get();
            startNanos = System.nanoTime();
        }
        return (System.nanoTime() - startNanos) / 1e9f;
    }
}
