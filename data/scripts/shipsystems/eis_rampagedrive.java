package data.scripts.shipsystems;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CollisionClass;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamageType;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.ShipSystemAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import data.scripts.util.MagicFakeBeam;
import static data.scripts.util.MagicFakeBeam.getShipCollisionPoint;
import java.awt.Color;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.lazywizard.lazylib.CollisionUtils;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.lwjgl.util.vector.Vector2f;

public class eis_rampagedrive extends BaseShipSystemScript {
    //massy private static Map mass_mult = new HashMap();
    private static Map bugs = new HashMap();
    private static Map wide = new HashMap();
    static {
        /*mass_mult.put(HullSize.FRIGATE, 3f);
        mass_mult.put(HullSize.DESTROYER, 3f);
        mass_mult.put(HullSize.CRUISER, 2f);
        mass_mult.put(HullSize.CAPITAL_SHIP, 2f); massy*/
        bugs.put(HullSize.FRIGATE, 90f);
        bugs.put(HullSize.DESTROYER, 90f);
        bugs.put(HullSize.CRUISER, 140f);
        bugs.put(HullSize.CAPITAL_SHIP, 140f);
        wide.put(HullSize.FRIGATE, 8f);
        wide.put(HullSize.DESTROYER, 100f);
        wide.put(HullSize.CRUISER, 150f);
        wide.put(HullSize.CAPITAL_SHIP, 8f);
    }
    private static Color color = new Color(255, 135, 240, 200);
    public static final float SPEED_BOOST = 250f;
    public static final float MASS_MULT = 1.25f;
    public static final float DAMAGE_MULT = 0.1f;
    public static final float RANGE = 600f;
    public static final float ROF_MULT = 0.5f;

    private static String poopystinky = Global.getSettings().getString("eis_ironshell", "eis_rampagedrive1");
    private static String poopystinky2 = Global.getSettings().getString("eis_ironshell", "eis_rampagedrive2");
    private static String poopystinky3 = Global.getSettings().getString("eis_ironshell", "eis_rampagedrive3");
    
    private boolean reset = true;
    private float activeTime = 0f;
    private float jitterLevel;
    private boolean DidRam = false;
    
    private Float mass = null;

    @Override
    public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
        CombatEngineAPI engine =  Global.getCombatEngine();
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
            DidRam = false;
        }

        ShipAPI target = ship.getShipTarget();
        float turnrate = ship.getMaxTurnRate()*2;

        if (state == State.OUT) {
            stats.getMaxSpeed().unmodify(id); // to slow down ship to its regular top speed while powering drive down
            ship.setMass(mass);
            DidRam = false;
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
            if (!DidRam) {
                Vector2f from = ship.getLocation();
                float angle = ship.getFacing();
                Vector2f end = MathUtils.getPoint(from, (Float) bugs.get(ship.getHullSize()), angle);
                List <CombatEntityAPI> entity = CombatUtils.getEntitiesWithinRange(ship.getLocation(), (Float) bugs.get(ship.getHullSize())+30f);
                if (!entity.isEmpty()) {
                    for (CombatEntityAPI e : entity) {
                        if (e.getCollisionClass() == CollisionClass.NONE){continue;}
                        if (e.getOwner() == ship.getOwner()) {continue;}
                        Vector2f col = new Vector2f(1000000,1000000);                  
                        if (e instanceof ShipAPI ){                    
                            if(e!=ship && ((ShipAPI)e).getParentStation()!=e && (e.getCollisionClass()!=CollisionClass.NONE && e.getCollisionClass() != CollisionClass.FIGHTER) && CollisionUtils.getCollides(ship.getLocation(), end, e.getLocation(), e.getCollisionRadius())) {
                                            //&&
                                            //!(e.getCollisionClass()==CollisionClass.FIGHTER && e.getOwner()==ship.getOwner() && !((ShipAPI)e).getEngineController().isFlamedOut())               
                                ShipAPI s = (ShipAPI) e;
                                Vector2f hitPoint = getShipCollisionPoint(from, end, s, angle);
                                if (hitPoint != null ){col = hitPoint;}
                            }
                            if (col.x != 1000000 && MathUtils.getDistanceSquared(from, col) < MathUtils.getDistanceSquared(from, end)) {
                                DidRam = true;
                                MagicFakeBeam.spawnFakeBeam(engine, ship.getLocation(), (Float) bugs.get(ship.getHullSize()), ship.getFacing(), (Float) wide.get(ship.getHullSize()), 0.1f, 0.1f, 25, color, color, 2500f, DamageType.HIGH_EXPLOSIVE, 0, ship);
                                //engine.addFloatingText(ship.getLocation(), "Yamete!", 25f, Color.WHITE, ship, 1f, 0.5f);
                            }
                        }
                    }
                }
            }
            
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
            return new StatusData(poopystinky2, true);
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
