package LightCycleMod;

import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.TickEvent.ServerTickEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


//Handle events and then run the appropriate functions based on those events
@Mod.EventBusSubscriber
public class LightEventHandler {

	private LightCycleFunctions functions;
	
	@SubscribeEvent
	public void on_load( WorldEvent.Load event )
	{
		if ( functions == null )
		{
			functions = new LightCycleFunctions( event );
			functions.disable_doDaylightCycle();
		}
	}

	//Tick the server time on teach server sided world tick.
	@SubscribeEvent
	public void on_world_tick( ServerTickEvent event )
	{
		functions.on_world_tick( event );
	}

	//Receive commands and handle using brigadier
	@SubscribeEvent
	public void read_chat( ServerChatEvent event )
	{
		functions.read_chat(event);
	}

}
