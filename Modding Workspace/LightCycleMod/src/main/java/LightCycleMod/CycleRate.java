package LightCycleMod;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;

public class CycleRate implements Command<CommandSource>{

	static LightCycleFunctions 		functions = LightCycleMod.instance.handler.get_functions();
	private static final CycleRate 	CMD = new CycleRate();
	public static int 				light_cycle_length;
	
	public static ArgumentBuilder<CommandSource, ?> register(CommandDispatcher<CommandSource> dispatcher)
	{
		dispatcher.register(
				Commands.literal("set")
					.requires( cs -> cs.hasPermissionLevel(2))
					.then(
							Commands.argument("<Full Day Length: Minutes>", IntegerArgumentType.integer(0, 2880)).executes(
										(context) -> {
											return set_light_cycle_length(context.getSource(), IntegerArgumentType.getInteger(context, "<Full Day Length: Minutes>"));
										}
									)
							
					)
					.executes(CMD)
		);
		
		/*
		return Commands.literal("set")
			.then(Commands.argument("<Light Cycle Duration>", IntegerArgumentType.integer(0, 1000000)).executes((commandcontext) -> {
		         //return set_day_length(commandcontext.getSource(), IntegerArgumentType.getInteger(commandcontext, "duration"));
				return (set_light_cycle_length(commandcontext.getSource(), IntegerArgumentType.getInteger(commandcontext, "<Light Cycle Duration>")));
		    })
			.requires(cs -> cs.hasPermissionLevel(2));
		*/
	}

	@Override
	public int run(CommandContext<CommandSource> context) throws CommandSyntaxException 
	{
		functions.set_inc_time_by((double)light_cycle_length);
		context.getSource().sendFeedback( new StringTextComponent("Set day length to " + light_cycle_length + " minutes."), false);
		return 0;
	}
	
	
	public static int set_light_cycle_length(CommandSource source, int new_length )
	{
		light_cycle_length = new_length;
		source.sendFeedback( new StringTextComponent("(NOT IN RUN)Set day length to " + light_cycle_length + " minutes."), false);
		return new_length;
	}
	
	
}
