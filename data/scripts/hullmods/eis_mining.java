package data.scripts.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.WeaponAPI;
import java.util.Iterator;
import java.util.List;

public class eis_mining extends BaseHullMod {
//This is supposedly a... IPDAI clone somewhat.
    @Override
    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
        	List weapons = ship.getAllWeapons();
		Iterator iter = weapons.iterator();
		while (iter.hasNext()) {
                    WeaponAPI weapon = (WeaponAPI)iter.next();
                    weapon.setPD(true);
		}
    }
    
    @Override
    public String getDescriptionParam(int index, HullSize hullSize) {
	return null;
    }
}







