package github.pitbox46.hiddennames.network;

import github.pitbox46.hiddennames.HiddenNames;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

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
                    pb.writeUniqueId(msg.uuid);
                    pb.writeBoolean(msg.bool);
                    pb.writeTextComponent(msg.name);
                },
                pb -> new NamePacket(pb.readUniqueId(), pb.readBoolean(), pb.readTextComponent()),
                (msg, ctx) -> {
                    ctx.get().enqueueWork(() -> HiddenNames.PROXY.handleNameplateChange(ctx.get(), msg.uuid, msg.bool, msg.name));
                    ctx.get().setPacketHandled(true);
                });
    }
}
