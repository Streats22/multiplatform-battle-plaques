package dev.battleplacks.client.cobblemon;

import com.cobblemon.mod.common.api.battles.model.actor.ActorType;
import com.cobblemon.mod.common.client.CobblemonClient;
import com.cobblemon.mod.common.client.battle.ActiveClientBattlePokemon;
import com.cobblemon.mod.common.client.battle.ClientBattle;
import com.cobblemon.mod.common.client.battle.ClientBattleActor;
import com.cobblemon.mod.common.client.battle.ClientBattlePokemon;
import com.cobblemon.mod.common.client.battle.ClientBattleSide;
import com.cobblemon.mod.common.pokemon.Gender;
import dev.battleplacks.client.BattleSource;
import dev.battleplacks.client.PlaqueModel;
import dev.battleplacks.client.PlaqueModel.Active;
import dev.battleplacks.client.PlaqueModel.Kind;
import dev.battleplacks.team.SharedTeams;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * The only class that reads Cobblemon's client battle state. Signatures checked with javap against
 * Cobblemon 1.8.4 (Fabric and NeoForge jars). RCT trainers are ordinary battles whose actor type is NPC.
 */
public final class CobblemonBattleSource implements BattleSource {
    private final PokemonPortraits portraits = new PokemonPortraits();
    private final PartyReader parties = new PartyReader(portraits);

    @Override
    public boolean inBattle() {
        return CobblemonClient.INSTANCE.getBattle() != null;
    }

    @Override
    public Optional<UUID> battleId() {
        ClientBattle battle = CobblemonClient.INSTANCE.getBattle();
        return battle == null ? Optional.empty() : Optional.of(battle.getBattleId());
    }

    @Override
    public List<PlaqueModel> snapshot() {
        ClientBattle battle = CobblemonClient.INSTANCE.getBattle();
        if (battle == null) return List.of();
        parties.startBattle(battle.getBattleId());

        UUID me = Minecraft.getInstance().player == null ? null : Minecraft.getInstance().player.getUUID();
        boolean mineIsSide1 = battle.getSide1().getActors().stream().anyMatch(a -> a.getUuid().equals(me));

        List<PlaqueModel> models = new ArrayList<>();
        addSide(models, mineIsSide1 ? battle.getSide1() : battle.getSide2(), me, true);
        addSide(models, mineIsSide1 ? battle.getSide2() : battle.getSide1(), me, false);
        return models;
    }

    @Override
    public List<SharedTeams.Member> ownTeamToShare() {
        ClientBattle battle = CobblemonClient.INSTANCE.getBattle();
        UUID me = Minecraft.getInstance().player == null ? null : Minecraft.getInstance().player.getUUID();
        if (battle == null || me == null) return List.of();
        for (ClientBattleSide side : List.of(battle.getSide1(), battle.getSide2())) {
            for (ClientBattleActor actor : side.getActors()) {
                if (actor.getUuid().equals(me)) return parties.shareable(actor);
            }
        }
        return List.of();
    }

    private void addSide(List<PlaqueModel> out, ClientBattleSide side, UUID me, boolean ownSide) {
        for (ClientBattleActor actor : side.getActors()) {
            boolean isMe = actor.getUuid().equals(me);
            List<Active> active = new ArrayList<>();
            for (ActiveClientBattlePokemon slot : actor.getActivePokemon()) {
                if (slot.getBattlePokemon() != null) active.add(toActive(slot.getBattlePokemon()));
            }
            out.add(new PlaqueModel(kindOf(actor), actor.getDisplayName().getString(), actor.getUuid(), ownSide,
                    isMe ? parties.own(actor) : parties.opponent(actor), active));
        }
    }

    private static Kind kindOf(ClientBattleActor actor) {
        ActorType type = actor.getType();
        return type == ActorType.PLAYER ? Kind.PLAYER : type == ActorType.WILD ? Kind.WILD : Kind.NPC;
    }

    private Active toActive(ClientBattlePokemon p) {
        Gender g = p.getGender();
        char gender = g == Gender.MALE ? 'M' : g == Gender.FEMALE ? 'F' : '-';
        return new Active(p.getDisplayName().getString(), p.getLevel(), gender,
                p.getHpValue() / Math.max(1f, p.getMaxHp()), (int) p.getHpValue(), (int) p.getMaxHp(), p.isHpFlat(),
                p.getStatus() == null ? null : p.getStatus().getShowdownName(), portraits.of(p));
    }
}
