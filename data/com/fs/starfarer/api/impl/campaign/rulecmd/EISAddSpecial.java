package com.fs.starfarer.api.impl.campaign.rulecmd;


import java.util.List;
import java.util.Map;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.SpecialItemData;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.util.Misc.Token;

/**
 * EISAddSpecial <spec id> <data>
 * Totally not a copy of Console Command's version
 */

public class EISAddSpecial extends BaseCommandPlugin {

	public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Token> params, Map<String, MemoryAPI> memoryMap) {
		if (dialog == null) return false;
		String id1 = params.get(0).getString(memoryMap);
                if (params.size() > 1) {String data2 = params.get(1).getString(memoryMap);
		Global.getSector().getPlayerFleet().getCargo().addSpecial(new SpecialItemData(id1, data2), 1f);}
                else {Global.getSector().getPlayerFleet().getCargo().addSpecial(new SpecialItemData(id1, null), 1f);}
		return true;
	}
}















