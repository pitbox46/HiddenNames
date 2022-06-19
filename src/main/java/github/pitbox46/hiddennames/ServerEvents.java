package github.pitbox46.hiddennames;

import github.pitbox46.hiddennames.data.NameData;
import github.pitbox46.hiddennames.network.BlocksHidePacket;
import github.pitbox46.hiddennames.network.PacketHandler;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fmllegacy.network.PacketDistributor;
import net.minecraftforge.fmlserverevents.FMLServerStartingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class ServerEvents {
    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    public static void onServerStarting(FMLServerStartingEvent event) throws IOException {
        HiddenNames.JSON = new JsonData(HiddenNames.MODID, "data.json", event.getServer());
        HiddenNames.JSON.getOrCreateFile();
        HiddenNames.JSON.readToData();
    }

    @SubscribeEvent
    public static void onJoinServer(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getPlayer();
        NameData.DATA.computeIfAbsent(player.getUUID(), uuid -> new NameData(player));
        PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), new BlocksHidePacket(Config.BLOCKS_HIDE.get()));
        NameData.sendSyncData();
    }

    @SubscribeEvent
    public static void onWorldSave(WorldEvent.Save event) throws IOException {
        if (HiddenNames.JSON != null)
            HiddenNames.JSON.saveToJson();
    }
}
