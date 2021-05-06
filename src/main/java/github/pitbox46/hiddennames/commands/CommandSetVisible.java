package github.pitbox46.hiddennames.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import github.pitbox46.hiddennames.Config;
import github.pitbox46.hiddennames.network.BooleanPacket;
import github.pitbox46.hiddennames.network.PacketHandler;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraftforge.fml.network.PacketDistributor;

public class CommandSetVisible implements Command<CommandSource> {

    private static final CommandSetVisible CMD = new CommandSetVisible();

    public static ArgumentBuilder<CommandSource, ?> register(CommandDispatcher<CommandSource> dispatcher) {
        return Commands
                .literal("nameplatesvisible")
                .requires(cs -> cs.hasPermissionLevel(2))
                .then(Commands.argument("boolean", BoolArgumentType.bool())
                .executes(CMD));
    }

    @Override
    public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
        Config.NAMES_VISIBLE.set(BoolArgumentType.getBool(context, "boolean"));
        Config.NAMES_VISIBLE.save();
        PacketHandler.CHANNEL.send(PacketDistributor.ALL.noArg(), new BooleanPacket(BooleanPacket.Type.SET_ALL, Config.NAMES_VISIBLE.get()));
        return 0;
    }
}
