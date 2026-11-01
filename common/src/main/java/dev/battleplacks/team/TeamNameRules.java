package dev.battleplacks.team;

/** What counts as an acceptable team name. Used by the config screen, the server and every receiving client. */
public final class TeamNameRules {
    public static final int MAX_LENGTH = 20;

    private TeamNameRules() {}

    public record Result(boolean valid, String name, String reason) {
        static Result ok(String name) { return new Result(true, name, ""); }

        static Result rejected(String reason) { return new Result(false, "", reason); }
    }

    /** An empty name is valid and means "use the default". */
    public static Result validate(String raw) {
        String name = clean(raw);
        if (name.isEmpty()) return Result.ok("");
        if (name.length() > MAX_LENGTH) return Result.rejected("At most " + MAX_LENGTH + " characters");
        if (ProfanityFilter.isProfane(name)) return Result.rejected("That name is not allowed");
        return Result.ok(name);
    }

    /** Trims, collapses spaces and drops control and formatting characters. */
    public static String clean(String raw) {
        if (raw == null) return "";
        return raw.replaceAll("[\\p{Cntrl}\u00a7]", "").replaceAll("\\s+", " ").trim();
    }
}
