package data.scripts.world;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.SectorGeneratorPlugin;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import static data.scripts.eis_modPlugin.IRONSTANDSETERNAL;
import exerelin.campaign.AllianceManager;
import exerelin.campaign.DiplomacyManager;
import exerelin.campaign.alliances.Alliance;
import exerelin.campaign.PlayerFactionStore;
import exerelin.utilities.NexConfig;
import exerelin.utilities.NexFactionConfig;
import java.util.Map;

public class Seriouslywhatisthewholepointofthisisweartoluddiwillmakethispersoneattheirshoe implements SectorGeneratorPlugin {
    @Override
    public void generate(SectorAPI sector) {
        Global.getSector().addScript(new MyLoveForIron());
        Makethefactionrelationshipgobrrrrt(sector);
    }

    public static void Makethefactionrelationshipgobrrrrt(SectorAPI sector) {
        FactionAPI iron = sector.getFaction(IRONSTANDSETERNAL);
        //sector.getFaction(Factions.PLAYER).setRelationship(iron.getId(), RepLevel.COOPERATIVE);
        iron.setRelationship(Factions.HEGEMONY, 1.00f); //Tax-exempted.
        if (Global.getSettings().getBoolean("GreaterHegemony")) {
            Alliance lol = AllianceManager.createAlliance(IRONSTANDSETERNAL, Factions.HEGEMONY, AllianceManager.getBestAlignment(IRONSTANDSETERNAL, Factions.HEGEMONY));
            lol.setName(Global.getSettings().getString("eis_ironshell", "eis_greaterhegemony"));
            lol.addPermaMember(IRONSTANDSETERNAL);
            lol.addPermaMember("hegemony");
        }
        iron.setRelationship(Factions.PLAYER, Global.getSector().getPlayerFaction().getRelationship(Factions.HEGEMONY)); //Be a good little taxpayer.
        if (Global.getSettings().getModManager().isModEnabled("nexerelin")) {
            NexFactionConfig factionConfig = NexConfig.getFactionConfig(IRONSTANDSETERNAL);if (!DiplomacyManager.isRandomFactionRelationships()) {factionConfig.minRelationships.clear();factionConfig.minRelationships.put("hegemony", 0.251f);}
            for (Map.Entry<String, Float> entry : factionConfig.startRelationships.entrySet()){
                if (IRONSTANDSETERNAL.equals(entry.getKey())) continue;
                iron.setRelationship(entry.getKey(), entry.getValue());
            }
            if (IRONSTANDSETERNAL.equals(PlayerFactionStore.getPlayerFactionIdNGC()) || "hegemony".equals(PlayerFactionStore.getPlayerFactionIdNGC())) {
                iron.setRelationship(Factions.HEGEMONY, 0.75f); //Prevents you from instantly gaining 1 story point repping up to 100...
            }
        }
    }
}