package data.missions.eis_blanks;

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
		api.initFleet(FleetSide.PLAYER, "HSS", FleetGoal.ATTACK, false, 5);
		api.initFleet(FleetSide.ENEMY, "TTS", FleetGoal.ATTACK, true, 5);

		// Set a blurb for each fleet
		api.setFleetTagline(FleetSide.PLAYER, "Hegemony Patrol");
		api.setFleetTagline(FleetSide.ENEMY, "Contracted Tri-Tachyon detachment");
		
		// These show up as items in the bulleted list under 
		// "Tactical Objectives" on the mission detail screen
		api.addBriefingItem("The envoy is skilled in: Polarized Armor (Elite), Helmsmanship (Elite), and Missile Specialization.");
		api.addBriefingItem("Wolves are equipped with Avaritia, boosting vent speed and providing a damage boost after venting.");
		api.addBriefingItem("Wolves do not hunt alone. Defeat all enemy forces. HSS Luna must survive.");
		
			// Set up the player's fleet
			FleetMemberAPI member = api.addToFleet(FleetSide.PLAYER, "eis_wolf_avaritia", FleetMemberType.SHIP, "HSS Luna", true);
			
			PersonAPI sweetperson = Global.getSector().getFaction("ironshell").createRandomPerson(FullName.Gender.FEMALE);
			sweetperson.setId("eisceleste_mission1");
			sweetperson.getName().setFirst("Caeda");
			sweetperson.getName().setLast("Celeste");
			sweetperson.getName().setGender(FullName.Gender.FEMALE);
			sweetperson.setPersonality("aggressive");
			sweetperson.setPortraitSprite("graphics/portraits/eis_celeste.png");
			sweetperson.setFaction("hegemony");
			sweetperson.getStats().setSkillLevel(Skills.POLARIZED_ARMOR, 2);
			sweetperson.getStats().setSkillLevel(Skills.HELMSMANSHIP, 2);
			sweetperson.getStats().setSkillLevel(Skills.MISSILE_SPECIALIZATION, 1);
			sweetperson.getMemoryWithoutUpdate().set("$chatterChar", "eis_celeste");
			sweetperson.getStats().setLevel(3);
			member.setCaptain(sweetperson);
			FleetMemberAPI member2 = api.addToFleet(FleetSide.PLAYER, "eis_wolf_avaritia", FleetMemberType.SHIP, "HSS Zeta", false);
			FleetMemberAPI member3 = api.addToFleet(FleetSide.PLAYER, "eis_wolf_avaritia", FleetMemberType.SHIP, "HSS Theta", false);
			FleetMemberAPI member4 = api.addToFleet(FleetSide.PLAYER, "eis_wolf_avaritia", FleetMemberType.SHIP, "HSS Eta", false);
			FleetMemberAPI member5 = api.addToFleet(FleetSide.PLAYER, "enforcer_Elite", FleetMemberType.SHIP, false);
			FleetMemberAPI member6 = api.addToFleet(FleetSide.PLAYER, "eis_condor_wow", FleetMemberType.SHIP, false);
            FleetMemberAPI member7 = api.addToFleet(FleetSide.PLAYER, "eis_condor_wow", FleetMemberType.SHIP, false);
			// Mark player flagship as essential
			api.defeatOnShipLoss("HSS Luna");
			
			// Set up the enemy fleet.
			api.getDefaultCommander(FleetSide.ENEMY).getStats().setSkillLevel(Skills.PHASE_CORPS, 1);
			api.addToFleet(FleetSide.ENEMY, "medusa_PD", FleetMemberType.SHIP, false);
			//api.addToFleet(FleetSide.ENEMY, "shade_Assault", FleetMemberType.SHIP, false);
            api.addToFleet(FleetSide.ENEMY, "shade_Assault", FleetMemberType.SHIP, false);
			//api.addToFleet(FleetSide.ENEMY, "shrike_Attack", FleetMemberType.SHIP, false);
			api.addToFleet(FleetSide.ENEMY, "shrike_Support", FleetMemberType.SHIP, false);
			api.addToFleet(FleetSide.ENEMY, "omen_PD", FleetMemberType.SHIP, false);
			api.addToFleet(FleetSide.ENEMY, "brawler_tritachyon_Standard", FleetMemberType.SHIP, false);
			api.addToFleet(FleetSide.ENEMY, "brawler_tritachyon_Standard", FleetMemberType.SHIP, false);
			api.addToFleet(FleetSide.ENEMY, "drover_Strike", FleetMemberType.SHIP, false);
		// Set up the map.
		float width = 14000f;
		float height = 10500f;
		api.initMap((float)-width/2f, (float)width/2f, (float)-height/2f, (float)height/2f);
		
		float minX = -width/2;
		float minY = -height/2;
		api.addObjective(minX + width * 0.4f - 1000, minY + height * 0.5f, "nav_buoy");
		api.addObjective(minX + width * 0.8f - 1000, minY + height * 0.3f, "sensor_array");
		
		for (int i = 0; i < 5; i++) {
			float x = (float) Math.random() * width - width/2;
			float y = (float) Math.random() * height - height/2;
			float radius = 100f + (float) Math.random() * 900f; 
			api.addNebula(x, y, radius);
		}
		api.addPlugin(new BaseEveryFrameCombatPlugin() {
                    private boolean reallyStarted = false;
                    private boolean started = false;
                    private boolean finished = false;
                    private IntervalUtil tracker = new IntervalUtil(1f, 1f);
                    WeightedRandomPicker<String> AmongUs = new WeightedRandomPicker<String>();
			public void init(CombatEngineAPI engine) {
				engine.getContext().aiRetreatAllowed = false;
                                engine.getContext().enemyDeployAll = true;
                                engine.getContext().setInitialDeploymentBurnDuration(1f);
                                engine.getContext().setNormalDeploymentBurnDuration(1f);
				
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
                                if (Global.getCombatEngine().getFleetManager(FleetSide.ENEMY).getDeployedCopy().isEmpty() && !finished) {
                                    finished = true;
									if (Global.getCombatEngine().getPlayerShip() != null && Global.getCombatEngine().getPlayerShip().getFleetMember() != null) {
										Global.getCombatEngine().getCombatUI().addMessage(1, Global.getCombatEngine().getPlayerShip().getFleetMember(), Misc.getPositiveHighlightColor(), Global.getCombatEngine().getPlayerShip().getName(), Misc.getTextColor(), ": ", Global.getSettings().getColor("standardTextColor"), "Never knew Tri-Tachs were this pathetic.");
										Global.getCombatEngine().addFloatingText(Global.getCombatEngine().getPlayerShip().getLocation(), "Never knew Tri-Tachs were this pathetic.", Global.getSettings().getModManager().isModEnabled("chatter") ? Global.getSettings().getInt("chatter_floaterFontSize") : 32, Global.getSettings().getColor("standardTextColor"), Global.getCombatEngine().getPlayerShip(), 0, 0);
									}
                                    for (int i=1; i <= NexConfig.getFactionConfig("ironshell").getStartFleetSet(StartFleetType.SUPER.name()).getNumFleets(); i++) {
                                        if ("eis_wolf_avaritia".equals(NexConfig.getFactionConfig("ironshell").getStartFleetSet(StartFleetType.SUPER.name()).getFleet(i).get(0))) {break;}
                                        if ((i) == NexConfig.getFactionConfig("ironshell").getStartFleetSet(StartFleetType.SUPER.name()).getNumFleets()) {
                                            List<String> wolfFleet = new ArrayList<String>();
                                            wolfFleet.add("eis_wolf_avaritia");
                                            wolfFleet.add("eis_wolf_avaritia");
                                            wolfFleet.add("eis_wolf_avaritia");
											wolfFleet.add("eis_wolf_avaritia");
                                            NexConfig.getFactionConfig("ironshell").getStartFleetSet(StartFleetType.SUPER.name()).addFleet(wolfFleet);
                                            break;
                                        }
                                    }
                                }
                            }
                        }
		});
	}
}






