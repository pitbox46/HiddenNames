package github.pitbox46.hiddennames.client;

import com.mojang.logging.LogUtils;
import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.client.ConfigScreenFactoryRegistry;
import github.pitbox46.hiddennames.network.BlocksHidePacket;
import github.pitbox46.hiddennames.network.NameDataSyncPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import org.slf4j.Logger;

public class HiddenNamesClient implements ClientModInitializer {
    public static final String MODID = "hiddennames";
    public static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public void onInitializeClient() {
        ConfigScreenFactoryRegistry.INSTANCE.register(MODID, ConfigurationScreen::new);

        ClientPlayNetworking.registerGlobalReceiver(BlocksHidePacket.TYPE, ((payload, context) -> {
            context.client().execute(() -> ClientPayloadHandler.handle(payload, context));
        }));
        ClientPlayNetworking.registerGlobalReceiver(NameDataSyncPacket.TYPE, ((payload, context) -> {
            context.client().execute(() -> ClientPayloadHandler.handle(payload, context));
        }));
    }
}
