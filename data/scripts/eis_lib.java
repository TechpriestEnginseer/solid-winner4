package data.scripts;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.WeaponAPI.WeaponType;
import com.fs.starfarer.api.fleet.FleetMemberAPI;

public class eis_lib {
    
    public static float getDamageTypeMult(ShipAPI source, CombatEntityAPI target) {
        if (source == null || target == null) return 1f;
        float damageTypeMult = 1f;
        if (target instanceof ShipAPI) {
            switch (((ShipAPI) target).getHullSize()) {
                case CAPITAL_SHIP:
                    damageTypeMult *= source.getMutableStats().getDamageToCapital().getModifiedValue();
                    break;
                case CRUISER:
                    damageTypeMult *= source.getMutableStats().getDamageToCruisers().getModifiedValue();
                    break;
                case DESTROYER:
                    damageTypeMult *= source.getMutableStats().getDamageToDestroyers().getModifiedValue();
                    break;
                case FRIGATE:
                    damageTypeMult *= source.getMutableStats().getDamageToFrigates().getModifiedValue();
                    break;
                case FIGHTER:
                    damageTypeMult *= source.getMutableStats().getDamageToFighters().getModifiedValue();
                    break;
            }
        }

        return damageTypeMult;
    }
    
    public static float getDamageTypeMult(ShipAPI source, CombatEntityAPI target, WeaponType sneed, Boolean feed) {
        if (source == null || target == null) return 1f;
        float damageTypeMult = 1f;
        if (target instanceof ShipAPI) {
            switch (((ShipAPI) target).getHullSize()) {
                case CAPITAL_SHIP:
                    damageTypeMult *= source.getMutableStats().getDamageToCapital().getModifiedValue();
                    break;
                case CRUISER:
                    damageTypeMult *= source.getMutableStats().getDamageToCruisers().getModifiedValue();
                    break;
                case DESTROYER:
                    damageTypeMult *= source.getMutableStats().getDamageToDestroyers().getModifiedValue();
                    break;
                case FRIGATE:
                    damageTypeMult *= source.getMutableStats().getDamageToFrigates().getModifiedValue();
                    break;
                case FIGHTER:
                    damageTypeMult *= source.getMutableStats().getDamageToFighters().getModifiedValue();
                    break;
            }
        }
        switch(sneed) {
            case ENERGY:
                damageTypeMult *= source.getMutableStats().getEnergyWeaponDamageMult().getModifiedValue();
                break;
            case BALLISTIC:
                damageTypeMult *= source.getMutableStats().getBallisticWeaponDamageMult().getModifiedValue();
                break;
            case MISSILE:
                damageTypeMult *= source.getMutableStats().getMissileDamageTakenMult().getModifiedValue();
                break;
        }
        if (feed) {damageTypeMult *= source.getMutableStats().getBeamWeaponDamageMult().getModifiedValue();}

        return damageTypeMult;
    }
    
    public static Integer PlayerHasHowManySmods(int maxSmods) {
        int howmanysmods = 0;
        if (Global.getSector().getPlayerFleet() != null) {
            for (FleetMemberAPI member : Global.getSector().getPlayerFleet().getMembersWithFightersCopy()) {
                if (member.getVariant().getSMods().size() > howmanysmods) {
                    howmanysmods = member.getVariant().getSMods().size();
                    if (howmanysmods > maxSmods) {howmanysmods=maxSmods;break;}
                }
            }
        }
        if (howmanysmods == 0) return null;
        return howmanysmods;
    }
}