package dev.battleplacks.client.cobblemon;

import com.cobblemon.mod.common.client.CobblemonClient;
import com.cobblemon.mod.common.pokemon.Pokemon;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/** The local player's current party, outside battle too; used by the config screen's "My picks" list. */
public final class PartyList {
    public record Entry(UUID id, String label) {}

    private PartyList() {}

    public static List<Entry> current() {
        List<Entry> entries = new ArrayList<>();
        var party = CobblemonClient.INSTANCE.getStorage().getParty();
        for (Pokemon pokemon : party.getSlots()) {
            if (pokemon == null) continue;
            entries.add(new Entry(pokemon.getUuid(), pokemon.getDisplayName(false).getString() + " (Lv. " + pokemon.getLevel() + ")"));
        }
        return entries;
    }
}
