package data.scripts.shipsystems;

import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.GuidedMissileAI;
import com.fs.starfarer.api.combat.MissileAIPlugin;
import com.fs.starfarer.api.combat.MissileAPI;
import com.fs.starfarer.api.combat.ShipCommand;

public class eis_dummyMissileAI implements MissileAIPlugin, GuidedMissileAI 
{
    CombatEntityAPI target;
    MissileAPI missile;
    
    public eis_dummyMissileAI(MissileAPI missile, CombatEntityAPI target)
    {
        setTarget(target);
        this.missile = missile;
    }

    @Override
    public void advance(float amount)
    {
        missile.giveCommand(ShipCommand.ACCELERATE);
    }

    @Override
    public void setTarget(CombatEntityAPI target)
    {
        this.target = target;
    }

    @Override
    public CombatEntityAPI getTarget()
    {
        return target;
    }
}
