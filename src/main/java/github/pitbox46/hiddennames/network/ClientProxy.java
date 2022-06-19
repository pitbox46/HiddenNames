package github.pitbox46.hiddennames.network;

import github.pitbox46.hiddennames.data.NameData;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

public class ClientProxy extends CommonProxy {
    private static boolean blocksHide = false;

    public ClientProxy() {
        super();
        MinecraftForge.EVENT_BUS.register(this);
    }

    public static boolean doBlocksHide() {
        return blocksHide;
    }

    @Override
    public void handleBlocksHideUpdate(NetworkEvent.Context ctx, boolean bool) {
        blocksHide = bool;
    }

    @Override
    public void handleNameDataSync(NetworkEvent.Context ctx, NameData data) {
        if (Minecraft.getInstance().level != null) {
            Player player = Minecraft.getInstance().level.getPlayerByUUID(data.getUuid());
            NameData.DATA.put(data.getUuid(), data);
            if (player != null)
                player.refreshDisplayName();
        }
    }
}
