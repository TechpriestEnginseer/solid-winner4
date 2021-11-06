package data.scripts.world;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.PersonImportance;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.characters.FullName;
import com.fs.starfarer.api.characters.ImportantPeopleAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.ids.Ranks;
import com.fs.starfarer.api.impl.campaign.ids.Skills;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.ids.Personalities;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.impl.campaign.ids.Voices;

public class MyLoveForIron implements EveryFrameScript {

    private boolean done = false;

    @Override
    public void advance(float amount) {
	if (!done) {
        ImportantPeopleAPI ip = Global.getSector().getImportantPeople();
        MarketAPI market = Global.getSector().getEconomy().getMarket("chitagupta");
        MarketAPI market2 = Global.getSector().getEconomy().getMarket("yami");
        if (market != null && market2 != null) {
            market.getPlanetEntity().setInteractionImage("illustrations", "is_station_illustration");
            for (SectorEntityToken linked : market.getConnectedEntities()) {
                if (!"chitagupta".equals(linked.getId())) {
                    linked.setName("Lekhani Station");
                    //linked.setInteractionImage("illustrations", "is_station_illustration");
                    linked.setCustomDescriptionId("station_chitagupta");
                }
            }
            //I did this math of upgrade time in my head, have 59 + IndustryRegularUpgradeTime
            market2.getPlanetEntity().setInteractionImage("illustrations", "is_yami_illustration");
            market.getIndustry("spaceport").startUpgrading();
            ((BaseIndustry) market.getIndustry("spaceport")).setBuildProgress(-274f);
            market.getIndustry("heavyindustry").startUpgrading();
            ((BaseIndustry) market.getIndustry("heavyindustry")).setBuildProgress(-274f);
            market.getIndustry("battlestation").startUpgrading();
            ((BaseIndustry) market.getIndustry("battlestation")).setBuildProgress(-609);
            market.getIndustry("militarybase").startUpgrading();
            ((BaseIndustry) market.getIndustry("militarybase")).setBuildProgress(-213);
            PersonAPI mommy = Global.getFactory().createPerson();
            mommy.setId("eiskimquy");
            mommy.setFaction("ironshell");
            mommy.setGender(FullName.Gender.FEMALE);
            mommy.setPostId(Ranks.POST_FACTION_LEADER);
            mommy.setRankId(Ranks.FACTION_LEADER);
            mommy.setImportance(PersonImportance.VERY_HIGH);
            mommy.getName().setFirst("Kim");
            mommy.getName().setLast("Quy");
            mommy.setPortraitSprite("graphics/portraits/eis_hegemomy.png");
            mommy.addTag("VNSector");
            //Formerly logistic officer (5 + 1)
            mommy.getStats().setSkillLevel(Skills.SPACE_OPERATIONS, 3);
            mommy.getStats().setSkillLevel(Skills.PLANETARY_OPERATIONS, 3);
            mommy.getStats().setSkillLevel(Skills.INDUSTRIAL_PLANNING, 3);
            mommy.getStats().setSkillLevel(Skills.DAMAGE_CONTROL, 1);
            mommy.getStats().setSkillLevel(Skills.RELIABILITY_ENGINEERING, 3);
            //Of course she knows how to train, she's leading an entire faction (2)
            mommy.getStats().setSkillLevel(Skills.COORDINATED_MANEUVERS, 1);
            mommy.getStats().setSkillLevel(Skills.CREW_TRAINING, 1);
            //Mandatory sensor to see the dumb player (2)
            mommy.getStats().setSkillLevel(Skills.NAVIGATION, 1);
            mommy.getStats().setSkillLevel(Skills.SENSORS, 1);
            //Distinguished Combatant during the Second AI War (6 + 6)
            mommy.getStats().setSkillLevel(Skills.HELMSMANSHIP, 3);  
            mommy.getStats().setSkillLevel(Skills.TARGET_ANALYSIS, 3);
            mommy.getStats().setSkillLevel(Skills.POINT_DEFENSE, 3);
            mommy.getStats().setSkillLevel(Skills.IMPACT_MITIGATION, 3);
            mommy.getStats().setSkillLevel(Skills.SHIELD_MODULATION, 3);
            mommy.getStats().setSkillLevel(Skills.MISSILE_SPECIALIZATION, 3);
            mommy.getStats().setSkillLevel("eis_xiv", 3);
            mommy.getStats().setLevel(15); //(24 actuality)
            mommy.getMemoryWithoutUpdate().set("$nex_preferredAdmin", true);
            mommy.getMemoryWithoutUpdate().set("$nex_preferredAdmin_factionId", "ironshell");
            //mommy.addTag(Tags.CONTACT_MILITARY);
            mommy.addTag("eis_military");
            mommy.setVoice(Voices.OFFICIAL);
            ip.addPerson(mommy);
            
            PersonAPI heartless = Global.getFactory().createPerson();
            heartless.setId("eisdarren");
            heartless.setFaction("ironshell");
            heartless.setGender(FullName.Gender.MALE);
            heartless.setPostId("CommissionerIS");
            heartless.setRankId("CommissionerIS");
            heartless.setImportance(PersonImportance.VERY_HIGH);
            heartless.getName().setFirst("Darren");
            heartless.getName().setLast("Hartley");
            heartless.setPortraitSprite("graphics/portraits/eis_dunscaith.png");
            heartless.addTag("VNSector");
            //Askonian Veterans during its Crisis (8 + 4)
            heartless.getStats().setSkillLevel(Skills.HELMSMANSHIP, 1);
            heartless.getStats().setSkillLevel(Skills.TARGET_ANALYSIS, 1);
            heartless.getStats().setSkillLevel(Skills.POINT_DEFENSE, 3);
            heartless.getStats().setSkillLevel(Skills.IMPACT_MITIGATION, 3);
            heartless.getStats().setSkillLevel(Skills.RANGED_SPECIALIZATION, 1);
            heartless.getStats().setSkillLevel(Skills.SHIELD_MODULATION, 1);
            heartless.getStats().setSkillLevel(Skills.SYSTEMS_EXPERTISE, 3);
            heartless.getStats().setSkillLevel(Skills.MISSILE_SPECIALIZATION, 3);
            // Attention! I am Head Commissioner here! You will obey or executed! (4)
            heartless.getStats().setSkillLevel(Skills.COORDINATED_MANEUVERS, 1);
            heartless.getStats().setSkillLevel(Skills.CREW_TRAINING, 1);
            heartless.getStats().setSkillLevel(Skills.OFFICER_TRAINING, 1);
            heartless.getStats().setSkillLevel(Skills.OFFICER_MANAGEMENT, 1);
            //Mandatory sensor to see the dumb player (2)
            heartless.getStats().setSkillLevel(Skills.NAVIGATION, 1);
            heartless.getStats().setSkillLevel(Skills.SENSORS, 1);
            //I will never die until my love's death has been avenged  (2 + 2)
            heartless.getStats().setSkillLevel(Skills.DAMAGE_CONTROL, 3);
            heartless.getStats().setSkillLevel(Skills.RELIABILITY_ENGINEERING, 3);
            heartless.getStats().setSkillLevel("eis_xiv", 3);
            heartless.getStats().setLevel(15); //(24 in actuality)
            heartless.setPersonality(Personalities.AGGRESSIVE);
            heartless.addTag("eis_military");
            heartless.setVoice(Voices.OFFICIAL);
            ip.addPerson(heartless);
            
            PersonAPI sweetperson = Global.getFactory().createPerson();
            sweetperson.setId("eisceleste");
            sweetperson.setFaction("ironshell");
            sweetperson.setGender(FullName.Gender.FEMALE);
            sweetperson.setPostId("ResearcherIS");
            sweetperson.setRankId("ResearcherIS");
            sweetperson.setImportance(PersonImportance.VERY_HIGH);
            sweetperson.getName().setFirst("Caeda");
            sweetperson.getName().setLast("Celeste");
            sweetperson.setPortraitSprite("graphics/portraits/eis_celeste.png");
            sweetperson.addTag("VNSector");
            //One of the last of the House of Celeste of Eventide (5)
            sweetperson.getStats().setSkillLevel(Skills.HELMSMANSHIP, 1);
            sweetperson.getStats().setSkillLevel(Skills.TARGET_ANALYSIS, 1);
            sweetperson.getStats().setSkillLevel(Skills.POINT_DEFENSE, 1);
            sweetperson.getStats().setSkillLevel(Skills.IMPACT_MITIGATION, 1);
            sweetperson.getStats().setSkillLevel(Skills.RANGED_SPECIALIZATION, 1);
            //You damn right I better be good at making missiles and shields! (4 + 3)
            sweetperson.getStats().setSkillLevel(Skills.SHIELD_MODULATION, 3);
            sweetperson.getStats().setSkillLevel(Skills.SYSTEMS_EXPERTISE, 3);
            sweetperson.getStats().setSkillLevel(Skills.MISSILE_SPECIALIZATION, 3);
            sweetperson.getStats().setSkillLevel(Skills.WEAPON_DRILLS, 1);
            //Eat Dee's Elf! (7 + 2)
            //sweetperson.getStats().setSkillLevel(Skills.WOLFPACK_TACTICS, 1); no selkie u cant even use this...
            //sweetperson.getStats().setSkillLevel(Skills.FLUX_REGULATION, 1);
            sweetperson.getStats().setSkillLevel(Skills.NAVIGATION, 1);
            sweetperson.getStats().setSkillLevel(Skills.SENSORS, 1);
            sweetperson.getStats().setSkillLevel(Skills.AUTOMATED_SHIPS, 1);
            sweetperson.getStats().setSkillLevel(Skills.GUNNERY_IMPLANTS, 3);
            sweetperson.getStats().setSkillLevel(Skills.ENERGY_WEAPON_MASTERY, 3);
            sweetperson.getStats().setSkillLevel(Skills.ELECTRONIC_WARFARE, 1);
            sweetperson.getStats().setSkillLevel(Skills.FIGHTER_UPLINK, 1);
            sweetperson.getStats().setLevel(15); //(21 in actuality)
            //sweetperson.addTag(Tags.CONTACT_TRADE);
            sweetperson.addTag(Tags.CONTACT_SCIENCE);
            sweetperson.addTag("eis_celeste");
            sweetperson.setVoice(Voices.SCIENTIST);
            ip.addPerson(sweetperson);

            PersonAPI mommy2 = Global.getSector().getFaction("ironshell").createRandomPerson(FullName.Gender.FEMALE);
            mommy2.setId("eissneed");
            mommy2.setPostId("WardenIS");
            mommy2.setRankId("WardenIS");
            mommy2.getName().setFirst("Charlotte");
            mommy2.getName().setLast("Hex");
            /*WeightedRandomPicker<String> portraitPicker = new WeightedRandomPicker<String>();
            portraitPicker.add("graphics/portraits/eis_charlotte.png", 50);
            portraitPicker.add("graphics/portraits/eis_elsa.png", 25);
            portraitPicker.add("graphics/portraits/eis_xuan.png", 25);
            mommy2.setPortraitSprite(portraitPicker.pick());*/
            mommy2.setPortraitSprite("graphics/portraits/eis_charlotte.png");
            mommy2.addTag("VNSector");
            //Forgive her she's still new... (8 + 1)
            mommy2.getStats().setSkillLevel(Skills.HELMSMANSHIP, 1);
            mommy2.getStats().setSkillLevel(Skills.TARGET_ANALYSIS, 1);
            mommy2.getStats().setSkillLevel(Skills.POINT_DEFENSE, 3);
            mommy2.getStats().setSkillLevel(Skills.IMPACT_MITIGATION, 1);
            mommy2.getStats().setSkillLevel(Skills.SHIELD_MODULATION, 1);
            mommy2.getStats().setSkillLevel(Skills.MISSILE_SPECIALIZATION, 1);
            mommy2.getStats().setSkillLevel(Skills.SYSTEMS_EXPERTISE, 1);
            mommy2.getStats().setSkillLevel(Skills.RELIABILITY_ENGINEERING, 1);
            
            //mommy2.getStats().setSkillLevel(Skills.NAVIGATION, 1);
            //mommy2.getStats().setSkillLevel(Skills.SENSORS, 1);
            
            //AMONG US (1 + 1)
            mommy2.getStats().setSkillLevel("eis_xiv", 1);
            
            mommy2.getStats().setLevel(10); //(11 in actuality)
            mommy2.setPersonality(Personalities.RECKLESS); //Takes after her mother too much...
            mommy2.addTag(Tags.CONTACT_UNDERWORLD);
            mommy2.addTag("eis_military");
            mommy2.setImportance(PersonImportance.HIGH);
            mommy2.setVoice(Voices.SOLDIER);
            
            ip.addPerson(mommy2);
            
            PersonAPI coffeemom = Global.getSector().getFaction("ironshell").createRandomPerson(FullName.Gender.FEMALE);
            coffeemom.setId("eisava");
            coffeemom.setPostId("HeadWardenIS");
            coffeemom.setRankId("HeadWardenIS");
            coffeemom.setImportance(PersonImportance.HIGH);
            coffeemom.getName().setFirst("Ava");
            coffeemom.getName().setLast("Nitia");
            coffeemom.setPortraitSprite("graphics/portraits/eis_ava.png");
            coffeemom.addTag("VNSector");
            coffeemom.getMemoryWithoutUpdate().set("$nex_preferredAdmin", true);
            coffeemom.getMemoryWithoutUpdate().set("$nex_preferredAdmin_factionId", "ironshell");
            
            //Actual Administrator (7 + 1)
            coffeemom.getStats().setSkillLevel(Skills.WEAPON_DRILLS, 1);
            coffeemom.getStats().setSkillLevel(Skills.COORDINATED_MANEUVERS, 1);
            coffeemom.getStats().setSkillLevel(Skills.CREW_TRAINING, 1);
            coffeemom.getStats().setSkillLevel(Skills.ELECTRONIC_WARFARE, 1);
            coffeemom.getStats().setSkillLevel(Skills.ENERGY_WEAPON_MASTERY, 3);
            coffeemom.getStats().setSkillLevel(Skills.SPACE_OPERATIONS, 3);
            coffeemom.getStats().setSkillLevel(Skills.PLANETARY_OPERATIONS, 3);
            //Gacha Mech idk whatever pilot (7 + 7)
            coffeemom.getStats().setSkillLevel(Skills.HELMSMANSHIP, 3);
            coffeemom.getStats().setSkillLevel(Skills.TARGET_ANALYSIS, 3);
            coffeemom.getStats().setSkillLevel(Skills.IMPACT_MITIGATION, 3);
            coffeemom.getStats().setSkillLevel(Skills.SHIELD_MODULATION, 3);
            coffeemom.getStats().setSkillLevel(Skills.MISSILE_SPECIALIZATION, 3);
            coffeemom.getStats().setSkillLevel(Skills.SYSTEMS_EXPERTISE, 3);
            coffeemom.getStats().setSkillLevel(Skills.RELIABILITY_ENGINEERING, 3);
            
            //Sad
            coffeemom.getStats().setSkillLevel(Skills.NAVIGATION, 1);
            coffeemom.getStats().setSkillLevel(Skills.SENSORS, 1);
            
            //Amongus
            coffeemom.getStats().setSkillLevel("eis_xiv", 3);
            
            coffeemom.getStats().setLevel(15);
            coffeemom.setPersonality(Personalities.AGGRESSIVE);
            //coffeemom.addTag(Tags.CONTACT_MILITARY);
            coffeemom.addTag("eis_military");
            coffeemom.setVoice(Voices.OFFICIAL);
            
            ip.addPerson(coffeemom);
            
            market.setAdmin(mommy);
            market.getCommDirectory().addPerson(mommy, 0);
            market.addPerson(mommy);
            market.getCommDirectory().addPerson(sweetperson, 2);
            market.addPerson(sweetperson);
            market.getCommDirectory().addPerson(heartless, 1);
            market.addPerson(heartless);
            market2.getCommDirectory().addPerson(coffeemom, 0);
            market2.setAdmin(coffeemom);
            market2.addPerson(mommy2);
            market2.getCommDirectory().addPerson(mommy2, 1);
            market2.addPerson(mommy2);
        }
    }
        done = true;
    }

    @Override
    public boolean isDone() {
        return done;
    }

    @Override
    public boolean runWhilePaused() {
        return true;
    }
}
