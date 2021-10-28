package github.pitbox46.hiddennames.utils;

import github.pitbox46.hiddennames.CSVObject;
import github.pitbox46.hiddennames.network.NamePacket;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.fmllegacy.network.PacketDistributor;
import net.minecraftforge.fmllegacy.network.simple.SimpleChannel;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class CSVHandler {

    public static void replaceEntry(File csv, String playerName, Columns column, String newEntry) {
        CSVObject csvObject = CSVObject.read(csv);
        List<List<String>> table = CSVObject.byColumnToByRow(csvObject.getTable());
        for(List<String> row: table) {
            if(row.get(csvObject.getHeader().indexOf(Columns.REAL_NAME.name)).equals(playerName)) {
                row.set(csvObject.getHeader().indexOf(column.name), newEntry);
            }
        }
        CSVObject.write(csv, new CSVObject(table, csvObject.getHeader()));
    }

    public static void replaceAllEntries(File csv, Columns column, String newEntry) {
        CSVObject csvObject = CSVObject.read(csv);
        List<List<String>> table = CSVObject.byColumnToByRow(csvObject.getTable());
        for(List<String> row: table) {
            row.set(csvObject.getHeader().indexOf(column.name), newEntry);
        }
        CSVObject.write(csv, new CSVObject(table, csvObject.getHeader()));
    }

    public static String getEntry(File csv, String playerName, Columns column) {
        CSVObject csvObject = CSVObject.read(csv);
        List<List<String>> table = CSVObject.byColumnToByRow(csvObject.getTable());
        for(List<String> row: table) {
            if(row.get(csvObject.getHeader().indexOf(Columns.REAL_NAME.name)).equals(playerName)) {
                return row.get(csvObject.getHeader().indexOf(column.name));
            };
        }
        return null;
    }

    private static void replaceCSV(File csv, String newContent) throws IOException {
        csv.delete();
        File newCSV = new File(csv.toURI());
        FileWriter csvWriter = new FileWriter(newCSV);
        csvWriter.write(newContent);
        csvWriter.close();
    }

    public static void updateClients(File csv, SimpleChannel channel) {
        CSVObject csvObject = CSVObject.read(csv);
        List<List<String>> table = CSVObject.byColumnToByRow(csvObject.getTable());
        for(List<String> row: table) {
            channel.send(PacketDistributor.ALL.noArg(), new NamePacket(
                            UUID.fromString(row.get(csvObject.getHeader().indexOf(Columns.UUID.name))),
                            new TextComponent(row.get(csvObject.getHeader().indexOf(Columns.DISPLAY_NAME.name))).withStyle(ChatFormatting.getByName(row.get(csvObject.getHeader().indexOf(Columns.NAME_COLOR.name)))),
                            Boolean.parseBoolean(row.get(csvObject.getHeader().indexOf(Columns.NAME_VISIBLE.name))) ? AnimatedStringTextComponent.Animation.valueOf(row.get(csvObject.getHeader().indexOf(Columns.ANIMATION.name))) : AnimatedStringTextComponent.Animation.HIDDEN)
                    );
        }
    }

    public enum Columns {
        UUID("UUID"),
        REAL_NAME("Real Name"),
        DISPLAY_NAME("Displayed Name"),
        NAME_COLOR("Name Color"),
        NAME_VISIBLE("Name Visible"),
        ANIMATION("Animation");

        public String name;
        Columns(String name) {
            this.name = name;
        }
    }
}
