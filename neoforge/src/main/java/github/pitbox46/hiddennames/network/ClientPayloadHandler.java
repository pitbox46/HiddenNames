package github.pitbox46.hiddennames.network;

import github.pitbox46.hiddennames.data.NameData;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ClientPayloadHandler {
    private static boolean blocksHide = false;

    public static boolean doBlocksHide() {
        return blocksHide;
    }

    public static void handle(BlocksHidePacket packet, IPayloadContext ctx) {
        blocksHide = packet.blocksHide();
    }

    public static void handle(NameDataSyncPacket packet, IPayloadContext ctx) {
        if (Minecraft.getInstance().level != null) {
            NameData data = packet.data();
            Player player = Minecraft.getInstance().level.getPlayerByUUID(data.getUuid());
            NameData.DATA.put(data.getUuid(), data);
            if (player != null)
                player.refreshDisplayName();
        }
    }
}
