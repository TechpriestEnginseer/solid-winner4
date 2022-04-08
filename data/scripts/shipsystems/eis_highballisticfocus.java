package data.scripts.shipsystems;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.WeaponAPI.WeaponType;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import com.fs.starfarer.api.plugins.ShipSystemStatsScript;
import com.fs.starfarer.api.util.Misc;
import java.awt.Color;
import java.util.EnumSet;

public class eis_highballisticfocus extends BaseShipSystemScript {

	public static final float DAMAGE_BONUS_PERCENT = 40f;
        public static final float ROF_BONUS = 0.8f;
        public static final float FLUX_REDUCTION = .4f;
        private float activeTime = 0f;
        private CombatEngineAPI engine =  Global.getCombatEngine();
        private static final Color Sneed = new Color (255,200,0,155);
        private static final Color Sneed2 = new Color (255,0,255,255);
        public static String eis_highballisticfocus1 = Global.getSettings().getString("eis_ironshell", "eis_highballisticfocus1");
        public static String eis_highballisticfocus2 = Global.getSettings().getString("eis_ironshell", "eis_highballisticfocus2");
        public static String eis_highballisticfocus3 = Global.getSettings().getString("eis_ironshell", "eis_highballisticfocus3");
        public static String eis_highballisticfocus4 = Global.getSettings().getString("eis_ironshell", "eis_highballisticfocus4");
        
        @Override
	public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
                if (engine.isPaused()) return;
                ShipAPI ship = (ShipAPI) stats.getEntity();
                if (ship == null) {return;}
                float mult = 1f + ROF_BONUS * effectLevel;
		float bonusPercent = DAMAGE_BONUS_PERCENT * effectLevel;
                float amount = engine.getElapsedInLastFrame();
                activeTime += amount;
                if (activeTime >= 3f) {
                    stats.getEnergyWeaponDamageMult().unmodifyPercent(id);
                    ship.setWeaponGlow(0f, Sneed2, EnumSet.of(WeaponType.ENERGY));
                    ship.setWeaponGlow(0.7f*effectLevel, Sneed, EnumSet.of(WeaponType.BALLISTIC));
                    stats.getBallisticRoFMult().modifyMult(id, mult/2f);
                    stats.getBallisticWeaponFluxCostMod().modifyMult(id, 1f - FLUX_REDUCTION/2f);
                } else if (activeTime >= 2.7f) {
                    stats.getEnergyWeaponDamageMult().modifyPercent(id, bonusPercent);
                    ship.setWeaponGlow(Math.abs(activeTime-3.0f), Sneed2, EnumSet.of(WeaponType.ENERGY));
                    ship.setWeaponGlow(Math.abs(activeTime-3.7f), Sneed, EnumSet.of(WeaponType.BALLISTIC));
                    stats.getBallisticRoFMult().modifyMult(id, mult);
                    stats.getBallisticWeaponFluxCostMod().modifyMult(id, 1f - FLUX_REDUCTION);
                } else if (activeTime >= 0.33f) {
                    stats.getEnergyWeaponDamageMult().modifyPercent(id, bonusPercent);
                    ship.setWeaponGlow(1f, Sneed2, EnumSet.of(WeaponType.ENERGY));
                    ship.setWeaponGlow(1f, Sneed, EnumSet.of(WeaponType.BALLISTIC));
                    stats.getBallisticRoFMult().modifyMult(id, mult);
                    stats.getBallisticWeaponFluxCostMod().modifyMult(id, 1f - FLUX_REDUCTION);
                } else if (activeTime < 0.33f) {
                    stats.getEnergyWeaponDamageMult().modifyPercent(id, bonusPercent);
                    ship.setWeaponGlow(activeTime*3f, Sneed2, EnumSet.of(WeaponType.ENERGY));
                    ship.setWeaponGlow(activeTime*3f, Sneed, EnumSet.of(WeaponType.BALLISTIC));
                    stats.getBallisticRoFMult().modifyMult(id, mult);
                    stats.getBallisticWeaponFluxCostMod().modifyMult(id, 1f - FLUX_REDUCTION);
                }
		if (state == ShipSystemStatsScript.State.OUT) {
                    stats.getMaxSpeed().unmodify(id);
		} else {
                    stats.getMaxSpeed().modifyFlat(id, 20f);
                    stats.getAcceleration().modifyPercent(id, 80f * effectLevel);
                    stats.getDeceleration().modifyPercent(id, 80f* effectLevel);
                    stats.getTurnAcceleration().modifyFlat(id, 12f  * effectLevel);
                    stats.getTurnAcceleration().modifyPercent(id, 80f * effectLevel);
                    stats.getMaxTurnRate().modifyFlat(id, 6f);
                    stats.getMaxTurnRate().modifyPercent(id, 40f);
		}
                
		/*if (stats.getEntity() instanceof ShipAPI) {
			ship.getEngineController().fadeToOtherColor(this, new Color(100,255,100,255), new Color(0,0,0,0), effectLevel, 0.67f);
			ship.getEngineController().extendFlame(this, 2f * effectLevel, 0f * effectLevel, 0f * effectLevel);
		}*/
	}
        @Override
	public void unapply(MutableShipStatsAPI stats, String id) {
                ShipAPI ship = (ShipAPI) stats.getEntity();
                if (ship == null) {return;}
		stats.getEnergyWeaponDamageMult().unmodify(id);
		stats.getBallisticRoFMult().unmodify(id);
                stats.getBallisticWeaponFluxCostMod().unmodify(id);
		stats.getMaxSpeed().unmodify(id);
		stats.getMaxTurnRate().unmodify(id);
		stats.getTurnAcceleration().unmodify(id);
		stats.getAcceleration().unmodify(id);
		stats.getDeceleration().unmodify(id);
                ship.setWeaponGlow(0f, Sneed, EnumSet.of(WeaponType.BALLISTIC));
                ship.setWeaponGlow(0f, Sneed2, EnumSet.of(WeaponType.ENERGY));
                activeTime = 0f;
	}
	
        @Override
	public StatusData getStatusData(int index, State state, float effectLevel) {
		if (index == 0 && activeTime <= 3f) {
			return new StatusData("+40% "+eis_highballisticfocus1+(Misc.getRoundedValueMaxOneAfterDecimal(3-activeTime)), false);
		} else if (index == 1) {
                        if (activeTime <= 3f) {return new StatusData("+80% "+eis_highballisticfocus2, false);}
                        return new StatusData("+40% "+eis_highballisticfocus2, false);
		} else if (index == 2) {
                        if (activeTime <= 3f) {return new StatusData("-40% "+eis_highballisticfocus3, false);}
			return new StatusData("-40% "+eis_highballisticfocus3, false);
		} else if (index == 3) {
			return new StatusData("+20 "+eis_highballisticfocus4, false);
		}
		return null;
	}
}
