package data.scripts.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.FluxTrackerAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.util.IntervalUtil;
import java.awt.Color;

public class eis_peacekeepercore extends BaseHullMod {
    public static final float HAVE_SEX_BONUS = 50f;
    public static final float HAVE_SEX_BONUS2 = 0.66f;
    IntervalUtil tracker = new IntervalUtil(0.5f, 0.5f);
    
    @Override
    public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
        stats.getDamageToMissiles().modifyPercent(id, HAVE_SEX_BONUS);
        stats.getDamageToFighters().modifyPercent(id, HAVE_SEX_BONUS);
        stats.getBallisticRoFMult().modifyMult(id, HAVE_SEX_BONUS2);
        //stats.getDamageToFrigates().modifyMult(id, HAVE_SEX_BONUS2);
        //stats.getDamageToDestroyers().modifyMult(id, HAVE_SEX_BONUS2);
        //stats.getDamageToCruisers().modifyMult(id, HAVE_SEX_BONUS2);
        //stats.getDamageToCapital().modifyMult(id, HAVE_SEX_BONUS2);
    }
    @Override
    public void advanceInCombat(ShipAPI ship, float amount) {
        CombatEngineAPI engine =  Global.getCombatEngine();
        if (engine == null || engine.isPaused() || !ship.isAlive()) {return;}
        tracker.advance(amount);
        if (tracker.intervalElapsed()) {
            //try {
                FluxTrackerAPI flux = ship.getFluxTracker();
                if (flux.isOverloaded() && ship.getSystem().getAmmo() > 0) {
                    flux.stopOverload();
                    flux.setCurrFlux(0f);
                    flux.setHardFlux(0f);
                    ship.useSystem();
                    flux.setCurrFlux(0f);
                    flux.setHardFlux(0f);
                    ship.getMutableStats().getCombatEngineRepairTimeMult().modifyMult("eis_peacekeepercore", 0f);
                    engine.addFloatingText(ship.getLocation(), "My Life For Ava!", 15f, Color.WHITE, ship, 1f, 0.5f);
                } else {ship.getMutableStats().getCombatEngineRepairTimeMult().unmodifyMult("eis_peacekeepercore");}
            //}
            //catch(Exception e) { //these hands because I had to do THIS INSTEAD OF AN IF STATEMENT WHAT'S WRONG WITH YOU.
            //}
        }
    }
}


