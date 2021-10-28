package github.pitbox46.hiddennames.network;

import github.pitbox46.hiddennames.utils.AnimatedStringTextComponent;
import net.minecraft.network.chat.Component;

import java.util.UUID;

public class NamePacket {
    public final UUID uuid;
    public final Component name;
    public final AnimatedStringTextComponent.Animation anime;
    public NamePacket(UUID uuid, Component name, AnimatedStringTextComponent.Animation anime) {
        this.uuid = uuid;
        this.name = name;
        this.anime = anime;
    }
}
