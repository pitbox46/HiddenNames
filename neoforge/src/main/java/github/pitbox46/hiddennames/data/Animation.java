package github.pitbox46.hiddennames.data;

import github.pitbox46.hiddennames.HiddenNames;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.function.Function;

public record Animation(ResourceLocation key, Function<Input, Return> renderer) {
    public static final ResourceLocation HIDDEN_KEY = ResourceLocation.fromNamespaceAndPath(HiddenNames.MODID, "hidden");

    public boolean isHidden() {
        return key().equals(HIDDEN_KEY);
    }

    public record Input(Player player, Component ogName, Component displayName, long tick) {

    }

    public record Return(Component name, boolean show) {

    }
}
