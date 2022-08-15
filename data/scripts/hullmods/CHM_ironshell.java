package data.scripts.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import java.awt.Color;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class CHM_ironshell extends BaseHullMod {
    //for some reason, some fighters are existing in fleet.. and not in their carrier weird null ugh..
    private static final Map coom = new HashMap();
    static {
        coom.put(HullSize.FIGHTER, 0.88f);
        coom.put(HullSize.FRIGATE, 0.88f);
        coom.put(HullSize.DESTROYER, 0.90f);
        coom.put(HullSize.CRUISER, 0.92f);
        coom.put(HullSize.CAPITAL_SHIP, 0.94f);
        coom.put(HullSize.DEFAULT, 0.94f);
    }
    @Override
    public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
        stats.getBeamDamageTakenMult().modifyMult(id, (Float) coom.get(hullSize));
        stats.getBeamShieldDamageTakenMult().modifyMult(id, (Float) coom.get(hullSize));
    }
    @Override
    public String getDescriptionParam(int index, HullSize hullSize, ShipAPI ship) {
        if (index == 0) return "" + (int) ((1.01f - (Float) coom.get(HullSize.FRIGATE)) * 100f) + "%";
        if (index == 1) return "" + (int) ((1f - (Float) coom.get(HullSize.DESTROYER)) * 100f) + "%";
        if (index == 2) return "" + (int) ((1f - (Float) coom.get(HullSize.CRUISER)) * 100f) + "%";
        if (index == 3) return "" + (int) ((1f - (Float) coom.get(HullSize.CAPITAL_SHIP)) * 100f) + "%";
        return null;
    }
    
    @Override
    public void addPostDescriptionSection(final TooltipMakerAPI tooltip, final ShipAPI.HullSize hullSize, final ShipAPI ship, final float width, final boolean isForModSpec) {
        final Color flavor = new Color(110,110,110,255);
        int month = Global.getSector().getClock().getCal().get(Calendar.WEEK_OF_MONTH);
        switch (month) {
            case 0:
                tooltip.addPara("%s", 6f, flavor, new String[] { Global.getSettings().getString("eis_ironshell", "CHM_ironshell1") });
                break;
            case 1:
                tooltip.addPara("%s", 6f, flavor, new String[] { Global.getSettings().getString("eis_ironshell", "CHM_ironshell1") });
                tooltip.addPara("%s", 1f, flavor, new String[] { Global.getSettings().getString("eis_ironshell", "CHM_ironshell1a") });
                break;
            case 2:
                tooltip.addPara("%s", 6f, flavor, new String[] { Global.getSettings().getString("eis_ironshell", "CHM_ironshell2") });
                tooltip.addPara("%s", 1f, flavor, new String[] { Global.getSettings().getString("eis_ironshell", "CHM_ironshell2a") });
                break;
            case 3:
                tooltip.addPara("%s", 6f, flavor, new String[] { Global.getSettings().getString("eis_ironshell", "CHM_ironshell3") });
                tooltip.addPara("%s", 1f, flavor, new String[] { Global.getSettings().getString("eis_ironshell", "CHM_ironshell3a") });
                break;
            case 4:
                tooltip.addPara("%s", 6f, flavor, new String[] { Global.getSettings().getString("eis_ironshell", "CHM_ironshell4") });
                tooltip.addPara("%s", 1f, flavor, new String[] { Global.getSettings().getString("eis_ironshell", "CHM_ironshell4a") });
                break;
            case 5:
                tooltip.addPara("%s", 6f, flavor, new String[] { Global.getSettings().getString("eis_ironshell", "CHM_ironshell5") });
                tooltip.addPara("%s", 1f, flavor, new String[] { Global.getSettings().getString("eis_ironshell", "CHM_ironshell5a") });
                break;
            case 6:
                tooltip.addPara("%s", 6f, flavor, new String[] { Global.getSettings().getString("eis_ironshell", "CHM_ironshell6") });
                tooltip.addPara("%s", 6f, flavor, new String[] { Global.getSettings().getString("eis_ironshell", "CHM_ironshell6a") });
                tooltip.addPara("%s", 6f, flavor, new String[] { Global.getSettings().getString("eis_ironshell", "CHM_ironshell6b") });
                tooltip.addPara("%s", 6f, flavor, new String[] { Global.getSettings().getString("eis_ironshell", "CHM_ironshell6c") });
                tooltip.addPara("%s", 6f, flavor, new String[] { Global.getSettings().getString("eis_ironshell", "CHM_ironshell6d") });
                tooltip.addPara("%s", 1f, flavor, new String[] { Global.getSettings().getString("eis_ironshell", "CHM_ironshell1a") });
                break;
            default:
                tooltip.addPara("%s", 6f, flavor, new String[] { Global.getSettings().getString("eis_ironshell", "CHM_ironshell1") });
            }
    }
    
    @Override
    public Color getBorderColor() {return new Color(147, 102, 50, 0);}

    @Override
    public Color getNameColor() {return new Color(252,173,60, 255);}
}

