package github.pitbox46.hiddennames.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import github.pitbox46.hiddennames.HiddenNames;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class ModCommands {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralCommandNode<CommandSourceStack> cmdTut = dispatcher.register(
                Commands.literal(HiddenNames.MODID)
                        .then(CommandSetVisible.register(dispatcher))
                        .then(CommandSetDefault.register(dispatcher))
                        .then(CommandSetName.register(dispatcher))
                        .then(CommandBlocksHide.register(dispatcher))
        );

        dispatcher.register(Commands.literal("hiddennames").redirect(cmdTut));
    }
}
