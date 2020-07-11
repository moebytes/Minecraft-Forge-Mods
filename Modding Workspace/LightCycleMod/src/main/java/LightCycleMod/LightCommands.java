package LightCycleMod;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.TimeArgument;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;

public class LightCommands extends Commands{

	static LightCycleFunctions functions;
	
	public LightCommands(CommandDispatcher<CommandSource> dispatcher, LightCycleFunctions light_cycle_functions)
	{
		super( Commands.EnvironmentType.DEDICATED );
		functions = light_cycle_functions;
		LightCommands.register(dispatcher);
	}
	
	public static void register(CommandDispatcher<CommandSource> dispatcher)
	{
		dispatcher.register(Commands.literal("time").requires((p_198828_0_) -> {
			return p_198828_0_.hasPermissionLevel(2);
		}).then(Commands.argument("set", TimeArgument.func_218091_a()).executes((p_200564_0_) -> {
			return set_speed(p_200564_0_.getSource(), IntegerArgumentType.getInteger(p_200564_0_, "set"));
		})));
	}
	
	
	public static int set_speed(CommandSource source, int time)
	{
		for(ServerWorld serverworld : source.getServer().getWorlds())
			serverworld.func_241114_a_((long)time);
		
		source.sendFeedback(new TranslationTextComponent("commands.lightcycle.set", time), true);
		return (int)functions.get_inc_time_by();
	}
}