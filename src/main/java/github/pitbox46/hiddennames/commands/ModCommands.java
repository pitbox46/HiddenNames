package github.pitbox46.hiddennames.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import github.pitbox46.hiddennames.HiddenNames;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class ModCommands {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
        LiteralCommandNode<CommandSourceStack> cmdTut = dispatcher.register(
                Commands.literal(HiddenNames.MODID)
                        .then(CommandSetVisible.register(dispatcher, context))
                        .then(CommandSetDefault.register(dispatcher, context))
                        .then(CommandSetName.register(dispatcher, context))
                        .then(CommandBlocksHide.register(dispatcher, context))
        );

        dispatcher.register(Commands.literal("hiddennames").redirect(cmdTut));
    }
}
