package data.scripts;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.BaseCampaignEventListener;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.LocationAPI;
import com.fs.starfarer.api.campaign.NascentGravityWellAPI;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.PlanetSpecAPI;
import com.fs.starfarer.api.campaign.PlayerMarketTransaction;
import com.fs.starfarer.api.campaign.RepLevel;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.SpecialItemData;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.campaign.comm.IntelInfoPlugin;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.characters.AdminData;
import com.fs.starfarer.api.characters.FullName;
import com.fs.starfarer.api.characters.OfficerDataAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.FleetInteractionDialogPluginImpl.OptionId;
import com.fs.starfarer.api.impl.campaign.econ.impl.eis_SpecialItemEffectsRepo;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.HullMods;
import com.fs.starfarer.api.impl.campaign.ids.Submarkets;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.impl.campaign.intel.BaseMissionIntel.MissionResult;
import com.fs.starfarer.api.impl.campaign.intel.BaseMissionIntel.MissionState;
import com.fs.starfarer.api.impl.campaign.intel.FactionCommissionIntel;
import com.fs.starfarer.api.impl.campaign.shared.SharedData;
import com.fs.starfarer.api.loading.Description;
import com.fs.starfarer.api.loading.PersonMissionSpec;
import com.fs.starfarer.api.loading.VariantSource;
import com.fs.starfarer.api.util.Misc;
import java.awt.Color;
import exerelin.campaign.SectorManager;
import exerelin.campaign.intel.rebellion.RebellionIntel;
import data.scripts.world.Seriouslywhatisthewholepointofthisisweartoluddiwillmakethispersoneattheirshoe;
import exerelin.campaign.AllianceManager;
import exerelin.campaign.DiplomacyManager;
import exerelin.campaign.PlayerFactionStore;
import exerelin.campaign.alliances.Alliance;
import exerelin.utilities.NexConfig;
import exerelin.utilities.NexFactionConfig;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import kentington.capturecrew.CaptiveInteractionDialogPlugin;
import kentington.capturecrew.LootAddScript;
import org.dark.shaders.light.LightData;
import org.dark.shaders.util.ShaderLib;
import org.dark.shaders.util.TextureData;
import org.json.JSONException;
import org.lazywizard.lazylib.MathUtils;


public class eis_modPlugin extends BaseModPlugin {
    public static boolean haveNex = Global.getSettings().getModManager().isModEnabled("nexerelin");
    public static boolean haveSWP = Global.getSettings().getModManager().isModEnabled("swp");
    public static boolean haveNia = Global.getSettings().getModManager().isModEnabled("tahlan");
    public static boolean hasTart = Global.getSettings().getModManager().isModEnabled("TORCHSHIPS");
    
    public static final String IRONSTANDSETERNAL = "ironshell";
    
    @Override
    public void onNewGame() {
        if (haveNex && SectorManager.getCorvusMode()) {
            StarSystemAPI system = Global.getSector().getStarSystem("Naraka");
            for (SectorEntityToken stable_location_finder : system.getEntitiesWithTag(Tags.STABLE_LOCATION)) {
                if (stable_location_finder.getOrbitFocus().equals(system.getEntityById("naraka")) && !stable_location_finder.getId().equals("naraka_relay")) {
                    system.removeEntity(stable_location_finder);
                    SectorEntityToken relay = system.addCustomEntity(null, Global.getSettings().getString("eis_ironshell", "eis_modPlugin1"), "nav_buoy", "hegemony");
                    relay.setCircularOrbitPointingDown(system.getEntityById("naraka"), 0, 2750, 160);
                }
            }
            SectorEntityToken lollmao2 = system.addCustomEntity(null, Global.getSettings().getString("eis_ironshell", "eis_modPlugin2"), "sensor_array", "hegemony");
            lollmao2.setCircularOrbitPointingDown(system.getEntityById("naraka"), 90 + 60, 3000, 100);
            LocationAPI hyper = Global.getSector().getHyperspace();
            String amazing = "mamamia";
            for (SectorEntityToken e : hyper.getAllEntities()) {if (e.getLocation().x > 10800 && e.getLocation().x < 11200 && e.getLocation().y > -7200 && e.getLocation().y < -6800) {amazing = e.getId();}}Global.getSector().getHyperspace().removeEntity(Global.getSector().getEntityById(amazing));
            system.removeEntity(Global.getSector().getEntityById("yami"));
            system.removeEntity(Global.getSector().getEntityById("chitagupta"));
            PlanetAPI naraka_b = system.addPlanet("eis_yami", Global.getSector().getEntityById("yama"), "Yami", "cryovolcanic", 0, 60, 600, 40); //formerly 450 and 650
            naraka_b.setCustomDescriptionId("planet_yami_ironshell");
            PlanetAPI naraka_c = system.addPlanet("eis_chitagupta", Global.getSector().getEntityById("naraka"), "Chitagupta", "water", 90, 100, 5750, 380);
            NascentGravityWellAPI well = Global.getSector().createNascentGravityWell(naraka_c,50f);
            well.autoUpdateHyperLocationBasedOnInSystemEntityAtRadius(naraka_c, 470f);
            hyper.addEntity(well);
            naraka_c.setCustomDescriptionId("planet_chitagupta_ironshell");
            PlanetSpecAPI sex = naraka_c.getSpec();
            sex.setAtmosphereColor(new Color(30, 90, 140, 130));
            sex.setAtmosphereThickness(0.4f);
            sex.setAtmosphereThicknessMin(62f);
            sex.setCloudTexture("graphics/planets/clouds_white.png");
            sex.setCloudColor(new Color (255,255,255,200));
            sex.setCloudRotation(-3f);
            sex.setIconColor(new Color (45,98,174,255));
            sex.setPlanetColor(new Color (255,255,255,255));
            naraka_c.applySpecChanges();
            SharedData.getData().getPersonBountyEventData().addParticipatingFaction(IRONSTANDSETERNAL);
        }
        new Seriouslywhatisthewholepointofthisisweartoluddiwillmakethispersoneattheirshoe().generate(Global.getSector());
    }
    
    @Override
    public void onApplicationLoad() throws JSONException, IOException {
        if (Global.getSettings().getModManager().isModEnabled("shaderLib") && Global.getSettings().loadJSON("GRAPHICS_OPTIONS.ini").getBoolean("enableShaders")) {
            ShaderLib.init();
            TextureData.readTextureDataCSV("data/lights/eis_texture_data.csv");
            LightData.readLightDataCSV("data/lights/eis_lights_data.csv");
        }
            if (Global.getSettings().getHullSpec("eagle_xiv") != null) {Global.getSettings().getHullSpec("eagle_xiv").addTag("eis_bp");}
            if (Global.getSettings().getFighterWingSpec("eis_piranha") != null) {Global.getSettings().getFighterWingSpec("eis_piranha").addTag("leader_no_swarm");Global.getSettings().getFighterWingSpec("eis_piranha").addTag("attack_at_an_angle");}
            if (Global.getSettings().getHullSpec("dominator_xiv") != null) {Global.getSettings().getHullSpec("dominator_xiv").addTag("eis_bp");}if (Global.getSettings().getHullSpec("enforcer_xiv") != null) {Global.getSettings().getHullSpec("enforcer_xiv").addTag("eis_bp");}if (Global.getSettings().getHullSpec("falcon_xiv") != null) {Global.getSettings().getHullSpec("falcon_xiv").addTag("eis_bp");}
            if (Global.getSettings().getHullSpec("onslaught_xiv") != null) {Global.getSettings().getHullSpec("onslaught_xiv").addTag("eis_bp");}
            if (hasTart) {
                if (Global.getSettings().getHullSpec("TADA_tinnitus_xiv") != null) {Global.getSettings().getHullSpec("TADA_tinnitus_xiv").addTag("eis_bp");}
                if (Global.getSettings().getHullSpec("TADA_gunwall_XIV") != null) {Global.getSettings().getHullSpec("TADA_gunwall_XIV").addTag("eis_bp");}
            }
            if (haveNia) {
                if (Global.getSettings().getHullSpec("tahlan_nelson_xiv") != null) {Global.getSettings().getHullSpec("tahlan_nelson_xiv").addTag("eis_bp");}
		if (Global.getSettings().getHullSpec("tahlan_tower_xiv") != null) {Global.getSettings().getHullSpec("tahlan_tower_xiv").addTag("eis_bp");}
            }
            if (haveSWP) {
                if (Global.getSettings().getHullSpec("swp_alastor_xiv") != null) {Global.getSettings().getHullSpec("swp_alastor_xiv").addTag("eis_bp");}
                if (Global.getSettings().getHullSpec("swp_conquest_xiv") != null) {Global.getSettings().getHullSpec("swp_conquest_xiv").addTag("eis_bp");}
                if (Global.getSettings().getHullSpec("swp_gryphon_xiv") != null) {Global.getSettings().getHullSpec("swp_gryphon_xiv").addTag("eis_bp");}
                if (Global.getSettings().getHullSpec("swp_hammerhead_xiv") != null) {Global.getSettings().getHullSpec("swp_hammerhead_xiv").addTag("eis_bp");}
                if (Global.getSettings().getHullSpec("swp_lasher_xiv") != null) {Global.getSettings().getHullSpec("swp_lasher_xiv").addTag("eis_bp");}
                if (Global.getSettings().getHullSpec("swp_sunder_xiv") != null) {Global.getSettings().getHullSpec("swp_sunder_xiv").addTag("eis_bp");}
            }
            Global.getSettings().resetCached();
            if (Global.getSettings().getVariant("eis_eradicator_elite") != null) {
                ShipVariantAPI EliteEVariant = Global.getSettings().getVariant("eis_eradicator_elite");
                if (haveSWP) {
                    if (Global.getSettings().getWeaponSpec("swp_iontorpedo") != null) {
                        EliteEVariant.setNumFluxCapacitors(EliteEVariant.getNumFluxCapacitors()-6); //27 to 21
                        EliteEVariant.clearSlot("WS 000");
                        EliteEVariant.addWeapon("WS 000", "swp_iontorpedo"); //from atropos
                        EliteEVariant.clearSlot("WS 001");
                        EliteEVariant.addWeapon("WS 001","swp_iontorpedo"); //from atropos
                    }
                }
            }
            if (Global.getSettings().getVariant("eis_vengeance_standard") != null) {
                ShipVariantAPI StandardVVariant = Global.getSettings().getVariant("eis_vengeance_standard");
                if (haveNia) {
                    if (Global.getSettings().getWeaponSpec("tahlan_armiger") != null) {
                        StandardVVariant.setNumFluxCapacitors(StandardVVariant.getNumFluxCapacitors()-10);
                        StandardVVariant.clearSlot("WS0012");
                        StandardVVariant.addWeapon("WS0012", "tahlan_armiger"); //from tac laser
                    }
                } 
            }
            if (Global.getSettings().getVariant("eis_tyrant_standard") != null) {
                ShipVariantAPI StandardTVariant = Global.getSettings().getVariant("eis_tyrant_standard");
                if (haveSWP) {
                    if (Global.getSettings().getWeaponSpec("swp_contender") != null) {
                        StandardTVariant.setNumFluxCapacitors(StandardTVariant.getNumFluxCapacitors()+1);
                        StandardTVariant.clearSlot("WS0001");
                        StandardTVariant.addWeapon("WS0001", "swp_contender"); //from vulcan
                    }
                }
                if (haveNia) {
                    if (Global.getSettings().getWeaponSpec("tahlan_efreet") != null) {
                        //StandardTVariant.setNumFluxCapacitors(StandardTVariant.getNumFluxCapacitors()-1);
                        StandardTVariant.clearSlot("WS0020");
                        StandardTVariant.addWeapon("WS0020", "tahlan_efreet"); //from gauss
                    }
                    if (Global.getSettings().getWeaponSpec("tahlan_armiger") != null) {
                        //StandardTVariant.setNumFluxCapacitors(StandardTVariant.getNumFluxCapacitors()-1);
                        StandardTVariant.clearSlot("WS0021");
                        StandardTVariant.addWeapon("WS0021", "tahlan_armiger"); //from gauss
                    }
                } 
            }
            if (Global.getSettings().getVariant("eis_skyrend_standard") != null) {
                ShipVariantAPI StandardSVariant = Global.getSettings().getVariant("eis_skyrend_standard");
                if (haveSWP) {
                    if (Global.getSettings().getWeaponSpec("swp_lightphaselance") != null) {
                        StandardSVariant.clearSlot("WS0009");
                        StandardSVariant.addWeapon("WS0009", "swp_lightphaselance"); //from burst pd laser
                        StandardSVariant.clearSlot("WS0010");
                        StandardSVariant.addWeapon("WS0010", "swp_lightphaselance"); //from burst pd laser
                        StandardSVariant.clearSlot("WS0011");
                        StandardSVariant.addWeapon("WS0011", "swp_lightphaselance"); //from burst pd laser
                        StandardSVariant.clearSlot("WS0012");
                        StandardSVariant.addWeapon("WS0012", "swp_lightphaselance"); //from burst pd laser
                    }
                } 
            }
            if (Global.getSettings().getVariant("eis_endeavor_attack") != null) {
                ShipVariantAPI AttackEVariant = Global.getSettings().getVariant("eis_endeavor_attack");
                if (haveSWP) {
                    if (Global.getSettings().getWeaponSpec("swp_iontorpedo") != null) {
                        AttackEVariant.setNumFluxCapacitors(AttackEVariant.getNumFluxCapacitors()-6); //from 22 to 18
                        AttackEVariant.clearSlot("WS0028");
                        AttackEVariant.addWeapon("WS0028", "swp_iontorpedo"); //from harpoon
                        AttackEVariant.clearSlot("WS0029");
                        AttackEVariant.addWeapon("WS0029", "swp_iontorpedo"); //from harpoon
                    }
                } 
            }
            if (Global.getSettings().getVariant("eis_endeavor_standard") != null) {
                ShipVariantAPI StandardEVariant = Global.getSettings().getVariant("eis_endeavor_standard");
                if (haveNia) {
                    if (Global.getSettings().getWeaponSpec("tahlan_armiger") != null) {
                        StandardEVariant.setNumFluxCapacitors(StandardEVariant.getNumFluxCapacitors()-10); //from 39 to 29
                        StandardEVariant.clearSlot("WS0011");
                        StandardEVariant.addWeapon("WS0011", "tahlan_armiger"); //from mjonir
                    }
                } 
            }
            if (Global.getSettings().getVariant("eis_endeavor_support") != null) {
                ShipVariantAPI SupportEVariant = Global.getSettings().getVariant("eis_endeavor_support");
                if (haveSWP) {
                    if (Global.getSettings().getWeaponSpec("swp_iontorpedo") != null) {
                        SupportEVariant.setNumFluxCapacitors(SupportEVariant.getNumFluxCapacitors()-4); //from 21 to 17
                        SupportEVariant.clearSlot("WS0028");
                        SupportEVariant.addWeapon("WS0028", "swp_iontorpedo"); //from harpoon
                        SupportEVariant.clearSlot("WS0029");
                        SupportEVariant.addWeapon("WS0029", "swp_iontorpedo"); //from harpoon
                    }
                } 
            }
            if (Global.getSettings().getVariant("eis_indomitable_assault") != null) {
                ShipVariantAPI AssaultIVariant = Global.getSettings().getVariant("eis_indomitable_assault");
                if (haveNia) {
                    if (Global.getSettings().getWeaponSpec("tahlan_efreet") != null) {
                        AssaultIVariant.setNumFluxCapacitors(AssaultIVariant.getNumFluxCapacitors()-8); //from 24 to 16
                        AssaultIVariant.clearSlot("WS0003");
                        AssaultIVariant.addWeapon("WS0003", "tahlan_efreet"); //from mjolnir
                    }
                } 
            }
            if (Global.getSettings().getVariant("eis_indomitable_missile") != null) {
                ShipVariantAPI MissileIVariant = Global.getSettings().getVariant("eis_indomitable_missile");
                if (haveSWP) {
                    if (Global.getSettings().getWeaponSpec("swp_iontorpedo") != null) {
                        MissileIVariant.setNumFluxCapacitors(MissileIVariant.getNumFluxCapacitors()-4); //from 24 to 20
                        MissileIVariant.clearSlot("WS0005");
                        MissileIVariant.addWeapon("WS0005", "swp_iontorpedo"); //from annihilator
                        MissileIVariant.clearSlot("WS0006");
                        MissileIVariant.addWeapon("WS0006", "swp_iontorpedo"); //from annihilator
                    }
                } 
            }
            if (Global.getSettings().getVariant("eis_champion_standard") != null) {
                ShipVariantAPI StandardCVariant = Global.getSettings().getVariant("eis_champion_standard");
                if (haveNia) {
                    if (Global.getSettings().getWeaponSpec("tahlan_armiger") != null) {
                        StandardCVariant.setNumFluxCapacitors(StandardCVariant.getNumFluxCapacitors()-10); //from 26 to 16
                        StandardCVariant.clearSlot("WS 008");
                        StandardCVariant.addWeapon("WS 008", "tahlan_armiger"); //from mjollnir
                    }
                } 
            }
            if (Global.getSettings().getVariant("eis_champion_elite") != null) {
                ShipVariantAPI EliteCVariant = Global.getSettings().getVariant("eis_champion_elite");
                if (hasTart) {
                    if (Global.getSettings().getWeaponSpec("TADA_dioscuri") != null) {
                        EliteCVariant.setNumFluxCapacitors(EliteCVariant.getNumFluxCapacitors()+2); //from 22 to 24
                        EliteCVariant.clearSlot("WS 008");
                        EliteCVariant.addWeapon("WS 008", "TADA_dioscuri"); //from hephag
                    }
                } 
            }
            Global.getSettings().resetCached();
            if (Global.getSettings().getMissionScore("eis_traitors") > 0) {
                for (int i=1; i <= NexConfig.getFactionConfig("ironshell").getStartFleetSet(NexFactionConfig.StartFleetType.SUPER.name()).getNumFleets(); i++) {
                    if ("eis_indomitable_missile".equals(NexConfig.getFactionConfig("ironshell").getStartFleetSet(NexFactionConfig.StartFleetType.SUPER.name()).getFleet(i).get(0))) {break;}
                    if ((i) == NexConfig.getFactionConfig("ironshell").getStartFleetSet(NexFactionConfig.StartFleetType.SUPER.name()).getNumFleets()) {
                        List<String> indomitableFleet = new ArrayList<>(1);
                        indomitableFleet.add("eis_indomitable_missile");
                        indomitableFleet.add("eis_flagellator_standard");
                        indomitableFleet.add("eis_vanguard_xiv_elite2");
                        NexConfig.getFactionConfig("ironshell").getStartFleetSet(NexFactionConfig.StartFleetType.SUPER.name()).addFleet(indomitableFleet);
                        break;
                    }
                }
            }
            if (Global.getSettings().getMissionScore("eis_blanks") > 0) {
                for (int i=1; i <= NexConfig.getFactionConfig("ironshell").getStartFleetSet(NexFactionConfig.StartFleetType.SUPER.name()).getNumFleets(); i++) {
                    if ("eis_wolf_avaritia".equals(NexConfig.getFactionConfig("ironshell").getStartFleetSet(NexFactionConfig.StartFleetType.SUPER.name()).getFleet(i).get(0))) {break;}
                    if ((i) == NexConfig.getFactionConfig("ironshell").getStartFleetSet(NexFactionConfig.StartFleetType.SUPER.name()).getNumFleets()) {
                        List<String> wolfFleet = new ArrayList<String>();
                        wolfFleet.add("eis_wolf_avaritia");
                        wolfFleet.add("eis_wolf_avaritia");
                        wolfFleet.add("eis_wolf_avaritia");
			wolfFleet.add("eis_wolf_avaritia");
                        NexConfig.getFactionConfig("ironshell").getStartFleetSet(NexFactionConfig.StartFleetType.SUPER.name()).addFleet(wolfFleet);
                        break;
                    }
                }
            }
    }

    
    @Override
    public void onGameLoad(boolean newGame) {
        /*Whatever they should know should be from the Hegemony Auxiliary and XIV Blueprint respectively.
        for (String ship : Global.getSector().getFaction(Factions.HEGEMONY).getKnownShips()) {
            if (!Global.getSector().getFaction(IRONSTANDSETERNAL).knowsShip(ship)) {
                Global.getSector().getFaction(IRONSTANDSETERNAL).addKnownShip(ship, true);
            }
        } 
        for (String baseShip : Global.getSector().getFaction(Factions.HEGEMONY).getAlwaysKnownShips()) {
            if (!Global.getSector().getFaction(IRONSTANDSETERNAL).useWhenImportingShip(baseShip)) {
                Global.getSector().getFaction(IRONSTANDSETERNAL).addUseWhenImportingShip(baseShip);
            }
        }
        for (String fighter : Global.getSector().getFaction(Factions.HEGEMONY).getKnownFighters()) {
            if (!Global.getSector().getFaction(IRONSTANDSETERNAL).knowsFighter(fighter)) {
                Global.getSector().getFaction(IRONSTANDSETERNAL).addKnownFighter(fighter, true);
            }
        }
        for (String weapon : Global.getSector().getFaction(Factions.HEGEMONY).getKnownWeapons()) {
            if (!Global.getSector().getFaction(IRONSTANDSETERNAL).knowsWeapon(weapon)) {
               Global.getSector().getFaction(IRONSTANDSETERNAL).addKnownWeapon(weapon, true);
            }
        }*/
        eis_SpecialItemEffectsRepo.addItemEffectsToVanillaRepo();
        if (newGame) {
            if (IRONSTANDSETERNAL.equals(PlayerFactionStore.getPlayerFactionIdNGC())) {
                for (int i = 0; i < Global.getSector().getPlayerFleet().getFleetData().getOfficersCopy().size(); i++) {
                    for (int u = 0; u < Global.getSector().getPlayerFleet().getFleetData().getOfficersCopy().get(i).getPerson().getStats().getSkillsCopy().size(); u++) {
                        if (u==0) {Global.getSector().getPlayerFleet().getFleetData().getOfficersCopy().get(i).getPerson().getStats().setSkillLevel(Global.getSector().getPlayerFleet().getFleetData().getOfficersCopy().get(i).getPerson().getStats().getSkillsCopy().get(u).getSkill().getId(), 1);}
                        Global.getSector().getPlayerFleet().getFleetData().getOfficersCopy().get(i).getPerson().getStats().setSkillLevel(Global.getSector().getPlayerFleet().getFleetData().getOfficersCopy().get(i).getPerson().getStats().getSkillsCopy().get(u).getSkill().getId(), 0);
                    }
                    Global.getSector().getPlayerFleet().getFleetData().getOfficersCopy().get(i).getPerson().getStats().addXP(-Global.getSector().getPlayerFleet().getFleetData().getOfficersCopy().get(i).getPerson().getStats().getXP());
                    //Global.getSector().getPlayerFleet().getFleetData().getOfficersCopy().get(i).getPerson().getStats().setLevel(2);
                    Global.getSector().getPlayerFleet().getFleetData().getOfficersCopy().get(i).getPerson().getStats().setSkillLevel("eis_xiv", 1);
                }
                if ("eis_valorous".equals(Global.getSector().getPlayerFleet().getFlagship().getHullSpec().getBaseHullId())) { //This includes skinned Valorous.. surprisingly...
                    if (Global.getSettings().getMissionScore("eis_queens") > 0) {
                        Global.getSector().getPlayerFleet().getFlagship().setVariant(Global.getSettings().getVariant("eis_valorous_bond"), false, false);
                        Global.getSector().getPlayerFleet().getFlagship().getVariant().setSource(VariantSource.REFIT);
                    } else {
                        ShipVariantAPI PlayerVariant = Global.getSector().getPlayerFleet().getFlagship().getVariant();
                        PlayerVariant.removeSuppressedMod(HullMods.FRAGILE_SUBSYSTEMS);
                        PlayerVariant.addPermaMod(HullMods.FRAGILE_SUBSYSTEMS);
                        PlayerVariant.removeSuppressedMod(HullMods.FAULTY_GRID);
                        PlayerVariant.addPermaMod(HullMods.FAULTY_GRID);
                        PlayerVariant.removeSuppressedMod(HullMods.DEGRADED_ENGINES);
                        PlayerVariant.addPermaMod(HullMods.DEGRADED_ENGINES);
                        PlayerVariant.clearSlot("WS0002");
                        PlayerVariant.clearSlot("WS 001");
                        Global.getSector().getPlayerFleet().getFlagship().getVariant().setSource(VariantSource.REFIT);
                    }
                }
                if ("wolf_hegemony_Assault".equals(Global.getSector().getPlayerFleet().getFlagship().getVariant().getHullVariantId())) {
                    Global.getSector().getPlayerFleet().getFlagship().getVariant().addPermaMod("eis_aquila", true);
                    Global.getSector().getPlayerFleet().getFlagship().getVariant().addPermaMod("eis_avaritia", true);
                    Global.getSector().getPlayerFleet().getFlagship().getVariant().setSource(VariantSource.REFIT);
                }
                if (Global.getSettings().getMissionScore("eis_traitors") > 0 && "eis_indomitable_missile".equals(Global.getSector().getPlayerFleet().getFlagship().getVariant().getHullVariantId())) {
                    for (FleetMemberAPI membersWithFightersCopy : Global.getSector().getPlayerFleet().getMembersWithFightersCopy()) {
                        if ("eis_flagellator".equals(membersWithFightersCopy.getHullId())) {membersWithFightersCopy.getVariant().addPermaMod("eis_damperhull", true);membersWithFightersCopy.getVariant().setSource(VariantSource.REFIT);}
                    }
                    Global.getSector().getPlayerFleet().getFlagship().getVariant().addPermaMod("eis_damperhull", true);
                    Global.getSector().getPlayerFleet().getFlagship().getVariant().setSource(VariantSource.REFIT);
                }
            }
            if (haveSWP) {
                //{Global.getSector().getFaction(IRONSTANDSETERNAL).getKnownShips().add("swp_nautilus");} We really have no use for this.
                //{Global.getSector().getFaction(IRONSTANDSETERNAL).getKnownShips().add("swp_caliber");} Flagellator fills this role.
                if (Global.getSettings().getHullSpec("swp_striker") != null) {Global.getSector().getFaction(IRONSTANDSETERNAL).getKnownShips().add("swp_striker");}
                //{Global.getSector().getFaction(IRONSTANDSETERNAL).getKnownShips().add("swp_vindicator");} Champion and Nelson fills this role.
                //{Global.getSector().getFaction(IRONSTANDSETERNAL).getKnownShips().add("swp_punisher");} Indomitable fills this role.
                if (Global.getSettings().getHullSpec("swp_alastor") != null) {Global.getSector().getFaction(IRONSTANDSETERNAL).getKnownShips().add("swp_alastor");}
                if (Global.getSettings().getHullSpec("swp_archer") != null) {Global.getSector().getFaction(IRONSTANDSETERNAL).getKnownShips().add("swp_archer");}
                //{Global.getSector().getFaction(IRONSTANDSETERNAL).getKnownShips().add("swp_albatross");} Courageous fills this role.
                //{Global.getSector().getFaction(IRONSTANDSETERNAL).getKnownShips().add("swp_vulture");} We really have no use for this.
		//{Global.getSector().getFaction(IRONSTANDSETERNAL).getKnownShips().add("swp_liberator");} We really have no use for this.
                if (Global.getSettings().getHullSpec("swp_alastor_xiv") != null) {Global.getSector().getFaction(IRONSTANDSETERNAL).getKnownShips().add("swp_alastor_xiv");}
                if (Global.getSettings().getHullSpec("swp_conquest_xiv") != null) {Global.getSector().getFaction(IRONSTANDSETERNAL).getKnownShips().add("swp_conquest_xiv");}
                if (Global.getSettings().getHullSpec("swp_gryphon_xiv") != null) {Global.getSector().getFaction(IRONSTANDSETERNAL).getKnownShips().add("swp_gryphon_xiv");}
                if (Global.getSettings().getHullSpec("swp_hammerhead_xiv") != null) {Global.getSector().getFaction(IRONSTANDSETERNAL).getKnownShips().add("swp_hammerhead_xiv");}
                if (Global.getSettings().getHullSpec("swp_lasher_xiv") != null) {Global.getSector().getFaction(IRONSTANDSETERNAL).getKnownShips().add("swp_lasher_xiv");}
                if (Global.getSettings().getHullSpec("swp_sunder_xiv") != null) {Global.getSector().getFaction(IRONSTANDSETERNAL).getKnownShips().add("swp_sunder_xiv");}
                if (Global.getSettings().getWeaponSpec("swp_contender") != null) {Global.getSector().getFaction(IRONSTANDSETERNAL).getKnownWeapons().add("swp_contender");}
                if (Global.getSettings().getWeaponSpec("swp_plasmaflame") != null) {Global.getSector().getFaction(IRONSTANDSETERNAL).getKnownWeapons().add("swp_plasmaflame");}
                if (Global.getSettings().getWeaponSpec("swp_hornet") != null) {Global.getSector().getFaction(IRONSTANDSETERNAL).getKnownWeapons().add("swp_hornet");}
                if (Global.getSettings().getWeaponSpec("swp_tornado") != null) {Global.getSector().getFaction(IRONSTANDSETERNAL).getKnownWeapons().add("swp_tornado");}
                if (Global.getSettings().getWeaponSpec("swp_iontorpedo") != null) {Global.getSector().getFaction(IRONSTANDSETERNAL).getKnownWeapons().add("swp_iontorpedo");}
                if (Global.getSettings().getWeaponSpec("swp_ionblaster") != null) {Global.getSector().getFaction(IRONSTANDSETERNAL).getKnownWeapons().add("swp_ionblaster");}
                if (Global.getSettings().getWeaponSpec("swp_lightphaselance") != null) {Global.getSector().getFaction(IRONSTANDSETERNAL).getKnownWeapons().add("swp_lightphaselance");}
                //{Global.getSector().getFaction("ironsentinel").getKnownShips().add("swp_nautilus");} We really have no use for this.
                //{Global.getSector().getFaction("ironsentinel").getKnownShips().add("swp_caliber");} Flagellator fills this role.
                if (Global.getSettings().getHullSpec("swp_striker") != null) {Global.getSector().getFaction("ironsentinel").getKnownShips().add("swp_striker");}
                //{Global.getSector().getFaction("ironsentinel").getKnownShips().add("swp_vindicator");} Champion and Nelson fills this role.
                //{Global.getSector().getFaction("ironsentinel").getKnownShips().add("swp_punisher");} Indomitable fills this role.
                if (Global.getSettings().getHullSpec("swp_alastor") != null) {Global.getSector().getFaction("ironsentinel").getKnownShips().add("swp_alastor");}
                if (Global.getSettings().getHullSpec("swp_archer") != null) {Global.getSector().getFaction("ironsentinel").getKnownShips().add("swp_archer");}
                //{Global.getSector().getFaction("ironsentinel").getKnownShips().add("swp_albatross");} Courageous fills this role.
                //{Global.getSector().getFaction("ironsentinel").getKnownShips().add("swp_vulture");} We really have no use for this.
		//{Global.getSector().getFaction("ironsentinel").getKnownShips().add("swp_liberator");} We really have no use for this.
                if (Global.getSettings().getHullSpec("swp_alastor_xiv") != null) {Global.getSector().getFaction("ironsentinel").getKnownShips().add("swp_alastor_xiv");}
                if (Global.getSettings().getHullSpec("swp_conquest_xiv") != null) {Global.getSector().getFaction("ironsentinel").getKnownShips().add("swp_conquest_xiv");}
                if (Global.getSettings().getHullSpec("swp_gryphon_xiv") != null) {Global.getSector().getFaction("ironsentinel").getKnownShips().add("swp_gryphon_xiv");}
                if (Global.getSettings().getHullSpec("swp_hammerhead_xiv") != null) {Global.getSector().getFaction("ironsentinel").getKnownShips().add("swp_hammerhead_xiv");}
                if (Global.getSettings().getHullSpec("swp_lasher_xiv") != null) {Global.getSector().getFaction("ironsentinel").getKnownShips().add("swp_lasher_xiv");}
                if (Global.getSettings().getHullSpec("swp_sunder_xiv") != null) {Global.getSector().getFaction("ironsentinel").getKnownShips().add("swp_sunder_xiv");}
                if (Global.getSettings().getWeaponSpec("swp_contender") != null) {Global.getSector().getFaction("ironsentinel").getKnownWeapons().add("swp_contender");}
                if (Global.getSettings().getWeaponSpec("swp_plasmaflame") != null) {Global.getSector().getFaction("ironsentinel").getKnownWeapons().add("swp_plasmaflame");}
                if (Global.getSettings().getWeaponSpec("swp_hornet") != null) {Global.getSector().getFaction("ironsentinel").getKnownWeapons().add("swp_hornet");}
                if (Global.getSettings().getWeaponSpec("swp_tornado") != null) {Global.getSector().getFaction("ironsentinel").getKnownWeapons().add("swp_tornado");}
                if (Global.getSettings().getWeaponSpec("swp_iontorpedo") != null) {Global.getSector().getFaction("ironsentinel").getKnownWeapons().add("swp_iontorpedo");}
                if (Global.getSettings().getWeaponSpec("swp_ionblaster") != null) {Global.getSector().getFaction("ironsentinel").getKnownWeapons().add("swp_ionblaster");}
                if (Global.getSettings().getWeaponSpec("swp_lightphaselance") != null) {Global.getSector().getFaction("ironsentinel").getKnownWeapons().add("swp_lightphaselance");}
            }
            if (haveNia) {
                //{Global.getSector().getFaction(IRONSTANDSETERNAL).getKnownShips().add("tahlan_Bungalow");} Mora fills this role.
                //if (Global.getSettings().getHullSpec("tahlan_Flagellator") != null) {Global.getSector().getFaction(IRONSTANDSETERNAL).getKnownShips().add("tahlan_Flagellator");}
                //{Global.getSector().getFaction(IRONSTANDSETERNAL).getKnownShips().add("tahlan_Bronco");} Flagellator fills this role.
                if (Global.getSettings().getHullSpec("tahlan_blockhead") != null) {Global.getSector().getFaction(IRONSTANDSETERNAL).getKnownShips().add("tahlan_blockhead");}
                if (Global.getSettings().getHullSpec("tahlan_Bento") != null) {Global.getSector().getFaction(IRONSTANDSETERNAL).getKnownShips().add("tahlan_Bento");}
                //{Global.getSector().getFaction(IRONSTANDSETERNAL).getKnownShips().add("tahlan_Phoca");} Champion or Eagle fills this role.
		 if (Global.getSettings().getHullSpec("tahlan_Castigator") != null) {Global.getSector().getFaction(IRONSTANDSETERNAL).getKnownShips().add("tahlan_Castigator");}
                if (Global.getSettings().getHullSpec("tahlan_nelson") != null) {Global.getSector().getFaction(IRONSTANDSETERNAL).getKnownShips().add("tahlan_nelson");}
                if (Global.getSettings().getHullSpec("tahlan_nelson_xiv") != null) {Global.getSector().getFaction(IRONSTANDSETERNAL).getKnownShips().add("tahlan_nelson_xiv");}
		if (Global.getSettings().getHullSpec("tahlan_tower_xiv") != null) {Global.getSector().getFaction(IRONSTANDSETERNAL).getKnownShips().add("tahlan_tower_xiv");}
                //{Global.getSector().getFaction(IRONSTANDSETERNAL).getHullFrequency().replace("tahlan_nelson_xiv", 1.1f);}
                if (Global.getSettings().getWeaponSpec("tahlan_armiger") != null) {Global.getSector().getFaction(IRONSTANDSETERNAL).getKnownWeapons().add("tahlan_armiger");}
                if (Global.getSettings().getWeaponSpec("tahlan_efreet") != null) {Global.getSector().getFaction(IRONSTANDSETERNAL).getKnownWeapons().add("tahlan_efreet");}
                //{Global.getSector().getFaction("ironsentinel").getKnownShips().add("tahlan_Bungalow");} Mora fills this role.
                //if (Global.getSettings().getHullSpec("tahlan_Flagellator") != null) {Global.getSector().getFaction("ironsentinel").getKnownShips().add("tahlan_Flagellator");}
                //{Global.getSector().getFaction("ironsentinel").getKnownShips().add("tahlan_Bronco");} Flagellator fills this role.
                if (Global.getSettings().getHullSpec("tahlan_blockhead") != null) {Global.getSector().getFaction("ironsentinel").getKnownShips().add("tahlan_blockhead");}
                if (Global.getSettings().getHullSpec("tahlan_Bento") != null) {Global.getSector().getFaction("ironsentinel").getKnownShips().add("tahlan_Bento");}
                //{Global.getSector().getFaction("ironsentinel").getKnownShips().add("tahlan_Phoca");} Champion or Eagle fills this role.
		if (Global.getSettings().getHullSpec("tahlan_Castigator") != null) {Global.getSector().getFaction("ironsentinel").getKnownShips().add("tahlan_Castigator");}
                if (Global.getSettings().getHullSpec("tahlan_nelson") != null) {Global.getSector().getFaction("ironsentinel").getKnownShips().add("tahlan_nelson");}
                if (Global.getSettings().getHullSpec("tahlan_nelson_xiv") != null) {Global.getSector().getFaction("ironsentinel").getKnownShips().add("tahlan_nelson_xiv");}
		if (Global.getSettings().getHullSpec("tahlan_tower_xiv") != null) {Global.getSector().getFaction("ironsentinel").getKnownShips().add("tahlan_tower_xiv");}
                if (Global.getSettings().getWeaponSpec("tahlan_armiger") != null) {Global.getSector().getFaction("ironsentinel").getKnownWeapons().add("tahlan_armiger");}
                if (Global.getSettings().getWeaponSpec("tahlan_efreet") != null) {Global.getSector().getFaction("ironsentinel").getKnownWeapons().add("tahlan_efreet");}
            }
            if (hasTart) {
                //{Global.getSector().getFaction(IRONSTANDSETERNAL).getKnownShips().add("TADA_bully");} This ship is not a bully.
                //{Global.getSector().getFaction(IRONSTANDSETERNAL).getKnownShips().add("TADA_scalper");} Role feels weird.
                if (Global.getSettings().getHullSpec("TADA_tinnitus") != null) {Global.getSector().getFaction(IRONSTANDSETERNAL).getKnownShips().add("TADA_tinnitus");}
                if (Global.getSettings().getHullSpec("TADA_gunwall") != null) {Global.getSector().getFaction(IRONSTANDSETERNAL).getKnownShips().add("TADA_gunwall");}
                if (Global.getSettings().getHullSpec("TADA_tinnitus_xiv") != null) {Global.getSector().getFaction(IRONSTANDSETERNAL).getKnownShips().add("TADA_tinnitus_xiv");}
                if (Global.getSettings().getHullSpec("TADA_gunwall_XIV") != null) {Global.getSector().getFaction(IRONSTANDSETERNAL).getKnownShips().add("TADA_gunwall_XIV");}
                if (Global.getSettings().getWeaponSpec("TADA_dioscuri") != null) {Global.getSector().getFaction(IRONSTANDSETERNAL).getKnownWeapons().add("TADA_dioscuri");}
                //{Global.getSector().getFaction("ironsentinel").getKnownShips().add("TADA_bully");} This ship is not a bully.
                //{Global.getSector().getFaction("ironsentinel").getKnownShips().add("TADA_scalper");} Role feels weird.
                if (Global.getSettings().getHullSpec("TADA_tinnitus") != null) {Global.getSector().getFaction("ironsentinel").getKnownShips().add("TADA_tinnitus");}
                if (Global.getSettings().getHullSpec("TADA_gunwall") != null) {Global.getSector().getFaction("ironsentinel").getKnownShips().add("TADA_gunwall");}
                if (Global.getSettings().getHullSpec("TADA_tinnitus_xiv") != null) {Global.getSector().getFaction("ironsentinel").getKnownShips().add("TADA_tinnitus_xiv");}
                if (Global.getSettings().getHullSpec("TADA_gunwall_XIV") != null) {Global.getSector().getFaction("ironsentinel").getKnownShips().add("TADA_gunwall_XIV");}
                if (Global.getSettings().getWeaponSpec("TADA_dioscuri") != null) {Global.getSector().getFaction("ironsentinel").getKnownWeapons().add("TADA_dioscuri");}
            }
            Global.getSector().getFaction(IRONSTANDSETERNAL).clearShipRoleCache();
            Global.getSector().getFaction("ironsentinel").clearShipRoleCache();
        }
        /*Nexual Connection Code*/
        if (Global.getSector().getPlayerFaction().getMusicMap().get("market_friendly") == null && Global.getSector().getMemoryWithoutUpdate().get("$Nexual_Market") != null) {Global.getSector().getPlayerFaction().getMusicMap().put("market_friendly", Global.getSector().getMemoryWithoutUpdate().getString("$Nexual_Market"));}
        if (Global.getSector().getPlayerFaction().getMusicMap().get("encounter_friendly") == null && Global.getSector().getMemoryWithoutUpdate().get("$Nexual_Encounter") != null) {Global.getSector().getPlayerFaction().getMusicMap().put("encounter_friendly", Global.getSector().getMemoryWithoutUpdate().getString("$Nexual_Encounter"));}
        
        //For Random Sector stuff
        if (!SectorManager.getCorvusMode()) {
            Global.getSector().getFaction(IRONSTANDSETERNAL).addKnownHullMod("eis_avaritia");
            Global.getSector().getFaction(IRONSTANDSETERNAL).addKnownHullMod("eis_damperhull");
            Global.getSector().getFaction(IRONSTANDSETERNAL).addKnownHullMod("eis_justatip");
            Global.getSettings().getHullModSpec("eis_avaritia").setTier(3);
            Global.getSettings().getHullModSpec("eis_avaritia").setRarity(1f);
            Global.getSettings().getHullModSpec("eis_damperhull").setTier(3);
            Global.getSettings().getHullModSpec("eis_damperhull").setRarity(1f);
            Global.getSettings().getHullModSpec("eis_justatip").setTier(3);
            Global.getSettings().getHullModSpec("eis_justatip").setRarity(1f);
        }
        
        if (SectorManager.getCorvusMode()) {
            if (Global.getSector().getEntityById("hanan_pacha") != null && Global.getSector().getEntityById("hanan_pacha").getMarket() != null && !Global.getSector().getEntityById("hanan_pacha").getMarket().hasCondition(Conditions.IRRADIATED)) {Global.getSector().getEntityById("hanan_pacha").getMarket().addCondition(Conditions.IRRADIATED);}
            Global.getSector().getFaction(IRONSTANDSETERNAL).removeKnownHullMod("eis_avaritia");
            Global.getSector().getFaction(IRONSTANDSETERNAL).removeKnownHullMod("eis_damperhull");
            Global.getSector().getFaction(IRONSTANDSETERNAL).removeKnownHullMod("eis_justatip");
            Global.getSettings().getHullModSpec("eis_avaritia").setTier(6);
            Global.getSettings().getHullModSpec("eis_avaritia").setRarity(0f);
            Global.getSettings().getHullModSpec("eis_damperhull").setTier(6);
            Global.getSettings().getHullModSpec("eis_damperhull").setRarity(0f);
            Global.getSettings().getHullModSpec("eis_justatip").setTier(6);
            Global.getSettings().getHullModSpec("eis_justatip").setRarity(0f);
        }
        
        /*remove in 1.2*/if (SectorManager.getCorvusMode() && Global.getSector().getEntityById("eis_yami") != null && Global.getSector().getEntityById("eis_yami").getOrbitFocus() != Global.getSector().getEntityById("yama")) {
            Global.getSector().getEntityById("eis_yami").setCircularOrbit(Global.getSector().getEntityById("yama"), 0, 600f, 40f);
            if (Global.getSettings().getBoolean("GreaterHegemony")) {Alliance lol = AllianceManager.getFactionAlliance(IRONSTANDSETERNAL);
            lol.addPermaMember(IRONSTANDSETERNAL);
            lol.addPermaMember("hegemony");}
            if (Global.getSector().getEconomy().getMarket("eis_chitagupta") != null && IRONSTANDSETERNAL.equals(Global.getSector().getEconomy().getMarket("eis_chitagupta"))) {Global.getSector().getEconomy().getMarket("eis_chitagupta").getMemoryWithoutUpdate().set("$nex_colony_growth_limit", 6);
            Global.getSector().getEconomy().getMarket("eis_chitagupta").setImmigrationIncentivesOn(true);}
            if (Global.getSector().getEconomy().getMarket("eis_yami") != null && IRONSTANDSETERNAL.equals(Global.getSector().getEconomy().getMarket("eis_yami").getFactionId())) {Global.getSector().getEconomy().getMarket("eis_yami").getMemoryWithoutUpdate().set("$nex_colony_growth_limit", 5);
            Global.getSector().getEconomy().getMarket("eis_yami").setImmigrationIncentivesOn(true);}
        }
        if (SectorManager.getCorvusMode() && Global.getSector().getImportantPeople().getPerson("eiskimquy") != null && !Global.getSector().getImportantPeople().getPerson("eiskimquy").getMemoryWithoutUpdate().getBoolean("$EISDoneQuest1Success") && ((Global.getSector().getEntityById("hanan_pacha") != null && Global.getSector().getEntityById("hanan_pacha").getMarket().getFactionId().equals(IRONSTANDSETERNAL)) || (Global.getSector().getEntityById("udana_stations") != null && Global.getSector().getEntityById("udana_stations").getMarket().getFactionId().equals(IRONSTANDSETERNAL)))) {
            Global.getSector().getImportantPeople().getPerson("eiskimquy").getMemoryWithoutUpdate().set("$EISDoneQuest1Success", true);
        }
        //Substance Abuse compatibility
        if (Global.getSettings().getModManager().isModEnabled("alcoholism")) {
            Global.getSector().getFaction(IRONSTANDSETERNAL).getMemoryWithoutUpdate().set("$alcoholism_faction_alcohol_types", new String[]{"alcoholism_stout"});
            if (Global.getSettings().getIndustrySpec("alcohol_brewery") != null && Global.getSector().getEntityById("eis_chitagupta") != null && Global.getSector().getEntityById("eis_chitagupta").getMarket() != null && !Global.getSector().getEntityById("eis_chitagupta").getMarket().hasIndustry("alcohol_brewery")) {
                Global.getSector().getEntityById("eis_chitagupta").getMarket().addIndustry("alcohol_brewery");
                Global.getSector().getEntityById("eis_chitagupta").getMarket().getIndustry("alcohol_brewery").setSpecialItem(new SpecialItemData("alcoholism_stout_item", null));
            }
        }
        
        
        if (Global.getSector().getImportantPeople().getPerson("eiskimquy") != null) {
            if (Global.getSector().getImportantPeople().getPerson("eiskimquy").getMemoryWithoutUpdate().getBoolean("$eis_xiv_legion")) {
                Global.getSector().getFaction(IRONSTANDSETERNAL).getKnownShips().add("legion_xiv");Global.getSector().getFaction(IRONSTANDSETERNAL).getPriorityShips().add("legion_xiv");Global.getSector().getFaction("ironshell").getHullFrequency().put("legion_xiv", 1f);
                Global.getSector().getFaction("ironsentinel").getKnownShips().add("legion_xiv");Global.getSector().getFaction("ironsentinel").getPriorityShips().add("legion_xiv");Global.getSector().getFaction("ironsentinel").getHullFrequency().put("legion_xiv", 1f);
                if (Global.getSettings().getBoolean("GreaterHegemony")) {Global.getSector().getFaction("hegemony").getKnownShips().add("legion_xiv");Global.getSector().getFaction("hegemony").getPriorityShips().add("legion_xiv");Global.getSector().getFaction("hegemony").getHullFrequency().put("legion_xiv", 0.25f);}
            }
            if (haveNia && Global.getSettings().getHullSpec("tahlan_Castigator_xiv") != null && Global.getSector().getImportantPeople().getPerson("eiskimquy").getMemoryWithoutUpdate().getBoolean("$eis_xiv_castigator")) {
                Global.getSector().getFaction(IRONSTANDSETERNAL).getKnownShips().add("tahlan_Castigator_xiv");Global.getSector().getFaction("ironshell").getHullFrequency().put("tahlan_Castigator_xiv", 1f);
                Global.getSector().getFaction("ironsentinel").getKnownShips().add("tahlan_Castigator_xiv");Global.getSector().getFaction("ironsentinel").getPriorityShips().add("tahlan_Castigator_xiv");Global.getSector().getFaction("ironsentinel").getHullFrequency().put("tahlan_Castigator_xiv", 1f);
                if (Global.getSettings().getBoolean("GreaterHegemony")) {Global.getSector().getFaction("hegemony").getKnownShips().add("tahlan_Castigator_xiv");Global.getSector().getFaction("hegemony").getHullFrequency().put("tahlan_Castigator_xiv", 0.25f);}
                if (Global.getSettings().getDescription("tahlan_Castigator_xiv", Description.Type.SHIP) != null) {Global.getSettings().getDescription("tahlan_Castigator_xiv", Description.Type.SHIP).setText1(Global.getSettings().getString("eis_ironshell", "tahlan_Castigator_xiv_update"));}
            }
            Global.getSector().getFaction(IRONSTANDSETERNAL).clearShipRoleCache();
            Global.getSector().getFaction("ironsentinel").clearShipRoleCache();
            Global.getSector().getFaction("hegemony").clearShipRoleCache();
        }
        
            for (PersonMissionSpec mission: Global.getSettings().getAllMissionSpecs()) {
                if (!(mission.getTagsAny().contains("eis_celeste") || mission.getTagsAny().contains("eis_military")) && 
                        !(mission.getTagsAny().isEmpty() && mission.getTagsAll().isEmpty() && mission.getTagsNotAny().isEmpty()) 
                        && mission.getPersonId() == null
                        && !mission.getTagsNotAny().contains("ironshell")) {
                    mission.getTagsNotAny().add("ironshell");
                }
            }
            for (String illegal: Global.getSector().getFaction(Factions.HEGEMONY).getIllegalCommodities()) {
                if (!"hand_weapons".equals(illegal)) { //https://constitution.congress.gov/constitution/amendment-2/
                    Global.getSector().getFaction(IRONSTANDSETERNAL).makeCommodityIllegal(illegal);
                }
            }
            for (String hullMods : Global.getSector().getFaction(Factions.HEGEMONY).getKnownHullMods()) {
                if (!Global.getSector().getFaction(IRONSTANDSETERNAL).knowsHullMod(hullMods)) {
                    Global.getSector().getFaction(IRONSTANDSETERNAL).addKnownHullMod(hullMods);
                }
            }
            
            Global.getSector().addTransientListener(new MarketCheckTariffs());
            if (Global.getSettings().getBoolean("GreaterHegemony") && !DiplomacyManager.isRandomFactionRelationships()) {
                Global.getSector().getListenerManager().addListener(new MarketshipChange(), true);
            }
            
            Global.getSector().addTransientListener(new MarketCheckTariffs3());
            if (Global.getSector().getMemoryWithoutUpdate().get("$EIS_taxburden") == null) {
                Global.getSector().getMemoryWithoutUpdate().set("$EIS_taxburden", 0f);
                Global.getSector().getMemoryWithoutUpdate().set("$EIS_BMTax", 0f);
                Global.getSector().getMemoryWithoutUpdate().set("$EIS_OMTax", 0f);
                Global.getSector().getMemoryWithoutUpdate().set("$EIS_BMHISTax", 0f);
                Global.getSector().getMemoryWithoutUpdate().set("$EIS_PayrollTax", 0f);
                Global.getSector().getMemoryWithoutUpdate().set("$EIS_WealthTax", 0f);
                Global.getSector().getMemoryWithoutUpdate().set("$EIS_MiscTax", 0f);
                Global.getSector().getMemoryWithoutUpdate().set("$EIS_taxespaid", true, 45f); //What's this about April 15th?
            }
            if (Global.getSettings().getModManager().isModEnabled("capturecrew")) {Global.getSector().addTransientListener(new MarketCheckTariffs2());}
            if (haveNex && SectorManager.getCorvusMode()) {
                //Global.getSector().addTransientListener(new MarketCheckTariffs4());
                Global.getSector().getListenerManager().addListener(new Amongus(), true);
            }
            if (Global.getSettings().getBoolean("NoEISPlayer") && !(IRONSTANDSETERNAL.equals(PlayerFactionStore.getPlayerFactionId()) || "hegemony".equals(PlayerFactionStore.getPlayerFactionId()))) {
                /*Global.getSector().getFaction(Factions.HEGEMONY).getPortraits(FullName.Gender.FEMALE).remove("graphics/portraits/eis_elsa.png");
                Global.getSector().getFaction(Factions.HEGEMONY).getPortraits(FullName.Gender.FEMALE).remove("graphics/portraits/eis_judy.png");
                Global.getSector().getFaction(Factions.HEGEMONY).getPortraits(FullName.Gender.FEMALE).remove("graphics/portraits/eis_trinh.png");
                Global.getSector().getFaction(Factions.HEGEMONY).getPortraits(FullName.Gender.FEMALE).remove("graphics/portraits/eis_velma.png");*/
                Global.getSector().getPlayerFaction().getPortraits(FullName.Gender.MALE).remove("graphics/portraits/eis_hegelin.png");
                Global.getSector().getPlayerFaction().getPortraits(FullName.Gender.MALE).remove("graphics/portraits/eis_richard.png");
                Global.getSector().getPlayerFaction().getPortraits(FullName.Gender.FEMALE).remove("graphics/portraits/eis_audrey.png");
                Global.getSector().getPlayerFaction().getPortraits(FullName.Gender.FEMALE).remove("graphics/portraits/eis_elsa.png");
                Global.getSector().getPlayerFaction().getPortraits(FullName.Gender.FEMALE).remove("graphics/portraits/eis_hang.png");
                Global.getSector().getPlayerFaction().getPortraits(FullName.Gender.FEMALE).remove("graphics/portraits/eis_hegelina.png");
                Global.getSector().getPlayerFaction().getPortraits(FullName.Gender.FEMALE).remove("graphics/portraits/eis_hoa.png");
                Global.getSector().getPlayerFaction().getPortraits(FullName.Gender.FEMALE).remove("graphics/portraits/eis_hue.png");
                Global.getSector().getPlayerFaction().getPortraits(FullName.Gender.FEMALE).remove("graphics/portraits/eis_judy.png");
                Global.getSector().getPlayerFaction().getPortraits(FullName.Gender.FEMALE).remove("graphics/portraits/eis_ngoc.png");
		Global.getSector().getPlayerFaction().getPortraits(FullName.Gender.FEMALE).remove("graphics/portraits/eis_phuong.png");
                Global.getSector().getPlayerFaction().getPortraits(FullName.Gender.FEMALE).remove("graphics/portraits/eis_rebecca.png");
                Global.getSector().getPlayerFaction().getPortraits(FullName.Gender.FEMALE).remove("graphics/portraits/eis_ritah.png");
                Global.getSector().getPlayerFaction().getPortraits(FullName.Gender.FEMALE).remove("graphics/portraits/eis_thu.png");
                Global.getSector().getPlayerFaction().getPortraits(FullName.Gender.FEMALE).remove("graphics/portraits/eis_tina.png");
                Global.getSector().getPlayerFaction().getPortraits(FullName.Gender.FEMALE).remove("graphics/portraits/eis_trinh.png");
                Global.getSector().getPlayerFaction().getPortraits(FullName.Gender.FEMALE).remove("graphics/portraits/eis_vang.png");
                Global.getSector().getPlayerFaction().getPortraits(FullName.Gender.FEMALE).remove("graphics/portraits/eis_velma.png");
                Global.getSector().getPlayerFaction().getPortraits(FullName.Gender.FEMALE).remove("graphics/portraits/eis_xuan.png");
            }
    }
    //Phased out for Better Colonies's SP improvement system.
    /*public static class MarketCheckTariffs4 extends BaseCampaignEventListener {
        MarketCheckTariffs4() {
            super(false);
        }
        
        @Override
        public void reportEconomyMonthEnd() {
            if (Global.getSector().getEconomy().getMarket("eis_chitagupta") != null && IRONSTANDSETERNAL.equals(Global.getSector().getEconomy().getMarket("eis_chitagupta").getFactionId())) {
                if (MathUtils.getRandomNumberInRange(1, 12) <= 2) {
                    for (Industry industry : Global.getSector().getEconomy().getMarket("eis_chitagupta").getIndustries()) {
                        if (industry.isImproved()) continue;
                        if (industry.canImprove() && industry.isFunctional() && !industry.isUpgrading() && !industry.isHidden()) {industry.setImproved(true);break;}
                    }
                }
            }
        }
    }*/
    
    public static class MarketCheckTariffs3 extends BaseCampaignEventListener {
        MarketCheckTariffs3() {
            super(false);
        }
        
        @Override
        public void reportShownInteractionDialog (InteractionDialogAPI dialog) {
            if (dialog != null && dialog.getInteractionTarget() != null && dialog.getInteractionTarget().getMemoryWithoutUpdate() != null) {
                if (dialog.getInteractionTarget().getMemoryWithoutUpdate().getBoolean("$EIS_YKWYD")) {
                    dialog.getOptionPanel().setEnabled(OptionId.CLEAN_DISENGAGE, false);
                    dialog.getOptionPanel().setTooltip(OptionId.CLEAN_DISENGAGE, Global.getSettings().getString("eis_ironshell", "EISStoryPointForceEngage"));
                    dialog.getOptionPanel().setTooltipHighlightColors(OptionId.CLEAN_DISENGAGE, Misc.getStoryOptionColor());
                    dialog.getOptionPanel().setTooltipHighlights(OptionId.CLEAN_DISENGAGE, Global.getSettings().getString("eis_ironshell", "EISStoryPointForceEngageHighlight"));
                }
            }
        }
    }

    private static class MarketCheckTariffs2 extends BaseCampaignEventListener {
        private MarketCheckTariffs2() {
            super(false);
        }
        
        @Override
        public void reportShownInteractionDialog (InteractionDialogAPI dialog) {
            if (dialog.getPlugin() instanceof CaptiveInteractionDialogPlugin) {
                for (EveryFrameScript script : Global.getSector().getScripts()) {
                    if (script instanceof LootAddScript) {
                        List<PersonAPI> Coolscript = ((LootAddScript) script).captiveOfficers;
                        for (int i=0;i<Coolscript.size();i++) {
                            if (Coolscript.contains(Global.getSector().getImportantPeople().getPerson("eiskimquy"))) {Coolscript.remove(Global.getSector().getImportantPeople().getPerson("eiskimquy"));}
                            if (Coolscript.contains(Global.getSector().getImportantPeople().getPerson("eisdarren"))) {Coolscript.remove(Global.getSector().getImportantPeople().getPerson("eisdarren"));}
                            if (Coolscript.contains(Global.getSector().getImportantPeople().getPerson("eisceleste"))) {Coolscript.remove(Global.getSector().getImportantPeople().getPerson("eisceleste"));}
                            if (Coolscript.contains(Global.getSector().getImportantPeople().getPerson("eissneed"))) {Coolscript.remove(Global.getSector().getImportantPeople().getPerson("eissneed"));}
                            if (Coolscript.contains(Global.getSector().getImportantPeople().getPerson("eisava"))) {Coolscript.remove(Global.getSector().getImportantPeople().getPerson("eisava"));}
                        }
                    } 
                }
            }
        }
    }
    
    private static class MarketCheckTariffs extends BaseCampaignEventListener {
        private MarketCheckTariffs() {
            super(false);
        }

        @Override
        public void reportPlayerMarketTransaction(PlayerMarketTransaction transaction) {
           if (Global.getSector().getMemoryWithoutUpdate().getFloat("$EIS_taxburden") <= 40000000 && transaction.getSubmarket().getPlugin().isParticipatesInEconomy()) {
               if (transaction.getSubmarket().getSpecId().equals(Submarkets.GENERIC_MILITARY)|| transaction.getSubmarket().getSpecId().equals(Submarkets.SUBMARKET_OPEN)) {
                   Global.getSector().getMemoryWithoutUpdate().set("$EIS_taxburden", Global.getSector().getMemoryWithoutUpdate().getFloat("$EIS_taxburden")-(Math.abs(transaction.getCreditValue()*0.04f*(SectorManager.getManager().isHardMode() ? 0.5f : 1f)*transaction.getSubmarket().getTariff())));
                   Global.getSector().getMemoryWithoutUpdate().set("$EIS_OMTax", Global.getSector().getMemoryWithoutUpdate().getFloat("$EIS_OMTax")-(Math.abs(transaction.getCreditValue()*0.04f*(SectorManager.getManager().isHardMode() ? 0.5f : 1f)*transaction.getSubmarket().getTariff())));
               }
               if (transaction.getSubmarket().getPlugin().isBlackMarket() || transaction.getSubmarket().getSpecId().equals(Submarkets.SUBMARKET_BLACK)) {
                   if (transaction.getMarket().hasCondition("nex_rebellion_condition") && ("hegemony".equals(RebellionIntel.getOngoingEvent(transaction.getMarket()).getRebelFaction().getId()) || IRONSTANDSETERNAL.equals(RebellionIntel.getOngoingEvent(transaction.getMarket()).getRebelFaction().getId()))) {
                            Global.getSector().getMemoryWithoutUpdate().set("$EIS_taxburden", Global.getSector().getMemoryWithoutUpdate().getFloat("$EIS_taxburden")-(Math.abs(transaction.getCreditValue()*0.08f*(SectorManager.getManager().isHardMode() ? 0.5f : 1f))));
                            Global.getSector().getMemoryWithoutUpdate().set("$EIS_BMHISTax", Global.getSector().getMemoryWithoutUpdate().getFloat("$EIS_BMHISTax")-(Math.abs(transaction.getCreditValue()*0.08f*(SectorManager.getManager().isHardMode() ? 0.5f : 1f))));
                    } else {
                        Global.getSector().getMemoryWithoutUpdate().set("$EIS_taxburden", Global.getSector().getMemoryWithoutUpdate().getFloat("$EIS_taxburden")+(Math.abs(transaction.getCreditValue()*0.1f)));
                        Global.getSector().getMemoryWithoutUpdate().set("$EIS_BMTax", Global.getSector().getMemoryWithoutUpdate().getFloat("$EIS_BMTax")+(Math.abs(transaction.getCreditValue()*0.1f)));
                   }
               }
           }
        }
        
        @Override
	public void reportEconomyTick(int iterIndex) { //Calculate all the taxation
            if (Global.getSector().getMemoryWithoutUpdate().getFloat("$EIS_taxburden") <= 40000000) {
                float f = 1f / Global.getSettings().getFloat("economyIterPerMonth");
                CampaignFleetAPI playerFleet = Global.getSector().getPlayerFleet();
                float crewCost = playerFleet.getCargo().getCrew() * Global.getSettings().getInt("crewSalary") * 0.1f * f;
                float marineCost = playerFleet.getCargo().getMarines() * Global.getSettings().getInt("marineSalary") * f;
                float officersalary = 0;
                float adminsalary = 0;
                for (OfficerDataAPI od : playerFleet.getFleetData().getOfficersCopy()) {
                    float salary = Misc.getOfficerSalary(od.getPerson());
                    if (salary <= 0) continue;
                    officersalary += salary * 0.05f * f;
                }
                for (AdminData data : Global.getSector().getCharacterData().getAdmins()) {
                    float salary2 = Misc.getAdminSalary(data.getPerson());
                    if (salary2 <= 0) continue;
                    adminsalary += salary2 * 0.1f * f;
                }
                float taxrefund = crewCost+marineCost+officersalary+adminsalary*(SectorManager.getManager().isHardMode() ? 0.5f : 1f);
                if (Global.getSector().getPlayerFleet().getCargo().getCredits().get() >= (SectorManager.getManager().isHardMode() ? 2500000f : 5000000f) && MathUtils.getRandomNumberInRange(1, 5) <= 3) {
                    float wealthcredit = 0.000154f*Math.round(Global.getSector().getPlayerFleet().getCargo().getCredits().get())*f;
                    Global.getSector().getMemoryWithoutUpdate().set("$EIS_taxburden", Global.getSector().getMemoryWithoutUpdate().getFloat("$EIS_taxburden")+wealthcredit);
                    Global.getSector().getMemoryWithoutUpdate().set("$EIS_WealthTax", Global.getSector().getMemoryWithoutUpdate().getFloat("$EIS_WealthTax")+wealthcredit);
                    if ("hegemony".equals(Misc.getCommissionFactionId()) || "ironshell".equals(Misc.getCommissionFactionId()) || AllianceManager.getPlayerAlliance(true) == AllianceManager.getFactionAlliance("ironshell")) {
                        if (Math.abs(Global.getSector().getMemoryWithoutUpdate().getFloat("$EIS_PayrollTax")) <= Global.getSector().getMemoryWithoutUpdate().getFloat("$EIS_WealthTax")+(SectorManager.getManager().isHardMode() ? 25000f : 50000f)) {
                            Global.getSector().getMemoryWithoutUpdate().set("$EIS_taxburden", Global.getSector().getMemoryWithoutUpdate().getFloat("$EIS_taxburden")-taxrefund);
                            Global.getSector().getMemoryWithoutUpdate().set("$EIS_PayrollTax", Global.getSector().getMemoryWithoutUpdate().getFloat("$EIS_PayrollTax")-taxrefund);
                        }
                    }
                } else {
                    if ("hegemony".equals(Misc.getCommissionFactionId()) || "ironshell".equals(Misc.getCommissionFactionId()) || AllianceManager.getPlayerAlliance(true) == AllianceManager.getFactionAlliance("ironshell")) {
                        if (Math.abs(Global.getSector().getMemoryWithoutUpdate().getFloat("$EIS_PayrollTax")) <= Global.getSector().getMemoryWithoutUpdate().getFloat("$EIS_WealthTax")+(SectorManager.getManager().isHardMode() ? 25000f : 50000f)) {
                            Global.getSector().getMemoryWithoutUpdate().set("$EIS_taxburden", Global.getSector().getMemoryWithoutUpdate().getFloat("$EIS_taxburden")-taxrefund);
                            Global.getSector().getMemoryWithoutUpdate().set("$EIS_PayrollTax", Global.getSector().getMemoryWithoutUpdate().getFloat("$EIS_PayrollTax")-taxrefund);
                        }
                    }
                }
            }
            /*So we detect tax evasion?
            No...
                Oh...
            Maybe?*/
        }
        
        @Override
        public void reportPlayerReputationChange(String factionId, float delta) {
            if (("hegemony".equals(factionId) || IRONSTANDSETERNAL.equals(factionId)) && (Misc.getCommissionFactionId() != null && (Misc.getCommissionFactionId().equals(IRONSTANDSETERNAL) || Misc.getCommissionFactionId().equals("hegemony"))) && !DiplomacyManager.isRandomFactionRelationships()) {
                if ((Global.getSector().getFaction("hegemony").getRelToPlayer().getRel() <= 0.251f || Global.getSector().getFaction("ironshell").getRelToPlayer().getRel() <= 0.251f) && delta <= -0.36f) {
                    List<IntelInfoPlugin> intels =  Global.getSector().getIntelManager().getIntel(FactionCommissionIntel.class);
                    for (IntelInfoPlugin intel : intels) {
                        FactionCommissionIntel intelpenis = (FactionCommissionIntel) intel;
                        if (!intelpenis.isEnding()) {
                            /*intelpenis.setMissionResult(new MissionResult(-1, null));
                            intelpenis.setMissionState(MissionState.COMPLETED);
                            intelpenis.endMission();
                            intelpenis.sendUpdateIfPlayerHasIntel(null, false);*/
                            if (Global.getSector().getCampaignUI().getCurrentInteractionDialog() != null || Global.getSector().getCampaignUI().isShowingDialog()) {
                                Global.getSector().getCampaignUI().getCurrentInteractionDialog().getTextPanel().addPara(Global.getSettings().getString("eis_ironshell", "eis_greaterhegemony_annul"), Misc.getNegativeHighlightColor());
                                Global.getSector().getIntelManager().addIntelToTextPanel(intelpenis, Global.getSector().getCampaignUI().getCurrentInteractionDialog().getTextPanel());
                            }
                            Global.getSector().getFaction("hegemony").adjustRelationship("player", delta, RepLevel.VENGEFUL);
                            Global.getSector().getFaction(IRONSTANDSETERNAL).adjustRelationship("player", delta, RepLevel.VENGEFUL);
                            if (delta < 0f) {
                                Global.getSector().getFaction(IRONSTANDSETERNAL).adjustRelationship("hegemony", -delta);
                                Global.getSector().getFaction("hegemony").adjustRelationship(IRONSTANDSETERNAL, -delta);
                            }
                            
                            if (AllianceManager.getFactionAlliance(IRONSTANDSETERNAL) == null && AllianceManager.getFactionAlliance("hegemony") == null) {
                                if (Global.getSettings().getBoolean("GreaterHegemony")) {
                                    Alliance lol = AllianceManager.createAlliance(IRONSTANDSETERNAL, Factions.HEGEMONY, AllianceManager.getBestAlignment(IRONSTANDSETERNAL, Factions.HEGEMONY));
                                    lol.setName(Global.getSettings().getString("eis_ironshell", "eis_greaterhegemony"));
                                }
                            }
                        }                          
                    }
                }
            }
            if (("hegemony".equals(factionId) || IRONSTANDSETERNAL.equals(factionId)) && !(Misc.getCommissionFactionId() != null && (Misc.getCommissionFactionId().equals(IRONSTANDSETERNAL) || Misc.getCommissionFactionId().equals("hegemony"))) && !DiplomacyManager.isRandomFactionRelationships()) {
                if (AllianceManager.getFactionAlliance(IRONSTANDSETERNAL) == AllianceManager.getFactionAlliance("hegemony") && delta < 0) {
                    if (Global.getSector().getFaction("hegemony").getRelToPlayer().getRel() <= -0.5f) {
                        //Global.getSector().getFaction(IRONSTANDSETERNAL).setRelationship("player", Global.getSector().getFaction("hegemony").getRelToPlayer().getRel());
                        Global.getSector().getFaction(IRONSTANDSETERNAL).adjustRelationship("player", delta, RepLevel.HOSTILE);
                    } else if (Global.getSector().getFaction(IRONSTANDSETERNAL).getRelToPlayer().getRel() <= -0.5f) {
                        //Global.getSector().getFaction("hegemony").setRelationship("player", Global.getSector().getFaction(IRONSTANDSETERNAL).getRelToPlayer().getRel());
                        Global.getSector().getFaction(Factions.HEGEMONY).adjustRelationship("player", delta, RepLevel.HOSTILE);
                    }
                }
            }
        }
    }
    

    @Override
    public void onNewGameAfterEconomyLoad() {
        if (Global.getSector().getStarSystem("Naraka") != null) {
            Global.getSector().getStarSystem("Naraka").setBackgroundTextureFilename("graphics/backgrounds/eis_selkieperfection.jpg");
        }
    }
    
    @Override
    public void onNewGameAfterTimePass() {
        if (!DiplomacyManager.isRandomFactionRelationships() && Global.getSector().getPlayerFaction().getRelationship(IRONSTANDSETERNAL) >= -0.05f && Global.getSector().getPlayerFaction().getRelationship(IRONSTANDSETERNAL) <= 0.05f) {
            Global.getSector().getFaction(IRONSTANDSETERNAL).setRelationship(Factions.PLAYER, Global.getSector().getPlayerFaction().getRelationship(Factions.HEGEMONY));
            if (Misc.getCommissionFactionId() != null && !"player".equals(Misc.getCommissionFactionId())) {Global.getSector().getFaction(IRONSTANDSETERNAL).setRelationship(Misc.getCommissionFactionId(), Global.getSector().getPlayerFaction().getRelationship(Factions.HEGEMONY));}
        }
    }
}