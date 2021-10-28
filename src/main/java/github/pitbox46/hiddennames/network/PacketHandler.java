package github.pitbox46.hiddennames.network;

import github.pitbox46.hiddennames.HiddenNames;
import github.pitbox46.hiddennames.utils.AnimatedStringTextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fmllegacy.network.NetworkRegistry;
import net.minecraftforge.fmllegacy.network.simple.SimpleChannel;

public class PacketHandler {
    private static final String PROTOCOL_VERSION = "3.2.1";
    public static SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation("hiddennames","main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals);
    private static int ID = 0;

    public static void init() {
        CHANNEL.registerMessage(
                ID++,
                NamePacket.class,
                (msg, pb) -> {
                    pb.writeUUID(msg.uuid);
                    pb.writeComponent(msg.name);
                    pb.writeEnum(msg.anime);
                },
                pb -> new NamePacket(pb.readUUID(), pb.readComponent(), pb.readEnum(AnimatedStringTextComponent.Animation.class)),
                (msg, ctx) -> {
                    ctx.get().enqueueWork(() -> HiddenNames.PROXY.handleNameplateChange(
                            ctx.get(),
                            msg.uuid,
                            (AnimatedStringTextComponent) new AnimatedStringTextComponent(msg.name.getString(), msg.anime).setStyle(msg.name.getStyle())
                    ));
                    ctx.get().setPacketHandled(true);
                });
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
    }
}
