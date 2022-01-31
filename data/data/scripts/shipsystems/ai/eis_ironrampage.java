package data.scripts.shipsystems.ai;

import com.fs.starfarer.api.combat.CollisionClass;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipSystemAIScript;
import com.fs.starfarer.api.combat.ShipSystemAPI;
import com.fs.starfarer.api.combat.ShipwideAIFlags;
import com.fs.starfarer.api.combat.ShipwideAIFlags.AIFlags;
import com.fs.starfarer.api.util.IntervalUtil;
import java.util.List;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
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
            tracker.advance(amount);
            if (AIUtils.canUseSystemThisFrame(ship)) {
                if (tracker.intervalElapsed()) {
                    if (target == null) {
                        return;
                    }

                    if (ship.getShipTarget() == null) {
                        ship.setShipTarget(target);
                        return;
                    }

                    if (!target.isAlive() || target.isAlly()) {
                        return;
                    }
                    //Vector2f end = MathUtils.getPoint(ship.getLocation(), 600f, ship.getFacing());
                    List<ShipAPI> entity = AIUtils.getNearbyAllies(ship, 500f);
                    if (!entity.isEmpty()){
                        for (ShipAPI e : entity) {                                
                            if(e.getCollisionClass()!=CollisionClass.NONE && e.getCollisionClass() != CollisionClass.FIGHTER) {
                                //if (getShipCollisionPoint(ship.getLocation(), end, s, ship.getFacing()) != null) {
                                    if (Math.abs(MathUtils.getShortestRotation(VectorUtils.getAngle(ship.getLocation(), e.getLocation()), ship.getFacing())) <= 40f) {
                                        //engine.addFloatingText(ship.getLocation(), "Nya get out!!!", 30f, Color.WHITE, ship, 1f, 0.5f);
                                        return;
                                    }
                                //}
                            }
                        }
                    }
                    if (target.isFighter() || target.isDrone() || target.isStation() || target.isStationModule() || target.getEngineController().isFlamedOut() || ship.isRetreating()) {
                        return;
                    }
                    
                    if (MathUtils.isWithinRange(ship, target, 650f)) {
                        ship.useSystem(); //If you're still close to the damn ship. WHO CARES IF YOU'RE HIGH FLUX. GLORY TO THE FIRST MAN TO DIE, CHARGE!
                        flags.setFlag(AIFlags.DO_NOT_BACK_OFF, 2.5f);
                    }
                    
                    if (MathUtils.isWithinRange(ship, target, 2000f) && ship.getSystem().getAmmo() < 2) {
                        return; //wtf ur TOO FAR GO AWAY JUST BACK OFF
                    }

                    /*if (ship.getHardFluxLevel()-(ship.getHullLevel()*0.2) > ship.getSystem().getAmmo()*MathUtils.getRandomNumberInRange(0.5f, 1f)) {
                        return;
                    }*/
                    
                    if (flags.hasFlag(AIFlags.BACKING_OFF) && ship.getHardFluxLevel() >= ship.getSystem().getAmmo()*MathUtils.getRandomNumberInRange(0.85f, 1.0f)) {
                        return;
                    }

                    if (flags.hasFlag(AIFlags.PURSUING) || flags.hasFlag(AIFlags.HARASS_MOVE_IN)) {
                        ship.useSystem();
                        flags.setFlag(AIFlags.DO_NOT_BACK_OFF, 2.5f);
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