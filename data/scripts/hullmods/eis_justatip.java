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
    private static String sText = Global.getSettings().getString("eis_ironshell", "eis_justatip_stext");
    private static String Text2 = Global.getSettings().getString("eis_ironshell", "eis_justatip_text2");
    private static String Text3 = Global.getSettings().getString("eis_ironshell", "eis_justatip_text3");
    private static String Text7 = Global.getSettings().getString("eis_ironshell", "eis_justatip_text7");
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
                ship.addListener(new eis_justtip(ship, 50f, 150f));
            } else {
		ship.addListener(new eis_justtip(ship, 100f, 100f));
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
                Gula.addPara(Text4, 0f, Misc.getHighlightColor(), Misc.getRoundedValue(halfdamagenumber/2));
                Gula.addPara(Text6, Misc.getNegativeHighlightColor(), 0f);
                Gula.addPara(Text5, 0f, Misc.getGrayColor(), Misc.getHighlightColor(), Misc.getRoundedValue(halfdamagenumber), Misc.getRoundedValue(sdamagenumber));
                tooltip.addImageWithText(PAD);
            } else if (ship.getHullSpec().isBuiltInMod("eis_justatip") || ship.getVariant().getSMods().contains("eis_justatip")) {
                Gula.addPara(Text0, 0f, Misc.getPositiveHighlightColor(), Misc.getRoundedValue(halfdamagenumber));
                Gula.addPara(Text, 0f, Misc.getPositiveHighlightColor(), Misc.getRoundedValue(sdamagenumber));
                Gula.addPara(Text2, 0f, Misc.getNegativeHighlightColor(), "-"+Misc.getRoundedValue(halfdamagenumber)+"%");
                Gula.addPara(Text3, 0f, Misc.getNegativeHighlightColor(), "-"+Misc.getRoundedValue(halfdamagenumber)+"%");
                Gula.addPara(Text7, 0f, Misc.getNegativeHighlightColor(), "-"+Misc.getRoundedValue(halfdamagenumber)+"%");
                Gula.addPara(Text4, 0f, Misc.getHighlightColor(), Misc.getRoundedValue(halfdamagenumber/2));
                Gula.addPara(Text6, Misc.getNegativeHighlightColor(), 0f);
                Gula.addPara(Text5, 0f, Misc.getPositiveHighlightColor(), Misc.getHighlightColor(), Misc.getRoundedValue(halfdamagenumber), Misc.getRoundedValue(sdamagenumber));
                tooltip.addImageWithText(PAD);
                tooltip.addSectionHeading("", Alignment.MID, 3f);
                Map<String, Boolean> weaponlist = new HashMap<>(); //The hullmods has to be cleared everytime ig.
                for (WeaponAPI weapon : ship.getAllWeapons()) {
                    if (weapon.getType() == WeaponType.MISSILE && !weaponlist.containsKey(weapon.getDisplayName()) && weapon.getDamage().getBaseDamage() >= damagenumber) {
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
                        }
                        if (isokaytoadd) {tooltip.addPara("• %s: %s "+each, 0f, new Color[]{Misc.getMissileMountColor(),Misc.getPositiveHighlightColor()}, weapon.getDisplayName(),"+"+Misc.getRoundedValue(water));}
                        }
                    }
            } else if (!isForModSpec) {
                Gula.addPara(Text0, 0f, Misc.getHighlightColor(), Misc.getRoundedValue(damagenumber));
                Gula.addPara(Text, 0f, Misc.getPositiveHighlightColor(), Misc.getRoundedValue(damagenumber));
                Gula.addPara(Text2, 0f, Misc.getNegativeHighlightColor(), "-"+Misc.getRoundedValue(halfdamagenumber)+"%");
                Gula.addPara(Text3, 0f, Misc.getNegativeHighlightColor(), "-"+Misc.getRoundedValue(halfdamagenumber)+"%");
                Gula.addPara(Text7, 0f, Misc.getNegativeHighlightColor(), "-"+Misc.getRoundedValue(halfdamagenumber)+"%");
                Gula.addPara(Text4, 0f, Misc.getHighlightColor(), Misc.getRoundedValue(halfdamagenumber/2));
                Gula.addPara(Text6, Misc.getNegativeHighlightColor(), 0f);
                Gula.addPara(Text5, 0f, Misc.getGrayColor(), Misc.getHighlightColor(), Misc.getRoundedValue(halfdamagenumber), Misc.getRoundedValue(sdamagenumber));
                tooltip.addImageWithText(PAD);
                tooltip.addSectionHeading("", Alignment.MID, 3f);
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
                            }
                            if (isokaytoadd) {tooltip.addPara("• %s: %s "+each, 0f, new Color[]{Misc.getMissileMountColor(),Misc.getPositiveHighlightColor()}, weapon.getDisplayName(),"+"+Misc.getRoundedValue(water));}
                        }
                    }
                }
            }
                   
        }
        
	public static class eis_justtip implements DamageDealtModifier {
		protected ShipAPI ship;
                protected float actualnumberref;
                protected float damagebonus;
		public eis_justtip(ShipAPI ship, float actualnumberref, float damagebonus) {
			this.ship = ship;
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
                                //Global.getCombatEngine().addFloatingText(ship.getLocation(), String.valueOf(damage.getModifier().getModifiedValue()), 15f, Color.WHITE, ship, 1f, 0.5f);
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







