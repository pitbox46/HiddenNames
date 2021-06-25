package github.pitbox46.hiddennames.commands;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import github.pitbox46.hiddennames.utils.AnimatedStringTextComponent;
import github.pitbox46.hiddennames.utils.CSVUtils;
import github.pitbox46.hiddennames.HiddenNames;
import github.pitbox46.hiddennames.network.PacketHandler;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.ColorArgument;
import net.minecraft.command.arguments.ComponentArgument;
import net.minecraft.command.arguments.GameProfileArgument;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.server.command.EnumArgument;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CommandSetName implements Command<CommandSource> {
    private static final Logger LOGGER = LogManager.getLogger();

    private static final CommandSetName CMD = new CommandSetName();
    private static final ResetName RESET_CMD = new ResetName();
    private static final SetName NAME_CMD = new SetName();
    private static final SetColor COLOR_CMD = new SetColor();
    private static final SetAnimation ANIMATION_CMD = new SetAnimation();

    public static ArgumentBuilder<CommandSource, ?> register(CommandDispatcher<CommandSource> dispatcher) {
        return Commands
                .literal("setName")
                .requires(cs -> cs.hasPermissionLevel(2))
                .then(Commands.argument("player", GameProfileArgument.gameProfile())
                        .then(Commands.argument("color", ColorArgument.color())
                                .then(Commands.argument("animation", EnumArgument.enumArgument(AnimatedStringTextComponent.Animation.class))
                                        .then(Commands.argument("name", ComponentArgument.component())
                                                .executes(CMD))))
                        .then(Commands.literal("name")
                                .then(Commands.argument("name", ComponentArgument.component())
                                        .executes(NAME_CMD)))
                        .then(Commands.literal("colour")
                                .then(Commands.argument("color", ColorArgument.color())
                                        .executes(COLOR_CMD)))
                        .then(Commands.literal("animation")
                                .then(Commands.argument("animation", EnumArgument.enumArgument(AnimatedStringTextComponent.Animation.class))
                                        .executes(ANIMATION_CMD)))
                        .then(Commands.literal("reset")
                                .executes(RESET_CMD))
                );
    }

    @Override
    public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
        for(GameProfile profile: GameProfileArgument.getGameProfiles(context, "player")) {
            CSVUtils.replaceEntry(HiddenNames.dataFile, profile.getName(),2, ComponentArgument.getComponent(context, "name").getString());
            CSVUtils.replaceEntry(HiddenNames.dataFile, profile.getName(),3, ColorArgument.getColor(context, "color").getFriendlyName());
            CSVUtils.replaceEntry(HiddenNames.dataFile, profile.getName(),5, context.getArgument("animation", AnimatedStringTextComponent.Animation.class).name());
        }
        CSVUtils.updateClients(HiddenNames.dataFile, PacketHandler.CHANNEL);
        return 0;
    }

    public static class ResetName implements Command<CommandSource>{
        @Override
        public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
            for(GameProfile profile: GameProfileArgument.getGameProfiles(context, "player")) {
                CSVUtils.replaceEntry(HiddenNames.dataFile, profile.getName(),2, profile.getName());
                CSVUtils.replaceEntry(HiddenNames.dataFile, profile.getName(),3, TextFormatting.WHITE.getFriendlyName());
                CSVUtils.replaceEntry(HiddenNames.dataFile, profile.getName(),5, AnimatedStringTextComponent.Animation.NONE.name());
            }
            CSVUtils.updateClients(HiddenNames.dataFile, PacketHandler.CHANNEL);
            return 0;
        }
    }

    public static class SetName implements Command<CommandSource>{
        @Override
        public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
            for(GameProfile profile: GameProfileArgument.getGameProfiles(context, "player")) {
                CSVUtils.replaceEntry(HiddenNames.dataFile, profile.getName(),2, ComponentArgument.getComponent(context, "name").getString());
            }
            CSVUtils.updateClients(HiddenNames.dataFile, PacketHandler.CHANNEL);
            return 0;
        }
    }

    public static class SetColor implements Command<CommandSource>{
        @Override
        public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
            for(GameProfile profile: GameProfileArgument.getGameProfiles(context, "player")) {
                CSVUtils.replaceEntry(HiddenNames.dataFile, profile.getName(),3, ColorArgument.getColor(context, "color").getFriendlyName());
            }
            CSVUtils.updateClients(HiddenNames.dataFile, PacketHandler.CHANNEL);
            return 0;
        }
    }

    public static class SetAnimation implements Command<CommandSource>{
        @Override
        public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
            LOGGER.debug(context.getArgument("animation", AnimatedStringTextComponent.Animation.class));
            AnimatedStringTextComponent.Animation animation = context.getArgument("animation", AnimatedStringTextComponent.Animation.class);
            if(animation == AnimatedStringTextComponent.Animation.HIDDEN) {
                for(GameProfile profile: GameProfileArgument.getGameProfiles(context, "player")) {
                    CSVUtils.replaceEntry(HiddenNames.dataFile, profile.getName(),4, "false");
                }
            } else {
                for (GameProfile profile : GameProfileArgument.getGameProfiles(context, "player")) {
                    CSVUtils.replaceEntry(HiddenNames.dataFile, profile.getName(), 5, animation.name());
                }
            }
            CSVUtils.updateClients(HiddenNames.dataFile, PacketHandler.CHANNEL);
            return 0;
        }
    }
}
