package data.scripts.weapons;

import com.fs.starfarer.api.AnimationAPI;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.EveryFrameWeaponEffectPlugin;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.util.IntervalUtil;
import java.awt.Color;

public class eis_blinker3 implements EveryFrameWeaponEffectPlugin {
    private static final Color engagecolor = new Color(235, 120, 35, 255); 
    private static final Color recallcolor = new Color(0, 230, 35,255);
    private boolean MyClum = false;
    private boolean MyClum2 = false;
    private boolean itswitched = true;
    private int storedHashCode;
    private final IntervalUtil tracker = new IntervalUtil(0.3f, 0.5f);
    //Funniest shit you'll see... I haven't heard of FadeUtils...
    @Override
    public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {
        if (engine.isPaused()) return;
        SpriteAPI anime = weapon.getSprite();
        AnimationAPI anime2 = weapon.getAnimation();
        float MyClumGhost = anime2.getAlphaMult();
        ShipAPI ship = weapon.getShip();
        if (Global.getCombatEngine().hashCode() != storedHashCode) {
          itswitched = true;
          storedHashCode = Global.getCombatEngine().hashCode();
        }
        if (ship != null && ship.isAlive() && !ship.getFluxTracker().isOverloadedOrVenting()) {
            if (itswitched) {
                anime2.setAlphaMult(MyClumGhost-10*amount);
                if (MyClumGhost <= 0f) {
                    if (ship.isPullBackFighters()) {
                        anime.setColor(recallcolor);
                    } else if (!ship.isPullBackFighters()) {
                        anime.setColor(engagecolor);
                    }
                    itswitched = false;
                    MyClum = true;
                }
            } 
            if (MyClum) {
                anime2.setAlphaMult(MyClumGhost+10*amount);
                if (MyClumGhost >= 1f) {
                    MyClum = false;
                    MyClum2 = true;
                }
            } else if (MyClum2) {anime2.setAlphaMult(1f);}
        } else {
            itswitched = true;
            anime2.setAlphaMult(0f);
        }
        tracker.advance(amount);
        if (tracker.intervalElapsed()) {
            if ((ship.isPullBackFighters() && anime.getColor() == engagecolor) || (!ship.isPullBackFighters() && anime.getColor() == recallcolor)) {
                itswitched = true;
                MyClum = false;
                MyClum2 = false;
            }
        }
    }
}