package github.pitbox46.hiddennames.network;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.network.NetworkEvent;

public class ClientProxy extends CommonProxy {
    private static boolean nameplateVisibility;

    public ClientProxy() {
        super();
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void handleNameplateChange(NetworkEvent.Context ctx, boolean bool) {
        nameplateVisibility = bool;
    }

    public static boolean isNameplateVisible() {
        return nameplateVisibility;
    }
}
