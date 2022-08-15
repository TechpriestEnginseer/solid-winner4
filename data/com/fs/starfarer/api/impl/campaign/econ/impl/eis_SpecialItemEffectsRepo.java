package com.fs.starfarer.api.impl.campaign.econ.impl;

//import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.SpecialItemData;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.InstallableIndustryItemPlugin;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import java.util.HashMap;
import java.util.Map;

public class eis_SpecialItemEffectsRepo {
    
    public static void addItemEffectsToVanillaRepo() {
        ItemEffectsRepo.ITEM_EFFECTS.putAll(ITEM_EFFECTS);
    }

    public static Map<String, InstallableItemEffect> ITEM_EFFECTS = new HashMap<String, InstallableItemEffect>() {
        {put("eis_bbp", new BaseInstallableItemEffect("eis_bbp") {
            @Override
            public void apply(final Industry industry) {
                if (industry.getMarket().hasCondition("eis_aquaculture"))  industry.getMarket().removeCondition("eis_aquaculture");
            }
            @Override
            public void unapply(final Industry industry) {
                if (!industry.getMarket().hasCondition("eis_aquaculture")) industry.getMarket().addCondition("eis_aquaculture");
            }                  
            @Override
            protected void addItemDescriptionImpl(final Industry industry, final TooltipMakerAPI text, final SpecialItemData data, final InstallableIndustryItemPlugin.InstallableItemDescriptionMode mode, final String pre, final float pad) {
                if (industry != null) {if (industry.getMarket() != null) {if ("player".equals(industry.getMarket().getFactionId()) && industry.getSpecialItem() != null) {
                        text.addPara(pre + "%s.",
                        pad, Misc.getNegativeHighlightColor(), 
                        "" + Global.getSettings().getString("eis_ironshell", "eis_bbp_warning"));} else text.addPara(pre + Global.getSettings().getString("eis_ironshell", "eis_bbp"),
                        pad, Misc.getHighlightColor(), 
                        "" + (int)200 + "%");} else text.addPara(pre + Global.getSettings().getString("eis_ironshell", "eis_bbp"),
                        pad, Misc.getHighlightColor(), 
                        "" + (int)200 + "%");}    else text.addPara(pre + Global.getSettings().getString("eis_ironshell", "eis_bbp"),
                        pad, Misc.getHighlightColor(), 
                        "" + (int)200 + "%");
            }});
        }
    };
	
	
}







