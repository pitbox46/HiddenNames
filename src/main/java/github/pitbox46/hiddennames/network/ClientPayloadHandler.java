package github.pitbox46.hiddennames.network;

import github.pitbox46.hiddennames.data.NameData;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public class ClientPayloadHandler {
    private static final ClientPayloadHandler INSTANCE = new ClientPayloadHandler();
    private boolean blocksHide = false;

    public static ClientPayloadHandler getInstance() {
        return INSTANCE;
    }

    public boolean doBlocksHide() {
        return blocksHide;
    }

    public void handle(BlocksHidePacket packet, PlayPayloadContext ctx) {
        blocksHide = packet.blocksHide();
    }

    public void handle(NameDataSyncPacket packet, PlayPayloadContext ctx) {
        if (Minecraft.getInstance().level != null) {
            NameData data = packet.data();
            Player player = Minecraft.getInstance().level.getPlayerByUUID(data.getUuid());
            NameData.DATA.put(data.getUuid(), data);
            if (player != null)
                player.refreshDisplayName();
        }
    }
}
