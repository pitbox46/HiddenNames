package github.pitbox46.hiddennames.network;

import net.minecraft.util.text.ITextComponent;

import java.awt.*;
import java.util.UUID;

public class NamePacket {
    public final UUID uuid;
    public final boolean bool;
    public final ITextComponent name;
    public NamePacket(UUID uuid, boolean bool, ITextComponent name) {
        this.uuid = uuid;
        this.bool = bool;
        this.name = name;
    }
}
