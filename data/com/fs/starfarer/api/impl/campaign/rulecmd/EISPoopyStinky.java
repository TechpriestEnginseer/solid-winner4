package com.fs.starfarer.api.impl.campaign.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.TextPanelAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.characters.MutableCharacterStatsAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.events.OfficerManagerEvent;
import com.fs.starfarer.api.impl.campaign.ids.Personalities;
import com.fs.starfarer.api.util.Misc;
import java.awt.Color;
import java.util.List;
import java.util.Map;
import org.lazywizard.lazylib.MathUtils;

/**
 * pooopy stinkyyyy
 *	EISPoopyStinky <boolean>
 */

public class EISPoopyStinky extends BaseCommandPlugin {
        public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
            if (params.get(0).getBoolean(memoryMap)) {
                final PersonAPI person = OfficerManagerEvent.createOfficer(Global.getSector().getFaction("ironshell"), 5, OfficerManagerEvent.SkillPickPreference.GENERIC, MathUtils.getRandom());
                person.setPersonality(Personalities.AGGRESSIVE);
                MutableCharacterStatsAPI stats = person.getStats();
                stats.setSkillLevel("eis_xiv", 1);
		TextPanelAPI text = dialog.getTextPanel();
		Color hl = Misc.getHighlightColor();
		text.addSkillPanel(person, false);
		text.setFontSmallInsignia();
		String personality = Misc.lcFirst(person.getPersonalityAPI().getDisplayName());
		text.addParagraph(Global.getSettings().getString("eis_ironshell", "EISOfficerPersonality") + personality + Global.getSettings().getString("eis_ironshell", "EISOfficerLevel") + stats.getLevel());
		text.highlightInLastPara(hl, personality, "" + stats.getLevel());
		text.addParagraph(person.getPersonalityAPI().getDescription());
		text.setFontInsignia();
                Global.getSector().getMemoryWithoutUpdate().set("$EISWowOfficer", person.getId(), 1);
                Global.getSector().getImportantPeople().addPerson(person);
            } else {
				MutableCharacterStatsAPI stats = dialog.getInteractionTarget().getActivePerson().getStats();
				TextPanelAPI text = dialog.getTextPanel();
				Color hl = Misc.getHighlightColor();
                                //text.beginTooltip().addSkillPanel(dialog.getInteractionTarget().getActivePerson(), 0);
                                //text.addTooltip();
                                text.addSkillPanel(dialog.getInteractionTarget().getActivePerson(), false);
                                //text.addSkillPanel(dialog.getInteractionTarget().getActivePerson(), true);
				text.setFontSmallInsignia();
				String personality = Misc.lcFirst(dialog.getInteractionTarget().getActivePerson().getPersonalityAPI().getDisplayName());
				text.addParagraph("Personality: " + personality + ", level: " + stats.getLevel());
				text.highlightInLastPara(hl, personality, "" + stats.getLevel());
				text.addParagraph(dialog.getInteractionTarget().getActivePerson().getPersonalityAPI().getDescription());
				text.setFontInsignia();
            }
            return true;
        }
    
}
