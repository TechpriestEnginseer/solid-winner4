package data.scripts.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.combat.WeaponAPI.WeaponSize;
import com.fs.starfarer.api.combat.WeaponAPI.WeaponType;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.api.util.Misc;
import data.scripts.util.MagicUI;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

public class eis_microforge extends BaseHullMod {

    private static final float TIME_PER_RELOAD = 33f;
    private static final float FLAT_RELOAD = 1f;
    
    private static String mfIcon = "graphics/icons/hullsys/missile_autoforge.png";
    private static String mfTitle = Global.getSettings().getString("eis_ironshell", "eis_microforge_title");
    private static String mfText = Global.getSettings().getString("eis_ironshell", "eis_microforge_text");
    private static String mfStatusText = Global.getSettings().getString("eis_ironshell", "eis_microforge_status_text");
    
    private static Map missiletimer = new HashMap();
    private static Map hardfluxlvl = new HashMap();
    static {
        missiletimer.put(HullSize.FRIGATE, 130f);
        missiletimer.put(HullSize.DESTROYER, 85f);
        missiletimer.put(HullSize.CRUISER, 55f);
        missiletimer.put(HullSize.CAPITAL_SHIP, 40f);
        hardfluxlvl.put(HullSize.FRIGATE, 0.4f);
        hardfluxlvl.put(HullSize.DESTROYER, 0.55f);
        hardfluxlvl.put(HullSize.CRUISER, 0.7f);
        hardfluxlvl.put(HullSize.CAPITAL_SHIP, 0.85f);
    }
    
    @Override
    public void addPostDescriptionSection(TooltipMakerAPI tooltip, ShipAPI.HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
        float HEIGHT = 50f;
        float PAD = 10f;
        Color YELLOW = new Color(241, 199, 0);
        
        TooltipMakerAPI rapidAmmoFeeder = tooltip.beginImageWithText(mfIcon, HEIGHT);
        rapidAmmoFeeder.addPara(mfTitle, 0f, YELLOW, mfTitle);
        if ("eis_eradicator".equals(ship.getHullSpec().getBaseHullId())) {rapidAmmoFeeder.addPara(mfText, 0f, Misc.getPositiveHighlightColor(), "70", Integer.toString(Math.round(FLAT_RELOAD)), Math.round(TIME_PER_RELOAD)+"%", Integer.toString(Math.round((Float) hardfluxlvl.get(hullSize)*100f))+"%");
        } else {rapidAmmoFeeder.addPara(mfText, 0f, Misc.getPositiveHighlightColor(), Integer.toString(Math.round((Float) missiletimer.get(hullSize))), Integer.toString(Math.round(FLAT_RELOAD)), Math.round(TIME_PER_RELOAD)+"%", Integer.toString(Math.round((Float) hardfluxlvl.get(hullSize)*100f))+"%");}
        tooltip.addImageWithText(PAD);        
    }    
    
    /*public static class PeriodicMissileReloadData {
        IntervalUtil interval = new IntervalUtil(120f, 120f);
    }*/

     public static class PeriodicMissileReloadData2 {
        IntervalUtil interval = new IntervalUtil(100f, 100f);
        public PeriodicMissileReloadData2(float interval) {
            this.interval=new IntervalUtil(interval,interval);
        }
    }

    @Override
    public String getDescriptionParam(int index, HullSize hullSize, ShipAPI ship) {
        return null;
    }

    @Override
    public void advanceInCombat(ShipAPI ship, float amount) {
        super.advanceInCombat(ship, amount);
        if (!ship.isAlive() || ship.getCurrentCR() == 0f) {return;}
        CombatEngineAPI engine = Global.getCombatEngine();
        String key = "eis_microforge" + "_" + ship.getId();
        PeriodicMissileReloadData2 data = (PeriodicMissileReloadData2) engine.getCustomData().get(key);
        if (data == null) {
            if ("eis_eradicator".equals(ship.getHullSpec().getBaseHullId())) {data = new PeriodicMissileReloadData2(70f);} else {
            data = new PeriodicMissileReloadData2((float) missiletimer.get(ship.getHullSize()));}
            engine.getCustomData().put(key, data);
        }
        boolean advance = false;
        for (WeaponAPI w : ship.getAllWeapons()) {
            if (w.getType() != WeaponType.MISSILE) {
                continue;
            }
            if (w.usesAmmo() && w.getAmmo() < w.getMaxAmmo() && w.getSize() == WeaponSize.SMALL && w.getAmmoTracker().getAmmoPerSecond() == 0) {
                advance = true;
            }
        }
        
        if (advance) {
            MagicUI.drawSystemBar(ship, MagicUI.REDCOLOR, data.interval.getElapsed()/data.interval.getMaxInterval(), 0);
            if (!ship.getFluxTracker().isOverloadedOrVenting() && ship.getHardFluxLevel() <= (float) hardfluxlvl.get(ship.getHullSize())) {
                data.interval.advance(amount);
                if (data.interval.intervalElapsed()) {
                    for (WeaponAPI w : ship.getAllWeapons()) {
                        if (w.getType() != WeaponType.MISSILE) {
                            continue;
                        }
                        if (w.usesAmmo() && w.getAmmo() < w.getMaxAmmo() && w.getSize() == WeaponSize.SMALL && w.getAmmoTracker().getAmmoPerSecond() == 0) {
                            int reload = (int) Math.max(1f, (float) w.getSpec().getMaxAmmo() / 3f);
                            int reloadtext = reload;
                            if (w.getAmmo() + reload >= w.getMaxAmmo()) {
                                reloadtext = w.getMaxAmmo()-w.getAmmo();
                                w.setAmmo(w.getMaxAmmo());
                            } else {
                                w.setAmmo(w.getAmmo() + reload);
                            }
                            if (ship.getOwner() == 0) {
                                engine.addFloatingText(w.getLocation(), "+" + (reloadtext), 25, Color.GREEN, ship, 0f, 0f);
                            }
                        }
                    }
                    Global.getSoundPlayer().playSound("system_forgevats", 1f, 1f, ship.getLocation(), ship.getVelocity());
                }
            } else {
                if (ship == Global.getCombatEngine().getPlayerShip()) {
                    Global.getCombatEngine().maintainStatusForPlayerShip(key, mfIcon,
                            mfTitle, mfStatusText, true);
                }
            }
        } else {
            data.interval.setElapsed(0f);
        }
    }
    
    @Override
    public int getDisplaySortOrder() {
        return 102;
    }
}


