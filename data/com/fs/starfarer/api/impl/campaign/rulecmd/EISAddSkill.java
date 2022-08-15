package com.fs.starfarer.api.impl.campaign.rulecmd;


import java.util.List;
import java.util.Map;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Misc.Token;

/**
 * EISAddSkill <string> <boolean> <int>
 */

public class EISAddSkill extends BaseCommandPlugin {

	public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Token> params, Map<String, MemoryAPI> memoryMap) {
		if (dialog == null) return false;
                if (params.size() < 2) {return Global.getSector().getPlayerPerson().getStats().getSkillLevel(params.get(0).getString(memoryMap)) > 0;}
                if (params.size() < 3 && params.get(1).getBoolean(memoryMap)) {Global.getSector().getPlayerPerson().getStats().setSkillLevel(params.get(0).getString(memoryMap), Global.getSector().getPlayerPerson().getStats().getSkillLevel(params.get(0).getString(memoryMap)) >= 2 ? 2 : Global.getSector().getPlayerPerson().getStats().getSkillLevel(params.get(0).getString(memoryMap))+1);
                PersonAPI person = Global.getFactory().createPerson();
                person.setFaction(dialog.getInteractionTarget().getFaction().getId());
                person.getStats().setSkillLevel(params.get(0).getString(memoryMap), Global.getSector().getPlayerPerson().getStats().getSkillLevel(params.get(0).getString(memoryMap)));
                dialog.getTextPanel().addSkillPanel(person, false);
                dialog.getTextPanel().setFontSmallInsignia();
                dialog.getTextPanel().addParagraph(Global.getSettings().getString("eis_ironshell", "eis_xivrulescsv") + Global.getSettings().getSkillSpec(params.get(0).getString(memoryMap)).getName() + (Global.getSector().getPlayerPerson().getStats().getSkillLevel(params.get(0).getString(memoryMap)) >= 2 ? Global.getSettings().getString("eis_ironshell", "eis_xivrulescsv2") : ""), Misc.getPositiveHighlightColor());
                dialog.getTextPanel().highlightInLastPara(Misc.getHighlightColor(), Global.getSettings().getSkillSpec(params.get(0).getString(memoryMap)).getName(), Global.getSettings().getString("eis_ironshell", "eis_xivrulescsv2"));
		dialog.getTextPanel().setFontInsignia();return true;} else {
                Global.getSector().getPlayerPerson().getStats().setSkillLevel(params.get(0).getString(memoryMap), 0);
                }
		return false;
	}
}















