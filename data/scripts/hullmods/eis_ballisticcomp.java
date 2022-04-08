package data.scripts.hullmods;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.WeaponAPI.WeaponType;
import com.fs.starfarer.api.impl.hullmods.BaseLogisticsHullMod;

public class eis_ballisticcomp extends BaseLogisticsHullMod {
    private static float weeeeee = 50f;
    @Override
    public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
        int holybased = 0;
	for (String slot : stats.getVariant().getNonBuiltInWeaponSlots() ) {
            if (stats.getVariant().getSlot(slot).getWeaponType() == WeaponType.COMPOSITE && stats.getVariant().getWeaponSpec(slot).getType() == WeaponType.BALLISTIC) {holybased++;}
        } 
        stats.getVariant().getNonBuiltInWeaponSlots().size();
        stats.getFluxDissipation().modifyFlat(id, weeeeee*(holybased));
    }
        
    @Override
    public String getDescriptionParam(int index, HullSize hullSize, ShipAPI ship) {
        return null;
    }
}







