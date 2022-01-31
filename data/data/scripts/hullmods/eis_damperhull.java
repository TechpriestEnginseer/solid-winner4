package data.scripts.hullmods;

import com.fs.starfarer.api.Global;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.HullMods;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import data.scripts.util.MagicIncompatibleHullmods;
import java.util.HashSet;
import java.util.Set;

public class eis_damperhull extends BaseHullMod {
    private static Map duration = new HashMap();
    private static Map cooldown = new HashMap();
    private static Map armor = new HashMap();
    private static Set<String> BLOCKED_HULLMODS = new HashSet();
    private static Color color = new Color(255, 135, 240, 200);
    static {
        duration.put(HullSize.FRIGATE, 1f);
        duration.put(HullSize.DESTROYER, 1f);
        duration.put(HullSize.CRUISER, 1.5f);
        duration.put(HullSize.CAPITAL_SHIP, 2f);
        cooldown.put(HullSize.FRIGATE, 6f);
        cooldown.put(HullSize.DESTROYER, 9f);
        cooldown.put(HullSize.CRUISER, 12f);
        cooldown.put(HullSize.CAPITAL_SHIP, 15f);
        armor.put(HullSize.FRIGATE, 50f);
        armor.put(HullSize.DESTROYER, 100f);
        armor.put(HullSize.CRUISER, 150f);
        armor.put(HullSize.CAPITAL_SHIP, 200f);
        BLOCKED_HULLMODS.add(HullMods.HEAVYARMOR);
    }
    
    public static String aquilaIcon = "graphics/icons/hullsys/infernium_injector.png";
    public static String aquilaTitle = Global.getSettings().getString("eis_ironshell", "eis_aquilaTitle");
    public static String aquilaText1 = Global.getSettings().getString("eis_ironshell", "eis_aquilaText1");
    public static String aquilaText1b = Global.getSettings().getString("eis_ironshell", "eis_aquilaText1b");
    public static String aquilaText4b = Global.getSettings().getString("eis_ironshell", "eis_aquilaText4b");
    public static String aquilaText2 = Global.getSettings().getString("eis_ironshell", "eis_aquilaText2");
    public static String aquilaText3 = Global.getSettings().getString("eis_ironshell", "eis_aquilaText3");
    public static String aquilaText4 = Global.getSettings().getString("eis_ironshell", "eis_aquilaText4");
    public static String aquilaText4c = Global.getSettings().getString("eis_ironshell", "eis_aquilaText4c");
    public static String aquilaText5 = Global.getSettings().getString("eis_ironshell", "eis_aquilaText5");
    public static String aquilaText6 = Global.getSettings().getString("eis_ironshell", "eis_aquilaText6");
    public static String aquilaText7 = Global.getSettings().getString("eis_ironshell", "eis_aquilaText7");
    
    private static final float SPEED_BOOST_PERCENT = 75f;
    private static final float ENGAGEMENT_REDUCTION_PERCENT = 50f;
    private static final float ZERO_FLUX_LEVEL = 5f;
    private static final float VENT_RATE_BONUS = 25f;
    private static final float DAMAGE_BONUS = 10f;
    private static final float DAMAGE_BONUS2 = 10f;

    @Override
    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
        for (String tmp : BLOCKED_HULLMODS) {
            if (ship.getVariant().getHullMods().contains(tmp)) {
                MagicIncompatibleHullmods.removeHullmodWithWarning(ship.getVariant(), tmp, "eis_damperhull");
            }
        }
    }

    @Override
    public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {  
        stats.getEffectiveArmorBonus().modifyFlat(id, (float)armor.get(hullSize));
    }

    @Override
    public String getDescriptionParam(int index, HullSize hullSize) {
        return null;
    }

    @Override
    public boolean isApplicableToShip(ShipAPI ship) {
        return true;
    }

    @Override
    public void advanceInCombat(ShipAPI ship, float amount) {
        ship.getEngineController().fadeToOtherColor(this, color, null, 1f, 1f);
        //ship.getEngineController().extendFlame(this, 0.1f, 0.1f, 0.1f);
    }
    
    @Override
    public void addPostDescriptionSection(TooltipMakerAPI tooltip, ShipAPI.HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
    }
}
