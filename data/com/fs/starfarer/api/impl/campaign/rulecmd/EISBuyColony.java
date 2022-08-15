package com.fs.starfarer.api.impl.campaign.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.ids.Strings;
import static com.fs.starfarer.api.impl.campaign.rulecmd.Nex_BuyColony.getValue;
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
            int value = (getValue(market, false, true).getModifiedInt() < 0 ? 0 : getValue(market, false, true).getModifiedInt());
            if (value > 0) {
                dialog.getTextPanel().addParagraph(Global.getSettings().getString("eis_ironshell", "EISGained") + Misc.getWithDGS(value) + Strings.C + "", Misc.getPositiveHighlightColor());
                dialog.getTextPanel().setFontSmallInsignia();
                dialog.getTextPanel().highlightInLastPara(Misc.getHighlightColor(), Misc.getWithDGS(value) + Strings.C);
                Global.getSector().getPlayerFleet().getCargo().getCredits().add(value);
                dialog.getTextPanel().setFontSmallInsignia();
            }
            setColonyPlayerOwned(market, true, dialog);
            //AddRemoveCommodity.addCreditsGainText(getValue(market, false, true).getModifiedInt(), dialog.getTextPanel());
            BuyColonyIntel intel = new BuyColonyIntel(market.getFactionId(), market);
            intel.init();
            if (dialog != null) Global.getSector().getIntelManager().addIntelToTextPanel(intel, dialog.getTextPanel());
            return true;
        }
}
