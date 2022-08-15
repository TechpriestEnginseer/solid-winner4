package data.scripts.shipsystems;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CollisionClass;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.MissileAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipCommand;
import com.fs.starfarer.api.combat.ShipEngineControllerAPI;
import com.fs.starfarer.api.combat.ShipSystemAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import com.fs.starfarer.api.util.Misc;

import org.dark.shaders.distortion.DistortionShader;
import org.dark.shaders.distortion.RippleDistortion;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lwjgl.util.vector.Vector2f;

import java.awt.Color;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.lazywizard.lazylib.combat.CombatUtils;

public class eis_zandatsu2 extends BaseShipSystemScript {
    public static final float MAX_TURN_BONUS = 50f;
    public static final float TURN_ACCEL_BONUS = 50f;
    public static final float INSTANT_BOOST_FLAT = 300f;
    public static final float INSTANT_BOOST_MULT = 5f;
    private static final Color ENGINE_COLOR = new Color(255, 10, 10);
    // SNEED private static final Color CONTRAIL_COLOR = new Color(255, 100, 100, 75);
    private static final Color BOOST_COLOR = new Color(255, 175, 175, 200);
    private static final Color Sneed = new Color (255,200,0,155);
    private static final Vector2f ZERO = new Vector2f();
    //private final Object ENGINEKEY1 = new Object();
    private final Object ENGINEKEY2 = new Object();
    private final Map<Integer, Float> engState = new HashMap<>();
    private boolean ended = false;
    private float boostScale = 0.75f;
    private float boostVisualDir = 0f;
    private boolean boostForward = false;
    private static String poopystinky = Global.getSettings().getString("eis_ironshell", "eis_zandatsu");
    private static final float BUFF_DURATION = 12.0f;
    private static final float REFLECT_RANGE = 300f; // added onto ship collision radius
    private static final float ROTATION_SPEED = 420f; // 420f how fast missiles get rotated in degrees per second
    private static final float ROF_BONUS = 1.5f;
    private static final float FLUX_REDUCTION = 25f;
    private boolean formerlychuck = false;
    private boolean reset = true;
    private boolean reflectSuccess = false;
    private int doEffects = 0;
    private float activeTime = 0f;
    private CombatEngineAPI engine =  Global.getCombatEngine();
    private ShipAPI ship;
    private Map<MissileAPI,MissileTracker> missileMap = new HashMap<>();
    private float sneedreloadtime = 1.5f;

    @Override
    public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
        if (engine.isPaused() || stats.getEntity() == null) return;
        if (reset) {
            reset = false;
            //if (activeTime < 0.5f) {
                //reflectSuccess = false;
            //}
            doEffects = 0;
            ship = (ShipAPI)stats.getEntity();
        }
        float ADJUSTED_RANGE = ship.getMutableStats().getSystemRangeBonus().computeEffective(REFLECT_RANGE);
        float shipRadius = ship.getCollisionRadius();
        float amount = engine.getElapsedInLastFrame();
        ShipAPI ship = (ShipAPI) stats.getEntity();
        if (ship == null) {return;}
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

            List<ShipEngineControllerAPI.ShipEngineAPI> engList = ship.getEngineController().getShipEngines();
            for (int i = 0; i < engList.size(); i++) {
                ShipEngineControllerAPI.ShipEngineAPI eng = engList.get(i);
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
            formerlychuck = true;
            /* Black magic to counteract the effects of maneuvering penalties/bonuses on the effectiveness of this system */
            float decelMult = Math.max(0.5f, Math.min(2f, stats.getDeceleration().getModifiedValue() / stats.getDeceleration().getBaseValue()));
            float adjFalloffPerSec = 0.45f * (float) Math.pow(decelMult, 0.5); //0.55f for frigates
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

            List<ShipEngineControllerAPI.ShipEngineAPI> engList = ship.getEngineController().getShipEngines();
            for (int i = 0; i < engList.size(); i++) {
                ShipEngineControllerAPI.ShipEngineAPI eng = engList.get(i);
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
            formerlychuck = true;
            stats.getMaxTurnRate().modifyPercent(id, MAX_TURN_BONUS);
            stats.getTurnAcceleration().modifyPercent(id, TURN_ACCEL_BONUS * effectLevel);
            ship.getEngineController().getExtendLengthFraction().advance(amount * 2f);
            ship.getEngineController().getExtendWidthFraction().advance(amount * 2f);
            ship.getEngineController().getExtendGlowFraction().advance(amount * 2f);
            List<ShipEngineControllerAPI.ShipEngineAPI> engList = ship.getEngineController().getShipEngines();
            for (int i = 0; i < engList.size(); i++) {
                ShipEngineControllerAPI.ShipEngineAPI eng = engList.get(i);
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
                boostScale = 0.75f; // 0.75f biggest sneed ever
                if (ship.getEngineController().isAccelerating()) {
                    direction.y += 0.55f; //0.75f - 0.2f
                    boostScale -= 0.2f; //0.2f
                    boostForward = true;
                } else if (ship.getEngineController().isAcceleratingBackwards() || ship.getEngineController().isDecelerating()) {
                    direction.y -= 0.4f; //0.75f - 0.35f ?
                    boostScale -= 0.35f; //0.35f
                }
                if (ship.getEngineController().isStrafingLeft()) {
                    direction.x -= 1f;
                    boostScale += 0.25f;
                    boostForward = false;
                } else if (ship.getEngineController().isStrafingRight()) {
                    direction.x += 1f;
                    boostScale += 0.25f;
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
                for (ShipEngineControllerAPI.ShipEngineAPI eng : ship.getEngineController().getShipEngines()) {
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
        if (state == State.OUT) {
            if (doEffects < 10) {
                if (doEffects == 0) {
                        RippleDistortion ripple = new RippleDistortion(ship.getLocation(), ship.getVelocity());
                    ripple.setSize(ADJUSTED_RANGE + shipRadius * 1.75f);
                    ripple.setIntensity(shipRadius);
                    ripple.setFrameRate(60f);
                    ripple.fadeInSize(0.75f);
                    ripple.fadeOutIntensity(0.5f);
                    DistortionShader.addDistortion(ripple);
                }
                for (int i = 0; i < 2; i++) {
                    Vector2f particleLoc = ship.getLocation();
                    Vector2f particleVel = MathUtils.getRandomPointInCircle(null, (shipRadius + ADJUSTED_RANGE) * 2f);
                    engine.addSmoothParticle(particleLoc, particleVel, (float)Math.random() * 15f + 5f, 0.5f, 0.75f, Color.white);
                }
                doEffects++;
            }
            List<MissileAPI> missilesInRange = CombatUtils.getMissilesWithinRange(ship.getLocation(), ADJUSTED_RANGE + shipRadius);
            for (MissileAPI missile : missilesInRange) {
                if (!missileMap.keySet().contains(missile) && missile.getOwner() != ship.getOwner())
                    missileMap.put(missile, new MissileTracker(missile));
            }
            List<MissileAPI> toRemove = new ArrayList<>();
            for (MissileAPI missile : missileMap.keySet()) {
                if (missile == null || missile.isFading()) {
                    toRemove.add(missile);continue;
                }
                if (missile.isMine()) {
                    toRemove.add(missile);missile.fadeOutThenIn(amount);continue;
                }
                MissileTracker tracker = missileMap.get(missile);
                if (!tracker.isFacingOrigin()) {
                    if (tracker.shouldTurnLeft()) {
                        VectorUtils.rotate(missile.getVelocity(), ROTATION_SPEED * amount);
                        missile.setFacing(missile.getFacing() + ROTATION_SPEED * amount);
                    } else {
                        VectorUtils.rotate(missile.getVelocity(), -ROTATION_SPEED * amount);
                        missile.setFacing(missile.getFacing() - ROTATION_SPEED * amount);
                    }
                }
                if (tracker.getTotalRotation() > 30 && missile.getOwner() != ship.getOwner()) {
                    if (missile.isGuided()) {missile.setMissileAI(new eis_dummyMissileAI(missile, missile.getSource()));}
                    missile.setOwner(ship.getOwner());
                    missile.setSource(ship);
                    reflectSuccess = true;
                    activeTime = 12f;
                }
                if (!missilesInRange.contains(missile))toRemove.add(missile);
            }
            for (MissileAPI missile : toRemove) {
                missileMap.remove(missile);
            }
            ship.setCollisionClass(CollisionClass.NONE);
        }
        if (state == State.IDLE) {
            if (formerlychuck) {
                stats.getMaxTurnRate().unmodify(id);
                stats.getDeceleration().unmodify(id);
                stats.getTurnAcceleration().unmodify(id);
                ship.setCollisionClass(CollisionClass.SHIP);
                formerlychuck = false;
                ended = false;boostScale = 0.75f;boostVisualDir = 0f;boostForward = false;engState.clear();reset = true;
            }
            if (activeTime > 0f) {
                activeTime -= amount;
                if (reflectSuccess && activeTime <= BUFF_DURATION) {
                    stats.getBallisticRoFMult().modifyMult(id, ROF_BONUS);
                    stats.getBallisticWeaponFluxCostMod().modifyMult(id, 1f - (FLUX_REDUCTION * 0.01f));
                    ship.setWeaponGlow(1f, Sneed, EnumSet.of(WeaponAPI.WeaponType.BALLISTIC));
                    sneedreloadtime = 2f;
                }
                if (reflectSuccess && activeTime <= 0) {
                    stats.getBallisticRoFMult().unmodify(id);
                    stats.getBallisticWeaponFluxCostMod().unmodify(id);
                    ship.setWeaponGlow(0f, Sneed, EnumSet.of(WeaponAPI.WeaponType.BALLISTIC));
                    reflectSuccess = false;
                    sneedreloadtime = 1.5f;
                }
                for (WeaponAPI w : ship.getAllWeapons()) {
                    float reloadRate = w.getSpec().getAmmoPerSecond() * 1.5f;
                    float nuCharge = reloadRate * sneedreloadtime;
                    if (w.getType() == WeaponAPI.WeaponType.BALLISTIC && w.usesAmmo() && reloadRate > 0) {
                        w.getAmmoTracker().setAmmoPerSecond(nuCharge);
                    }
                }
            }
        }
    }

    @Override
    public void unapply(MutableShipStatsAPI stats, String id) {
        /*ShipAPI ship = (ShipAPI) stats.getEntity();
        if (ship == null) {
            return;
        }
        stats.getMaxTurnRate().unmodify(id);
        stats.getDeceleration().unmodify(id);
        stats.getTurnAcceleration().unmodify(id);
	//stats.getBallisticRoFMult().unmodify(id);
	//stats.getBallisticWeaponFluxCostMod().unmodify(id);
        ended = false;boostScale = 0.75f;boostVisualDir = 0f;boostForward = false;engState.clear();reset = true;*/
    }
    
    /*@Override
    public String getInfoText(ShipSystemAPI system, ShipAPI ship) {
        if (ship != null) {
            if (ship.getEngineController().isFlamedOut()) {
                return "CANNOT THRUST";
            }
        }
        return null;
    }*/

    private static float getSystemEngineScale(ShipEngineControllerAPI.ShipEngineAPI engine, float direction) {
        float engAngle = engine.getEngineSlot().getAngle();
        if (Math.abs(MathUtils.getShortestRotation(engAngle, direction)) > 100f) {
            return 1f;
        } else {
            return 0f;
        }
    }



    @Override
    public StatusData getStatusData(int index, State state, float effectLevel) {
        if (index == 0 && reflectSuccess && activeTime > 0f)
            return new StatusData(poopystinky + Misc.getRoundedValueMaxOneAfterDecimal(activeTime), false);
        return null;
    }

    private class MissileTracker  {
        private MissileAPI missile;
        private Vector2f source;
        private float initialFacing;

        public MissileTracker(MissileAPI missile) {
            if (missile.getSource() instanceof ShipAPI) {
                this.source = missile.getSource().getLocation();
            } else {
                this.source = MathUtils.getRandomPointInCone(missile.getLocation(), 800f, missile.getFacing() - 150, missile.getFacing() - 210);
            }
            this.missile = missile;
            this.initialFacing = missile.getFacing();
        }
        public boolean isFacingOrigin() {
            if (source == null)
                return false;
            return Math.abs(MathUtils.getShortestRotation(missile.getFacing(), VectorUtils.getAngle(missile.getLocation(), source))) < 10;
        }
        public boolean shouldTurnLeft() {
            if (source == null)
                return false;
            float delta = MathUtils.getShortestRotation(missile.getFacing(), VectorUtils.getAngle(missile.getLocation(), source));
            return delta > 0;
        }
        public float getTotalRotation() {
            return Math.abs((missile.getFacing() - initialFacing) % 360);
        }
    }
}