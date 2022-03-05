package com.fs.starfarer.api.impl.campaign.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.util.Misc;
import java.util.List;
import java.util.Map;

/*
*	EIS_HasFleetPoints <fleet points> <boolean>
*/


public class EIS_HasFleetPoints extends BaseCommandPlugin {
	public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
            if (dialog == null) return false;
            if (params.get(1).getBoolean(memoryMap)) {
               return (Global.getSector().getPlayerFleet().getFleetPoints() <= params.get(0).getInt(memoryMap));
            } else {
               return (Global.getSector().getPlayerFleet().getFleetPoints() >= params.get(0).getInt(memoryMap));
            }
	}
}

