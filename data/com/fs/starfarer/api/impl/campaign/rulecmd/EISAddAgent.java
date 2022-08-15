package com.fs.starfarer.api.impl.campaign.rulecmd;


import java.util.List;
import java.util.Map;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.ids.Ranks;
import com.fs.starfarer.api.util.Misc.Token;
import exerelin.campaign.CovertOpsManager;
import exerelin.campaign.intel.agents.AgentIntel;
import java.util.Locale;

/**
 * Dude it's totally not a copy of Nexerelin's AddAgent trust me!
 * EISAddAgent <level> <role>
 */

public class EISAddAgent extends BaseCommandPlugin {

	public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Token> params, Map<String, MemoryAPI> memoryMap) {
		if (dialog == null || dialog.getInteractionTarget() == null || dialog.getInteractionTarget().getMarket() == null || CovertOpsManager.getManager().getAgents().size() >= CovertOpsManager.getManager().getMaxAgents().getModifiedValue()) return false;
		if (params.size() == 1) {
                    dialog.getInteractionTarget().getActivePerson().getMemoryWithoutUpdate().set("$eis_agent_salary", AgentIntel.getSalary(params.get(0).getInt(memoryMap)), 0);
                    dialog.getInteractionTarget().getActivePerson().getMemoryWithoutUpdate().set("$eis_agent_hiringbonus", AgentIntel.getSalary(params.get(0).getInt(memoryMap))*4, 0);
                }
                if (params.size() > 1) {PersonAPI agent = Global.getSector().getFaction("ironshell").createRandomPerson();
		agent.setRankId(Ranks.AGENT);
		agent.setPostId(Ranks.POST_AGENT);
		AgentIntel intel = new AgentIntel(agent, Global.getSector().getPlayerFaction(), params.get(0).getInt(memoryMap));
                intel.addSpecialization(AgentIntel.Specialization.valueOf(params.get(1).getString(memoryMap).toUpperCase(Locale.ENGLISH)));
		intel.setMarket(dialog.getInteractionTarget().getMarket());
		intel.init();
                Global.getSector().getIntelManager().addIntelToTextPanel(intel, dialog.getTextPanel());}
		return true;
	}
}















