package github.pitbox46.hiddennames.network;

import github.pitbox46.hiddennames.HiddenNames;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fmllegacy.network.NetworkRegistry;
import net.minecraftforge.fmllegacy.network.simple.SimpleChannel;

public class PacketHandler {
    private static final String PROTOCOL_VERSION = "3.2.1";
    public static SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation("hiddennames", "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals);
    private static int ID = 0;

    public static void init() {
        CHANNEL.registerMessage(
                ID++,
                BlocksHidePacket.class,
                (msg, pb) -> {
                    pb.writeBoolean(msg.blocksHide);
                },
                pb -> new BlocksHidePacket(pb.readBoolean()),
                (msg, ctx) -> {
                    ctx.get().enqueueWork(() -> HiddenNames.PROXY.handleBlocksHideUpdate(
                            ctx.get(),
                            msg.blocksHide
                    ));
                    ctx.get().setPacketHandled(true);
                });
        CHANNEL.registerMessage(
                ID++,
                NameDataSyncPacket.class,
                NameDataSyncPacket::encoder,
                NameDataSyncPacket.decoder(),
                (msg, ctx) -> {
                    ctx.get().enqueueWork(() -> HiddenNames.PROXY.handleNameDataSync(
                            ctx.get(),
                            msg.data
                    ));
                    ctx.get().setPacketHandled(true);
                }
        );
    }
}
