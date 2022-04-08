package data.scripts.weapons;

import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;
import org.lwjgl.util.vector.Vector2f;
import static com.fs.starfarer.api.combat.DamageType.HIGH_EXPLOSIVE;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.MissileAPI;
import com.fs.starfarer.api.combat.OnHitEffectPlugin;
import com.fs.starfarer.api.combat.ShipAPI;

// Who would just take this code from FPE's Fever.. just imagine!

public class eis_aphelion implements OnHitEffectPlugin {

    @Override
    public void onHit(DamagingProjectileAPI projectile, CombatEntityAPI target, Vector2f point, boolean shieldHit, ApplyDamageResultAPI damageResult, CombatEngineAPI engine) {
        if ((target instanceof ShipAPI && ((ShipAPI)target).isFighter()) || target instanceof MissileAPI) {
            engine.applyDamage (target, point, 100f, HIGH_EXPLOSIVE, 0f, false, false, projectile.getSource());
        }
    }
}
