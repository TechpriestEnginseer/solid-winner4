package data.missions.eis_queens;

import java.util.List;
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
import com.fs.starfarer.api.combat.ShipAPI;
import java.awt.Color;

//You wouldn't just edit the file to make this mission easier wouldn't you?

public class MissionDefinition implements MissionDefinitionPlugin {

	public void defineMission(MissionDefinitionAPI api) {

		// Set up the fleets
		api.initFleet(FleetSide.PLAYER, "HSS", FleetGoal.ATTACK, false, 0);
		api.initFleet(FleetSide.ENEMY, "TTS", FleetGoal.ATTACK, true, 10);

		// Set a blurb for each fleet
		api.setFleetTagline(FleetSide.PLAYER, "The HSS Bond accompanied by patrol ships");
		api.setFleetTagline(FleetSide.ENEMY, "Tri-Tachyon mercenary detachment");
		
		// These show up as items in the bulleted list under 
		// "Tactical Objectives" on the mission detail screen
		api.addBriefingItem("Ava has the following skills: Combat Endurance (Elite), Helmsmanship, Iron Heritage, and Support");
		api.addBriefingItem("Doctrine. The HSS Bond has Zandatsu which gains a buff when reflecting a missile.");
		api.addBriefingItem("Defeat all enemy forces. HSS Bond must survive.");
		
			// Set up the player's fleet
			FleetMemberAPI member = api.addToFleet(FleetSide.PLAYER, "eis_valorous_bond", FleetMemberType.SHIP, "HSS Bond", true);
			api.getDefaultCommander(FleetSide.PLAYER).getStats().setSkillLevel(Skills.SUPPORT_DOCTRINE, 1);
			//That's my Ava!
			PersonAPI coffeemom = Global.getSector().getFaction("ironshell").createRandomPerson(FullName.Gender.FEMALE);
			coffeemom.setId("eisava_mission1");
			coffeemom.getName().setFirst("Ava");
			coffeemom.getName().setLast("Nitia");
			coffeemom.getName().setGender(FullName.Gender.FEMALE);
			coffeemom.setPersonality("aggressive");
			coffeemom.setPortraitSprite("graphics/portraits/eis_ava.png");
			coffeemom.setFaction("hegemony");
			coffeemom.getStats().setSkillLevel(Skills.HELMSMANSHIP, 1);
			coffeemom.getStats().setSkillLevel(Skills.COMBAT_ENDURANCE, 3);
			//coffeemom.getStats().setSkillLevel(Skills.SUPPORT_DOCTRINE, 1);
			coffeemom.getStats().setSkillLevel("eis_xiv", 1);
			coffeemom.getMemoryWithoutUpdate().set("$chatterChar", "eis_ava");
			coffeemom.getStats().setLevel(3);
			member.setCaptain(coffeemom);
			//Apparently this repair tracker doesn't work yet.. so we're giving them the old way.
            member.getRepairTracker().setCR(0.85f); //member.getRepairTracker().setCR(member.getRepairTracker().getMaxCR());
			
			FleetMemberAPI member2 = api.addToFleet(FleetSide.PLAYER, "eis_courageous_elite", FleetMemberType.SHIP, false);
			member2.getRepairTracker().setCR(0.85f);//member2.getRepairTracker().setCR(member2.getRepairTracker().getMaxCR());
			//member2.setAlly(true);
			FleetMemberAPI member3 = api.addToFleet(FleetSide.PLAYER, "eis_courageous_elite", FleetMemberType.SHIP, false);
			member3.getRepairTracker().setCR(0.85f);//member3.getRepairTracker().setCR(member3.getRepairTracker().getMaxCR());
			//member3.setAlly(true);
			FleetMemberAPI member4 = api.addToFleet(FleetSide.PLAYER, "eis_courageous_elite", FleetMemberType.SHIP, false);
			member4.getRepairTracker().setCR(0.85f);//member4.getRepairTracker().setCR(member4.getRepairTracker().getMaxCR());
			//member4.setAlly(true);
			//FleetMemberAPI member5 = api.addToFleet(FleetSide.PLAYER, "eis_courageous_elite", FleetMemberType.SHIP, false);
			//member5.getRepairTracker().setCR(member5.getRepairTracker().getMaxCR());
			//member5.setAlly(true);
			
			// Mark player flagship as essential
			api.defeatOnShipLoss("HSS Bond");
			
			// Set up the enemy fleet.
			api.addToFleet(FleetSide.ENEMY, "odyssey_Balanced", FleetMemberType.SHIP, false);
			api.addToFleet(FleetSide.ENEMY, "medusa_Attack", FleetMemberType.SHIP, false);
			api.addToFleet(FleetSide.ENEMY, "medusa_PD", FleetMemberType.SHIP, false);
			api.addToFleet(FleetSide.ENEMY, "sunder_CS", FleetMemberType.SHIP, false);
			api.addToFleet(FleetSide.ENEMY, "sunder_Assault", FleetMemberType.SHIP, false);
			api.addToFleet(FleetSide.ENEMY, "wolf_CS", FleetMemberType.SHIP, false);
			api.addToFleet(FleetSide.ENEMY, "wolf_Assault", FleetMemberType.SHIP, false);
			api.addToFleet(FleetSide.ENEMY, "tempest_Attack", FleetMemberType.SHIP, false);
			//api.addToFleet(FleetSide.ENEMY, "tempest_Attack", FleetMemberType.SHIP, false);
			api.addToFleet(FleetSide.ENEMY, "tempest_Attack", FleetMemberType.SHIP, false);
			api.addToFleet(FleetSide.ENEMY, "lasher_CS", FleetMemberType.SHIP, false);
			api.addToFleet(FleetSide.ENEMY, "lasher_Assault", FleetMemberType.SHIP, false);
		// Set up the map.
		float width = 16000f; //from 24000f, i don't like wide maps..
		float height = 18000f;
		api.initMap((float)-width/2f, (float)width/2f, (float)-height/2f, (float)height/2f);
		
		float minX = -width/2;
		float minY = -height/2;
		
		for (int i = 0; i < 5; i++) { //15
			float x = (float) Math.random() * width - width/2;
			float y = (float) Math.random() * height - height/2;
			float radius = 100f + (float) Math.random() * 900f; 
			api.addNebula(x, y, radius);
		}
		
		api.addNebula(minX + width * 0.8f - 1000, minY + height * 0.4f, 2000);
		api.addNebula(minX + width * 0.8f - 1000, minY + height * 0.5f, 2000);
		api.addNebula(minX + width * 0.8f - 1000, minY + height * 0.6f, 2000);
		
		api.addObjective(minX + width * 0.9f - 1000, minY + height * 0.5f, "sensor_array"); //0.8f 0.5f
		api.addObjective(minX + width * 0.8f - 1000, minY + height * 0.7f, "nav_buoy"); //0.7f 0.6f
		api.addObjective(minX + width * 0.4f + 1000, minY + height * 0.3f, "sensor_array");
		api.addObjective(minX + width * 0.3f, minY + height * 0.4f, "nav_buoy"); //0.5f 0.5f
		//api.addObjective(minX + width * 0.2f + 1000, minY + height * 0.5f, "sensor_array");
		
		api.addAsteroidField(minX, minY + height / 2, 0, 8000f,
							 20f, 70f, 100);
		api.addPlugin(new BaseEveryFrameCombatPlugin() {
			public void init(CombatEngineAPI engine) {
				engine.getContext().aiRetreatAllowed = false;
                                engine.getContext().setInitialDeploymentBurnDuration(1f);
                                engine.getContext().setNormalDeploymentBurnDuration(1f);
				
			}
			public void advance(float amount, List events) {
				/*if (Global.getCombatEngine().isPaused()) {
					return;
				}
				for (ShipAPI ship : Global.getCombatEngine().getShips()) {
					if (ship.getCustomData().get("poopystinky") == null) {
						ship.setCurrentCR(ship.getCurrentCR()+ship.getMutableStats().getMaxCombatReadiness().getModifiedValue());
						ship.setCustomData("poopystinky", true);
					}
				}; We don't need it for the time being.. but it is useful!*/
			}
		});
	}
}






