package github.pitbox46.hiddennames.commands;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import github.pitbox46.hiddennames.HiddenNames;
import github.pitbox46.hiddennames.network.PacketHandler;
import github.pitbox46.hiddennames.utils.AnimatedStringTextComponent;
import github.pitbox46.hiddennames.utils.CSVHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ColorArgument;
import net.minecraft.commands.arguments.ComponentArgument;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraftforge.server.command.EnumArgument;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CommandSetName implements Command<CommandSourceStack> {
    private static final Logger LOGGER = LogManager.getLogger();

    private static final CommandSetName CMD = new CommandSetName();
    private static final ResetName RESET_CMD = new ResetName();
    private static final SetName NAME_CMD = new SetName();
    private static final SetColor COLOR_CMD = new SetColor();
    private static final SetAnimation ANIMATION_CMD = new SetAnimation();

    public static ArgumentBuilder<CommandSourceStack, ?> register(CommandDispatcher<CommandSourceStack> dispatcher) {
        return Commands
                .literal("setName")
                .requires(cs -> cs.hasPermission(2))
                .then(Commands.argument("player", GameProfileArgument.gameProfile())
                        .then(Commands.argument("color", ColorArgument.color())
                                .then(Commands.argument("animation", EnumArgument.enumArgument(AnimatedStringTextComponent.Animation.class))
                                        .then(Commands.argument("name", ComponentArgument.textComponent())
                                                .executes(CMD))))
                        .then(Commands.literal("name")
                                .then(Commands.argument("name", ComponentArgument.textComponent())
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
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        for(GameProfile profile: GameProfileArgument.getGameProfiles(context, "player")) {
            CSVHandler.replaceEntry(HiddenNames.dataFile, profile.getName(), CSVHandler.Columns.DISPLAY_NAME, ComponentArgument.getComponent(context, "name").getString());
            CSVHandler.replaceEntry(HiddenNames.dataFile, profile.getName(), CSVHandler.Columns.NAME_COLOR, ColorArgument.getColor(context, "color").getName());
            CSVHandler.replaceEntry(HiddenNames.dataFile, profile.getName(), CSVHandler.Columns.ANIMATION, context.getArgument("animation", AnimatedStringTextComponent.Animation.class).name());
        }
        CSVHandler.updateClients(HiddenNames.dataFile, PacketHandler.CHANNEL);
        return 0;
    }

    public static class ResetName implements Command<CommandSourceStack>{
        @Override
        public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
            for(GameProfile profile: GameProfileArgument.getGameProfiles(context, "player")) {
                CSVHandler.replaceEntry(HiddenNames.dataFile, profile.getName(), CSVHandler.Columns.DISPLAY_NAME, profile.getName());
                CSVHandler.replaceEntry(HiddenNames.dataFile, profile.getName(), CSVHandler.Columns.NAME_COLOR, ChatFormatting.WHITE.getName());
                CSVHandler.replaceEntry(HiddenNames.dataFile, profile.getName(), CSVHandler.Columns.ANIMATION, AnimatedStringTextComponent.Animation.NONE.name());
            }
            CSVHandler.updateClients(HiddenNames.dataFile, PacketHandler.CHANNEL);
            return 0;
        }
    }

    public static class SetName implements Command<CommandSourceStack>{
        @Override
        public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
            for(GameProfile profile: GameProfileArgument.getGameProfiles(context, "player")) {
                CSVHandler.replaceEntry(HiddenNames.dataFile, profile.getName(), CSVHandler.Columns.DISPLAY_NAME, ComponentArgument.getComponent(context, "name").getString());
            }
            CSVHandler.updateClients(HiddenNames.dataFile, PacketHandler.CHANNEL);
            return 0;
        }
    }

    public static class SetColor implements Command<CommandSourceStack>{
        @Override
        public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
            for(GameProfile profile: GameProfileArgument.getGameProfiles(context, "player")) {
                CSVHandler.replaceEntry(HiddenNames.dataFile, profile.getName(), CSVHandler.Columns.NAME_COLOR, ColorArgument.getColor(context, "color").getName());
            }
            CSVHandler.updateClients(HiddenNames.dataFile, PacketHandler.CHANNEL);
            return 0;
        }
    }

    public static class SetAnimation implements Command<CommandSourceStack>{
        @Override
        public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
            LOGGER.debug(context.getArgument("animation", AnimatedStringTextComponent.Animation.class));
            AnimatedStringTextComponent.Animation animation = context.getArgument("animation", AnimatedStringTextComponent.Animation.class);
            if(animation == AnimatedStringTextComponent.Animation.HIDDEN) {
                for(GameProfile profile: GameProfileArgument.getGameProfiles(context, "player")) {
                    CSVHandler.replaceEntry(HiddenNames.dataFile, profile.getName(), CSVHandler.Columns.NAME_VISIBLE, "false");
                }
            } else {
                for (GameProfile profile : GameProfileArgument.getGameProfiles(context, "player")) {
                    CSVHandler.replaceEntry(HiddenNames.dataFile, profile.getName(), CSVHandler.Columns.ANIMATION, animation.name());
                }
            }
            CSVHandler.updateClients(HiddenNames.dataFile, PacketHandler.CHANNEL);
            return 0;
        }
    }
}
