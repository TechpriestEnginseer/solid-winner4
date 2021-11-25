package data.missions.eis_queens;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.impl.campaign.ids.Skills;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.characters.FullName;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.fleet.FleetGoal;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.mission.FleetSide;
import com.fs.starfarer.api.mission.MissionDefinitionAPI;
import com.fs.starfarer.api.mission.MissionDefinitionPlugin;

public class MissionDefinition implements MissionDefinitionPlugin {

	public void defineMission(MissionDefinitionAPI api) {

		// Set up the fleets
		api.initFleet(FleetSide.PLAYER, "HSS", FleetGoal.ATTACK, false, 10);
		api.initFleet(FleetSide.ENEMY, "TTS", FleetGoal.ATTACK, true, 10);

		// Set a blurb for each fleet
		api.setFleetTagline(FleetSide.PLAYER, "Hegemony patrol");
		api.setFleetTagline(FleetSide.ENEMY, "Tri-Tachyon mercenary detachment");
		
		// These show up as items in the bulleted list under 
		// "Tactical Objectives" on the mission detail screen
		api.addBriefingItem("Defeat all enemy forces");
		api.addBriefingItem("HSS Bond must survive");
		api.addBriefingItem("Your Valorous has access to the Aquila Reactor Protocol, granting faster venting");
		api.addBriefingItem("Venting will make your enemies use their missiles, use this to your advantage in your ship system");
		
		// Set up the player's fleet
		FleetMemberAPI member = api.addToFleet(FleetSide.PLAYER, "eis_valorous_bond", FleetMemberType.SHIP, "HSS Bond", true);
		PersonAPI coffeemom = Global.getSector().getFaction("ironshell").createRandomPerson(FullName.Gender.FEMALE);
		coffeemom.setId("eisava_mission");
        coffeemom.getName().setFirst("Ava");
        coffeemom.getName().setLast("Nitia");
        coffeemom.getName().setGender(FullName.Gender.FEMALE);
        coffeemom.setPersonality("steady");
        coffeemom.setPortraitSprite("graphics/portraits/eis_ava.png");
        coffeemom.setFaction("hegemony");
        coffeemom.getStats().setSkillLevel(Skills.HELMSMANSHIP, 1);
        coffeemom.getStats().setSkillLevel(Skills.TARGET_ANALYSIS, 1);
        coffeemom.getStats().setSkillLevel(Skills.IMPACT_MITIGATION, 1);
        coffeemom.getStats().setSkillLevel(Skills.SHIELD_MODULATION, 1);
        coffeemom.getStats().setSkillLevel(Skills.MISSILE_SPECIALIZATION, 1);
        coffeemom.getStats().setSkillLevel(Skills.SYSTEMS_EXPERTISE, 1);
        coffeemom.getStats().setSkillLevel(Skills.RELIABILITY_ENGINEERING, 1);
		coffeemom.getStats().setLevel(7);
        member.setCaptain(coffeemom);
		
		FleetMemberAPI member2 = api.addToFleet(FleetSide.PLAYER, "eis_courageous_elite", FleetMemberType.SHIP, false);
		member2.setAlly(true);
		FleetMemberAPI member3 = api.addToFleet(FleetSide.PLAYER, "eis_courageous_elite", FleetMemberType.SHIP, false);
		member3.setAlly(true);
		FleetMemberAPI member4 = api.addToFleet(FleetSide.PLAYER, "eis_courageous_elite", FleetMemberType.SHIP, false);
		member4.setAlly(true);
		FleetMemberAPI member5 = api.addToFleet(FleetSide.PLAYER, "eis_courageous_elite", FleetMemberType.SHIP, false);
		member5.setAlly(true);
		
		// Mark player flagship as essential
		api.defeatOnShipLoss("HSS Bond");
		
		// Set up the enemy fleet.
		api.addToFleet(FleetSide.ENEMY, "odyssey_Balanced", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "medusa_Attack", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "medusa_Attack", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "sunder_CS", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "wolf_CS", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "wolf_CS", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "tempest_Attack", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "tempest_Attack", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "tempest_Attack", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "lasher_CS", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "lasher_CS", FleetMemberType.SHIP, false);
		
		// Set up the map.
		float width = 24000f;
		float height = 18000f;
		api.initMap((float)-width/2f, (float)width/2f, (float)-height/2f, (float)height/2f);
		
		float minX = -width/2;
		float minY = -height/2;
		
		for (int i = 0; i < 15; i++) {
			float x = (float) Math.random() * width - width/2;
			float y = (float) Math.random() * height - height/2;
			float radius = 100f + (float) Math.random() * 900f; 
			api.addNebula(x, y, radius);
		}
		
		api.addNebula(minX + width * 0.8f - 1000, minY + height * 0.4f, 2000);
		api.addNebula(minX + width * 0.8f - 1000, minY + height * 0.5f, 2000);
		api.addNebula(minX + width * 0.8f - 1000, minY + height * 0.6f, 2000);
		
		api.addObjective(minX + width * 0.8f - 1000, minY + height * 0.4f, "sensor_array");
		api.addObjective(minX + width * 0.7f - 1000, minY + height * 0.6f, "nav_buoy");
		api.addObjective(minX + width * 0.4f + 1000, minY + height * 0.3f, "sensor_array");
		api.addObjective(minX + width * 0.5f, minY + height * 0.5f, "nav_buoy");
		//api.addObjective(minX + width * 0.2f + 1000, minY + height * 0.5f, "sensor_array");
		
		api.addAsteroidField(minX, minY + height / 2, 0, 8000f,
							 20f, 70f, 100);
	}

}






