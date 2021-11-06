package data.scripts.world;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.SectorGeneratorPlugin;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import exerelin.campaign.PlayerFactionStore;

public class Seriouslywhatisthewholepointofthisisweartoluddiwillmakethispersoneattheirshoe implements SectorGeneratorPlugin {
    @Override
    public void generate(SectorAPI sector) {
        Global.getSector().addScript(new MyLoveForIron());
        Makethefactionrelationshipgobrrrrt(sector);
    }

    public static void Makethefactionrelationshipgobrrrrt(SectorAPI sector) {
        FactionAPI iron = sector.getFaction("ironshell");

        //sector.getFaction(Factions.PLAYER).setRelationship(iron.getId(), RepLevel.COOPERATIVE);
        iron.setRelationship(Factions.HEGEMONY, 1.00f); //Tax-exempted.
        //iron.setRelationship(Factions.PLAYER, Global.getSector().getPlayerFaction().getRelationship(Factions.HEGEMONY)); //Be a good little taxpayer.
        if (Global.getSettings().getModManager().isModEnabled("nexerelin")) {
            if ("ironshell".equals(PlayerFactionStore.getPlayerFactionIdNGC()) || "hegemony".equals(PlayerFactionStore.getPlayerFactionIdNGC())) {
                iron.setRelationship(Factions.HEGEMONY, 0.7f); //Because you sync relations at least at the start, you get decreased relations here. Put in the work to make them cooperative.
            }
        }
        iron.setRelationship(Factions.TRITACHYON, -0.40f); //Tax resistance is illegal.
        iron.setRelationship(Factions.PERSEAN, -0.40f); //Tax resistance is illegal.
        iron.setRelationship(Factions.PIRATES, -0.50f); //Definite refusal to pay taxes deserve punishment.
        iron.setRelationship(Factions.LUDDIC_CHURCH, -0.20f); //Religious exemptions need not apply here.
        iron.setRelationship(Factions.LUDDIC_PATH, -0.50f); //"Freedom Fighting" is not tax-exempted.
        iron.setRelationship(Factions.REMNANTS, -0.50f); //Lawful neutral property are still taxable.
        
        //I mean I talked to these mod authors about relations and they seem cool, mostly because I just copy Hegemony's relations for it.
        //Lol.. or they never responded so I just looked in what Hegemony gave them. x)
        
        iron.setRelationship("al_ars", -0.50f); //Does not pay taxes.
        iron.setRelationship("almighty_dollar", 0.15f); //Tax writeoffs.
        iron.setRelationship("blade_breakers", -1.00f); //Aggressively resist taxes
        iron.setRelationship("communist_clouds", -0.60f); //For some weird reason... doesn't like paying taxes.
        iron.setRelationship("dassault_mikoyan", -0.75f); //Formerly known author never pays taxes, scratch it does pay taxes
        iron.setRelationship("diableavionics", -0.60f); //Does not pay taxes.
        iron.setRelationship("exalted", -1.00f); //Tax evaders are not tolerated.
        iron.setRelationship("interstellarimperium", -0.80f); //Definitely does not pay taxes.
        iron.setRelationship("kadur_remnant", -1.00f); //Pay taxes, but wants a tax return. Unacceptable.
        iron.setRelationship("keruvim", -0.60f); //Does not pay taxes.
        iron.setRelationship("mayasura", -1.00f); //Does not pay taxes.
        iron.setRelationship("magellan_protectorate", -0.90f); //Formerly known author never pays taxes.
        iron.setRelationship("prv", -0.30f); //Late-tax deadline.
        iron.setRelationship("ocua", -0.30f); //Late-tax deadline.
        iron.setRelationship("rb", -0.60f); //Does not pay taxes.
        iron.setRelationship("scalartech", -0.30f); //Late-tax deadline.
        iron.setRelationship("science_fuckers", -0.60f); //Definitely does not pay taxes.
        iron.setRelationship("shadow_industry", -0.60f); //Does not pay taxes, in facts is defiant and resistant.
        iron.setRelationship("star_federation", -0.40f); //Must pay taxes.
        iron.setRelationship("sylphon", -0.20f); //Duly pay their taxes like 5000 cycles ago.
        iron.setRelationship("tahlan_legioinfernalis", -0.90f); //Aggressively resist taxes.
        iron.setRelationship("tiandong", 0f); //Tax-exempted.
        iron.setRelationship("xhanempire", -0.60f); //Does not pay taxes.
        iron.setRelationship("warhawk_republic", -0.60f); //Does not pay taxes.
        iron.setRelationship("xlu", -0.80f); //Does not pay taxes.
        iron.setRelationship("unitedpamed", 0.25f); //Tax-exempted.
        iron.setRelationship("vic", 0.2f); //Bad bad rat never pay the tax. then selkie said plz can we be ally then selkie said omg we need to be enemy omg we need to be ally again, nvm enemy astarat cringe!
    }
}