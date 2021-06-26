package github.pitbox46.hiddennames.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import github.pitbox46.hiddennames.Config;
import github.pitbox46.hiddennames.network.BlocksHidePacket;
import github.pitbox46.hiddennames.network.PacketHandler;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraftforge.fml.network.PacketDistributor;

public class CommandBlocksHide implements Command<CommandSource> {

    private static final CommandBlocksHide CMD = new CommandBlocksHide();

    public static ArgumentBuilder<CommandSource, ?> register(CommandDispatcher<CommandSource> dispatcher) {
        return Commands
                .literal("blocksHideName")
                .requires(cs -> cs.hasPermissionLevel(2))
                .then(Commands.argument("boolean", BoolArgumentType.bool())
                        .executes(CMD));
    }

    @Override
    public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
        Config.BLOCKS_HIDE.set(BoolArgumentType.getBool(context, "boolean"));
        Config.BLOCKS_HIDE.save();
        PacketHandler.CHANNEL.send(PacketDistributor.ALL.noArg(), new BlocksHidePacket(Config.BLOCKS_HIDE.get()));
        return 0;
    }
}