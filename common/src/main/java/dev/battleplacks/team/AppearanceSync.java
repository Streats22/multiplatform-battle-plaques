package dev.battleplacks.team;

/** How a client tells the server its appearance. Implemented per loader. */
public interface AppearanceSync {
    void announce(Appearance appearance);
}
