package com.fs.starfarer.api.impl.campaign.rulecmd;


import java.util.List;
import java.util.Map;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.SpecialItemData;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Misc.Token;

/**
 * EISLearnSpecial <string> <string> <boolean>
 */

public class EISLearnSpecial extends BaseCommandPlugin {

	public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Token> params, Map<String, MemoryAPI> memoryMap) {
		if (dialog == null) return false;
                if (params.size() == 2) {return (Global.getSector().getPlayerFaction().knowsShip(params.get(1).getString(memoryMap)) || Global.getSector().getPlayerFleet().getCargo().getQuantity(CargoAPI.CargoItemType.SPECIAL, new SpecialItemData(params.get(0).getString(memoryMap), params.get(1).getString(memoryMap))) > 0);}
                if (params.size() == 3) {
                    if (Global.getSector().getPlayerFleet().getCargo().getQuantity(CargoAPI.CargoItemType.SPECIAL, new SpecialItemData(params.get(0).getString(memoryMap), params.get(1).getString(memoryMap))) > 0) {
                        Global.getSector().getPlayerFleet().getCargo().removeItems(CargoAPI.CargoItemType.SPECIAL, new SpecialItemData(params.get(0).getString(memoryMap), params.get(1).getString(memoryMap)), 1);
                        dialog.getTextPanel().addParagraph(Global.getSettings().getString("eis_ironshell", "EISLost") + Global.getSettings().getHullSpec(params.get(1).getString(memoryMap)).getHullName() + Global.getSettings().getString("eis_ironshell", "EISBlueprint"), Misc.getNegativeHighlightColor());
                    }
                    Global.getSector().getFaction("ironshell").getKnownShips().add(params.get(1).getString(memoryMap));
                    Global.getSector().getFaction("ironshell").getHullFrequency().put(params.get(1).getString(memoryMap), 1f);
                    Global.getSector().getFaction("ironsentinel").getKnownShips().add(params.get(1).getString(memoryMap));
                    Global.getSector().getFaction("ironsentinel").getHullFrequency().put(params.get(1).getString(memoryMap), 1f);
                    Global.getSector().getFaction("ironsentinel").getPriorityShips().add(params.get(1).getString(memoryMap));
                    if (Global.getSettings().getBoolean("GreaterHegemony")) {Global.getSector().getFaction("hegemony").getKnownShips().add(params.get(1).getString(memoryMap));Global.getSector().getFaction("hegemony").getHullFrequency().put(params.get(1).getString(memoryMap), 0.25f);}
                    if (params.get(2).getBoolean(memoryMap)) {
                        Global.getSector().getFaction("ironshell").getPriorityShips().add(params.get(1).getString(memoryMap));
                        if (Global.getSettings().getBoolean("GreaterHegemony")) {Global.getSector().getFaction("hegemony").getPriorityShips().add(params.get(1).getString(memoryMap));}
                    }
                    Global.getSector().getFaction("ironshell").clearShipRoleCache();
                    Global.getSector().getFaction("ironsentinel").clearShipRoleCache();
                    Global.getSector().getFaction("hegemony").clearShipRoleCache();
                }
                return false;
	}
}















