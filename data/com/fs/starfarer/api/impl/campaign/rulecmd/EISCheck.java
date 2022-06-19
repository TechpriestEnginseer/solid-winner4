package com.fs.starfarer.api.impl.campaign.rulecmd;


import java.util.List;
import java.util.Map;

import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.FleetInteractionDialogPluginImpl.OptionId;
import com.fs.starfarer.api.util.Misc.Token;

/**
 * EISCheck
 */

public class EISCheck extends BaseCommandPlugin {
	public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Token> params, Map<String, MemoryAPI> memoryMap) {
            if (dialog == null) return false;
            dialog.getOptionPanel().setEnabled(OptionId.CLEAN_DISENGAGE, false);
            dialog.getOptionPanel().setTooltip(OptionId.CLEAN_DISENGAGE, "Look at the child trying to run away from responsibility. Own up to it.");
            return true;
        }
}