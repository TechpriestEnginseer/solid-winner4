package data.scripts.shipsystems;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.MissileAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import com.fs.starfarer.api.util.Misc;

import org.dark.shaders.distortion.DistortionShader;
import org.dark.shaders.distortion.RippleDistortion;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.lwjgl.util.vector.Vector2f;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class eis_damned_vengeance extends BaseShipSystemScript {
    private static final float BUFF_DURATION = 9.0f;
    private static final float DEBUFF_DURATION = 3.0f; 
    private static final float REFLECT_RANGE = 400f; // added onto ship collision radius
    private static final float ROTATION_SPEED = 420f; // how fast missiles get rotated in degrees per second
    public static final float SHIELD_ARC_BONUS = 40f;
    public static final float SHIELD_BONUS = .25f;
    public static float PIERCE_MULT = 0.5f;
    private float ORIGINALCOOMSAUCE;
    
    private static String poopystinky = Global.getSettings().getString("eis_ironshell", "eis_taste_vengeance1");
    private static String poopystinky2 = Global.getSettings().getString("eis_ironshell", "eis_taste_vengeance2");
    private static String poopystinky3 = Global.getSettings().getString("eis_ironshell", "eis_taste_vengeance3");
    private static String poopystinky4 = Global.getSettings().getString("eis_ironshell", "eis_taste_vengeance4");
    
    private static final Color PARRY_FAIL_CORE_COLOR = new Color (255, 10, 0, 70); // shield colors if debuffed
    private static final Color PARRY_FAIL_RING_COLOR = new Color (255,100,77,175);
    private static final Color PARRY_SUCCESS_CORE_COLOR = new Color (79,187,255,70); // shield colors if buffed
    private static final Color PARRY_SUCCESS_RING_COLOR = new Color (205,235,255,175);
    private static final Color PARRY_JITTER_RING_COLOR = new Color (255,255,255,80);
    private Color color = new Color(100,165,255,255);
    
    private boolean reset = true;
    private boolean reflectSuccess = false;
    
    private int doEffects = 0;
    
    private float activeTime = 0f;
    private float SpaghettiSauce;
    
    private CombatEngineAPI engine =  Global.getCombatEngine();
    
    private ShipAPI ship;
    
    private Color initialShieldRingColor;
    private Color initialShieldCoreColor;
    
    private Map<MissileAPI,MissileTracker> missileMap = new HashMap<>();

    @Override
    public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
        if (engine.isPaused() || stats.getEntity() == null) 
            return;
        if (stats.getEntity().getShield() == null)
            return;
        if (reset) {
            reset = false;
            reflectSuccess = false;
            doEffects = 0;
            activeTime = 0f;
            ship = (ShipAPI)stats.getEntity();
            ORIGINALCOOMSAUCE = ship.getShield().getArc();
            initialShieldCoreColor = ship.getShield().getInnerColor();
            initialShieldRingColor = ship.getShield().getRingColor();
        }
		float jitterLevel = effectLevel;
		float jitterRangeBonus = 0;
		float maxRangeBonus = 10f;
        Color jitterColor = PARRY_JITTER_RING_COLOR;
        Color jitterUnderColor = PARRY_JITTER_RING_COLOR;
        if (state == State.ACTIVE) {
			jitterLevel = 0f;
			jitterRangeBonus = maxRangeBonus;
        } else if (state == State.OUT) 
        {
			jitterRangeBonus = jitterLevel * maxRangeBonus;
            if (activeTime > (BUFF_DURATION-0.5f)) {
                jitterLevel = Math.max(1 - activeTime / BUFF_DURATION, 1.0f);
                ship.setJitter(this, jitterColor, jitterLevel, 1, 0, 0 + jitterRangeBonus);
		ship.setJitterUnder(this, jitterUnderColor, jitterLevel, 10, 0f, 7f + jitterRangeBonus);
            }
        } else {
            jitterLevel = 0;
        }
        float ADJUSTED_RANGE = ship.getMutableStats().getSystemRangeBonus().computeEffective(REFLECT_RANGE);
        float amount = engine.getElapsedInLastFrame();
        if (state == State.ACTIVE) {
            if (doEffects < 10) {
                if (doEffects == 0) { // use doEffects % num == 0 to do it a few times if you want
                    RippleDistortion ripple = new RippleDistortion(ship.getLocation(), ship.getVelocity());
                    ripple.setSize(ADJUSTED_RANGE + ship.getCollisionRadius() * 1.75f);
                    ripple.setIntensity(ship.getCollisionRadius());
                    ripple.setFrameRate(60f);
                    ripple.fadeInSize(0.75f);
                    ripple.fadeOutIntensity(0.5f);
                    DistortionShader.addDistortion(ripple);
                }
                for (int i = 0; i < 4; i++) {
                    Vector2f particleLoc = ship.getLocation();
                    Vector2f particleVel = MathUtils.getRandomPointInCircle(null, (ship.getCollisionRadius() + ADJUSTED_RANGE) * 2f);
                    engine.addSmoothParticle(particleLoc, particleVel, (float)Math.random() * 15f + 5f, 0.5f, 0.75f, Color.white);
                }
                doEffects++;
            }
            //I see the coom in my sight.
            List<MissileAPI> missilesInRange = CombatUtils.getMissilesWithinRange(ship.getLocation(), ADJUSTED_RANGE + ship.getCollisionRadius());
            for (MissileAPI missile : missilesInRange) {
                if (!missileMap.keySet().contains(missile) && missile.getOwner() != ship.getOwner())
                    missileMap.put(missile, new MissileTracker(missile));
            }
            List<MissileAPI> toRemove = new ArrayList<>();
            for (MissileAPI missile : missileMap.keySet()) {
                if (missile == null || missile.isFading()) {
                    toRemove.add(missile);
                    continue;
                }
                if (missile.isMine()) { //Just ignore, I don't want to have sex with Doom Mines. It just makes them time out. :^)
                    toRemove.add(missile);
                    missile.fadeOutThenIn(amount);
                    continue;
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
                if (tracker.getTotalRotation() > 90 && missile.getOwner() != ship.getOwner()) {
                    if (missile.isGuided()) {missile.setMissileAI(new eis_dummyMissileAI(missile, missile.getSource()));}
                    missile.setOwner(ship.getOwner());
                    missile.setSource(ship);
                    reflectSuccess = true;
                }
                if (!missilesInRange.contains(missile)) toRemove.add(missile);
            }
            for (MissileAPI missile : toRemove) {
                missileMap.remove(missile);
            }
        }
        if (state == State.OUT) {
            activeTime += amount;
            if (ship.getShield().isOff()) {SpaghettiSauce = 0;} else {SpaghettiSauce = ship.getShield().getActiveArc();}
            if (reflectSuccess && activeTime <= BUFF_DURATION) { // blend from normal -> buff color
                ship.getShield().setRingColor(PARRY_SUCCESS_RING_COLOR);
                ship.getShield().setInnerColor(PARRY_SUCCESS_CORE_COLOR);
                stats.getShieldDamageTakenMult().modifyMult(id, 1f - SHIELD_BONUS);
                stats.getMaxSpeed().modifyMult(id, 1.15f);
                ship.getEngineController().fadeToOtherColor(this, color, null, 1f, 1f);
                ship.getEngineController().extendFlame(this, 0.2f, 0.2f, 0.2f);
            } else if (!reflectSuccess && activeTime <= DEBUFF_DURATION) { // blend from normal -> debuff color
                ship.getShield().setRingColor(PARRY_FAIL_RING_COLOR);
                ship.getShield().setInnerColor(PARRY_FAIL_CORE_COLOR);
                ship.getShield().setActiveArc(SpaghettiSauce+1.4f);
                if (SpaghettiSauce >= ORIGINALCOOMSAUCE+SHIELD_ARC_BONUS*3) {
                    ship.getShield().setActiveArc(ORIGINALCOOMSAUCE+(SHIELD_ARC_BONUS*3));
                }
                stats.getShieldDamageTakenMult().modifyMult(id, 1f + SHIELD_BONUS);
            }
            if (reflectSuccess && activeTime > BUFF_DURATION) { // blend from buff color -> normal
                ship.getShield().setRingColor(initialShieldCoreColor);
                ship.getShield().setInnerColor(initialShieldCoreColor);
                stats.getShieldDamageTakenMult().unmodifyMult(id);
            } else if (!reflectSuccess && activeTime > DEBUFF_DURATION) { // blend from debuff color -> normal
                ship.getShield().setRingColor(initialShieldRingColor);
                ship.getShield().setInnerColor(initialShieldCoreColor);
                if (ship.getShield().getActiveArc() >= ORIGINALCOOMSAUCE) { 
                    ship.getShield().setActiveArc(SpaghettiSauce-1f);
                } //else {ship.getShield().setActiveArc(ORIGINALCOOMSAUCE);}
                stats.getShieldDamageTakenMult().unmodifyMult(id);
                stats.getMaxSpeed().unmodifyMult(id);
            }
        }
    }

    @Override
    public void unapply(MutableShipStatsAPI stats, String id) {
        reset = true;
    }

    @Override
    public StatusData getStatusData(int index, State state, float effectLevel) {
        if (index == 0 && state == State.OUT && reflectSuccess && activeTime < BUFF_DURATION)
            return new StatusData(poopystinky, false);
        if (index == 1 && state == State.OUT && reflectSuccess && activeTime < BUFF_DURATION)
            return new StatusData(poopystinky2 + Misc.getRoundedValueMaxOneAfterDecimal(BUFF_DURATION - activeTime) + "", false);
        if (index == 0 && state == State.OUT && !reflectSuccess && activeTime < DEBUFF_DURATION)
            return new StatusData(poopystinky3, true);
        if (index == 1 && state == State.OUT && !reflectSuccess && activeTime < DEBUFF_DURATION)
            return new StatusData(poopystinky4 + Misc.getRoundedValueMaxOneAfterDecimal(DEBUFF_DURATION - activeTime) + "", true);
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