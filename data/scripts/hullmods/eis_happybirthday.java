package data.scripts.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.FluxTrackerAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.util.IntervalUtil;
import java.awt.Color;

public class eis_happybirthday extends BaseHullMod {
    IntervalUtil tracker = new IntervalUtil(0.5f, 0.5f);
    
    @Override
    public void advanceInCombat(ShipAPI ship, float amount) {
        CombatEngineAPI engine =  Global.getCombatEngine();
        if (engine == null || engine.isPaused() || !ship.isAlive()) {return;}
        tracker.advance(amount);
        if (tracker.intervalElapsed()) {
                FluxTrackerAPI flux = ship.getFluxTracker();
                if (flux.isOverloaded() && ship.getSystem().getAmmo() > 0) {
                    flux.stopOverload();
                    flux.setCurrFlux(0f);
                    flux.setHardFlux(0f);
                    ship.useSystem();
                    for (WeaponAPI w : ship.getAllWeapons()) {
                        if (w.usesAmmo() && w.getAmmo() < w.getMaxAmmo()) {
                            engine.addFloatingText(w.getLocation(), "+" + (w.getMaxAmmo()-w.getAmmo()), 20f, Color.GREEN, ship, 0f, 0f);
                            w.setAmmo(w.getMaxAmmo());
                            w.setRemainingCooldownTo(0.2f);
                        }
                    }
                    flux.setCurrFlux(0f);
                    flux.setHardFlux(0f);
                    ship.getMutableStats().getCombatEngineRepairTimeMult().modifyMult("eis_happybirthday", 0f);
                } else {ship.getMutableStats().getCombatEngineRepairTimeMult().unmodifyMult("eis_happybirthday");}
        }
    }
    @Override
    public boolean isApplicableToShip(ShipAPI ship) {
        return false;
    }
}