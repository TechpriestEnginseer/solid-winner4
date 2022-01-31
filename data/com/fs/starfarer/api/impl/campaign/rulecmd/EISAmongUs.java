package com.fs.starfarer.api.impl.campaign.rulecmd;


import java.util.List;
import java.util.Map;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.comm.IntelInfoPlugin;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.util.Misc.Token;
import exerelin.campaign.intel.missions.ConquestMissionIntel;

/**
 * pleased about ownership
 * EISAmongUs <string>
 */

public class EISAmongUs extends BaseCommandPlugin {
	public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Token> params, Map<String, MemoryAPI> memoryMap) {
            if (dialog == null) return false;
            String sus = params.get(0).getString(memoryMap);
            List<IntelInfoPlugin> intels =  Global.getSector().getIntelManager().getIntel(ConquestMissionIntel.class);
            for (IntelInfoPlugin intel : intels) {
              ConquestMissionIntel conquestIntel = (ConquestMissionIntel) intel;
              if (conquestIntel.getPostingLocation() != null && conquestIntel.getFactionForUIColors() != null) {
                if (sus.equals(conquestIntel.getPostingLocation().getId()) && 
                "ironshell".equals(conquestIntel.getFactionForUIColors().getId()) && 
                conquestIntel.isCompleted())
                    return true;
              }
            }
            return false;
        }
}