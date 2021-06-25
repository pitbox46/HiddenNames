package github.pitbox46.hiddennames;

import github.pitbox46.hiddennames.network.PacketHandler;
import github.pitbox46.hiddennames.utils.CSVUtils;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;

public class ServerEvents {
    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    public static void onJoinServer(PlayerEvent.PlayerLoggedInEvent event) {
        try {
            BufferedReader csvReader = new BufferedReader(new FileReader(HiddenNames.dataFile));
            String row = csvReader.readLine();
            while(row != null) {
                String[] data = row.split(",");
                if(data[0].equals(event.getPlayer().getUniqueID().toString())) {
                    csvReader.close();
                    CSVUtils.updateClients(HiddenNames.dataFile, PacketHandler.CHANNEL);
                    return;
                }
                row = csvReader.readLine();
            }
            csvReader.close();

            FileWriter csvWriter = new FileWriter(HiddenNames.dataFile,true);
            csvWriter.append("\n")
                    .append(event.getPlayer().getUniqueID().toString()).append(",")
                    .append(event.getPlayer().getName().getString()).append(",")
                    .append(event.getPlayer().getDisplayName().getString()).append(",")
                    .append(TextFormatting.WHITE.getFriendlyName()).append(",")
                    .append(Config.DEFAULT_VISIBLE.get().toString()).append(",")
                    .append("NONE");

            csvWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        CSVUtils.updateClients(HiddenNames.dataFile, PacketHandler.CHANNEL);
    }
}
