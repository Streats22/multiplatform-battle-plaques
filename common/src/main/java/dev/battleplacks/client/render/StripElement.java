package dev.battleplacks.client.render;

import dev.battleplacks.client.PlaqueModel;
import dev.battleplacks.client.PlaqueModel.TeamMember;
import dev.battleplacks.config.PlaqueConfig;
import dev.battleplacks.config.TeamLayout;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

/**
 * The compact strip under Cobblemon's own tile: a big trainer in front of their Pokemon (the active one, or more of
 * the team depending on the layout), aligned to the top of the strip and continuing down behind the party bar.
 * The background texture is optional.
 */
final class StripElement implements CardElement {
    private static final ResourceLocation BACKGROUND =
            ResourceLocation.fromNamespaceAndPath("battleplacks", "textures/gui/strip.png");
    private static final int HEIGHT = 44, PAD = 3;
    private static final int TRAINER_OFFSET = 28, POKEMON_OFFSET = 44;
    private static final float TRAINER_SCALE = 50f;
    /** Pokemon sizes at 100% "Pokémon size": 22 is about a Cobblemon tile portrait. */
    private static final float ACTIVE_SIZE = 22, DOUBLES_SIZE = 17, TEAM_SIZE = 16, BENCH_SIZE = 12, MIN_SIZE = 6;
    private static final int ACTIVE_GAP = 6, ROW_GAP = 2;

    private final PlaqueModel model;
    private final TrainerEntityPainter painter;
    /** How far the models may continue below the strip, behind the element drawn under it (the party bar). */
    private final int reachBelow;

    StripElement(PlaqueModel model, TrainerEntityPainter painter, int reachBelow) {
        this.model = model;
        this.painter = painter;
        this.reachBelow = reachBelow;
    }

    @Override
    public int height() { return HEIGHT; }

    @Override
    public void draw(CardContext c, int y) {
        GuiGraphics g = c.g();
        if (c.cfg().showStripBackground) {
            g.setColor(1f, 1f, 1f, c.alpha());
            g.blit(BACKGROUND, c.x(), y, 0, 0, CardContext.WIDTH, HEIGHT, CardContext.WIDTH, HEIGHT);
            g.setColor(1f, 1f, 1f, 1f);
        }
        g.enableScissor(c.x() + 1, y + 1, c.x() + CardContext.WIDTH - 1, y + HEIGHT + reachBelow);
        drawPokemon(c, y);
        drawTrainer(c, y);
        g.disableScissor();
    }

    /**
     * Pokemon first so the trainer stands in front, laid out by the side's {@link TeamLayout}. At pokemonOffset 0 the
     * top of a Pokemon sits at the top of the strip and the strip's bottom edge cuts it off (like the trainer).
     * Everything is placed by distance from the card's near edge, so right-hand cards mirror automatically.
     */
    private void drawPokemon(CardContext c, int y) {
        PlaqueConfig cfg = c.cfg();
        int start = POKEMON_OFFSET + cfg.pokemonSideways;
        int top = y + PAD + cfg.pokemonOffset;
        float scale = cfg.pokemonSize / 100f;
        switch (layoutFor(cfg)) {
            case ACTIVE_ONLY -> drawActive(c, start, top, scale);
            case ACTIVE_AND_BENCH -> {
                int end = drawActive(c, start, top, scale);
                float size = BENCH_SIZE * scale;
                // The bench sits low, so its heads show above the party bar and the rest continues behind it
                drawRow(c, bench(cfg), end + ROW_GAP, Math.round(y + HEIGHT - size * 1.1f), size, false);
            }
            case FULL_TEAM -> drawRow(c, known(cfg), start, top, TEAM_SIZE * scale, true);
            case MY_PICKS -> {
                List<TeamMember> picks = picks(cfg);
                if (picks.isEmpty()) drawActive(c, start, top, scale);
                else drawRow(c, picks, start, top, ACTIVE_SIZE * scale, true);
            }
        }
    }

    /** "My picks" is about your own Pokémon; the opponent's side shows the active one plus their bench instead. */
    private TeamLayout layoutFor(PlaqueConfig cfg) {
        return cfg.teamLayout == TeamLayout.MY_PICKS && !model.own() ? TeamLayout.ACTIVE_AND_BENCH : cfg.teamLayout;
    }

    /** The Pokemon on the field at full size. Returns the distance from the near edge where the next one would go. */
    private int drawActive(CardContext c, int start, int top, float scale) {
        int count = model.active().size();
        float size = (count > 1 ? DOUBLES_SIZE : ACTIVE_SIZE) * scale;
        int fromEdge = start;
        for (int i = 0; i < count; i++) {
            var portrait = model.active().get(i).portrait();
            if (portrait != null) {
                float cx = c.edgeX(fromEdge, Math.round(size)) + size / 2;
                portrait.draw(c.g(), cx, top, size, !c.left(), c.pokemonPartial());
            }
            fromEdge += Math.round(size + ACTIVE_GAP);
        }
        return fromEdge;
    }

    /**
     * A row of team members starting {@code fromEdge} from the near edge, shrunk to fit the card if needed.
     * Fainted Pokémon are drawn darkened, unknown ones as a "?" placeholder.
     */
    private void drawRow(CardContext c, List<TeamMember> members, int fromEdge, int top, float size, boolean markActive) {
        if (members.isEmpty()) return;
        int room = CardContext.WIDTH - 2 - fromEdge;
        size = Math.max(MIN_SIZE, Math.min(size, (float) room / members.size() - ROW_GAP));
        GuiGraphics g = c.g();
        for (int i = 0; i < members.size(); i++) {
            TeamMember m = members.get(i);
            int x = c.edgeX(fromEdge + Math.round(i * (size + ROW_GAP)), Math.round(size));
            if (m.portrait() == null) {
                drawUnknown(c, x, top, Math.round(size));
                continue;
            }
            boolean fainted = m.state() == PlaqueModel.SlotState.FAINTED;
            if (fainted) g.setColor(0.35f, 0.35f, 0.35f, 1f); // darkens the 3D model (ColorModulator)
            m.portrait().draw(g, x + size / 2, top, size, !c.left(), c.pokemonPartial());
            if (fainted) g.setColor(1f, 1f, 1f, 1f);
            if (markActive && m.active()) g.fill(x, top - 2, x + Math.round(size), top - 1, c.fade(c.accent()));
        }
    }

    private static void drawUnknown(CardContext c, int x, int top, int size) {
        int boxTop = top + size / 4;
        c.g().fill(x + 1, boxTop, x + size - 1, boxTop + size - 2, c.fade(0xAA2A2F3A));
        int textX = x + (size - c.font().width("?")) / 2;
        c.g().drawString(c.font(), "?", textX, boxTop + (size - 2 - 8) / 2 + 1, c.fade(0xFFB0B0B0), false);
    }

    /** The team minus empty slots: our own padding never shows, the opponent's unseen slots follow the setting. */
    private List<TeamMember> known(PlaqueConfig cfg) {
        boolean showUnknown = !model.own() && cfg.showUnknownSlots;
        return model.team().stream().filter(m -> m.portrait() != null || showUnknown && m.id() == null).toList();
    }

    private List<TeamMember> bench(PlaqueConfig cfg) {
        return known(cfg).stream().filter(m -> !m.active()).toList();
    }

    private List<TeamMember> picks(PlaqueConfig cfg) {
        return model.team().stream()
                .filter(m -> m.id() != null && cfg.pickedPokemon.contains(m.id().toString()))
                .toList();
    }

    /** Head at the top of the strip (moved by trainerOffset), body running down until the strip ends. */
    private void drawTrainer(CardContext c, int y) {
        int x = c.edgeX(TRAINER_OFFSET, 0);
        int top = y + PAD + c.cfg().trainerOffset;
        float scale = TRAINER_SCALE * c.cfg().trainerSize / 100f;
        painter.find(model.entityId()).ifPresentOrElse(
                e -> painter.paintHangingFrom(c, e, x, top, scale, model.own()),
                () -> c.g().fill(x - 8, top, x + 8, top + 16, 0xFF55607A));
    }
}
