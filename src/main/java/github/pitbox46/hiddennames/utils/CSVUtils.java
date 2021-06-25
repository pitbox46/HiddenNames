package github.pitbox46.hiddennames.utils;

import github.pitbox46.hiddennames.network.NamePacket;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.io.*;
import java.util.UUID;

public class CSVUtils {
    public static void replaceEntry(File csv, String playerName, int column, String newEntry) {
        try {
            BufferedReader csvReader = new BufferedReader(new FileReader(csv));
            StringBuilder newContent = new StringBuilder();
            String row = csvReader.readLine();
            while(row != null) {
                String[] data = row.split(",");
                if(data[1].equals(playerName)) {
                    data[column] = newEntry;
                }
                newContent.append(String.join(",",data)).append("\n");

                row = csvReader.readLine();
            }
            //Remove final \n
            newContent.deleteCharAt(newContent.length()-1);

            csvReader.close();

            replaceCSV(csv, newContent.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void replaceAllEntries(File csv, int column, String newEntry) {
        try {
            BufferedReader csvReader = new BufferedReader(new FileReader(csv));
            StringBuilder newContent = new StringBuilder();
            String row = csvReader.readLine();
            while(row != null) {
                String[] data = row.split(",");
                if(!data[0].equals("UUID") && !data[1].equals("Real Name")) {
                    data[column] = newEntry;
                }
                newContent.append(String.join(",",data)).append("\n");

                row = csvReader.readLine();
            }
            //Remove final \n
            newContent.deleteCharAt(newContent.length()-1);

            csvReader.close();

            replaceCSV(csv, newContent.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getEntry(File csv, String playerName, int column) {
        try {
            BufferedReader csvReader = new BufferedReader(new FileReader(csv));

            String row = csvReader.readLine();
            while(row != null) {
                String[] data = row.split(",");
                if(data[1].equals(playerName)) {
                    return data[column];
                }
                row = csvReader.readLine();
            }
            csvReader.close();
        } catch (IOException e) {
            e.printStackTrace();
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
        try {
            BufferedReader csvReader = new BufferedReader(new FileReader(csv));
            String row = csvReader.readLine();
            while(row != null && !row.isEmpty()) {
                String[] data = row.split(",");
                if(!data[0].equals("UUID")) {
                    channel.send(PacketDistributor.ALL.noArg(), new NamePacket(
                            UUID.fromString(data[0]),
                            new StringTextComponent(data[2]).mergeStyle(TextFormatting.getValueByName(data[3])),
                            Boolean.parseBoolean(data[4]) ? AnimatedStringTextComponent.Animation.valueOf(data[5]) : AnimatedStringTextComponent.Animation.HIDDEN)
                    );
                }
                row = csvReader.readLine();
            }
            csvReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
