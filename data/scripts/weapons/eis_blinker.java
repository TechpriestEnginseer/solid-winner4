package data.scripts.weapons;

import com.fs.starfarer.api.AnimationAPI;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.EveryFrameWeaponEffectPlugin;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.WeaponAPI;

public class eis_blinker implements EveryFrameWeaponEffectPlugin {
    @Override
    public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {
        AnimationAPI anime = weapon.getAnimation();
        ShipAPI ship = weapon.getShip();
        weapon.getSprite().setAdditiveBlend();
        if (ship != null && ship.isAlive() && !ship.getFluxTracker().isVenting()) { 
            anime.setAlphaMult(1f);
        } else {
            anime.setAlphaMult(0f);
        }
    }
}