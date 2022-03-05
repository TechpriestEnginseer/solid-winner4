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
    
    private static final int rangeRadius = 400;
    private static final int rangeRelay = 1000;
    private static final int shieldEffBuff = 30;
    private static final int shieldArcDebuff = 120;
    private static final int shieldUnfoldRate = 50;
    private static final int buffDur = 9;
    private static final int debuffDur = 3;
    private static final float poggers = 2000f;
    private static String hullmodname = Global.getSettings().getHullModSpec("neural_interface").getDisplayName();

    private static String successIcon = "graphics/icons/hullsys/fortress_shield.png";
    private static String failIcon = Global.getSettings().getSpriteName("misc", "eis_parryfail");
    private static String relayIcon = "graphics/icons/campaign/sensor_strength.png";

    private static String Paragraph = Global.getSettings().getString("eis_ironshell", "eis_vengeanceParagraph");
    private static String ParagraphShunted = Global.getSettings().getString("eis_ironshell", "eis_vengeanceParagraphShunted");
    private static String successTitle = Global.getSettings().getString("eis_ironshell", "eis_vengeanceSuccessTitle");
    private static String successText1 = Global.getSettings().getString("eis_ironshell", "eis_vengeanceSuccessText1");
    private static String successText2 = Global.getSettings().getString("eis_ironshell", "eis_vengeanceSuccessText2");
    private static String failTitle = Global.getSettings().getString("eis_ironshell", "eis_vengeanceFailTitle");
    private static String failText1 = Global.getSettings().getString("eis_ironshell", "eis_vengeanceFailText1");
    private static String failText2 = Global.getSettings().getString("eis_ironshell", "eis_vengeanceFailText2");
    private static String failText3 = Global.getSettings().getString("eis_ironshell", "eis_vengeanceFailText3");
    private static String relayTitle = Global.getSettings().getString("eis_ironshell", "eis_vengeanceRelayTitle");
    private static String relayText = Global.getSettings().getString("eis_ironshell", "eis_vengeanceRelayText1");
    private static String relayText2 = Global.getSettings().getString("eis_ironshell", "eis_vengeanceRelayText2");
    
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


