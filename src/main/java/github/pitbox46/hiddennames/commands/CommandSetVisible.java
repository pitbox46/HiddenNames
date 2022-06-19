package github.pitbox46.hiddennames.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import github.pitbox46.hiddennames.data.Animations;
import github.pitbox46.hiddennames.data.NameData;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.world.entity.player.Player;

public class CommandSetVisible {
    public static ArgumentBuilder<CommandSourceStack, ?> register(CommandDispatcher<CommandSourceStack> dispatcher) {
        return Commands
                .literal("nameplateVisible")
                .requires(cs -> cs.hasPermission(2))
                .then(Commands.argument("player", EntityArgument.players())
                        .then(Commands.argument("boolean", BoolArgumentType.bool())
                                .executes(ctx -> {
                                    for (Player player : EntityArgument.getPlayers(ctx, "players")) {
                                        boolean flag = ctx.getArgument("boolean", Boolean.class);
                                        NameData data = NameData.DATA.get(player.getUUID());
                                        if (!flag) {
                                            data.setAnimation(Animations.HIDDEN);
                                        } else if (data.getAnimation() == Animations.HIDDEN) {
                                            data.setAnimation(Animations.NULL);
                                        }
                                    }
                                    NameData.sendSyncData();
                                    return 0;
                                })));
    }
}
