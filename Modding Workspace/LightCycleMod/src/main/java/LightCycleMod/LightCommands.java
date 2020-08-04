package LightCycleMod;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;

/*
 * Author	: Peter Caylor
 * Date		: 7/12/2020
 * Purpose	: Registers commands with the server. Allows for command auto-complete
 * 
 * Command format looks like this: "/lightcyclemod set <Light Cycle Duration: Minutes>
 *                                 "/<LightCycleMod.mod_id> set <DOUBLE>
 */
public class LightCommands
{

    static LightCycleFunctions functions = LightCycleMod.instance.handler.get_functions();
    static final int OP_PERMISSION_LEVEL = 2;
    static final int MIN_DAY_LENGTH      = -10000;
    static final int MAX_DAY_LENGTH      = 10000;

    public static void register( CommandDispatcher<CommandSource> dispatcher )
    {
        dispatcher.register(
            Commands.literal( LightCycleMod.mod_id ).then( Commands.literal( "set" ).requires( cs -> cs.hasPermissionLevel( OP_PERMISSION_LEVEL ) )
                .then( Commands.argument( "Light Cycle Duration: Minutes", DoubleArgumentType.doubleArg( MIN_DAY_LENGTH, MAX_DAY_LENGTH ) ).executes( ( context ) ->
                    {
                        return (int)set_light_cycle_length( context.getSource(), DoubleArgumentType.getDouble( context, "Light Cycle Duration: Minutes" ) );
                    } ) ) ) );
    }

    public static double set_light_cycle_length( CommandSource source, double new_length )
    {
        functions.set_inc_time_by( (double)new_length );
        source.sendFeedback( new StringTextComponent( "Set day length to " + new_length + " minutes." ), false );
        return new_length;
    }
}