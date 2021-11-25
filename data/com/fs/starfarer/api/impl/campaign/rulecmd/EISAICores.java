package com.fs.starfarer.api.impl.campaign.rulecmd;

import java.util.List;
import java.util.Map;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.CargoPickerListener;
import com.fs.starfarer.api.campaign.CargoStackAPI;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.OptionPanelAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.TextPanelAPI;
import com.fs.starfarer.api.campaign.econ.CommoditySpecAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.CoreReputationPlugin.CustomRepImpact;
import com.fs.starfarer.api.impl.campaign.CoreReputationPlugin.RepActionEnvelope;
import com.fs.starfarer.api.impl.campaign.CoreReputationPlugin.RepActions;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Strings;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Misc.Token;
import com.fs.starfarer.api.util.WeightedRandomPicker;

public class EISAICores extends BaseCommandPlugin {
    
        public String yesamongus;
        public String yesamongus2;
        public String yesamongus3;
        
	protected CampaignFleetAPI playerFleet;
	protected SectorEntityToken entity;
	protected FactionAPI playerFaction;
	protected FactionAPI entityFaction;
	protected TextPanelAPI text;
	protected OptionPanelAPI options;
	protected CargoAPI playerCargo;
	protected MemoryAPI memory;
	protected InteractionDialogAPI dialog;
	protected Map<String, MemoryAPI> memoryMap;
	protected PersonAPI person;
	protected FactionAPI faction;

	protected boolean buysAICores;
	protected float valueMult;
	protected float repMult;
	
	public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Token> params, Map<String, MemoryAPI> memoryMap) {
		
		this.dialog = dialog;
		this.memoryMap = memoryMap;
		
		String command = params.get(0).getString(memoryMap);
		if (command == null) return false;
		
		memory = getEntityMemory(memoryMap);
		
		entity = dialog.getInteractionTarget();
		text = dialog.getTextPanel();
		options = dialog.getOptionPanel();
		
		playerFleet = Global.getSector().getPlayerFleet();
		playerCargo = playerFleet.getCargo();
		
		playerFaction = Global.getSector().getPlayerFaction();
		entityFaction = entity.getFaction();
		
		person = dialog.getInteractionTarget().getActivePerson();
		faction = person.getFaction();
		
		//buysAICores = faction.getCustomBoolean("buysAICores");
		valueMult = 0.5f; //faction.getCustomFloat("AICoreValueMult");
		repMult = faction.getCustomFloat("AICoreRepMult");
                switch (command) {
                    case "selectCores":
                        WeightedRandomPicker<String> AmongUs = new WeightedRandomPicker<String>();
                        AmongUs.add("graphics/icons/cargo/ai_core_gamma.png", 3);
                        AmongUs.add("graphics/icons/cargo/ai_core_beta.png", 2);
                        AmongUs.add("graphics/icons/cargo/ai_core_alpha.png", 1);
                        yesamongus = AmongUs.pick();
                        yesamongus2 = AmongUs.pick();
                        yesamongus3 = AmongUs.pick();
			selectCores();
                        break;
                    case "selectSMatter":
                        WeightedRandomPicker<String> AmongUs2 = new WeightedRandomPicker<String>();
                        AmongUs2.add("graphics/ISTL/icons/cargo/istl_sigma_unstable.png", 3);
                        AmongUs2.add("graphics/ISTL/icons/cargo/istl_sigma_low.png", 2);
                        AmongUs2.add("graphics/ISTL/icons/cargo/istl_sigma_high.png", 1);
                        yesamongus = AmongUs2.pick();
                        yesamongus2 = AmongUs2.pick();
                        yesamongus3 = AmongUs2.pick();
                        selectLigma();
                        break;
                    case "playerHasLigma":
                        return playerHasLigma();
                    default:
                        break;
                }
		
		return true;
	}
        //Nope.
	/*protected boolean personCanAcceptCores() {
		if (person == null || !buysAICores) return false;
		
		return Ranks.POST_BASE_COMMANDER.equals(person.getPostId()) ||
			   Ranks.POST_STATION_COMMANDER.equals(person.getPostId()) ||
			   Ranks.POST_ADMINISTRATOR.equals(person.getPostId()) ||
			   Ranks.POST_OUTPOST_COMMANDER.equals(person.getPostId());
	}*/

	protected void selectCores() {
		CargoAPI copy = Global.getFactory().createCargo(false);
		for (CargoStackAPI stack : playerCargo.getStacksCopy()) {
			CommoditySpecAPI spec = stack.getResourceIfResource();
			if (spec != null && spec.getDemandClass().equals(Commodities.AI_CORES)) {
				copy.addFromStack(stack);
			}
		}
		copy.sort();
		
		final float width = 310f;
		dialog.showCargoPickerDialog("Select AI cores to turn in", "Confirm", "Cancel", true, width, copy, new CargoPickerListener() {
			public void pickedCargo(CargoAPI cargo) {
				cargo.sort();
				for (CargoStackAPI stack : cargo.getStacksCopy()) {
					playerCargo.removeItems(stack.getType(), stack.getData(), stack.getSize());
					if (stack.isCommodityStack()) {
						AddRemoveCommodity.addCommodityLossText(stack.getCommodityId(), (int) stack.getSize(), text);
					}
				}
				
				float bounty = computeCoreCreditValue(cargo);
				float repChange = computeCoreReputationValue(cargo);

				if (bounty > 0) {
					playerCargo.getCredits().add(bounty);
					AddRemoveCommodity.addCreditsGainText((int)bounty, text);
				}
				
				if (repChange >= 1f) {
					CustomRepImpact impact = new CustomRepImpact();
					impact.delta = repChange * 0.01f;
					if (impact.delta >= 0.01f) {
						Global.getSector().adjustPlayerReputation(
								new RepActionEnvelope(RepActions.CUSTOM, impact,
													  null, text, true), 
													  person);
					}
				}
				if (bounty == 0 || repChange < 1f) {
                                    FireAll.fire(null, dialog, memoryMap, "PopulateCoresTurnInOptionsCeleste");
                                } else {FireBest.fire(null, dialog, memoryMap, "AICoresTurnedInCeleste");}
				
			}
			public void cancelledCargoSelection() {
			}
			public void recreateTextPanel(TooltipMakerAPI panel, CargoAPI cargo, CargoStackAPI pickedUp, boolean pickedUpFromSource, CargoAPI combined) {
			
				float bounty = computeCoreCreditValue(combined);
				float repChange = computeCoreReputationValue(combined);
				
				float pad = 3f;
				float opad = 10f;

				panel.setParaFontOrbitron();
				panel.addPara(Misc.ucFirst("Celeste Family"), faction.getBaseUIColor(), 1f);
				panel.setParaFontDefault();
				panel.addImage("graphics/icons/cargo/package_celeste_enlarged.png", width * 1f, 3f);
                                //panel.addImage(yesamongus, width * 1f, 3f);
                                panel.addImages(width, 80, 3f, 0f, yesamongus, yesamongus2, yesamongus3);
				panel.addPara("Compared to dealing with others, turning AI cores in to Caeda Celeste will result in: ", opad);
				panel.beginGridFlipped(width, 1, 40f, 10f);
				panel.addToGrid(0, 0, "Gratituity value", "" + (int)(valueMult * 100f) + "%");
				panel.addToGrid(0, 1, "Relation gain", "" + (int)(repMult * 100f) + "%");
				panel.addGrid(pad);
				
				panel.addPara("If you turn in the selected AI cores, you will receive a %s donation " +
						"and your standing with Caeda Celeste will improve by %s points.",
						opad * 1f, Misc.getHighlightColor(),
						Misc.getWithDGS(bounty) + Strings.C,
						"" + (int) repChange);
			}
		});
	}
        
    protected boolean playerHasLigma() {
        for (CargoStackAPI stack : playerCargo.getStacksCopy()) {
            CommoditySpecAPI spec = stack.getResourceIfResource();
            if (spec != null && spec.hasTag("sigma_matter")) {
                    return true;
            }
        }
        return false;
    }
        
    protected void selectLigma() {
        CargoAPI copy = Global.getFactory().createCargo(false);
        for (CargoStackAPI stack : playerCargo.getStacksCopy()) {
            CommoditySpecAPI spec = stack.getResourceIfResource();
            if (spec != null && spec.hasTag("sigma_matter")) {
                copy.addFromStack(stack);
            }
        }
        copy.sort();

        final float width = 310f;
        dialog.showCargoPickerDialog("Select Sigma matter to turn in", "Confirm", "Cancel", true, width, copy, new CargoPickerListener() {
            @Override
            public void pickedCargo(CargoAPI cargo) {
                cargo.sort();
                for (CargoStackAPI stack : cargo.getStacksCopy()) {
                    playerCargo.removeItems(stack.getType(), stack.getData(), stack.getSize());
                    if (stack.isCommodityStack()) { // should be always, but just in case
                        AddRemoveCommodity.addCommodityLossText(stack.getCommodityId(), (int) stack.getSize(), text);
                    }
                }

                float bounty = computeCoreCreditValue(cargo);
                float repChange = computeCoreReputationValue(cargo);

                if (bounty > 0) {
                    playerCargo.getCredits().add(bounty);
                    AddRemoveCommodity.addCreditsGainText((int)bounty, text);
                }

                if (repChange >= 1f) {
                    CustomRepImpact impact = new CustomRepImpact();
                    impact.delta = repChange * 0.01f;
                    if (impact.delta >= 0.01f) {
                            Global.getSector().adjustPlayerReputation(
                                new RepActionEnvelope(RepActions.CUSTOM, impact,
                                                      null, text, true), 
                                                      person);
                    }
                }
		if (bounty == 0 || repChange < 1f) {
                    FireAll.fire(null, dialog, memoryMap, "PopulateCoresTurnInOptionsCeleste");
                } else {FireBest.fire(null, dialog, memoryMap, "AICoresTurnedInCeleste");}
            }
            @Override
            public void cancelledCargoSelection() {
            }
            @Override
            public void recreateTextPanel(TooltipMakerAPI panel, CargoAPI cargo, CargoStackAPI pickedUp, boolean pickedUpFromSource, CargoAPI combined) {

                float bounty = computeCoreCreditValue(combined);
                float repChange = computeCoreReputationValue(combined);

                float pad = 3f;
                float opad = 10f;
                
                panel.setParaFontOrbitron();
		panel.addPara(Misc.ucFirst("Celeste Family"), faction.getBaseUIColor(), 1f);
                panel.setParaFontDefault();
                panel.addImage("graphics/icons/cargo/package_celeste_enlarged.png", width * 1f, 3f);
                panel.addImages(width, 80, 3f, 0f, yesamongus, yesamongus2, yesamongus3);
		panel.addPara("Compared to dealing with others, turning Sigma matter in to Caeda Celeste will result in: ", opad);
                panel.beginGridFlipped(width, 1, 40f, 10f);
                panel.addToGrid(0, 0, "Gratituity value", "" + (int)(valueMult * 100f) + "%");
                panel.addToGrid(0, 1, "Relation gain", "" + (int)(repMult * 100f) + "%");
                panel.addGrid(pad);

                panel.addPara("If you turn in the selected Sigma matter, you will receive %s in compensation " +
                                "and your standing with Caeda Celeste will improve by %s points.",
                                opad * 1f, Misc.getHighlightColor(),
                                Misc.getWithDGS(bounty) + Strings.C,
                                "" + (int) repChange);
            }
        });
    }

	protected float computeCoreCreditValue(CargoAPI cargo) {
		float bounty = 0;
                float sigmabounty = 0;
		for (CargoStackAPI stack : cargo.getStacksCopy()) {
			CommoditySpecAPI spec = stack.getResourceIfResource();
			if (spec != null && spec.getDemandClass().equals(Commodities.AI_CORES)) {
				bounty += spec.getBasePrice() * stack.getSize();
			}
                        if (spec != null && spec.hasTag("sigma_matter")) {
                                sigmabounty += spec.getBasePrice() * stack.getSize();
                        }
		}
		bounty *= valueMult;
                sigmabounty *= valueMult;
                bounty += sigmabounty;
		return bounty;
	}
	
	protected float computeCoreReputationValue(CargoAPI cargo) {
		float rep = 0;
                float sigmarep = 0;
		for (CargoStackAPI stack : cargo.getStacksCopy()) {
			CommoditySpecAPI spec = stack.getResourceIfResource();
			if (spec != null && spec.getDemandClass().equals(Commodities.AI_CORES)) {
				rep += getBaseRepValue(spec.getId()) * stack.getSize();
			}
                        if (spec != null && spec.hasTag("sigma_matter")) {
                                sigmarep += getBaseRepValue(spec.getId()) * stack.getSize();
                        }
		}
		rep *= repMult;
                sigmarep *= repMult;
                rep += sigmarep;
		return rep;
	}
	
	public static float getBaseRepValue(String coreType) {
		if (Commodities.OMEGA_CORE.equals(coreType)) {
			return 0f; //Lol.
		}
		if (Commodities.ALPHA_CORE.equals(coreType)) {
			return 7.5f;
		}
		if (Commodities.BETA_CORE.equals(coreType)) {
			return 3f;
		}
		if (Commodities.GAMMA_CORE.equals(coreType)) {
			return 1.5f;
		}
                if ("istl_sigma_matter2".equals(coreType)) {
                    return 5.5f;
                }

                if ("istl_sigma_matter1".equals(coreType)) {
                    return 4;
                }
                if ("istl_sigma_matter3".equals(coreType)) {
                        return 1.5f;
                }
		return 1f;
	}
	
	//Technically don't need this.
	/*protected boolean playerHasCores() {
		for (CargoStackAPI stack : playerCargo.getStacksCopy()) {
			CommoditySpecAPI spec = stack.getResourceIfResource();
			if (spec != null && spec.getDemandClass().equals(Commodities.AI_CORES)) {
				return true;
			}
		}
		return false;
	}*/

	
	
}















