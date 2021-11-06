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
    private final float EFFECT_RANGE = 400f;
    private ShipSystemAPI system;
    private final IntervalUtil tracker = new IntervalUtil(0.2f, 0.5f);
    
    @Override
    public void advance(float amount, Vector2f missileDangerDir, Vector2f collisionDangerDir, ShipAPI target) {
        if (engine == null) {return;}
        if (engine.isPaused()) {return;}
        
        if (!ship.getFluxTracker().isOverloadedOrVenting() || !system.isActive() || !system.isCoolingDown()) {
            tracker.advance(amount);
            if (tracker.intervalElapsed()) {
                float missileThreatLevel = 0f;
                List<MissileAPI> allMissiles = CombatUtils.getMissilesWithinRange(ship.getLocation(), ship.getCollisionRadius()+ship.getMutableStats().getSystemRangeBonus().computeEffective(EFFECT_RANGE) * MathUtils.getRandomNumberInRange(0.65f,0.95f));
                for (MissileAPI missile : allMissiles) {
                    if (missile.getOwner() != ship.getOwner()) {
                        float scale = 1f;
                        switch (missile.getDamageType()) {
                            case FRAGMENTATION:
                                scale = 0.25f;
                                break;
                            case KINETIC:
                                scale = 2f;
                                break;
                            case HIGH_EXPLOSIVE:
                                scale = 0.5f;
                                break;
                            default:
                            case ENERGY:
                                break;
                        }
                        missileThreatLevel += missile.getDamageAmount() * scale;
                    }
                }
                if (missileThreatLevel >= MathUtils.getRandomNumberInRange(0.3f,1.2f) * ship.getHitpoints() * ((1-ship.getFluxTracker().getFluxLevel()))/2.5) {
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