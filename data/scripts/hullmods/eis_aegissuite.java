package data.scripts.hullmods;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.impl.hullmods.BaseLogisticsHullMod;

public class eis_aegissuite extends BaseLogisticsHullMod {
//This is supposedly a... Terminator Core hullmod duplicate.... somewhat.
    private static float DAMAGE_MISSILES_PERCENT = 50f;
    private static float DAMAGE_FIGHTERS_PERCENT = 50f;
    @Override
    public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
        stats.getDamageToMissiles().modifyPercent(id, DAMAGE_MISSILES_PERCENT);
        stats.getDamageToFighters().modifyPercent(id, DAMAGE_FIGHTERS_PERCENT);
	stats.getProjectileSpeedMult().modifyMult(id, 1.2f);
        stats.getBallisticWeaponRangeBonus().modifyFlat(id, 100f);
        //Yes I know Vulcan Cannon gets +100 more range this is intentional with both range bonus.
	stats.getNonBeamPDWeaponRangeBonus().modifyFlat(id, 100f);
	stats.getAutofireAimAccuracy().modifyFlat(id, 1f);
	stats.getEngineDamageTakenMult().modifyMult(id, 0f);
	stats.getDynamic().getMod(Stats.PD_IGNORES_FLARES).modifyFlat(id, 1f);
    }
    @Override
    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
        super.applyEffectsAfterShipCreation(ship, id);   
    }
    
    @Override
    public String getDescriptionParam(int index, HullSize hullSize) {
	return null;
    }
}







