package com.fs.starfarer.api.impl.campaign.rulecmd;

import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemKeys;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.util.Misc;
import java.util.List;
import java.util.Map;

/*
*	EIS_HasVariants <variantid> <variantid> ... <variantid>
*/

public class EIS_HasVariants extends BaseCommandPlugin {
	public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
		if (dialog == null) return false;
                List<String> startingVariants = (List<String>)memoryMap.get(MemKeys.LOCAL).get("$startShips_" + params.get(0).getString(memoryMap));
                boolean verificationfailed = false;
                if (startingVariants.size() != params.size()-1) {return verificationfailed;}
                for (int i = 1; i < params.size(); i++) {
                    if (!verificationfailed) {
                        if (startingVariants.get(i-1) == null) {verificationfailed = true;break;}
                        if (!params.get(i).getString(memoryMap).equals(startingVariants.get(i-1))) {verificationfailed = true;}
                    }
                }
		return !verificationfailed;
	}
}

