package LightCycleMod;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;

public class LightCommands{

	public static void register(CommandDispatcher<CommandSource> dispatcher)
	{
		LiteralCommandNode<CommandSource> light_cmd = dispatcher.register(
				Commands.literal(LightCycleMod.mod_id)
				.then(CycleRate.register(dispatcher))
		);
		
		dispatcher.register(Commands.literal("light").redirect(light_cmd));
	}
	
}