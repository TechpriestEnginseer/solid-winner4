package data.scripts.shipsystems;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.ShipCommand;
import com.fs.starfarer.api.combat.ShipEngineControllerAPI.ShipEngineAPI;
import com.fs.starfarer.api.combat.ShipSystemAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import com.fs.starfarer.api.util.Misc;
import java.awt.Color;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lwjgl.util.vector.Vector2f;
//Nonononono don't steal too much of this, or else Dark Revenant will .. ooooh! NOO DON'T DO IT AHHHHHHHHHHHHH
//Everyday I am baffled by this code everyday... 

public class eis_jump extends BaseShipSystemScript {
    public static final float MAX_TURN_BONUS = 50f;
    public static final float TURN_ACCEL_BONUS = 50f;
    public static final float INSTANT_BOOST_FLAT = 250f;
    public static final float INSTANT_BOOST_MULT = 4.5f;
    public static final Map<HullSize, Integer> IMGONNACHARGE = new HashMap<>();
    public static final Map<HullSize, Float> OHGODIMRECHARGING = new HashMap<>();
    private static final Color ENGINE_COLOR = new Color(255, 10, 10);
    //private static final Color CONTRAIL_COLOR = new Color(255, 100, 100, 75);
    private static final Color BOOST_COLOR = new Color(255, 175, 175, 200);
    private static final Vector2f ZERO = new Vector2f();
    private static final String poopystinky = Global.getSettings().getString("eis_ironshell", "eis_jump_failed");
    //private final Object ENGINEKEY1 = new Object();
    private final Object ENGINEKEY2 = new Object();
    private final Map<Integer, Float> engState = new HashMap<>();
    private boolean ended = false;
    private float boostScale = 0.75f;
    private float boostVisualDir = 0f;
    private boolean boostForward = false;
    static {
        
        IMGONNACHARGE.put(HullSize.FIGHTER, 2);
        IMGONNACHARGE.put(HullSize.FRIGATE, 2);
        IMGONNACHARGE.put(HullSize.DESTROYER, 2);
        IMGONNACHARGE.put(HullSize.CRUISER, 2);
        IMGONNACHARGE.put(HullSize.CAPITAL_SHIP, 2);
        
        OHGODIMRECHARGING.put(HullSize.FIGHTER, 0.18f);
        OHGODIMRECHARGING.put(HullSize.FRIGATE, 0.18f);
        OHGODIMRECHARGING.put(HullSize.DESTROYER, 0.18f);
        OHGODIMRECHARGING.put(HullSize.CRUISER, 0.15f);
        OHGODIMRECHARGING.put(HullSize.CAPITAL_SHIP, 0.15f);
    }

    @Override
    public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
        ShipAPI ship = (ShipAPI) stats.getEntity();
        if (ship == null) {return;}
        float shipRadius = ship.getCollisionRadius();
        float amount = Global.getCombatEngine().getElapsedInLastFrame();
        if (Global.getCombatEngine().isPaused()) {amount = 0f;}
        
        // SNEED ship.getEngineController().fadeToOtherColor(ENGINEKEY1, ENGINE_COLOR, CONTRAIL_COLOR, effectLevel, 1f);
        ship.getEngineController().extendFlame(ENGINEKEY2, 0f, 1f * effectLevel, 3f * effectLevel);

        if (!ended) {
            /* Unweighted direction calculation for visual purposes - 0 degrees is forward */
            Vector2f direction = new Vector2f();
            if (ship.getEngineController().isAccelerating()) {
                direction.y += 1f;
            } else if (ship.getEngineController().isAcceleratingBackwards() || ship.getEngineController().isDecelerating()) {
                direction.y -= 1f;
            }
            if (ship.getEngineController().isStrafingLeft()) {
                direction.x -= 1f;
            } else if (ship.getEngineController().isStrafingRight()) {
                direction.x += 1f;
            }
            if (direction.length() <= 0f) {
                direction.y = 1f;
            }
            boostVisualDir = MathUtils.clampAngle(VectorUtils.getFacing(direction) - 90f);
        }

        if (state == State.IN) {

            List<ShipEngineAPI> engList = ship.getEngineController().getShipEngines();
            for (int i = 0; i < engList.size(); i++) {
                ShipEngineAPI eng = engList.get(i);
                if (eng.isSystemActivated()) {
                    float targetLevel = getSystemEngineScale(eng, boostVisualDir) * 0.4f;
                    Float currLevel = engState.get(i);
                    if (currLevel == null) {
                        currLevel = 0f;
                    }
                    if (currLevel > targetLevel) {
                        currLevel = Math.max(targetLevel, currLevel - (amount / 0.1f));
                    } else {
                        currLevel = Math.min(targetLevel, currLevel + (amount / 0.1f));
                    }
                    engState.put(i, currLevel);
                    ship.getEngineController().setFlameLevel(eng.getEngineSlot(), currLevel);
                }
            }
        }

        if (state == State.OUT) {
            /* Black magic to counteract the effects of maneuvering penalties/bonuses on the effectiveness of this system */
            float decelMult = Math.max(0.5f, Math.min(2f, stats.getDeceleration().getModifiedValue() / stats.getDeceleration().getBaseValue()));
            float adjFalloffPerSec = 0.25f * (float) Math.pow(decelMult, 0.5);
            float maxDecelPenalty = 1f / decelMult;

            stats.getMaxTurnRate().unmodify(id);
            stats.getDeceleration().modifyMult(id, (1f - effectLevel) * 1f * maxDecelPenalty);
            stats.getTurnAcceleration().modifyPercent(id, TURN_ACCEL_BONUS * effectLevel);

            if (boostForward) {
                ship.giveCommand(ShipCommand.ACCELERATE, null, 0);
                ship.blockCommandForOneFrame(ShipCommand.ACCELERATE_BACKWARDS);
                ship.blockCommandForOneFrame(ShipCommand.DECELERATE);
            } else {
                ship.blockCommandForOneFrame(ShipCommand.ACCELERATE);
            }

            if (amount > 0f) {
                ship.getVelocity().scale((float) Math.pow(adjFalloffPerSec, amount));
            }

            List<ShipEngineAPI> engList = ship.getEngineController().getShipEngines();
            for (int i = 0; i < engList.size(); i++) {
                ShipEngineAPI eng = engList.get(i);
                if (eng.isSystemActivated()) {
                    float targetLevel = getSystemEngineScale(eng, boostVisualDir) * effectLevel;
                    if (targetLevel >= (1f - 0.15f/0.9f)) {
                        targetLevel = 1f;
                    } else {
                        targetLevel = targetLevel / (1f - 0.15f/0.9f);
                    }
                    engState.put(i, targetLevel);
                    ship.getEngineController().setFlameLevel(eng.getEngineSlot(), targetLevel);
                }
            }
        } else if (state == State.ACTIVE) {
            stats.getMaxTurnRate().modifyPercent(id, MAX_TURN_BONUS);
            stats.getTurnAcceleration().modifyPercent(id, TURN_ACCEL_BONUS * effectLevel);
            ship.getEngineController().getExtendLengthFraction().advance(amount * 2f);
            ship.getEngineController().getExtendWidthFraction().advance(amount * 2f);
            ship.getEngineController().getExtendGlowFraction().advance(amount * 2f);
            List<ShipEngineAPI> engList = ship.getEngineController().getShipEngines();
            for (int i = 0; i < engList.size(); i++) {
                ShipEngineAPI eng = engList.get(i);
                if (eng.isSystemActivated()) {
                    float targetLevel = getSystemEngineScale(eng, boostVisualDir);
                    Float currLevel = engState.get(i);
                    if (currLevel == null) {
                        currLevel = 0f;
                    }
                    if (currLevel > targetLevel) {
                        currLevel = Math.max(targetLevel, currLevel - (amount / 0.1f));
                    } else {
                        currLevel = Math.min(targetLevel, currLevel + (amount / 0.1f));
                    }
                    engState.put(i, currLevel);
                    ship.getEngineController().setFlameLevel(eng.getEngineSlot(), currLevel);
                }
            }
        }

        if (state == State.OUT) {
            if (!ended) {
                Vector2f direction = new Vector2f();
                boostForward = false;
                boostScale = 0.75f;
                if (ship.getEngineController().isAccelerating()) {
                    direction.y += 0.55f; //0.75f - 0.2f
                    boostScale -= 0.1f;
                    boostForward = true;
                } else if (ship.getEngineController().isAcceleratingBackwards() || ship.getEngineController().isDecelerating()) {
                    direction.y -= 0.4f; //0.75f - 0.35f ?
                    boostScale -= 0.35f;
                }
                if (ship.getEngineController().isStrafingLeft()) {
                    direction.x -= 1f;
                    boostScale += 0.3f; //from 0.25f an increase
                    boostForward = false;
                } else if (ship.getEngineController().isStrafingRight()) {
                    direction.x += 1f;
                    boostScale += 0.3f;
                    boostForward = false;
                }
                if (direction.length() <= 0f) {
                    direction.y = 0.55f; //0.75f - 0.2f ?
                    boostScale -= 0.2f;
                }
                Misc.normalise(direction);
                VectorUtils.rotate(direction, ship.getFacing() - 90f, direction);
                direction.scale(((ship.getMaxSpeedWithoutBoost() * INSTANT_BOOST_MULT) + INSTANT_BOOST_FLAT) * boostScale);
                Vector2f.add(ship.getVelocity(), direction, ship.getVelocity());
                ended = true;

                float duration = (float) Math.sqrt(shipRadius) / 25f;
                ship.getEngineController().getExtendLengthFraction().advance(1f);
                ship.getEngineController().getExtendWidthFraction().advance(1f);
                ship.getEngineController().getExtendGlowFraction().advance(1f);
                for (ShipEngineAPI eng : ship.getEngineController().getShipEngines()) {
                    float level = 1f;
                    if (eng.isSystemActivated()) {
                        level = getSystemEngineScale(eng, boostVisualDir);
                    }
                    if ((eng.isActive() || eng.isSystemActivated()) && (level > 0f)) {
                        Color bigBoostColor = new Color(
                                Math.round(0.1f * ENGINE_COLOR.getRed()),
                                Math.round(0.1f * ENGINE_COLOR.getGreen()),
                                Math.round(0.1f * ENGINE_COLOR.getBlue()),
                                Math.round(0.3f * ENGINE_COLOR.getAlpha() * level));
                        Color boostColor = new Color(BOOST_COLOR.getRed(), BOOST_COLOR.getGreen(), BOOST_COLOR.getBlue(),
                                Math.round(BOOST_COLOR.getAlpha() * level));
                        Global.getCombatEngine().spawnExplosion(eng.getLocation(), ZERO, bigBoostColor,
                                2.5f * 4f * boostScale * eng.getEngineSlot().getWidth(), duration);
                        Global.getCombatEngine().spawnExplosion(eng.getLocation(), ZERO, boostColor,
                                2.5f * 2f * boostScale * eng.getEngineSlot().getWidth(), 0.15f);
                    }
                }
            }
        }
    }

    @Override
    public void unapply(MutableShipStatsAPI stats, String id) {
        ShipAPI ship = (ShipAPI) stats.getEntity();
        if (ship == null) {
            return;
        }
        ended = false;
        boostScale = 0.75f;
        boostVisualDir = 0f;
        boostForward = false;
        engState.clear();

        stats.getMaxTurnRate().unmodify(id);
        stats.getDeceleration().unmodify(id);
        stats.getTurnAcceleration().unmodify(id);
    }

    @Override
    public String getInfoText(ShipSystemAPI system, ShipAPI ship) {
        if (ship != null) {
            if (ship.getEngineController().isFlamedOut()) {
                return poopystinky;
            }
        }
        return null;
    }
    
    @Override
    public float getRegenOverride(ShipAPI ship) {
        if (ship != null) {
            return OHGODIMRECHARGING.get(ship.getHullSize());
        }
        return -1;
    }
    
    @Override
    public int getUsesOverride(ShipAPI ship) {
        if (ship != null) {
            return IMGONNACHARGE.get(ship.getHullSize());
        }
        return -1;
    }

    private static float getSystemEngineScale(ShipEngineAPI engine, float direction) {
        float engAngle = engine.getEngineSlot().getAngle();
        if (Math.abs(MathUtils.getShortestRotation(engAngle, direction)) > 100f) {
            return 1f;
        } else {
            return 0f;
        }
    }
}
