package data.scripts.shipsystems;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import com.fs.starfarer.api.util.Misc;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class eis_damper extends BaseShipSystemScript {
	private static final Object KEY_JITTER = new Object();
	private static final float DAMAGE_REDUCTION_PERCENT = 60f;
	private static final float DAMAGE_REDUCTION_PERCENT2 = 50f;
	
	private static final Color JITTER_UNDER_COLOR = new Color(255,165,90,155);
	//private static final Color JITTER_COLOR = new Color(255,165,90,55);
        
        private static String Icon = "graphics/icons/hullsys/damper_field.png";
        private static String StatusTitle = Global.getSettings().getString("eis_ironshell", "eis_damper1"); // Damper Subfield (Fighter)
        private static String StatusText = Global.getSettings().getString("eis_ironshell", "eis_damper2"); //%s less damage taken
        private static String StatusText2 = Global.getSettings().getString("eis_ironshell", "eis_damper3"); //%s hard flux dissipation
        private static String StatusText3 = Global.getSettings().getString("eis_ironshell", "eis_damper4"); //0 Shield Upkeep
        private boolean ShieldOn = false;
        
	
	public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
		ShipAPI ship = null;
		if (stats.getEntity() instanceof ShipAPI) {
			ship = (ShipAPI) stats.getEntity();
		} else {
			return;
		}
                ShieldOn = ship.getShield() != null && ship.getShield().isOn();
                stats.getHardFluxDissipationFraction().modifyFlat(id, DAMAGE_REDUCTION_PERCENT / 100f);
                stats.getShieldUpkeepMult().modifyMult(id, 0f);
		
		if (effectLevel > 0) {
                    // But why are all the stuff uncommented? So it's more performance friendly...
			//float jitterLevel = effectLevel;
			//float maxRangeBonus = 3f;
			//float jitterRangeBonus = jitterLevel * maxRangeBonus;
			for (ShipAPI fighter : getFighters(ship)) {
				if (fighter.isHulk()) continue;
				MutableShipStatsAPI fStats = fighter.getMutableStats();
                                fStats.getShieldDamageTakenMult().modifyMult(id, 0.5f * effectLevel);
                                fStats.getHullDamageTakenMult().modifyMult(id, 0.5f * effectLevel);
                                fStats.getArmorDamageTakenMult().modifyMult(id, 0.5f * effectLevel);
                                fStats.getEmpDamageTakenMult().modifyMult(id, 0.5f * effectLevel);
				//if (effectLevel > 0) {
					//fighter.setJitterUnder(KEY_JITTER, JITTER_COLOR, 0.25f, 1, 0f, effectLevel);
					fighter.setJitter(KEY_JITTER, JITTER_UNDER_COLOR, effectLevel, 1, 0f, 0 + effectLevel);
				//}
			}
                    if (Global.getCombatEngine().getPlayerShip() == ship) {
                        Global.getCombatEngine().maintainStatusForPlayerShip("eis_damper", Icon, StatusTitle, Misc.getRoundedValueMaxOneAfterDecimal(DAMAGE_REDUCTION_PERCENT2 * effectLevel) + StatusText, false);
                    }
		}
	}
	
	private List<ShipAPI> getFighters(ShipAPI carrier) {
		List<ShipAPI> result = new ArrayList<ShipAPI>();
		for (ShipAPI ship : Global.getCombatEngine().getShips()) {
			if (!ship.isFighter()) continue;
			if (ship.getWing() == null) continue;
			if (ship.getWing().getSourceShip() == carrier) {
				result.add(ship);
			}
		}
		return result;
	}
	
	
        @Override
	public void unapply(MutableShipStatsAPI stats, String id) {
		ShipAPI ship = null;
		if (stats.getEntity() instanceof ShipAPI) {
			ship = (ShipAPI) stats.getEntity();
		} else {
			return;
		}
                ShieldOn = false;
                stats.getHardFluxDissipationFraction().unmodifyFlat(id);
                stats.getShieldUpkeepMult().unmodifyMult(id);
		for (ShipAPI fighter : getFighters(ship)) {
			if (fighter.isHulk()) continue;
			MutableShipStatsAPI fStats = fighter.getMutableStats();
                        fStats.getShieldDamageTakenMult().unmodifyMult(id);
                        fStats.getHullDamageTakenMult().unmodify(id);
                        fStats.getArmorDamageTakenMult().unmodify(id);
                        fStats.getEmpDamageTakenMult().unmodify(id);
		}
	}
	
	
	public StatusData getStatusData(int index, State state, float effectLevel) {
		/*if (index == 0) {
			return new StatusData("-" + Misc.getRoundedValueMaxOneAfterDecimal(DAMAGE_REDUCTION_PERCENT * effectLevel) + "% less damage taken (fighters)", false);
		}*/
		if (index == 0 && ShieldOn) {
			return new StatusData(Misc.getRoundedValueMaxOneAfterDecimal(DAMAGE_REDUCTION_PERCENT * effectLevel) + StatusText2, false);
		}
                if (index == 1 && ShieldOn) {
                        return new StatusData(StatusText3, false);
                }
		return null;
	}
}
