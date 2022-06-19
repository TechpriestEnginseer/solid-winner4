package com.fs.starfarer.api.impl.campaign.rulecmd;

import java.util.Map;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.econ.CommodityOnMarketAPI;
import com.fs.starfarer.api.campaign.econ.CommoditySpecAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.missions.hub.HubMissionWithBarEvent;
import com.fs.starfarer.api.util.Misc;

public class EISCheapCommodityMission extends HubMissionWithBarEvent {
	
	public static float MIN_BASE_VALUE = 10000;
	public static float MAX_BASE_VALUE = 250000;
	public static float BASE_PRICE_MULT = 0.8f;
        
	public static float ILLEGAL_QUANTITY_MULT = 0.7f;
	
	protected String commodityId;
	protected int quantity;
	protected int pricePerUnit;
	
	
	@Override
	protected boolean create(MarketAPI createdAt, boolean barEvent) {
		
		PersonAPI person = getPerson();
		if (person == null) return false;
		MarketAPI market = person.getMarket();
		if (market == null) return false;
		
		if (!setPersonMissionRef(person, "$cheapCom_ref")) {
			return false;
		}
                
		CommodityOnMarketAPI com = null;
		requireMarketIs(market);
		//requireMarketLocationNot(createdAt.getContainingLocation());
		requireCommodityIsNotPersonnel();
		requireCommodityDeficitAtMost(0);
		requireCommodityAvailableAtLeast(1);
		requireCommoditySurplusAtLeast(0);
                //preferCommodityIllegal();
                //preferCommodityLegal();
                
		com = pickCommodity();
		if (com == null) return false;
		
		commodityId = com.getId();
		
		float value = MIN_BASE_VALUE + (MAX_BASE_VALUE - MIN_BASE_VALUE) * getQuality();
		quantity = getRoundNumber(value / com.getCommodity().getBasePrice());
		if (com.isIllegal()) {
			quantity *= ILLEGAL_QUANTITY_MULT;
		}
		
		if (quantity < 10) quantity = 10;
                if (commodityId.equals(Commodities.FUEL)) {if (quantity > Global.getSector().getPlayerFleet().getCargo().getFreeFuelSpace()) {quantity = Global.getSector().getPlayerFleet().getCargo().getFreeFuelSpace();}}
                if (quantity > Global.getSector().getPlayerFleet().getCargo().getSpaceLeft()) {quantity = (int) Global.getSector().getPlayerFleet().getCargo().getSpaceLeft();}
		pricePerUnit = (int) (com.getMarket().getSupplyPrice(com.getId(), quantity, true) / (float) quantity * 
							  BASE_PRICE_MULT / getRewardMult());
		pricePerUnit = getRoundNumber(pricePerUnit);
		if (pricePerUnit < 2) pricePerUnit = 2;
		
		setRepFactionChangesVeryLow();
		setRepPersonChangesVeryLow();
		return true;
	}
	
        @Override
	protected void updateInteractionDataImpl() {
		set("$eis_cheapCom_ref2", this);
		
		set("$eis_cheapCom_commodityId", commodityId);
		set("$eis_cheapCom_commodityName", getSpec().getLowerCaseName());
		set("$eis_cheapCom_quantity", Misc.getWithDGS(quantity));
		set("$eis_cheapCom_pricePerUnit", Misc.getDGSCredits(pricePerUnit));
		set("$eis_cheapCom_totalPrice", Misc.getDGSCredits(pricePerUnit * quantity));
		set("$eis_cheapCom_manOrWoman", getPerson().getManOrWoman());
	}
	
	@Override
	public void accept(InteractionDialogAPI dialog, Map<String, MemoryAPI> memoryMap) {
			currentStage = new Object();
			abort();
	}
	
	protected transient CommoditySpecAPI spec;
	protected CommoditySpecAPI getSpec() {
		if (spec == null) {
			spec = Global.getSettings().getCommoditySpec(commodityId);
		}
		return spec;
	}
}

