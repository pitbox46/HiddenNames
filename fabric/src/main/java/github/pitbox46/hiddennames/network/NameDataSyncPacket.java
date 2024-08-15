package github.pitbox46.hiddennames.network;

import github.pitbox46.hiddennames.HiddenNames;
import github.pitbox46.hiddennames.data.NameData;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record NameDataSyncPacket(NameData data) implements CustomPacketPayload {
    public static final Type<NameDataSyncPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(HiddenNames.MODID, "name_data_sync"));
    public static final StreamCodec<RegistryFriendlyByteBuf, NameDataSyncPacket> CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC,
            packet -> packet.data().getUuid(),
            ComponentSerialization.STREAM_CODEC,
            packet -> packet.data().getDisplayName(),
            ResourceLocation.STREAM_CODEC,
            packet -> packet.data().getAnimation().key(),
            (uuid, component, key) -> new NameDataSyncPacket(new NameData(uuid, component, HiddenNames.ANIMATION_REGISTRY.get(key)))
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
