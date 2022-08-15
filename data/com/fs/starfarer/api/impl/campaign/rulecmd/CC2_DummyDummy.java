package com.fs.starfarer.api.impl.campaign.rulecmd;

/*import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.TextPanelAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.characters.ImportantPeopleAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.util.Misc;
import java.util.List;
import java.util.Map;*/

/*public class CC2_DummyDummy extends BaseCommandPlugin {
        public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
		ImportantPeopleAPI ip = Global.getSector().getImportantPeople();
                if (ip.getPerson("CC2_DummySkillHolder") != null) {
                    TextPanelAPI text = dialog.getTextPanel();
                    text.addSkillPanel(ip.getPerson("CC2_DummySkillHolder"), false); //Any officer skillssss
                    text.addSkillPanel(ip.getPerson("CC2_DummySkillHolder"), true); //Any admin skillsssss
                    text.setFontSmallInsignia();
                    text.addParagraph("These are all the skills you have available.");
                    text.setFontInsignia();
                    if (ip.getPerson("CC2_DummySkillHolder").getStats().getSkillLevel("eis_xiv") == 1) {
                        ip.getPerson("CC2_DummySkillHolder").getStats().setSkillLevel("eis_xiv", 2);
                    }
                    Global.getSector().getPlayerPerson().getStats().setSkillLevel("eis_xiv", 2);
                } else {
                    PersonAPI person = Global.getFactory().createPerson();
                    person.setId("CC2_DummySkillHolder");
                    person.getName().setFirst("Stop");
                    person.getName().setLast("Coping");
                    person.getStats().setSkillLevel("eis_xiv", 1);
                    ip.addPerson(person);
                }				
            return true;
        }
    
}*/
