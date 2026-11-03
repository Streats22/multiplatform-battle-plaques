package dev.battleplacks.client.cobblemon;

import com.cobblemon.mod.common.api.gui.GuiUtilsKt;
import com.cobblemon.mod.common.client.battle.ClientBattlePokemon;
import com.cobblemon.mod.common.client.render.models.blockbench.FloatingState;
import com.cobblemon.mod.common.client.render.models.blockbench.PosableState;
import com.cobblemon.mod.common.pokemon.Species;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.battleplacks.client.PlaqueModel.PortraitDrawer;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Draws a Pokémon's 3D model with Cobblemon's own portrait renderer. That renderer hangs the model DOWN from the
 * point it is given (it is not centred on it), so the box we clip to starts at the requested top and runs downward.
 * Checked with javap against Cobblemon 1.8.4's BattleOverlay and PartyOverlay.
 */
final class PokemonPortraits {
    /** BattleOverlay uses scale 18 * 0.65 for its 28 px portrait window. */
    private static final float SCALE_PER_SIZE = 0.64f;
    /** The clip box is larger than the nominal size so more of the model shows. */
    private static final float CLIP_PER_SIZE = 1.8f;
    /** BattleOverlay draws from 5 px above its 28 px window; the same ratio keeps the head inside our box. */
    private static final float ANCHOR_ABOVE_PER_SIZE = 5f / 28f;

    /** One posing state per Pokémon that is not on the field, like PartyOverlay keeps per party slot. */
    private final Map<UUID, FloatingState> benchStates = new HashMap<>();

    /** Forget bench states; called when a new battle starts. */
    void clear() { benchStates.clear(); }

    /** A Pokémon on the field: Cobblemon already keeps its posing state. */
    PortraitDrawer of(ClientBattlePokemon pokemon) {
        PosableState state = pokemon.getState();
        Species species = pokemon.getSpecies();
        return drawer(species.getResourceIdentifier(), species.getForm(state.getCurrentAspects()).getBaseScale(), state);
    }

    /** A Pokémon that is not on the field (bench, or a shared team): known by its id, species and form aspects. */
    PortraitDrawer of(UUID id, Species species, Set<String> aspects) {
        FloatingState state = benchStates.computeIfAbsent(id, k -> new FloatingState());
        state.setCurrentAspects(aspects);
        return drawer(species.getResourceIdentifier(), species.getForm(aspects).getBaseScale(), state);
    }

    private static PortraitDrawer drawer(ResourceLocation species, float baseScale, PosableState state) {
        return (g, cx, top, size, reversed, partial) -> {
            PoseStack pose = g.pose();
            float clip = size * CLIP_PER_SIZE;
            g.enableScissor((int) (cx - clip / 2), (int) top, (int) (cx + clip / 2), (int) (top + clip));
            pose.pushPose();
            pose.translate(cx, top - size * ANCHOR_ABOVE_PER_SIZE, 0);
            GuiUtilsKt.drawPosablePortrait(species, pose, size * SCALE_PER_SIZE, baseScale, reversed, state, partial);
            pose.popPose();
            g.disableScissor();
        };
    }
}
