package data.scripts.weapons;

import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.OnFireEffectPlugin;
import com.fs.starfarer.api.combat.WeaponAPI;
import java.awt.Color;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;

//Author: Nia Tahl

public class eis_InfernalStarScript implements OnFireEffectPlugin {

    @Override
    public void onFire(DamagingProjectileAPI projectile, WeaponAPI weapon, CombatEngineAPI engine) {
        // Splinters
        Vector2f loc = projectile.getLocation();
        Vector2f vel = weapon.getShip().getVelocity();
        int splinterCount = MathUtils.getRandomNumberInRange(4,6);
        for (int j = 0; j < splinterCount; j++) {
            //Gets a random "offset velocity" for our projectile, so they can spread out slightly more tha with just an angle adjustment
            Vector2f randomVel = MathUtils.getRandomPointOnCircumference(null, MathUtils.getRandomNumberInRange(20f, 60f));
            randomVel.x += vel.x;
            randomVel.y += vel.y;

            engine.spawnProjectile(projectile.getSource(), projectile.getWeapon(), weapon.getId() + "_dummy", loc, projectile.getFacing(), randomVel);
        }
        // Visuals
        for (int i = 0; i<10; i++) {
            Vector2f vel2 = new Vector2f(weapon.getShip().getVelocity());
            Vector2f.add(vel2,new Vector2f(MathUtils.getRandomNumberInRange(-30f, 30f), MathUtils.getRandomNumberInRange(-30f, 30f)),vel2);
            engine.addNebulaParticle(projectile.getLocation(), vel2, MathUtils.getRandomNumberInRange(20f, 40f),
                    0.5f, 0f, 0.5f, MathUtils.getRandomNumberInRange(0.5f, 1.2f), new Color(140,120,120,180));
        }
        engine.spawnExplosion(projectile.getLocation(), new Vector2f(0f, 0f), new Color(255,180,100,10), MathUtils.getRandomNumberInRange(60f,90f), 0.1f);
    }
}
