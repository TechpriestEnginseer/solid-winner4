package data.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.characters.ShipSkillEffect;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;

public class eis_xiv {
	public static final float CAPACITY_MULT = 1.1f;
	public static final float DISSIPATION_MULT = 1.1f;
	public static final float HANDLING_MULT = 1.09f; //"+8%" to how modifiers work.
        public static final float ARMOR_BONUS_MULT = 1.1f;
        public static String poopystinky1 = Global.getSettings().getString("eis_ironshell", "eis_xiv1");
        public static String poopystinky2 = Global.getSettings().getString("eis_ironshell", "eis_xiv2");
        public static String poopystinky3 = Global.getSettings().getString("eis_ironshell", "eis_xiv3");

	public static class Level1 implements ShipSkillEffect {

		public void apply(MutableShipStatsAPI stats, HullSize hullSize, String id, float level) {
                    if (stats.getVariant() != null) {
                        if (stats.getVariant().hasHullMod("fourteenth") || (stats.getVariant().getHullSpec().hasTag("heg_aux_bp") && "Hegemony".equals(stats.getVariant().getHullSpec().getManufacturer()))) {
                            stats.getArmorBonus().modifyMult(id, ARMOR_BONUS_MULT);
                            stats.getHullBonus().modifyMult(id, ARMOR_BONUS_MULT);
                        };
                    }
		}
		
		public void unapply(MutableShipStatsAPI stats, HullSize hullSize, String id) {
                        stats.getArmorBonus().unmodifyMult(id);
                        stats.getHullBonus().unmodifyMult(id);
		}
		
		public String getEffectDescription(float level) {
			return poopystinky1;
		}
		
		public String getEffectPerLevelDescription() {
			return null;
		}
		
		public ScopeDescription getScopeDescription() {
			return ScopeDescription.PILOTED_SHIP;
		}
	}

	public static class Level2 implements ShipSkillEffect {

		public void apply(MutableShipStatsAPI stats, HullSize hullSize, String id, float level) {
                    if (stats.getVariant() != null) {
                        if (stats.getVariant().hasHullMod("fourteenth") || (stats.getVariant().getHullSpec().hasTag("heg_aux_bp") && "Hegemony".equals(stats.getVariant().getHullSpec().getManufacturer()))) {
                            /*stats.getMaxSpeed().modifyMult(id, HANDLING_MULT);
                            stats.getAcceleration().modifyMult(id, HANDLING_MULT);
                            stats.getDeceleration().modifyMult(id, HANDLING_MULT);
                            stats.getMaxTurnRate().modifyMult(id, HANDLING_MULT);
                            stats.getTurnAcceleration().modifyMult(id, HANDLING_MULT);*/
                            stats.getMaxSpeed().unmodifyMult("fourteenth");
                            stats.getAcceleration().unmodifyMult("fourteenth");
                            stats.getDeceleration().unmodifyMult("fourteenth");
                            stats.getMaxTurnRate().unmodifyMult("fourteenth");
                            stats.getTurnAcceleration().unmodifyMult("fourteenth");
                        };
                    }
		}
		
		public void unapply(MutableShipStatsAPI stats, HullSize hullSize, String id) {
                    /*stats.getMaxSpeed().unmodifyMult(id);
                    stats.getAcceleration().unmodifyMult(id);
                    stats.getDeceleration().unmodifyMult(id);
                    stats.getMaxTurnRate().unmodifyMult(id);
                    stats.getTurnAcceleration().unmodifyMult(id);*/
		}	
		
		public String getEffectDescription(float level) {
			return poopystinky2;
		}
		
		public String getEffectPerLevelDescription() {
			return null;
		}
		
		public ScopeDescription getScopeDescription() {
			return ScopeDescription.PILOTED_SHIP;
		}
	}
        
	public static class Level3 implements ShipSkillEffect {

		public void apply(MutableShipStatsAPI stats, HullSize hullSize, String id, float level) {
                    if (stats.getVariant() != null) {
                        if (stats.getVariant().hasHullMod("fourteenth")) {
                            stats.getFluxCapacity().modifyMult(id, CAPACITY_MULT);
                            stats.getFluxDissipation().modifyMult(id, DISSIPATION_MULT);
                        };
                    }
		
		}
		
		public void unapply(MutableShipStatsAPI stats, HullSize hullSize, String id) {
                        stats.getFluxCapacity().unmodifyMult(id);
                        stats.getFluxDissipation().unmodifyMult(id);
		}	
		
		public String getEffectDescription(float level) {
			return poopystinky3;
		}
		
		public String getEffectPerLevelDescription() {
			return null;
		}
		
		public ScopeDescription getScopeDescription() {
			return ScopeDescription.PILOTED_SHIP;
		}
	}
	
	
}
