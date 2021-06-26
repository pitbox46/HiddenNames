package github.pitbox46.hiddennames.network;

import github.pitbox46.hiddennames.utils.AnimatedStringTextComponent;
import net.minecraft.util.text.ITextComponent;

import java.util.UUID;

public class NamePacket {
    public final UUID uuid;
    public final ITextComponent name;
    public final AnimatedStringTextComponent.Animation anime;
    public NamePacket(UUID uuid, ITextComponent name, AnimatedStringTextComponent.Animation anime) {
        this.uuid = uuid;
        this.name = name;
        this.anime = anime;
    }
}
