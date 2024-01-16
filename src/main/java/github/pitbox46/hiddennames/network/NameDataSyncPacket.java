package github.pitbox46.hiddennames.network;

import github.pitbox46.hiddennames.HiddenNames;
import github.pitbox46.hiddennames.data.Animations;
import github.pitbox46.hiddennames.data.NameData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record NameDataSyncPacket(NameData data) implements CustomPacketPayload {
    public NameDataSyncPacket(FriendlyByteBuf buf) {
        this(new NameData(buf.readUUID(), buf.readComponent(), Animations.getAnimation(buf.readUtf())));
    }

    @Override
    public void write(FriendlyByteBuf pBuffer) {
        pBuffer.writeUUID(data.getUuid());
        pBuffer.writeComponent(data.getDisplayName());
        pBuffer.writeUtf(data.getAnimation().key());
    }

    public static final ResourceLocation ID = new ResourceLocation(HiddenNames.MODID, "name_data_sync");

    @Override
    public ResourceLocation id() {
        return ID;
    }
}
