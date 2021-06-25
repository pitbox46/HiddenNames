package github.pitbox46.hiddennames.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import github.pitbox46.hiddennames.Config;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;

public class CommandSetDefault implements Command<CommandSource> {

    private static final CommandSetDefault CMD = new CommandSetDefault();

    public static ArgumentBuilder<CommandSource, ?> register(CommandDispatcher<CommandSource> dispatcher) {
        return Commands
                .literal("defaultVisible")
                .requires(cs -> cs.hasPermissionLevel(2))
                .then(Commands.argument("boolean", BoolArgumentType.bool())
                .executes(CMD));
    }

    @Override
    public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
        Config.DEFAULT_VISIBLE.set(BoolArgumentType.getBool(context, "boolean"));
        Config.DEFAULT_VISIBLE.save();
        return 0;
    }
}
