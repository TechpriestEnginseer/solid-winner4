package com.fs.starfarer.api.impl.campaign.rulecmd;

import java.util.List;
import java.util.Map;

import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemKeys;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Misc.Token;

/**
 * EISCalculatePercentage <local memory string> <float> <float>
 */
public class EISCalculatePercentage extends BaseCommandPlugin {

	public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Token> params, Map<String, MemoryAPI> memoryMap) {
		if (dialog == null) return false;
                if (Math.signum(params.get(1).getFloat(memoryMap)) == Math.signum(params.get(2).getFloat(memoryMap))) {
                    memoryMap.get(MemKeys.LOCAL).set(params.get(0).getString(memoryMap), params.get(1).getFloat(memoryMap)/params.get(2).getFloat(memoryMap) < 1 ? Misc.getRoundedValueMaxOneAfterDecimal(params.get(1).getFloat(memoryMap)/params.get(2).getFloat(memoryMap) * 100f)+"%" : "100%", 0);
                    memoryMap.get(MemKeys.LOCAL).set(params.get(0).getString(memoryMap)+"lol", params.get(1).getFloat(memoryMap)/params.get(2).getFloat(memoryMap) < 1 ? Misc.getRoundedValueFloat(params.get(1).getFloat(memoryMap)/params.get(2).getFloat(memoryMap)) : 1, 0);
                } else if (Math.signum(params.get(1).getFloat(memoryMap)) != Math.signum(params.get(2).getFloat(memoryMap))) {
                    memoryMap.get(MemKeys.LOCAL).set(params.get(0).getString(memoryMap), params.get(1).getFloat(memoryMap) > 0 ? "100%" : "0%", 0);
                    memoryMap.get(MemKeys.LOCAL).set(params.get(0).getString(memoryMap)+"lol", params.get(1).getFloat(memoryMap) > 0 ? 100 : 0, 0);
                }
		return true;
	}

}
