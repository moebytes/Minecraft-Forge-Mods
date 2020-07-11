package LightCycleMod;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.storage.IServerConfiguration;
import net.minecraft.world.storage.IServerWorldInfo;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.WorldTickEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.LogicalSide;


public class LightCycleFunctions{

	public					World world;
	MinecraftServer 		minecraftserver;
	GameRules				gamerules;
	IServerConfiguration 	serverconfig;		//func_240793_aU_ is getIServerConfiguration();
	IServerWorldInfo 		worldinfo; 			//func_230407_G_  is getIServerWorldInfo
	
	long					curr_day_time;
	int						tick_counter;
	int						inc_time_by;
	int						new_cycle_in_minutes;

	final int DEFAULT_CYCLE_TIME	= 20;
	final int TICKS_PER_DAY			= 24000;
	final Logger logger 			= LogManager.getLogger();
	
	//Basic setup to do once the world loads
	//Setup the objects needed to modify the daytime, and set the gamerule "doDaylightCycle" to false 
	public LightCycleFunctions(WorldEvent event)
	{
		this.world 				= event.getWorld().getWorld();
		minecraftserver 		= this.world.getServer();
		serverconfig 			= minecraftserver.func_240793_aU_();	//func_240793_aU_ is get_IServerConfiguration();
		worldinfo 				= serverconfig.func_230407_G_(); 			//func_230407_G_  is get_IServerWorldInfo
		gamerules				= minecraftserver.getGameRules();
		curr_day_time			= worldinfo.getDayTime();
		new_cycle_in_minutes	= 1;
		tick_counter			= 0;
		
		inc_time_by( new_cycle_in_minutes );
	}
	
	//Tick the server time on teach server sided world tick.
	public void on_world_tick(WorldTickEvent event)
	{
		//Check to prevent logical side errors and null values here
		if (event.side != LogicalSide.SERVER || event.phase != Phase.START)
			return;
		
		if (world == null)
		{
			logger.info("WORLD IS NULL");
			return;
		}

		//Update the current time and then increment it
		curr_day_time = worldinfo.getDayTime();
		if ( tick_counter != 0 && tick_counter % 20 == 0 )
		{
			worldinfo.setDayTime(curr_day_time + inc_time_by);
			logger.info("INCREMENTED BY: " + inc_time_by);
			logger.info("CURRENT TIME  : " + worldinfo.getDayTime() );
			tick_counter = 0;
		}
		
		tick_counter++;
	}

	public void read_chat(ServerChatEvent event)
	{
		minecraftserver.getCommandManager();
		logger.info("READ A CHAT MESSAGE." + event.getMessage() );
		logger.info("USERNAME: " + event.getUsername() );
		logger.info("MESSAGE : " + event.getMessage() );
	}

	//Gets the do_daylight_cycle gamerule and sets it to false so I can increment the daylight cycle myself
	public void disable_doDaylightCycle()
	{
		gamerules.get( GameRules.DO_DAYLIGHT_CYCLE ).set( false, minecraftserver );
	}
	
	public void inc_time_by(int new_cycle_in_minutes)
	{
		//Get an even number that is closest to what we want
		double temp_timer   	= get_ticks_per_second( new_cycle_in_minutes );
		if ( temp_timer - Math.floor(temp_timer) < .5 )
			inc_time_by = (int)temp_timer;
		else
			inc_time_by = (int)Math.ceil(temp_timer);
	}

	//Read the requested daylight cycle time in minutes and return the ratio of that vs the default
	public double get_ticks_per_second(int new_cycle_length)
	{
		return Math.pow((double)DEFAULT_CYCLE_TIME, 2) / new_cycle_in_minutes;
	}
	

}