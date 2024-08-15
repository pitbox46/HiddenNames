package github.pitbox46.hiddennames.network;

import github.pitbox46.hiddennames.HiddenNames;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record BlocksHidePacket(boolean blocksHide) implements CustomPacketPayload {
    public static final Type<BlocksHidePacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(HiddenNames.MODID, "blocks_hide"));
    public static final StreamCodec<FriendlyByteBuf, BlocksHidePacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL,
            BlocksHidePacket::blocksHide,
            BlocksHidePacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
