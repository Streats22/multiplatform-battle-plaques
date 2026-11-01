package dev.battleplacks.team;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** Team names other players have shared with us. Every name is re-checked on arrival, whatever the server says. */
public final class TeamNames {
    private static final Map<UUID, String> NAMES = new ConcurrentHashMap<>();

    private TeamNames() {}

    public static void set(UUID player, String raw) {
        TeamNameRules.Result result = TeamNameRules.validate(raw);
        if (result.valid() && !result.name().isEmpty()) NAMES.put(player, result.name());
        else NAMES.remove(player);
    }

    public static Optional<String> get(UUID player) {
        return Optional.ofNullable(NAMES.get(player));
    }

    public static void clear() { NAMES.clear(); }
}
