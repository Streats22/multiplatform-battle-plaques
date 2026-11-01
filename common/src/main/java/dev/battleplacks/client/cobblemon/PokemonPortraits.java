package dev.battleplacks.client.cobblemon;

import com.cobblemon.mod.common.api.gui.GuiUtilsKt;
import com.cobblemon.mod.common.client.battle.ClientBattlePokemon;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.battleplacks.client.PlaqueModel.PortraitDrawer;

/** Draws a Pokémon's 3D model with Cobblemon's own portrait renderer, clipped to a square. */
final class PokemonPortraits {
    /** BattleOverlay uses scale 18 for a 28 px portrait. */
    private static final float SCALE_PER_SIZE = 0.64f;

    PortraitDrawer of(ClientBattlePokemon pokemon) {
        return (g, cx, cy, size, reversed, partial) -> {
            PoseStack pose = g.pose();
            g.enableScissor((int) (cx - size / 2), (int) (cy - size / 2), (int) (cx + size / 2), (int) (cy + size / 2));
            pose.pushPose();
            pose.translate(cx, cy, 0);
            // TODO(visual): vertical offset and scale of the model inside the frame need an in-game check.
            GuiUtilsKt.drawPosablePortrait(
                    pokemon.getSpecies().getResourceIdentifier(), pose, size * SCALE_PER_SIZE,
                    pokemon.getSpecies().getForm(pokemon.getState().getCurrentAspects()).getBaseScale(),
                    reversed, pokemon.getState(), partial);
            pose.popPose();
            g.disableScissor();
        };
    }
}
