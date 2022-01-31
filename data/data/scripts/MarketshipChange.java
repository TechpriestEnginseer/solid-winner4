package data.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.Nex_MarketCMD;
import exerelin.campaign.InvasionRound;
import exerelin.campaign.SectorManager;
import exerelin.utilities.InvasionListener;
import exerelin.utilities.NexUtilsMarket;
import java.util.List;

/**
 * Handles changing of market descriptions when a market is captured.
 */
public class MarketshipChange implements InvasionListener {
        
	public static MarketshipChange currInstance;
	
	public MarketshipChange registerInstance() {
		currInstance = this;
		return this;
	}
	
	public static MarketshipChange getInstance() {
		return currInstance;
	}

	@Override
	public void reportInvadeLoot(InteractionDialogAPI dialog, MarketAPI market, 
			Nex_MarketCMD.TempDataInvasion actionData, CargoAPI cargo) {
	}
	
	@Override
	public void reportInvasionRound(InvasionRound.InvasionRoundResult result, CampaignFleetAPI fleet, 
			MarketAPI defender, float atkStr, float defStr) {
	}
	
	@Override
	public void reportInvasionFinished(CampaignFleetAPI fleet, FactionAPI attackerFaction, 
			MarketAPI market, float numRounds, boolean success) {
	}
        @Override
        public void reportMarketTransfered(MarketAPI market, FactionAPI newOwner, FactionAPI oldOwner, boolean playerInvolved, boolean isCapture, List<String> factionsToNotify, float repChangeStrength) {
            if (Factions.HEGEMONY.equals(NexUtilsMarket.getOriginalOwner(market)) && ("ironshell").equals(newOwner.getId())) {
                 SectorManager.transferMarket(market, Global.getSector().getFaction(Factions.HEGEMONY), newOwner, false, false, null, 0);
            }
            if ("ironshell".equals(NexUtilsMarket.getOriginalOwner(market)) && Factions.HEGEMONY.equals(newOwner.getId())) {
                SectorManager.transferMarket(market, Global.getSector().getFaction("ironshell"), newOwner, false, false, null, 0);
            }
        }
}