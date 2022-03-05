package com.fs.starfarer.api.impl.campaign.rulecmd;


/*import java.util.List;
import java.util.Map;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.comm.IntelInfoPlugin;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.intel.inspection.HegemonyInspectionIntel;
import com.fs.starfarer.api.util.Misc.Token;

public class EISKillaPacha extends BaseCommandPlugin {
	public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Token> params, Map<String, MemoryAPI> memoryMap) {
            List<IntelInfoPlugin> intels =  Global.getSector().getIntelManager().getIntel(HegemonyInspectionIntel.class);
            for (IntelInfoPlugin intel : intels) {
              //((HegemonyInspectionIntel) intel).setOutcome(HegemonyInspectionIntel.HegemonyInspectionOutcome.BRIBED);
              ((HegemonyInspectionIntel) intel).forceFail(false);
              Global.getSector().getIntelManager().addIntelToTextPanel(intel, dialog.getTextPanel());
            }
            return true;
        }
}*/