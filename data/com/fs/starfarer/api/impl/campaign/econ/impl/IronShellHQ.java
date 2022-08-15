package com.fs.starfarer.api.impl.campaign.econ.impl;

import java.util.Random;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.BattleAPI;
import com.fs.starfarer.api.campaign.CampaignEventListener.FleetDespawnReason;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.FactionAPI.ShipPickMode;
import com.fs.starfarer.api.campaign.econ.CommodityOnMarketAPI;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.listeners.FleetEventListener;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.impl.campaign.econ.impl.MilitaryBase.PatrolFleetData;
import com.fs.starfarer.api.impl.campaign.fleets.FleetFactory.PatrolType;
import com.fs.starfarer.api.impl.campaign.fleets.FleetFactoryV3;
import com.fs.starfarer.api.impl.campaign.fleets.FleetParamsV3;
import com.fs.starfarer.api.impl.campaign.fleets.PatrolAssignmentAIV4;
import com.fs.starfarer.api.impl.campaign.fleets.RouteManager;
import com.fs.starfarer.api.impl.campaign.fleets.RouteManager.OptionalFleetData;
import com.fs.starfarer.api.impl.campaign.fleets.RouteManager.RouteData;
import com.fs.starfarer.api.impl.campaign.fleets.RouteManager.RouteFleetSpawner;
import com.fs.starfarer.api.impl.campaign.fleets.RouteManager.RouteSegment;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.MemFlags;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.MarketCMD.RaidDangerLevel;
import com.fs.starfarer.api.loading.VariantSource;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Pair;
import com.fs.starfarer.api.util.WeightedRandomPicker;
import static data.scripts.eis_lib.PlayerHasHowManySmods;
import java.awt.Color;


public class IronShellHQ extends BaseIndustry implements RouteFleetSpawner, FleetEventListener {
	@Override
	public boolean isHidden() {
		return !market.getFactionId().equals("ironshell");
	}
	
	@Override
	public boolean isFunctional() {
		return super.isFunctional() && market.getFactionId().equals("ironshell");
	}

        @Override
	public void apply() {
		super.apply(true);
		
		int size = market.getSize();
		
		demand(Commodities.SUPPLIES, size - 2);
		demand(Commodities.FUEL, size - 2);
		demand(Commodities.SHIPS, size - 2);
		
		supply(Commodities.CREW, size-1);
		
		demand(Commodities.HAND_WEAPONS, size-2);
		supply(Commodities.MARINES, size-1);
			
		Pair<String, Integer> deficit = getMaxDeficit(Commodities.HAND_WEAPONS);
		applyDeficitToProduction(1, deficit, Commodities.MARINES);
		
		modifyStabilityWithBaseMod();
		
		MemoryAPI memory = market.getMemoryWithoutUpdate();
		Misc.setFlagWithReason(memory, MemFlags.MARKET_PATROL, getModId(), true, -1);
		Misc.setFlagWithReason(memory, MemFlags.MARKET_MILITARY, getModId(), true, -1);
		
		if (!isFunctional()) {
			supply.clear();
			unapply();
                        market.getStability().modifyFlat(id, -2f, Global.getSettings().getString("eis_ironshell", "eis_ironshellhq"));
		}

	}

	@Override
	public void unapply() {
		super.unapply();
		
		MemoryAPI memory = market.getMemoryWithoutUpdate();
		Misc.setFlagWithReason(memory, MemFlags.MARKET_PATROL, getModId(), false, -1);
		Misc.setFlagWithReason(memory, MemFlags.MARKET_MILITARY, getModId(), false, -1);
		
		unmodifyStabilityWithBaseMod();
                
                market.getStability().unmodify(id);
	}
	
        @Override
	protected boolean hasPostDemandSection(boolean hasDemand, IndustryTooltipMode mode) {
		return mode != IndustryTooltipMode.NORMAL || isFunctional();
	}
	
	@Override
	protected void addPostDemandSection(TooltipMakerAPI tooltip, boolean hasDemand, IndustryTooltipMode mode) {
		if (mode != IndustryTooltipMode.NORMAL || isFunctional()) {
			addStabilityPostDemandSection(tooltip, hasDemand, mode);
		}
	}
	
	@Override
	protected int getBaseStabilityMod() {
		return 2;
	}
	
        @Override
	public String getNameForModifier() {
		if (getSpec().getName().contains("HQ")) {
			return getSpec().getName();
		}
		return Misc.ucFirst(getSpec().getName());
	}
	
	@Override
	protected Pair<String, Integer> getStabilityAffectingDeficit() {
		return getMaxDeficit(Commodities.SUPPLIES, Commodities.FUEL, Commodities.SHIPS, Commodities.HAND_WEAPONS);
	}
	
	@Override
	public String getCurrentImage() {
		return super.getCurrentImage();
	}

	
        @Override
	public boolean isDemandLegal(CommodityOnMarketAPI com) {
		return true;
	}

        @Override
	public boolean isSupplyLegal(CommodityOnMarketAPI com) {
		return true;
	}

	protected IntervalUtil tracker = new IntervalUtil(Global.getSettings().getFloat("averagePatrolSpawnInterval") * 0.7f,
													  Global.getSettings().getFloat("averagePatrolSpawnInterval") * 1.3f);
	
	protected float returningPatrolValue = 0f;
	
	@Override
	protected void buildingFinished() {
		super.buildingFinished();
		
		tracker.forceIntervalElapsed();
	}
	
	@Override
	protected void upgradeFinished(Industry previous) {
		super.upgradeFinished(previous);
		
		tracker.forceIntervalElapsed();
	}

	@Override
	public void advance(float amount) {
		super.advance(amount);
		
		if (Global.getSector().getEconomy().isSimMode()) return;

		if (!isFunctional()) return;
		
		float days = Global.getSector().getClock().convertToDays(amount);
		
		float spawnRate = 1f;
		float rateMult = market.getStats().getDynamic().getStat(Stats.COMBAT_FLEET_SPAWN_RATE_MULT).getModifiedValue();
		spawnRate *= rateMult;
		
		
		float extraTime = 0f;
		if (returningPatrolValue > 0) {
			// apply "returned patrols" to spawn rate, at a maximum rate of 1 interval per day
			float interval = tracker.getIntervalDuration();
			extraTime = interval * days;
			returningPatrolValue -= days;
			if (returningPatrolValue < 0) returningPatrolValue = 0;
		}
                if (getCount(PatrolType.COMBAT)+getCount(PatrolType.HEAVY)<2) {
                    tracker.advance(days * spawnRate + extraTime);
                }
		
		//tracker.advance(days * spawnRate * 100f);
		
		if (tracker.intervalElapsed()) {
			String sid = getRouteSourceId();
			
			//int light = getCount(PatrolType.FAST);
			int medium = getCount(PatrolType.COMBAT);
			int heavy = getCount(PatrolType.HEAVY);

			//int maxLight = 0;
			int maxMedium = 1;
			int maxHeavy = 1;
			
			WeightedRandomPicker<PatrolType> picker = new WeightedRandomPicker<PatrolType>();
			picker.add(PatrolType.HEAVY, maxHeavy - heavy); 
			picker.add(PatrolType.COMBAT, maxMedium - medium); 
			//picker.add(PatrolType.FAST, maxLight - light); 
			
			if (picker.isEmpty()) return;
			
			PatrolType type = picker.pick();
			PatrolFleetData custom = new PatrolFleetData(type);
			
			OptionalFleetData extra = new OptionalFleetData(market);
			extra.fleetType = type.getFleetType();
			
			RouteData route = RouteManager.getInstance().addRoute(sid, market, Misc.genRandomSeed(), extra, this, custom);
			float patrolDays = 35f + (float) Math.random() * 10f;
			
			route.addSegment(new RouteSegment(patrolDays, market.getPrimaryEntity()));
		}
	}
	
        @Override
	public void reportAboutToBeDespawnedByRouteManager(RouteData route) {
	}
	
        @Override
	public boolean shouldRepeat(RouteData route) {
		return false;
	}
	
	public int getCount(PatrolType ... types) {
		int count = 0;
		for (RouteData data : RouteManager.getInstance().getRoutesForSource(getRouteSourceId())) {
			if (data.getCustom() instanceof PatrolFleetData) {
				PatrolFleetData custom = (PatrolFleetData) data.getCustom();
				for (PatrolType type : types) {
					if (type == custom.type) {
						count++;
						break;
					}
				}
			}
		}
		return count;
	}

	public int getMaxPatrols(PatrolType type) {
		if (type == PatrolType.FAST) {
			return (int) market.getStats().getDynamic().getMod(Stats.PATROL_NUM_LIGHT_MOD).computeEffective(0);
		}
		if (type == PatrolType.COMBAT) {
			return (int) market.getStats().getDynamic().getMod(Stats.PATROL_NUM_MEDIUM_MOD).computeEffective(0);
		}
		if (type == PatrolType.HEAVY) {
			return (int) market.getStats().getDynamic().getMod(Stats.PATROL_NUM_HEAVY_MOD).computeEffective(0);
		}
		return 0;
	}
	
        @Override
	public boolean shouldCancelRouteAfterDelayCheck(RouteData route) {
		return false;
	}

        @Override
	public void reportBattleOccurred(CampaignFleetAPI fleet, CampaignFleetAPI primaryWinner, BattleAPI battle) {
		
	}

        @Override
	public void reportFleetDespawnedToListener(CampaignFleetAPI fleet, FleetDespawnReason reason, Object param) {
		if (!isFunctional()) return;
		
		if (reason == FleetDespawnReason.REACHED_DESTINATION) {
			RouteData route = RouteManager.getInstance().getRoute(getRouteSourceId(), fleet);
			if (route.getCustom() instanceof PatrolFleetData) {
				PatrolFleetData custom = (PatrolFleetData) route.getCustom();
				if (custom.spawnFP > 0) {
					float fraction  = fleet.getFleetPoints() / custom.spawnFP;
					returningPatrolValue += fraction;
				}
			}
		}
	}
	
        @Override
	public CampaignFleetAPI spawnFleet(RouteData route) {
		
		PatrolFleetData custom = (PatrolFleetData) route.getCustom();
		PatrolType type = custom.type;
		
		Random random = route.getRandom();
		
		float combat = 0f;
		float tanker = 0f;
		float freighter = 0f;
		String fleetType = type.getFleetType();
		switch (type) {
		case FAST:
			combat = Math.round(3f + (float) random.nextFloat() * 2f) * 5f;
			break;
		case COMBAT:
			combat = Math.round(12f + (float) random.nextFloat() * 5.6f) * 8f; //96-140
                        combat *= (Global.getSector().getClock().getCycle() <= 206 ? 1f : (Global.getSector().getClock().getCycle()-205>5) ? 2f : (Global.getSector().getClock().getCycle()-205)*0.5f);
			//tanker = Math.round((float) random.nextFloat()); //* 5f;
			break;
		case HEAVY:
			combat = Math.round(15f + (float) random.nextFloat() * 5.1f) * 10f; //150-200
                        combat *= (Global.getSector().getClock().getCycle() <= 206 ? 1f : (Global.getSector().getClock().getCycle()-205>5) ? 2f : (Global.getSector().getClock().getCycle()-205)*0.5f);
			break;
		}
		
		FleetParamsV3 params = new FleetParamsV3(
				market, 
				null, // loc in hyper; don't need if have market
				"ironsentinel",
				2f, // quality override route.getQualityOverride()
				fleetType,
				combat, // combatPts
				freighter, // freighterPts 
				tanker, // tankerPts
				0f, // transportPts
				0f, // linerPts
				0f, // utilityPts
				0f // qualityMod
				);
                
		params.timestamp = route.getTimestamp();
		params.random = random;
		params.modeOverride = ShipPickMode.PRIORITY_THEN_ALL;
                params.averageSMods = PlayerHasHowManySmods(2);
                params.minShipSize = 2;
                
                if (type == PatrolType.HEAVY) {}//params.flagshipVariantId = "eis_legion_xiv_Elite";/*params.commander = Global.getSector().getImportantPeople().getPerson("eiskimquy");*/}
                if (type == PatrolType.COMBAT) {}
		CampaignFleetAPI fleet = FleetFactoryV3.createFleet(params);
		
		if (fleet == null || fleet.isEmpty()) return null;
		
		fleet.setFaction("ironshell", true);
		fleet.setNoFactionInName(true);
		
		fleet.addEventListener(this);
		
//		PatrolAssignmentAIV2 ai = new PatrolAssignmentAIV2(fleet, custom);
//		fleet.addScript(ai);
		
		fleet.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_PATROL_FLEET, true);
		fleet.getMemoryWithoutUpdate().set(MemFlags.FLEET_IGNORES_OTHER_FLEETS, true, 0.3f);

		/*if (type == PatrolType.FAST || type == PatrolType.COMBAT) {
			fleet.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_CUSTOMS_INSPECTOR, true);
		}*/
		
		/*String postId = Ranks.POST_PATROL_COMMANDER;
		String rankId = Ranks.SPACE_COMMANDER;
		
		fleet.getCommander().setPostId(postId);
		fleet.getCommander().setRankId(rankId);*/
                if (type == PatrolType.HEAVY) {
                    if (!"eiskimquy".equals(fleet.getCommander().getId())) {
                        FleetMemberAPI currFlag = fleet.getFlagship();
                        currFlag.setFlagship(false);
                        FleetMemberAPI newFlagship = Global.getFactory().createFleetMember(FleetMemberType.SHIP, Global.getSettings().getVariant("eis_legion_xiv_Elite"));
                        fleet.getFleetData().addFleetMember(newFlagship);
                        newFlagship.setVariant(Global.getSettings().getVariant("eis_legion_xiv_Elite").clone(), false, true);
                        fleet.getFleetData().removeFleetMember(currFlag);
                        fleet.getFleetData().setFlagship(newFlagship);
                        newFlagship.setFlagship(true);
                        fleet.getFlagship().getVariant().setSource(VariantSource.REFIT);
                        fleet.getFlagship().getVariant().addMod("reinforcedhull");
                        fleet.getFlagship().getVariant().addPermaMod("reinforcedhull");
                        fleet.getFlagship().getVariant().addMod("missile_reload");
                        fleet.getFlagship().getVariant().addPermaMod("missile_reload");
                        fleet.getFlagship().getVariant().addTag("no_autofit");
                        fleet.getFlagship().getVariant().addTag("unboardable");
                        fleet.getFlagship().setShipName("EIS Hoan Kiem");
                        newFlagship.setCaptain(Global.getSector().getImportantPeople().getPerson("eiskimquy"));
                        fleet.setCommander(Global.getSector().getImportantPeople().getPerson("eiskimquy"));
                        newFlagship.getRepairTracker().setCR(newFlagship.getRepairTracker().getMaxCR());
                        fleet.getFleetData().sort();
                        fleet.forceSync();
                        /*fleet.setCommander(Global.getSector().getImportantPeople().getPerson("eiskimquy"));
                        fleet.getFlagship().setShipName("EIS Hoan Kiem");
                        fleet.getFlagship().setVariant(Global.getSettings().getVariant("eis_legion_xiv_Elite"), false, true);
                        fleet.getFlagship().setCaptain(Global.getSector().getImportantPeople().getPerson("eiskimquy"));*/
                    }
                }
                if (type == PatrolType.COMBAT) {
                    if (!"eisdarren".equals(fleet.getCommander().getId())) {
                        FleetMemberAPI currFlag = fleet.getFlagship();
                        currFlag.setFlagship(false);
                        FleetMemberAPI newFlagship = Global.getFactory().createFleetMember(FleetMemberType.SHIP, Global.getSettings().getVariant("eis_tyrant_standard").clone());
                        fleet.getFleetData().addFleetMember(newFlagship);
                        fleet.getFleetData().removeFleetMember(currFlag);
                        fleet.getFleetData().setFlagship(newFlagship);
                        newFlagship.setFlagship(true);
                        fleet.getFlagship().getVariant().setSource(VariantSource.REFIT);
                        //fleet.getFlagship().getVariant().addTag("no_autofit");
                        newFlagship.setCaptain(Global.getSector().getImportantPeople().getPerson("eisdarren"));
                        fleet.setCommander(Global.getSector().getImportantPeople().getPerson("eisdarren"));
                        newFlagship.getRepairTracker().setCR(newFlagship.getRepairTracker().getMaxCR());
                        fleet.getFleetData().sort();
                        fleet.forceSync();
                        /*ShipVariantAPI v = Global.getSettings().getVariant("eis_tyrant_standard");
                        ShipVariantAPI memberVariant = v.clone();
                        //memberVariant.setSource(VariantSource.REFIT);
                        fleet.getFlagship().setVariant(memberVariant, false, false);
                        fleet.getFlagship().setVariant(memberVariant, false, false);
                        fleet.getFlagship().getFleetData().setSyncNeeded();
                        fleet.setCommander(Global.getSector().getImportantPeople().getPerson("eisdarren"));
                        fleet.getFlagship().setCaptain(Global.getSector().getImportantPeople().getPerson("eisdarren"));*/
                    }
                }
		
		market.getContainingLocation().addEntity(fleet);
		fleet.setFacing((float) Math.random() * 360f);
		// this will get overridden by the patrol assignment AI, depending on route-time elapsed etc
		fleet.setLocation(market.getPrimaryEntity().getLocation().x, market.getPrimaryEntity().getLocation().y);
		
		fleet.addScript(new PatrolAssignmentAIV4(fleet, route));
		
		//market.getContainingLocation().addEntity(fleet);
		//fleet.setLocation(market.getPrimaryEntity().getLocation().x, market.getPrimaryEntity().getLocation().y);
		
		if (custom.spawnFP <= 0) {
			custom.spawnFP = fleet.getFleetPoints();
		}
		
		return fleet;
	}
	
	public String getRouteSourceId() {
		return getMarket().getId() + "_" + "ironsentinel";
	}

	@Override
	public boolean isAvailableToBuild() {
		return false;
	}
	
        @Override
	public boolean showWhenUnavailable() {
		return false;
	}

	@Override
	public boolean canImprove() {
		return true;
	}
        
        @Override
        public float getPatherInterest() {
            if (isImproved()) return -4f + super.getPatherInterest();
            else return super.getPatherInterest();
        }
	
        @Override
	protected void applyImproveModifiers() {
		/*String key = "ironshellhq_improve";
		if (isImproved()) {
                    market.getStats().getDynamic().getMod(Stats.PATROL_NUM_HEAVY_MOD).modifyFlat(key, IMPROVE_NUM_PATROLS_BONUS);
		} else {
                    market.getStats().getDynamic().getMod(Stats.PATROL_NUM_HEAVY_MOD).unmodifyFlat(key);
		}*/
	}
	
        @Override
	public void addImproveDesc(TooltipMakerAPI info, ImprovementDescriptionMode mode) {
		float opad = 10f;
		Color highlight = Misc.getHighlightColor();
		//String str = "" + (int) IMPROVE_NUM_PATROLS_BONUS;
		//String type = "heavy patrols";
                
		if (mode == ImprovementDescriptionMode.INDUSTRY_TOOLTIP) {
			info.addPara(Global.getSettings().getString("eis_ironshell", "eis_ironshellhq2"), 0f, highlight, "4");
		} else {
			info.addPara(Global.getSettings().getString("eis_ironshell", "eis_ironshellhq3"), 0f, highlight, "4");
		}

		info.addSpacer(opad);
		super.addImproveDesc(info, mode);
	}
	
	@Override
	public RaidDangerLevel adjustCommodityDangerLevel(String commodityId, RaidDangerLevel level) {
		return level.next();
	}

	@Override
	public RaidDangerLevel adjustItemDangerLevel(String itemId, String data, RaidDangerLevel level) {
		return level.next();
	}
	
}
