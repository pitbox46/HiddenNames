package github.pitbox46.hiddennames.network;

import github.pitbox46.hiddennames.data.NameData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.world.scores.PlayerTeam;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;

public class ClientPayloadHandler {
    private static boolean blocksHide = false;

    public static boolean doBlocksHide() {
        return blocksHide;
    }

    public static void handle(BlocksHidePacket packet, IPayloadContext ctx) {
        blocksHide = packet.blocksHide();
    }

    public static void handle(NameDataSyncPacket packet, IPayloadContext ctx) {
        if (Minecraft.getInstance().player != null) {
            NameData data = packet.data();
            UUID uuid = data.getUuid();
            NameData.DATA.put(uuid, data);

            PlayerInfo playerInfo = Minecraft.getInstance().player.connection.getPlayerInfo(uuid);
            if (playerInfo != null) {
                playerInfo.setTabListDisplayName(PlayerTeam.formatNameForTeam(playerInfo.getTeam(), NameData.DATA.get(uuid).getDisplayName()));
            }
        }
    }
}
