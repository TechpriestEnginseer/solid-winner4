package data.scripts.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.ShipAPI;

public class eis_perfectvengeancecore extends BaseHullMod {
    
    @Override
    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
        if (ship.getVariant().hasHullMod("shield_shunt")) {
            ship.getVariant().removeMod("shield_shunt");
            Global.getSoundPlayer().playUISound("cr_allied_critical", 1f, 1f);
        }
    }
}


