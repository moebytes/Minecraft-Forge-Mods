package LightCycleMod;

import net.minecraftforge.event.TickEvent.ServerTickEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;

/*
 * Author	: Peter Caylor
 * Date		: 7/11/2020
 * Purpose	: Receives events from forge and runs the appropriate function depending on what is fired.
 */

//Handle events and then run the appropriate functions based on those events
@Mod.EventBusSubscriber
public class LightEventHandler {

	private LightCycleFunctions functions;

	@SubscribeEvent
	public void on_start( FMLServerStartingEvent event )
	{
		functions.register_server_commands(event);
	}
	
	@SubscribeEvent
	public void on_load( WorldEvent.Load event )
	{
		if ( functions == null )
		{
			functions = new LightCycleFunctions( event );
			functions.disable_doDaylightCycle();
			functions.set_time_on_start();
		}
	}

	//Tick the server time on teach server sided world tick.
	@SubscribeEvent
	public void on_world_tick( ServerTickEvent event )
	{
		functions.on_world_tick( event );
	}

	public LightCycleFunctions get_functions()
	{
		return functions;
	}
	
}
