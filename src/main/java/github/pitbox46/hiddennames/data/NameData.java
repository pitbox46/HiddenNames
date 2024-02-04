package github.pitbox46.hiddennames.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import github.pitbox46.hiddennames.Config;
import github.pitbox46.hiddennames.PlayerDuck;
import github.pitbox46.hiddennames.network.NameDataSyncPacket;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NameData {
    /**
     * Instead of failing hard, we prefer to compute an error name
     */
    public final static Map<UUID, NameData> DATA = new HashMap<>() {
        @Override
        public NameData get(Object key) {
            NameData data = super.get(key);
            return data != null ? data : new NameData((UUID) key, Component.literal("ERROR_DESYNC:" + key.toString()), Animations.NO_ANIMATION);
        }
    };

    private final UUID uuid;
    private Component displayName;
    private Animation animation;

    public NameData(Player player) {
        this(player, Config.DEFAULT_VISIBLE.get() ? Animations.NO_ANIMATION: Animations.HIDDEN);
    }

    public NameData(Player player, @Nonnull Animation animation) {
        this(player.getUUID(), ((PlayerDuck) player).hiddenNames$getUnmodifiedDisplayName(), animation);
    }

    public NameData(UUID uuid, Component displayName, Animation animation) {
        this.uuid = uuid;
        this.displayName = displayName;
        this.animation = animation;
    }

    //TODO Consider only sending necessary packets rather than sending them all (only new players need all packets)
    public static void sendSyncData() {
        for (NameData data : DATA.values()) {
            PacketDistributor.ALL.noArg().send(new NameDataSyncPacket(data));
        }
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
        return animation == null ? Animations.NO_ANIMATION : animation;
    }

    public void setAnimation(Animation animation) {
        this.animation = animation == null ? Animations.NO_ANIMATION : animation;
    }

    //region Serial
    public JsonObject serialize(JsonObject json) {
        json.addProperty("uuid", uuid.toString());
        json.add("displayName", Component.Serializer.toJsonTree(displayName));
        json.addProperty("animation", animation.key());
        return json;
    }

    public static NameData deserialize(JsonObject json) {
        UUID uuid = UUID.fromString(json.getAsJsonPrimitive("uuid").getAsString());
        Component component = Component.Serializer.fromJson(json.get("displayName"));
        Animation animation = Animations.getAnimation(json.getAsJsonPrimitive("animation").getAsString());
        return new NameData(uuid, component, animation);
    }

    public static JsonArray deserializeAll() {
        JsonArray array = new JsonArray();
        for (NameData data : DATA.values())
            array.add(data.serialize(new JsonObject()));
        return array;
    }
    //endregion
}
