package com.fs.starfarer.api.impl.campaign.rulecmd;


import java.util.List;
import java.util.Map;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.util.Misc.Token;

/**
 * https://fractalsoftworks.com/forum/index.php?topic=5061.msg337296
 * EISMegamind2 <id> <memkey> <also cheats as a setter too>
 */

public class EISMegamind2 extends BaseCommandPlugin {
	public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Token> params, Map<String, MemoryAPI> memoryMap) {
            if (dialog == null) return false;
            if (Global.getSector().getEntityById(params.get(0).getString(memoryMap)) != null && params.size() > 2) {Global.getSector().getEntityById(params.get(0).getString(memoryMap)).getMemoryWithoutUpdate().set(params.get(1).getString(memoryMap), params.get(2).getBoolean(memoryMap));return true;}
            if (Global.getSector().getEntityById(params.get(0).getString(memoryMap)) != null) {return Global.getSector().getEntityById(params.get(0).getString(memoryMap)).getMemoryWithoutUpdate().getBoolean(params.get(1).getString(memoryMap));}
            return false;
        }
}