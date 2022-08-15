package data.scripts.hullmods;

import com.fs.starfarer.api.Global;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShieldAPI.ShieldType;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.combat.WeaponAPI.WeaponType;
import com.fs.starfarer.api.combat.listeners.WeaponBaseRangeModifier;
import com.fs.starfarer.api.impl.campaign.ids.HullMods;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.scripts.util.MagicIncompatibleHullmods;
import java.util.HashSet;
import java.util.Set;

public class eis_aquila extends BaseHullMod {
    private static Map speed = new HashMap();
    private static Map speedcheck = new HashMap();
    private static Map peak_mult = new HashMap();
    private static Set<String> BLOCKED_HULLMODS = new HashSet();
    private static Color color = new Color(255, 135, 240, 200);
    static {
        speed.put(HullSize.FRIGATE, 50f);
        speed.put(HullSize.DESTROYER, 50f);
        speed.put(HullSize.CRUISER, 50f);
        speed.put(HullSize.CAPITAL_SHIP, 25f);
        speedcheck.put(HullSize.FRIGATE, 66f);
        speedcheck.put(HullSize.DESTROYER, 66f);
        speedcheck.put(HullSize.CRUISER, 67f);
        speedcheck.put(HullSize.CAPITAL_SHIP, 33f);
        peak_mult.put(HullSize.FRIGATE, 85f);
        peak_mult.put(HullSize.DESTROYER, 85f);
        peak_mult.put(HullSize.CRUISER, 85f);
        peak_mult.put(HullSize.CAPITAL_SHIP, 75f);
        BLOCKED_HULLMODS.add(HullMods.SAFETYOVERRIDES);
    }
    
    private static String aquilaIcon = "graphics/icons/hullsys/infernium_injector.png";
    private static String aquilaTitle = Global.getSettings().getString("eis_ironshell", "eis_aquilaTitle");
    private static String aquilaText1 = Global.getSettings().getString("eis_ironshell", "eis_aquilaText1");
    private static String aquilaText1b = Global.getSettings().getString("eis_ironshell", "eis_aquilaText1b");
    private static String aquilaText4b = Global.getSettings().getString("eis_ironshell", "eis_aquilaText4b");
    private static String aquilaText2 = Global.getSettings().getString("eis_ironshell", "eis_aquilaText2");
    private static String aquilaText3 = Global.getSettings().getString("eis_ironshell", "eis_aquilaText3");
    private static String aquilaText4d = Global.getSettings().getString("eis_ironshell", "eis_aquilaText4d");
    private static String aquilaText4 = Global.getSettings().getString("eis_ironshell", "eis_aquilaText4");
    private static String aquilaText4c = Global.getSettings().getString("eis_ironshell", "eis_aquilaText4c");
    private static String aquilaText5 = Global.getSettings().getString("eis_ironshell", "eis_aquilaText5");
    private static String aquilaText5a = Global.getSettings().getString("eis_ironshell", "eis_aquilaText5a");
    private static String aquilaText5b = Global.getSettings().getString("eis_ironshell", "eis_aquilaText5b");
    private static String aquilaText6 = Global.getSettings().getString("eis_ironshell", "eis_aquilaText6");
    private static String aquilaText7 = Global.getSettings().getString("eis_ironshell", "eis_aquilaText7");
    private static String ballistic = Global.getSettings().getString("eis_ironshell", "eis_ballistic");
    private static String energy = Global.getSettings().getString("eis_ironshell", "eis_energy");
    
    private static final float SPEED_BOOST_PERCENT = 75f;
    private static final float BONUS_MAX_CAP = 1500f;
    private static final float ENGAGEMENT_REDUCTION_PERCENT = 50f;
    private static final float ZERO_FLUX_LEVEL = 5f;
    private static final float VENT_RATE_BONUS = 25f;
    private static final float DAMAGE_BONUS = 10f;
    private static final float DAMAGE_BONUS2 = 10f;
    private static boolean getreal = false;

    @Override
    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
        getreal = false;
        if (ship.getOwner() == 0) { //(At least the AI can understand its range blocks now I think hopefully?)
            MutableShipStatsAPI stats = ship.getMutableStats();
            if (!stats.getBallisticWeaponRangeBonus().isUnmodified()) {
                if (stats.getBallisticWeaponRangeBonus().getFlatBonus() > 0) {
                    stats.getBallisticWeaponRangeBonus().modifyFlat(id, -stats.getBallisticWeaponRangeBonus().getFlatBonus());
                    getreal = true;
                }
                if (stats.getBallisticWeaponRangeBonus().getBonusMult() > 1) {
                    stats.getBallisticWeaponRangeBonus().modifyMult(id, 1/stats.getBallisticWeaponRangeBonus().getBonusMult());
                    getreal = true;
                }
                /*if (stats.getBallisticWeaponRangeBonus().getPercentMod() > 0) {
                    stats.getBallisticWeaponRangeBonus().modifyPercent(id, -stats.getBallisticWeaponRangeBonus().getPercentMod());
                    getreal = true;
                }
                if (stats.getBallisticWeaponRangeBonus().getMult() > 1) {
                    stats.getBallisticWeaponRangeBonus().modifyMult(id, 1/stats.getBallisticWeaponRangeBonus().getMult());
                    getreal = true;
                }*/
            }
            if (!stats.getBeamPDWeaponRangeBonus().isUnmodified()) {
                if (stats.getBeamPDWeaponRangeBonus().getFlatBonus() > 0) {
                    stats.getBeamPDWeaponRangeBonus().modifyFlat(id, -stats.getBeamPDWeaponRangeBonus().getFlatBonus());
                    getreal = true;
                }
                if (stats.getBeamPDWeaponRangeBonus().getBonusMult() > 1) {
                    stats.getBeamPDWeaponRangeBonus().modifyMult(id, 1/stats.getBeamPDWeaponRangeBonus().getBonusMult());
                    getreal = true;
                }
                /*if (stats.getBeamPDWeaponRangeBonus().getPercentMod() > 0) {
                    stats.getBeamPDWeaponRangeBonus().modifyPercent(id, -stats.getBeamPDWeaponRangeBonus().getPercentMod());
                    getreal = true;
                }
                if (stats.getBeamPDWeaponRangeBonus().getMult() > 1) {
                    stats.getBeamPDWeaponRangeBonus().modifyMult(id, 1/stats.getBeamPDWeaponRangeBonus().getMult());
                    getreal = true;
                }*/
            }
            if (!stats.getBeamWeaponRangeBonus().isUnmodified()) {
                if (stats.getBeamWeaponRangeBonus().getFlatBonus() > 0) {
                    stats.getBeamWeaponRangeBonus().modifyFlat(id, -stats.getBeamWeaponRangeBonus().getFlatBonus());
                    getreal = true;
                }
                if (stats.getBeamWeaponRangeBonus().getBonusMult() > 1) {
                    stats.getBeamWeaponRangeBonus().modifyMult(id, 1/stats.getBeamWeaponRangeBonus().getBonusMult());
                    getreal = true;
                }
                /*if (stats.getBeamWeaponRangeBonus().getPercentMod() > 0) {
                    stats.getBeamWeaponRangeBonus().modifyPercent(id, -stats.getBeamWeaponRangeBonus().getPercentMod());
                    getreal = true;
                }
                if (stats.getBeamWeaponRangeBonus().getMult() > 1) {
                    stats.getBeamWeaponRangeBonus().modifyMult(id, 1/stats.getBeamWeaponRangeBonus().getMult());
                    getreal = true;
                }*/
            }
            if (!stats.getEnergyWeaponRangeBonus().isUnmodified()) {
                if (stats.getEnergyWeaponRangeBonus().getFlatBonus() > 0) {
                    stats.getEnergyWeaponRangeBonus().modifyFlat(id, -stats.getEnergyWeaponRangeBonus().getFlatBonus());
                    getreal = true;
                }
                if (stats.getEnergyWeaponRangeBonus().getBonusMult() > 1) {
                    stats.getEnergyWeaponRangeBonus().modifyMult(id, 1/stats.getEnergyWeaponRangeBonus().getBonusMult());
                    getreal = true;
                }
                /*if (stats.getEnergyWeaponRangeBonus().getPercentMod() > 0) {
                    stats.getEnergyWeaponRangeBonus().modifyPercent(id, -stats.getEnergyWeaponRangeBonus().getPercentMod());
                    getreal = true;
                }
                if (stats.getEnergyWeaponRangeBonus().getMult() > 1) {
                    stats.getEnergyWeaponRangeBonus().modifyMult(id, 1/stats.getEnergyWeaponRangeBonus().getMult());
                    getreal = true;
                }*/
            }
            if (!stats.getNonBeamPDWeaponRangeBonus().isUnmodified()) {
                if (stats.getNonBeamPDWeaponRangeBonus().getFlatBonus() > 0) {
                    stats.getNonBeamPDWeaponRangeBonus().modifyFlat(id, -stats.getNonBeamPDWeaponRangeBonus().getFlatBonus());
                    getreal = true;
                }
                if (stats.getNonBeamPDWeaponRangeBonus().getBonusMult() > 1) {
                    stats.getNonBeamPDWeaponRangeBonus().modifyMult(id, 1/stats.getNonBeamPDWeaponRangeBonus().getBonusMult());
                    getreal = true;
                }
                /*if (stats.getBeamPDWeaponRangeBonus().getPercentMod() > 0) {
                    stats.getNonBeamPDWeaponRangeBonus().modifyPercent(id, -stats.getNonBeamPDWeaponRangeBonus().getPercentMod());
                    getreal = true;
                }
                if (stats.getNonBeamPDWeaponRangeBonus().getMult() > 1) {
                    stats.getNonBeamPDWeaponRangeBonus().modifyMult(id, 1/stats.getNonBeamPDWeaponRangeBonus().getMult());
                    getreal = true;
                }*/
            }
        }
        /*if (!stats.getMissileWeaponRangeBonus().isUnmodified()) {
            if (stats.getMissileWeaponRangeBonus().getFlatBonus() > 0) {
                stats.getMissileWeaponRangeBonus().modifyFlat(id, -stats.getMissileWeaponRangeBonus().getFlatBonus());
            }
            if (stats.getMissileWeaponRangeBonus().getPercentMod() > 0) {
                stats.getMissileWeaponRangeBonus().modifyPercent(id, -stats.getMissileWeaponRangeBonus().getPercentMod());
            }
            if (stats.getMissileWeaponRangeBonus().getMult() > 1) {
                stats.getMissileWeaponRangeBonus().modifyMult(id, 1/stats.getMissileWeaponRangeBonus().getMult());
            }
        } Although it should check... I don't think some of the missiles work like that... haha..*/
        for (String tmp : BLOCKED_HULLMODS) {
            if (ship.getVariant().getHullMods().contains(tmp)) {
                MagicIncompatibleHullmods.removeHullmodWithWarning(ship.getVariant(), tmp, "eis_aquila");
            }
        }
        ship.addListener(new AquilaRangeModifier());
    }
	public static class AquilaRangeModifier implements WeaponBaseRangeModifier {
		public AquilaRangeModifier() {
		}
		
		public float getWeaponBaseRangePercentMod(ShipAPI ship, WeaponAPI weapon) {
			return 0;
		}
		public float getWeaponBaseRangeMultMod(ShipAPI ship, WeaponAPI weapon) {
			return 1f;
		}
		public float getWeaponBaseRangeFlatMod(ShipAPI ship, WeaponAPI weapon) {
			if (weapon.getSlot() == null || weapon.getSlot().getWeaponType() != WeaponType.MISSILE) {
				return 0f;
			}
			float bonus = 0;
			float base = weapon.getSpec().getMaxRange();
			if (base > BONUS_MAX_CAP) {
				bonus = BONUS_MAX_CAP - base;
			}
			if (bonus > 0) bonus = 0;
			return bonus;
		}
	}

    @Override
    public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {  
        if (stats.getMaxSpeed().base >= (Float) speedcheck.get(hullSize)) {
            stats.getMaxSpeed().modifyFlat(id, (Float) speed.get(hullSize));
            stats.getAcceleration().modifyFlat(id, (Float) speed.get(hullSize) * 1.6f);
            stats.getDeceleration().modifyFlat(id, (Float) speed.get(hullSize) * 0.8f);
        } else {
            stats.getMaxSpeed().modifyFlat(id, (Float) stats.getMaxSpeed().base * 0.75f);
            stats.getAcceleration().modifyFlat(id, (Float) stats.getAcceleration().base * 1.2f); 
            stats.getDeceleration().modifyFlat(id, (Float) stats.getDeceleration().base * 0.6f);
        }
        stats.getZeroFluxMinimumFluxLevel().modifyFlat(id, ZERO_FLUX_LEVEL * 0.01f);
        stats.getPeakCRDuration().modifyMult(id, (Float) peak_mult.get(hullSize) * 0.01f);
        if (stats.getVariant().getHullSpec().getShieldType() != ShieldType.PHASE) {
            stats.getEnergyWeaponDamageMult().modifyPercent (id, DAMAGE_BONUS2);
            stats.getBallisticWeaponDamageMult().modifyPercent (id, DAMAGE_BONUS);
            stats.getVentRateMult().modifyPercent(id, VENT_RATE_BONUS);
        }
        stats.getFighterWingRange().modifyMult(id, 0.5f);
    }

    /*@Override
    public String getDescriptionParam(int index, HullSize hullSize) {
        if (index == 0) {
            return "" + ((Float) speed.get(HullSize.CRUISER)).intValue();
        }
        if (index == 1) {
            return "" + ((Float) speed.get(HullSize.CAPITAL_SHIP)).intValue();
        }
        if (index == 2) {
            return "" + Misc.getRoundedValue(VENT_RATE_BONUS) + "%";
        }
        if (index == 3) {
            return "" + Misc.getRoundedValue(ZERO_FLUX_LEVEL) + "%";
        }
        if (index == 4) {
            return "" + ((int) (100f - (Float) peak_mult.get(HullSize.CRUISER))) + "%"; //float
        }
        if (index == 5) {
            return "" + ((int) (100f - (Float) peak_mult.get(HullSize.CAPITAL_SHIP))) + "%"; //float
        }
        return null;
    }*/

    @Override
    public boolean isApplicableToShip(ShipAPI ship) {
        return !((ship.getVariant().getHullSize() == HullSize.FRIGATE || ship.getVariant().getHullSize() == HullSize.DESTROYER) || (ship.getVariant().hasHullMod(HullMods.CIVGRADE) && !ship.getVariant().hasHullMod(HullMods.MILITARIZED_SUBSYSTEMS)) || ship.getVariant().hasHullMod(HullMods.SAFETYOVERRIDES));
    }

    /*@Override
    public String getUnapplicableReason(ShipAPI ship) {
        if (ship != null && ship.getHullSize() != HullSize.CAPITAL_SHIP && ship.getHullSize() != HullSize.CRUISER) {
            return "Can only be installed on cruisers and capital ships";
        }
        if (ship.getVariant().hasHullMod(HullMods.CIVGRADE) && !ship.getVariant().hasHullMod(HullMods.MILITARIZED_SUBSYSTEMS)) {
            return "Can not be installed on civilian ships";
        }
        return null;
    }*/

    @Override
    public void advanceInCombat(ShipAPI ship, float amount) {
        ship.getEngineController().fadeToOtherColor(this, color, null, 1f, 1f);
        if (ship.getParentStation() == null && ship.getChildModulesCopy() != null && !ship.getChildModulesCopy().isEmpty()) {
            for (ShipAPI childModulesCopy : ship.getChildModulesCopy()) {
                childModulesCopy.getEngineController().fadeToOtherColor(this, color, null, 1f, 1f);
                MutableShipStatsAPI stats = childModulesCopy.getMutableStats();
                if (stats.getBallisticWeaponRangeBonus().getBonusMult() > 1f) {stats.getBallisticWeaponRangeBonus().modifyMult("eis_aquila", 1/stats.getBallisticWeaponRangeBonus().getBonusMult());}
                if (stats.getBallisticWeaponRangeBonus().getFlatBonus() > 0f) {stats.getBallisticWeaponRangeBonus().modifyFlat("eis_aquila", -stats.getBallisticWeaponRangeBonus().getFlatBonus());}
                if (stats.getBeamPDWeaponRangeBonus().getBonusMult() > 1f) {stats.getBeamPDWeaponRangeBonus().modifyMult("eis_aquila", 1/stats.getBeamPDWeaponRangeBonus().getBonusMult());}
                if (stats.getBeamPDWeaponRangeBonus().getFlatBonus() > 0f) {stats.getBeamPDWeaponRangeBonus().modifyFlat("eis_aquila", -stats.getBeamPDWeaponRangeBonus().getFlatBonus());}
                if (stats.getBeamWeaponRangeBonus().getBonusMult() > 1f) {stats.getBeamWeaponRangeBonus().modifyMult("eis_aquila", 1/stats.getBeamWeaponRangeBonus().getBonusMult());}
                if (stats.getBeamWeaponRangeBonus().getFlatBonus() > 0f) {stats.getBeamWeaponRangeBonus().modifyFlat("eis_aquila", -stats.getBeamWeaponRangeBonus().getFlatBonus());}
                if (stats.getEnergyWeaponRangeBonus().getBonusMult() > 1f) {stats.getEnergyWeaponRangeBonus().modifyMult("eis_aquila", 1/stats.getEnergyWeaponRangeBonus().getBonusMult());}
                if (stats.getEnergyWeaponRangeBonus().getFlatBonus() > 0f) {stats.getEnergyWeaponRangeBonus().modifyFlat("eis_aquila", -stats.getEnergyWeaponRangeBonus().getFlatBonus());}
                if (stats.getNonBeamPDWeaponRangeBonus().getBonusMult() > 1f) {stats.getNonBeamPDWeaponRangeBonus().modifyMult("eis_aquila", 1/stats.getNonBeamPDWeaponRangeBonus().getBonusMult());}
                if (stats.getNonBeamPDWeaponRangeBonus().getFlatBonus() > 0f) {stats.getNonBeamPDWeaponRangeBonus().modifyFlat("eis_aquila", -stats.getNonBeamPDWeaponRangeBonus().getFlatBonus());}
            }
        }
        //ship.getEngineController().extendFlame(this, 0.1f, 0.1f, 0.1f);
    }
    
    @Override
    public void addPostDescriptionSection(TooltipMakerAPI tooltip, ShipAPI.HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
        float HEIGHT = 50f;
        float PAD = 10f;
        Color YELLOW = new Color(241, 199, 0);

        TooltipMakerAPI aquila = tooltip.beginImageWithText(aquilaIcon, HEIGHT);
        aquila.addPara(aquilaTitle, 0f, YELLOW, aquilaTitle);
        /*if (ship != null) {
            if (ship.getMutableStats().getMaxSpeed().base >= (Float) speedcheck.get(hullSize)) {
                aquila.addPara(aquilaText1, 0f, Misc.getPositiveHighlightColor(), "" + Math.round((Float) speed.get(hullSize)));
            } else {
                aquila.addPara(aquilaText1, 0f, Misc.getPositiveHighlightColor(), "" + Math.round(ship.getMutableStats().getMaxSpeed().base * 0.75f));
            }
        }*/
        aquila.addPara(aquilaText1, 0f, Misc.getPositiveHighlightColor(),
                "" + Math.round((Float) speed.get(HullSize.CRUISER)),
                "" + Math.round((Float) speed.get(HullSize.CAPITAL_SHIP)),
                Math.round((SPEED_BOOST_PERCENT)) + "%");
        if (ship != null) {
            aquila.addPara(aquilaText1b, 0f, Misc.getPositiveHighlightColor(),
                    ship.getHullSpec().getHullNameWithDashClass(),
                    "" + Math.round(Math.min((Float) speed.get(hullSize), ship.getMutableStats().getMaxSpeed().base * 0.75f)));
            if (ship.getVariant().getHullSpec().getShieldType() != ShieldType.PHASE) {
                Color[] colors = new Color[3];
                colors[0] = Misc.getBallisticMountColor();
                colors[1] = Misc.getEnergyMountColor();
                colors[2] = Misc.getPositiveHighlightColor();
                aquila.addPara(aquilaText4b, 0f, colors, ballistic, energy, Math.round(DAMAGE_BONUS) + "%");
                aquila.addPara(aquilaText2, 0f, Misc.getPositiveHighlightColor(), Math.round(VENT_RATE_BONUS) + "%");
            }
        }
        aquila.addPara(aquilaText3, 0f, Misc.getPositiveHighlightColor(), Math.round(ZERO_FLUX_LEVEL) + "%");
        if (ship != null) {
            aquila.addPara(aquilaText4, 0f, Misc.getNegativeHighlightColor(),
                    //Math.round(100f - (Float) peak_mult.get(HullSize.CRUISER)) + "%",
                    Math.round(100f - (Float) peak_mult.get(hullSize)) + "%");
            for (WeaponAPI weapon : ship.getAllWeapons()) {
                if (weapon.getType() == WeaponType.MISSILE && weapon.getRange() > BONUS_MAX_CAP) { aquila.addPara(aquilaText4d, 0f, Misc.getNegativeHighlightColor(), Misc.getRoundedValue(BONUS_MAX_CAP));
                break;}
            }
            
        }
        aquila.addPara(aquilaText4c, 0f, Misc.getNegativeHighlightColor(), Math.round(ENGAGEMENT_REDUCTION_PERCENT) + "%");
        tooltip.addImageWithText(PAD);
        if (getreal) {
            tooltip.addPara(aquilaText5b, 10f, Misc.getNegativeHighlightColor(), aquilaText5);
        } else {if (ship != null && ship.isShipWithModules()) tooltip.addPara(aquilaText5a, 10f, Misc.getNegativeHighlightColor(), aquilaText5a); else {tooltip.addPara(aquilaText5, 10f, Misc.getNegativeHighlightColor(), aquilaText5);}}
        tooltip.addPara(aquilaText7, 5f, Misc.getNegativeHighlightColor(), aquilaText7);
        tooltip.addPara(aquilaText6, 5f, Misc.getNegativeHighlightColor(), aquilaText6);

    }
    
    @Override
    public int getDisplaySortOrder() {
        return 168;
    }
    
    @Override
    public Color getNameColor() {
        if (getreal) {return Misc.getNegativeHighlightColor();}
        return null;
    }
}
