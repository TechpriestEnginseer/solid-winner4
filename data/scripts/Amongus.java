package data.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.FleetAssignment;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.JumpPointAPI.JumpDestination;
import com.fs.starfarer.api.campaign.PersonImportance;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.SpecialItemData;
import com.fs.starfarer.api.campaign.ai.FleetAIFlags;
import com.fs.starfarer.api.campaign.ai.ModularFleetAIAPI;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.listeners.ColonyPlayerHostileActListener;
import com.fs.starfarer.api.characters.FullName;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.fleets.FleetFactoryV3;
import com.fs.starfarer.api.impl.campaign.fleets.FleetParamsV3;
import com.fs.starfarer.api.impl.campaign.ids.Abilities;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.FleetTypes;
import com.fs.starfarer.api.impl.campaign.ids.Items;
import com.fs.starfarer.api.impl.campaign.ids.MemFlags;
import com.fs.starfarer.api.impl.campaign.ids.Personalities;
import com.fs.starfarer.api.impl.campaign.ids.Skills;
import com.fs.starfarer.api.impl.campaign.ids.Voices;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.MarketCMD.TempData;
import com.fs.starfarer.api.impl.campaign.skills.OfficerTraining;
import com.fs.starfarer.api.util.Misc;
import static data.scripts.eis_lib.PlayerHasHowManySmods;
import data.scripts.eis_modPlugin.MarketCheckTariffs3;
import java.util.ArrayList;
import java.util.List;
import org.lwjgl.util.vector.Vector2f;

//What do you mean I "borrowed" this from some Australian cat that is dressing up to be a dog in an eggsuit?

public class Amongus implements ColonyPlayerHostileActListener {
    
    
    public static Amongus currInstance;
    
    public Amongus registerInstance() {
	currInstance = this;
	return this;
    }
	
    public static Amongus getInstance() {
	return currInstance;
    }
    
    @Override
    public void reportRaidForValuablesFinishedBeforeCargoShown(InteractionDialogAPI dialog, MarketAPI market, TempData actionData, CargoAPI cargo) {
            if ("chicomoztoc".equals(market.getId()) || "eis_chitagupta".equals(market.getId()) || "eventide".equals(market.getId()) || "nachiketa".equals(market.getId())) {
                if ("hegemony".equals(market.getFactionId()) || "ironshell".equals(market.getFactionId())) {
                    if (cargo.getQuantity(CargoAPI.CargoItemType.SPECIAL, new SpecialItemData(Items.PRISTINE_NANOFORGE, null)) > 0 || cargo.getQuantity(CargoAPI.CargoItemType.SPECIAL, new SpecialItemData(Items.CORRUPTED_NANOFORGE, null)) > 0 || cargo.getQuantity(CargoAPI.CargoItemType.SPECIAL, new SpecialItemData(Items.FULLERENE_SPOOL, null)) > 0 || cargo.getQuantity(CargoAPI.CargoItemType.SPECIAL, new SpecialItemData(Items.SYNCHROTRON, null)) > 0) {
                        List<CampaignFleetAPI> fleetSystem = new ArrayList<>(market.getContainingLocation().getFleets());
                        boolean yay = false;
                        for (CampaignFleetAPI fleet : fleetSystem){
                            if (fleet.getMemoryWithoutUpdate().getBoolean("$EIS_YKWYD")) {
                                yay = true;
                                break;
                            }
                        }
                        if (!yay && "chicomoztoc".equals(market.getId())) {UhohStinky(market, true);} else if (!yay) {UhohStinky(market, false);}
                        if (!Global.getSector().hasTransientScript(MarketCheckTariffs3.class)) {Global.getSector().addTransientListener(new MarketCheckTariffs3());}
                        Global.getSector().getFaction(Factions.HEGEMONY).setRelationship("player", -1f);
                        Global.getSector().getFaction("ironshell").setRelationship("player", -1f);
                        if (Global.getSector().getImportantPeople().getPerson("eisava") != null) {Global.getSector().getImportantPeople().getPerson("eisava").getRelToPlayer().setRel(-1f);}
                        if (Global.getSector().getPlayerFleet().getAbility("emergency_burn") != null) {Global.getSector().getPlayerFleet().getAbility("emergency_burn").activate();Global.getSector().getPlayerFleet().getAbility("emergency_burn").deactivate();Global.getSector().getPlayerFleet().getAbility("emergency_burn").setCooldownLeft(1f);}
                        if (Global.getSector().getPlayerFleet().getAbility("fracture_jump") != null) {Global.getSector().getPlayerFleet().getAbility("fracture_jump").activate();Global.getSector().getPlayerFleet().getAbility("fracture_jump").deactivate();Global.getSector().getPlayerFleet().getAbility("fracture_jump").setCooldownLeft(1f);}
                    }
                }
            }
    }

    public void UhohStinky(MarketAPI market, boolean Hardspawn) {
        
		FleetParamsV3 params = new FleetParamsV3(
				market, 
				null, // loc in hyper; don't need if have market
				"ironsentinel",
				2f, // quality override route.getQualityOverride()
				FleetTypes.TASK_FORCE,
				Math.round(Hardspawn ? 40f+Global.getSector().getPlayerFleet().getFleetPoints()*1.7f : Global.getSector().getPlayerFleet().getFleetPoints()*0.8f), // combatPts
				0f, // freighterPts 
				0f, // tankerPts
				0f, // transportPts
				0f, // linerPts
				0f, // utilityPts
				0f // qualityMod
				);
                params.officerNumberMult = Hardspawn ? 2 : 1;
                params.officerLevelBonus = Hardspawn ? 2: 1;
                params.officerLevelLimit = Global.getSettings().getInt("officerMaxLevel") + (int) OfficerTraining.MAX_LEVEL_BONUS;
		params.modeOverride = FactionAPI.ShipPickMode.PRIORITY_THEN_ALL;
                params.averageSMods = Hardspawn ? PlayerHasHowManySmods(3) : PlayerHasHowManySmods(1);
                params.commander = Global.getSector().getImportantPeople().getPerson("eisava") != null ? Global.getSector().getImportantPeople().getPerson("eisava") : generateava();
                if (Global.getSector().getPlayerFleet().getNumCapitals()*5f+Global.getSector().getPlayerFleet().getNumCruisers()<Global.getSector().getPlayerFleet().getNumCruisers()*2f+Global.getSector().getPlayerFleet().getNumDestroyers()+Global.getSector().getPlayerFleet().getNumFrigates()) {
                    params.maxShipSize = 3;
                }
            CampaignFleetAPI fleet = FleetFactoryV3.createFleet(params);
            if (fleet == null || fleet.isEmpty()) return;
            fleet.setFaction("ironshell", true);
            fleet.getFlagship().setCaptain(Global.getSector().getImportantPeople().getPerson("eisava"));
            fleet.setNoFactionInName(true);
            fleet.setName("Aranitia");
            //fleet.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_PIRATE, true);
            fleet.getMemoryWithoutUpdate().set("$EIS_YKWYD", true);
            market.getContainingLocation().addEntity(fleet);
            //fleet.setLocation(market.getPrimaryEntity().getLocation().x+1000f, market.getPrimaryEntity().getLocation().y+1000f);
            makeFleetInterceptPlayer(fleet, true, true, true, 90f);
            SectorEntityToken token = market.getContainingLocation().createToken(market.getPrimaryEntity().getLocation().x+1000f, market.getPrimaryEntity().getLocation().y+1000f);
            fleet.setFacing((float) Math.random() * 360f);
            Global.getSector().doHyperspaceTransition(fleet, fleet, new JumpDestination(token, null));
            Misc.giveStandardReturnToSourceAssignments(fleet, false);
    }
    
    public static PersonAPI generateava() {
            PersonAPI coffeemom = Global.getSector().getFaction("ironshell").createRandomPerson(FullName.Gender.FEMALE);
            coffeemom.setId("eisava");
            coffeemom.setPostId("HeadWardenIS");
            coffeemom.setRankId("HeadWardenIS");
            coffeemom.setImportance(PersonImportance.VERY_HIGH);
            coffeemom.getName().setFirst("Ava");
            coffeemom.getName().setLast("Nitia");
            coffeemom.setPortraitSprite("graphics/portraits/eis_ava.png");
            coffeemom.addTag("VNSector");
            coffeemom.getMemoryWithoutUpdate().set("$nex_preferredAdmin", true);
            coffeemom.getMemoryWithoutUpdate().set("$nex_preferredAdmin_factionId", "ironshell");
            //Gacha whatever pilot (7 + 5 elite)
            coffeemom.getStats().setSkillLevel(Skills.HELMSMANSHIP, 1);
            coffeemom.getStats().setSkillLevel(Skills.GUNNERY_IMPLANTS, 1);
            coffeemom.getStats().setSkillLevel(Skills.IMPACT_MITIGATION, 2);
            coffeemom.getStats().setSkillLevel(Skills.FIELD_MODULATION, 2);
            coffeemom.getStats().setSkillLevel(Skills.SYSTEMS_EXPERTISE, 2);
            coffeemom.getStats().setSkillLevel(Skills.POLARIZED_ARMOR, 2);
            coffeemom.getStats().setSkillLevel(Skills.COMBAT_ENDURANCE, 2);
            //Admiral (4)
            coffeemom.getStats().setSkillLevel(Skills.CREW_TRAINING, 1);
            coffeemom.getStats().setSkillLevel(Skills.FLUX_REGULATION, 1);
            coffeemom.getStats().setSkillLevel(Skills.SUPPORT_DOCTRINE, 1);
            coffeemom.getStats().setSkillLevel(Skills.OFFICER_TRAINING, 1);
            //Administrator (1)
            coffeemom.getStats().setSkillLevel(Skills.INDUSTRIAL_PLANNING, 1);
            //Sad (3)
            coffeemom.getStats().setSkillLevel(Skills.HULL_RESTORATION, 1);//coffeemom.getStats().setSkillLevel(Skills.NAVIGATION, 1);
            coffeemom.getStats().setSkillLevel(Skills.SENSORS, 1);
            coffeemom.getStats().setSkillLevel(Skills.CONTAINMENT_PROCEDURES, 1);
            //Amongus
            coffeemom.getStats().setSkillLevel("eis_xiv", 2);
            coffeemom.getStats().setLevel(14);
            coffeemom.setPersonality(Personalities.AGGRESSIVE);
            //coffeemom.addTag(Tags.CONTACT_MILITARY);
            coffeemom.addTag("eis_military");
            coffeemom.setVoice(Voices.OFFICIAL);
            return coffeemom;
    }

    public static void makeFleetInterceptPlayer(CampaignFleetAPI fleet, boolean makeAggressive, boolean makeLowRepImpact, boolean makeHostile, float interceptDays) {
        fleet.addAbility(Abilities.EMERGENCY_BURN);
        CampaignFleetAPI playerFleet = Global.getSector().getPlayerFleet();

        if (fleet.getAI() == null) {
            fleet.setAI(Global.getFactory().createFleetAI(fleet));
            fleet.setLocation(fleet.getLocation().x+100f, fleet.getLocation().y+100f);
        }

        if (makeAggressive) {
            float expire = fleet.getMemoryWithoutUpdate().getExpire(MemFlags.MEMORY_KEY_MAKE_AGGRESSIVE);
            fleet.getMemoryWithoutUpdate().set(MemFlags.FLEET_FIGHT_TO_THE_LAST, true);
            fleet.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_NO_SHIP_RECOVERY, true);
            fleet.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_MAKE_AGGRESSIVE, true, Math.max(expire, interceptDays));
            fleet.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_ALLOW_LONG_PURSUIT, true);
            fleet.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_MAKE_AGGRESSIVE_ONE_BATTLE_ONLY, true, Math.max(expire, interceptDays));
        }

        if (makeHostile) {
            fleet.getMemoryWithoutUpdate().unset(MemFlags.MEMORY_KEY_MAKE_ALLOW_DISENGAGE);
            fleet.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_MAKE_HOSTILE, true, interceptDays);
        }
        fleet.getMemoryWithoutUpdate().set(FleetAIFlags.PLACE_TO_LOOK_FOR_TARGET, new Vector2f(playerFleet.getLocation()), interceptDays);

        if (makeLowRepImpact) {
            Misc.makeLowRepImpact(playerFleet, "csSpecial");
        }

        if (fleet.getAI() instanceof ModularFleetAIAPI) {
            ((ModularFleetAIAPI)fleet.getAI()).getTacticalModule().setTarget(playerFleet);
        }

        fleet.addAssignmentAtStart(FleetAssignment.INTERCEPT, playerFleet, interceptDays, null);
    }
    
    @Override
    public void reportRaidToDisruptFinished(InteractionDialogAPI dialog, MarketAPI market, TempData actionData, Industry industry) {
    }

    @Override
    public void reportTacticalBombardmentFinished(InteractionDialogAPI dialog, MarketAPI market, TempData actionData) {
    }

    @Override
    public void reportSaturationBombardmentFinished(InteractionDialogAPI dialog, MarketAPI market, TempData actionData) {
    }
}