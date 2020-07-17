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
 * Author   : Peter Caylor
 * Date     : 7/11/2020
 * Purpose  : Handles the internal functionality for the light cycle mod. Functions here modify the server time or assist other functions.
 */

public class LightCycleFunctions
{

    public static LightCycleFunctions instance;
    private World                     world;
    private MinecraftServer           minecraftserver;
    private GameRules                 gamerules;
    private IServerConfiguration      serverconfig;                                 // func_240793_aU_ is getIServerConfiguration();
    private IServerWorldInfo          worldinfo;                                    // func_230407_G_ is getIServerWorldInfo

    private int                       update_push_freq;

    private long                      curr_day_time;
    private long                      inc_time_by;

    private double                    new_cycle_in_minutes = 0;

    public final int                  DEFAULT_CYCLE_TIME   = 20;
    public final int                  TICKS_PER_DAY        = 24000;
    private final Logger              logger               = LogManager.getLogger();

    // Basic setup to do once the world loads
    // Setup the objects needed to modify the daytime, and set the gamerule
    // "doDaylightCycle" to false
    public LightCycleFunctions( WorldEvent.Load event )
    {
        instance        = this;
        this.world      = event.getWorld().getWorld();
        minecraftserver = this.world.getServer();
        serverconfig    = minecraftserver.func_240793_aU_(); // func_240793_aU_ is get_IServerConfiguration()
        worldinfo       = serverconfig.func_230407_G_();     // func_230407_G_ is get_IServerWorldInfo()
        gamerules       = minecraftserver.getGameRules();

        gamerules.get( GameRules.DO_DAYLIGHT_CYCLE ).set( false, minecraftserver );
        new_cycle_in_minutes = set_time_on_start();
        update_push_freq();
        set_inc_time_by( new_cycle_in_minutes );
    }

    // Tick the server time on server sided world tick.
    public void on_world_tick( ServerTickEvent event )
    {
        // Update the current time and then increment it
        curr_day_time = worldinfo.getDayTime();
        long gametime = worldinfo.getGameTime();
        if ( gametime % update_push_freq == 0 && gametime != 0 )
        {
            worldinfo.setDayTime( curr_day_time + (inc_time_by / (DEFAULT_CYCLE_TIME / update_push_freq)) );
            // logger.info("TIME: " + worldinfo.getDayTime());

            // This already happens every 20 ticks, so I want to do it whenever it is not
            // normally done
            if ( gametime % 20 != 0 )
                minecraftserver.getPlayerList().func_232642_a_( new SUpdateTimePacket( world.getGameTime(), world.getDayTime(), world.getGameRules().getBoolean( GameRules.DO_DAYLIGHT_CYCLE ) ),
                    world.func_234923_W_() );
        }
    }

    public void read_chat( ServerChatEvent event )
    {
        logger.info( "READ A CHAT MESSAGE." + event.getMessage() );
        logger.info( "USERNAME: " + event.getUsername() );
        logger.info( "MESSAGE : " + event.getMessage() );
    }

    // Updates how often the server pushes world SUpdateTimePackets to clients
    public void update_push_freq()
    {
        if ( new_cycle_in_minutes < 5 )
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
        // TODO: update read/write functions error catching in case of no file / bad
        // read uses this logic instead
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
            logger.info( "Could not read last increment speed from json: " + e );
        }
        return DEFAULT_CYCLE_TIME;
    }

    // Read the requested daylight cycle time in minutes and return the ratio of
    // that vs the default
    public double get_ticks_per_second( double new_cycle_length )
    {
        return (double)TICKS_PER_DAY / (60.0 * new_cycle_length);
    }

    // Registers chat commands with the server sided command manager. Allows for
    // command autocomplete and robust automatic error checking.
    public void register_server_commands( FMLServerStartingEvent event )
    {
        LightCommands.register( event.getCommandDispatcher() );
    }

    // Sets the daylight increment by taking a requested number in minutes and
    // converting it into server ticks per second
    public void set_inc_time_by( double new_cycle_in_minutes )
    {
        this.new_cycle_in_minutes = new_cycle_in_minutes;
        inc_time_by               = (long)get_ticks_per_second( new_cycle_in_minutes );
        DataStorage storage       = new DataStorage();
        storage.write_json( new_cycle_in_minutes );
        update_push_freq();
        logger.info( "(LOGGER) Set day length to " + new_cycle_in_minutes + " minutes." );
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

    public GameRules get_gamerules()
    {
        return this.gamerules;
    }

    public IServerConfiguration get_serverconfig()
    {
        return this.serverconfig;
    }

    public IServerWorldInfo get_worldinfo()
    {
        return this.worldinfo;
    }

    public int get_update_push_freq()
    {
        return this.update_push_freq;
    }

    public long get_curr_day_time()
    {
        return this.curr_day_time;
    }

    public long get_inc_time_by()
    {
        return this.inc_time_by;
    }

    public double get_new_cycle_in_minutes()
    {
        return this.new_cycle_in_minutes;
    }

    public void set_update_push_freq( int update_push_freq )
    {
        this.update_push_freq = update_push_freq;
    }

    public void set_inc_time_by( long inc_time_by )
    {
        this.inc_time_by = inc_time_by;
    }

    public void set_new_cycle_in_minutes( double new_cycle_in_minutes )
    {
        this.new_cycle_in_minutes = new_cycle_in_minutes;
    }
}