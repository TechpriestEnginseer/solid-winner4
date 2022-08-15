package com.fs.starfarer.api.impl.campaign.rulecmd;


import java.util.List;
import java.util.Map;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.loading.VariantSource;
import com.fs.starfarer.api.util.Misc.Token;

/**
 * Dude it's totally not a copy of Console Command's AddShip trust me!
 * EISAddShip <variant id> <boolean to keep smod>
 */

public class EISAddShip extends BaseCommandPlugin {

	public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Token> params, Map<String, MemoryAPI> memoryMap) {
		if (dialog == null) return false;
		String variant1 = params.get(0).getString(memoryMap);
                String name2 = Global.getSector().getFaction("ironshell").pickRandomShipName();
                FleetMemberAPI ship = Global.getFactory().createFleetMember(FleetMemberType.SHIP, Global.getSettings().getVariant(variant1).clone());
                ship.setShipName(name2);
                ShipVariantAPI variant = ship.getVariant();
                variant.setSource(VariantSource.REFIT);
                if (params.size() > 1 && params.get(1).getBoolean(memoryMap)) {
                    variant.addTag(Tags.VARIANT_ALWAYS_RETAIN_SMODS_ON_SALVAGE);
                }
		Global.getSector().getPlayerFleet().getFleetData().addFleetMember(ship);
                AddRemoveCommodity.addFleetMemberGainText(variant, dialog.getTextPanel());
		return true;
	}
}















