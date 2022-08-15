package data.scripts.shipsystems.ai;

import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.MissileAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipSystemAIScript;
import com.fs.starfarer.api.combat.ShipSystemAPI;
import com.fs.starfarer.api.combat.ShipwideAIFlags;
import com.fs.starfarer.api.util.IntervalUtil;
import java.util.List;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.lwjgl.util.vector.Vector2f;

public class eis_myironstandseternal implements ShipSystemAIScript {
    
    private CombatEngineAPI engine;
    private ShipAPI ship;
    private final float EFFECT_RANGE = 420f;
    private ShipSystemAPI system;
    private final IntervalUtil tracker = new IntervalUtil(0.4f, 0.6f);
    
    @Override
    public void advance(float amount, Vector2f missileDangerDir, Vector2f collisionDangerDir, ShipAPI target) {
        if (engine == null) {return;}
        if (engine.isPaused()) {return;}
        tracker.advance(amount);
        if (tracker.intervalElapsed()) {
            if (!ship.getFluxTracker().isOverloadedOrVenting() || !system.isActive() || !system.isCoolingDown()) {
                float missileThreatLevel = 0f;
                int missileThreatAmount = 0;
                List<MissileAPI> allMissiles = CombatUtils.getMissilesWithinRange(ship.getLocation(), ship.getCollisionRadius()+ship.getMutableStats().getSystemRangeBonus().computeEffective(EFFECT_RANGE) * MathUtils.getRandomNumberInRange(0.5f,0.8f));
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
                                scale = 0.8f;
                                break;
                            default:
                            case ENERGY:
                                break;
                        }
                        missileThreatLevel += missile.getDamageAmount() * scale;
                        if (missile.getDamageAmount() >= 1000f || missile.getEmpAmount() >= 500f) {
                            missileThreatAmount += 2;
                        } else {missileThreatAmount += 1;}
                    }
                }
                if (missileThreatLevel >= MathUtils.getRandomNumberInRange(0.15f,1f) * ship.getHitpoints() * ((1-ship.getFluxTracker().getFluxLevel()))/2.5 || missileThreatAmount >= 4) {
                    ship.useSystem();
                }
            }
        }
    }

    @Override
    public void init(ShipAPI ship, ShipSystemAPI system, ShipwideAIFlags flags, CombatEngineAPI engine) {
        this.ship = ship;
        this.system = system;
        this.engine = engine;
    }
}