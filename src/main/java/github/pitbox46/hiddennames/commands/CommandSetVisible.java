package github.pitbox46.hiddennames.commands;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import github.pitbox46.hiddennames.HiddenNames;
import github.pitbox46.hiddennames.network.PacketHandler;
import github.pitbox46.hiddennames.utils.CSVHandler;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.GameProfileArgument;

public class CommandSetVisible implements Command<CommandSourceStack> {

    private static final CommandSetVisible CMD = new CommandSetVisible();

    public static ArgumentBuilder<CommandSourceStack, ?> register(CommandDispatcher<CommandSourceStack> dispatcher) {
        return Commands
                .literal("nameplateVisible")
                .requires(cs -> cs.hasPermission(2))
                .then(Commands.argument("player", GameProfileArgument.gameProfile())
                        .then(Commands.argument("boolean", BoolArgumentType.bool())
                                .executes(CMD)));
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        for(GameProfile profile: GameProfileArgument.getGameProfiles(context, "player")) {
            CSVHandler.replaceEntry(HiddenNames.dataFile, profile.getName(), CSVHandler.Columns.NAME_VISIBLE, String.valueOf(BoolArgumentType.getBool(context, "boolean")));
        }
        CSVHandler.updateClients(HiddenNames.dataFile, PacketHandler.CHANNEL);
        return 0;
    }
}
