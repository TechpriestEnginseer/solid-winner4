package data.scripts.shipsystems.ai;

import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipSystemAIScript;
import com.fs.starfarer.api.combat.ShipSystemAPI;
import com.fs.starfarer.api.combat.ShipwideAIFlags;
import com.fs.starfarer.api.combat.ShipwideAIFlags.AIFlags;
import com.fs.starfarer.api.util.IntervalUtil;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.combat.AIUtils;
import org.lwjgl.util.vector.Vector2f;

public class eis_ironrampage implements ShipSystemAIScript {
    private ShipAPI ship;
    private CombatEngineAPI engine;
    private ShipwideAIFlags flags;
    private ShipSystemAPI system;
    private IntervalUtil tracker = new IntervalUtil(0.6f, 1f);

    @Override
    public void advance(float amount, Vector2f missileDangerDir, Vector2f collisionDangerDir, ShipAPI target) {
        if (!engine.isPaused()) {
            if (!system.isActive()) {
                tracker.advance(amount);
                if (tracker.intervalElapsed()) {
                    if (target == null) {
                        return;
                    }

                    if (ship.getShipTarget() == null) {
                        ship.setShipTarget(target);
                        return;
                    }

                    if (!target.isAlive()) {
                        return;
                    }

                    if (target.isFighter() || target.isDrone() || target.isStation() || target.isStationModule() || target.getEngineController().isFlamedOut()) {
                        return;
                    }

                    if (!AIUtils.canUseSystemThisFrame(ship)) {
                        return;
                    }
                    
                    if (MathUtils.isWithinRange(ship, target, 600f)) {
                        ship.useSystem(); //If you're still close to the damn ship. WHO CARES IF YOU'RE HIGH FLUX. GLORY TO THE FIRST MAN TO DIE, CHARGE!
                    }

                    if (ship.isRetreating() || ship.getFluxTracker().getFluxLevel() > MathUtils.getRandomNumberInRange(0.4f, 1.0f)) {
                        return;
                    }
                    
                    if (flags.hasFlag(AIFlags.BACKING_OFF) && ship.getFluxTracker().getFluxLevel() >= MathUtils.getRandomNumberInRange(0.8f, 1.0f)) {
                        return;
                    }

                    if (flags.hasFlag(AIFlags.MANEUVER_TARGET) || flags.hasFlag(AIFlags.PURSUING) || flags.hasFlag(AIFlags.HARASS_MOVE_IN)) {
                        ship.useSystem();
                    }
                }
            }

        }
    }
    
    public void init(ShipAPI ship, ShipSystemAPI system, ShipwideAIFlags flags, CombatEngineAPI engine) {
        this.ship = ship;
        this.flags = flags;
        this.engine = engine;
        this.system = system;
    }
}
