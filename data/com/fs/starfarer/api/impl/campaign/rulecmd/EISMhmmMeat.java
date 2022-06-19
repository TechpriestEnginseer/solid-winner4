package com.fs.starfarer.api.impl.campaign.rulecmd;

import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemKeys;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.util.Misc;
import java.util.List;
import java.util.Map;

/**
 * Check both local and whatever it is you were looking for...
 * EISMhmmMeat <memkey> <MemKeys>
 * WILL not nullcheck
 */



public class EISMhmmMeat extends BaseCommandPlugin {
	public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
            if (dialog == null) return false;
            if (memoryMap.get(MemKeys.LOCAL).get(params.get(0).getString(memoryMap)) != null) {
                return memoryMap.get(MemKeys.LOCAL).getBoolean(params.get(0).getString(memoryMap));
            }
            if (memoryMap.get(params.get(1).getString(memoryMap)).get(params.get(0).getString(memoryMap)) != null) {
                return memoryMap.get(params.get(1).getString(memoryMap)).getBoolean(params.get(0).getString(memoryMap));
            }
            return false;
        }
}
