package LightCycleMod;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent.ServerTickEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;

/*
 * A simple mod that manages the light cycle for MC 1.16.1 on a dedicated server
 */

@Mod( "lightcyclemod" )
public class LightCycleMod
{
    // Allows us to reference our main mod class from outside the main mod class
    public static LightCycleMod instance;
    public static final String  mod_id  = "lightcyclemod";
    public LightEventHandler    handler = new LightEventHandler();

    // Register the functions for ForgeModLoader
    public LightCycleMod()
    {
        // Registers these classes and all functions with the @SubscribeEvent the FML
        // event bus, so they will run when we load the mod
        instance = this;
        MinecraftForge.EVENT_BUS.register( handler );
    }

}

/*
 * Events should happen in this order every time:
 *      1) Server starts, on_start() is fired
 *      2) Server world is loaded. on_load() is fired
 *      3) World begins ticking after the server is started and the world is loaded. on_world_tick() is fired.
 */
@Mod.EventBusSubscriber
//Handle events and then run the appropriate functions based on those events
class LightEventHandler
{

    private LightCycleFunctions functions;

    @SubscribeEvent
    public void on_start( FMLServerStartingEvent event )
    {
        functions.register_server_commands( event );
    }

    @SubscribeEvent
    public void on_load( WorldEvent.Load event )
    {
        if ( functions == null )
            functions = new LightCycleFunctions( event );
    }

    // Tick the server time on teach server sided world tick.
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
