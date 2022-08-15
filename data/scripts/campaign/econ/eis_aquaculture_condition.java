package data.scripts.campaign.econ;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.impl.campaign.econ.BaseHazardCondition;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

public class eis_aquaculture_condition extends BaseHazardCondition {
    private static String poopystinky = Global.getSettings().getString("eis_ironshell", "eis_aquaculture_condition1");
    private static String poopystinky2 = Global.getSettings().getString("eis_ironshell", "eis_aquaculture_condition2");
    private static String poopystinky3 = Global.getSettings().getString("eis_ironshell", "eis_aquaculture_condition3");
    
    @Override
    public void apply(String id) {	
        Industry industry = market.getIndustry(Industries.AQUACULTURE);
        if (industry != null) {
            if (industry.isFunctional()) {
                industry.supply(id + "_0", Commodities.FOOD, -3, poopystinky3);
            }
        }
        market.getHazard().modifyFlat(id, 0.50f, condition.getName());
        /*if (!"ironshell".equals(market.getFactionId())) {
            market.getStability().modifyFlat(id, (-(market.getStabilityValue()-1f)), "Iron Shell Presence");
        }*/ 
    }
    
    @Override
    public void unapply(String id) {	
        Industry industry = market.getIndustry(Industries.AQUACULTURE);
        if (industry != null) {
            if (industry.isFunctional()) {
                industry.getSupply(Commodities.FOOD).getQuantity().unmodifyFlat(id + "_0");
            }
        }
        market.getHazard().unmodifyFlat(id);
        //market.getStability().unmodify(id);
    }
    
    @Override
    protected void createTooltipAfterDescription(TooltipMakerAPI tooltip, boolean expanded) {
        float pad = 10f;
        tooltip.addPara(poopystinky, pad, Misc.getHighlightColor(), "-3");
        tooltip.addPara(poopystinky2, pad, Misc.getHighlightColor(), "+50%");
    }
}