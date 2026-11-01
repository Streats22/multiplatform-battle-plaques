package dev.battleplacks.config;

/** Colour scheme for a card. Each constant carries its own palette, so adding a theme is one line. */
public enum Theme {
    COBBLEMON(0x2F3F4F, 0x8FD4E8, 0xFFFFFFFF, 0xFFB8D0DC, 0xFF32CBFF, 0xFFFC5454),
    CLASSIC(0xF8F0D8, 0x505050, 0xFF303030, 0xFF606060, 0xFF58A8F8, 0xFFF85858),
    MINIMAL(0x000000, -1, 0xFFFFFFFF, 0xFFB0B0B0, 0xFF6EE7B7, 0xFFFCA5A5),
    MIDNIGHT(0x14162B, 0x3B4080, 0xFFE8E8FF, 0xFF9090C0, 0xFF7AA2FF, 0xFFFF7A9A);


    /** Resolved colours (ARGB) for one opacity. */
    public record Palette(int panel, int edge, int text, int subText, int accentOwn, int accentOpponent) {
        public boolean hasEdge() { return (edge >>> 24) != 0; }

        /** The user's override colour if set, otherwise this theme's accent. */
        public int accent(boolean own, PlaqueConfig cfg) {
            int override = own ? cfg.accentOwn : cfg.accentOpponent;
            return override != 0 ? override : own ? accentOwn : accentOpponent;
        }
    }

    private final int panelRgb, edgeRgb, text, subText, accentOwn, accentOpponent;

    Theme(int panelRgb, int edgeRgb, int text, int subText, int accentOwn, int accentOpponent) {
        this.panelRgb = panelRgb;
        this.edgeRgb = edgeRgb;
        this.text = text;
        this.subText = subText;
        this.accentOwn = accentOwn;
        this.accentOpponent = accentOpponent;
    }

    public Palette palette(int opacityPercent) {
        int a = Math.round(opacityPercent * 2.55f) << 24;
        return new Palette(a | panelRgb, edgeRgb < 0 ? 0 : a | edgeRgb, text, subText, accentOwn, accentOpponent);
    }
}
