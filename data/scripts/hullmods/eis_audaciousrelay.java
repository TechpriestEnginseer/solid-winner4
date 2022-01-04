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
    private int storedHashCode;

    public static final float RANGE_RELAY = 1000f;
    public static final float SPEED_INCREASE_PERCENT = 25f;
    public static final float SHIELD_EFF_PERCENT = 15f;
    public static final float PPT_INCREASE = 10f;
    public static final float BUFF_REFRESH_TIME = 20f;
    
    public static final String DATA_KEY = "eis_audaciousrelay_data";
    
    private static final Color PARRY_SUCCESS_CORE_COLOR = new Color (79,187,255,70); // shield colors if buffed
    private static final Color PARRY_SUCCESS_RING_COLOR = new Color (205,235,255,175);
    private static final Color AQUILA_SUCCESS_CORE_COLOR = new Color(255, 135, 240, 200);
    private static final Color initialShieldRingColor = new Color(255,255,255,255);
    private static final Color initialShieldCoreColor = new Color(255,125,125,75);
    
    public static String relayIcon = "graphics/icons/campaign/sensor_strength.png";
    public static String relayTitle = Global.getSettings().getString("eis_ironshell", "eis_avaritiarelayTitle");
    public static String relayText1 = Global.getSettings().getString("eis_ironshell", "eis_avaritiarelayText1");
    public static String relayText2 = Global.getSettings().getString("eis_ironshell", "eis_avaritiarelayText2");
    public static String relayText3 = Global.getSettings().getString("eis_ironshell", "eis_avaritiarelayText3");
    public static String relayText4 = Global.getSettings().getString("eis_ironshell", "eis_avaritiarelayText4");
    public static String relayText5 = Global.getSettings().getString("eis_ironshell", "eis_avaritiarelayText5");
    public static String StatusText = Global.getSettings().getString("eis_ironshell", "eis_avaritiarelayStatusText");
    public static String StatusText2 = Global.getSettings().getString("eis_ironshell", "eis_avaritiarelayStatusText2");
    
    
    @Override
    public void addPostDescriptionSection(TooltipMakerAPI tooltip, ShipAPI.HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
        float HEIGHT = 50f;
        float PAD = 10f;
        Color YELLOW = new Color(241,199,0);    
        
        TooltipMakerAPI relay = tooltip.beginImageWithText(relayIcon, HEIGHT);
        relay.addPara(relayTitle, 0f, YELLOW, relayTitle);
        relay.addPara(relayText1, 0f, YELLOW, Integer.toString(Math.round(RANGE_RELAY)));
        relay.addPara(relayText2, 0f, Misc.getPositiveHighlightColor(), Math.round(SPEED_INCREASE_PERCENT)+"%");
        if (ship != null && ship.getShield() != null) {relay.addPara(relayText3, 0f, Misc.getPositiveHighlightColor(), Math.round(SHIELD_EFF_PERCENT)+"%");}
        relay.addPara(relayText4, 0f, Misc.getPositiveHighlightColor(), Integer.toString(Math.round(PPT_INCREASE)));
        relay.addPara(relayText5, 0f, YELLOW, Integer.toString(Math.round(BUFF_REFRESH_TIME)));
        tooltip.addImageWithText(PAD);        
        
    }
    
    @Override
    public void advanceInCombat(ShipAPI ship, float amount) {
        CombatEngineAPI engine = Global.getCombatEngine();
        //Crashtest and beware of hullmod saving data stuff.
        if (engine == null || engine.isPaused() || !engine.isEntityInPlay(ship) || !ship.isAlive()) {return;}
        String key = DATA_KEY + "_" + ship.getId();
        eis_audaciousrelaydata data = (eis_audaciousrelaydata) engine.getCustomData().get(key);
        if (data == null) {
            data = new eis_audaciousrelaydata();
            engine.getCustomData().put(key, data);
        }
        if (!data.runOnce) {
            data.runOnce = true;
            data.buffId = this.getClass().getName() + "_" + ship.getId();
        }
        
        if (engine.hashCode() != storedHashCode) {
          storedHashCode = engine.hashCode();
          ship.getMutableStats().getPeakCRDuration().unmodifyFlat(data.buffId);
        }
        
        //detect ship and look for mommies tummies
        if (data.activeTime <= 0f) {
            data.tracker.advance(amount);
            if (data.tracker.intervalElapsed()) {
                ShipAPI ship3 = null;
                List<ShipAPI> shipinrange = CombatUtils.getShipsWithinRange(ship.getLocation(), RANGE_RELAY);
                for (ShipAPI ship2 : shipinrange) {
                    if (ship2.getOwner() == ship.getOwner() && (ship2.getVariant().hasHullMod("eis_vengeance") || ship2.getVariant().hasHullMod("eis_perfect_vengeance"))) {
                        data.hasMommy = true;
                        ship3 = ship2;
                        break;
                    }
                }
                if (data.hasMommy) {
                    data.activeTime = 20f;
                    data.howmanytimes += 1;
                    MutableShipStatsAPI stats = ship.getMutableStats();
                    stats.getMaxSpeed().modifyMult(data.buffId, 1.25f);
                    stats.getShieldDamageTakenMult().modifyMult(data.buffId, 0.75f);
                    if (stats.getPeakCRDuration().getMult() != 1) {
                        stats.getPeakCRDuration().modifyFlat(data.buffId, data.howmanytimes*PPT_INCREASE/stats.getPeakCRDuration().getMult());
                    } else if (stats.getPeakCRDuration().getMult() == 0) {;} //do nothing cuz.. have sex
                    else {
                        stats.getPeakCRDuration().modifyFlat(data.buffId, data.howmanytimes*PPT_INCREASE);
                    }
                    if (ship3 != null) {
                        if (ship.getVariant().hasHullMod("eis_aquila")) {
                            Global.getCombatEngine().spawnEmpArcVisual(ship.getLocation(), ship, ship3.getLocation(), ship3, 12f, AQUILA_SUCCESS_CORE_COLOR, PARRY_SUCCESS_RING_COLOR);
                            stats.getDynamic().getStat(DATA_KEY).modifyFlat(DATA_KEY, 1);
                        } else {
                            Global.getCombatEngine().spawnEmpArcVisual(ship.getLocation(), ship, ship3.getLocation(), ship3, 12f, PARRY_SUCCESS_CORE_COLOR, PARRY_SUCCESS_RING_COLOR);stats.getDynamic().getStat(DATA_KEY).modifyFlat(DATA_KEY, 1);
                        }
                    }
                }
            }
        }
        if (data.activeTime > 0f) {
            //ok bye mommmmmmmy c: hold buff for 20 second.
            if (ship == Global.getCombatEngine().getPlayerShip()) {
                if (ship.getShield() != null) {
                    Global.getCombatEngine().maintainStatusForPlayerShip(data.buffId, "graphics/icons/hullsys/burn_drive.png",
                            "Vengeance Connected", "+25% "+StatusText, false);
                }
                else {Global.getCombatEngine().maintainStatusForPlayerShip(data.buffId, "graphics/icons/hullsys/burn_drive.png",
                                            "Vengeance Connected", "+25% "+StatusText2, false);
                }
                Global.getCombatEngine().maintainStatusForPlayerShip(data.buffId+"2", "graphics/icons/hullsys/burn_drive.png",
                        "Vengeance Connected", "Increased PPT " + Misc.getRoundedValue(data.activeTime), false);
            }
            if (ship.getShield() != null) {
                ship.getShield().setRingColor(PARRY_SUCCESS_RING_COLOR);
                ship.getShield().setInnerColor(PARRY_SUCCESS_CORE_COLOR);
            }
            data.activeTime -= amount;
            if (data.activeTime <= 0f) {
                data.hasMommy = false;
                MutableShipStatsAPI stats = ship.getMutableStats();
                stats.getMaxSpeed().unmodifyMult(data.buffId);
                stats.getShieldDamageTakenMult().unmodifyMult(data.buffId);
                if (ship.getShield() != null) {
                    ship.getShield().setRingColor(initialShieldRingColor);
                    ship.getShield().setInnerColor(initialShieldCoreColor);
                }
                stats.getDynamic().getStat(DATA_KEY).unmodifyFlat(DATA_KEY);
            }
        }
    }

    public static class eis_audaciousrelaydata {
        int howmanytimes = 0;
        String buffId = "";
        boolean hasMommy = false;
        boolean runOnce = false;
        float activeTime = 0f;
        IntervalUtil tracker = new IntervalUtil(1f, 1f);
    }
    
}






