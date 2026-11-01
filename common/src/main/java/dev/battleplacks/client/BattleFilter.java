package dev.battleplacks.client;

import dev.battleplacks.config.PlaqueConfig;
import dev.battleplacks.config.TrainerFilterMode;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Decides whether a battle gets Battleplacks cards. Every opponent must be accepted: other players and wild
 * Pokémon follow their config switches, NPC trainers follow the name patterns.
 */
public final class BattleFilter {
    private List<String> compiledFrom = List.of();
    private final List<Pattern> compiled = new ArrayList<>();

    public boolean accepts(List<PlaqueModel> models, PlaqueConfig cfg) {
        boolean sawOpponent = false;
        for (PlaqueModel m : models) {
            if (m.own()) continue;
            sawOpponent = true;
            if (!acceptsOpponent(m, cfg)) return false;
        }
        return sawOpponent;
    }

    private boolean acceptsOpponent(PlaqueModel m, PlaqueConfig cfg) {
        return switch (m.kind()) {
            case PLAYER -> cfg.includePlayerBattles;
            case WILD -> cfg.includeWildBattles;
            case NPC -> acceptsTrainer(m.name(), cfg);
        };
    }

    private boolean acceptsTrainer(String name, PlaqueConfig cfg) {
        if (cfg.trainerFilterMode == TrainerFilterMode.ALL) return true;
        boolean matches = matchesAny(name, cfg.trainerPatterns);
        return cfg.trainerFilterMode == TrainerFilterMode.ONLY_MATCHING ? matches : !matches;
    }

    private boolean matchesAny(String name, List<String> patterns) {
        if (!patterns.equals(compiledFrom)) {
            compiled.clear();
            for (String p : patterns) compiled.add(compile(p));
            compiledFrom = List.copyOf(patterns);
        }
        return compiled.stream().anyMatch(p -> p.matcher(name).find());
    }

    /** A bad regex is treated as plain text instead of breaking the HUD. */
    private static Pattern compile(String pattern) {
        try {
            return Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
        } catch (PatternSyntaxException e) {
            return Pattern.compile(Pattern.quote(pattern), Pattern.CASE_INSENSITIVE);
        }
    }
}
