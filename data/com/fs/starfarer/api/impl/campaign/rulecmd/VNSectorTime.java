package com.fs.starfarer.api.impl.campaign.rulecmd;

import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.campaign.rules.MemKeys;
import com.fs.starfarer.api.util.Misc;
import java.util.List;
import java.util.Map;

public class VNSectorTime extends BaseCommandPlugin {
	public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
		if (dialog == null) return false;
                float based = memoryMap.get(MemKeys.LOCAL).getExpire("$VNS_Interacted");
		String days = Misc.getAtLeastStringForDays((int) based);
                memoryMap.get(MemKeys.LOCAL).set("$VNS_InteractedDGS", days.toLowerCase(), 0);
		return true;
	}

}

