package data.scripts.console;

import com.fs.starfarer.api.Global;
import exerelin.campaign.SectorManager;
import org.lazywizard.console.BaseCommand;
import org.lazywizard.console.CommonStrings;
import org.lazywizard.console.Console;

public class eis_irsmommies implements BaseCommand {
    @Override
    public CommandResult runCommand(String args, CommandContext context) {
        if (context != CommandContext.CAMPAIGN_MAP) {
            Console.showMessage(CommonStrings.ERROR_CAMPAIGN_ONLY);
            return CommandResult.WRONG_CONTEXT;
        }
        if (Global.getSettings().getModManager().isModEnabled("nexerelin") && !SectorManager.getManager().isCorvusMode()) {
            Console.showMessage("Error: Random Core Mode... they should have spawned no?!");
            return CommandResult.ERROR;
        }
        if (Global.getSector().getEconomy().getMarket("chitagupta") != null || Global.getSector().getEconomy().getMarket("yami") != null) {
                Console.showMessage("Error: Chitagupta and Yami have markets... this won't work.");
                return CommandResult.ERROR;
        }
        /*if (Global.getSector().getStarSystem("Naraka") != null) {
            StarSystemAPI system = Global.getSector().getStarSystem("Naraka");
            SectorEntityToken lollmao2 = system.addCustomEntity(null, null, "sensor_array", "hegemony"); 
            lollmao2.setCircularOrbitPointingDown(system.getEntityById("naraka"), 90 + 60, 3000, 100);
            system.removeEntity(Global.getSector().getEntityById("yami"));
            system.removeEntity(Global.getSector().getEntityById("chitagupta"));
            PlanetAPI naraka_b = system.addPlanet("yami", Global.getSector().getEntityById("yama"), "Yami", "cryovolcanic", 0, 60, 600, 40); //formerly 450
            naraka_b.setCustomDescriptionId("planet_yami_ironshell");
            PlanetAPI naraka_c = system.addPlanet("chitagupta", Global.getSector().getEntityById("naraka"), "Chitagupta", "water", 90, 100, 5750, 380);
            JumpPointAPI jumpPoint1 = Global.getFactory().createJumpPoint("naraka_jump", "Naraka Jump-point");
            jumpPoint1.setCircularOrbit( system.getEntityById("naraka"), 60 + 60, 4600, 140);
            jumpPoint1.setRelatedPlanet(naraka_c);
            system.addEntity(jumpPoint1);
            SectorEntityToken planet = Global.getSector().getEntityById("chitagupta");
            LocationAPI hyper = Global.getSector().getHyperspace();
            NascentGravityWellAPI well = Global.getSector().createNascentGravityWell(planet,50f);
            hyper.addEntity(well);
            well.autoUpdateHyperLocationBasedOnInSystemEntityAtRadius(planet, 470f);
            naraka_c.setCustomDescriptionId("planet_chitagupta_ironshell");
            PlanetSpecAPI sex = naraka_c.getSpec();
            sex.setAtmosphereColor(new Color(30, 90, 140, 130));
            sex.setAtmosphereThickness(0.4f);
            sex.setAtmosphereThicknessMin(62f);
            sex.setCloudTexture("graphics/planets/clouds_white.png");
            sex.setCloudColor(new Color (255,255,255,200));
            sex.setCloudRotation(-3f);
            sex.setIconColor(new Color (45,98,174,255));
            sex.setPlanetColor(new Color (255,255,255,255));
            naraka_c.applySpecChanges();
            SharedData.getData().getPersonBountyEventData().addParticipatingFaction("ironshell");
            new Seriouslywhatisthewholepointofthisisweartoluddiwillmakethispersoneattheirshoe().generate(Global.getSector());
        }
        Console.showMessage("Chitagupta and Yami creating.... done! If you did something to these two planets, WELP THAT'S ON YOU! Also, I can't really guarantee the bug-free from this command.");*/
        Console.showMessage("Doesn't work anymore.. sorry :(");
        return CommandResult.SUCCESS;
    }
}
