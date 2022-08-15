package com.fs.starfarer.api.impl.campaign.rulecmd;

import java.util.List;
import java.util.Map;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemKeys;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Misc.Token;

/**
 *	EISTaxes
 */
public class EISTaxes extends BaseCommandPlugin {

	public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Token> params, Map<String, MemoryAPI> memoryMap) {
		if (dialog == null) return false;
		
		float credits = Global.getSector().getPlayerFleet().getCargo().getCredits().get();
                float taxes = Global.getSector().getMemoryWithoutUpdate().getFloat("$EIS_taxburden");
                if (taxes >= credits) {taxes = credits;}
                
		memoryMap.get(MemKeys.LOCAL).set("$EIS_taxes", (int)taxes, 0);
		memoryMap.get(MemKeys.LOCAL).set("$EIS_taxesDGS", Misc.getDGSCredits(Math.abs(taxes)), 0);
                if (Global.getSector().getMemoryWithoutUpdate().getFloat("$EIS_MiscTax") > 0f) {
                    memoryMap.get(MemKeys.LOCAL).set("$EIS_MiscTaxes", "-"+Misc.getDGSCredits(Math.abs(Global.getSector().getMemoryWithoutUpdate().getFloat("$EIS_MiscTax"))), 0);
                } else {
                    memoryMap.get(MemKeys.LOCAL).set("$EIS_MiscTaxes", Misc.getDGSCredits(Math.abs(Global.getSector().getMemoryWithoutUpdate().getFloat("$EIS_MiscTax"))), 0);
                }
                memoryMap.get(MemKeys.LOCAL).set("$EIS_BMTaxes", Misc.getDGSCredits(Math.abs(Global.getSector().getMemoryWithoutUpdate().getFloat("$EIS_BMTax"))), 0);
                memoryMap.get(MemKeys.LOCAL).set("$EIS_OMTaxes", Misc.getDGSCredits(Math.abs(Global.getSector().getMemoryWithoutUpdate().getFloat("$EIS_OMTax"))), 0);
                memoryMap.get(MemKeys.LOCAL).set("$EIS_BMHISTaxes", Misc.getDGSCredits(Math.abs(Global.getSector().getMemoryWithoutUpdate().getFloat("$EIS_BMHISTax"))), 0);
                memoryMap.get(MemKeys.LOCAL).set("$EIS_PayrollTaxes", Misc.getDGSCredits(Math.abs(Global.getSector().getMemoryWithoutUpdate().getFloat("$EIS_PayrollTax"))), 0);
                memoryMap.get(MemKeys.LOCAL).set("$EIS_WealthTaxes", Misc.getDGSCredits(Math.abs(Global.getSector().getMemoryWithoutUpdate().getFloat("$EIS_WealthTax"))), 0); 
                
		return (taxes >= 0);
	}

}
