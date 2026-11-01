package dev.battleplacks.client;

import java.util.List;

/** Where battle state comes from. The renderer depends on this interface, never on Cobblemon directly. */
public interface BattleSource {
    boolean inBattle();

    /** One model per trainer, the local player first. Empty when no battle is running. */
    List<PlaqueModel> snapshot();
}
