package github.pitbox46.hiddennames;

import github.pitbox46.hiddennames.commands.ModCommands;
import github.pitbox46.hiddennames.network.ClientProxy;
import github.pitbox46.hiddennames.network.CommonProxy;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderNameplateEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(HiddenNames.MODID)
public class HiddenNames {
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();
    public static final String MODID = "hiddennames";
    public static CommonProxy PROXY;

    public HiddenNames() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.SERVER_CONFIG);
        PROXY = DistExecutor.safeRunForDist(() -> ClientProxy::new, () -> CommonProxy::new);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        ModCommands.register(event.getDispatcher());
    }

    @Mod.EventBusSubscriber(Dist.CLIENT)
    public static class ClientEvents {
        @SubscribeEvent
        public static void onRenderNameplate(RenderNameplateEvent event) {
            if (event.getEntity() instanceof PlayerEntity && !ClientProxy.isNameplateVisible()) {
                event.setResult(Event.Result.DENY);
            }
        }
    }
}
