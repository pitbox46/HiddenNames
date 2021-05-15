package github.pitbox46.hiddennames.commands;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import github.pitbox46.hiddennames.CSVUtils;
import github.pitbox46.hiddennames.HiddenNames;
import github.pitbox46.hiddennames.network.PacketHandler;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.ColorArgument;
import net.minecraft.command.arguments.ComponentArgument;
import net.minecraft.command.arguments.GameProfileArgument;
import net.minecraft.util.text.TextFormatting;

public class CommandSetName implements Command<CommandSource> {

    private static final CommandSetName CMD = new CommandSetName();
    private static final ResetName RESET_CMD = new ResetName();

    public static ArgumentBuilder<CommandSource, ?> register(CommandDispatcher<CommandSource> dispatcher) {
        return Commands
                .literal("setName")
                .requires(cs -> cs.hasPermissionLevel(2))
                .then(Commands.argument("player", GameProfileArgument.gameProfile())
                        .then(Commands.argument("color", ColorArgument.color())
                                .then(Commands.argument("name", ComponentArgument.component())
                                        .executes(CMD)))
                        .then(Commands.literal("reset")
                                .executes(RESET_CMD))
                );
    }

    @Override
    public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
        for(GameProfile profile: GameProfileArgument.getGameProfiles(context, "player")) {
            CSVUtils.replaceEntry(HiddenNames.config, profile.getName(),2, ComponentArgument.getComponent(context, "name").getString());
            CSVUtils.replaceEntry(HiddenNames.config, profile.getName(),3, ColorArgument.getColor(context, "color").getFriendlyName());
        }
        CSVUtils.updateClients(HiddenNames.config, PacketHandler.CHANNEL);
        return 0;
    }

    public static class ResetName implements Command<CommandSource>{
        @Override
        public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
            for(GameProfile profile: GameProfileArgument.getGameProfiles(context, "player")) {
                CSVUtils.replaceEntry(HiddenNames.config, profile.getName(),2, profile.getName());
                CSVUtils.replaceEntry(HiddenNames.config, profile.getName(),3, TextFormatting.WHITE.getFriendlyName());
            }
            CSVUtils.updateClients(HiddenNames.config, PacketHandler.CHANNEL);
            return 0;
        }
    }
}
