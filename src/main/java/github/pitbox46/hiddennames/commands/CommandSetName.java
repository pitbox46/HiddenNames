package github.pitbox46.hiddennames.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import github.pitbox46.hiddennames.data.Animation;
import github.pitbox46.hiddennames.data.NameData;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ColorArgument;
import net.minecraft.commands.arguments.ComponentArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Player;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CommandSetName {
    private static final Logger LOGGER = LogManager.getLogger();

    public static ArgumentBuilder<CommandSourceStack, ?> register(CommandDispatcher<CommandSourceStack> dispatcher) {
        return Commands
                .literal("setName")
                .requires(cs -> cs.hasPermission(2))
                .then(Commands.argument("players1", EntityArgument.players())
                        .then(Commands.argument("color1", ColorArgument.color())
                                .then(Commands.argument("animation1", AnimationArgument.animationArgument())
                                        .then(Commands.argument("name1", ComponentArgument.textComponent())
                                                .executes(ctx -> {
                                                    for (Player player : EntityArgument.getPlayers(ctx, "players1")) {
                                                        MutableComponent displayName = ctx.getArgument("name1", Component.class).copy().withStyle(ctx.getArgument("color1", ChatFormatting.class));
                                                        NameData.DATA.put(player.getUUID(), new NameData(player.getUUID(), displayName, ctx.getArgument("animation1", Animation.class)));
                                                    }
                                                    NameData.sendSyncData();
                                                    return 0;
                                                }))))
                        .then(Commands.literal("name")
                                .then(Commands.argument("name1", ComponentArgument.textComponent())
                                        .executes(ctx -> {
                                            for (Player player : EntityArgument.getPlayers(ctx, "players1")) {
                                                Component previous = NameData.DATA.get(player.getUUID()).getDisplayName();
                                                Component newName = ctx.getArgument("name1", Component.class)
                                                        .plainCopy()
                                                        .withStyle(previous.getStyle());
                                                NameData.DATA.get(player.getUUID()).setDisplayName(newName);
                                            }
                                            NameData.sendSyncData();
                                            return 0;
                                        })))
                        .then(Commands.literal("color")
                                .then(Commands.argument("color1", ColorArgument.color())
                                        .executes(ctx -> {
                                            for (Player player : EntityArgument.getPlayers(ctx, "players1")) {
                                                Component previous = NameData.DATA.get(player.getUUID()).getDisplayName();
                                                Component newName = new TextComponent(previous.getString())
                                                        .withStyle(ctx.getArgument("color1", ChatFormatting.class));
                                                NameData.DATA.get(player.getUUID()).setDisplayName(newName);
                                            }
                                            NameData.sendSyncData();
                                            return 0;
                                        })))
                        .then(Commands.literal("animation")
                                .then(Commands.argument("animation1", AnimationArgument.animationArgument())
                                        .executes(ctx -> {
                                            for (Player player : EntityArgument.getPlayers(ctx, "players1")) {
                                                NameData.DATA.get(player.getUUID()).setAnimation(ctx.getArgument("animation1", Animation.class));
                                            }
                                            NameData.sendSyncData();
                                            return 0;
                                        })))
                        .then(Commands.literal("reset")
                                .executes(ctx -> {
                                    for (Player player : EntityArgument.getPlayers(ctx, "players1")) {
                                        NameData.DATA.put(player.getUUID(), new NameData(player));
                                    }
                                    NameData.sendSyncData();
                                    return 0;
                                }))
                );
    }
}
