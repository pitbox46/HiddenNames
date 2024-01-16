package github.pitbox46.hiddennames;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import github.pitbox46.hiddennames.data.NameData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class JsonData {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public final MinecraftServer server;
    public final String modName;
    public final String fileName;
    public Path path;

    public JsonData(String modName, String fileName, MinecraftServer server) {
        this.server = server;
        this.modName = modName;
        this.fileName = fileName;
    }

    public void getOrCreateFile() throws IOException {
        Path modFolderPath = server.getWorldPath(new LevelResource(modName));
        File modFolder = Files.isDirectory(modFolderPath) ? modFolderPath.toFile() : Files.createDirectory(modFolderPath).toFile();
        File dataFile = new File(modFolder, fileName);
        if (dataFile.createNewFile()) {
            Files.writeString(dataFile.toPath(), GSON.toJson(new JsonArray()));
        }
        this.path = dataFile.toPath();
    }

    public void readToData() throws IOException {
        JsonArray json = GSON.fromJson(Files.readString(path), JsonArray.class);
        for (JsonElement element : json) {
            NameData data = NameData.deserialize(element.getAsJsonObject());
            NameData.DATA.put(data.getUuid(), data);
        }
    }

    public void saveToJson() throws IOException {
        Files.write(path, GSON.toJson(NameData.deserializeAll()).getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
    }
}
