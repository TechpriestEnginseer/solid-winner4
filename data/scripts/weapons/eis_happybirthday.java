package data.scripts.weapons;

import com.fs.starfarer.api.combat.BaseCombatLayeredRenderingPlugin;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.EveryFrameWeaponEffectPlugin;
import com.fs.starfarer.api.combat.OnFireEffectPlugin;
import com.fs.starfarer.api.combat.WeaponAPI;

public class eis_happybirthday extends BaseCombatLayeredRenderingPlugin implements OnFireEffectPlugin, EveryFrameWeaponEffectPlugin {
   
    
    @Override
    public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {
    }
    
    @Override
    public void onFire(DamagingProjectileAPI projectile, WeaponAPI weapon, CombatEngineAPI engine) {
        //weapon.getShip().setHitpoints(1f);
        //Global.getCombatEngine().applyDamage(weapon.getShip(), weapon.getShip().getLocation(), 500_000, DamageType.OTHER, 500_000, true, false, null);
        engine.removeEntity(weapon.getShip());
    }
}