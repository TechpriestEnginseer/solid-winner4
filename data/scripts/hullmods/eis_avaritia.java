package data.scripts.hullmods;

import com.fs.starfarer.api.GameState;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.characters.PersonalityAPI;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.FluxTrackerAPI;
import com.fs.starfarer.api.combat.MissileAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.ShipCommand;
import com.fs.starfarer.api.combat.ShipwideAIFlags;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.combat.WeaponAPI.WeaponType;
import com.fs.starfarer.api.impl.campaign.ids.HullMods;
import com.fs.starfarer.api.mission.FleetSide;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.api.util.Misc;
import data.scripts.util.MagicIncompatibleHullmods;
import data.scripts.util.MagicUI;
import java.awt.Color;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.combat.AIUtils;

//rubi totally did not write this code simply because I did not want to do anything with this.

public class eis_avaritia extends BaseHullMod {
    private static Set<String> BLOCKED_HULLMODS = new HashSet();
    
    private static Map speed = new HashMap();
    private static Map DAMAGE_BUFF_DURATION = new HashMap();
    static {
        speed.put(HullSize.FIGHTER, 20f);
	speed.put(HullSize.FRIGATE, 50f); //50f 65f
	speed.put(HullSize.DESTROYER, 35f); //30f 55f
	speed.put(HullSize.CRUISER, 25f); //20f 50f
	speed.put(HullSize.CAPITAL_SHIP, 20f); //10f 40f
        speed.put(HullSize.DEFAULT, 20f);
        DAMAGE_BUFF_DURATION.put(HullSize.FIGHTER, 2f);
	DAMAGE_BUFF_DURATION.put(HullSize.FRIGATE, 2f); //50f
	DAMAGE_BUFF_DURATION.put(HullSize.DESTROYER, 2.25f); //30f
	DAMAGE_BUFF_DURATION.put(HullSize.CRUISER, 2.5f); //20f
	DAMAGE_BUFF_DURATION.put(HullSize.CAPITAL_SHIP, 3f); //10f
        DAMAGE_BUFF_DURATION.put(HullSize.DEFAULT, 2f);
        BLOCKED_HULLMODS.add(HullMods.SAFETYOVERRIDES);
    }
    private static final float PERCENT_PER_VENT = 50f;
    private static final float MIN_VENT_PERCENT = 50f;
    //public static final float VENT_RATE_BONUS = 15f;
    //public static final float CAPACITOR_MULT = 2f;
    private static final float DAMAGE_SBUFF_PERCENT = 50f;
    private static final float ROF_SBUFF_MULT = 1.5f;
    private static final float DAMAGE_BUFF_PERCENT = 30f;

    private static final String DATA_KEY = "eis_avaritia_data_key";

    private static String Icon = Global.getSettings().getSpriteName("ui", "icon_tactical_venting");
    private static String Title = Global.getSettings().getString("eis_ironshell", "eis_avaritiaTitle");
    private static String Text1 = Global.getSettings().getString("eis_ironshell", "eis_avaritiaText1");
    private static String Text4 = Global.getSettings().getString("eis_ironshell", "eis_avaritiaText4");
    private static String Text2 = Global.getSettings().getString("eis_ironshell", "eis_avaritiaText2");
    //public static String Text3 = "• Increases flux dissipation rate while venting by %s";
    //public static String Text4 = "• Effectiveness of additional flux capacitors is %s";
    private static String Text5 = Global.getSettings().getString("eis_ironshell", "eis_avaritiaText5");
    private static String Text5b = Global.getSettings().getString("eis_ironshell", "eis_avaritiaText5b");
    private static String Text6 = Global.getSettings().getString("eis_ironshell", "eis_avaritiaText6");
    private static String Text7 = Global.getSettings().getString("eis_ironshell", "eis_avaritiaText7");
    private static String Text8 = Global.getSettings().getString("eis_ironshell", "eis_avaritiaText8");
    private static String ApplicableText = Global.getSettings().getString("eis_ironshell", "eis_avaritiaApplicableText");
    private static String StatusTitle = Global.getSettings().getString("eis_ironshell", "eis_avaritiaStatusTitle");
    private static String StatusText = Global.getSettings().getString("eis_ironshell", "eis_avaritiaStatusText");
    private static String StatusText2 = Global.getSettings().getString("eis_ironshell", "eis_avaritiaStatusText2");
    private static String StatusTitle2 = Global.getSettings().getString("eis_ironshell", "eis_avaritiaStatusTitle2");
    private static String StatusText2a = Global.getSettings().getString("eis_ironshell", "eis_avaritiaStatusText2a");
    private static String StatusText2b = Global.getSettings().getString("eis_ironshell", "eis_avaritiaStatusText2b");
    private static Color Amongus = Global.getSettings().getColor("textFriendColor");
    private static Color SUS = new Color(255, 0, 191);
    private static final float MAX_GLOW_PERCENT = 0.8f;
    private static final float FADE_IN_OUT_TIME = 0.2f;

    /*
     @Override
     public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
     stats.getVentRateMult().modifyPercent(id, VENT_RATE_BONUS);
     stats.getFluxCapacity().modifyFlat(id, (CAPACITOR_MULT - 1f) * Global.getSettings().getFloat(FLUX_PER_CAP_STRING) * stats.getVariant().getNumFluxCapacitors());
     }
     */
    
    @Override
    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
        for (String tmp : BLOCKED_HULLMODS) {
            if (ship.getVariant().getHullMods().contains(tmp)) {
                MagicIncompatibleHullmods.removeHullmodWithWarning(ship.getVariant(), tmp, "eis_avaritia");
            }
        }
    }
    
    @Override
    public void advanceInCombat(ShipAPI ship, float amount) {
        CombatEngineAPI engine = Global.getCombatEngine();
        // stop and delete keys if out of combat
        if (Global.getCurrentState() != GameState.COMBAT || !engine.isEntityInPlay(ship) || !ship.isAlive()) {
            return;
        }

        // initialize data
        FluxTrackerAPI fluxTracker = ship.getFluxTracker();
        String key = DATA_KEY + "_" + ship.getId();
        AvaritiaData data = (AvaritiaData) engine.getCustomData().get(key);
        if (data == null) {
            data = new AvaritiaData();
            engine.getCustomData().put(key, data);
        }

        if (!data.runOnce) {
            data.runOnce = true;
            List<WeaponAPI> loadout = ship.getAllWeapons();
            if (loadout != null) {
                for (WeaponAPI w : loadout) {
                    if (w.getType() != WeaponAPI.WeaponType.MISSILE) {
                        if (w.getRange() > data.maxRange) {
                            data.maxRange = w.getRange()+100f;
                        }
                    }
                }
            }
            data.timer.randomize();
            data.buffId = this.getClass().getName() + "_" + ship.getId();
        }

        if (fluxTracker.isVenting() && !data.startedVenting) {
            data.startedVenting = true;
            
            data.fluxLevelWhenStartedVenting = fluxTracker.getFluxLevel();
            data.ventDuration = (ship.getMaxFlux()*0.5f/(2*ship.getMutableStats().getFluxDissipation().getModifiedValue() * ship.getMutableStats().getVentRateMult().getModifiedValue()));
            if (data.fluxLevelWhenStartedVenting <= MIN_VENT_PERCENT * 0.01f) {
                //note: increaseFlux() does not work when venting
                //fluxTracker.increaseFlux((MIN_VENT_PERCE((MIN_VENT_PERCENT * 0.01f) NT * 0.01f - fluxLevelWhenStartedVenting) * fluxTracker.getMaxFlux(), false);
                if (ship.getShipAI() == null) {fluxTracker.setCurrFlux((MIN_VENT_PERCENT * 0.01f) * fluxTracker.getMaxFlux());} //AI cheat because it always vents at 10% hard flux DESPITE FLAGGED NOT TO BUT YOU KNOW.
                data.fluxLevelToVentTo = 0f;
            } else {
                data.fluxLevelToVentTo = data.fluxLevelWhenStartedVenting - 0.5f;
            }
            
            //ship.getFluxTracker().setHardFlux(ship.getFluxTracker().getHardFlux()-(ship.getFluxTracker().getHardFlux() > ship.getMaxFlux()*0.5f ? ship.getMaxFlux()*0.5f : ship.getFluxTracker().getHardFlux()));
            
            //if (ship.getHullSpec().isBuiltInMod("eis_avaritia") || ship.getVariant().getSMods().contains("eis_avaritia")) {
                MutableShipStatsAPI stats = ship.getMutableStats();
                HullSize hullSize = ship.getHullSize();
                stats.getMaxSpeed().modifyFlat("eis_avaritia", (Float) speed.get(hullSize));
                stats.getAcceleration().modifyFlat("eis_avaritia", (Float) speed.get(hullSize) * 2f);
                stats.getDeceleration().modifyFlat("eis_avaritia", (Float) speed.get(hullSize) * 2f);
                stats.getTurnAcceleration().modifyFlat("eis_avaritia", (Float) speed.get(hullSize) * 0.45f);
                stats.getTurnAcceleration().modifyPercent("eis_avaritia", (Float) speed.get(hullSize) * 3f);
                stats.getMaxTurnRate().modifyFlat("eis_avaritia", (Float) speed.get(hullSize) * 0.225f);
                stats.getMaxTurnRate().modifyPercent("eis_avaritia", (Float) speed.get(hullSize) * 1.5f);
            //}
        }

        if (engine.getPlayerShip() == ship && fluxTracker.getFluxLevel() > 0.5f) {
            engine.maintainStatusForPlayerShip(data.buffId + " 2", Icon, StatusTitle2,
                   StatusText2a, false);
        }
        
        if (data.startedVenting && engine.getPlayerShip() == ship) {
            //engine.maintainStatusForPlayerShip(data.buffId, Icon, "Avaritia Overhaul",
            //        "Venting to " + Misc.getRoundedValueMaxOneAfterDecimal(100f * data.fluxLevelToVentTo) + "% flux", true);
            //engine.maintainStatusForPlayerShip(data.buffId + " 2", Icon, "Avaritia Overhaul",
            //        "Removing " + Math.round(ship.getMaxFlux() * (fluxTracker.getFluxLevel() - data.fluxLevelToVentTo)) + " flux until cancelled", true);
            //engine.maintainStatusForPlayerShip(data.buffId + " 2", Icon, "Avaritia Overhaul",
                   //"Removing " + Math.round(ship.getMaxFlux() * (fluxTracker.getFluxLevel() - data.fluxLevelToVentTo)) + " flux until cancelled", true);
            if (ship.getHullSpec().isBuiltInMod("eis_avaritia") || ship.getVariant().getSMods().contains("eis_avaritia")) {
                engine.maintainStatusForPlayerShip("eis_avaritia", Icon, StatusTitle, StatusText, false);
            }
            MagicUI.drawHUDStatusBar(ship, fluxTracker.getFluxLevel(), Amongus, Amongus, data.fluxLevelToVentTo, Misc.getRoundedValueMaxOneAfterDecimal(ship.getMaxFlux() * (fluxTracker.getFluxLevel() - data.fluxLevelToVentTo)/(2*ship.getMutableStats().getFluxDissipation().getModifiedValue() * ship.getMutableStats().getVentRateMult().getModifiedValue())) + StatusText2b, ".", true);
        }

        if (data.startedVenting && (fluxTracker.getFluxLevel() <= Math.max(0.01f,data.fluxLevelToVentTo))) {
            fluxTracker.stopVenting();
            data.startedVenting = false;
            data.fluxLevelToVentTo = 0f;
            data.buffDurationRemaining = data.ventDuration > (Float) DAMAGE_BUFF_DURATION.get(ship.getHullSize()) ? (Float) DAMAGE_BUFF_DURATION.get(ship.getHullSize()) : data.ventDuration ;
            Global.getSoundPlayer().playSound("ai_core_pickup", 1f, 0.4f, ship.getLocation(), ship.getVelocity());
        }

        if (fluxTracker.isVenting() && fluxTracker.getHardFlux() >= amount*2*ship.getMutableStats().getFluxDissipation().getModifiedValue() * ship.getMutableStats().getVentRateMult().getModifiedValue()) {
            fluxTracker.setHardFlux(fluxTracker.getHardFlux()-(amount*2*ship.getMutableStats().getFluxDissipation().getModifiedValue() * ship.getMutableStats().getVentRateMult().getModifiedValue()));
        }
        
        if (!fluxTracker.isVenting()) {
            data.startedVenting = false;
            data.fluxLevelToVentTo = 0f;
            MutableShipStatsAPI stats = ship.getMutableStats();
            stats.getMaxSpeed().unmodifyFlat("eis_avaritia");
            stats.getAcceleration().unmodifyFlat("eis_avaritia");
            stats.getDeceleration().unmodifyFlat("eis_avaritia");
            stats.getTurnAcceleration().unmodifyFlat("eis_avaritia");
            stats.getTurnAcceleration().unmodifyPercent("eis_avaritia");
            stats.getMaxTurnRate().unmodifyFlat("eis_avaritia");
            stats.getMaxTurnRate().unmodifyPercent("eis_avaritia");
        }

        // < Stuff from Nia, adapted >
        //Apply our buff, if our duration is not yet up
        if (data.buffDurationRemaining > 0f) {
            if (ship.getHullSpec().isBuiltInMod("eis_avaritia") || ship.getVariant().getSMods().contains("eis_avaritia")) {
                ship.getMutableStats().getEnergyWeaponDamageMult().modifyPercent(data.buffId, DAMAGE_SBUFF_PERCENT);
                ship.getMutableStats().getBallisticWeaponDamageMult().modifyPercent(data.buffId, DAMAGE_SBUFF_PERCENT);
                ship.getMutableStats().getEnergyRoFMult().modifyMult(data.buffId, ROF_SBUFF_MULT);
                ship.getMutableStats().getBallisticRoFMult().modifyMult(data.buffId, ROF_SBUFF_MULT);
                ship.getMutableStats().getVentRateMult().modifyMult(data.buffId, 0f);
                if (ship == Global.getCombatEngine().getPlayerShip()) {
                    Global.getCombatEngine().maintainStatusForPlayerShip(data.buffId,"graphics/icons/hullsys/high_energy_focus.png", StatusTitle,"+" + Math.round(DAMAGE_SBUFF_PERCENT) + StatusText2 + Misc.getRoundedValueMaxOneAfterDecimal(data.buffDurationRemaining), false);
                }
            } else {
                ship.getMutableStats().getEnergyWeaponDamageMult().modifyPercent(data.buffId, DAMAGE_BUFF_PERCENT);
                ship.getMutableStats().getBallisticWeaponDamageMult().modifyPercent(data.buffId, DAMAGE_BUFF_PERCENT);
                ship.getMutableStats().getVentRateMult().modifyMult(data.buffId, 0f);
                if (ship == Global.getCombatEngine().getPlayerShip()) {
                    Global.getCombatEngine().maintainStatusForPlayerShip(data.buffId,"graphics/icons/hullsys/high_energy_focus.png", StatusTitle,"+" + Math.round(DAMAGE_BUFF_PERCENT) + StatusText2 + Misc.getRoundedValueMaxOneAfterDecimal(data.buffDurationRemaining), false);
                }
            }
            data.buffDurationRemaining -= amount;
            
            //If we are the player ship, also display a tooltip showing our current bonus
            /*if (ship == Global.getCombatEngine().getPlayerShip() && data.buffDurationRemaining > 0f) {
                Global.getCombatEngine().maintainStatusForPlayerShip(data.buffId,
                        "graphics/icons/hullsys/high_energy_focus.png", StatusTitle,
                        "+" + Math.round(DAMAGE_BUFF_PERCENT) + StatusText2, false);
            }*/
            Global.getSoundPlayer().playLoop("system_high_energy_focus_loop", ship, 1f, 0.6f, ship.getLocation(), ship.getVelocity());
            EnumSet<WeaponType> WEAPON_TYPES = EnumSet.of(WeaponType.BALLISTIC, WeaponType.ENERGY);
            // aaaaaaaaaaaaaa Selkie why did you make me do this
            // don't swap the max and mins, they're there to clamp glow between 0 and 1 in case of float errors or something
            // i'll kill you.
            if (data.buffDurationRemaining > (Float) DAMAGE_BUFF_DURATION.get(ship.getHullSize()) - FADE_IN_OUT_TIME) {
                ship.setWeaponGlow(Math.min(MAX_GLOW_PERCENT,-MAX_GLOW_PERCENT/FADE_IN_OUT_TIME*data.buffDurationRemaining + (Float) DAMAGE_BUFF_DURATION.get(ship.getHullSize())*MAX_GLOW_PERCENT/FADE_IN_OUT_TIME), SUS, WEAPON_TYPES);
            } else if (data.buffDurationRemaining < FADE_IN_OUT_TIME) {
                ship.setWeaponGlow(Math.max(0f,MAX_GLOW_PERCENT/FADE_IN_OUT_TIME*data.buffDurationRemaining), SUS, WEAPON_TYPES);
            } else {
                ship.setWeaponGlow(MAX_GLOW_PERCENT, SUS, WEAPON_TYPES);
            }
        } //If our duration IS up, remove the bonus
        else {
            ship.getMutableStats().getEnergyWeaponDamageMult().unmodify(data.buffId);
            ship.getMutableStats().getBallisticWeaponDamageMult().unmodify(data.buffId);
            ship.getMutableStats().getEnergyRoFMult().unmodifyMult(data.buffId);
            ship.getMutableStats().getBallisticRoFMult().unmodifyMult(data.buffId);
            ship.getMutableStats().getVentRateMult().unmodifyMult(data.buffId);
            EnumSet<WeaponType> WEAPON_TYPES = EnumSet.of(WeaponType.BALLISTIC, WeaponType.ENERGY);
            ship.setWeaponGlow(0f, SUS, WEAPON_TYPES);
        }

        // Venting AI stuff that I borrowed from Tart
        if (ship.getShipAI() == null) {
            return;
        }
        data.timer.advance(amount);
        if (data.timer.intervalElapsed()) {
            if (ship.getFluxTracker().isOverloadedOrVenting() || ship.getSystem().isActive()) {
                return;
            }

            MissileAPI closest = AIUtils.getNearestEnemyMissile(ship);
            if (closest != null && MathUtils.isWithinRange(ship, closest, 500f)) {
                return;
            }

            if (ship.getFluxTracker().getFluxLevel() < 0.5 && AIUtils.getNearbyEnemies(ship, data.maxRange) != null) {
                return;
            }
            
            if (ship.getFluxTracker().getFluxLevel() >= 0.7f && ship.getFluxTracker().getHardFlux()<ship.getFluxTracker().getCurrFlux()*0.7f && ship.getAIFlags().hasFlag(ShipwideAIFlags.AIFlags.MANEUVER_TARGET)) {
                //engine.addFloatingText(ship.getLocation(), "Over-fluxed, staying on target", 20f, SUS, ship, 1f, 1f);
                ship.giveCommand(ShipCommand.VENT_FLUX, null, 0);
                ship.getAIFlags().setFlag(ShipwideAIFlags.AIFlags.DO_NOT_BACK_OFF, 2f);
                ship.getAIFlags().setFlag(ShipwideAIFlags.AIFlags.DO_NOT_BACK_OFF_EVEN_WHILE_VENTING, 2f);
                ship.getAIFlags().setFlag(ShipwideAIFlags.AIFlags.BACK_OFF_MIN_RANGE, 2f, data.maxRange-ship.getCollisionRadius()*0.5f);
                ship.getAIFlags().setFlag(ShipwideAIFlags.AIFlags.MANEUVER_RANGE_FROM_TARGET, 2f, data.maxRange-ship.getCollisionRadius()*0.5f);
                return;
            }
            
            if (ship.getAIFlags().hasFlag(ShipwideAIFlags.AIFlags.DO_NOT_USE_SHIELDS) && !ship.getAIFlags().hasFlag(ShipwideAIFlags.AIFlags.DO_NOT_VENT) && !ship.getAIFlags().hasFlag(ShipwideAIFlags.AIFlags.HAS_INCOMING_DAMAGE)) {
                //engine.addFloatingText(ship.getLocation(), "Fluxing shields down", 20f, SUS, ship, 1f, 1f);
                ship.giveCommand(ShipCommand.VENT_FLUX, null, 0);
                ship.getAIFlags().setFlag(ShipwideAIFlags.AIFlags.DO_NOT_BACK_OFF, 2f);
                ship.getAIFlags().setFlag(ShipwideAIFlags.AIFlags.DO_NOT_BACK_OFF_EVEN_WHILE_VENTING, 2f);
                ship.getAIFlags().setFlag(ShipwideAIFlags.AIFlags.BACK_OFF_MIN_RANGE, 2f, data.maxRange-ship.getCollisionRadius()*0.5f);
                ship.getAIFlags().setFlag(ShipwideAIFlags.AIFlags.MANEUVER_RANGE_FROM_TARGET, 2f, data.maxRange-ship.getCollisionRadius()*0.5f);
                return;
            }

            //venting need
            float ventingNeed;
            switch (ship.getHullSize()) {
                case CAPITAL_SHIP:
                    ventingNeed = 2 * (float) Math.pow(ship.getFluxTracker().getFluxLevel(), 5f);
                    break;
                case CRUISER:
                    ventingNeed = 1.5f * (float) Math.pow(ship.getFluxTracker().getFluxLevel(), 4f);
                    break;
                case DESTROYER:
                    ventingNeed = (float) Math.pow(ship.getFluxTracker().getFluxLevel(), 3f);
                    break;
                default:
                    ventingNeed = (float) Math.pow(ship.getFluxTracker().getFluxLevel(), 2f);
                    break;
            }

            float hullFactor;
            switch (ship.getHullSize()) {
                case CAPITAL_SHIP:
                    hullFactor = (float) Math.pow(ship.getHullLevel(), 0.4f);
                    break;
                case CRUISER:
                    hullFactor = (float) Math.pow(ship.getHullLevel(), 0.6f);
                    break;
                case DESTROYER:
                    hullFactor = ship.getHullLevel();
                    break;
                default:
                    hullFactor = (float) Math.pow(ship.getHullLevel(), 2f);
                    break;
            }

            //situational danger
            float dangerFactor = 0;

            List<ShipAPI> nearbyEnemies = AIUtils.getNearbyEnemies(ship, 2000f);
            for (ShipAPI enemy : nearbyEnemies) {
                //reset often with timid or cautious personalities
                FleetSide side = FleetSide.PLAYER;
                if (ship.getOriginalOwner() > 0) {
                    side = FleetSide.ENEMY;
                }
                if (Global.getCombatEngine().getFleetManager(side).getDeployedFleetMember(ship) != null) {
                    PersonalityAPI personality = (Global.getCombatEngine().getFleetManager(side).getDeployedFleetMember(ship)).getMember().getCaptain().getPersonalityAPI();
                    if (personality.getId().equals("timid") || personality.getId().equals("cautious")) {
                        if (enemy.getFluxTracker().isOverloaded() && enemy.getFluxTracker().getOverloadTimeRemaining() > ship.getFluxTracker().getTimeToVent()) {
                            continue;
                        }
                        if (enemy.getFluxTracker().isVenting() && enemy.getFluxTracker().getTimeToVent() > ship.getFluxTracker().getTimeToVent()) {
                            continue;
                        }
                    }
                }
                if (!enemy.getFluxTracker().isOverloadedOrVenting() || !enemy.getEngineController().isFlamedOut()) {
                    if (ship.getShipTarget() != null && enemy != ship.getShipTarget()) {break;}
                    switch (enemy.getHullSize()) {
                        case CAPITAL_SHIP:
                            dangerFactor += Math.max(0, 3f - (MathUtils.getDistanceSquared(enemy.getLocation(), ship.getLocation()) / 1000000));
                            break;
                        case CRUISER:
                            dangerFactor += Math.max(0, 2.25f - (MathUtils.getDistanceSquared(enemy.getLocation(), ship.getLocation()) / 1000000));
                            break;
                        case DESTROYER:
                            dangerFactor += Math.max(0, 1.5f - (MathUtils.getDistanceSquared(enemy.getLocation(), ship.getLocation()) / 1000000));
                            break;
                        case FRIGATE:
                            dangerFactor += Math.max(0, 1f - (MathUtils.getDistanceSquared(enemy.getLocation(), ship.getLocation()) / 1000000));
                            break;
                        default:
                            dangerFactor += Math.max(0, 0.5f - (MathUtils.getDistanceSquared(enemy.getLocation(), ship.getLocation()) / 640000));
                            break;
                    }
                }
            }
            
            float decisionLevel = (ventingNeed * hullFactor + 1) / (dangerFactor + 1);
            //engine.addFloatingText(ship.getLocation(), "Yamete! "+decisionLevel, 20f, Amongus, ship, 1f, 1f);
            if (decisionLevel >= 1.5f || (ship.getFluxTracker().getFluxLevel() >= 0.55f && dangerFactor == 0)) {
                //engine.addFloatingText(ship.getLocation(), "Flux NOW "+decisionLevel, 20f, SUS, ship, 1f, 1f);
                ship.giveCommand(ShipCommand.VENT_FLUX, null, 0);
                ship.getAIFlags().setFlag(ShipwideAIFlags.AIFlags.DO_NOT_BACK_OFF, 2f);
                ship.getAIFlags().setFlag(ShipwideAIFlags.AIFlags.DO_NOT_BACK_OFF_EVEN_WHILE_VENTING, 2f);
                ship.getAIFlags().setFlag(ShipwideAIFlags.AIFlags.BACK_OFF_MIN_RANGE, 2f, data.maxRange-ship.getCollisionRadius()*0.5f);
                ship.getAIFlags().setFlag(ShipwideAIFlags.AIFlags.MANEUVER_RANGE_FROM_TARGET, 2f, data.maxRange-ship.getCollisionRadius()*0.5f);
            }
        }
        // < end Stuff from Nia>
    }

    @Override
    public boolean isApplicableToShip(ShipAPI ship) {
        return ship.isFrigate() || ship.isDestroyer() || ship.isCruiser();
    }

    /*@Override
    public String getUnapplicableReason(ShipAPI ship) {
        if (!(ship.isDestroyer() || ship.isCruiser())) {
            return ApplicableText;
        }
        return null;
    }*/

    @Override
    public void addPostDescriptionSection(TooltipMakerAPI tooltip, ShipAPI.HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
        float HEIGHT = 50f;
        float PAD = 10f;
        Color YELLOW = new Color(241, 199, 0);

        TooltipMakerAPI avaritia = tooltip.beginImageWithText(Icon, HEIGHT);
        avaritia.addPara(Title, 0f, YELLOW, Title);
        avaritia.addPara(Text1, 0f, YELLOW, Math.round(PERCENT_PER_VENT) + "%");
        avaritia.addPara(Text4, 0f);
        avaritia.addPara(Text2, 0f, YELLOW, Math.round(MIN_VENT_PERCENT) + "%", Math.round(MIN_VENT_PERCENT) + "%");
        //avaritia.addPara(Text3, 0f, Misc.getPositiveHighlightColor(), Math.round(VENT_RATE_BONUS) + "%");
        //avaritia.addPara(Text4, 0f, Misc.getPositiveHighlightColor(), "doubled");
        if (isForModSpec) {
            avaritia.addPara(Text5, 0f, Misc.getPositiveHighlightColor(), Math.round(DAMAGE_BUFF_PERCENT) + "%", "2");
            avaritia.addPara(Text7, 0f, Misc.getPositiveHighlightColor(), "80", Math.round(DAMAGE_SBUFF_PERCENT) + "%");
            avaritia.addPara(Text6, 0f, Misc.getGrayColor(), Misc.getHighlightColor(), Math.round(DAMAGE_SBUFF_PERCENT) + "%");
            avaritia.addPara(Text8, 0f, Misc.getGrayColor(), Misc.getHighlightColor(), "+"+Math.round(DAMAGE_SBUFF_PERCENT) + "%");
	} else if (ship.getHullSpec().isBuiltInMod("eis_avaritia") || ship.getVariant().getSMods().contains("eis_avaritia")) {
            avaritia.addPara(Text5, 0f, Misc.getPositiveHighlightColor(), Math.round(DAMAGE_BUFF_PERCENT) + "%", Misc.getRoundedValueMaxOneAfterDecimal((Float) DAMAGE_BUFF_DURATION.get(ship.getHullSize())) + "");
            avaritia.addPara(Text7, 0f, Misc.getPositiveHighlightColor(), String.valueOf(Math.round((float)speed.get(hullSize))));
            avaritia.addPara(Text6, 0f, Misc.getPositiveHighlightColor(), Misc.getHighlightColor(), Math.round(DAMAGE_SBUFF_PERCENT) + "%");
            avaritia.addPara(Text8, 0f, Misc.getPositiveHighlightColor(), Misc.getHighlightColor(), "+"+Math.round(DAMAGE_SBUFF_PERCENT) + "%");
        } else if (!isForModSpec) {
            avaritia.addPara(Text5, 0f, Misc.getPositiveHighlightColor(), Math.round(DAMAGE_BUFF_PERCENT) + "%", Misc.getRoundedValueMaxOneAfterDecimal((Float) DAMAGE_BUFF_DURATION.get(ship.getHullSize())) + "");
            avaritia.addPara(Text7, 0f, Misc.getPositiveHighlightColor(), String.valueOf(Math.round((float)speed.get(hullSize))));
            avaritia.addPara(Text6, 0f, Misc.getGrayColor(), Misc.getHighlightColor(), Math.round(DAMAGE_SBUFF_PERCENT) + "%");
            avaritia.addPara(Text8, 0f, Misc.getGrayColor(), Misc.getHighlightColor(), "+"+Math.round(DAMAGE_SBUFF_PERCENT) + "%");
	}
        tooltip.addImageWithText(PAD);
	tooltip.addPara(Text5b, PAD, Misc.getNegativeHighlightColor(), Text5b);
        tooltip.addPara(ApplicableText, PAD, Misc.getNegativeHighlightColor(), ApplicableText);

    }

    public static class AvaritiaData {
        float ventDuration = 0f;
        boolean startedVenting = false;
        float fluxLevelToVentTo = 0f;
        float fluxLevelWhenStartedVenting = -1f;
        float buffDurationRemaining = 0f;
        String buffId = "";
        boolean runOnce = false;
        float maxRange = 0f;
        IntervalUtil timer = new IntervalUtil(0.5f, 1.5f);
    }
}
