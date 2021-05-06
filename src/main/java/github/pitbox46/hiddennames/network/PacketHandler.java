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
                BooleanPacket.class,
                (msg, pb) -> {
                    pb.writeEnumValue(msg.type);
                    pb.writeBoolean(msg.bool);
                },
                pb -> new BooleanPacket(pb.readEnumValue(BooleanPacket.Type.class), pb.readBoolean()),
                (msg, ctx) -> {
                    ctx.get().enqueueWork(() -> HiddenNames.PROXY.handleNameplateChange(ctx.get(), msg.bool));
                    ctx.get().setPacketHandled(true);
                });
    }
}
