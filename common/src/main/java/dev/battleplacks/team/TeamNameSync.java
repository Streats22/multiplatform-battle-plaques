package dev.battleplacks.team;

/** How a client tells the server its team name. Implemented per loader. */
public interface TeamNameSync {
    void announce(String name);
}
