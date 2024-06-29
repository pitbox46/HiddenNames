package github.pitbox46.hiddennames.network;

import github.pitbox46.hiddennames.HiddenNames;
import github.pitbox46.hiddennames.data.Animations;
import github.pitbox46.hiddennames.data.NameData;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
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
            ByteBufCodecs.stringUtf8(256),
            packet -> packet.data().getAnimation().key(),
            (uuid, component, key) -> new NameDataSyncPacket(new NameData(uuid, component, Animations.getAnimation(key)))
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
