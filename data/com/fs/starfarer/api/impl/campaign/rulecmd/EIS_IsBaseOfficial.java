package com.fs.starfarer.api.impl.campaign.rulecmd;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemKeys;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.ids.Ranks;
import com.fs.starfarer.api.util.Misc;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/*do you think anyone ever reads this code to find out this is totally not a copy off histidine's nex code*/
/*probably, they must have sex!*/

/**
 * Verifies post id
 * EIS_IsBaseOfficial
 */
public class EIS_IsBaseOfficial extends BaseCommandPlugin {
	public static final Set<String> TRADER_POSTS = new HashSet<>();
	
	static {
		TRADER_POSTS.add(Ranks.POST_STATION_COMMANDER);
		TRADER_POSTS.add(Ranks.POST_OUTPOST_COMMANDER);
		TRADER_POSTS.add(Ranks.POST_PORTMASTER);
		TRADER_POSTS.add(Ranks.POST_SUPPLY_MANAGER);
		TRADER_POSTS.add(Ranks.POST_SUPPLY_OFFICER);
        }
        
	@Override
	public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap)  {
                String post;
                try {    // stupid-ass workaround for unexplained NPE when interacting with Remnant stations while non-hostile
                    post = memoryMap.get(MemKeys.LOCAL).getString("$postId");
                } catch (NullPointerException ex) {return false;}
                return TRADER_POSTS.contains(post);
		}
	}
