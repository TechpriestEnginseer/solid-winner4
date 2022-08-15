package data.missions.eis_traitors;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.impl.campaign.ids.Skills;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.characters.FullName;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.combat.BaseEveryFrameCombatPlugin;
import com.fs.starfarer.api.fleet.FleetGoal;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.mission.FleetSide;
import com.fs.starfarer.api.mission.MissionDefinitionAPI;
import com.fs.starfarer.api.mission.MissionDefinitionPlugin;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.DeployedFleetMemberAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.WeightedRandomPicker;
import exerelin.utilities.NexConfig;
import exerelin.utilities.NexFactionConfig.StartFleetType;
import java.util.ArrayList;
import java.util.List;
import org.lazywizard.lazylib.combat.AIUtils;

//You wouldn't just edit the file to make this mission easier wouldn't you?

public class MissionDefinition implements MissionDefinitionPlugin {

	public void defineMission(MissionDefinitionAPI api) {
		// Set up the fleets
		api.initFleet(FleetSide.PLAYER, "HSS", FleetGoal.ESCAPE, false, 3);
		api.initFleet(FleetSide.ENEMY, "HSS", FleetGoal.ATTACK, true, 10);

		// Set a blurb for each fleet
		api.setFleetTagline(FleetSide.PLAYER, "Loyalist Hegemony force");
		api.setFleetTagline(FleetSide.ENEMY, "Traitor Hegemony force");
		
		// These show up as items in the bulleted list under 
		// "Tactical Objectives" on the mission detail screen
		api.addBriefingItem("Darren is skilled in: Helmsmanship, Impact Mitigation, System Expertise, Combat Endurance.");
		api.addBriefingItem("Rampage Drive is a ramming ship system dealing massive damage on direct impact.");
		api.addBriefingItem("Retreat with 3 or more survivors. HSS Upholder must survive.");
		
			// Set up the player's fleet
			FleetMemberAPI member = api.addToFleet(FleetSide.PLAYER, "eis_indomitable_standard", FleetMemberType.SHIP, "HSS Upholder", true);
			
			PersonAPI unforgiven = Global.getSector().getFaction("ironshell").createRandomPerson(FullName.Gender.MALE);
			unforgiven.setId("eisdarren_mission1");
			unforgiven.getName().setFirst("Darren");
			unforgiven.getName().setLast("Hartley");
			unforgiven.getName().setGender(FullName.Gender.MALE);
			unforgiven.setPersonality("aggressive");
			unforgiven.setPortraitSprite("graphics/portraits/eis_dunscaith.png");
			unforgiven.setFaction("hegemony");
			unforgiven.getStats().setSkillLevel(Skills.HELMSMANSHIP, 1);
			unforgiven.getStats().setSkillLevel(Skills.COMBAT_ENDURANCE, 1);
			unforgiven.getStats().setSkillLevel(Skills.IMPACT_MITIGATION, 1);
			unforgiven.getStats().setSkillLevel(Skills.SYSTEMS_EXPERTISE, 1);
			unforgiven.getMemoryWithoutUpdate().set("$chatterChar", "eis_hartley");
			unforgiven.getStats().setLevel(4);
			member.setCaptain(unforgiven);
			//Apparently this repair tracker doesn't work yet.. so we're giving them the old way.
                        member.getRepairTracker().setCR(0.85f); //member.getRepairTracker().setCR(member.getRepairTracker().getMaxCR());
			FleetMemberAPI member2 = api.addToFleet(FleetSide.PLAYER, "eis_indomitable_missile", FleetMemberType.SHIP, false);
			FleetMemberAPI member3 = api.addToFleet(FleetSide.PLAYER, "eis_flagellator_elite_brother", FleetMemberType.SHIP, false);
			FleetMemberAPI member4 = api.addToFleet(FleetSide.PLAYER, "eis_flagellator_elite_brother", FleetMemberType.SHIP, false);
			// Mark player flagship as essential
			api.defeatOnShipLoss("HSS Upholder");
			
			// Set up the enemy fleet.
			api.getDefaultCommander(FleetSide.ENEMY).getStats().setSkillLevel(Skills.SUPPORT_DOCTRINE, 1);
                        api.getDefaultCommander(FleetSide.ENEMY).getStats().setSkillLevel(Skills.COORDINATED_MANEUVERS, 1);
                        api.getDefaultCommander(FleetSide.ENEMY).getStats().setSkillLevel(Skills.ELECTRONIC_WARFARE, 1);
			api.addToFleet(FleetSide.ENEMY, "eagle_xiv_Elite", FleetMemberType.SHIP, false).getRepairTracker().setCR(0.85f);
			api.addToFleet(FleetSide.ENEMY, "eagle_xiv_Elite", FleetMemberType.SHIP, false).getRepairTracker().setCR(0.85f);
			api.addToFleet(FleetSide.ENEMY, "falcon_xiv_Escort", FleetMemberType.SHIP, false).getRepairTracker().setCR(0.85f);
			api.addToFleet(FleetSide.ENEMY, "falcon_xiv_Escort", FleetMemberType.SHIP, false).getRepairTracker().setCR(0.85f);
			api.addToFleet(FleetSide.ENEMY, "manticore_Assault", FleetMemberType.SHIP, false).getRepairTracker().setCR(0.85f);
                        api.addToFleet(FleetSide.ENEMY, "manticore_Assault", FleetMemberType.SHIP, false).getRepairTracker().setCR(0.85f);
                        if (Global.getSettings().getModManager().isModEnabled("swp")) {api.addToFleet(FleetSide.ENEMY, "swp_hammerhead_xiv_eli", FleetMemberType.SHIP, false).getRepairTracker().setCR(0.85f);
                        api.addToFleet(FleetSide.ENEMY, "swp_hammerhead_xiv_eli", FleetMemberType.SHIP, false).getRepairTracker().setCR(0.85f);
			api.addToFleet(FleetSide.ENEMY, "swp_sunder_xiv_eli", FleetMemberType.SHIP, false).getRepairTracker().setCR(0.85f);}
                        else {api.addToFleet(FleetSide.ENEMY, "hammerhead_Elite", FleetMemberType.SHIP, false).getRepairTracker().setCR(0.85f);
                        api.addToFleet(FleetSide.ENEMY, "hammerhead_Balanced", FleetMemberType.SHIP, false).getRepairTracker().setCR(0.85f);
			api.addToFleet(FleetSide.ENEMY, "sunder_CS", FleetMemberType.SHIP, false).getRepairTracker().setCR(0.85f);}
                        api.addToFleet(FleetSide.ENEMY, "vigilance_Strike", FleetMemberType.SHIP, false).getRepairTracker().setCR(0.85f);
                        if (Global.getSettings().getModManager().isModEnabled("swp")) {api.addToFleet(FleetSide.ENEMY, "swp_brawler_hegemony_ass", FleetMemberType.SHIP, false).getRepairTracker().setCR(0.85f);
			api.addToFleet(FleetSide.ENEMY, "swp_brawler_hegemony_ass", FleetMemberType.SHIP, false).getRepairTracker().setCR(0.85f);}
                        else {api.addToFleet(FleetSide.ENEMY, "brawler_Assault", FleetMemberType.SHIP, false).getRepairTracker().setCR(0.85f);
			api.addToFleet(FleetSide.ENEMY, "brawler_Elite", FleetMemberType.SHIP, false).getRepairTracker().setCR(0.85f);}
			api.addToFleet(FleetSide.ENEMY, "centurion_Assault", FleetMemberType.SHIP, false).getRepairTracker().setCR(0.85f);
                        api.addToFleet(FleetSide.ENEMY, "centurion_Assault", FleetMemberType.SHIP, false).getRepairTracker().setCR(0.85f);
			api.addToFleet(FleetSide.ENEMY, "eis_vanguard_xiv_mission", FleetMemberType.SHIP, false).getRepairTracker().setCR(0.85f);
			api.addToFleet(FleetSide.ENEMY, "eis_vanguard_xiv_mission", FleetMemberType.SHIP, false).getRepairTracker().setCR(0.85f);
		// Set up the map.
		float height = 18000f;
		float width = 6000f;
		api.initMap((float)-width/2f, (float)width/2f, (float)-height/2f, (float)height/2f);
		
		float minX = -width/2;
		float minY = -height/2;
		
		/*for (int i = 0; i < 5; i++) {
			float x = (float) Math.random() * width - width/2;
			float y = (float) Math.random() * height - height/2;
			float radius = 100f + (float) Math.random() * 900f; 
			api.addNebula(x, y, radius);
		}*/
                api.addAsteroidField(minX, 1.25f*(minY + height / 2), 0, 8000f,
							 20f, 70f, 100);
                api.addPlanet(0, -9000f, 2000f, "rocky_metallic", 250f);
                api.setBackgroundSpriteName("graphics/backgrounds/background4.jpg");
		api.addPlugin(new BaseEveryFrameCombatPlugin() {
                    private boolean reallyStarted = false;
                    private boolean started = false;
                    private boolean finished = false;
                    private IntervalUtil tracker = new IntervalUtil(1f, 1f);
                    WeightedRandomPicker<String> AmongUs = new WeightedRandomPicker<String>();
			public void init(CombatEngineAPI engine) {
				engine.getContext().aiRetreatAllowed = true;
                                engine.getContext().enemyDeployAll = true;
                                engine.getContext().setInitialDeploymentBurnDuration(1f);
                                engine.getContext().setNormalDeploymentBurnDuration(1f);
                                engine.getContext().setInitialEscapeRange(1000f);
                                engine.getContext().setFlankDeploymentDistance(500f);
                                AmongUs.add("My life for the Hegemony. Go!", 1);
                                AmongUs.add("There is still loyalty in the Hegemony.", 1);
                                AmongUs.add("We still serve with honor. We will show these traitors our hand.", 1);
				
			}
			public void advance(float amount, List events) {
                            //Apparently you need two frames to really make it work
                            if (!started) {
                                started = true;
                                return;
                            }
                            
                            if (!reallyStarted) {
                                reallyStarted = true;
                                return;
                            }
                            
                            if (Global.getCombatEngine().isPaused()) {
                                return;
                            }
                            tracker.advance(amount);
                            if (tracker.intervalElapsed()) {
                                if ((Global.getCombatEngine().getFleetManager(FleetSide.ENEMY).getDeployedCopy().isEmpty() || Global.getCombatEngine().getFleetManager(FleetSide.PLAYER).getRetreatedCopy().size() >= 3) && !finished) {
                                    finished = true;
									if (Global.getCombatEngine().getPlayerShip() != null && Global.getCombatEngine().getPlayerShip().getFleetMember() != null) {
										Global.getCombatEngine().getCombatUI().addMessage(1, Global.getCombatEngine().getPlayerShip().getFleetMember(), Misc.getPositiveHighlightColor(), Global.getCombatEngine().getPlayerShip().getName(), Misc.getTextColor(), ": ", Global.getSettings().getColor("standardTextColor"), "It's over now, we need to rendezvous with any other surviving loyalists.");
										Global.getCombatEngine().addFloatingText(Global.getCombatEngine().getPlayerShip().getLocation(), "It's over now, we need to rendezvous with any other surviving loyalists.", Global.getSettings().getModManager().isModEnabled("chatter") ? Global.getSettings().getInt("chatter_floaterFontSize") : 32, Global.getSettings().getColor("standardTextColor"), Global.getCombatEngine().getPlayerShip(), 0, 0);
									}
                                }
								
								if (finished) {
									for (int i=1; i <= NexConfig.getFactionConfig("ironshell").getStartFleetSet(StartFleetType.SUPER.name()).getNumFleets(); i++) {
                                        if ("eis_indomitable_missile".equals(NexConfig.getFactionConfig("ironshell").getStartFleetSet(StartFleetType.SUPER.name()).getFleet(i).get(0))) {break;}
                                        if ((i) == NexConfig.getFactionConfig("ironshell").getStartFleetSet(StartFleetType.SUPER.name()).getNumFleets()) {
                                            List<String> indomitableFleet = new ArrayList<String>();
                                            indomitableFleet.add("eis_indomitable_missile");
                                            indomitableFleet.add("eis_flagellator_standard");
                                            indomitableFleet.add("eis_vanguard_xiv_elite2");
                                            NexConfig.getFactionConfig("ironshell").getStartFleetSet(StartFleetType.SUPER.name()).addFleet(indomitableFleet);
                                            break;
                                        }
                                    }
								}
								
                                for (ShipAPI ship : AIUtils.getNearbyEnemies(Global.getCombatEngine().getPlayerShip(), 2000f)) {
                                    if (ship.getHullSpec().getHullId().startsWith("eis_vanguard_xiv")) {
                                        String AmongUsYes = (String) AmongUs.pick();
                                        Global.getCombatEngine().getCombatUI().addMessage(1, ship.getFleetMember(), Misc.getHighlightColor(), ship.getName(), Misc.getTextColor(), ": ", Global.getSettings().getColor("standardTextColor"), AmongUsYes);
                                        Global.getCombatEngine().addFloatingText(ship.getLocation(), AmongUsYes, Global.getSettings().getModManager().isModEnabled("chatter") ? Global.getSettings().getInt("chatter_floaterFontSize") : 32, Global.getSettings().getColor("standardTextColor"), ship, 0, 0);
                                        ship.setOwner(0);
                                        ship.setAlly(true);
                                        if (ship.getShipAI() != null) {
                                            //from secret xhan's mind control ship system to make sure AI doesn't get confused...
                                            DeployedFleetMemberAPI member_a = Global.getCombatEngine().getFleetManager(FleetSide.PLAYER).getDeployedFleetMember(ship);
                                            if (member_a != null) Global.getCombatEngine().getFleetManager(FleetSide.PLAYER).getTaskManager(false).orderSearchAndDestroy(member_a, false);

                                            DeployedFleetMemberAPI member_aa = Global.getCombatEngine().getFleetManager(FleetSide.PLAYER).getDeployedFleetMember(ship);
                                            if (member_aa != null) Global.getCombatEngine().getFleetManager(FleetSide.PLAYER).getTaskManager(true).orderSearchAndDestroy(member_aa, false);

                                            DeployedFleetMemberAPI member_b = Global.getCombatEngine().getFleetManager(FleetSide.ENEMY).getDeployedFleetMember(ship);
                                            if (member_b != null) Global.getCombatEngine().getFleetManager(FleetSide.ENEMY).getTaskManager(false).orderSearchAndDestroy(member_b, false);
                                            ship.getShipAI().forceCircumstanceEvaluation();
                                        }
                                        //Global.getCombatEngine().getFleetManager(FleetSide.ENEMY).getDeployedCopy().remove(ship.getFleetMember());
                                        //Global.getCombatEngine().getFleetManager(FleetSide.PLAYER).getDeployedCopy().add(ship.getFleetMember());
                                    }
                                }
                                
                            }
                        }
		});
	}
}






