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

    public static final float TIME_PER_RELOAD = 33f;
    public static final float FLAT_RELOAD = 1f;
    
    public static String mfIcon = "graphics/icons/hullsys/missile_autoforge.png";
    public static String mfTitle = Global.getSettings().getString("eis_ironshell", "eis_microforge_title");
    public static String mfText = Global.getSettings().getString("eis_ironshell", "eis_microforge_text");
    public static String mfStatusText = Global.getSettings().getString("eis_ironshell", "eis_microforge_status_text");
    
    private static Map missiletimer = new HashMap();
    private static Map hardfluxlvl = new HashMap();
    static {
        missiletimer.put(HullSize.FRIGATE, 85f);
        missiletimer.put(HullSize.DESTROYER, 70f);
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
        rapidAmmoFeeder.addPara(mfText, 0f, Misc.getPositiveHighlightColor(), Integer.toString(Math.round((Float) missiletimer.get(hullSize))), Integer.toString(Math.round(FLAT_RELOAD)), Math.round(TIME_PER_RELOAD)+"%", Integer.toString(Math.round((Float) hardfluxlvl.get(hullSize)*100f))+"%");
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
        if (!ship.isAlive()) {return;}
        CombatEngineAPI engine = Global.getCombatEngine();
        String key = "eis_microforge" + "_" + ship.getId();
        if (ship.getHullSize() == HullSize.CRUISER) {}
        PeriodicMissileReloadData2 data = (PeriodicMissileReloadData2) engine.getCustomData().get(key);
        if (data == null) {
            data = new PeriodicMissileReloadData2((float) missiletimer.get(ship.getHullSize()));
            engine.getCustomData().put(key, data);
        }
        boolean advance = false;
        for (WeaponAPI w : ship.getAllWeapons()) {
            if (w.getType() != WeaponType.MISSILE) {
                continue;
            }
            if (w.usesAmmo() && w.getAmmo() < w.getMaxAmmo() && w.getSize() == WeaponSize.SMALL) {
                advance = true;
            }
        }
        
        if (advance) {
            MagicUI.drawSystemBar(ship, new Color(255,0,0), data.interval.getElapsed()/data.interval.getMaxInterval(), 0);
            if (!ship.getFluxTracker().isOverloadedOrVenting() && ship.getHardFluxLevel() <= (float) hardfluxlvl.get(ship.getHullSize())) {
                data.interval.advance(amount);
                if (data.interval.intervalElapsed()) {
                    for (WeaponAPI w : ship.getAllWeapons()) {
                        if (w.getType() != WeaponType.MISSILE) {
                            continue;
                        }
                        if (w.usesAmmo() && w.getAmmo() < w.getMaxAmmo() && w.getSize() == WeaponSize.SMALL) {
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


