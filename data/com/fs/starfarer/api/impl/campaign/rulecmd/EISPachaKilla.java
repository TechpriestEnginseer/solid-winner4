package com.fs.starfarer.api.impl.campaign.rulecmd;


import java.util.List;
import java.util.Map;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.comm.IntelInfoPlugin;
import exerelin.campaign.intel.colony.ColonyExpeditionIntel;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.util.Misc.Token;

/**
 * pleased about ownership
 * EISPachaKilla 
 */

public class EISPachaKilla extends BaseCommandPlugin {
	public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Token> params, Map<String, MemoryAPI> memoryMap) {
            
            List<IntelInfoPlugin> intels =  Global.getSector().getIntelManager().getIntel(ColonyExpeditionIntel.class);
            for (IntelInfoPlugin intel : intels) {
              ColonyExpeditionIntel colonyIntel = (ColonyExpeditionIntel) intel;
              if ((colonyIntel.getTarget().getId().equals("market_hanan_pacha") && colonyIntel.getTarget().getFactionId().equals("ironshell")) || (Global.getSector().getEntityById("hanan_pacha").getMarket().getFactionId().equals("ironshell")))
                  return true;
            }
            return false;
        }
}