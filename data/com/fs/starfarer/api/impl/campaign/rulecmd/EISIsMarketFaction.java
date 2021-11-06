package com.fs.starfarer.api.impl.campaign.rulecmd;

import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.util.Misc.Token;
import java.util.List;
import java.util.Map;

/**
 * EISIsMarketFaction <faction id>
 * Totally not a copy of SWP's version.
 */
public class EISIsMarketFaction extends BaseCommandPlugin {

    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Token> params, final Map<String, MemoryAPI> memoryMap) {
        MarketAPI market = dialog.getInteractionTarget().getMarket();
        
        if (market == null) {
            return true;
        }

        String factionId = params.get(0).getString(memoryMap);
        return (market.getFaction().getId().contentEquals(factionId));
    }
}