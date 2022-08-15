package data.scripts.weapons;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.EveryFrameWeaponEffectPlugin;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ViewportAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.util.IntervalUtil;
import java.awt.Color;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;
import static org.lazywizard.lazylib.combat.AIUtils.getNearbyAllies;

import static org.lwjgl.opengl.GL11.*;

//Author: Nia Tahl (then edited by me!)
//Mainly because he disliked how there was no particle effect for this ship system, and there you go, this came out.


//Then suddenly WE NEEDED THIS WOW!!!!!!!!!!!!!!!

public class eis_VengeanceGlowScript implements EveryFrameWeaponEffectPlugin {
    private static final float[] COLOR_NORMAL = {255f / 255f, 100f / 255f, 20f / 255f};
    private static final float MAX_JITTER_DISTANCE = 0.2f;
    private static final float MAX_OPACITY = 1f;

    private static final float EFFECT_RANGE = 380f; //It is 400f, but for "player visual purposes" we reduce it by 5% See some random player theory crap.
    public static final float ROTATION_SPEED = 10f;
    public static final Color COLOR = new Color(215, 21, 16, 166);
    
    private final boolean basedonwhat = Global.getSettings().getBoolean("VengeanceSFX");
    //private boolean loaded = false;
    private float rotation = 0f;
    private float opacity = 0f;
    private SpriteAPI sprite = Global.getSettings().getSprite("misc", "sfxvengeance");

    private IntervalUtil interval = new IntervalUtil(0.05f,0.1f);
    private IntervalUtil interval2 = new IntervalUtil(1f,1f);
    
    private static final float EFFECT_RANGE2 = 950f; //It is 1000f, but for "player visual purposes" we reduce it by 5%
    public static final float ROTATION_SPEED2 = 5f;
    public static final Color COLOR2 = new Color(15, 215, 55, 166);
    private boolean wow = false;
    private float rotation2 = 0f;
    private float opacity2 = 0f;

    @Override
    public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {
        if (!basedonwhat || engine == null || !engine.isUIShowingHUD() || engine.isUIShowingDialog() || engine.getCombatUI().isShowingCommandUI()) {
            return;
        }
        
        ShipAPI ship = weapon.getShip();
        if (ship == null) {
            return;
        }
        boolean player = ship == engine.getPlayerShip();

        //--------------------
        // Revengeance section
        //--------------------

        /*if (sprite == null) {
            // Load sprite if it hasn't been loaded yet - not needed if you add it to settings.json
            if (!loaded) {
                try {
                    Global.getSettings().loadTexture(SPRITE_PATH);
                } catch (IOException ex) {
                    throw new RuntimeException("Failed to load sprite '" + SPRITE_PATH + "'!", ex);
                }

                loaded = true;
            }
            
        }*/
        
        if (ship.getOwner() == 0 && engine.getPlayerShip().getVariant().hasHullMod("eis_vengeance2") && !ship.isHulk() && !ship.isPiece() && ship.isAlive()) {
            if (engine.getPlayerShip().getMutableStats().getDynamic().getStat("eis_audaciousrelay_data").getModifiedValue() <= 4f) {
                interval2.advance(amount);
                if (interval2.intervalElapsed()) {
                    wow = false;
                    for (ShipAPI e : getNearbyAllies(engine.getPlayerShip(), 2000f)) {
                        if (e == ship) {wow = true;break;}
                    }
                }
                if (wow) {
                    opacity2 = Math.min(1f,opacity2+4f*amount);
                } else {
                    opacity2 = Math.max(0f,opacity2-2f*amount);
                }
            } else {
                opacity2 = Math.max(0f,opacity2-2f*amount);
            }
        }
        
        final Vector2f loc2 = ship.getLocation();
        final ViewportAPI view2 = Global.getCombatEngine().getViewport();
        if (view2.isNearViewport(loc2, EFFECT_RANGE2)) {
            glPushAttrib(GL_ENABLE_BIT);
            glMatrixMode(GL_PROJECTION);
            glPushMatrix();
            glViewport(0, 0, Display.getWidth(), Display.getHeight());
            glOrtho(0.0, Display.getWidth(), 0.0, Display.getHeight(), -1.0, 1.0);
            glEnable(GL_TEXTURE_2D);
            glEnable(GL_BLEND);
            float scale2 = Global.getSettings().getScreenScaleMult();
            float radius2 = ((EFFECT_RANGE2+ship.getCollisionRadius()) * 2f * scale2 / view2.getViewMult());
            sprite.setSize(radius2, radius2);
            sprite.setColor(COLOR2);
            sprite.setAdditiveBlend();
            sprite.setAlphaMult(0.4f*opacity2);
            sprite.renderAtCenter(view2.convertWorldXtoScreenX(loc2.x) * scale2, view2.convertWorldYtoScreenY(loc2.y) * scale2);
            sprite.setAngle(rotation2);
            glPopMatrix();
            glPopAttrib();
        }
        
        rotation2 += ROTATION_SPEED2 * amount;
        if (rotation2 > 360f){
            rotation2 -= 360f;
        }
        

        if (!player || ship.getSystem().isActive()  || ship.isHulk() || ship.isPiece() || !ship.isAlive()) {
            opacity = Math.max(0f,opacity-2f*amount);
        } else {
            opacity = Math.min(1f,opacity+4f*amount);
        }

        final Vector2f loc = ship.getLocation();
        final ViewportAPI view = Global.getCombatEngine().getViewport();
        if (view.isNearViewport(loc, EFFECT_RANGE)) {
            glPushAttrib(GL_ENABLE_BIT);
            glMatrixMode(GL_PROJECTION);
            glPushMatrix();
            glViewport(0, 0, Display.getWidth(), Display.getHeight());
            glOrtho(0.0, Display.getWidth(), 0.0, Display.getHeight(), -1.0, 1.0);
            glEnable(GL_TEXTURE_2D);
            glEnable(GL_BLEND);
            float scale = Global.getSettings().getScreenScaleMult();
            float adjustedRange = ship.getMutableStats().getSystemRangeBonus().computeEffective(EFFECT_RANGE);
            float radius = ((adjustedRange+ship.getCollisionRadius()) * 2f * scale / view.getViewMult());
            sprite.setSize(radius, radius);
            sprite.setColor(COLOR);
            sprite.setAdditiveBlend();
            sprite.setAlphaMult(0.4f*opacity);
            sprite.renderAtCenter(view.convertWorldXtoScreenX(loc.x) * scale, view.convertWorldYtoScreenY(loc.y) * scale);
            sprite.setAngle(rotation);
            glPopMatrix();
            glPopAttrib();
        }

        // We can just jump out here. Stops the rotation while paused and none of the other stuff needs to run while paused
        if (Global.getCombatEngine().isPaused()) {
            return;
        }

        // Spin it
        rotation += ROTATION_SPEED * amount;
        if (rotation > 360f){
            rotation -= 360f;
        }

        //----------------------
        // HEAT GLOW SECTION
        //----------------------

        float currentBrightness = ship.getSystem().getEffectLevel();

        //No glows on wrecks or in refit screen
        if ( ship.isHulk() || ship.isPiece() || !ship.isAlive() || ship.getOriginalOwner() == -1 || ship.getFluxTracker().isOverloadedOrVenting()) {
            currentBrightness = 0;
        }

        Color colorToUse = new Color(COLOR_NORMAL[0], COLOR_NORMAL[1], COLOR_NORMAL[2], currentBrightness * MAX_OPACITY);

        //Switches to the proper sprite
        if (currentBrightness > 0) {
            weapon.getAnimation().setFrame(1);
        } else {
            weapon.getAnimation().setFrame(0);
        }

        //Spawn some smoke
        if (ship.getSystem().isActive()) {
            interval.advance(amount);
            if (interval.intervalElapsed()) {
                for (int i = 0; i < 3; i++) {
                    engine.addSmokeParticle(weapon.getLocation(),
                            MathUtils.getRandomPointInCircle(new Vector2f(0f, 0f), 15f),
                            MathUtils.getRandomNumberInRange(10f, 30f),
                            1f,
                            MathUtils.getRandomNumberInRange(0.5f, 1.5f),
                            new Color(200f/255f, 200f/255f, 200f/255f, MathUtils.getRandomNumberInRange(0.4f,0.7f)*currentBrightness));
                }
            }
        }

        //And finally actually apply the color
        weapon.getSprite().setColor(colorToUse);

        //Jitter! Jitter based on our maximum jitter distance and our flux level
        if (currentBrightness > 0) {
            Vector2f randomOffset = MathUtils.getRandomPointInCircle(new Vector2f(weapon.getSprite().getWidth() / 2f, weapon.getSprite().getHeight() / 2f), currentBrightness*MAX_JITTER_DISTANCE);
            weapon.getSprite().setCenter(randomOffset.x, randomOffset.y);
        }


    }
}