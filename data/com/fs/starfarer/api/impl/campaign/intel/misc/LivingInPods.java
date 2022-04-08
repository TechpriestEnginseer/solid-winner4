package com.fs.starfarer.api.impl.campaign.intel.misc;

import java.util.Set;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.impl.campaign.intel.BaseIntelPlugin;
import com.fs.starfarer.api.ui.SectorMapAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;

public class LivingInPods extends BaseIntelPlugin {
	protected SectorEntityToken entity;
        protected String text;
        protected String finishedtext2;
        protected String person;
        protected String finishedsociety;
        protected String society;
        protected String title;
	public LivingInPods(SectorEntityToken entity, String text, String finishedtext2, String person, String finishedsociety, String society, String title) {
		this.entity = entity;
                this.text = text;
                this.finishedtext2 = finishedtext2;
                this.person = person;
                this.finishedsociety = finishedsociety;
                this.society = society;
                this.title = title;
	}
        
	/*@Override
	public void advance(float amount) {
            super.advance(amount);
            
	}*/
        
	@Override
	public void createSmallDescription(TooltipMakerAPI info, float width, float height) {
		float opad = 10f;
                info.addImage(Global.getSector().getImportantPeople().getPerson(person).getFaction().getLogo(), width, 128, opad);
		if (entity.getFaction().equals(Global.getSector().getImportantPeople().getPerson(person).getFaction())) {info.addPara(finishedtext2, opad);
                Global.getSector().getImportantPeople().getPerson(person).getMemoryWithoutUpdate().unset(finishedsociety);} else {info.addPara(text, opad);}
	}
	
	@Override
	public String getIcon() {
		return Global.getSector().getImportantPeople().getPerson(person).getPortraitSprite();
	}
	
	@Override
	public Set<String> getIntelTags(SectorMapAPI map) {
		Set<String> tags = super.getIntelTags(map);
		tags.add(Tags.INTEL_STORY);
		return tags;
	}
	
        @Override
	public String getName() {
		return title;
	}

	@Override
	public SectorEntityToken getMapLocation(SectorMapAPI map) {
		return entity;
	}
	
	@Override
	public boolean shouldRemoveIntel() {
		return Global.getSector().getImportantPeople().getPerson(person).getMemoryWithoutUpdate().getBoolean(society) && !isNew();
	}
	
}







