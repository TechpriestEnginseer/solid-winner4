//By Nicke535, enables a ship to swap between different shell types on-the-fly
package data.scripts.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import java.awt.Color;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.lwjgl.util.vector.Vector2f;
import java.util.ArrayList;
import java.util.List;
import static com.fs.starfarer.api.util.Misc.ZERO;

public class eis_elusarcaLoader extends BaseHullMod {
    public final String T3_PRIMARY_WEAPON_NAME = "eis_astrantia";
    public final String T3_SPLIT_WEAPON_NAME = "eis_astrantia_split";
    public final float T3_PRIMARY_INACCURACY = 4f;
    public final float T3_SPLIT_INACCURACY = 25f;
    public final float T3_PRIMARY_SPEED_VARIATION = 0.03f;
    public final float T3_SPLIT_SPEED_VARIATION = 0.1f;
    public final int T3_SPLIT_COUNT = 5;
    public final Color T3_MUZZLE_COLOR = new Color(255,100,100,255);
    public final Color T3_SPLIT_COLOR = new Color(255,100,100,255);

    //Hacky, but it works: register which projectiles don't need swapping, since they were fired when we were in "burst" mode (type-3 shells)
    private List<DamagingProjectileAPI> registeredProjectiles = new ArrayList<DamagingProjectileAPI>();

    @Override
    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
        //Nothing, really
    }

    @Override
    public void advanceInCombat(ShipAPI ship, float amount) {
        CombatEngineAPI engine = Global.getCombatEngine();
        //Don't run if we are paused
        if (engine.isPaused() || ship == null || !engine.isEntityInPlay(ship)) {
            return;
        }

        //List for cleaning up dead projectiles from memory
        List<DamagingProjectileAPI> cleanList = new ArrayList<>();

        //Splits shots that should be splitting
        for (DamagingProjectileAPI proj : registeredProjectiles) {
            //Only split burst shots
            if (proj.getProjectileSpecId().contains("eis_astrantia_shot")) {
                //Calculates split range : hard-coded to match up with range properly
                WeaponAPI weapon = proj.getWeapon();
                Vector2f loc = proj.getLocation();
                float projAngle = proj.getFacing();
                float projDamage = proj.getDamageAmount();
                float splitDuration = 0.09f;//(weapon.getRange() / weapon.getProjectileSpeed()) * 0.1f;
                //Split once our duration has passed; spawn a bunch of shots
                if (proj.getElapsed() > splitDuration) {
                    //Hide the explosion with some muzzle flash
                    spawnDecoParticles(loc, projAngle, Misc.ZERO, engine, T3_SPLIT_COLOR, 1.5f, 1f);
                    engine.addSmoothParticle(loc, ZERO, 200f, 0.5f, 0.1f, T3_MUZZLE_COLOR);
                    engine.addHitParticle(loc, ZERO, 100f, 0.5f, 0.25f, T3_SPLIT_COLOR);

                    //Actually spawn shots
                    for (int i = 0; i < T3_SPLIT_COUNT; i++) {
                        //Spawns the shot, with some inaccuracy
                        float angleOffset = MathUtils.getRandomNumberInRange(-T3_SPLIT_INACCURACY / 2, T3_SPLIT_INACCURACY / 2) + MathUtils.getRandomNumberInRange(-T3_SPLIT_INACCURACY / 2, T3_SPLIT_INACCURACY / 2);
                        DamagingProjectileAPI newProj = (DamagingProjectileAPI) engine.spawnProjectile(ship, weapon, T3_SPLIT_WEAPON_NAME, loc, projAngle + angleOffset, new Vector2f(0,0));
                        //Varies the speed slightly, for a more artillery-esque look
                        float rand = MathUtils.getRandomNumberInRange(1 - T3_SPLIT_SPEED_VARIATION, 1 + T3_SPLIT_SPEED_VARIATION);
                        newProj.getVelocity().x *= rand;
                        newProj.getVelocity().y *= rand;
                        //Splits up the damage
                        newProj.setDamageAmount(projDamage / (float) T3_SPLIT_COUNT * 1.25f);
                        ProximityFuseAIAPI AI = (ProximityFuseAIAPI)(newProj.getAI());
                        AI.updateDamage();
                        //Removes the original projectile
                        engine.removeEntity(proj);
                    }
                    cleanList.add(proj);
                    continue;
                }
            }

            //If this projectile is not loaded in memory, cleaning time!
            if (!engine.isEntityInPlay(proj)) {
                cleanList.add(proj);
            }
        }

        //Runs the cleaning
        for (DamagingProjectileAPI proj : cleanList) {
            registeredProjectiles.remove(proj);
        }

        //Finds all projectiles within a a short range from our ship
        for (DamagingProjectileAPI proj : CombatUtils.getProjectilesWithinRange(ship.getLocation(), ship.getCollisionRadius()+200f)) {
            //Saves some memory, and makes the rest of the code slightly more compact, while also ignoring anything not from our ship
            if (proj.getProjectileSpecId() == null || proj.getSource() != ship || registeredProjectiles.contains(proj) || !engine.isEntityInPlay(proj)) {
                continue;
            }

            if (proj.getProjectileSpecId().contains("eis_astrantia_shot")) {
                //Stores the data all "shrapnel" needs anyway
                WeaponAPI weapon = proj.getWeapon();
                Vector2f loc = proj.getLocation();
                float projAngle = proj.getFacing();
                float projDamage = proj.getDamageAmount();
                //Muzzle flash!
                //spawnDecoParticles(loc, projAngle, ship.getVelocity(), engine, T3_MUZZLE_COLOR, 2.5f, 1f);
                //Spawns the shot, with some inaccuracy
                float angleOffset = MathUtils.getRandomNumberInRange(-T3_PRIMARY_INACCURACY / 2, T3_PRIMARY_INACCURACY / 2) + MathUtils.getRandomNumberInRange(-T3_PRIMARY_INACCURACY / 2, T3_PRIMARY_INACCURACY / 2);
                DamagingProjectileAPI newProj = (DamagingProjectileAPI)engine.spawnProjectile(ship, weapon, T3_PRIMARY_WEAPON_NAME, loc, projAngle + angleOffset, ship.getVelocity());
                //Varies the speed slightly, for a more artillery-esque look
                float rand = MathUtils.getRandomNumberInRange(1-T3_PRIMARY_SPEED_VARIATION, 1+T3_PRIMARY_SPEED_VARIATION);
                newProj.getVelocity().x *= rand;
                newProj.getVelocity().y *= rand;
                //Removes the original projectile
                engine.removeEntity(proj);
                registeredProjectiles.add(newProj);
            }
        }
    }

    //Prevents the hullmod from being put on ships
    @Override
    public boolean isApplicableToShip(ShipAPI ship) {
        return false;
    }

    //A whole bunch of descriptions, most unused for now
    public String getDescriptionParam(int index, HullSize hullSize) {
        return null;
    }

    //For the cool extra description section
    @Override
    public void addPostDescriptionSection(TooltipMakerAPI tooltip, HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
        float pad = 10f;
        tooltip.addSectionHeading("Details", Alignment.MID, pad);

        /*//Type-3 Shells
        text = tooltip.beginImageWithText("graphics/tahlan/hullmods/ammo_type3_icon.png", 36);
        text.addPara(txt("hmd_Ammo15"), 0, Color.ORANGE, txt("hmd_Ammo15"));
        text.addPara(txt("hmd_Ammo16") + "\n"
                + txt("hmd_Ammo17"), 0, Color.RED, txt("hmd_Ammo18"));
        tooltip.addImageWithText(pad);*/
    }

    //For spawning muzzle flash
    private void spawnDecoParticles(Vector2f point, float angle, Vector2f offsetVelocity, CombatEngineAPI engine, Color color, float widthMod, float sizeMod) {

        //Moves the offset backwards slightly
        float offsetDistance = 20f;
        point = MathUtils.getPointOnCircumference(point, offsetDistance, angle+180f);

        //Spawns particles in a cone
        Vector2f offsetPoint = MathUtils.getPointOnCircumference(point, 60f * sizeMod, angle);
        for (int i = 0; i < 5; i++) {
            Vector2f spawnPointStart = MathUtils.getRandomPointOnLine(point, offsetPoint);
            Vector2f spawnPoint = MathUtils.getRandomPointInCircle(spawnPointStart, 8f);

            engine.addSmokeParticle(spawnPoint, offsetVelocity, MathUtils.getRandomNumberInRange(10f, 20f) * sizeMod, MathUtils.getRandomNumberInRange(0.5f,0.8f), MathUtils.getRandomNumberInRange(0.55f, 0.7f), color);
        }
        offsetPoint = MathUtils.getPointOnCircumference(point, 60f * sizeMod, angle+(2f*widthMod));
        for (int i = 0; i < 3; i++) {
            Vector2f spawnPointStart = MathUtils.getRandomPointOnLine(point, offsetPoint);
            Vector2f spawnPoint = MathUtils.getRandomPointInCircle(spawnPointStart, 8f);

            engine.addSmokeParticle(spawnPoint, offsetVelocity, MathUtils.getRandomNumberInRange(10f, 20f) * sizeMod, MathUtils.getRandomNumberInRange(0.5f,0.8f), MathUtils.getRandomNumberInRange(0.55f, 0.7f), color);
        }
        offsetPoint = MathUtils.getPointOnCircumference(point, 60f * sizeMod, angle-(2f*widthMod));
        for (int i = 0; i < 3; i++) {
            Vector2f spawnPointStart = MathUtils.getRandomPointOnLine(point, offsetPoint);
            Vector2f spawnPoint = MathUtils.getRandomPointInCircle(spawnPointStart, 8f);

            engine.addSmokeParticle(spawnPoint, offsetVelocity, MathUtils.getRandomNumberInRange(10f, 20f) * sizeMod, MathUtils.getRandomNumberInRange(0.5f,0.8f), MathUtils.getRandomNumberInRange(0.55f, 0.7f), color);
        }
    }
}
