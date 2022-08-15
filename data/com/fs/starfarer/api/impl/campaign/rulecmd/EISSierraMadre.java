package com.fs.starfarer.api.impl.campaign.rulecmd;


import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.CargoStackAPI;
import java.util.List;
import java.util.Map;

import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import static com.fs.starfarer.api.impl.campaign.rulecmd.AddRemoveCommodity.addStackGainText;
import static com.fs.starfarer.api.impl.campaign.rulecmd.RemoveShip.addShipLossText;
import com.fs.starfarer.api.loading.WeaponSlotAPI;
import com.fs.starfarer.api.util.Misc.Token;

/**
 * EISSierraMadre <guilt> for Inventor Raccoon :^)
 */

public class EISSierraMadre extends BaseCommandPlugin {
	public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Token> params, Map<String, MemoryAPI> memoryMap) {
            if (dialog == null) return false;
            if (Global.getSector().getPlayerFleet() == null) return false;
            if (params.size() > 0) {
                Global.getSector().getPlayerPerson().getMemoryWithoutUpdate().set("$sotf_guilt", Global.getSector().getPlayerPerson().getMemoryWithoutUpdate().getFloat("$sotf_guilt")+params.get(0).getFloat(memoryMap));
                for (FleetMemberAPI member : Global.getSector().getPlayerFleet().getMembersWithFightersCopy()) {
                    if (member.getVariant().hasHullMod("fronsec_sierrasconcord") || member.getVariant().hasHullMod("sotf_sierrasconcord")) {
                        CargoAPI WeaponReturned = Global.getFactory().createCargo(true);
                        if (member.getVariant().getFittedWeaponSlots() != null) for (String id : member.getVariant().getFittedWeaponSlots()) {WeaponSlotAPI slot = member.getVariant().getSlot(id);if (slot.isDecorative() || slot.isBuiltIn() || slot.isHidden() ||slot.isSystemSlot() || slot.isStationModule()) continue;Global.getSector().getPlayerFleet().getCargo().addWeapons(member.getVariant().getWeaponId(id), 1);WeaponReturned.addWeapons(member.getVariant().getWeaponId(id), 1);}
                        if (member.getVariant().getFittedWings() != null) for (String wing : member.getVariant().getFittedWings()) {Global.getSector().getPlayerFleet().getCargo().addFighters(wing, 1);WeaponReturned.addFighters(wing, 1);}
                        WeaponReturned.sort();
                        for (CargoStackAPI lol : WeaponReturned.getStacksCopy()) {addStackGainText(lol, dialog.getTextPanel());}
                        Global.getSector().getPlayerFleet().getFleetData().removeFleetMember(member);
                        addShipLossText(member, dialog.getTextPanel());
                        return true;
                        
                    }
                }
            }
            for (FleetMemberAPI member : Global.getSector().getPlayerFleet().getMembersWithFightersCopy()) {
                if (member.getVariant().hasHullMod("fronsec_sierrasconcord") || member.getVariant().hasHullMod("sotf_sierrasconcord")) {
                    return true;
                }
            }
            return false;
        }
}