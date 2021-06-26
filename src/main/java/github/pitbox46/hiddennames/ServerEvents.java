package github.pitbox46.hiddennames;

import github.pitbox46.hiddennames.network.BlocksHidePacket;
import github.pitbox46.hiddennames.network.PacketHandler;
import github.pitbox46.hiddennames.utils.AnimatedStringTextComponent;
import github.pitbox46.hiddennames.utils.CSVHandler;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.network.PacketDistributor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class ServerEvents {
    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    public static void onJoinServer(PlayerEvent.PlayerLoggedInEvent event) {
        CSVObject csvObject = CSVObject.read(HiddenNames.dataFile);
        List<List<String>> table = CSVObject.byColumnToByRow(csvObject.getTable());
        for(List<String> row: table) {
            if(row.get(csvObject.getHeader().indexOf(CSVHandler.Columns.UUID.name)).equals(event.getPlayer().getUniqueID().toString())) {
                CSVHandler.updateClients(HiddenNames.dataFile, PacketHandler.CHANNEL);
                return;
            }
        }
        List<String> newLine = new ArrayList<>();
        newLine.add(event.getPlayer().getUniqueID().toString());
        newLine.add(event.getPlayer().getName().getString());
        newLine.add(event.getPlayer().getDisplayName().getString());
        newLine.add(TextFormatting.WHITE.getFriendlyName());
        newLine.add(Config.DEFAULT_VISIBLE.get().toString());
        newLine.add(AnimatedStringTextComponent.Animation.NONE.name());
        table.add(newLine);

        CSVObject.write(HiddenNames.dataFile, new CSVObject(table, csvObject.getHeader()));
        CSVHandler.updateClients(HiddenNames.dataFile, PacketHandler.CHANNEL);
        PacketHandler.CHANNEL.send(PacketDistributor.ALL.noArg(), new BlocksHidePacket(Config.BLOCKS_HIDE.get()));
    }
}
