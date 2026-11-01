package dev.battleplacks.config;

/** Screen corner a card is pinned to. */
public enum Anchor {
    TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT;

    public boolean isLeft() { return this == TOP_LEFT || this == BOTTOM_LEFT; }

    public boolean isTop() { return this == TOP_LEFT || this == TOP_RIGHT; }
}
