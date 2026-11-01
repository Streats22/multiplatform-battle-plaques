package dev.battleplacks.client.render;

import dev.battleplacks.config.Anchor;

/** Tracks screen space used per corner so several cards in one corner stack instead of overlapping. */
final class AnchorStacker {
    private static final int SPACING = 4;

    private final int[] used = new int[Anchor.values().length];

    /** Returns the space already used in this corner (screen px) and reserves {@code height} more. */
    int reserve(Anchor anchor, int height) {
        int before = used[anchor.ordinal()];
        used[anchor.ordinal()] += height + SPACING;
        return before;
    }
}
