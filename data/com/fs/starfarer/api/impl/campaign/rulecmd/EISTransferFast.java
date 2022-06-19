package com.fs.starfarer.api.impl.campaign.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.util.Misc;
import exerelin.campaign.SectorManager;
import java.util.List;
import java.util.Map;

/**
 * EISTransferFast <entity string> <faction string>
 */

public class EISTransferFast extends BaseCommandPlugin {
    	public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
            if (Global.getSettings().getModManager().isModEnabled("nexerelin")) {
                SectorManager.transferMarket(Global.getSector().getEntityById(params.get(0).getString(memoryMap)).getMarket(), Global.getSector().getFaction(params.get(1).getString(memoryMap)), Global.getSector().getFaction(Factions.PLAYER), false, false, null, 0);
            }
            return true;
        }
    
}
