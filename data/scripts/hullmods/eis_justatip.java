package data.scripts.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamageAPI;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.GuidedMissileAI;
import com.fs.starfarer.api.combat.MissileAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.combat.WeaponAPI.WeaponType;
import com.fs.starfarer.api.combat.listeners.DamageDealtModifier;
import com.fs.starfarer.api.loading.MissileSpecAPI;
import com.fs.starfarer.api.loading.WeaponSpecAPI;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONException;
import org.lwjgl.util.vector.Vector2f;

public class eis_justatip extends BaseHullMod {
    private static String Icon = "graphics/icons/hullsys/missile_racks.png";
    private static String Title = Global.getSettings().getString("eis_ironshell", "eis_justatip_title");
    private static String Text0 = Global.getSettings().getString("eis_ironshell", "eis_justatip_text0");
    private static String Text = Global.getSettings().getString("eis_ironshell", "eis_justatip_text");
    private static String Text9 = Global.getSettings().getString("eis_ironshell", "eis_justatip_text9");
    private static String Text2 = Global.getSettings().getString("eis_ironshell", "eis_justatip_text2");
    private static String Text3 = Global.getSettings().getString("eis_ironshell", "eis_justatip_text3");
    private static String Text7 = Global.getSettings().getString("eis_ironshell", "eis_justatip_text7");
    private static String Text8 = Global.getSettings().getString("eis_ironshell", "eis_justatip_text8");
    private static String Text4 = Global.getSettings().getString("eis_ironshell", "eis_justatip_text4");
    private static String Text6 = Global.getSettings().getString("eis_ironshell", "eis_justatip_text6");
    private static String Text5 = Global.getSettings().getString("eis_ironshell", "eis_justatip_text5");
    
    private static String each = Global.getSettings().getString("eis_ironshell", "eis_each");
    private static float sdamagenumber = 150f;
    private static float damagenumber = 100f;
    private static float halfdamagenumber = 50f;

    @Override
	public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
            if (ship.getHullSpec().isBuiltInMod("eis_justatip") || ship.getVariant().getSMods().contains("eis_justatip")) {
                ship.addListener(new eis_justtip(50f, sdamagenumber));
            } else {
		ship.addListener(new eis_justtip(100f, damagenumber));
            }
	}
        
    @Override
        public void applyEffectsToFighterSpawnedByShip(ShipAPI fighter, ShipAPI ship, String id)  {
            if (ship.getHullSpec().isBuiltInMod("eis_justatip") || ship.getVariant().getSMods().contains("eis_justatip")) {
                fighter.addListener(new eis_justtip(50f, (ship.getVariant() != null && ship.getVariant().getFittedWings() != null) ? ship.getVariant().getFittedWings().size() > 1 ? sdamagenumber/ship.getVariant().getFittedWings().size() : sdamagenumber : 25f));
            } else {
		fighter.addListener(new eis_justtip(100f,(ship.getVariant() != null && ship.getVariant().getFittedWings() != null) ? ship.getVariant().getFittedWings().size() > 1 ? damagenumber/ship.getVariant().getFittedWings().size() : damagenumber : 25f));
            }
        }
        
        @Override
        public void addPostDescriptionSection(TooltipMakerAPI tooltip, ShipAPI.HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
            float HEIGHT = 50f;
            float PAD = 10f;
            Color YELLOW = new Color(241, 199, 0);

            TooltipMakerAPI Gula = tooltip.beginImageWithText(Icon, HEIGHT);
            Gula.addPara(Title, 0f, YELLOW, Title);
            if (isForModSpec) {
                Gula.addPara(Text0, 0f, Misc.getHighlightColor(), Misc.getRoundedValue(damagenumber));
                Gula.addPara(Text, 0f, Misc.getPositiveHighlightColor(), Misc.getRoundedValue(damagenumber));
                Gula.addPara(Text2, 0f, Misc.getNegativeHighlightColor(), "-"+Misc.getRoundedValue(halfdamagenumber)+"%");
                Gula.addPara(Text3, 0f, Misc.getNegativeHighlightColor(), "-"+Misc.getRoundedValue(halfdamagenumber)+"%");
                Gula.addPara(Text7, 0f, Misc.getNegativeHighlightColor(), "-"+Misc.getRoundedValue(halfdamagenumber)+"%");
                Gula.addPara(Text8, 0f, Misc.getPositiveHighlightColor(), Misc.getRoundedValue(damagenumber)+"%");
                Gula.addPara(Text4, 0f, Misc.getHighlightColor(), Misc.getRoundedValue(halfdamagenumber/2));
                Gula.addPara(Text6, Misc.getNegativeHighlightColor(), 0f);
                Gula.addPara(Text5, 0f, Misc.getGrayColor(), Misc.getHighlightColor(), Misc.getRoundedValue(halfdamagenumber), Misc.getRoundedValue(sdamagenumber));
                tooltip.addImageWithText(PAD);
            } else if (ship.getHullSpec().isBuiltInMod("eis_justatip") || ship.getVariant().getSMods().contains("eis_justatip")) {
                Gula.addPara(Text0, 0f, Misc.getPositiveHighlightColor(), Misc.getRoundedValue(halfdamagenumber));
                Gula.addPara(Text, 0f, Misc.getPositiveHighlightColor(), Misc.getRoundedValue(sdamagenumber));
                if (ship.getVariant().hasHullMod("eis_vengeancecore") || ship.getVariant().hasHullMod("eis_zandatsu")) {Gula.addPara(Text9, 0f);}
                Gula.addPara(Text2, 0f, Misc.getNegativeHighlightColor(), "-"+Misc.getRoundedValue(halfdamagenumber)+"%");
                Gula.addPara(Text3, 0f, Misc.getNegativeHighlightColor(), "-"+Misc.getRoundedValue(halfdamagenumber)+"%");
                Gula.addPara(Text7, 0f, Misc.getNegativeHighlightColor(), "-"+Misc.getRoundedValue(halfdamagenumber)+"%");
                if (!ship.getVariant().getFittedWings().isEmpty()) {Gula.addPara(Text8, 0f, Misc.getPositiveHighlightColor(), Misc.getRoundedValue(ship.getVariant().getFittedWings().size() > 1 ? sdamagenumber/ship.getVariant().getFittedWings().size() : sdamagenumber));}
                Gula.addPara(Text4, 0f, Misc.getHighlightColor(), Misc.getRoundedValue(halfdamagenumber/2));
                Gula.addPara(Text6, Misc.getNegativeHighlightColor(), 0f);
                Gula.addPara(Text5, 0f, Misc.getPositiveHighlightColor(), Misc.getHighlightColor(), Misc.getRoundedValue(halfdamagenumber), Misc.getRoundedValue(sdamagenumber));
                tooltip.addImageWithText(PAD);
                boolean diditonce = false;
                Map<String, Boolean> weaponlist = new HashMap<>(); //The hullmods has to be cleared everytime ig.
                for (WeaponAPI weapon : ship.getAllWeapons()) {
                    if (weapon.getType() == WeaponType.MISSILE && !weaponlist.containsKey(weapon.getDisplayName()) && weapon.getDamage().getBaseDamage() >= halfdamagenumber) {
                        weaponlist.put(weapon.getDisplayName(), true);
                        boolean isokaytoadd = true;
                        float water = sdamagenumber;
                        if (weapon.getSpec().getProjectileSpec() instanceof MissileSpecAPI) {
                            if (((MissileSpecAPI)weapon.getSpec().getProjectileSpec()).getBehaviorJSON() != null) {
                                try {if ("MIRV".equals(((MissileSpecAPI)weapon.getSpec().getProjectileSpec()).getBehaviorJSON().getString("behavior"))) {
                                    water /= 2;
                                }} catch (JSONException sex) {}
                                try {if ("PROXIMITY_FUSE".equals(((MissileSpecAPI)weapon.getSpec().getProjectileSpec()).getBehaviorJSON().getString("behavior"))) {
                                    isokaytoadd = false;
                                }} catch (JSONException sex) {}
                            }
                            if (!weapon.usesAmmo() || weapon.getSpec().getAmmoPerSecond() > 0) {
                                    water /= 2;
                            }
                                //if (((MissileSpecAPI)weapon.getSpec().getProjectileSpec()).getHullSpec().getEngineSpec().getMaxTurnRate())
                            if (weapon.getSpec().getTrackingStr() != null && !("None".equals(weapon.getSpec().getTrackingStr()))) {
                                water /= 2;
                            }
                            if (water < 25f) {water = 25f;}
                        }
                        if (isokaytoadd) {if (!diditonce){tooltip.addSectionHeading(ship.getHullSpec().getHullName(), Alignment.MID, 3f);diditonce = true;tooltip.addPara("• %s: %s "+each, 5f, new Color[]{Misc.getMissileMountColor(),Misc.getPositiveHighlightColor()}, weapon.getDisplayName(),"+"+Misc.getRoundedValue(water));} else {tooltip.addPara("• %s: %s "+each, 1f, new Color[]{Misc.getMissileMountColor(),Misc.getPositiveHighlightColor()}, weapon.getDisplayName(),"+"+Misc.getRoundedValue(water));}}
                        }
                }
                if (ship.getVariant().getFittedWings() != null) {boolean diditonce2 = false;Map<String, Boolean> weaponlist2 = new HashMap<>();
                    for (String wing : ship.getVariant().getFittedWings()) {if (Global.getSettings().getFighterWingSpec(wing) != null && Global.getSettings().getFighterWingSpec(wing).getVariant() != null) {diditonce2=false;for (String fighterweaponslot : Global.getSettings().getFighterWingSpec(wing).getVariant().getFittedWeaponSlots()) {WeaponSpecAPI weaponspec = Global.getSettings().getFighterWingSpec(wing).getVariant().getWeaponSpec(fighterweaponslot);
                        if (weaponspec.getType() == WeaponType.MISSILE && !weaponlist2.containsKey(weaponspec.getWeaponName()+wing) && (weaponspec.getDerivedStats().getDamagePerShot() >= halfdamagenumber)) {
                            weaponlist2.put(weaponspec.getWeaponName()+wing, true);
                            boolean isokaytoadd = true;
                            float water = ship.getVariant().getFittedWings().size() > 1 ? sdamagenumber/ship.getVariant().getFittedWings().size() : sdamagenumber;
                            if (weaponspec.getProjectileSpec() instanceof MissileSpecAPI) {
                                if (((MissileSpecAPI)weaponspec.getProjectileSpec()).getBehaviorJSON() != null) {
                                    try {if ("MIRV".equals(((MissileSpecAPI)weaponspec.getProjectileSpec()).getBehaviorJSON().getString("behavior"))) {
                                        water /= 2;
                                    }} catch (JSONException sex) {}
                                    try {if ("PROXIMITY_FUSE".equals(((MissileSpecAPI)weaponspec.getProjectileSpec()).getBehaviorJSON().getString("behavior"))) {
                                        isokaytoadd = false;
                                    }} catch (JSONException sex) {}
                                }
                                if (!weaponspec.usesAmmo() || weaponspec.getAmmoPerSecond() > 0) {
                                        water /= 2;
                                }
                                    //if (((MissileSpecAPI)weapon.getSpec().getProjectileSpec()).getHullSpec().getEngineSpec().getMaxTurnRate())
                                if (weaponspec.getTrackingStr() != null && !("None".equals(weaponspec.getTrackingStr()))) {
                                    water /= 2;
                                }
                                if (water < 25f) {water = 25f;}
                            }
                            if (isokaytoadd) {if (!diditonce2){tooltip.addSectionHeading(Global.getSettings().getFighterWingSpec(wing).getWingName(), Alignment.MID, 3f);diditonce2 = true;tooltip.addPara("• %s: %s "+each, 5f, new Color[]{Misc.getMissileMountColor(),Misc.getPositiveHighlightColor()}, weaponspec.getWeaponName(),"+"+Misc.getRoundedValue(water));} else {tooltip.addPara("• %s: %s "+each, 1f, new Color[]{Misc.getMissileMountColor(),Misc.getPositiveHighlightColor()}, weaponspec.getWeaponName(),"+"+Misc.getRoundedValue(water));}}  
                        }}}} //shhh don't worry about this
                    }
            } else if (!isForModSpec) {
                Gula.addPara(Text0, 0f, Misc.getHighlightColor(), Misc.getRoundedValue(damagenumber));
                Gula.addPara(Text, 0f, Misc.getPositiveHighlightColor(), Misc.getRoundedValue(damagenumber));
                if (ship.getVariant().hasHullMod("eis_vengeancecore") || ship.getVariant().hasHullMod("eis_zandatsu")) {Gula.addPara(Text9, 0f);}
                Gula.addPara(Text2, 0f, Misc.getNegativeHighlightColor(), "-"+Misc.getRoundedValue(halfdamagenumber)+"%");
                Gula.addPara(Text3, 0f, Misc.getNegativeHighlightColor(), "-"+Misc.getRoundedValue(halfdamagenumber)+"%");
                Gula.addPara(Text7, 0f, Misc.getNegativeHighlightColor(), "-"+Misc.getRoundedValue(halfdamagenumber)+"%");
                if (!ship.getVariant().getFittedWings().isEmpty()) {Gula.addPara(Text8, 0f, Misc.getPositiveHighlightColor(), Misc.getRoundedValue(ship.getVariant().getFittedWings().size() > 1 ? damagenumber/ship.getVariant().getFittedWings().size() : damagenumber));}
                Gula.addPara(Text4, 0f, Misc.getHighlightColor(), Misc.getRoundedValue(halfdamagenumber/2));
                Gula.addPara(Text6, Misc.getNegativeHighlightColor(), 0f);
                Gula.addPara(Text5, 0f, Misc.getGrayColor(), Misc.getHighlightColor(), Misc.getRoundedValue(halfdamagenumber), Misc.getRoundedValue(sdamagenumber));
                tooltip.addImageWithText(PAD);
                boolean diditonce = false;
                if (ship != null) {
                    Map<String, Boolean> weaponlist = new HashMap<>(); //The hullmods has to be cleared everytime ig.
                    for (WeaponAPI weapon : ship.getAllWeapons()) {
                        if (weapon.getType() == WeaponType.MISSILE && !weaponlist.containsKey(weapon.getDisplayName()) && weapon.getDamage().getBaseDamage() >= damagenumber) {
                            weaponlist.put(weapon.getDisplayName(), true);
                            boolean isokaytoadd = true;
                            float water = damagenumber;
                            if (weapon.getSpec().getProjectileSpec() instanceof MissileSpecAPI) {
                                if (((MissileSpecAPI)weapon.getSpec().getProjectileSpec()).getBehaviorJSON() != null) {
                                    try {if ("MIRV".equals(((MissileSpecAPI)weapon.getSpec().getProjectileSpec()).getBehaviorJSON().getString("behavior"))) {
                                        water /= 2;
                                    }} catch (JSONException sex) {}
                                    try {if ("PROXIMITY_FUSE".equals(((MissileSpecAPI)weapon.getSpec().getProjectileSpec()).getBehaviorJSON().getString("behavior"))) {
                                        isokaytoadd = false;
                                    }} catch (JSONException sex) {}
                                }
                                if (!weapon.usesAmmo() || weapon.getAmmoPerSecond() > 0) {
                                    water /= 2;
                                }
                                //if (((MissileSpecAPI)weapon.getSpec().getProjectileSpec()).getHullSpec().getEngineSpec().getMaxTurnRate())
                                if (weapon.getSpec().getTrackingStr() != null && !("None".equals(weapon.getSpec().getTrackingStr()))) {
                                    water /= 2;
                                }
                                if (water < 25f) {water = 25f;}
                            }
                            if (isokaytoadd) {if (!diditonce){tooltip.addSectionHeading(ship.getHullSpec().getHullName(), Alignment.MID, 3f);diditonce = true;tooltip.addPara("• %s: %s "+each, 5f, new Color[]{Misc.getMissileMountColor(),Misc.getPositiveHighlightColor()}, weapon.getDisplayName(),"+"+Misc.getRoundedValue(water));} else {tooltip.addPara("• %s: %s "+each, 1f, new Color[]{Misc.getMissileMountColor(),Misc.getPositiveHighlightColor()}, weapon.getDisplayName(),"+"+Misc.getRoundedValue(water));}}
                        }
                    }
                    if (ship.getVariant().getFittedWings() != null) {boolean diditonce2 = false;Map<String, Boolean> weaponlist2 = new HashMap<>();
                    for (String wing : ship.getVariant().getFittedWings()) {if (Global.getSettings().getFighterWingSpec(wing) != null && Global.getSettings().getFighterWingSpec(wing).getVariant() != null) {diditonce2=false;for (String fighterweaponslot : Global.getSettings().getFighterWingSpec(wing).getVariant().getFittedWeaponSlots()) {WeaponSpecAPI weaponspec = Global.getSettings().getFighterWingSpec(wing).getVariant().getWeaponSpec(fighterweaponslot);
                        if ((weaponspec.getType() == WeaponType.MISSILE || weaponspec.getWeaponId().equals("bomb"))  && !weaponlist2.containsKey(weaponspec.getWeaponName()+wing) && (weaponspec.getDerivedStats().getDamagePerShot() >= damagenumber)) {
                            weaponlist2.put(weaponspec.getWeaponName()+wing, true);
                            boolean isokaytoadd = true;
                            float water = ship.getVariant().getFittedWings().size() > 1 ? damagenumber/ship.getVariant().getFittedWings().size() : damagenumber;
                            if (weaponspec.getProjectileSpec() instanceof MissileSpecAPI) {
                                if (((MissileSpecAPI)weaponspec.getProjectileSpec()).getBehaviorJSON() != null) {
                                    try {if ("MIRV".equals(((MissileSpecAPI)weaponspec.getProjectileSpec()).getBehaviorJSON().getString("behavior"))) {
                                        water /= 2;
                                    }} catch (JSONException sex) {}
                                    try {if ("PROXIMITY_FUSE".equals(((MissileSpecAPI)weaponspec.getProjectileSpec()).getBehaviorJSON().getString("behavior"))) {
                                        isokaytoadd = false;
                                    }} catch (JSONException sex) {}
                                }
                                if (!weaponspec.usesAmmo() || weaponspec.getAmmoPerSecond() > 0) {
                                        water /= 2;
                                }
                                    //if (((MissileSpecAPI)weapon.getSpec().getProjectileSpec()).getHullSpec().getEngineSpec().getMaxTurnRate())
                                if (weaponspec.getTrackingStr() != null && !("None".equals(weaponspec.getTrackingStr()))) {
                                    water /= 2;
                                }
                                if (water < 25f) {water = 25f;}
                            }
                            if (isokaytoadd) {if (!diditonce2){tooltip.addSectionHeading(Global.getSettings().getFighterWingSpec(wing).getWingName(), Alignment.MID, 3f);diditonce2 = true;tooltip.addPara("• %s: %s "+each, 5f, new Color[]{Misc.getMissileMountColor(),Misc.getPositiveHighlightColor()}, weaponspec.getWeaponName(),"+"+Misc.getRoundedValue(water));} else {tooltip.addPara("• %s: %s "+each, 1f, new Color[]{Misc.getMissileMountColor(),Misc.getPositiveHighlightColor()}, weaponspec.getWeaponName(),"+"+Misc.getRoundedValue(water));}}  
                        }}}} //shhh don't worry about this
                    }
                }
            }
                   
        }
        
	public static class eis_justtip implements DamageDealtModifier {
		//protected ShipAPI ship;
                protected float actualnumberref;
                protected float damagebonus;
		public eis_justtip(float actualnumberref, float damagebonus) {
			//this.ship = ship;
                        this.actualnumberref = actualnumberref;
                        this.damagebonus = damagebonus;
		}
		
                @Override
		public String modifyDamageDealt(Object param, CombatEntityAPI target, DamageAPI damage, Vector2f point, boolean shieldHit) {
                        if (param instanceof MissileAPI) {
                            if (damage.getBaseDamage() >= actualnumberref) { //Check if base damage is over 100 or 50
                                float actualnumber = damagebonus; //damage.getBaseDamage() > damagenumber ? 100f : damage.getBaseDamage();
                                if (((MissileAPI) param).isGuided() || ((MissileAPI) param).getMissileAI() instanceof GuidedMissileAI) {
                                    actualnumber /= 2; //Divide into 50
                                }
                                if (((MissileAPI) param).isFromMissile()) {
                                    actualnumber /= 2; //Divide into 50 or 25 or 12.5
                                }
                                if (((MissileAPI) param).getWeaponSpec() == null || (!((MissileAPI) param).getWeaponSpec().usesAmmo() || ((MissileAPI) param).getWeaponSpec().getAmmoPerSecond() > 0)) {
                                    actualnumber /= 2; //Divide into 50 or 25 or 12.5
                                }
                                if (actualnumber < 25f) {actualnumber = 25f;} //if less than 25, back to 25
                                damage.getModifier().modifyFlat("eis_justatip", Misc.getRoundedValueFloat(actualnumber/damage.getBaseDamage()));
                                //Global.getCombatEngine().addFloatingText(target.getLocation(), String.valueOf(damage.getModifier().getModifiedValue()), 15f, Color.WHITE, target, 1f, 0.5f);
                            }
                        } else if (param instanceof DamagingProjectileAPI && ((DamagingProjectileAPI)param).isFromMissile()) { //Special exception for Sabots...
                                float actualnumber = damagebonus/4; //damage.getBaseDamage() > damagenumber ? 50f : damage.getBaseDamage()/2f;
                                damage.getModifier().modifyFlat("eis_justatip", Misc.getRoundedValueFloat(actualnumber/damage.getBaseDamage()));
                                //Global.getCombatEngine().addFloatingText(ship.getLocation(), String.valueOf(damage.getModifier().getModifiedValue()), 15f, Color.WHITE, ship, 1f, 0.5f);*/
                        }
                        return null;
		}
	}
        
    @Override
    public String getDescriptionParam(int index, HullSize hullSize, ShipAPI ship) {
        return null;
    }
    
    @Override
    public boolean shouldAddDescriptionToTooltip(HullSize hullSize, ShipAPI ship, boolean isForModSpec) {
	return false;
    }
}