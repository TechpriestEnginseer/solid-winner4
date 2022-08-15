package data.scripts.shipsystems.ai;

import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipSystemAIScript;
import com.fs.starfarer.api.combat.ShipSystemAPI;
import com.fs.starfarer.api.combat.ShipwideAIFlags;
import com.fs.starfarer.api.util.IntervalUtil;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.combat.AIUtils;
import org.lwjgl.util.vector.Vector2f;

public class eis_unbreakable implements ShipSystemAIScript {
    private CombatEngineAPI engine;
    private ShipAPI ship;
    private ShipSystemAPI system;
    private ShipwideAIFlags flags;
    private final IntervalUtil tracker = new IntervalUtil(0.6f, 1.0f);
    float desire = 0f;
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
            if (desire < 0) {desire = 0f;}
            if (ship.getHardFluxLevel() >= 0.8f || (ship.getHardFluxLevel() >= 0.33f && ship.getShield() != null && ship.getShield().isOn())) {
                desire += ship.getHardFluxLevel();
            } else {
                desire -= 0.2f;
            }
            
            if (!ship.isPullBackFighters()) {
                desire += 0.5f;
            } else {
                desire += 0.1f;
                //uhh
            }
            if (ship.getHullLevel() >= 0.8f && system.getAmmo() <= 1) {
                desire -= 0.15f;
            }
            if (desire >= MathUtils.getRandomNumberInRange(2f,3f)) {
                ship.useSystem();
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