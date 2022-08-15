package data.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.RuleBasedDialog;
import com.fs.starfarer.api.impl.campaign.RuleBasedInteractionDialogPluginImpl;
import com.fs.starfarer.api.impl.campaign.rulecmd.FireAll;
import exerelin.campaign.ui.DelayedDialogScreenScript;

public class Sussybaka extends DelayedDialogScreenScript {
    @Override
    protected void showDialog() {
	if (Global.getSector().getCampaignUI().showInteractionDialog(new RuleBasedInteractionDialogPluginImpl(), Global.getSector().getPlayerFleet())) {
            FireAll.fire(null, Global.getSector().getCampaignUI().getCurrentInteractionDialog(), ((RuleBasedDialog) Global.getSector().getCampaignUI().getCurrentInteractionDialog().getPlugin()).getMemoryMap(), "EIS_Tip");
	}
        Global.getSector().removeTransientScriptsOfClass(Sussybaka.class);
    }
}