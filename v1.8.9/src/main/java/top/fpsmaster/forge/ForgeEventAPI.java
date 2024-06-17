package top.fpsmaster.forge;

import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import top.fpsmaster.event.EventDispatcher;
import top.fpsmaster.event.events.EventAttack;

public class ForgeEventAPI {

    @SubscribeEvent
    public void onAttack(AttackEntityEvent e){
        EventDispatcher.dispatchEvent(new EventAttack(e.target));
    }

}
