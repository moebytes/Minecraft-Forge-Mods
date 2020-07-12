package LightCycleMod;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.event.TickEvent.ServerTickEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;

/*
 * Author	: Peter Caylor
 * Date		: 7/11/2020
 * Purpose	: This class receives events from forge and runs the appropriate function depending on what is fired.
 */

//Handle events and then run the appropriate functions based on those events
@Mod.EventBusSubscriber
public class LightEventHandler {

	private LightCycleFunctions functions;
	private Logger logger = LogManager.getLogger();

	@SubscribeEvent
	public void on_start( FMLServerStartingEvent event )
	{
		functions.register_server_commands(event);
	}
	
	@SubscribeEvent
	public void on_load( WorldEvent.Load event )
	{
		DataStorage storage = new DataStorage();
		if ( functions == null )
		{
			functions = new LightCycleFunctions( event );
			functions.disable_doDaylightCycle();
			
			try
			{
				File file = new File("lightcyclemod.txt");
				if ( file.exists() )
					this.functions.new_cycle_in_minutes = storage.read_json();
				else
					storage.write_json(LightCycleMod.instance.handler.get_functions().get_inc_time_by());
			}
			catch(Exception e)
			{
				logger.info("Could not read last increment speed from json: " + e);
			}
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
