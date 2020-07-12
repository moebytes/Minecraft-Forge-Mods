package LightCycleMod;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.network.play.server.SUpdateTimePacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.storage.IServerConfiguration;
import net.minecraft.world.storage.IServerWorldInfo;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.ServerTickEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;


/*
 * Author	: Peter Caylor
 * Date		: 7/11/2020
 * Purpose	: Handles the internal functionality for the light cycle mod. Functions here modify the server time or assist other functions.
 */

public class LightCycleFunctions{

	public					World world;
	MinecraftServer			minecraftserver;
	GameRules				gamerules;
	IServerConfiguration	serverconfig;		//func_240793_aU_ is getIServerConfiguration();
	IServerWorldInfo		worldinfo; 			//func_230407_G_  is getIServerWorldInfo
	
	long					curr_day_time;
	long					inc_time_by;

	double					new_cycle_in_minutes	= 0;
	final int				DEFAULT_CYCLE_TIME		= 20;
	final int				TICKS_PER_DAY			= 24000;
	final Logger			logger 					= LogManager.getLogger();
	
	//Basic setup to do once the world loads
	//Setup the objects needed to modify the daytime, and set the gamerule "doDaylightCycle" to false 
	public LightCycleFunctions(WorldEvent event)
	{
		this.world				= event.getWorld().getWorld();
		minecraftserver			= this.world.getServer();
		serverconfig			= minecraftserver.func_240793_aU_();	//func_240793_aU_ is get_IServerConfiguration();
		worldinfo				= serverconfig.func_230407_G_(); 			//func_230407_G_  is get_IServerWorldInfo
		gamerules				= minecraftserver.getGameRules();
		
		try
		{
			new_cycle_in_minutes = (new DataStorage()).read_json();
		}
		catch(Exception e)
		{
			logger.info("Error reading json file.");
		}
		
		if (new_cycle_in_minutes == 0)
			new_cycle_in_minutes = DEFAULT_CYCLE_TIME;
		
		set_inc_time_by( new_cycle_in_minutes );
		
		
	}
	
	//Tick the server time on server sided world tick.
	public void on_world_tick(ServerTickEvent event)
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
		long gametime = worldinfo.getGameTime();
		if ( gametime % 5 == 0 && gametime != 0)
		{
			minecraftserver.getPlayerList().func_232642_a_(new SUpdateTimePacket(world.getGameTime(), world.getDayTime(), world.getGameRules().getBoolean(GameRules.DO_DAYLIGHT_CYCLE)), world.func_234923_W_());
			worldinfo.setDayTime( curr_day_time + (inc_time_by / 5) );
		}
		
		if ( gametime % 20 == 0 && gametime != 0)
			logger.info("TIME: " + curr_day_time);
			
		
	}

	public void read_chat(ServerChatEvent event)
	{
		logger.info("READ A CHAT MESSAGE." + event.getMessage() );
		logger.info("USERNAME: " + event.getUsername() );
		logger.info("MESSAGE : " + event.getMessage() );
	}
	
	//Sets the server cycle rate on start
	public void set_time_on_start()
	{
		DataStorage storage = new DataStorage();
		try
		{
			File file = new File(storage.get_json_path());
			if ( file.exists() )
				new_cycle_in_minutes = storage.read_json();
			else
				//storage.write_json(LightCycleMod.instance.handler.get_functions().get_inc_time_by());
				storage.write_json( DEFAULT_CYCLE_TIME );
		}
		catch(Exception e)
		{
			logger.info("Could not read last increment speed from json: " + e);
		}
	}
	
	//Gets the do_daylight_cycle gamerule and sets it to false so I can increment the daylight cycle myself
	//Saves the current speed in a txt file so it persists beyond restarts
	public void disable_doDaylightCycle()
	{
		gamerules.get( GameRules.DO_DAYLIGHT_CYCLE ).set( false, minecraftserver );
	}
	
	public void set_inc_time_by(double new_cycle_in_minutes)
	{
		//Get an even number that is closest to what we want
		double temp_timer = get_ticks_per_second( new_cycle_in_minutes );
		if ( temp_timer - Math.floor(temp_timer) < .5 )
			inc_time_by = (long)temp_timer;
		else
			inc_time_by = (long)Math.floor(temp_timer);
		
		DataStorage storage = new DataStorage();
		storage.write_json(new_cycle_in_minutes);
	}
	
	public long get_inc_time_by()
	{
		return inc_time_by;
	}
	
	//Read the requested daylight cycle time in minutes and return the ratio of that vs the default
	public double get_ticks_per_second(double new_cycle_length)
	{
		return (double)TICKS_PER_DAY / ( 60.0 * new_cycle_length );
	}
	
	
	public void register_server_commands(FMLServerStartingEvent event)
	{
		LightCommands.register( event.getCommandDispatcher() );
	}

}