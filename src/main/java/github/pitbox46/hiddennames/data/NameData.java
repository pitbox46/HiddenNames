package github.pitbox46.hiddennames.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import github.pitbox46.hiddennames.Config;
import github.pitbox46.hiddennames.network.NameDataSyncPacket;
import github.pitbox46.hiddennames.network.PacketHandler;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.PacketDistributor;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NameData {
    public final static Map<UUID, NameData> DATA = new HashMap<>();

    private final UUID uuid;
    private Component displayName;
    private Animation animation;

    public NameData(Player player) {
        this(player, Config.DEFAULT_VISIBLE.get() ? Animations.NULL : Animations.HIDDEN);
    }

    public NameData(Player player, @Nonnull Animation animation) {
        this(player.getUUID(), player.getDisplayName(), animation);
    }

    public NameData(UUID uuid, Component displayName, Animation animation) {
        this.uuid = uuid;
        this.displayName = displayName;
        this.animation = animation;
    }

    public static NameData deserialize(JsonObject json) {
        UUID uuid = UUID.fromString(json.getAsJsonPrimitive("uuid").getAsString());
        Component component = Component.Serializer.fromJson(json.get("displayName"));
        Animation animation = Animations.getAnimation("animation");
        return new NameData(uuid, component, animation);
    }

    //TODO Consider only sending necessary packets rather than sending them all (only new players need all packets)
    public static void sendSyncData() {
        for (NameData data : DATA.values()) {
            PacketHandler.CHANNEL.send(PacketDistributor.ALL.noArg(), new NameDataSyncPacket(data));
        }
    }

    public static JsonArray deserializeAll() {
        JsonArray array = new JsonArray();
        for (NameData data : DATA.values())
            array.add(data.serialize(new JsonObject()));
        return array;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Component getDisplayName() {
        return displayName;
    }

    public void setDisplayName(Component displayName) {
        this.displayName = displayName;
    }

    public Animation getAnimation() {
        return animation == null ? Animations.NULL : animation;
    }

    public void setAnimation(Animation animation) {
        this.animation = (animation == null ? Animations.NULL : animation);
    }

    public JsonObject serialize(JsonObject json) {
        json.addProperty("uuid", uuid.toString());
        json.add("displayName", Component.Serializer.toJsonTree(displayName));
        json.addProperty("animation", Animations.getKey(animation));
        return json;
    }
}
