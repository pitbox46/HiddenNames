package github.pitbox46.hiddennames.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import github.pitbox46.hiddennames.Config;
import github.pitbox46.hiddennames.data.Animation;
import github.pitbox46.hiddennames.data.NameData;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ColorArgument;
import net.minecraft.commands.arguments.ComponentArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Function;

public class CommandSetName {
    public static ArgumentBuilder<CommandSourceStack, ?> register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
        return buildSetName(
                Commands.literal("setName")
                        .requires(cs -> cs.hasPermission(Config.CHANGE_OWN_NAME_LEVEL.get())),
                ctx -> ctx.getSource().getPlayer()==null ? Collections.emptySet():Collections.singleton(ctx.getSource().getPlayer()),
                context
        ).then(buildSetName(
                Commands.argument("players1", EntityArgument.players())
                        .requires(cs -> cs.hasPermission(2)),
                ctx -> {
                    try {
                        return EntityArgument.getPlayers(ctx, "players1");
                    } catch (CommandSyntaxException e) {
                        throw new RuntimeException(e);
                    }
                },
                context
        ));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> buildSetName(ArgumentBuilder<CommandSourceStack, ?> builder, Function<CommandContext<CommandSourceStack>, Collection<ServerPlayer>> getPlayers, CommandBuildContext context) {
        return builder.then(Commands.argument("color1", ColorArgument.color())
                        .then(Commands.argument("animation1", AnimationArgument.animationArgument())
                                .then(Commands.argument("name1", ComponentArgument.textComponent(context))
                                        .executes(ctx -> {
                                            for (Player player : getPlayers.apply(ctx)) {
                                                MutableComponent displayName = ctx.getArgument("name1", Component.class).copy().withStyle(ctx.getArgument("color1", ChatFormatting.class));
                                                NameData.DATA.put(player.getUUID(), new NameData(player.getUUID(), displayName, ctx.getArgument("animation1", Animation.class)));
                                            }
                                            NameData.sendSyncData();
                                            return 0;
                                        }))))
                .then(Commands.literal("name")
                        .then(Commands.argument("name1", ComponentArgument.textComponent(context))
                                .executes(ctx -> {
                                    for (Player player : getPlayers.apply(ctx)) {
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
                                    for (Player player : getPlayers.apply(ctx)) {
                                        Component previous = NameData.DATA.get(player.getUUID()).getDisplayName();
                                        Component newName = Component
                                                .literal(previous.getString())
                                                .withStyle(ctx.getArgument("color1", ChatFormatting.class));
                                        NameData.DATA.get(player.getUUID()).setDisplayName(newName);
                                    }
                                    NameData.sendSyncData();
                                    return 0;
                                })))
                .then(Commands.literal("animation")
                        .then(Commands.argument("animation1", AnimationArgument.animationArgument())
                                .executes(ctx -> {
                                    for (Player player : getPlayers.apply(ctx)) {
                                        NameData.DATA.get(player.getUUID()).setAnimation(ctx.getArgument("animation1", Animation.class));
                                    }
                                    NameData.sendSyncData();
                                    return 0;
                                })))
                .then(Commands.literal("reset")
                        .executes(ctx -> {
                            for (Player player : getPlayers.apply(ctx)) {
                                NameData.DATA.put(player.getUUID(), new NameData(player));
                            }
                            NameData.sendSyncData();
                            return 0;
                        }));
    }
}
