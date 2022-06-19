package github.pitbox46.hiddennames.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import github.pitbox46.hiddennames.Config;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class CommandSetDefault implements Command<CommandSourceStack> {

    private static final CommandSetDefault CMD = new CommandSetDefault();

    public static ArgumentBuilder<CommandSourceStack, ?> register(CommandDispatcher<CommandSourceStack> dispatcher) {
        return Commands
                .literal("defaultVisible")
                .requires(cs -> cs.hasPermission(2))
                .then(Commands.argument("boolean", BoolArgumentType.bool())
                        .executes(CMD));
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Config.DEFAULT_VISIBLE.set(BoolArgumentType.getBool(context, "boolean"));
        Config.DEFAULT_VISIBLE.save();
        return 0;
    }
}
