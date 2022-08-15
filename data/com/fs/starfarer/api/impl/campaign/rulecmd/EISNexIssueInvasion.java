package com.fs.starfarer.api.impl.campaign.rulecmd;


import java.util.List;
import java.util.Map;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.intel.BaseMissionIntel;
import com.fs.starfarer.api.util.Misc.Token;
import exerelin.campaign.DiplomacyManager;
import exerelin.campaign.intel.missions.ConquestMissionIntel;

/*

EISNexIssueInvasion <string: udana_stations> <float: days>

*/

public class EISNexIssueInvasion extends BaseCommandPlugin {
	public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Token> params, Map<String, MemoryAPI> memoryMap) {
            if (Global.getSettings().getModManager().isModEnabled("nexerelin")) {
                if (Global.getSector().getEntityById(params.get(0).getString(memoryMap)) != null && !Global.getSector().getEntityById(params.get(0).getString(memoryMap)).getMarket().getFactionId().equals("ironshell")) {
                    //Global.getSector().getFaction("ironshell").setRelationship("shadow_industry", -1f);
                    //Global.getSector().getPlayerFaction().setRelationship("shadow_industry", -1f);
                    DiplomacyManager.createDiplomacyEvent(Global.getSector().getFaction("ironshell"), Global.getSector().getEntityById(params.get(0).getString(memoryMap)).getMarket().getFaction(), "declare_war", null);
                    DiplomacyManager.createDiplomacyEvent(Global.getSector().getFaction("hegemony"), Global.getSector().getEntityById(params.get(0).getString(memoryMap)).getMarket().getFaction(), "declare_war", null);
                    ConquestMissionIntel intel = new ConquestMissionIntel(Global.getSector().getEntityById(params.get(0).getString(memoryMap)).getMarket(), Global.getSector().getFaction("ironshell"), params.get(1).getFloat(memoryMap));
                    Global.getSector().addScript(intel);
                    intel.init();
                    intel.setMissionState(BaseMissionIntel.MissionState.ACCEPTED);
                    Global.getSector().getIntelManager().addIntelToTextPanel(intel, dialog.getTextPanel());
                }
            }
            return true;
        }
}