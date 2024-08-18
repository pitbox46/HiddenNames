package github.pitbox46.hiddennames.data;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import java.util.function.Function;

public record Animation(String key, Function<Input, Return> renderer) {
    public record Input(Player player, Component ogName, Component displayName, long tick) {

    }

    public record Return(Component name, boolean show) {

    }
}
