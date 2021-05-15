package github.pitbox46.hiddennames;

import github.pitbox46.hiddennames.commands.ModCommands;
import github.pitbox46.hiddennames.network.ClientProxy;
import github.pitbox46.hiddennames.network.CommonProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.entity.player.RemoteClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.storage.FolderName;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderNameplateEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.UUID;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(HiddenNames.MODID)
public class HiddenNames {
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();
    public static final String MODID = "hiddennames";
    public static CommonProxy PROXY;
    public static File config;

    public HiddenNames() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.SERVER_CONFIG);
        PROXY = DistExecutor.safeRunForDist(() -> ClientProxy::new, () -> CommonProxy::new);
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(ServerEvents.class);
    }

    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        Path modFolder = event.getServer().func_240776_a_(new FolderName("hiddennames"));
        config = new File(FileUtils.getOrCreateDirectory(modFolder, "hiddennames").toFile(), "config.csv");
        try {
            if(config.createNewFile()) {
                FileWriter configWriter = new FileWriter(config);
                configWriter.write("UUID,Real Name,Displayed Name,Name Color,Name Visible");
                configWriter.close();
            }
        } catch(IOException e) {
            LOGGER.warn(e.getMessage());
        }
    }

    @SubscribeEvent
    public void onNameFormat(PlayerEvent.NameFormat event) {
        if(ClientProxy.getDisplayName(event.getPlayer().getUniqueID()) != null && event.getPlayer() instanceof AbstractClientPlayerEntity) {
            event.setDisplayname(ClientProxy.getDisplayName(event.getPlayer().getUniqueID()));
            Minecraft.getInstance().player.connection.getPlayerInfo(event.getPlayer().getUniqueID())
                    .setDisplayName(ClientProxy.getDisplayName(event.getPlayer().getUniqueID()));
        }
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        ModCommands.register(event.getDispatcher());
    }

    @Mod.EventBusSubscriber(Dist.CLIENT)
    public static class ClientEvents {
        @SubscribeEvent
        public static void onRenderNameplate(RenderNameplateEvent event) {
            if (event.getEntity() instanceof PlayerEntity) {
                UUID uuid = event.getEntity().getUniqueID();
                if(!ClientProxy.isNameplateVisible(uuid)) {
                    event.setResult(Event.Result.DENY);
                }
            }
        }
    }
}
