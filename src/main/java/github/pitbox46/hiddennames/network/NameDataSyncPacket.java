package github.pitbox46.hiddennames.network;

import github.pitbox46.hiddennames.data.Animations;
import github.pitbox46.hiddennames.data.NameData;
import net.minecraft.network.FriendlyByteBuf;

import java.util.function.Function;

public class NameDataSyncPacket {
    public NameData data;

    public NameDataSyncPacket(NameData data) {
        this.data = data;
    }

    public static Function<FriendlyByteBuf, NameDataSyncPacket> decoder() {
        return buf -> new NameDataSyncPacket(new NameData(buf.readUUID(), buf.readComponent(), Animations.getAnimation(buf.readUtf())));
    }

    public void encoder(FriendlyByteBuf buf) {
        buf.writeUUID(data.getUuid());
        buf.writeComponent(data.getDisplayName());
        buf.writeUtf(data.getAnimation().key());
    }
}
