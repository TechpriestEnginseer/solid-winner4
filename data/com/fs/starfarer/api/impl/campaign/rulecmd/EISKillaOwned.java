package com.fs.starfarer.api.impl.campaign.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
//import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.util.Misc;
import java.util.List;
import java.util.Map;

public class EISKillaOwned extends BaseCommandPlugin {
	public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
            if (params.size() == 1) {return Global.getSector().getEntityById(params.get(0).getString(memoryMap)) != null;}
            return (Global.getSector().getEntityById(params.get(0).getString(memoryMap)) != null && Global.getSector().getEntityById(params.get(0).getString(memoryMap)).getMarket().getFactionId().equals(params.get(1).getString(memoryMap)));
            //return (Global.getSector().getEntityById("killa") != null && Global.getSector().getEntityById("killa").getMarket().getFactionId().equals(Factions.PLAYER));
        }
}