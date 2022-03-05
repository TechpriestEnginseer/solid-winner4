package com.fs.starfarer.api.impl.campaign.rulecmd;

import java.util.List;
import java.util.Map;

import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.SpecialItemData;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.util.Misc.Token;

// EISCheckItems <industry id> <special item id>
// Only checks for downgrade and upgrade
// Doesn't really check beyond that because.. lol.

public class EISCheckItems extends BaseCommandPlugin {
	public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Token> params, Map<String, MemoryAPI> memoryMap) {
                if (dialog.getInteractionTarget() != null && dialog.getInteractionTarget().getMarket() != null) {
                    if (dialog.getInteractionTarget().getMarket().getIndustry(params.get(0).getString(memoryMap)) == null) {
                        if (dialog.getInteractionTarget().getMarket().getIndustry(dialog.getInteractionTarget().getMarket().getIndustry(params.get(0).getString(memoryMap)).getSpec().getUpgrade()) != null) {
                            return dialog.getInteractionTarget().getMarket().getIndustry(dialog.getInteractionTarget().getMarket().getIndustry(params.get(0).getString(memoryMap)).getSpec().getUpgrade()).wantsToUseSpecialItem(new SpecialItemData(params.get(1).getString(memoryMap), null));
                        }
                        if (dialog.getInteractionTarget().getMarket().getIndustry(dialog.getInteractionTarget().getMarket().getIndustry(params.get(0).getString(memoryMap)).getSpec().getDowngrade()) != null) {
                            return dialog.getInteractionTarget().getMarket().getIndustry(dialog.getInteractionTarget().getMarket().getIndustry(params.get(0).getString(memoryMap)).getSpec().getDowngrade()).wantsToUseSpecialItem(new SpecialItemData(params.get(1).getString(memoryMap), null));
                        }
                    } else {
                        return dialog.getInteractionTarget().getMarket().getIndustry(params.get(0).getString(memoryMap)).wantsToUseSpecialItem(new SpecialItemData(params.get(1).getString(memoryMap), null));
                    }
                }
            return false;
	}
}