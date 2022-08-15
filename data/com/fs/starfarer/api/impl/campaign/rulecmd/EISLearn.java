package com.fs.starfarer.api.impl.campaign.rulecmd;

import java.util.List;
import java.util.Map;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Misc.Token;


public class EISLearn extends BaseCommandPlugin {

	public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Token> params, final Map<String, MemoryAPI> memoryMap) {
		if (dialog == null) return false;
                if (!Global.getSector().getCharacterData().knowsHullMod(params.get(0).getString(memoryMap))) {Global.getSector().getCharacterData().addHullMod(params.get(0).getString(memoryMap));dialog.getTextPanel().setFontSmallInsignia();dialog.getTextPanel().addPara(Global.getSettings().getString("eis_ironshell", "EISLearnHullmod")+Global.getSettings().getHullModSpec(params.get(0).getString(memoryMap)).getDisplayName(), Misc.getHighlightColor());dialog.getTextPanel().setFontInsignia();}
                if (params.size() > 1) {Global.getSector().getMemoryWithoutUpdate().set(params.get(1).getString(memoryMap), Global.getSector().getMemoryWithoutUpdate().get(params.get(1).getString(memoryMap)) != null ? Global.getSector().getMemoryWithoutUpdate().getInt(params.get(1).getString(memoryMap))+params.get(2).getInt(memoryMap) : params.get(2).getInt(memoryMap));}
		return true;
	}
}

