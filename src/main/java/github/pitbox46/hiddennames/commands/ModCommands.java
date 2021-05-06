package github.pitbox46.hiddennames.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import github.pitbox46.hiddennames.HiddenNames;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;

public class ModCommands {

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        LiteralCommandNode<CommandSource> cmdTut = dispatcher.register(
                Commands.literal(HiddenNames.MODID)
                        .then(CommandSetVisible.register(dispatcher))
        );

        dispatcher.register(Commands.literal("hiddennames").redirect(cmdTut));
    }
}
