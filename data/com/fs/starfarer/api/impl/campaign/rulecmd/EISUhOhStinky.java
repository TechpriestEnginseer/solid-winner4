package com.fs.starfarer.api.impl.campaign.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.ids.Ranks;
import com.fs.starfarer.api.util.Misc;
import java.util.List;
import java.util.Map;

/**
 * pooopy stinkyyyy
 *	EISUhOhStinky <string> <boolean>
 */

public class EISUhOhStinky extends BaseCommandPlugin {
        public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
            String personId = params.get(0).getString(memoryMap);
            boolean Yieks = params.get(1).getBoolean(memoryMap);

            if (personId != null) {
                if (Yieks) {
                    PersonAPI officer = Global.getSector().getImportantPeople().getPerson(personId);
                    Global.getSector().getPlayerFleet().getFleetData().addOfficer(officer);
                    officer.setPostId(Ranks.POST_OFFICER);
                    AddRemoveCommodity.addOfficerGainText(officer, dialog.getTextPanel());
                    Global.getSector().getImportantPeople().removePerson(personId);
                } else {
                    Global.getSector().getImportantPeople().removePerson(personId);
                }
            }
            return true;
        }
}
