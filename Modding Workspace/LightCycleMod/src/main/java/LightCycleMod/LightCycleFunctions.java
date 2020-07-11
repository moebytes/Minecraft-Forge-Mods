package LightCycleMod;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.storage.IServerConfiguration;
import net.minecraft.world.storage.IServerWorldInfo;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.WorldTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;


public class LightCycleFunctions{

	public					World world;
	MinecraftServer 		minecraftserver;
	IServerConfiguration 	serverconfig;		//func_240793_aU_ is getIServerConfiguration();
	IServerWorldInfo 		worldinfo; 			//func_230407_G_  is getIServerWorldInfo
	long					curr_day_time;
	
	//Basic setup to do once the world loads
	//Setup the objects needed to modify the daytime, and set the gamerule "doDaylightCycle" to false 
	public void initial_setup(World world)
	{
		this.world 		= world;
		minecraftserver = this.world.getServer();
		serverconfig 	= minecraftserver.func_240793_aU_();	//func_240793_aU_ is get_IServerConfiguration();
		worldinfo 		= serverconfig.func_230407_G_(); 			//func_230407_G_  is get_IServerWorldInfo
		curr_day_time	= worldinfo.getDayTime();
		
		//world.getGameRules().
	}
	
	//Tick the server time on teach server sided world tick.
	@SubscribeEvent
	public void on_world_tick(WorldTickEvent event)
	{		
		//Initial setup
		if ( world.equals(null) )
			initial_setup(event.world);
		
		//Check to prevent logical side errors and null values here
		if (event.side != LogicalSide.SERVER || event.phase != Phase.START)
			return;
		
		if (world == null)
			return;
	}


	//Read the requested daylight cycle time in minutes and return the ratio of that vs the default
	public double get_ticking_ratio()
	{

		return 0.0;
	}

	//Receive commands and handle using brigadier
	@SubscribeEvent
	public void read_chat()
	{
		
	}
}
