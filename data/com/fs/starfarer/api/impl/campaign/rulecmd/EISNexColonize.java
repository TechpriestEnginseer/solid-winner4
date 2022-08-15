package com.fs.starfarer.api.impl.campaign.rulecmd;


import java.util.List;
import java.util.Map;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import exerelin.campaign.intel.colony.ColonyExpeditionIntel;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.util.Misc.Token;
import exerelin.campaign.fleets.InvasionFleetManager;
import exerelin.campaign.intel.invasion.InvasionIntel;


public class EISNexColonize extends BaseCommandPlugin {
	public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Token> params, Map<String, MemoryAPI> memoryMap) {
            if (Global.getSettings().getModManager().isModEnabled("nexerelin")) {
                if (Global.getSector().getEntityById(params.get(0).getString(memoryMap)) != null && Global.getSector().getEntityById(params.get(0).getString(memoryMap)).getMarket().getFactionId().equals(Factions.NEUTRAL)) {
                    ColonyExpeditionIntel intel = new ColonyExpeditionIntel(Global.getSector().getFaction("ironshell"), 
                        Global.getSector().getEconomy().getMarket("eis_chitagupta"), 
                        Global.getSector().getEntityById(params.get(0).getString(memoryMap)).getMarket(),
                        500, 1f);
                    intel.init();
                    Global.getSector().getIntelManager().addIntelToTextPanel(intel, dialog.getTextPanel());
                } else if (Global.getSector().getEntityById(params.get(0).getString(memoryMap)) != null && !Global.getSector().getEntityById(params.get(0).getString(memoryMap)).getMarket().getFactionId().equals(Factions.NEUTRAL)) {
                    if (Global.getSector().getFaction("ironshell").getRelationship(Global.getSector().getEntityById(params.get(0).getString(memoryMap)).getMarket().getFactionId()) >= -0.5f || !Global.getSector().getEntityById(params.get(0).getString(memoryMap)).getMarket().getFactionId().equals(Factions.HEGEMONY)) {
                    Global.getSector().getFaction("ironshell").setRelationship(Global.getSector().getEntityById(params.get(0).getString(memoryMap)).getMarket().getFactionId(), -0.75f);
                    float fp = InvasionFleetManager.getWantedFleetSize(Global.getSector().getFaction("ironshell"), Global.getSector().getEntityById(params.get(0).getString(memoryMap)).getMarket(), 0.2f, false);
                    fp *= 1.1f;
                    //fp *= MathUtils.getRandomNumberInRange(0.8f, 1.2f);
                    InvasionIntel intel2 = new InvasionIntel(Global.getSector().getFaction("ironshell"), Global.getSector().getEconomy().getMarket("eis_chitagupta"), Global.getSector().getEntityById(params.get(0).getString(memoryMap)).getMarket(), fp, 1);	
                    intel2.init();
                    Global.getSector().getIntelManager().addIntelToTextPanel(intel2, dialog.getTextPanel());}
                }
                
                /*if (Global.getSector().getEntityById("killa") != null && Global.getSector().getEntityById("killa").getMarket().getFactionId().equals(Factions.NEUTRAL)) {
                    ColonyExpeditionIntel intel2 = new ColonyExpeditionIntel(Global.getSector().getFaction("ironshell"), 
                        Global.getSector().getEconomy().getMarket("chitagupta"), 
                        Global.getSector().getEntityById("killa").getMarket(),
                        300, 1.5f);
                    intel2.init();
                    Global.getSector().getIntelManager().addIntelToTextPanel(intel2, dialog.getTextPanel());
                }*/
            }
            return true;
        }
}