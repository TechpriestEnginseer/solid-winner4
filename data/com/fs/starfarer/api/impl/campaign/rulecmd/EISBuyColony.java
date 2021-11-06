package com.fs.starfarer.api.impl.campaign.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import static com.fs.starfarer.api.impl.campaign.rulecmd.Nex_BuyColony.setColonyPlayerOwned;
import com.fs.starfarer.api.util.Misc;
import java.util.List;
import java.util.Map;
import exerelin.campaign.intel.BuyColonyIntel;

/*
EISBuyColony <string>
*/

public class EISBuyColony extends BaseCommandPlugin {
	public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
            MarketAPI market = Global.getSector().getEntityById(params.get(0).getString(memoryMap)).getMarket();
            setColonyPlayerOwned(market, true, dialog);
            BuyColonyIntel intel = new BuyColonyIntel(market.getFactionId(), market);
            intel.init();
            if (dialog != null) Global.getSector().getIntelManager().addIntelToTextPanel(intel, dialog.getTextPanel());
            return true;
        }
    
}
