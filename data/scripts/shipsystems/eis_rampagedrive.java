package data.scripts.shipsystems;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipSystemAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;

public class eis_rampagedrive extends BaseShipSystemScript {

    public static final float SPEED_BOOST = 250f;
    public static final float MASS_MULT = 3f;
    public static final float DAMAGE_MULT = 0.1f;
    public static final float RANGE = 600f;
    public static final float ROF_MULT = 0.5f;

    private static String poopystinky = Global.getSettings().getString("eis_ironshell", "eis_rampagedrive1");
    private static String poopystinky2 = Global.getSettings().getString("eis_ironshell", "eis_rampagedrive2");
    private static String poopystinky3 = Global.getSettings().getString("eis_ironshell", "eis_rampagedrive3");
    
    private CombatEngineAPI engine =  Global.getCombatEngine();
    private boolean reset = true;
    private float activeTime = 0f;
    private float jitterLevel;
    
    private Float mass = null;

    @Override
    public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {    
        ShipAPI ship = (ShipAPI) stats.getEntity();
        if (engine.isPaused() || ship == null) {
            return;
        }

        if (mass == null) {
            mass = ship.getMass();
        }
        
        if (reset) {
            reset = false;
            activeTime = 0f;
            jitterLevel = 0f;
        }

        ShipAPI target = ship.getShipTarget();
        float turnrate = ship.getMaxTurnRate()*2;

        if (state == State.OUT) {
            stats.getMaxSpeed().unmodify(id); // to slow down ship to its regular top speed while powering drive down
            ship.setMass(mass);
        } else {
            if (ship.getMass() == mass) {
                ship.setMass(mass * MASS_MULT);
            }
            stats.getMaxSpeed().modifyFlat(id, SPEED_BOOST);
            stats.getAcceleration().modifyFlat(id, SPEED_BOOST * 3);
            stats.getEmpDamageTakenMult().modifyMult(id, DAMAGE_MULT);
            stats.getArmorDamageTakenMult().modifyMult(id, DAMAGE_MULT);
            stats.getHullDamageTakenMult().modifyMult(id, DAMAGE_MULT);

            stats.getBallisticRoFMult().modifyMult(id, ROF_MULT);
            stats.getEnergyRoFMult().modifyMult(id, ROF_MULT);

            if((target != null && target.isAlive()) && ship.getSystem().isActive()){
                float facing = ship.getFacing();
                facing=MathUtils.getShortestRotation(
                        facing,
                        VectorUtils.getAngle(ship.getLocation(), target.getLocation())
                );
                if (!target.isFighter() && !target.isDrone()) {
                    ship.setAngularVelocity(Math.min(turnrate, Math.max(-turnrate, facing*5)));
                } else {ship.setAngularVelocity(Math.min(turnrate, Math.max(-turnrate, facing*2)));}
               activeTime += engine.getElapsedInLastFrame();
                if (activeTime <= 3f) {
                    jitterLevel = Math.max(1 - activeTime/1.0f, 1.0f);
                    stats.getEmpDamageTakenMult().modifyMult(id, DAMAGE_MULT);
                    stats.getArmorDamageTakenMult().modifyMult(id, DAMAGE_MULT);
                    stats.getHullDamageTakenMult().modifyMult(id, DAMAGE_MULT);
                    //ship.setJitter(this, new Color (255,140,60,65), jitterLevel, 1, 0, 5f);
                    //ship.setJitterUnder(this, new Color (255,80,30,135), jitterLevel, 10, 0f, 8f);
                }
                if (activeTime > 3f) {
                    jitterLevel = 0f;
                    stats.getEmpDamageTakenMult().unmodifyMult(id);
                    stats.getArmorDamageTakenMult().unmodifyMult(id);
                    stats.getHullDamageTakenMult().unmodifyMult(id);
                }
            }
        }
    }

    @Override
    public void unapply(MutableShipStatsAPI stats, String id) {
        reset = true;
        ShipAPI ship = (ShipAPI) stats.getEntity();
        if (ship == null) {
            return;
        }

        if (mass == null) {
            mass = ship.getMass();
        }
        if (ship.getMass() != mass) {
            ship.setMass(mass);
        }

        stats.getMaxSpeed().unmodify(id);
        stats.getAcceleration().unmodify(id);
        stats.getEmpDamageTakenMult().unmodify(id);
        stats.getHullDamageTakenMult().unmodify(id);
        stats.getArmorDamageTakenMult().unmodify(id);

        stats.getBallisticRoFMult().unmodify(id);
        stats.getEnergyRoFMult().unmodify(id);
    }

    public StatusData getStatusData(int index, State state, float effectLevel) {
        if (index == 0) {
            return new StatusData(poopystinky, false);
        } else if (index == 1) {
            return new StatusData(poopystinky2, false);
        }
        return null;
    }

    @Override
    public String getInfoText(ShipSystemAPI system, ShipAPI ship) {
        if (system.isOutOfAmmo()) return null;
        if (system.getState() != ShipSystemAPI.SystemState.IDLE) return null;

        //ShipAPI target = findTarget(ship);
        /*if (target != null && target != ship) {
            return "TARGET ENGAGED";
        }

        if ((target == null || target == ship) && ship.getShipTarget() != null) {
            return "OUT OF RANGE";
        }*/
        return poopystinky3;
    }

    /*protected ShipAPI findTarget(ShipAPI ship) {
        ShipAPI target = ship.getShipTarget();
        if(
                target!=null
                        &&
                        (!target.isDrone()||!target.isFighter())
                        &&
                        MathUtils.isWithinRange(ship, target, RANGE)
                        &&
                        target.getOwner()!=ship.getOwner()
                ){
            return target;
        } else {
            return null;
        }
    }*/
}
