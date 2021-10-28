package github.pitbox46.hiddennames.network;

import github.pitbox46.hiddennames.utils.AnimatedStringTextComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

import java.util.UUID;

public class CommonProxy {
    public CommonProxy() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(FMLCommonSetupEvent event) {
        PacketHandler.init();
    }

    //Client
    public void handleNameplateChange(NetworkEvent.Context ctx, UUID uuid, AnimatedStringTextComponent name) {}

    public void handleBlocksHideUpdate(NetworkEvent.Context ctx, boolean bool) {}
}
