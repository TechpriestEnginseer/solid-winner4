
package com.fs.starfarer.api.impl.campaign.rulecmd;

import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.util.Misc;
import java.util.List;
import java.util.Map;

/**
 * Check if they're greater than or less than a number exception for 100 for some weird erason
 * EISVibecheck <num> <num>
 */


public class EISVibeCheck extends BaseCommandPlugin {
    	public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
            if (dialog == null) return false;
            int deeznuts = params.get(0).getInt(memoryMap);
            int deeznuts1 = params.get(1).getInt(memoryMap);
            SectorEntityToken entity = dialog.getInteractionTarget();
            if (entity.getActivePerson() == null) return false;
            if (deeznuts1 == 100) {
                return entity.getActivePerson().getRelToPlayer().getRepInt() >= deeznuts && entity.getActivePerson().getRelToPlayer().getRepInt() <= deeznuts1;
            } else {
                return entity.getActivePerson().getRelToPlayer().getRepInt() >= deeznuts && entity.getActivePerson().getRelToPlayer().getRepInt() < deeznuts1;
            }
	}
}
