package data.scripts.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.combat.WeaponAPI.WeaponSize;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import java.awt.Color;

public class eis_indomitable extends BaseHullMod {
    
    private boolean AlreadyCame = false;
    private int storedHashCode;
    private int iMissile = 0;
    public static String failIcon = Global.getSettings().getSpriteName("misc", "eis_parryfail");
    
    public static String indeezTitle = Global.getSettings().getString("eis_ironshell", "eis_indeezTitle");
    public static String indeezText1 = Global.getSettings().getString("eis_ironshell", "eis_indeezText1");
    public static String indeezText1b = Global.getSettings().getString("eis_ironshell", "eis_indeezText1b");
    public static String indeezText1c = Global.getSettings().getString("eis_ironshell", "eis_indeezText1c");
    public static String indeezText1d = Global.getSettings().getString("eis_ironshell", "eis_indeezText1d");
    private static final float REDUCTION_PERCENT = 40f;
    
    @Override
    public void advanceInCombat(ShipAPI ship, float amount) {
        if (Global.getCombatEngine().isPaused()) {return;}
        if (Global.getCombatEngine().hashCode() != storedHashCode) {
          AlreadyCame = false;
          ship.getMutableStats().getPeakCRDuration().unmodifyMult("eis_indomitable");
          iMissile = 0;
          storedHashCode = Global.getCombatEngine().hashCode();
        }
        if (!AlreadyCame) {
            for (WeaponAPI w : ship.getAllWeapons()) {
                if (w.getType() == WeaponAPI.WeaponType.MISSILE && w.usesAmmo() && w.getSize() == WeaponSize.MEDIUM) {iMissile += 1;}
                if (w.getType() == WeaponAPI.WeaponType.MISSILE && w.usesAmmo() && w.getSize() == WeaponSize.LARGE) {iMissile += 2;}
                if (iMissile > 2) {break;}
            }
        }
        if (iMissile > 2 && !AlreadyCame) {
            for (WeaponAPI w : ship.getAllWeapons()) {
                if (w.getType() == WeaponAPI.WeaponType.MISSILE && w.usesAmmo()) {
                    int modifiedAmmo = (int) (w.getMaxAmmo()*(1f - 0.4f));
                    w.setMaxAmmo(modifiedAmmo);
                    w.setAmmo(w.getMaxAmmo());
                }
            }
            ship.getMutableStats().getPeakCRDuration().modifyMult("eis_indomitable", 0.6f);
            if (ship == Global.getCombatEngine().getPlayerShip()) Global.getSoundPlayer().playUISound("cr_playership_warning", 1f, 1f);
        }
	if (ship == Global.getCombatEngine().getPlayerShip() && iMissile > 2) {
            Global.getCombatEngine().maintainStatusForPlayerShip("eis_indeez", failIcon, indeezTitle, "REDUCED PPT AND MISSILE CAPACITY", true);
	}
        AlreadyCame = true;
    }
    
    @Override
    public void addPostDescriptionSection(TooltipMakerAPI tooltip, ShipAPI.HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
        float HEIGHT = 50f;
        float PAD = 10f;
        Color YELLOW = new Color(241,199,0);

        TooltipMakerAPI indeez = tooltip.beginImageWithText(failIcon, HEIGHT);
        indeez.addPara(indeezTitle, 0f, YELLOW, indeezTitle);
        indeez.addPara(indeezText1, 0f);
        indeez.addPara(indeezText1b, 0f, Misc.getNegativeHighlightColor(), "" + Math.round(REDUCTION_PERCENT) + "%");
        indeez.addPara(indeezText1c, 0f, Misc.getNegativeHighlightColor(), "" + Math.round(REDUCTION_PERCENT) + "%");
        tooltip.addImageWithText(PAD);
    }
}


