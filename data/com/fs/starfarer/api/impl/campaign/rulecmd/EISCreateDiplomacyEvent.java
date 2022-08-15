package com.fs.starfarer.api.impl.campaign.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.util.Misc;
import exerelin.campaign.DiplomacyManager;
import java.util.List;
import java.util.Map;

/*
EISCreateDiplomacyEvent <faction string> <otherfaction string> <event id>
*/

public class EISCreateDiplomacyEvent extends BaseCommandPlugin {
	public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
            DiplomacyManager.createDiplomacyEvent(Global.getSector().getFaction(params.get(0).getString(memoryMap)), Global.getSector().getFaction(params.get(1).getString(memoryMap)), params.get(2).getString(memoryMap), null);
            return true;
        }
}
