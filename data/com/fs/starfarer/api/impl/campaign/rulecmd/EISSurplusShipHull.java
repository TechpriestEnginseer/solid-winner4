package com.fs.starfarer.api.impl.campaign.rulecmd;

import java.util.List;
import java.util.Map;
import java.util.Random;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.FactionAPI.ShipPickMode;
import com.fs.starfarer.api.campaign.FactionAPI.ShipPickParams;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.PersonImportance;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.fleet.ShipRolePick;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.HullMods;
import com.fs.starfarer.api.impl.campaign.ids.ShipRoles;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.impl.campaign.intel.bases.PirateBaseManager;
import com.fs.starfarer.api.impl.campaign.missions.hub.HubMissionWithSearch;
import com.fs.starfarer.api.loading.VariantSource;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Misc.Token;
import com.fs.starfarer.api.util.WeightedRandomPicker;

public class EISSurplusShipHull extends HubMissionWithSearch {

	public static float BASE_PRICE_MULT = 0.8f;
	
	protected FleetMemberAPI member;
	protected int price;
        private boolean holybased = false;
	
	@Override
	protected boolean create(MarketAPI createdAt, boolean barEvent) {
                holybased = false;
		PersonAPI person = getPerson();
		if (person == null) return false;
		MarketAPI market = person.getMarket();
		if (market == null) return false;
		
		//if (!Misc.isMilitary(market) && market.getSize() < 7) return false;
		
		if (!setPersonMissionRef(person, "$sShip_ref")) {
			return false;
		}
		
		//genRandom = Misc.random;
		
		
		ShipPickParams params = new ShipPickParams(ShipPickMode.PRIORITY_THEN_ALL);
		String role = pickRole(getQuality(), person.getFaction(), person.getImportance(), genRandom);
		
		ShipVariantAPI variant = null;
		for (int i = 0; i < 10; i++) {
			List<ShipRolePick> picks = market.getFaction().pickShip(role, params, null, genRandom);
			if (picks.isEmpty()) return false;
			String variantId = picks.get(0).variantId;
			variant = Global.getSettings().getVariant(variantId);
			variant = Global.getSettings().getVariant(variant.getHullSpec().getHullId() + "_Hull").clone();
                        /*if (!(variant.getHullSpec().hasTag("") || variant.getHullSpec().hasTag(""))) {
                            variant = null;
                            continue;
                        } pure laziness that I won't add yet...*/
			if (variant.getHullSpec().hasTag(Tags.NO_SELL)) {
				variant = null;
				continue;
			}
			break;
		}
		if (variant == null) return false;
			
		member = Global.getFactory().createFleetMember(FleetMemberType.SHIP, variant.clone());
		//assignShipName(member, Factions.INDEPENDENT);
                if (person.getTags().contains("eis_celeste") || genRandom.nextFloat() >= 0.25f) {
                    assignShipName(member, "ironshell");
                    if (!member.getVariant().hasHullMod(HullMods.SAFETYOVERRIDES) && !member.getVariant().hasHullMod("eis_aquila")) {holybased=true;member.getVariant().addPermaMod("eis_aquila", true);member.getVariant().setSource(VariantSource.REFIT);member.getVariant().addTag(Tags.VARIANT_ALWAYS_RETAIN_SMODS_ON_SALVAGE);}
                } else {
                    assignShipName(member, Factions.HEGEMONY);BASE_PRICE_MULT = 0.4f;
                }
                
                
		/*float quality = ShipQuality.getShipQuality(market, person.getFaction().getId());
		float averageDmods = DefaultFleetInflater.getAverageDmodsForQuality(quality);
		int addDmods = DefaultFleetInflater.getNumDModsToAdd(variant, averageDmods, genRandom);
		if (addDmods > 0) {
			DModManager.setDHull(variant);
			DModManager.addDMods(member, true, addDmods, genRandom);
		}*/
		member.getCrewComposition().setCrew(100000);
		member.getRepairTracker().setCR(0.7f);
		
		/*if (BASE_PRICE_MULT == 1f) {
			price = (int) Math.round(variant.getHullSpec().getBaseValue());
		} else {*/
			price = getRoundNumber(variant.getHullSpec().getBaseValue() * BASE_PRICE_MULT);
		//}
		
		setRepFactionChangesTiny();
		setRepPersonChangesVeryLow();
		
		return true;
	}
	
	protected void updateInteractionDataImpl() {
		// this is weird - in the accept() method, the mission is aborted, which unsets
		// $sShip_ref. So: we use $sShip_ref2 in the ContactPostAccept rule
		// and $sShip_ref2 has an expiration of 0, so it'll get unset on its own later.
		set("$sShip_ref2", this);
                set("$sShip_aquila", holybased);
		set("$sShip_hullSize", member.getHullSpec().getDesignation().toLowerCase());
		set("$sShip_hullClass", member.getHullSpec().getHullNameWithDashClass());
		set("$sShip_price", Misc.getWithDGS(price));
		set("$sShip_manOrWoman", getPerson().getManOrWoman());
		set("$sShip_rank", getPerson().getRank().toLowerCase());
		set("$sShip_rankAOrAn", getPerson().getRankArticle());
                set("$sShip_heOrShe", getPerson().getHeOrShe());
		set("$sShip_hisOrHer", getPerson().getHisOrHer());
		set("$sShip_member", member);
	}
	
	@Override
	protected boolean callAction(String action, String ruleId, InteractionDialogAPI dialog, List<Token> params,
							     Map<String, MemoryAPI> memoryMap) {
		if ("showShip".equals(action)) {
			dialog.getVisualPanel().showFleetMemberInfo(member, true);
			return true;
		} else if ("showPerson".equals(action)) {
			dialog.getVisualPanel().showPersonInfo(getPerson(), true);
			return true;
		}
		return false;
	}

	@Override
	public String getBaseName() {
		return "Surplus Ship Hull"; // not used I don't think
	}
	
	@Override
	public void accept(InteractionDialogAPI dialog, Map<String, MemoryAPI> memoryMap) {
		// it's just an transaction immediate transaction handled in rules.csv
		// no intel item etc
		
		currentStage = new Object(); // so that the abort() assumes the mission was successful
		abort();
	}
	
	public static String pickRole(float quality, FactionAPI faction, PersonImportance imp, Random random) {
		WeightedRandomPicker<String> picker = new WeightedRandomPicker<String>(random);
		
		float cycles = PirateBaseManager.getInstance().getDaysSinceStart() / 365f;
		
		if (imp == PersonImportance.VERY_HIGH && cycles < 3) imp = PersonImportance.HIGH;
		if (imp == PersonImportance.HIGH && cycles < 1) imp = PersonImportance.MEDIUM;
		
		if (quality < 0.5f && imp.ordinal() > PersonImportance.MEDIUM.ordinal()) {
			imp = PersonImportance.MEDIUM;
		}
		
		float w = faction.getDoctrine().getWarships() - 1f;
		float c = faction.getDoctrine().getCarriers() - 1f;
		//float p = faction.getDoctrine().getPhaseShips() - 1f;
		//if (w + c + p < 1) w = 1;
		if (w + c < 1) w = 1;
                
		switch (imp) {
		case VERY_LOW:
			picker.add(ShipRoles.COMBAT_SMALL, w);
			picker.add(ShipRoles.COMBAT_MEDIUM, w/1.5f);
			picker.add(ShipRoles.CARRIER_SMALL, c);
			//picker.add(ShipRoles.PHASE_SMALL, p);
			break;
		case LOW:
			picker.add(ShipRoles.COMBAT_SMALL, w/1.5f);
			picker.add(ShipRoles.COMBAT_MEDIUM, w);
			picker.add(ShipRoles.CARRIER_SMALL, c);
			//picker.add(ShipRoles.PHASE_SMALL, p);
			break;
		case MEDIUM:
                        picker.add(ShipRoles.COMBAT_SMALL, w/2f);
			picker.add(ShipRoles.COMBAT_MEDIUM, w/1.5f);
			picker.add(ShipRoles.COMBAT_LARGE, w);
			picker.add(ShipRoles.CARRIER_SMALL, c/1.5f);
			picker.add(ShipRoles.CARRIER_MEDIUM, c);
			//picker.add(ShipRoles.PHASE_SMALL, p/2f);
			//picker.add(ShipRoles.PHASE_MEDIUM, p);
			break;
		case HIGH:
                        picker.add(ShipRoles.COMBAT_SMALL, w/2.5f);
			picker.add(ShipRoles.COMBAT_MEDIUM, w/2f);
			picker.add(ShipRoles.COMBAT_LARGE, w);
			picker.add(ShipRoles.COMBAT_CAPITAL, w/1.5f);
			picker.add(ShipRoles.CARRIER_MEDIUM, c);
			picker.add(ShipRoles.CARRIER_LARGE, c/1.5f);
			//picker.add(ShipRoles.PHASE_MEDIUM, p);
			//picker.add(ShipRoles.PHASE_LARGE, p/2f);
			break;
		case VERY_HIGH:
                        picker.add(ShipRoles.COMBAT_SMALL, w/2.5f);
			picker.add(ShipRoles.COMBAT_MEDIUM, w/2f);
			picker.add(ShipRoles.COMBAT_LARGE, w/1.5f);
			picker.add(ShipRoles.COMBAT_CAPITAL, w);
			picker.add(ShipRoles.CARRIER_MEDIUM, c/2f);
			picker.add(ShipRoles.CARRIER_LARGE, c);
			//picker.add(ShipRoles.PHASE_MEDIUM, p/2f);
			//picker.add(ShipRoles.PHASE_LARGE, p);
			//picker.add(ShipRoles.PHASE_CAPITAL, p/2f);
			break;
		}
		return picker.pick();
	}
	
}

