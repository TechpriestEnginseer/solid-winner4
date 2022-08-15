package com.fs.starfarer.api.impl.campaign.rulecmd;

import com.fs.starfarer.api.Global;
import java.awt.Color;

import org.lwjgl.util.vector.Vector2f;

import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.FleetTypes;
import com.fs.starfarer.api.impl.campaign.missions.DelayedFleetEncounter;
import com.fs.starfarer.api.impl.campaign.missions.hub.HubMissionWithSearch;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

public class EISDeadDropMission extends HubMissionWithSearch {

	public static float PROB_COMPLICATIONS = 0.9f; //0.5f to both
	public static float PROB_PATROL_AFTER = 0.9f;
	public static float MISSION_DAYS = 120f;
	
	public static enum Stage {
		DROP_OFF,
		COMPLETED,
		FAILED,
	}
	
	protected String thing;
	protected SectorEntityToken target;
	protected StarSystemAPI system;
	
	@Override
	protected boolean create(MarketAPI createdAt, boolean barEvent) {
		//genRandom = Misc.random;
		
		/*if (barEvent) {
			setGiverRank(Ranks.CITIZEN);
			setGiverPost(pickOne(Ranks.POST_AGENT, Ranks.POST_SMUGGLER, Ranks.POST_GANGSTER, 
						 		 Ranks.POST_FENCE, Ranks.POST_CRIMINAL));
			setGiverImportance(pickImportance());
			setGiverFaction(Factions.PIRATES);
			setGiverTags(Tags.CONTACT_UNDERWORLD);
			findOrCreateGiver(createdAt, false, false);
		}*/
		
		thing = pickOne(Global.getSettings().getString("eis_ironshell", "EISDeadDropMission1"),
						Global.getSettings().getString("eis_ironshell", "EISDeadDropMission2"), 
						Global.getSettings().getString("eis_ironshell", "EISDeadDropMission3"),
						Global.getSettings().getString("eis_ironshell", "EISDeadDropMission4"),
						Global.getSettings().getString("eis_ironshell", "EISDeadDropMission5"),
						Global.getSettings().getString("eis_ironshell", "EISDeadDropMission6"),
						Global.getSettings().getString("eis_ironshell", "EISDeadDropMission7"),
						Global.getSettings().getString("eis_ironshell", "EISDeadDropMission8"),
						Global.getSettings().getString("eis_ironshell", "EISDeadDropMission9"),
						Global.getSettings().getString("eis_ironshell", "EISDeadDropMission10"),
						Global.getSettings().getString("eis_ironshell", "EISDeadDropMission11"),
						Global.getSettings().getString("eis_ironshell", "EISDeadDropMission12"),
						Global.getSettings().getString("eis_ironshell", "EISDeadDropMission13"),
						Global.getSettings().getString("eis_ironshell", "EISDeadDropMission14"),
						Global.getSettings().getString("eis_ironshell", "EISDeadDropMission15"),
						Global.getSettings().getString("eis_ironshell", "EISDeadDropMission16"),
						Global.getSettings().getString("eis_ironshell", "EISDeadDropMission17"),
						Global.getSettings().getString("eis_ironshell", "EISDeadDropMission18"),
						Global.getSettings().getString("eis_ironshell", "EISDeadDropMission19"),
						Global.getSettings().getString("eis_ironshell", "EISDeadDropMission20"),
						Global.getSettings().getString("eis_ironshell", "EISDeadDropMission21"),
						Global.getSettings().getString("eis_ironshell", "EISDeadDropMission22")
						);
		
		PersonAPI person = getPerson();
		if (person == null) return false;
		
		
		if (!setPersonMissionRef(person, "$ddro_ref")) {
			return false;
		}
		
//		requireSystemTags(ReqMode.NOT_ANY, Tags.THEME_UNSAFE, Tags.THEME_CORE);
//		preferSystemTags(ReqMode.ANY, Tags.THEME_INTERESTING, Tags.THEME_INTERESTING_MINOR);
		requireSystemInterestingAndNotUnsafeOrCore();
		preferSystemInInnerSector();
		preferSystemUnexplored();
		preferSystemInDirectionOfOtherMissions();
		
		system = pickSystem();
		if (system == null) return false;
		
		target = spawnMissionNode(new LocData(EntityLocationType.HIDDEN_NOT_NEAR_STAR, null, system));
		if (!setEntityMissionRef(target, "$ddro_ref")) return false;
		
		makeImportant(target, "$eis_ddro_target", Stage.DROP_OFF);
		

		setStartingStage(Stage.DROP_OFF);
		setSuccessStage(Stage.COMPLETED);
		setFailureStage(Stage.FAILED);
		
		setStageOnMemoryFlag(Stage.COMPLETED, target, "$eis_ddro_completed");
		setTimeLimit(Stage.FAILED, MISSION_DAYS, null);
		

		setCreditReward(CreditReward.HIGH);
		
		if (rollProbability(PROB_COMPLICATIONS)) {
			triggerComplicationBegin(Stage.DROP_OFF, ComplicationSpawn.APPROACHING_OR_ENTERING,
					system, Factions.PERSEAN, //Factions.PIRATES
					Global.getSettings().getString("eis_ironshell", "EISDeadDropMission23") + getWithoutArticle(thing), Global.getSettings().getString("eis_ironshell", "EISDeadDropMission24"),
					Global.getSettings().getString("eis_ironshell", "EISDeadDropMission23") + getWithoutArticle(thing) + Global.getSettings().getString("eis_ironshell", "EISDeadDropMission25") + person.getNameString(),
					0,
					true, ComplicationRepImpact.NONE, null);
			triggerComplicationEnd(true);
		}
		
		return true;
	}
	
	@Override
	protected void notifyEnding() {
		super.notifyEnding();
		
		if (isSucceeded() && rollProbability(PROB_PATROL_AFTER)) {
			PersonAPI person = getPerson();
			if (person == null || person.getMarket() == null) return;
			String patrolFaction = person.getMarket().getFactionId();
			if (patrolFaction.equals(person.getFaction().getId()) || 
					Misc.isPirateFaction(person.getMarket().getFaction()) ||
					Factions.PLAYER.equals(patrolFaction)) {
				return;
			}
			
			DelayedFleetEncounter e = new DelayedFleetEncounter(genRandom, getMissionId());
			e.setDelayNone();
			e.setLocationInnerSector(true, patrolFaction);
			e.beginCreate();
			e.triggerCreateFleet(FleetSize.LARGE, FleetQuality.DEFAULT, patrolFaction, FleetTypes.PATROL_LARGE, new Vector2f());
			e.setFleetWantsThing(patrolFaction, 
					Global.getSettings().getString("eis_ironshell", "EISDeadDropMission26"), Global.getSettings().getString("eis_ironshell", "EISDeadDropMission27"),
					Global.getSettings().getString("eis_ironshell", "EISDeadDropMission28") + person.getNameString(),
					0,
					true, ComplicationRepImpact.FULL,
					DelayedFleetEncounter.TRIGGER_REP_LOSS_MEDIUM, getPerson());
			e.triggerSetAdjustStrengthBasedOnQuality(true, getQuality());
			e.triggerSetPatrol();
			e.triggerSetStandardAggroInterceptFlags();
			e.endCreate();
		}
	}



	protected void updateInteractionDataImpl() {
		set("$ddro_manOrWoman", getPerson().getManOrWoman());
		set("$ddro_heOrShe", getPerson().getHeOrShe());
		set("$ddro_reward", Misc.getWithDGS(getCreditsReward()));
		
		set("$ddro_aOrAnThing", thing);
		set("$ddro_thing", getWithoutArticle(thing));
		
		set("$ddro_personName", getPerson().getNameString());
		set("$ddro_systemName", system.getNameWithLowercaseTypeShort());
		set("$ddro_dist", getDistanceLY(target));
	}
	
	@Override
	public void addDescriptionForNonEndStage(TooltipMakerAPI info, float width, float height) {
		float opad = 10f;
		if (currentStage == Stage.DROP_OFF) {
			info.addPara(Global.getSettings().getString("eis_ironshell", "EISDeadDropMission29") + thing + Global.getSettings().getString("eis_ironshell", "EISDeadDropMission30")+ 
                                system.getNameWithLowercaseTypeShort() + Global.getSettings().getString("eis_ironshell", "EISDeadDropMission31"), opad);
		}
	}

	@Override
	public boolean addNextStepText(TooltipMakerAPI info, Color tc, float pad) {
		Color h = Misc.getHighlightColor();
		if (currentStage == Stage.DROP_OFF) {
			info.addPara(Global.getSettings().getString("eis_ironshell", "EISDeadDropMission29") + getWithoutArticle(thing) + Global.getSettings().getString("eis_ironshell", "EISDeadDropMission32") +  
					system.getNameWithLowercaseTypeShort(), tc, pad);
			return true;
		}
		return false;
	}	
	
	@Override
	public String getBaseName() {
		return Global.getSettings().getString("eis_ironshell", "EISDeadDropMission33");
	}
	
}






