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
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.campaign.comm.IntelInfoPlugin;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.characters.AdminData;
import com.fs.starfarer.api.characters.FullName;
import com.fs.starfarer.api.characters.OfficerDataAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.impl.campaign.FleetInteractionDialogPluginImpl.OptionId;
import com.fs.starfarer.api.impl.campaign.econ.impl.eis_SpecialItemEffectsRepo;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.HullMods;
import com.fs.starfarer.api.impl.campaign.ids.Submarkets;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.impl.campaign.intel.BaseMissionIntel.MissionResult;
import com.fs.starfarer.api.impl.campaign.intel.BaseMissionIntel.MissionState;
import com.fs.starfarer.api.impl.campaign.intel.FactionCommissionIntel;
import com.fs.starfarer.api.impl.campaign.shared.SharedData;
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
import java.io.IOException;
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
                    SectorEntityToken relay = system.addCustomEntity(null, "Navigation Relay", "nav_buoy", "hegemony");
                    relay.setCircularOrbitPointingDown(system.getEntityById("naraka"), 0, 2750, 160);
                }
            }
            SectorEntityToken lollmao2 = system.addCustomEntity(null, "Sensor Array", "sensor_array", "hegemony");
            lollmao2.setCircularOrbitPointingDown(system.getEntityById("naraka"), 90 + 60, 3000, 100);
            LocationAPI hyper = Global.getSector().getHyperspace();
            String amazing = "mamamia";
            for (SectorEntityToken e : hyper.getAllEntities()) {if (e.getLocation().x > 10800 && e.getLocation().x < 11200 && e.getLocation().y > -7200 && e.getLocation().y < -6800) {amazing = e.getId();}}Global.getSector().getHyperspace().removeEntity(Global.getSector().getEntityById(amazing));
            system.removeEntity(Global.getSector().getEntityById("yami"));
            system.removeEntity(Global.getSector().getEntityById("chitagupta"));
            PlanetAPI naraka_b = system.addPlanet("eis_yami", Global.getSector().getEntityById("yama"), "Yami", "cryovolcanic", 0, 60, 600, 40); //formerly 450 and 650
            naraka_b.setCustomDescriptionId("planet_yami_ironshell");//naraka_b.autoUpdateHyperLocationBasedOnInSystemEntityAtRadius(system.getStar(), 0);
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
            if (Global.getSettings().getHullSpec("dominator_xiv") != null) {Global.getSettings().getHullSpec("dominator_xiv").addTag("eis_bp");}if (Global.getSettings().getHullSpec("enforcer_xiv") != null) {Global.getSettings().getHullSpec("enforcer_xiv").addTag("eis_bp");}if (Global.getSettings().getHullSpec("falcon_xiv") != null) {Global.getSettings().getHullSpec("falcon_xiv").addTag("eis_bp");}
            if (Global.getSettings().getHullSpec("onslaught_xiv") != null) {Global.getSettings().getHullSpec("onslaught_xiv").addTag("eis_bp");}
            if (hasTart) {
                if (Global.getSettings().getHullSpec("TADA_tinnitus_xiv") != null) {Global.getSettings().getHullSpec("TADA_tinnitus_xiv").addTag("eis_bp");}
                if (Global.getSettings().getHullSpec("TADA_gunwall_XIV") != null) {Global.getSettings().getHullSpec("TADA_gunwall_XIV").addTag("eis_bp");}
            }
            if (haveNia) {
                if (Global.getSettings().getHullSpec("tahlan_nelson_xiv") != null) {Global.getSettings().getHullSpec("tahlan_nelson_xiv").addTag("eis_bp");}
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
                if (haveSWP) {
                    if (Global.getSettings().getWeaponSpec("swp_contender") != null) {
                        StandardVVariant.setNumFluxCapacitors(StandardVVariant.getNumFluxCapacitors()+4);
                        StandardVVariant.clearSlot("WS0001");
                        StandardVVariant.addWeapon("WS0001", "swp_contender"); //from LAG
                        StandardVVariant.clearSlot("WS0002");
                        StandardVVariant.addWeapon("WS0002", "swp_contender"); //from LAG
                    }
                }
                if (haveNia) {
                    if (Global.getSettings().getWeaponSpec("tahlan_armiger") != null) {
                        StandardVVariant.setNumFluxCapacitors(StandardVVariant.getNumFluxCapacitors()-6);
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
                        StandardTVariant.setNumFluxCapacitors(StandardTVariant.getNumFluxCapacitors()-1);
                        StandardTVariant.clearSlot("WS0020");
                        StandardTVariant.addWeapon("WS0020", "tahlan_efreet"); //from gauss
                    }
                    if (Global.getSettings().getWeaponSpec("tahlan_armiger") != null) {
                        StandardTVariant.setNumFluxCapacitors(StandardTVariant.getNumFluxCapacitors()-1);
                        StandardTVariant.clearSlot("WS0021");
                        StandardTVariant.addWeapon("WS0021", "tahlan_armiger"); //from gauss
                    }
                } 
            }
            if (Global.getSettings().getVariant("eis_skyrend_standard") != null) {
                ShipVariantAPI StandardSVariant = Global.getSettings().getVariant("eis_skyrend_standard");
                if (haveSWP) {
                    if (Global.getSettings().getWeaponSpec("swp_lightphaselance") != null) {
                        StandardSVariant.setNumFluxCapacitors(StandardSVariant.getNumFluxCapacitors()-12); //from 21 to 9
                        StandardSVariant.clearSlot("WS0009");
                        StandardSVariant.addWeapon("WS0009", "swp_lightphaselance"); //from tac laser
                        StandardSVariant.clearSlot("WS0010");
                        StandardSVariant.addWeapon("WS0010", "swp_lightphaselance"); //from tac laser
                        StandardSVariant.clearSlot("WS0011");
                        StandardSVariant.addWeapon("WS0011", "swp_lightphaselance"); //from tac laser
                        StandardSVariant.clearSlot("WS0012");
                        StandardSVariant.addWeapon("WS0012", "swp_lightphaselance"); //from tac laser
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
    }

    
    @Override
    public void onGameLoad(boolean newGame) {
        eis_SpecialItemEffectsRepo.addItemEffectsToVanillaRepo();
        if (newGame) {
            if (IRONSTANDSETERNAL.equals(PlayerFactionStore.getPlayerFactionIdNGC())) {
                for (int i = 0; i < Global.getSector().getPlayerFleet().getFleetData().getOfficersCopy().size(); i++) {
                    for (int u = 0; u < Global.getSector().getPlayerFleet().getFleetData().getOfficersCopy().get(i).getPerson().getStats().getSkillsCopy().size(); u++) {
                        if (u==0) {Global.getSector().getPlayerFleet().getFleetData().getOfficersCopy().get(i).getPerson().getStats().setSkillLevel(Global.getSector().getPlayerFleet().getFleetData().getOfficersCopy().get(i).getPerson().getStats().getSkillsCopy().get(u).getSkill().getId(), 1);}
                        Global.getSector().getPlayerFleet().getFleetData().getOfficersCopy().get(i).getPerson().getStats().setSkillLevel(Global.getSector().getPlayerFleet().getFleetData().getOfficersCopy().get(i).getPerson().getStats().getSkillsCopy().get(u).getSkill().getId(), 0);
                    }
                    Global.getSector().getPlayerFleet().getFleetData().getOfficersCopy().get(i).getPerson().getStats().addXP(-Global.getSector().getPlayerFleet().getFleetData().getOfficersCopy().get(i).getPerson().getStats().getXP());
                    Global.getSector().getPlayerFleet().getFleetData().getOfficersCopy().get(i).getPerson().getStats().setLevel(2);
                    Misc.setMentored(Global.getSector().getPlayerFleet().getFleetData().getOfficersCopy().get(i).getPerson(), true);
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
                if ("wolf_hegemony".equals(Global.getSector().getPlayerFleet().getFlagship().getHullSpec().getHullId())) {
                    Global.getSector().getPlayerFleet().getFlagship().getVariant().addPermaMod("eis_avaritia", true);
                    Global.getSector().getPlayerFleet().getFlagship().getVariant().addPermaMod(HullMods.FLUXBREAKERS, true);
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
                if (Global.getSettings().getHullSpec("tahlan_Flagellator") != null) {Global.getSector().getFaction(IRONSTANDSETERNAL).getKnownShips().add("tahlan_Flagellator");}
                //{Global.getSector().getFaction(IRONSTANDSETERNAL).getKnownShips().add("tahlan_Bronco");} Flagellator fills this role.
                if (Global.getSettings().getHullSpec("tahlan_blockhead") != null) {Global.getSector().getFaction(IRONSTANDSETERNAL).getKnownShips().add("tahlan_blockhead");}
                if (Global.getSettings().getHullSpec("tahlan_Bento") != null) {Global.getSector().getFaction(IRONSTANDSETERNAL).getKnownShips().add("tahlan_Bento");}
                //{Global.getSector().getFaction(IRONSTANDSETERNAL).getKnownShips().add("tahlan_Phoca");} Champion or Eagle fills this role.
				{Global.getSector().getFaction(IRONSTANDSETERNAL).getKnownShips().add("tahlan_Castigator");}
                if (Global.getSettings().getHullSpec("tahlan_nelson") != null) {Global.getSector().getFaction(IRONSTANDSETERNAL).getKnownShips().add("tahlan_nelson");}
                if (Global.getSettings().getHullSpec("tahlan_nelson_xiv") != null) {Global.getSector().getFaction(IRONSTANDSETERNAL).getKnownShips().add("tahlan_nelson_xiv");}
                //{Global.getSector().getFaction(IRONSTANDSETERNAL).getHullFrequency().replace("tahlan_nelson_xiv", 1.1f);}
                if (Global.getSettings().getWeaponSpec("tahlan_armiger") != null) {Global.getSector().getFaction(IRONSTANDSETERNAL).getKnownWeapons().add("tahlan_armiger");}
                if (Global.getSettings().getWeaponSpec("tahlan_efreet") != null) {Global.getSector().getFaction(IRONSTANDSETERNAL).getKnownWeapons().add("tahlan_efreet");}
                //{Global.getSector().getFaction("ironsentinel").getKnownShips().add("tahlan_Bungalow");} Mora fills this role.
                if (Global.getSettings().getHullSpec("tahlan_Flagellator") != null) {Global.getSector().getFaction("ironsentinel").getKnownShips().add("tahlan_Flagellator");}
                //{Global.getSector().getFaction("ironsentinel").getKnownShips().add("tahlan_Bronco");} Flagellator fills this role.
                if (Global.getSettings().getHullSpec("tahlan_blockhead") != null) {Global.getSector().getFaction("ironsentinel").getKnownShips().add("tahlan_blockhead");}
                if (Global.getSettings().getHullSpec("tahlan_Bento") != null) {Global.getSector().getFaction("ironsentinel").getKnownShips().add("tahlan_Bento");}
                //{Global.getSector().getFaction("ironsentinel").getKnownShips().add("tahlan_Phoca");} Champion or Eagle fills this role.
		if (Global.getSettings().getHullSpec("tahlan_Castigator") != null) {Global.getSector().getFaction("ironsentinel").getKnownShips().add("tahlan_Castigator");}
                if (Global.getSettings().getHullSpec("tahlan_nelson") != null) {Global.getSector().getFaction("ironsentinel").getKnownShips().add("tahlan_nelson");}
                if (Global.getSettings().getHullSpec("tahlan_nelson_xiv") != null) {Global.getSector().getFaction("ironsentinel").getKnownShips().add("tahlan_nelson_xiv");}
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
        /*remove in 1.2*/if (SectorManager.getCorvusMode() && Global.getSector().getEntityById("eis_yami") != null && Global.getSector().getEntityById("eis_yami").getOrbitFocus() != Global.getSector().getEntityById("eis_yama")) {
            Global.getSector().getEntityById("eis_yami").setCircularOrbit(Global.getSector().getEntityById("yama"), 0, 600f, 40f);
            Alliance lol = AllianceManager.getFactionAlliance(IRONSTANDSETERNAL);
            lol.addPermaMember(IRONSTANDSETERNAL);
            lol.addPermaMember("hegemony");
            Global.getSector().getEconomy().getMarket("eis_chitagupta").getMemoryWithoutUpdate().set("$nex_colony_growth_limit", 6);
            Global.getSector().getEconomy().getMarket("eis_chitagupta").setImmigrationIncentivesOn(true);
            Global.getSector().getEconomy().getMarket("eis_yami").getMemoryWithoutUpdate().set("$nex_colony_growth_limit", 5);
            Global.getSector().getEconomy().getMarket("eis_yami").setImmigrationIncentivesOn(true);
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
            if (MarketshipChange.getInstance() == null) {
                Global.getSector().getListenerManager().addListener(new MarketshipChange().registerInstance(), true); 
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
                Global.getSector().getMemoryWithoutUpdate().set("$EIS_taxespaid", true);
                Global.getSector().getMemoryWithoutUpdate().expire("$EIS_taxespaid", 45f);//What's this about April 15th?
            }
            if (Global.getSettings().getModManager().isModEnabled("capturecrew")) {Global.getSector().addTransientListener(new MarketCheckTariffs2());}
            if (haveNex && SectorManager.getCorvusMode()) {
                Global.getSector().addTransientListener(new MarketCheckTariffs4());
                if (Amongus.getInstance() == null) {Global.getSector().getListenerManager().addListener(new Amongus().registerInstance(), true);}
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
                Global.getSector().getPlayerFaction().getPortraits(FullName.Gender.FEMALE).remove("graphics/portraits/eis_judy.png");
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
    
    public static class MarketCheckTariffs4 extends BaseCampaignEventListener {
        MarketCheckTariffs4() {
            super(false);
        }
        
        @Override
        public void reportEconomyMonthEnd() {
            if (Global.getSector().getEconomy().getMarket("eis_chitagupta") != null && IRONSTANDSETERNAL.equals(Global.getSector().getEconomy().getMarket("eis_chitagupta").getFactionId())) {
                if (Math.floorDiv(Global.getSector().getClock().getMonth(), 3) == 1) {
                    for (Industry industry : Global.getSector().getEconomy().getMarket("eis_chitagupta").getIndustries()) {
                        if (industry.isImproved()) continue;
                        if (industry.canImprove() && industry.isFunctional()) {industry.setImproved(true);break;}
                    }
                }
            }
        }
    }
    
    public static class MarketCheckTariffs3 extends BaseCampaignEventListener {
        MarketCheckTariffs3() {
            super(false);
        }
        
        @Override
        public void reportShownInteractionDialog (InteractionDialogAPI dialog) {
            if (dialog != null && dialog.getInteractionTarget() != null && dialog.getInteractionTarget().getMemoryWithoutUpdate() != null && dialog.getInteractionTarget().getMemoryWithoutUpdate().getBoolean("$EIS_YKWYD")) {
                dialog.getOptionPanel().setEnabled(OptionId.CLEAN_DISENGAGE, false);
                dialog.getOptionPanel().setTooltip(OptionId.CLEAN_DISENGAGE, "Look at the child trying to run away from responsibility. Own up to it.");
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
                        Coolscript.remove(Global.getSector().getImportantPeople().getPerson("eiskimquy"));
                        Coolscript.remove(Global.getSector().getImportantPeople().getPerson("eisdarren"));
                        Coolscript.remove(Global.getSector().getImportantPeople().getPerson("eisceleste"));
                        Coolscript.remove(Global.getSector().getImportantPeople().getPerson("eissneed"));
                        Coolscript.remove(Global.getSector().getImportantPeople().getPerson("eisava"));
                        
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
               if (transaction.getSubmarket().getPlugin().isBlackMarket()) {
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
                if (Global.getSector().getPlayerFleet().getCargo().getCredits().get() >= (SectorManager.getManager().isHardMode() ? 2000000f : 4000000f)) {
                    float wealthcredit = 0.00154f*Math.round(Global.getSector().getPlayerFleet().getCargo().getCredits().get())*f;
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
        }
        
        @Override
        public void reportPlayerReputationChange(String factionId, float delta) {
            if (("hegemony".equals(factionId) || IRONSTANDSETERNAL.equals(factionId)) && (Misc.getCommissionFactionId() != null && (Misc.getCommissionFactionId().equals(IRONSTANDSETERNAL) || Misc.getCommissionFactionId().equals("hegemony"))) && !DiplomacyManager.isRandomFactionRelationships()) {
                if ((Global.getSector().getFaction("hegemony").getRelToPlayer().getRel() <= 0.251f || Global.getSector().getFaction("ironshell").getRelToPlayer().getRel() <= 0.251f) && delta <= -0.36f) {
                    List<IntelInfoPlugin> intels =  Global.getSector().getIntelManager().getIntel(FactionCommissionIntel.class);
                    for (IntelInfoPlugin intel : intels) {
                        FactionCommissionIntel intelpenis = (FactionCommissionIntel) intel;
                        if (!intelpenis.isEnding()) {
                            intelpenis.setMissionResult(new MissionResult(-1, null));
                            intelpenis.setMissionState(MissionState.COMPLETED);
                            intelpenis.endMission();
                            intelpenis.sendUpdateIfPlayerHasIntel(null, false);
                            if (Global.getSector().getCampaignUI().getCurrentInteractionDialog() != null || Global.getSector().getCampaignUI().isShowingDialog()) {
                                Global.getSector().getCampaignUI().getCurrentInteractionDialog().getTextPanel().addPara(Global.getSettings().getString("eis_ironshell", "eis_greaterhegemony_annul"), Misc.getNegativeHighlightColor());
                                Global.getSector().getIntelManager().addIntelToTextPanel(intelpenis, Global.getSector().getCampaignUI().getCurrentInteractionDialog().getTextPanel());
                            }
                            Global.getSector().getFaction("hegemony").adjustRelationship("player", delta, RepLevel.HOSTILE);
                            Global.getSector().getFaction(IRONSTANDSETERNAL).adjustRelationship("player", delta, RepLevel.HOSTILE);
                            Global.getSector().getFaction(IRONSTANDSETERNAL).adjustRelationship("hegemony", -delta);
                            Global.getSector().getFaction("hegemony").adjustRelationship(IRONSTANDSETERNAL, -delta);
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
                if (AllianceManager.getFactionAlliance(IRONSTANDSETERNAL) == AllianceManager.getFactionAlliance("hegemony") && (Global.getSector().getFaction("hegemony").getRelToPlayer().getRel() <= -0.5f || Global.getSector().getFaction(IRONSTANDSETERNAL).getRelToPlayer().getRel() <= -0.5f) && delta < 0) {
                    Global.getSector().getFaction(IRONSTANDSETERNAL).setRelationship("player", Global.getSector().getFaction("hegemony").getRelToPlayer().getRel());
                    Global.getSector().getFaction("hegemony").setRelationship("player", Global.getSector().getFaction(IRONSTANDSETERNAL).getRelToPlayer().getRel());
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
        if (!DiplomacyManager.isRandomFactionRelationships() && Global.getSector().getPlayerFaction().getRelationship(IRONSTANDSETERNAL) >= -0.1f && Global.getSector().getPlayerFaction().getRelationship(IRONSTANDSETERNAL) <= 0.1f) {
            Global.getSector().getFaction(IRONSTANDSETERNAL).setRelationship(Factions.PLAYER, Global.getSector().getPlayerFaction().getRelationship(Factions.HEGEMONY));
        }
    }
}