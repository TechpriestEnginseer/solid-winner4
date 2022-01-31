package data.scripts.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import java.awt.Color;

public class eis_vengeancecore extends BaseHullMod {
    
    public static final int rangeRadius = 400;
    public static final int rangeRelay = 1000;
    public static final int shieldEffBuff = 30;
    public static final int shieldArcDebuff = 120;
    public static final int shieldUnfoldRate = 50;
    public static final int buffDur = 9;
    public static final int debuffDur = 3;
    public static final float poggers = 1500f;
    public static String hullmodname = Global.getSettings().getHullModSpec("neural_interface").getDisplayName();

    public static String successIcon = "graphics/icons/hullsys/fortress_shield.png";
    public static String failIcon = Global.getSettings().getSpriteName("misc", "eis_parryfail");
    public static String relayIcon = "graphics/icons/campaign/sensor_strength.png";

    public static String Paragraph = Global.getSettings().getString("eis_ironshell", "eis_vengeanceParagraph");
    public static String ParagraphShunted = Global.getSettings().getString("eis_ironshell", "eis_vengeanceParagraphShunted");
    public static String successTitle = Global.getSettings().getString("eis_ironshell", "eis_vengeanceSuccessTitle");
    public static String successText1 = Global.getSettings().getString("eis_ironshell", "eis_vengeanceSuccessText1");
    public static String successText2 = Global.getSettings().getString("eis_ironshell", "eis_vengeanceSuccessText2");
    public static String failTitle = Global.getSettings().getString("eis_ironshell", "eis_vengeanceFailTitle");
    public static String failText1 = Global.getSettings().getString("eis_ironshell", "eis_vengeanceFailText1");
    public static String failText2 = Global.getSettings().getString("eis_ironshell", "eis_vengeanceFailText2");
    public static String failText3 = Global.getSettings().getString("eis_ironshell", "eis_vengeanceFailText3");
    public static String relayTitle = Global.getSettings().getString("eis_ironshell", "eis_vengeanceRelayTitle");
    public static String relayText = Global.getSettings().getString("eis_ironshell", "eis_vengeanceRelayText1");
    public static String relayText2 = Global.getSettings().getString("eis_ironshell", "eis_vengeanceRelayText2");
    
    /*@Override
    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
        /*if (ship.getVariant().hasHullMod("shield_shunt")) {
            ship.getVariant().removeMod("shield_shunt");
            Global.getSoundPlayer().playUISound("cr_allied_critical", 1f, 1f);
        }
    }*/
    
    @Override
    public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
        if (stats.getVariant().hasHullMod("neural_interface")) {
            stats.getFluxCapacity().modifyFlat(id, poggers);
        }
    }
    
    @Override
    public boolean shouldAddDescriptionToTooltip(HullSize hullSize, ShipAPI ship, boolean isForModSpec) {
        return false;
    }
    
    @Override
    public void addPostDescriptionSection(TooltipMakerAPI tooltip, ShipAPI.HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
        float HEIGHT = 50f;
        float PAD = 10f;
        Color YELLOW = new Color(241,199,0);
        if (ship.getShield() != null) {tooltip.addPara(Paragraph, PAD);} else {tooltip.addPara(ParagraphShunted, PAD);}
        TooltipMakerAPI success = tooltip.beginImageWithText(successIcon, HEIGHT);
        success.addPara(successTitle, 0f, YELLOW, successTitle);
        success.addPara(successText1, 0f, YELLOW, Integer.toString(Math.round(ship.getMutableStats().getSystemRangeBonus().computeEffective(rangeRadius))));
        if (ship.getShield() != null) {success.addPara(successText2, 0f, Misc.getPositiveHighlightColor(), Integer.toString(shieldEffBuff)+"%", Integer.toString(buffDur));}
        tooltip.addImageWithText(PAD);

        if (ship.getShield() != null) {
            TooltipMakerAPI fail = tooltip.beginImageWithText(failIcon, HEIGHT);
            fail.addPara(failTitle, 0f, YELLOW, failTitle);
            fail.addPara(failText1, 0f, Misc.getNegativeHighlightColor(), Integer.toString(shieldEffBuff)+"%", Integer.toString(debuffDur));
            fail.addPara(failText2, 0f, Misc.getNegativeHighlightColor(), Integer.toString(shieldArcDebuff), Integer.toString(debuffDur));
            fail.addPara(failText3, 0f, Misc.getNegativeHighlightColor(), Integer.toString(shieldUnfoldRate)+"%", Integer.toString(debuffDur));
            tooltip.addImageWithText(PAD);
        }

        TooltipMakerAPI relay = tooltip.beginImageWithText(relayIcon, HEIGHT);
        relay.addPara(relayTitle, 0f, YELLOW, relayTitle);
        relay.addPara(relayText, 0f, YELLOW, Integer.toString(rangeRelay));
        relay.addPara(relayText2, 0f, Misc.getPositiveHighlightColor(), hullmodname, Misc.getRoundedValue(poggers));
        tooltip.addImageWithText(PAD);
    }
}


