package github.pitbox46.hiddennames.network;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ClientProxy extends CommonProxy {
    private static Map<UUID,DisplayName> displayNames = new HashMap<>();

    public ClientProxy() {
        super();
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void handleNameplateChange(NetworkEvent.Context ctx, UUID uuid, boolean bool, ITextComponent name) {
        displayNames.put(uuid, new DisplayName(name, bool));
        if(Minecraft.getInstance().world != null)
            Minecraft.getInstance().world.getPlayerByUuid(uuid).refreshDisplayName();
    }

    public static boolean isNameplateVisible(UUID uuid) {
        return displayNames.get(uuid) != null && displayNames.get(uuid).isVisible();
    }

    public static ITextComponent getDisplayName(UUID uuid) {
        return displayNames.get(uuid) == null ? null : displayNames.get(uuid).getName();
    }
}
