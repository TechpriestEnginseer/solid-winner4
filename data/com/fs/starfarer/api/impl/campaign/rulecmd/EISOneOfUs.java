package com.fs.starfarer.api.impl.campaign.rulecmd;


import java.util.List;
import java.util.Map;

import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Misc.Token;
import exerelin.campaign.AllianceManager;
import exerelin.campaign.DiplomacyManager;

/**
 * Either checks if Hegemony or Iron Shell commissioned.
 * EISOneOfUs
 */

public class EISOneOfUs extends BaseCommandPlugin {
	public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Token> params, Map<String, MemoryAPI> memoryMap) {
            if (dialog == null) return false;
            
            if (!DiplomacyManager.isRandomFactionRelationships() || (AllianceManager.getFactionAlliance("hegemony") == AllianceManager.getFactionAlliance("ironshell"))) return ("hegemony".equals(Misc.getCommissionFactionId()) || "ironshell".equals(Misc.getCommissionFactionId()) || AllianceManager.getPlayerAlliance(true) == AllianceManager.getFactionAlliance("ironshell"));
            if (!params.isEmpty()) {return (AllianceManager.getPlayerAlliance(true) == AllianceManager.getFactionAlliance(params.get(0).getString(memoryMap)));}
            return ("ironshell".equals(Misc.getCommissionFactionId()) || AllianceManager.getPlayerAlliance(true) == AllianceManager.getFactionAlliance("ironshell"));
        }
}