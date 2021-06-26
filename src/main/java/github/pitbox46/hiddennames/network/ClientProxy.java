package github.pitbox46.hiddennames.network;

import github.pitbox46.hiddennames.utils.AnimatedStringTextComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ClientProxy extends CommonProxy {
    private static boolean blocksHide = false;
    private static Map<UUID, AnimatedStringTextComponent> displayNames = new HashMap<>();

    public ClientProxy() {
        super();
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void handleNameplateChange(NetworkEvent.Context ctx, UUID uuid, AnimatedStringTextComponent name) {
        displayNames.put(uuid, name);
        PlayerEntity player;
        if(Minecraft.getInstance().world != null) {
            player = Minecraft.getInstance().world.getPlayerByUuid(uuid);
            if(player != null)
                player.refreshDisplayName();
        }
    }

    @Override
    public void handleBlocksHideUpdate(NetworkEvent.Context ctx, boolean bool) {
        blocksHide = bool;
    }

    public static boolean doBlocksHide() {
        return blocksHide;
    }

    public static AnimatedStringTextComponent getDisplayName(UUID uuid) {
        return displayNames.get(uuid) == null ? null : displayNames.get(uuid);
    }
}
