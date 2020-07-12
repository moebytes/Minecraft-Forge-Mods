package LightCycleMod;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;

public class LightCommands{

	static LightCycleFunctions functions = LightCycleMod.instance.handler.get_functions();
	public static void register(CommandDispatcher<CommandSource> dispatcher)
	{
		dispatcher.register(
			Commands.literal(LightCycleMod.mod_id)
			.then(
				Commands.literal("set")
				.requires( cs -> cs.hasPermissionLevel(2))
				.then(
					Commands.argument("<Full Day Length: Minutes>", DoubleArgumentType.doubleArg(-10000, 10000))
					.executes((context) -> {
						return (int) set_light_cycle_length(context.getSource(), DoubleArgumentType.getDouble(context, "<Full Day Length: Minutes>"));
						}
					)
				)
			)
		);
	}
	
	public static double set_light_cycle_length(CommandSource source, double new_length )
	{
		functions.set_inc_time_by((double)new_length);
		source.sendFeedback( new StringTextComponent("Set day length to " + new_length + " minutes."), false);
		return new_length;
	}
}