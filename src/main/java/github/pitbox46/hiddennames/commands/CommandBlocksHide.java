package github.pitbox46.hiddennames.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import github.pitbox46.hiddennames.Config;
import github.pitbox46.hiddennames.network.BlocksHidePacket;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.neoforged.neoforge.network.PacketDistributor;

public class CommandBlocksHide implements Command<CommandSourceStack> {

    private static final CommandBlocksHide CMD = new CommandBlocksHide();

    public static ArgumentBuilder<CommandSourceStack, ?> register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
        return Commands
                .literal("blocksHideName")
                .requires(cs -> cs.hasPermission(2))
                .then(Commands.argument("boolean", BoolArgumentType.bool())
                        .executes(CMD));
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Config.BLOCKS_HIDE.set(BoolArgumentType.getBool(context, "boolean"));
        Config.BLOCKS_HIDE.save();
        PacketDistributor.sendToAllPlayers(new BlocksHidePacket(Config.BLOCKS_HIDE.get()));
        return 0;
    }
}