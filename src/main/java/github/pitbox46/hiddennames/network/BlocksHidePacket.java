package github.pitbox46.hiddennames.network;

import github.pitbox46.hiddennames.HiddenNames;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record BlocksHidePacket(boolean blocksHide) implements CustomPacketPayload {

    public BlocksHidePacket(FriendlyByteBuf buf) {
        this(buf.readBoolean());
    }

    @Override
    public void write(FriendlyByteBuf pBuffer) {
        pBuffer.writeBoolean(blocksHide);
    }

    public static final ResourceLocation ID = new ResourceLocation(HiddenNames.MODID, "blocks_hide");

    @Override
    public ResourceLocation id() {
        return ID;
    }
}
