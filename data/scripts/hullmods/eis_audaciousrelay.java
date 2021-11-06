package data.scripts.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.api.util.Misc;
import java.awt.Color;
import java.util.List;
import org.lazywizard.lazylib.combat.CombatUtils;

public class eis_audaciousrelay extends BaseHullMod {
    
    private float activeTime = 0f;
    private int storedHashCode;
    private int howmanytimes;
    private ShipAPI ship3;
    private CombatEngineAPI engine =  Global.getCombatEngine();

    public static final float RANGE_RELAY = 1000f;
    public static final float SPEED_INCREASE_PERCENT = 15f;
    public static final float SHIELD_EFF_PERCENT = 15f;
    public static final float PPT_INCREASE = 10f;
    public static final float BUFF_REFRESH_TIME = 20f;
    
    private static final Color PARRY_SUCCESS_CORE_COLOR = new Color (79,187,255,70); // shield colors if buffed
    private static final Color PARRY_SUCCESS_RING_COLOR = new Color (205,235,255,175);
    private static final Color AQUILA_SUCCESS_CORE_COLOR = new Color(255, 135, 240, 200);
    private Color initialShieldRingColor;
    private Color initialShieldCoreColor;
    
    public static String relayIcon = "graphics/icons/campaign/sensor_strength.png";
    public static String relayTitle = Global.getSettings().getString("eis_ironshell", "eis_avaritiarelayTitle");
    public static String relayText1 = Global.getSettings().getString("eis_ironshell", "eis_avaritiarelayText1");
    public static String relayText2 = Global.getSettings().getString("eis_ironshell", "eis_avaritiarelayText2");
    public static String relayText3 = Global.getSettings().getString("eis_ironshell", "eis_avaritiarelayText3");
    public static String relayText4 = Global.getSettings().getString("eis_ironshell", "eis_avaritiarelayText4");
    public static String relayText5 = Global.getSettings().getString("eis_ironshell", "eis_avaritiarelayText5");
    
    private final IntervalUtil tracker = new IntervalUtil(1.0f, 1.0f);
    
    @Override
    public void addPostDescriptionSection(TooltipMakerAPI tooltip, ShipAPI.HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
        float HEIGHT = 50f;
        float PAD = 10f;
        Color YELLOW = new Color(241,199,0);    
        
        TooltipMakerAPI relay = tooltip.beginImageWithText(relayIcon, HEIGHT);
        relay.addPara(relayTitle, 0f, YELLOW, relayTitle);
        relay.addPara(relayText1, 0f, YELLOW, Integer.toString(Math.round(RANGE_RELAY)));
        relay.addPara(relayText2, 0f, Misc.getPositiveHighlightColor(), Math.round(SPEED_INCREASE_PERCENT)+"%");
        relay.addPara(relayText3, 0f, Misc.getPositiveHighlightColor(), Math.round(SHIELD_EFF_PERCENT)+"%");
        relay.addPara(relayText4, 0f, Misc.getPositiveHighlightColor(), Integer.toString(Math.round(PPT_INCREASE)));
        relay.addPara(relayText5, 0f, YELLOW, Integer.toString(Math.round(BUFF_REFRESH_TIME)));
        tooltip.addImageWithText(PAD);        
        
    }
    
    @Override
    public void advanceInCombat(ShipAPI ship, float amount) {
        //Crashtest and beware of hullmod saving data stuff.
        if (engine == null || engine.isPaused()) {return;}
        if (engine.hashCode() != storedHashCode) {
          activeTime = 0f;
          howmanytimes = 0;
          storedHashCode = Global.getCombatEngine().hashCode();
          ship.getMutableStats().getPeakCRDuration().unmodifyFlat("eis_audaciousrelay");
          initialShieldCoreColor = ship.getShield().getInnerColor();
          initialShieldRingColor = ship.getShield().getRingColor();
        }
        boolean hasMommy = false;
        //detect ship and look for mommies tummies
        if (activeTime <= 0f) {
            tracker.advance(amount);
            if (tracker.intervalElapsed()) {
                List<ShipAPI> shipinrange = CombatUtils.getShipsWithinRange(ship.getLocation(), RANGE_RELAY);
                for (ShipAPI ship2 : shipinrange) {
                    if (ship2.getOwner() == ship.getOwner() && (ship2.getVariant().getHullMods().contains("eis_vengeance") || ship2.getVariant().getHullMods().contains("eis_perfect_vengeance"))) {
                        hasMommy = true;
                        ship3 = ship2;
                        break;
                    }
                }
                if (hasMommy) {
                    activeTime = 20f;
                    howmanytimes += 1;
                    MutableShipStatsAPI stats = ship.getMutableStats();
                    stats.getMaxSpeed().modifyMult("eis_audaciousrelay", 1.15f);
                    stats.getShieldDamageTakenMult().modifyMult("eis_audaciousrelay", 0.85f);
                    if (stats.getPeakCRDuration().getMult() != 1) {
                        stats.getPeakCRDuration().modifyFlat("eis_audaciousrelay", howmanytimes*PPT_INCREASE/stats.getPeakCRDuration().getMult());
                    } else if (stats.getPeakCRDuration().getMult() == 0) {;} //do nothing cuz.. have sex
                    else {
                        stats.getPeakCRDuration().modifyFlat("eis_audaciousrelay", howmanytimes*PPT_INCREASE);
                    }
                    if (ship3 != null) if (ship.getVariant().hasHullMod("eis_aquila"))
                        Global.getCombatEngine().spawnEmpArcVisual(ship.getLocation(), ship, ship3.getLocation(), ship3, 12f, AQUILA_SUCCESS_CORE_COLOR, PARRY_SUCCESS_RING_COLOR);
                    else Global.getCombatEngine().spawnEmpArcVisual(ship.getLocation(), ship, ship3.getLocation(), ship3, 12f, PARRY_SUCCESS_CORE_COLOR, PARRY_SUCCESS_RING_COLOR);
                }
            }
        }
        if (activeTime > 0f) {
            //ok bye mommmmmmmy c: hold buff for 20 second.
            if (ship == Global.getCombatEngine().getPlayerShip()) {
                Global.getCombatEngine().maintainStatusForPlayerShip("eis_audaciousrelay", "graphics/icons/hullsys/burn_drive.png",
                        "Vengeance Connected", "+15% Speed/Shield Efficiency", false);
                Global.getCombatEngine().maintainStatusForPlayerShip("eis_audaciousrelay2", "graphics/icons/hullsys/burn_drive.png",
                        "Vengeance Connected", "Increased PPT " + Misc.getRoundedValue(activeTime), false);
            }
            ship.getShield().setRingColor(PARRY_SUCCESS_RING_COLOR);
            ship.getShield().setInnerColor(PARRY_SUCCESS_CORE_COLOR);
            activeTime -= amount;
            if (activeTime <= 0f) {
                MutableShipStatsAPI stats = ship.getMutableStats();
                stats.getMaxSpeed().unmodifyMult("eis_audaciousrelay");
                stats.getShieldDamageTakenMult().unmodifyMult("eis_audaciousrelay");
                ship.getShield().setRingColor(initialShieldRingColor);
                ship.getShield().setInnerColor(initialShieldCoreColor);
            }
        }
    }
}






