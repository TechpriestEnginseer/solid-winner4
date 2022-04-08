package data.scripts.shipsystems.ai;

import com.fs.starfarer.api.combat.CombatAssignmentType;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.CombatFleetManagerAPI.AssignmentInfo;
import com.fs.starfarer.api.combat.MissileAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipSystemAIScript;
import com.fs.starfarer.api.combat.ShipSystemAPI;
import com.fs.starfarer.api.combat.ShipwideAIFlags;
import com.fs.starfarer.api.combat.ShipwideAIFlags.AIFlags;
import com.fs.starfarer.api.util.IntervalUtil;
import java.util.List;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.combat.AIUtils;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.lwjgl.util.vector.Vector2f;

public class eis_ironsneedeternal implements ShipSystemAIScript {
    private CombatEngineAPI engine;
    private ShipAPI ship;
    private final float EFFECT_RANGE = 315f; //300f is the effect range but we want to buff it so the AI can better predict things
    private ShipSystemAPI system;
    private ShipwideAIFlags flags;
    private final IntervalUtil tracker = new IntervalUtil(0.3f, 0.5f);
    float desire;
    //ififififififiiffififi statements holy 
    @Override
    public void advance(float amount, Vector2f missileDangerDir, Vector2f collisionDangerDir, ShipAPI target) {
        if (engine == null) {return;}
        if (engine.isPaused()) {return;}
        tracker.advance(amount);
        if (tracker.intervalElapsed()) {
            if (ship.getFluxTracker().isOverloadedOrVenting() || (system.getAmmo() == 0) || !AIUtils.canUseSystemThisFrame(ship)) {
                return;
            }
            CombatEntityAPI immediateTarget = null;
            if ((flags.getCustom(AIFlags.MANEUVER_TARGET) instanceof CombatEntityAPI)) {
                immediateTarget = (CombatEntityAPI) flags.getCustom(AIFlags.MANEUVER_TARGET);
            } else {
                immediateTarget = ship.getShipTarget();
            }
            AssignmentInfo assignment = engine.getFleetManager(ship.getOwner()).getTaskManager(ship.isAlly()).getAssignmentFor(ship);
            if (immediateTarget != null) {
                if (flags.hasFlag(AIFlags.PURSUING)) {
                    desire += 1.25f;
                }
                if (flags.hasFlag(AIFlags.HARASS_MOVE_IN)) {
                    desire += 1.5f;
                }
                if (flags.hasFlag(AIFlags.BACKING_OFF)) {
                    desire += 0.5f;
                }
                if (flags.hasFlag(AIFlags.DO_NOT_PURSUE)) {
                    desire -= 0.5f;
                }
            }
            if (flags.hasFlag(AIFlags.RUN_QUICKLY)) {
                desire += 0.75f;
            }
            if (flags.hasFlag(AIFlags.TURN_QUICKLY)) {
                desire += 0.25f;
            }
            if (flags.hasFlag(AIFlags.NEEDS_HELP) || flags.hasFlag(AIFlags.IN_CRITICAL_DPS_DANGER)) {
                desire += 3f;
            }
            if (flags.hasFlag(AIFlags.HAS_INCOMING_DAMAGE)) {
                desire += 1.5f;
            }
            if ((assignment != null) && (assignment.getType() == CombatAssignmentType.RETREAT)) {
                desire += 3f;
            }
           /* if (system.getAmmo() <= 1) {
                desire -= 0.15f;
            } else if (system.getAmmo() >= 2) {
                desire -= 0.1f;
            } // 3 why are you holding onto it? USE IT.*/
            float missileThreatLevel = 0f;
            int missileThreatAmount = 0;
            List<MissileAPI> allMissiles = CombatUtils.getMissilesWithinRange(ship.getLocation(), ship.getCollisionRadius()+ship.getMutableStats().getSystemRangeBonus().computeEffective(EFFECT_RANGE) * MathUtils.getRandomNumberInRange(0.5f,1f));
            for (MissileAPI missile : allMissiles) {
            try {if (missile.getBehaviorSpecParams().get("behavior").equals("PROXIMITY_FUSE")) {continue;}} catch (Exception sex) {}   
                if (missile.getOwner() != ship.getOwner() && !missile.isMine()) {
                    float scale = 1f;
                    switch (missile.getDamageType()) {
                        case FRAGMENTATION:
                            scale = 0.3f;
                            break;
                        case KINETIC:
                            scale = 1.7f;
                            break;
                        case HIGH_EXPLOSIVE:
                            scale = 0.7f;
                            break;
                        default:
                        case ENERGY:
                            break;
                        }
                        missileThreatLevel += missile.getDamageAmount() * scale;
                        if (missile.getDamageAmount() >= 1000f || missile.getEmpAmount() >= 500f) {
                            missileThreatAmount += 3;
                        } else {missileThreatAmount += 1;}
                    }
                }
            //engine.addFloatingText(ship.getLocation(), String.valueOf(desire), 30f, Color.WHITE, ship, 1f, 0.5f);
            if (desire >= MathUtils.getRandomNumberInRange(3f,4f) || missileThreatLevel >= MathUtils.getRandomNumberInRange(0.3f,1.2f) * ship.getHitpoints() * ((1-ship.getFluxTracker().getFluxLevel()))/2.5 || missileThreatAmount >= 2) {
                ship.useSystem();
                flags.setFlag(AIFlags.DO_NOT_BACK_OFF, 0.75f);
                desire = 0f;
            }
        }
    }

    @Override
    public void init(ShipAPI ship, ShipSystemAPI system, ShipwideAIFlags flags, CombatEngineAPI engine) {
        this.ship = ship;
        this.flags = flags;
        this.system = system;
        this.engine = engine;
    }
}