package data.scripts.hullmods;

import com.fs.starfarer.api.Global;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.MissileAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.ShipHullSpecAPI.ShipTypeHints;
import com.fs.starfarer.api.combat.ShipwideAIFlags.AIFlags;
import com.fs.starfarer.api.impl.campaign.ids.HullMods;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.api.util.Misc;
import data.scripts.util.MagicIncompatibleHullmods;
import data.scripts.util.MagicUI;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.combat.CombatUtils;

public class eis_damperhull extends BaseHullMod {
    private static Map duration = new HashMap();
    private static Map armor = new HashMap();
    private static Set<String> BLOCKED_HULLMODS = new HashSet();
    private static Color color = new Color(255, 135, 240, 200);
    private static Color colorunder = new Color(255, 135, 240, 150);
    static {
        duration.put(HullSize.FIGHTER, 1f);
        duration.put(HullSize.FRIGATE, 2f);
        duration.put(HullSize.DESTROYER, 2f);
        duration.put(HullSize.CRUISER, 2.5f);
        duration.put(HullSize.CAPITAL_SHIP, 3f);
        armor.put(HullSize.FIGHTER, 0f);
        armor.put(HullSize.FRIGATE, 60f);
        armor.put(HullSize.DESTROYER, 120f);
        armor.put(HullSize.CRUISER, 160f);
        armor.put(HullSize.CAPITAL_SHIP, 200f);
        BLOCKED_HULLMODS.add(HullMods.HEAVYARMOR);
    }
    private final float lonelyaside = 50f;
    private final float smodcooldown = 15f;
    private final float cooldown = 20f;
    private final float baste = -25f;
    private static final String DATA_KEY = "eis_damperhull_data";
    private static boolean vanagloriaSetting = Global.getSettings().getBoolean("VanagloriaNoMagicUI");
    private static String vanagloriaIcon = "graphics/icons/hullsys/damper_field.png";
    private static String vanagloriaTitle = Global.getSettings().getString("eis_ironshell", "eis_vanagloriaTitle");
    private static String vanagloriaText1 = Global.getSettings().getString("eis_ironshell", "eis_vanagloriaText1");
    private static String vanagloriaText12 = Global.getSettings().getString("eis_ironshell", "eis_vanagloriaText12");
    private static String vanagloriaText1b = Global.getSettings().getString("eis_ironshell", "eis_vanagloriaText1b");
    private static String vanagloriaText1c = Global.getSettings().getString("eis_ironshell", "eis_vanagloriaText1c");
    private static String damperText2 = Global.getSettings().getString("eis_ironshell", "eis_vanagloriaDamperText2");
    private static String damperText = Global.getSettings().getString("eis_ironshell", "eis_vanagloriaDamperText1");
    private static String damperText3 = Global.getSettings().getString("eis_ironshell", "eis_vanagloriaDamperText3");
    private static String damperText4 = Global.getSettings().getString("eis_ironshell", "eis_vanagloriaDamperText4");
    private static String damperText3b = Global.getSettings().getString("eis_ironshell", "eis_vanagloriaDamperText3b");
    private static String vanagloriaText6 = Global.getSettings().getString("eis_ironshell", "eis_vanagloriaText6");
    private static String vanagloriaText7 = Global.getSettings().getString("eis_ironshell", "eis_vanagloriaText7");
    private static String vanagloriaStatusTexta = Global.getSettings().getString("eis_ironshell", "eis_vanagloriaStatusTexta");
    private static String vanagloriaStatusTextb = Global.getSettings().getString("eis_ironshell", "eis_vanagloriaStatusTextb");
    private static String vanagloriaStatusTextUI1 = Global.getSettings().getString("eis_ironshell", "eis_vanagloriaStatusTextUI1");
    private static String vanagloriaStatusTextUI2 = Global.getSettings().getString("eis_ironshell", "eis_vanagloriaStatusTextUI2");
    private static String vanagloriaStatusTextUI3 = Global.getSettings().getString("eis_ironshell", "eis_vanagloriaStatusTextUI3");
    private static String vanagloriaStatusTextUI4 = Global.getSettings().getString("eis_ironshell", "eis_vanagloriaStatusTextUI4");
    
    private static String holdfirekey = Global.getSettings().getControlStringForEnumName("SHIP_HOLD_FIRE") != null ? Global.getSettings().getControlStringForEnumName("SHIP_HOLD_FIRE") : "Hold Fire";

    @Override
    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
        for (String tmp : BLOCKED_HULLMODS) {
            if (ship.getVariant().getHullMods().contains(tmp)) {
                MagicIncompatibleHullmods.removeHullmodWithWarning(ship.getVariant(), tmp, "eis_damperhull");
            }
        }
    }

    @Override
    public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
            stats.getEffectiveArmorBonus().modifyFlat(id, (float)armor.get(hullSize));
    }

    @Override
    public String getDescriptionParam(int index, HullSize hullSize) {
        return null;
    }

    @Override
    public boolean isApplicableToShip(ShipAPI ship) {
        return !((ship.getVariant().getHullSize() == HullSize.FRIGATE) || (ship.getVariant().hasHullMod(HullMods.CIVGRADE) && !ship.getVariant().hasHullMod(HullMods.MILITARIZED_SUBSYSTEMS)) || ship.getVariant().hasHullMod(HullMods.HEAVYARMOR));
    }

    @Override
    public void advanceInCombat(ShipAPI ship, float amount) {
        CombatEngineAPI engine = Global.getCombatEngine();
        if (engine == null || /*engine.isPaused() ||*/!engine.isEntityInPlay(ship) || !ship.isAlive() || ship.getParentStation() != null) {return;}
        String key = DATA_KEY + "_" + ship.getId();
        eis_damperhull2 data = (eis_damperhull2) engine.getCustomData().get(key);
        
        if (data == null) {
            data = new eis_damperhull2();
            engine.getCustomData().put(key, data);
        }
        if (!data.runOnce) {
            data.runOnce = true;
            data.buffId = this.getClass().getName() + "_" + ship.getId();
            data.maxcooldown = eis_damperhull3(ship);
            data.maxActiveTime = (float) duration.get(ship.getHullSize());
        }
        if (data.cooldown<data.maxcooldown && data.activeTime <= 0f && ship.getCurrentCR() > 0f && !ship.getFluxTracker().isOverloadedOrVenting()) {data.cooldown += amount;}
        if (data.activeTime > 0f) {
            data.activeTime -= amount;
            if (ship.getFluxTracker().isOverloadedOrVenting()) {data.activeTime = 0;}
            ship.getMutableStats().getHullDamageTakenMult().modifyMult(data.buffId, lonelyaside/100f);
            ship.getMutableStats().getArmorDamageTakenMult().modifyMult(data.buffId, lonelyaside/100f);
            ship.getMutableStats().getEmpDamageTakenMult().modifyMult(data.buffId, lonelyaside/100f);
            ship.getMutableStats().getVentRateMult().modifyMult(data.buffId, 0f);
            ship.setJitterUnder(ship.getId(), color, 1f, 15, 0f, 6f);
            ship.setJitter(ship.getId(), colorunder, 1f, 1, 0f, 4f);
            if (data.activeTime > 0f) {Global.getSoundPlayer().playLoop("system_damper_loop", ship, 1.1f, 0.3f, ship.getLocation(), ship.getVelocity());}
            //if (data.activeTime > 2f) {Global.getSoundPlayer().playSound("system_damper_loop", 1.1f, 0.3f, ship.getLocation(), ship.getVelocity());}
            //if (ship.isDefenseDisabled()) {data.DefenseAlreadyDisabled = true;}
            ship.setDefenseDisabled(true);
            //if (AIUtils.canUseSystemThisFrame(ship)) to-do later... I guess
            ship.setShipSystemDisabled(true);
            if (ship.getParentStation() == null && ship.getChildModulesCopy() != null && !ship.getChildModulesCopy().isEmpty()) {
                for (ShipAPI childModulesCopy : ship.getChildModulesCopy()) {
                    childModulesCopy.setJitterShields(false);
                    childModulesCopy.setDefenseDisabled(true);
                    childModulesCopy.setShipSystemDisabled(true);
                    childModulesCopy.getMutableStats().getHullDamageTakenMult().modifyMult(data.buffId, lonelyaside/100f);
                    childModulesCopy.getMutableStats().getArmorDamageTakenMult().modifyMult(data.buffId, lonelyaside/100f);
                    childModulesCopy.getMutableStats().getEmpDamageTakenMult().modifyMult(data.buffId, lonelyaside/100f);
                    childModulesCopy.getMutableStats().getVentRateMult().modifyMult(data.buffId, 0f);
                    childModulesCopy.setJitterUnder(childModulesCopy.getId(), color, 1f, 5, 0f, 7f);
                    childModulesCopy.setJitter(childModulesCopy.getId(), colorunder, 1f, 1, 0f, 5f);
                }
            }
        } else {
            ship.getMutableStats().getHullDamageTakenMult().unmodifyMult(data.buffId);
            ship.getMutableStats().getArmorDamageTakenMult().unmodifyMult(data.buffId);
            ship.getMutableStats().getEmpDamageTakenMult().unmodifyMult(data.buffId);
            ship.getMutableStats().getVentRateMult().unmodifyMult(data.buffId);
            //if (ship.isDefenseDisabled()) {data.DefenseAlreadyDisabled = true;}
            ship.setDefenseDisabled(false);
            //if (AIUtils.canUseSystemThisFrame(ship)) to-do later... I guess
            ship.setShipSystemDisabled(false);
            if (ship.getParentStation() == null && ship.getChildModulesCopy() != null && !ship.getChildModulesCopy().isEmpty()) {
                for (ShipAPI childModulesCopy : ship.getChildModulesCopy()) {
                    childModulesCopy.setJitterShields(true);
                    childModulesCopy.setDefenseDisabled(false);
                    childModulesCopy.setShipSystemDisabled(false);
                    childModulesCopy.getMutableStats().getHullDamageTakenMult().unmodifyMult(data.buffId);
                    childModulesCopy.getMutableStats().getArmorDamageTakenMult().unmodifyMult(data.buffId);
                    childModulesCopy.getMutableStats().getEmpDamageTakenMult().unmodifyMult(data.buffId);
                    childModulesCopy.getMutableStats().getVentRateMult().unmodifyMult(data.buffId);
                }
            }
        }
        if (engine.getPlayerShip() == ship) {
            /*if (data.activeTime <= 0f) {
                engine.maintainStatusForPlayerShip(data.buffId, "graphics/icons/hullsys/damper_field.png",
                            "Vanagloria", data.cooldown<data.maxcooldown ? Misc.getRoundedValueMaxOneAfterDecimal((data.cooldown/data.maxcooldown)*100f)+"% - Charging" : Misc.getRoundedValueMaxOneAfterDecimal((data.cooldown/data.maxcooldown)*100f)+"% - Hold Fire to Use", data.cooldown<data.maxcooldown);
            }*/
            
            if (data.activeTime > 0f) {
                
                if (vanagloriaSetting || (ship.getPhaseCloak() != null && ship.getPhaseCloak().getId().equals("damper"))) {engine.maintainStatusForPlayerShip(data.buffId+"2", "graphics/icons/hullsys/damper_field.png",
                            vanagloriaStatusTextUI1, (int) lonelyaside + vanagloriaStatusTextUI2, false);engine.maintainStatusForPlayerShip(data.buffId+"2", "graphics/icons/hullsys/damper_field.png",
                            vanagloriaStatusTextUI1, vanagloriaStatusTextUI3 + Math.round((data.activeTime/data.maxActiveTime)*100) + "%", false);} 
                else {MagicUI.drawInterfaceStatusBar(ship, data.activeTime/data.maxActiveTime,Misc.getNegativeHighlightColor(),Misc.getNegativeHighlightColor(),data.activeTime/data.maxActiveTime,vanagloriaStatusTextUI4,Math.round((data.activeTime/data.maxActiveTime)*100));engine.maintainStatusForPlayerShip(data.buffId+"2", "graphics/icons/hullsys/damper_field.png",
                            vanagloriaStatusTextUI1, (int) lonelyaside + vanagloriaStatusTextUI2, false);}
            } else {
                if (data.cooldown >= data.maxcooldown) {engine.maintainStatusForPlayerShip(data.buffId, "graphics/icons/hullsys/damper_field.png",
                            vanagloriaStatusTextUI1, vanagloriaStatusTexta+holdfirekey+vanagloriaStatusTextb, false);}
                if (vanagloriaSetting || ship.getPhaseCloak() != null && ship.getPhaseCloak().getId().equals("damper")) {engine.maintainStatusForPlayerShip(data.buffId+"2", "graphics/icons/hullsys/damper_field.png",
                            vanagloriaStatusTextUI1, vanagloriaStatusTextUI3 + Math.round((data.cooldown/data.maxcooldown)*100) + "%", true);} else {MagicUI.drawInterfaceStatusBar(ship, data.cooldown/data.maxcooldown,null,null,data.cooldown/data.maxcooldown,vanagloriaStatusTextUI4,Math.round((data.cooldown/data.maxcooldown)*100));
                }
            }
            
            if ((ship.isHoldFire() && !data.holdFireBefore) && data.cooldown>=data.maxcooldown && ship.getCurrentCR() > 0f && !ship.getFluxTracker().isOverloadedOrVenting()) {
                data.activeTime = data.maxActiveTime;
                data.cooldown = 0f;
                Global.getSoundPlayer().playSound("system_damper", 1.1f, 0.3f, ship.getLocation(), ship.getVelocity());
                //Global.getSoundPlayer().playSound("system_damper_loop", 1.1f, 0.3f, ship.getLocation(), ship.getVelocity());
                ship.setHoldFire(false);
            }
            data.holdFireBefore = (ship.isHoldFire());
        } 
        if (ship.getAI() != null) {
            data.tracker.advance(amount);
            if (data.tracker.intervalElapsed()) {
                if (data.cooldown>=data.maxcooldown && !ship.getSystem().isActive() && !(ship.getPhaseCloak() != null && ship.getPhaseCloak().isActive())) {
                    data.missiledetected = 0f;
                    List<MissileAPI> allMissiles = CombatUtils.getMissilesWithinRange(ship.getLocation(), ship.getCollisionRadius()+MathUtils.getRandomNumberInRange(50f,100f));
                    for (MissileAPI missile : allMissiles) {   
                        if (missile.getOwner() != ship.getOwner() && !missile.isMine()) {
                        float scale = 1f;
                        switch (missile.getDamageType()) {
                            case FRAGMENTATION:
                                scale = 0.3f;
                                break;
                            case KINETIC:
                                scale = 0.85f;
                                break;
                            case HIGH_EXPLOSIVE:
                                scale = 1.7f;
                                break;
                            default:
                            case ENERGY:
                                break;
                            }
                        data.missiledetected += missile.getDamageAmount() * scale;
                        }
                    }
                    if (data.missiledetected >= 1000f || (ship.getHullLevel() <= 0.5f && ship.getAIFlags().hasFlag(AIFlags.NEEDS_HELP)) || ((MathUtils.getRandomNumberInRange(ship.getFluxLevel(), 1f) >= 0.5f || MathUtils.getRandomNumberInRange(ship.getFluxLevel(), 1.3f) >= ship.getHullLevel()) && ship.getAIFlags().hasFlag(AIFlags.HAS_INCOMING_DAMAGE)) || ((ship.getFluxLevel() >= 0.6f || ship.getHullLevel() <= 0.4f && !ship.isPhased()) && ship.getAIFlags().hasFlag(AIFlags.IN_CRITICAL_DPS_DANGER))) {
                        data.activeTime = data.maxActiveTime;
                        data.cooldown = 0f;
                        Global.getSoundPlayer().playSound("system_damper", 1.1f, 0.3f, ship.getLocation(), ship.getVelocity());
                        //Global.getSoundPlayer().playSound("system_damper_loop", 1.1f, 0.3f, ship.getLocation(), ship.getVelocity());
                    }
                }
            }
        }
    }

    @Override
    public void addPostDescriptionSection(TooltipMakerAPI tooltip, ShipAPI.HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
        float HEIGHT = 50f;
        float PAD = 10f;
        Color YELLOW = new Color(241, 199, 0);

        TooltipMakerAPI vanagloria = tooltip.beginImageWithText(vanagloriaIcon, HEIGHT);
        vanagloria.addPara(vanagloriaTitle, 0f, YELLOW, vanagloriaTitle);
        
        if (ship != null) {
                vanagloria.addPara(vanagloriaText1, 0f, Misc.getPositiveHighlightColor(),
                    "+" + Math.round((Float) armor.get(hullSize)));
        } else {
            vanagloria.addPara(vanagloriaText12, 0f, Misc.getPositiveHighlightColor(),
                    "+" + Math.round((Float) armor.get(HullSize.DESTROYER)),
                    "" + Math.round((Float) armor.get(HullSize.CRUISER)),
                    "" + Math.round((Float) armor.get(HullSize.CAPITAL_SHIP)));
        }
        if (ship != null && ship.isShipWithModules()) {vanagloria.addPara(vanagloriaText1b, 0f);}
        vanagloria.addPara(vanagloriaText1c, 0f, YELLOW, holdfirekey);
        if (ship != null) {
             vanagloria.addPara(damperText, 0f, Misc.getPositiveHighlightColor(), Misc.getRoundedValue(lonelyaside)+"%", Misc.getRoundedValueMaxOneAfterDecimal((float) duration.get(hullSize)));
        } else {
             vanagloria.addPara(damperText, 0f, Misc.getPositiveHighlightColor(), Misc.getRoundedValue(lonelyaside)+"%", "2");
        }
        vanagloria.addPara(damperText3, 0f, YELLOW);
        if (ship != null) {
            if (ship.getShield() != null) {
            /*    vanagloria.addPara(damperText3b, 0f, Misc.getPositiveHighlightColor(),ship.getHullSpec().getHullNameWithDashClass(),"" + Misc.getRoundedValueMaxOneAfterDecimal(cooldown/(((ship.getShield().getFluxPerPointOfDamage()*ship.getMutableStats().getShieldDamageTakenMult().getModifiedValue()) > 1.33f) ? 1.33f : (ship.getShield().getFluxPerPointOfDamage()*ship.getMutableStats().getShieldDamageTakenMult().getModifiedValue()))));
            } else if (ship.getShield() == null && !ship.getHullSpec().isPhase() && (ship.getPhaseCloak() == null || !ship.getHullSpec().getHints().contains(ShipTypeHints.PHASE))) {
                vanagloria.addPara(damperText3b, 0f, Misc.getPositiveHighlightColor(),ship.getHullSpec().getHullNameWithDashClass(),"" + Misc.getRoundedValueMaxOneAfterDecimal(cooldown/1.33f));
            } else if (ship.getHullSpec().isPhase() && (ship.getPhaseCloak() != null || ship.getHullSpec().getHints().contains(ShipTypeHints.PHASE))){
                vanagloria.addPara(damperText3b, 0f, Misc.getPositiveHighlightColor(),ship.getHullSpec().getHullNameWithDashClass(),"" + Misc.getRoundedValueMaxOneAfterDecimal(cooldown/((ship.getPhaseCloak().getFluxPerSecond()+ship.getPhaseCloak().getFluxPerUse())*6f/ship.getHullSpec().getFluxCapacity())));
            } else {
                vanagloria.addPara(damperText3b, 0f, Misc.getPositiveHighlightColor(),ship.getHullSpec().getHullNameWithDashClass(),"" + Misc.getRoundedValueMaxOneAfterDecimal(cooldown/0.3f));
            */
            vanagloria.addPara(damperText3b, 0f, Misc.getPositiveHighlightColor(),ship.getHullSpec().getHullNameWithDashClass(),Misc.getRoundedValueMaxOneAfterDecimal(eis_damperhull3(ship)));
            }
        }
        if (isForModSpec) {
            vanagloria.addPara(damperText4, 0f, Misc.getGrayColor(), Misc.getHighlightColor(), Misc.getRoundedValue(baste)+"%");
	} else if (ship.getHullSpec().isBuiltInMod("eis_damperhull") || ship.getVariant().getSMods().contains("eis_damperhull")) {
            vanagloria.addPara(damperText4, 0f, Misc.getPositiveHighlightColor(), Misc.getHighlightColor(), Misc.getRoundedValue(baste)+"%");
        } else if (!isForModSpec) {
            vanagloria.addPara(damperText4, 0f, Misc.getGrayColor(), Misc.getHighlightColor(), Misc.getRoundedValue(baste)+"%");
	}
        vanagloria.addPara(damperText2, Misc.getNegativeHighlightColor(), 0f);
        tooltip.addImageWithText(PAD);
        tooltip.addPara(vanagloriaText7, 5f, Misc.getNegativeHighlightColor(), vanagloriaText7);
        tooltip.addPara(vanagloriaText6, 5f, Misc.getNegativeHighlightColor(), vanagloriaText6);
    }

    private float eis_damperhull3(ShipAPI ship) {
        float localcooldown = cooldown;
        if (ship.getVariant().getHullSpec().isBuiltInMod("eis_damperhull") || ship.getVariant().getSMods().contains("eis_damperhull")) {
            localcooldown = smodcooldown;
        }
        if (ship.getShield() != null) {
            return (localcooldown/(((ship.getShield().getFluxPerPointOfDamage()*ship.getMutableStats().getShieldDamageTakenMult().getModifiedValue()) > 1.33f) ? 1.33f : (ship.getShield().getFluxPerPointOfDamage()*ship.getMutableStats().getShieldDamageTakenMult().getModifiedValue())));
        } else if (ship.getShield() == null && !ship.getHullSpec().isPhase() && (ship.getPhaseCloak() == null || !ship.getHullSpec().getHints().contains(ShipTypeHints.PHASE))) {
            return localcooldown/1.33f;
        } else if (ship.getHullSpec().isPhase() && (ship.getPhaseCloak() != null || ship.getHullSpec().getHints().contains(ShipTypeHints.PHASE))){
            return localcooldown/((ship.getPhaseCloak().getFluxPerSecond()+ship.getPhaseCloak().getFluxPerUse())*6f/ship.getHullSpec().getFluxCapacity()); //ship.getMaxFlux(), this gets total things!
        } else {
            return localcooldown/0.3f;
        }
    }
    
    private static class eis_damperhull2 {
        String buffId = "";
        //boolean DefenseAlreadyDisabled = false;
        //boolean ShipSystemAlreadyDisabled = false;
        boolean runOnce = false;
        boolean holdFireBefore = false;
        float activeTime = 0f;
        float maxActiveTime = 0f;
        float cooldown = 0f;
        float maxcooldown = 0f;
        IntervalUtil tracker = new IntervalUtil(1f, 1f);
        float missiledetected = 0;
    }
    
}
