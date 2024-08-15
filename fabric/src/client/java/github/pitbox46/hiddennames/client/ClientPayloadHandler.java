package github.pitbox46.hiddennames.client;

import github.pitbox46.hiddennames.data.NameData;
import github.pitbox46.hiddennames.network.BlocksHidePacket;
import github.pitbox46.hiddennames.network.NameDataSyncPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;

import java.util.UUID;

public class ClientPayloadHandler {
    private static boolean blocksHide = false;

    public static boolean doBlocksHide() {
        return blocksHide;
    }

    public static void handle(BlocksHidePacket packet, ClientPlayNetworking.Context ctx) {
        blocksHide = packet.blocksHide();
    }

    public static void handle(NameDataSyncPacket packet, ClientPlayNetworking.Context ctx) {
        if (Minecraft.getInstance().level != null) {
            NameData data = packet.data();
            UUID uuid = data.getUuid();
            NameData.DATA.put(uuid, data);

            PlayerInfo playerInfo = Minecraft.getInstance().player.connection.getPlayerInfo(uuid);
            if (playerInfo != null) {
                playerInfo.setTabListDisplayName(NameData.DATA.get(uuid).getDisplayName());
            }
        }
    }
}
