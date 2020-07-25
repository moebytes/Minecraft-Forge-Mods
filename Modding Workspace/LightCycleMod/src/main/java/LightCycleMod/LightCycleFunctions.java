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
import net.minecraftforge.event.TickEvent.ServerTickEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;

/*
 * Author   : Peter Caylor
 * Date     : 7/11/2020
 * Purpose  : Handles the internal functionality for the light cycle mod. Functions here modify the server time or assist other functions.
 */

public class LightCycleFunctions
{

    public static LightCycleFunctions instance;
    private World                     world;
    private MinecraftServer           minecraftserver;
    private IServerWorldInfo          worldinfo;
    private int                       update_push_freq;
    private long                      inc_time_by;
    public  final int                 DEFAULT_CYCLE_TIME   = 20;
    public  final int                 TICKS_PER_DAY        = 24000;
    private final Logger              LOGGER               = LogManager.getLogger();

    // Basic setup to do once the world loads
    // Setup the objects needed to modify the daytime, and set the gamerule "doDaylightCycle" to false
    public LightCycleFunctions( WorldEvent.Load event )
    {
        instance                          = this;
        world                             = event.getWorld().getWorld();
        minecraftserver                   = world.getServer();
        IServerConfiguration serverconfig = minecraftserver.func_240793_aU_(); // func_240793_aU_ is getIServerConfiguration()
        worldinfo                         = serverconfig.func_230407_G_();     // func_230407_G_  is getIServerWorldInfo()
        GameRules gamerules               = minecraftserver.getGameRules();

        gamerules.get( GameRules.DO_DAYLIGHT_CYCLE ).set( false, minecraftserver );
        double new_cycle_in_minutes = set_time_on_start();
        update_push_freq( new_cycle_in_minutes );
        set_inc_time_by( new_cycle_in_minutes );
    }

    // Tick the server time on server sided world tick.
    public void on_world_tick( ServerTickEvent event )
    {
        if ( inc_time_by == 0 )
            return;
        
        // Update the current time and then increment it
        long curr_day_time = worldinfo.getDayTime();
        long gametime = worldinfo.getGameTime();
        
        if ( gametime % update_push_freq == 0 && gametime != 0 )
        {
            worldinfo.setDayTime( curr_day_time + (inc_time_by / (DEFAULT_CYCLE_TIME / update_push_freq)) );
            if ( gametime % 20 != 0 )
                minecraftserver.getPlayerList().func_232642_a_( new SUpdateTimePacket( world.getGameTime(), world.getDayTime(), world.getGameRules().getBoolean( GameRules.DO_DAYLIGHT_CYCLE ) ), world.func_234923_W_() );
        }
    }

    // Updates how often the server pushes world SUpdateTimePackets to clients
    private void update_push_freq( double new_cycle_in_minutes )
    {
        //Update push frequency is the amount of ticks per 1 update
        if ( new_cycle_in_minutes <= 0 )
            update_push_freq = 20;
        else if ( new_cycle_in_minutes < 5 )
            update_push_freq = 2;
        else if ( new_cycle_in_minutes < 10 )
            update_push_freq = 5;
        else if ( new_cycle_in_minutes < 20 )
            update_push_freq = 10;
        else
            update_push_freq = 20;
    }

    // Sets the server cycle rate on start
    // If there is no file storing a previous cycle rate, set it to default
    public double set_time_on_start()
    {
        DataStorage storage = new DataStorage();
        try
        {
            File file = new File( storage.get_json_path() );
            if ( file.exists() )
                return storage.read_json();
            else
            {
                file.getParentFile().mkdirs();
                file.createNewFile();
                storage.write_json( DEFAULT_CYCLE_TIME );
            }
        } catch ( Exception e )
        {
            LOGGER.info( "Could not read last increment speed from json: " + e );
        }
        return DEFAULT_CYCLE_TIME;
    }
    
    // Sets the daylight increment by taking a requested number in minutes and converting it into server ticks per second
    public void set_inc_time_by( double new_cycle_in_minutes )
    {
        if ( new_cycle_in_minutes == 0 )
            inc_time_by = 0;
        else
            inc_time_by = (long)get_ticks_per_second( new_cycle_in_minutes );
        
        DataStorage storage = new DataStorage();
        storage.write_json( new_cycle_in_minutes );
        LOGGER.info( "(LOGGER) Set day length to " + new_cycle_in_minutes + " minutes." );
        
        update_push_freq( new_cycle_in_minutes );
    }

    // Read the requested daylight cycle time in minutes and return the ratio of
    // that vs the default
    private double get_ticks_per_second( double new_cycle_length )
    {
        return (double)TICKS_PER_DAY / (60.0 * new_cycle_length);
    }

    // Registers chat commands with the server sided command manager. Allows for command autocomplete and robust automatic error checking.
    public void register_server_commands( FMLServerStartingEvent event )
    {
        LightCommands.register( event.getCommandDispatcher() );
    }

    // GETTERS AND SETTERS
    public World get_world()
    {
        return this.world;
    }

    public MinecraftServer get_minecraftserver()
    {
        return this.minecraftserver;
    }

    public IServerWorldInfo get_worldinfo()
    {
        return this.worldinfo;
    }

    public int get_update_push_freq()
    {
        return this.update_push_freq;
    }
    
    public long get_inc_time_by()
    {
        return this.inc_time_by;
    }

    public void set_update_push_freq( int update_push_freq )
    {
        this.update_push_freq = update_push_freq;
    }

    public void set_inc_time_by( long inc_time_by )
    {
        this.inc_time_by = inc_time_by;
    }
}