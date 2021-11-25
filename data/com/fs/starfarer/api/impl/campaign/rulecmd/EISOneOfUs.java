package com.fs.starfarer.api.impl.campaign.rulecmd;


import java.util.List;
import java.util.Map;

import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Misc.Token;

/**
 * Either checks if Hegemony or Iron Shell commissioned.
 * EISOneOfUs
 */

public class EISOneOfUs extends BaseCommandPlugin {
	public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Token> params, Map<String, MemoryAPI> memoryMap) {
            if (dialog == null) return false;
            return ("hegemony".equals(Misc.getCommissionFactionId()) || "ironshell".equals(Misc.getCommissionFactionId()));
        }
}