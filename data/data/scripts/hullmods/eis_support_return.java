package data.scripts.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.util.Misc;
import org.lazywizard.lazylib.MathUtils;

public class eis_support_return extends BaseHullMod {
    @Override
    public void advanceInCombat(ShipAPI ship, float amount) {
        CombatEngineAPI engine =  Global.getCombatEngine();
        if (engine == null || engine.isPaused() || !ship.isAlive()) {return;}
        if (ship.getWing() != null || ship.getWing().getSourceShip().isAlive()) {
            if (ship.getWing().getSource().getCurrRate() > 0.45f && !ship.getWing().isReturning(ship)) {
                for (WeaponAPI w : ship.getAllWeapons()) {
                    if (w.getAmmo() == 0) {
                        ship.getWing().getSource().setCurrRate(ship.getWing().getSource().getCurrRate()-MathUtils.getRandomNumberInRange(0.05f, 0.015f));
                        if (ship.getOwner() == 0) {engine.addFloatingText(ship.getLocation(), Misc.getRoundedValue(ship.getWing().getSource().getCurrRate()*100f)+"%", 20f, Misc.getPositiveHighlightColor(), ship, 1f, 1f);}
                        ship.getWing().orderReturn(ship);break;
                    }
                }
            }
        }
    }
}
