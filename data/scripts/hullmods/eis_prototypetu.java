package data.scripts.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;

public class eis_prototypetu extends BaseHullMod {
    private static final float DAKKA_RANGE_BONUS = 300f;
    private static final float HAVE_SEX_BONUS = 25f;
    private static final float HAVE_SEX2_BONUS = 0.8f;
    @Override
    public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
        stats.getNonBeamPDWeaponRangeBonus().modifyFlat(id, DAKKA_RANGE_BONUS);
        stats.getDamageToMissiles().modifyPercent(id, HAVE_SEX_BONUS);
        stats.getDamageToFighters().modifyPercent(id, HAVE_SEX_BONUS);
        stats.getDamageToFrigates().modifyMult(id, HAVE_SEX2_BONUS);
        stats.getDamageToDestroyers().modifyMult(id, HAVE_SEX2_BONUS);
        stats.getDamageToCruisers().modifyMult(id, HAVE_SEX2_BONUS);
        stats.getDamageToCapital().modifyMult(id, HAVE_SEX2_BONUS);
    }
}


