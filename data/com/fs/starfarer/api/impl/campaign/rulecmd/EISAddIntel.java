
package com.fs.starfarer.api.impl.campaign.rulecmd;

import java.util.List;
import java.util.Map;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.intel.misc.LivingInPods;
import com.fs.starfarer.api.util.Misc.Token;

public class EISAddIntel extends BaseCommandPlugin {
	public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Token> params, Map<String, MemoryAPI> memoryMap) {
		LivingInPods intel = null;
		/*for (IntelInfoPlugin iip : Global.getSector().getIntelManager().getIntel(LivingInPods.class)) {
			LivingInPods curr = (LivingInPods) iip;
			if (curr. == entity) {
				intel = curr;
				break;
			}
		}*/
		if (intel == null && params.get(0).getString(memoryMap) != null) {
			intel = new LivingInPods(Global.getSector().getEntityById(params.get(0).getString(memoryMap)), params.get(1).getString(memoryMap), params.get(2).getString(memoryMap), params.get(3).getString(memoryMap), params.get(4).getString(memoryMap), params.get(5).getString(memoryMap), params.get(6).getString(memoryMap));
			Global.getSector().getIntelManager().addIntel(intel, false, dialog.getTextPanel());
                        Global.getSector().addScript(intel);
		}
            return true;
	}
}