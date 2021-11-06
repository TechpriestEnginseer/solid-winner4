package data.scripts.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.FluxTrackerAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;

public class eis_peacekeepercore extends BaseHullMod {
    public static final float HAVE_SEX_BONUS = 50f;
    public static final float HAVE_SEX_BONUS2 = 0.66f;
    private CombatEngineAPI engine =  Global.getCombatEngine();
    
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
        if (engine == null || engine.isPaused() || !ship.isAlive()) {return;}
        try {
            FluxTrackerAPI flux = ship.getFluxTracker();
            if (flux.isOverloaded() && ship.getSystem().getAmmo() > 0) {
                flux.stopOverload();
                flux.setCurrFlux(0f);
                flux.setHardFlux(0f);
                ship.useSystem();
                flux.setCurrFlux(0f);
                flux.setHardFlux(0f);
                ship.setHitpoints(ship.getMaxHitpoints());
                ship.getMutableStats().getCombatEngineRepairTimeMult().modifyMult("eis_peacekeepercore", 0f);
            }
            ship.getMutableStats().getCombatEngineRepairTimeMult().unmodifyMult("eis_peacekeepercore");
        }
        catch(Exception e) { //these hands because I had to do THIS INSTEAD OF AN IF STATEMENT WHAT'S WRONG WITH YOU.
        }
    }
}


