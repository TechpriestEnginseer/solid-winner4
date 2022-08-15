package com.fs.starfarer.api.impl.campaign.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.OptionPanelAPI;
import com.fs.starfarer.api.campaign.rules.MemKeys;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.rulecmd.Nex_FactionDirectoryHelper.FactionListGrouping;
import com.fs.starfarer.api.util.Misc;
import exerelin.utilities.NexUtils;
import exerelin.utilities.StringHelper;
import java.awt.Color;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.lwjgl.input.Keyboard;

public class Nexual_Connection extends BaseCommandPlugin {
	
	public static final String FACTION_GROUPS_KEY = "$nex_factionDirectoryGroups";
	public static final float GROUPS_CACHE_TIME = 0f;
	public static final String MUSIC_PRINT_FACTION_OPTION_PREFIX = "nex_printFactionMusic_";
        public static final String MUSIC_PRINT_FACTION_OPTION_PREFIX2 = "nex_printFactionMusic_";
        protected static final int PREFIX_LENGTH = MUSIC_PRINT_FACTION_OPTION_PREFIX.length();
        protected static final int PREFIX_LENGTH2 = MUSIC_PRINT_FACTION_OPTION_PREFIX2.length();
	
	public static final List<String> ARRAYLIST_PLAYERFACTION = Arrays.asList(new String[]{Factions.PLAYER});
	
	public static final HashMap<Integer, Color> colorByMarketSize = new HashMap<>();
	static {
		colorByMarketSize.put(1, Color.WHITE);
		colorByMarketSize.put(2, Color.BLUE);
		colorByMarketSize.put(3, Color.CYAN);
		colorByMarketSize.put(4, Color.GREEN);
		colorByMarketSize.put(5, Color.YELLOW);
		colorByMarketSize.put(6, Color.ORANGE);
		colorByMarketSize.put(7, Color.PINK);
		colorByMarketSize.put(8, Color.RED);
		colorByMarketSize.put(9, Color.MAGENTA);
		colorByMarketSize.put(10, Color.MAGENTA);
	}
	
	public static Color getSizeColor(int size) {
		Color color = Color.GRAY;
		if (colorByMarketSize.containsKey(size))
			color = colorByMarketSize.get(size);
		return color;
	}
	
	@Override
	public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
		String arg = params.get(0).getString(memoryMap);
                String option = memoryMap.get(MemKeys.LOCAL).getString("$option");
		switch (arg)
		{
                        case "setVariables":
                                memoryMap.get(MemKeys.LOCAL).set("$nexual_market_theme", Global.getSector().getPlayerFaction().getMusicMap().get("market_friendly") != null ? ": "+Global.getSector().getPlayerFaction().getMusicMap().get("market_friendly") : "" ,0);
                                memoryMap.get(MemKeys.LOCAL).set("$nexual_encounter_theme", Global.getSector().getPlayerFaction().getMusicMap().get("encounter_friendly") != null ? ": "+Global.getSector().getPlayerFaction().getMusicMap().get("encounter_friendly") : "", 0);
                                return true;
                        case "listGroups":
				listGroups(dialog, memoryMap.get(MemKeys.LOCAL));
				return true;
			case "listFactions":
				OptionPanelAPI opts = dialog.getOptionPanel();
				opts.clearOptions();
				int num = (int)params.get(1).getFloat(memoryMap);
				//memoryMap.get(MemKeys.LOCAL).set("$nex_dirFactionGroup", num);
				List<FactionListGrouping> groups = (List<FactionListGrouping>)(memoryMap.get(MemKeys.LOCAL).get(FACTION_GROUPS_KEY));
				FactionListGrouping group = groups.get(num - 1);
				for (FactionAPI faction : group.factions)
				{

					opts.addOption(Nex_FactionDirectoryHelper.getFactionDisplayName(faction), 
							MUSIC_PRINT_FACTION_OPTION_PREFIX + faction.getId(), faction.getBaseUIColor(), null);
                                        if (faction.getMusicMap().isEmpty()) {opts.setEnabled(MUSIC_PRINT_FACTION_OPTION_PREFIX + faction.getId(), false);opts.setTooltip(MUSIC_PRINT_FACTION_OPTION_PREFIX + faction.getId(), Global.getSettings().getString("eis_ironshell", "EISNexMusic"));}
				}
				
				opts.addOption(Misc.ucFirst(StringHelper.getString("back")), memoryMap.get(MemKeys.LOCAL).getBoolean("$nexual_encounter") ? "nexual_factionEncounterMusic" : "nexual_marketEncounterMusic");
				opts.setShortcut(memoryMap.get(MemKeys.LOCAL).getBoolean("$nexual_encounter") ? "nexual_factionEncounterMusic" : "nexual_marketEncounterMusic", Keyboard.KEY_ESCAPE, false, false, false, false);
				
				NexUtils.addDevModeDialogOptions(dialog);
				return true;
			case "printMusic":
				String factionId = option.substring(PREFIX_LENGTH);
                                dialog.getOptionPanel().clearOptions();
                                /*if (Global.getSector().getFaction(factionId).getMusicMap().get("theme") != null) {
                                    dialog.getOptionPanel().addOption("Theme:" +Global.getSector().getFaction(factionId).getMusicMap().get("theme"), "nexual_theme");
                                }*/
                                if (Global.getSector().getFaction(factionId).getMusicMap().get("market_friendly") != null) {
                                    dialog.getOptionPanel().addOption(Global.getSettings().getString("eis_ironshell", "EISNexMusic1") + Global.getSector().getFaction(factionId).getMusicMap().get("market_friendly"), "nexual_market_friendly");
                                }
                                if (Global.getSector().getFaction(factionId).getMusicMap().get("market_neutral") != null) {
                                    dialog.getOptionPanel().addOption(Global.getSettings().getString("eis_ironshell", "EISNexMusic2") + Global.getSector().getFaction(factionId).getMusicMap().get("market_neutral"), "nexual_market_neutral");
                                }
                                if (Global.getSector().getFaction(factionId).getMusicMap().get("market_hostile") != null) {
                                    dialog.getOptionPanel().addOption(Global.getSettings().getString("eis_ironshell", "EISNexMusic3") + Global.getSector().getFaction(factionId).getMusicMap().get("market_hostile"), "nexual_market_hostile");
                                }
                                if (Global.getSector().getFaction(factionId).getMusicMap().get("encounter_friendly") != null) {
                                    dialog.getOptionPanel().addOption(Global.getSettings().getString("eis_ironshell", "EISNexMusic4") + Global.getSector().getFaction(factionId).getMusicMap().get("encounter_friendly"), "nexual_encounter_friendly");
                                }
                                if (Global.getSector().getFaction(factionId).getMusicMap().get("encounter_neutral") != null) {
                                    dialog.getOptionPanel().addOption(Global.getSettings().getString("eis_ironshell", "EISNexMusic5") + Global.getSector().getFaction(factionId).getMusicMap().get("encounter_neutral"), "nexual_encounter_neutral");
                                }
                                if (Global.getSector().getFaction(factionId).getMusicMap().get("encounter_hostile") != null) {
                                    dialog.getOptionPanel().addOption(Global.getSettings().getString("eis_ironshell", "EISNexMusic6") + Global.getSector().getFaction(factionId).getMusicMap().get("encounter_hostile"), "nexual_encounter_hostile");
                                }
                                memoryMap.get(MemKeys.LOCAL).set("$nexual_faction", factionId, 0f);
                                dialog.getOptionPanel().addOption(Misc.ucFirst(StringHelper.getString("back")), "nexual_factionDirectoryMain");
				dialog.getOptionPanel().setShortcut("nexual_factionDirectoryMain", Keyboard.KEY_ESCAPE, false, false, false, false);
				return true;
                        case "setMusic":
                                factionId = memoryMap.get(MemKeys.LOCAL).getString("$nexual_faction");
                                String musicchangeto = memoryMap.get(MemKeys.LOCAL).getBoolean("$nexual_encounter") ? "encounter_friendly" : "market_friendly";
                                //if (option.startsWith("nexual_theme")) {Global.getSector().getPlayerFaction().getMusicMap().put(musicchangeto, Global.getSector().getFaction(factionId).getMusicMap().get("theme"));}
                                if ("encounter_friendly".equals(musicchangeto)) {
                                    if (option.startsWith("nexual_market_friendly")) {Global.getSector().getPlayerFaction().getMusicMap().put(musicchangeto, Global.getSector().getFaction(factionId).getMusicMap().get("market_friendly"));Global.getSector().getMemoryWithoutUpdate().set("$Nexual_Encounter", Global.getSector().getFaction(factionId).getMusicMap().get("market_friendly"));}
                                    if (option.startsWith("nexual_market_neutral")) {Global.getSector().getPlayerFaction().getMusicMap().put(musicchangeto, Global.getSector().getFaction(factionId).getMusicMap().get("market_neutral"));Global.getSector().getMemoryWithoutUpdate().set("$Nexual_Encounter", Global.getSector().getFaction(factionId).getMusicMap().get("market_neutral"));}
                                    if (option.startsWith("nexual_market_hostile")) {Global.getSector().getPlayerFaction().getMusicMap().put(musicchangeto, Global.getSector().getFaction(factionId).getMusicMap().get("market_hostile"));Global.getSector().getMemoryWithoutUpdate().set("$Nexual_Encounter", Global.getSector().getFaction(factionId).getMusicMap().get("market_hostile"));}
                                    if (option.startsWith("nexual_encounter_friendly")) {Global.getSector().getPlayerFaction().getMusicMap().put(musicchangeto, Global.getSector().getFaction(factionId).getMusicMap().get("encounter_friendly"));Global.getSector().getMemoryWithoutUpdate().set("$Nexual_Encounter", Global.getSector().getFaction(factionId).getMusicMap().get("encounter_friendly"));}
                                    if (option.startsWith("nexual_encounter_neutral")) {Global.getSector().getPlayerFaction().getMusicMap().put(musicchangeto, Global.getSector().getFaction(factionId).getMusicMap().get("encounter_neutral"));Global.getSector().getMemoryWithoutUpdate().set("$Nexual_Encounter", Global.getSector().getFaction(factionId).getMusicMap().get("encounter_neutral"));}
                                    if (option.startsWith("nexual_encounter_hostile")) {Global.getSector().getPlayerFaction().getMusicMap().put(musicchangeto, Global.getSector().getFaction(factionId).getMusicMap().get("encounter_hostile"));Global.getSector().getMemoryWithoutUpdate().set("$Nexual_Encounter", Global.getSector().getFaction(factionId).getMusicMap().get("encounter_hostile"));}
                                    if (dialog.getInteractionTarget().getMarket() == null) {Global.getSoundPlayer().restartCurrentMusic();}
                                } else {
                                    if (option.startsWith("nexual_market_friendly")) {Global.getSector().getPlayerFaction().getMusicMap().put(musicchangeto, Global.getSector().getFaction(factionId).getMusicMap().get("market_friendly"));Global.getSector().getMemoryWithoutUpdate().set("$Nexual_Market", Global.getSector().getFaction(factionId).getMusicMap().get("market_friendly"));}
                                    if (option.startsWith("nexual_market_neutral")) {Global.getSector().getPlayerFaction().getMusicMap().put(musicchangeto, Global.getSector().getFaction(factionId).getMusicMap().get("market_neutral"));Global.getSector().getMemoryWithoutUpdate().set("$Nexual_Market", Global.getSector().getFaction(factionId).getMusicMap().get("market_neutral"));}
                                    if (option.startsWith("nexual_market_hostile")) {Global.getSector().getPlayerFaction().getMusicMap().put(musicchangeto, Global.getSector().getFaction(factionId).getMusicMap().get("market_hostile"));Global.getSector().getMemoryWithoutUpdate().set("$Nexual_Market", Global.getSector().getFaction(factionId).getMusicMap().get("market_hostile"));}
                                    if (option.startsWith("nexual_encounter_friendly")) {Global.getSector().getPlayerFaction().getMusicMap().put(musicchangeto, Global.getSector().getFaction(factionId).getMusicMap().get("encounter_friendly"));Global.getSector().getMemoryWithoutUpdate().set("$Nexual_Market", Global.getSector().getFaction(factionId).getMusicMap().get("encounter_friendly"));}
                                    if (option.startsWith("nexual_encounter_neutral")) {Global.getSector().getPlayerFaction().getMusicMap().put(musicchangeto, Global.getSector().getFaction(factionId).getMusicMap().get("encounter_neutral"));Global.getSector().getMemoryWithoutUpdate().set("$Nexual_Market", Global.getSector().getFaction(factionId).getMusicMap().get("encounter_neutral"));}
                                    if (option.startsWith("nexual_encounter_hostile")) {Global.getSector().getPlayerFaction().getMusicMap().put(musicchangeto, Global.getSector().getFaction(factionId).getMusicMap().get("encounter_hostile"));Global.getSector().getMemoryWithoutUpdate().set("$Nexual_Market", Global.getSector().getFaction(factionId).getMusicMap().get("encounter_hostile"));}
                                    if ("market_friendly".equals(musicchangeto) && dialog.getInteractionTarget().getMarket() != null) {Global.getSoundPlayer().restartCurrentMusic();}
                                }
                            return true;
		}
		
		return false;
	}
	public static void listGroups(InteractionDialogAPI dialog, MemoryAPI memory)
	{
		OptionPanelAPI opts = dialog.getOptionPanel();
		opts.clearOptions();
		List<FactionListGrouping> groups;
		
		if (memory.contains(FACTION_GROUPS_KEY))
		{
			groups = (List<FactionListGrouping>)memory.get(FACTION_GROUPS_KEY);
		}
		else
		{
			List<String> factionsForDirectory = Nex_FactionDirectoryHelper.getFactionsForDirectory(ARRAYLIST_PLAYERFACTION, true);
			groups = Nex_FactionDirectoryHelper.getFactionGroupings(factionsForDirectory);
			memory.set(FACTION_GROUPS_KEY, groups, GROUPS_CACHE_TIME);
		}

		int groupNum = 0;
		for (FactionListGrouping group : groups)
		{
			groupNum++;
			String optionId = "nexual_factionDirectoryList" + groupNum;
			opts.addOption(group.getGroupingRangeString(),
					optionId, group.tooltip);
			opts.setTooltipHighlights(optionId, group.getFactionNames().toArray(new String[0]));
			opts.setTooltipHighlightColors(optionId, group.getTooltipColors().toArray(new Color[0]));
		}
		
		Object exitOpt = "exerelinMarketSpecial";
		opts.addOption(Misc.ucFirst(StringHelper.getString("back")), exitOpt);
		opts.setShortcut(exitOpt, Keyboard.KEY_ESCAPE, false, false, false, false);
		
		NexUtils.addDevModeDialogOptions(dialog);
	}
}