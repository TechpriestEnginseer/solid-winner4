package com.fs.starfarer.api.impl.campaign.rulecmd;

import java.util.List;
import java.util.Map;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.util.Misc.Token;

/**
 *	EISTaxesFiled <float> <boolean>
 */

public class EISTaxesFiled extends BaseCommandPlugin {

    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Token> params, Map<String, MemoryAPI> memoryMap) {
            if (dialog == null) return false;
            if (params.get(1).getBoolean(memoryMap)) {
                Global.getSector().getMemoryWithoutUpdate().set("$EIS_taxburden", Global.getSector().getMemoryWithoutUpdate().getFloat("$EIS_taxburden")-params.get(0).getFloat(memoryMap));
                Global.getSector().getMemoryWithoutUpdate().set("$EIS_MiscTax", Global.getSector().getMemoryWithoutUpdate().getFloat("$EIS_MiscTax")-params.get(0).getFloat(memoryMap));
            } else {
                Global.getSector().getMemoryWithoutUpdate().set("$EIS_taxburden", Global.getSector().getMemoryWithoutUpdate().getFloat("$EIS_taxburden")-params.get(0).getFloat(memoryMap));
                Global.getSector().getMemoryWithoutUpdate().set("$EIS_BMTax", 0f);
                Global.getSector().getMemoryWithoutUpdate().set("$EIS_OMTax", 0f);
                Global.getSector().getMemoryWithoutUpdate().set("$EIS_BMHISTax", 0f);
                Global.getSector().getMemoryWithoutUpdate().set("$EIS_PayrollTax", 0f);
                Global.getSector().getMemoryWithoutUpdate().set("$EIS_WealthTax", 0f);
                //We will overlook.. a few credits or two. :)
                if (Global.getSector().getMemoryWithoutUpdate().getFloat("$EIS_taxburden") >= 365f) {
                    Global.getSector().getMemoryWithoutUpdate().set("$EIS_MiscTax", Global.getSector().getMemoryWithoutUpdate().getFloat("$EIS_MiscTax")+Global.getSector().getMemoryWithoutUpdate().getFloat("$EIS_taxburden"));
                } else {
                    Global.getSector().getMemoryWithoutUpdate().set("$EIS_MiscTax", 0f);
                }
            }
            return true;
    }
}
