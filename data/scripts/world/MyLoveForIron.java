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
        MarketAPI market = Global.getSector().getEconomy().getMarket("eis_chitagupta");
        MarketAPI market2 = Global.getSector().getEconomy().getMarket("eis_yami");
        if (market != null && "ironshell".equals(market.getFactionId()) && market2 != null && "ironshell".equals(market.getFactionId())) {
            market.getPlanetEntity().setInteractionImage("illustrations", "is_station_illustration");
            for (SectorEntityToken linked : market.getConnectedEntities()) {
                if (!"eis_chitagupta".equals(linked.getId())) {
                    linked.setName(Global.getSettings().getString("eis_ironshell", "eis_lekhanistation"));
                    //linked.setInteractionImage("illustrations", "is_station_illustration");
                    linked.setCustomDescriptionId("station_chitagupta");
                }
            }
            //I did this math of upgrade time in my head, have 59 + IndustryRegularUpgradeTime
            market2.getPlanetEntity().setInteractionImage("illustrations", "is_yami_illustration");
            if (market.getIndustry("spaceport") != null) {market.getIndustry("spaceport").startUpgrading();
            ((BaseIndustry) market.getIndustry("spaceport")).setBuildProgress(-274f);}
            if (market.getIndustry("heavyindustry") != null) {market.getIndustry("heavyindustry").startUpgrading();
            ((BaseIndustry) market.getIndustry("heavyindustry")).setBuildProgress(-274f);}
            if (market.getIndustry("battlestation") != null) {market.getIndustry("battlestation").startUpgrading();
            ((BaseIndustry) market.getIndustry("battlestation")).setBuildProgress(-609);}
            if (market.getIndustry("militarybase") != null) {market.getIndustry("militarybase").startUpgrading();
            ((BaseIndustry) market.getIndustry("militarybase")).setBuildProgress(-213);}
            //depreciated
            //mommy.getStats().setSkillLevel(Skills.SPACE_OPERATIONS, 3);
            //mommy.getStats().setSkillLevel(Skills.PLANETARY_OPERATIONS, 3);
            //mommy.getStats().setSkillLevel(Skills.RELIABILITY_ENGINEERING, 3);
            //heartless.getStats().setSkillLevel(Skills.RELIABILITY_ENGINEERING, 3);
            //heartless.getStats().setSkillLevel(Skills.RANGED_SPECIALIZATION, 1);
            //sweetperson.getStats().setSkillLevel(Skills.RANGED_SPECIALIZATION, 1);
            //sweetperson.getStats().setSkillLevel(Skills.WEAPON_DRILLS, 1);
            //sweetperson.getStats().setSkillLevel(Skills.WOLFPACK_TACTICS, 1); no selkie u cant even use this...
            //sweetperson.getStats().setSkillLevel(Skills.FLUX_REGULATION, 1);
            //coffeemom.getStats().setSkillLevel(Skills.RELIABILITY_ENGINEERING, 3);
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
            //Administrator
            mommy.getStats().setSkillLevel(Skills.INDUSTRIAL_PLANNING, 1);
            //Admiral (6)
            mommy.getStats().setSkillLevel(Skills.COORDINATED_MANEUVERS, 1);
            mommy.getStats().setSkillLevel(Skills.CREW_TRAINING, 1);
            mommy.getStats().setSkillLevel(Skills.OFFICER_TRAINING, 1);
            mommy.getStats().setSkillLevel(Skills.SUPPORT_DOCTRINE, 1);
            mommy.getStats().setSkillLevel(Skills.TACTICAL_DRILLS, 1);
            mommy.getStats().setSkillLevel(Skills.OFFICER_MANAGEMENT, 1);
            //Distinguished Combatant during the Second AI War (8 + 8)
            mommy.getStats().setSkillLevel(Skills.COMBAT_ENDURANCE, 2);
            mommy.getStats().setSkillLevel(Skills.MISSILE_SPECIALIZATION, 2);
            mommy.getStats().setSkillLevel(Skills.TARGET_ANALYSIS, 2);
            mommy.getStats().setSkillLevel(Skills.FIELD_MODULATION, 2);
            mommy.getStats().setSkillLevel(Skills.IMPACT_MITIGATION, 2);
            mommy.getStats().setSkillLevel(Skills.HELMSMANSHIP, 2);
            mommy.getStats().setSkillLevel(Skills.DAMAGE_CONTROL, 2);
            mommy.getStats().setSkillLevel(Skills.POINT_DEFENSE, 2);
            if (Global.getSettings().getSkillSpec("eis_xiv") == null) {throw new RuntimeException(Global.getSettings().getString("eis_ironshell", "EISLoveForIron"));}
            //Amongus + Sad
            mommy.getStats().setSkillLevel("eis_xiv", 2);
            mommy.getStats().setSkillLevel(Skills.HULL_RESTORATION, 1);
            mommy.getStats().setSkillLevel(Skills.NAVIGATION, 1);
            mommy.getStats().setSkillLevel(Skills.SENSORS, 1);
            mommy.getStats().setSkillLevel(Skills.CONTAINMENT_PROCEDURES, 1);
            mommy.getStats().setLevel(9);
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
            //Askonian Veterans during its Crisis (7 + 5)
            heartless.getStats().setSkillLevel(Skills.HELMSMANSHIP, 2);
            heartless.getStats().setSkillLevel(Skills.TARGET_ANALYSIS, 1);
            heartless.getStats().setSkillLevel(Skills.POLARIZED_ARMOR, 1);
            heartless.getStats().setSkillLevel(Skills.IMPACT_MITIGATION, 2);
            heartless.getStats().setSkillLevel(Skills.FIELD_MODULATION, 2);
            heartless.getStats().setSkillLevel(Skills.COMBAT_ENDURANCE, 2);
            heartless.getStats().setSkillLevel(Skills.BALLISTIC_MASTERY, 2);
            // Attention! I am Head Commissioner here! You will obey or executed! (5)
            heartless.getStats().setSkillLevel(Skills.COORDINATED_MANEUVERS, 1);
            heartless.getStats().setSkillLevel(Skills.CREW_TRAINING, 1);
            heartless.getStats().setSkillLevel(Skills.OFFICER_TRAINING, 1);
            heartless.getStats().setSkillLevel(Skills.TACTICAL_DRILLS, 1);
            heartless.getStats().setSkillLevel(Skills.SUPPORT_DOCTRINE, 1);
            //Sad
            heartless.getStats().setSkillLevel(Skills.HULL_RESTORATION, 1);
            heartless.getStats().setSkillLevel(Skills.SENSORS, 1);
            heartless.getStats().setSkillLevel(Skills.CONTAINMENT_PROCEDURES, 1);
            //I will never die until my love's death has been avenged
            heartless.getStats().setSkillLevel("eis_xiv", 2);
            heartless.getStats().setLevel(8); //(20 in actuality)
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
            //Eat Dee's Elf! (7 + 5) I am the last for my House Celeste of Eventide
            sweetperson.getStats().setSkillLevel(Skills.GUNNERY_IMPLANTS, 2);
            sweetperson.getStats().setSkillLevel(Skills.POLARIZED_ARMOR, 2);
            sweetperson.getStats().setSkillLevel(Skills.HELMSMANSHIP, 2);
            sweetperson.getStats().setSkillLevel(Skills.MISSILE_SPECIALIZATION, 2);
            sweetperson.getStats().setSkillLevel(Skills.FIELD_MODULATION, 2);
            sweetperson.getStats().setSkillLevel(Skills.COMBAT_ENDURANCE, 1);
            sweetperson.getStats().setSkillLevel(Skills.SYSTEMS_EXPERTISE, 1);
            //Admiral (4)
            sweetperson.getStats().setSkillLevel(Skills.CREW_TRAINING, 1);
            sweetperson.getStats().setSkillLevel(Skills.FLUX_REGULATION, 1);
            sweetperson.getStats().setSkillLevel(Skills.CYBERNETIC_AUGMENTATION, 1);
            sweetperson.getStats().setSkillLevel(Skills.ELECTRONIC_WARFARE, 1);
            //Sad
            sweetperson.getStats().setSkillLevel(Skills.HULL_RESTORATION, 1);
            sweetperson.getStats().setSkillLevel(Skills.NAVIGATION, 1);
            sweetperson.getStats().setSkillLevel(Skills.SENSORS, 1);
            sweetperson.getStats().setSkillLevel(Skills.CONTAINMENT_PROCEDURES, 1);
            //Amongus
            sweetperson.getStats().setSkillLevel("eis_xiv", 1);
            sweetperson.getStats().setLevel(8);
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
            mommy2.setPortraitSprite("graphics/portraits/eis_charlotte.png");
            mommy2.addTag("VNSector");
            //Forgive her she's still new... (7 + 1)
            mommy2.getStats().setSkillLevel(Skills.HELMSMANSHIP, 1); //3
            mommy2.getStats().setSkillLevel(Skills.COMBAT_ENDURANCE, 1); //3
            mommy2.getStats().setSkillLevel(Skills.ORDNANCE_EXPERTISE, 2);
            mommy2.getStats().setSkillLevel(Skills.BALLISTIC_MASTERY, 1);
            mommy2.getStats().setSkillLevel(Skills.FIELD_MODULATION, 1); //3
            mommy2.getStats().setSkillLevel(Skills.IMPACT_MITIGATION, 1); //3
            mommy2.getStats().setSkillLevel(Skills.TARGET_ANALYSIS, 1);
            
            //AMONG US (0)
            mommy2.getStats().setSkillLevel("eis_xiv", 1);
            mommy2.getStats().setLevel(7);
            mommy2.setPersonality(Personalities.RECKLESS); //Takes after her mother too much...
            mommy2.addTag(Tags.CONTACT_UNDERWORLD);
            mommy2.addTag("eis_military");
            mommy2.setImportance(PersonImportance.MEDIUM);
            mommy2.setVoice(Voices.SOLDIER);
            
            ip.addPerson(mommy2);
            
            PersonAPI coffeemom = Global.getSector().getFaction("ironshell").createRandomPerson(FullName.Gender.FEMALE);
            coffeemom.setId("eisava");
            coffeemom.setPostId("HeadWardenIS");
            coffeemom.setRankId("HeadWardenIS");
            coffeemom.setImportance(PersonImportance.VERY_HIGH);
            coffeemom.getName().setFirst("Ava");
            coffeemom.getName().setLast("Nitia");
            coffeemom.setPortraitSprite("graphics/portraits/eis_ava.png");
            coffeemom.addTag("VNSector");
            coffeemom.getMemoryWithoutUpdate().set("$nex_preferredAdmin", true);
            coffeemom.getMemoryWithoutUpdate().set("$nex_preferredAdmin_factionId", "ironshell");
            //Gacha whatever pilot (7 + 5 elite)
            coffeemom.getStats().setSkillLevel(Skills.HELMSMANSHIP, 1);
            coffeemom.getStats().setSkillLevel(Skills.GUNNERY_IMPLANTS, 1);
            coffeemom.getStats().setSkillLevel(Skills.IMPACT_MITIGATION, 2);
            coffeemom.getStats().setSkillLevel(Skills.FIELD_MODULATION, 2);
            coffeemom.getStats().setSkillLevel(Skills.SYSTEMS_EXPERTISE, 2);
            coffeemom.getStats().setSkillLevel(Skills.POLARIZED_ARMOR, 2);
            coffeemom.getStats().setSkillLevel(Skills.COMBAT_ENDURANCE, 2);
            //Admiral (4)
            coffeemom.getStats().setSkillLevel(Skills.CREW_TRAINING, 1);
            coffeemom.getStats().setSkillLevel(Skills.FLUX_REGULATION, 1);
            coffeemom.getStats().setSkillLevel(Skills.SUPPORT_DOCTRINE, 1);
            coffeemom.getStats().setSkillLevel(Skills.OFFICER_TRAINING, 1);
            //Administrator (1)
            coffeemom.getStats().setSkillLevel(Skills.INDUSTRIAL_PLANNING, 1);
            //Sad (3)
            coffeemom.getStats().setSkillLevel(Skills.HULL_RESTORATION, 1);//coffeemom.getStats().setSkillLevel(Skills.NAVIGATION, 1);
            coffeemom.getStats().setSkillLevel(Skills.SENSORS, 1);
            coffeemom.getStats().setSkillLevel(Skills.CONTAINMENT_PROCEDURES, 1);
            //Amongus
            coffeemom.getStats().setSkillLevel("eis_xiv", 2);
            coffeemom.getStats().setLevel(14);
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
            //market.getMemoryWithoutUpdate().set("$BarCMD_shownEvents", new ArrayList<>());
            market.getMemoryWithoutUpdate().set("$nex_colony_growth_limit", 6);
            market.setImmigrationIncentivesOn(true);
            market2.getCommDirectory().addPerson(coffeemom, 0);
            market2.setAdmin(coffeemom);
            market2.addPerson(coffeemom);
            market2.getCommDirectory().addPerson(mommy2, 1);
            market2.addPerson(mommy2);
            market2.getMemoryWithoutUpdate().set("$nex_colony_growth_limit", 5);
            market2.setImmigrationIncentivesOn(true);
            //market2.getMemoryWithoutUpdate().set("$BarCMD_shownEvents", new ArrayList<>());
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
