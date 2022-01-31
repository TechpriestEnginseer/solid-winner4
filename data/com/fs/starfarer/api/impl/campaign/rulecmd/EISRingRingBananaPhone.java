package com.fs.starfarer.api.impl.campaign.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Misc.Token;
import java.util.List;
import java.util.Map;

/**
 * EISRingRingBananaPhone <int>
 * If LY between first and 2nd is greater than int, return true else false.
 */
public class EISRingRingBananaPhone extends BaseCommandPlugin {

    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Token> params, final Map<String, MemoryAPI> memoryMap) {
        MarketAPI market = dialog.getInteractionTarget().getMarket();
        SectorEntityToken entity = dialog.getInteractionTarget();
        if (market == null) {return false;}
	if (entity == null) {return false;}
        if (Global.getSector().getPlayerFleet() == null || Global.getSector().getPlayerFleet().getLocationInHyperspace() == null) {return false;}
        return (Misc.getDistanceLY(Global.getSector().getPlayerFleet().getLocationInHyperspace(), entity.getLocationInHyperspace()) > params.get(0).getInt(memoryMap));
    }
}
