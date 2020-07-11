package LightCycleMod;

import net.minecraftforge.event.TickEvent.WorldTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class LightEventHandler {

	
	//Handle events and then run the appropriate functions based on those events
	public void on_world_tick()
	{
		
	}
	
	//Tick the server time on teach server sided world tick.
	@SubscribeEvent
	public void on_world_tick(WorldTickEvent event)
	{		

	}

	//Receive commands and handle using brigadier
	@SubscribeEvent
	public void read_chat()
	{
		
	}
}
	
	
}
