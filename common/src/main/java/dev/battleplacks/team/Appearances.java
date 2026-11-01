package dev.battleplacks.team;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** Appearances other players have shared with us. Everything is re-validated on arrival, whatever the server says. */
public final class Appearances {
    private static final Map<UUID, Appearance> BY_PLAYER = new ConcurrentHashMap<>();

    private Appearances() {}

    public static void set(UUID player, String name, int accentRgb, int styleId) {
        BY_PLAYER.put(player, AppearanceRules.sanitize(name, accentRgb, styleId));
    }

    public static void remove(UUID player) { BY_PLAYER.remove(player); }

    public static Optional<Appearance> get(UUID player) {
        return Optional.ofNullable(BY_PLAYER.get(player));
    }

    public static void clear() { BY_PLAYER.clear(); }
}
