package github.pitbox46.hiddennames;

import github.pitbox46.hiddennames.data.NameData;
import github.pitbox46.hiddennames.network.BlocksHidePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class ServerEvents {
    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event) throws IOException {
        HiddenNames.JSON = new JsonData(HiddenNames.MODID, "data.json", event.getServer());
        HiddenNames.JSON.getOrCreateFile();
        try {
            HiddenNames.JSON.readToData();
        } catch (IOException e) {
            LOGGER.error("Could not parse hiddennames/data.json");
            LOGGER.catching(e);
        }
    }

    @SubscribeEvent
    public static void onJoinServer(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        NameData.DATA.computeIfAbsent(player.getUUID(), uuid -> new NameData(player));
        PacketDistributor.PLAYER.with((ServerPlayer) player).send(new BlocksHidePacket(Config.BLOCKS_HIDE.get()));
        NameData.sendSyncData();
    }

    @SubscribeEvent
    public static void onWorldSave(LevelEvent.Save event) throws IOException {
        if (HiddenNames.JSON != null)
            HiddenNames.JSON.saveToJson();
    }
}
