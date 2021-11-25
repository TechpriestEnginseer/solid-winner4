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
import com.fs.starfarer.api.characters.AdminData;
import com.fs.starfarer.api.characters.FullName;
import com.fs.starfarer.api.characters.OfficerDataAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.impl.campaign.FleetInteractionDialogPluginImpl.OptionId;
import com.fs.starfarer.api.impl.campaign.econ.impl.eis_SpecialItemEffectsRepo;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.Submarkets;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.impl.campaign.intel.BaseMissionIntel.MissionResult;
import com.fs.starfarer.api.impl.campaign.intel.BaseMissionIntel.MissionState;
import com.fs.starfarer.api.impl.campaign.intel.FactionCommissionIntel;
import com.fs.starfarer.api.impl.campaign.shared.SharedData;
import com.fs.starfarer.api.loading.PersonMissionSpec;
import com.fs.starfarer.api.util.Misc;
import java.awt.Color;
import exerelin.campaign.SectorManager;
import exerelin.campaign.intel.rebellion.RebellionIntel;
import data.scripts.world.Seriouslywhatisthewholepointofthisisweartoluddiwillmakethispersoneattheirshoe;
import exerelin.campaign.AllianceManager;
import exerelin.campaign.DiplomacyManager;
import java.io.IOException;
import java.util.List;
import kentington.capturecrew.CaptiveInteractionDialogPlugin;
import kentington.capturecrew.LootAddScript;
import org.dark.shaders.light.LightData;
import org.dark.shaders.util.ShaderLib;
import org.dark.shaders.util.TextureData;
import org.json.JSONException;


public class eis_modPlugin extends BaseModPlugin {
    public static boolean haveNex = Global.getSettings().getModManager().isModEnabled("nexerelin");
    public static boolean haveSWP = Global.getSettings().getModManager().isModEnabled("swp");
    public static boolean haveNia = Global.getSettings().getModManager().isModEnabled("tahlan");
    public static boolean hasTart = Global.getSettings().getModManager().isModEnabled("TORCHSHIPS");
    public static String StinkyBad = Global.getSettings().getString("eis_ironshell", "eis_greaterhegemony_annul");
    
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
            PlanetAPI naraka_b = system.addPlanet("yami", Global.getSector().getEntityById("yama"), "Yami", "cryovolcanic", 0, 60, 600, 40); //formerly 450 and 650
            naraka_b.setCustomDescriptionId("planet_yami_ironshell");
            PlanetAPI naraka_c = system.addPlanet("chitagupta", Global.getSector().getEntityById("naraka"), "Chitagupta", "water", 90, 100, 5750, 380);
            NascentGravityWellAPI well = Global.getSector().createNascentGravityWell(naraka_c,50f);
            hyper.addEntity(well);
            well.autoUpdateHyperLocationBasedOnInSystemEntityAtRadius(naraka_c, 470f);
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
            new Seriouslywhatisthewholepointofthisisweartoluddiwillmakethispersoneattheirshoe().generate(Global.getSector());
        }
    }
    
    @Override
    public void onApplicationLoad() throws JSONException, IOException {
        if (Global.getSettings().getModManager().isModEnabled("shaderLib") && Global.getSettings().loadJSON("GRAPHICS_OPTIONS.ini").getBoolean("enableShaders")) {
            ShaderLib.init();
            TextureData.readTextureDataCSV("data/lights/eis_texture_data.csv");
            LightData.readLightDataCSV("data/lights/eis_lights_data.csv");
        }
        if (Global.getSettings().getVariant("eis_skyrend_standard") != null) {
            ShipVariantAPI StandardVVariant = Global.getSettings().getVariant("eis_skyrend_standard");
            if (haveSWP) {
                if (Global.getSettings().getWeaponSpec("swp_contender") != null) {
                    StandardVVariant.clearSlot("WS0001");
                    StandardVVariant.addWeapon("WS0001", "swp_contender"); //from LAG
                    StandardVVariant.clearSlot("WS0002");
                    StandardVVariant.addWeapon("WS0002", "swp_contender"); //from LAG
                    StandardVVariant.setNumFluxCapacitors(StandardVVariant.getNumFluxCapacitors()+4);
                }
            }
            if (haveNia) {
                if (Global.getSettings().getWeaponSpec("tahlan_armiger") != null) {
                    StandardVVariant.clearSlot("WS0012");
                    StandardVVariant.addWeapon("WS0012", "tahlan_armiger"); //from tac laser
                    StandardVVariant.setNumFluxCapacitors(StandardVVariant.getNumFluxCapacitors()-6);
                }
            } 
        }
        if (Global.getSettings().getVariant("eis_tyrant_standard") != null) {
            ShipVariantAPI StandardTVariant = Global.getSettings().getVariant("eis_tyrant_standard");
            if (haveSWP) {
                if (Global.getSettings().getWeaponSpec("swp_contender") != null) {
                    StandardTVariant.clearSlot("WS0001");
                    StandardTVariant.addWeapon("WS0001", "swp_contender"); //from vulcan
                    StandardTVariant.setNumFluxCapacitors(StandardTVariant.getNumFluxCapacitors()+1);
                }
            }
            if (haveNia) {
                if (Global.getSettings().getWeaponSpec("tahlan_efreet") != null) {
                    StandardTVariant.clearSlot("WS0020");
                    StandardTVariant.addWeapon("WS0020", "tahlan_efreet"); //from gauss
                    StandardTVariant.setNumFluxCapacitors(StandardTVariant.getNumFluxCapacitors()-1);
                }
                if (Global.getSettings().getWeaponSpec("tahlan_armiger") != null) {
                    StandardTVariant.clearSlot("WS0021");
                    StandardTVariant.addWeapon("WS0021", "tahlan_armiger"); //from gauss
                    StandardTVariant.setNumFluxCapacitors(StandardTVariant.getNumFluxCapacitors()-1);
                }
            } 
        }
        if (Global.getSettings().getVariant("eis_skyrend_standard") != null) {
            ShipVariantAPI StandardSVariant = Global.getSettings().getVariant("eis_skyrend_standard");
            if (haveSWP) {
                if (Global.getSettings().getWeaponSpec("swp_lightphaselance") != null) {
                    StandardSVariant.clearSlot("WS0009");
                    StandardSVariant.addWeapon("WS0009", "swp_lightphaselance"); //from tac laser
                    StandardSVariant.clearSlot("WS0010");
                    StandardSVariant.addWeapon("WS0010", "swp_lightphaselance"); //from tac laser
                    StandardSVariant.clearSlot("WS0011");
                    StandardSVariant.addWeapon("WS0011", "swp_lightphaselance"); //from tac laser
                    StandardSVariant.clearSlot("WS0012");
                    StandardSVariant.addWeapon("WS0012", "swp_lightphaselance"); //from tac laser
                    StandardSVariant.setNumFluxCapacitors(StandardSVariant.getNumFluxCapacitors()-12); //from 21 to 9
                }
            } 
        }
        if (Global.getSettings().getVariant("eis_endeavor_attack") != null) {
            ShipVariantAPI AttackEVariant = Global.getSettings().getVariant("eis_endeavor_attack");
            if (haveSWP) {
                if (Global.getSettings().getWeaponSpec("swp_iontorpedo") != null) {
                    AttackEVariant.clearSlot("WS0028");
                    AttackEVariant.addWeapon("WS0028", "swp_iontorpedo"); //from harpoon
                    AttackEVariant.clearSlot("WS0029");
                    AttackEVariant.addWeapon("WS0029", "swp_iontorpedo"); //from harpoon
                    AttackEVariant.setNumFluxCapacitors(AttackEVariant.getNumFluxCapacitors()-4); //from 22 to 18
                }
            } 
        }
        if (Global.getSettings().getVariant("eis_endeavor_standard") != null) {
            ShipVariantAPI StandardEVariant = Global.getSettings().getVariant("eis_endeavor_standard");
            if (haveNia) {
                if (Global.getSettings().getWeaponSpec("tahlan_armiger") != null) {
                    StandardEVariant.clearSlot("WS0011");
                    StandardEVariant.addWeapon("WS0011", "tahlan_armiger"); //from harpoon
                    StandardEVariant.setNumFluxCapacitors(StandardEVariant.getNumFluxCapacitors()-2); //from 3 to 1
                }
            } 
        }
        if (Global.getSettings().getVariant("eis_endeavor_support") != null) {
            ShipVariantAPI SupportEVariant = Global.getSettings().getVariant("eis_endeavor_support");
            if (haveSWP) {
                if (Global.getSettings().getWeaponSpec("swp_iontorpedo") != null) {
                    SupportEVariant.clearSlot("WS0028");
                    SupportEVariant.addWeapon("WS0028", "swp_iontorpedo"); //from harpoon
                    SupportEVariant.clearSlot("WS0029");
                    SupportEVariant.addWeapon("WS0029", "swp_iontorpedo"); //from harpoon
                    SupportEVariant.setNumFluxCapacitors(SupportEVariant.getNumFluxCapacitors()-4); //from 21 to 17
                }
            } 
        }
        if (Global.getSettings().getVariant("eis_indomitable_assault") != null) {
            ShipVariantAPI AssaultIVariant = Global.getSettings().getVariant("eis_indomitable_assault");
            if (haveNia) {
                if (Global.getSettings().getWeaponSpec("tahlan_efreet") != null) {
                    AssaultIVariant.clearSlot("WS0003");
                    AssaultIVariant.addWeapon("WS0003", "tahlan_efreet"); //from mjolnir
                    AssaultIVariant.setNumFluxCapacitors(AssaultIVariant.getNumFluxCapacitors()-2); //from 17 to 15
                }
            } 
        }
        if (Global.getSettings().getVariant("eis_indomitable_missile") != null) {
            ShipVariantAPI MissileIVariant = Global.getSettings().getVariant("eis_indomitable_missile");
            if (haveSWP) {
                if (Global.getSettings().getWeaponSpec("swp_iontorpedo") != null) {
                    MissileIVariant.clearSlot("WS0005");
                    MissileIVariant.addWeapon("WS0005", "swp_iontorpedo"); //from annihilator
                    MissileIVariant.clearSlot("WS0006");
                    MissileIVariant.addWeapon("WS0006", "swp_iontorpedo"); //from annihilator
                    MissileIVariant.setNumFluxCapacitors(MissileIVariant.getNumFluxCapacitors()-4); //from 17 to 13
                }
            } 
        }
        if (Global.getSettings().getVariant("eis_champion_standard") != null) {
            ShipVariantAPI StandardCVariant = Global.getSettings().getVariant("eis_champion_standard");
            if (haveNia) {
                if (Global.getSettings().getWeaponSpec("tahlan_armiger") != null) {
                    StandardCVariant.clearSlot("WS 008");
                    StandardCVariant.addWeapon("WS 008", "tahlan_armiger"); //from mjollnir
                    StandardCVariant.setNumFluxCapacitors(StandardCVariant.getNumFluxCapacitors()-1); //from 1 to 0
                    StandardCVariant.setNumFluxVents(StandardCVariant.getNumFluxVents()-1); //from 30 to 29
                }
            } 
        }
        if (Global.getSettings().getVariant("eis_champion_elite") != null) {
            ShipVariantAPI EliteCVariant = Global.getSettings().getVariant("eis_champion_elite");
            if (hasTart) {
                if (Global.getSettings().getWeaponSpec("TADA_dioscuri") != null) {
                    EliteCVariant.clearSlot("WS 008");
                    EliteCVariant.addWeapon("WS 008", "TADA_dioscuri"); //from hephag
                    EliteCVariant.setNumFluxCapacitors(EliteCVariant.getNumFluxCapacitors()+2); //from 4 to 6
                }
            } 
        }
        
        /*for (String variantId : Global.getSettings().getAllVariantIds()) {
            if (variantId.equals("eis_indomitable_missile") && haveSWP) {
                ShipVariantAPI variant = Global.getSettings().getVariant(variantId);
                variant.clearSlot("WS0005");
                variant.addWeapon("WS0005", "swp_iontorpedo");
                variant.clearSlot("WS0006");
                variant.addWeapon("WS0006", "swp_iontorpedo");
                variant.clearSlot("WS0007");
                variant.addWeapon("WS0007", "swp_iontorpedo");
                variant.clearSlot("WS0008");
                variant.addWeapon("WS0008", "swp_iontorpedo");
            }
        }*/
    }

    
    @Override
    public void onGameLoad(boolean newGame) {
        eis_SpecialItemEffectsRepo.addItemEffectsToVanillaRepo();
        if (newGame) {
            if (haveSWP) {
                Global.getSector().getFaction(IRONSTANDSETERNAL).getKnownWeapons().add("swp_contender");
                Global.getSector().getFaction(IRONSTANDSETERNAL).getKnownWeapons().add("swp_plasmaflame");
                Global.getSector().getFaction(IRONSTANDSETERNAL).getKnownWeapons().add("swp_hornet");
                Global.getSector().getFaction(IRONSTANDSETERNAL).getKnownWeapons().add("swp_tornado");
                Global.getSector().getFaction(IRONSTANDSETERNAL).getKnownWeapons().add("swp_iontorpedo");
                Global.getSector().getFaction(IRONSTANDSETERNAL).getKnownWeapons().add("swp_ionblaster");
                Global.getSector().getFaction(IRONSTANDSETERNAL).getKnownWeapons().add("swp_lightphaselance");
                Global.getSector().getFaction("ironsentinel").getKnownWeapons().add("swp_contender");
                Global.getSector().getFaction("ironsentinel").getKnownWeapons().add("swp_plasmaflame");
                Global.getSector().getFaction("ironsentinel").getKnownWeapons().add("swp_hornet");
                Global.getSector().getFaction("ironsentinel").getKnownWeapons().add("swp_tornado");
                Global.getSector().getFaction("ironsentinel").getKnownWeapons().add("swp_iontorpedo");
                Global.getSector().getFaction("ironsentinel").getKnownWeapons().add("swp_ionblaster");
                Global.getSector().getFaction("ironsentinel").getKnownWeapons().add("swp_lightphaselance");
            }
            if (haveNia) {
                Global.getSector().getFaction(IRONSTANDSETERNAL).getKnownWeapons().add("tahlan_armiger");
                Global.getSector().getFaction(IRONSTANDSETERNAL).getKnownWeapons().add("tahlan_efreet");
                Global.getSector().getFaction("ironsentinel").getKnownWeapons().add("tahlan_armiger");
                Global.getSector().getFaction("ironsentinel").getKnownWeapons().add("tahlan_efreet");
            }
            if (hasTart) {
                Global.getSector().getFaction(IRONSTANDSETERNAL).getKnownWeapons().add("TADA_dioscuri");
                Global.getSector().getFaction("ironsentinel").getKnownWeapons().add("TADA_dioscuri");
            }
            
        }
        if (haveNex && SectorManager.getCorvusMode()) {
            for (PersonMissionSpec mission: Global.getSettings().getAllMissionSpecs()) {
                if (!(mission.getTagsAny().contains("eis_celeste") || mission.getTagsAny().contains("eis_military")) && 
                        !(mission.getTagsAny().isEmpty() && mission.getTagsAll().isEmpty() && mission.getTagsNotAny().isEmpty()) 
                        && mission.getPersonId() == null
                        && !mission.getTagsNotAny().contains("ironshell")) {
                    mission.getTagsNotAny().add("ironshell");
                }
            }
            for (String illegal: Global.getSector().getFaction(Factions.HEGEMONY).getIllegalCommodities()) {
                if (!Global.getSector().getFaction(IRONSTANDSETERNAL).isIllegal(illegal) && !"hand_weapons".equals(illegal)) {
                    Global.getSector().getFaction(IRONSTANDSETERNAL).makeCommodityIllegal(illegal);
                }
            }
            for (String hullMods : Global.getSector().getFaction(Factions.HEGEMONY).getKnownHullMods()) {
                if (!Global.getSector().getFaction(IRONSTANDSETERNAL).knowsHullMod(hullMods)) {
                    Global.getSector().getFaction(IRONSTANDSETERNAL).addKnownHullMod(hullMods);
                }
            }
            Global.getSector().addTransientListener(new MarketCheckTariffs());
            if (Global.getSettings().getModManager().isModEnabled("capturecrew")) {Global.getSector().addTransientListener(new MarketCheckTariffs2());}
            
            if (MarketshipChange.getInstance() == null) {
                Global.getSector().getListenerManager().addListener(new MarketshipChange().registerInstance(), true); 
            }
            if (Amongus.getInstance() == null) {Global.getSector().getListenerManager().addListener(new Amongus().registerInstance(), true);}
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
        }
            if (Global.getSettings().getBoolean("NoEISPlayer")) {
                Global.getSector().getFaction(Factions.HEGEMONY).getPortraits(FullName.Gender.FEMALE).remove("graphics/portraits/eis_elsa.png");
                Global.getSector().getFaction(Factions.HEGEMONY).getPortraits(FullName.Gender.FEMALE).remove("graphics/portraits/eis_judy.png");
                Global.getSector().getFaction(Factions.HEGEMONY).getPortraits(FullName.Gender.FEMALE).remove("graphics/portraits/eis_trinh.png");
                Global.getSector().getFaction(Factions.HEGEMONY).getPortraits(FullName.Gender.FEMALE).remove("graphics/portraits/eis_velma.png");
                Global.getSector().getPlayerFaction().getPortraits(FullName.Gender.MALE).remove("graphics/portraits/eis_hegelin.png");
                Global.getSector().getPlayerFaction().getPortraits(FullName.Gender.MALE).remove("graphics/portraits/eis_richard.png");
                Global.getSector().getPlayerFaction().getPortraits(FullName.Gender.FEMALE).remove("graphics/portraits/eis_audrey.png");
                Global.getSector().getPlayerFaction().getPortraits(FullName.Gender.FEMALE).remove("graphics/portraits/eis_elsa.png");
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
                   Global.getSector().getMemoryWithoutUpdate().set("$EIS_taxburden", Global.getSector().getMemoryWithoutUpdate().getFloat("$EIS_taxburden")-(Math.abs(transaction.getCreditValue()*0.033f*transaction.getSubmarket().getTariff())));
                   Global.getSector().getMemoryWithoutUpdate().set("$EIS_OMTax", Global.getSector().getMemoryWithoutUpdate().getFloat("$EIS_OMTax")-(Math.abs(transaction.getCreditValue()*0.033f*transaction.getSubmarket().getTariff())));
               }
               if (transaction.getSubmarket().getPlugin().isBlackMarket()) {
                   if (transaction.getMarket().hasCondition("nex_rebellion_condition")) {
                        if ("hegemony".equals(RebellionIntel.getOngoingEvent(transaction.getMarket()).getRebelFaction().getId()) || IRONSTANDSETERNAL.equals(RebellionIntel.getOngoingEvent(transaction.getMarket()).getRebelFaction().getId())) {
                            Global.getSector().getMemoryWithoutUpdate().set("$EIS_taxburden", Global.getSector().getMemoryWithoutUpdate().getFloat("$EIS_taxburden")-(Math.abs(transaction.getCreditValue()*0.075f)));
                            Global.getSector().getMemoryWithoutUpdate().set("$EIS_BMHISTax", Global.getSector().getMemoryWithoutUpdate().getFloat("$EIS_BMHISTax")-(Math.abs(transaction.getCreditValue()*0.075f)));
                        }
                    } else {
                        Global.getSector().getMemoryWithoutUpdate().set("$EIS_taxburden", Global.getSector().getMemoryWithoutUpdate().getFloat("$EIS_taxburden")+(Math.abs(transaction.getCreditValue()*0.09f)));
                        Global.getSector().getMemoryWithoutUpdate().set("$EIS_BMTax", Global.getSector().getMemoryWithoutUpdate().getFloat("$EIS_BMTax")+(Math.abs(transaction.getCreditValue()*0.09f)));
                   }
               }
           }
        }
        
        @Override
	public void reportEconomyTick(int iterIndex) { //Calculate all the taxation
            if (Global.getSector().getMemoryWithoutUpdate().getFloat("$EIS_taxburden") <= 40000000) {
                float f = 1f / Global.getSettings().getFloat("economyIterPerMonth");
                CampaignFleetAPI playerFleet = Global.getSector().getPlayerFleet();
                float crewCost = playerFleet.getCargo().getCrew() * Global.getSettings().getInt("crewSalary") * 0.2f * f;
                float marineCost = playerFleet.getCargo().getMarines() * Global.getSettings().getInt("marineSalary") * f;
                float officersalary = 0;
                float adminsalary = 0;
                for (OfficerDataAPI od : playerFleet.getFleetData().getOfficersCopy()) {
                    float salary = Misc.getOfficerSalary(od.getPerson());
                    if (salary <= 0) continue;
                    officersalary += salary * 0.33f * f;
                }
                for (AdminData data : Global.getSector().getCharacterData().getAdmins()) {
                    float salary2 = Misc.getAdminSalary(data.getPerson());
                    if (salary2 <= 0) continue;
                    adminsalary += salary2 * 0.2f * f;
                }
                float taxrefund = crewCost+marineCost+officersalary+adminsalary;
                if (Global.getSector().getPlayerFleet().getCargo().getCredits().get() >= 3000000f) {
                    float wealthcredit = 0.00154f*Math.round(Global.getSector().getPlayerFleet().getCargo().getCredits().get())*f;
                    Global.getSector().getMemoryWithoutUpdate().set("$EIS_taxburden", Global.getSector().getMemoryWithoutUpdate().getFloat("$EIS_taxburden")+wealthcredit-taxrefund);
                    Global.getSector().getMemoryWithoutUpdate().set("$EIS_WealthTax", Global.getSector().getMemoryWithoutUpdate().getFloat("$EIS_WealthTax")+wealthcredit);
                    Global.getSector().getMemoryWithoutUpdate().set("$EIS_PayrollTax", Global.getSector().getMemoryWithoutUpdate().getFloat("$EIS_PayrollTax")-taxrefund);
                } else {Global.getSector().getMemoryWithoutUpdate().set("$EIS_taxburden", Global.getSector().getMemoryWithoutUpdate().getFloat("$EIS_taxburden")-taxrefund);
                    Global.getSector().getMemoryWithoutUpdate().set("$EIS_PayrollTax", Global.getSector().getMemoryWithoutUpdate().getFloat("$EIS_PayrollTax")-taxrefund);}
            }
        }
        
        @Override
        public void reportPlayerReputationChange(String factionId, float delta) {
            if ("hegemony".equals(factionId) && (Misc.getCommissionFactionId() != null && Misc.getCommissionFactionId().equals(IRONSTANDSETERNAL)) && !DiplomacyManager.isRandomFactionRelationships()) {
                if (Global.getSector().getFaction("hegemony").getRelToPlayer().getRel() == 0.1f && delta <= -0.36f) {
                    List<IntelInfoPlugin> intels =  Global.getSector().getIntelManager().getIntel(FactionCommissionIntel.class);
                    for (IntelInfoPlugin intel : intels) {
                        FactionCommissionIntel intelpenis = (FactionCommissionIntel) intel;
                        if (!intelpenis.isCompleted()) {
                            Global.getSector().getFaction("hegemony").adjustRelationship("player", delta);//, RepLevel.HOSTILE);
                            Global.getSector().getFaction(IRONSTANDSETERNAL).adjustRelationship("player", delta, RepLevel.HOSTILE);
                            intelpenis.setMissionResult(new MissionResult(-1, null));
                            intelpenis.setMissionState(MissionState.COMPLETED);
                            intelpenis.endMission();
                            intelpenis.sendUpdateIfPlayerHasIntel(null, false);
                            if (Global.getSector().getCampaignUI().getCurrentInteractionDialog() != null || Global.getSector().getCampaignUI().isShowingDialog()) {
                                Global.getSector().getCampaignUI().getCurrentInteractionDialog().getTextPanel().addPara(StinkyBad, Misc.getNegativeHighlightColor());
                                Global.getSector().getIntelManager().addIntelToTextPanel(intelpenis, Global.getSector().getCampaignUI().getCurrentInteractionDialog().getTextPanel());
                            }
                        }                          
                    }
                }
            }
            if (("hegemony".equals(factionId) || IRONSTANDSETERNAL.equals(factionId)) && !(Misc.getCommissionFactionId() != null && Misc.getCommissionFactionId().equals(IRONSTANDSETERNAL)) && !DiplomacyManager.isRandomFactionRelationships()) {
                if (AllianceManager.getFactionAlliance(IRONSTANDSETERNAL) == null && Global.getSector().getFaction("hegemony").getRelToPlayer().getRel() < -0.5f && delta < 0) {
                    Global.getSector().getFaction(IRONSTANDSETERNAL).setRelationship("player", Global.getSector().getFaction("hegemony").getRelToPlayer().getRel());
                }
            }
        }
        
        /*@Override
        public void reportPlayerClosedMarket(MarketAPI market) {
            if ("chicomoztoc".equals(market.getId())) {
                Industry heavyindustry = null;
                for (Industry industry : market.getIndustries()) {
                    if (industry.getSpec().hasTag("heavyindustry")) {
                        heavyindustry = industry;
                        break;
                    }
                }
                if (heavyindustry != null) {
                    if (heavyindustry.getSpecialItem() != null) {
                        Global.getSector().getFaction(Factions.INDEPENDENT).setRelationship("player", -1f);
                    }
                }
            }
        }*/
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