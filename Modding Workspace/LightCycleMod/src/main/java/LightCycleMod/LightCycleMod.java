package LightCycleMod;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;

/*
 * A simple mod that manages the light cycle for MC 1.16.1 on a dedicated server
 */

@Mod("lightcyclemod")
public class LightCycleMod 
{
	//Allows us to reference our main mod class from outside the main mod class
	public 	static 			LightCycleMod instance;
	public 	static final 	String mod_id = "lightcyclemod";
	public static final 	Logger logger = LogManager.getLogger(mod_id);
	public 					ServerWorld serverworld;
	//Register the functions for ForgeModLoader
	public LightCycleMod()
	{
		//Registers these classes and all functions with the @SubscribeEvent the FML event bus, so they will run when we load the mod
		instance = this;
		LightEventHandler event = new LightEventHandler();
		MinecraftForge.EVENT_BUS.register(event);
	}
}
