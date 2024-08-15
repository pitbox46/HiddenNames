package github.pitbox46.hiddennames.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import github.pitbox46.hiddennames.Config;
import github.pitbox46.hiddennames.HiddenNames;
import github.pitbox46.hiddennames.PlayerDuck;
import github.pitbox46.hiddennames.network.NameDataSyncPacket;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NameData {
    public static final Component.SerializerAdapter COMPONENT_SERIALIZER = new Component.SerializerAdapter(RegistryAccess.EMPTY);
    /**
     * Instead of failing hard, we prefer to compute an error name
     */
    public static final Map<UUID, NameData> DATA = new HashMap<>() {
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
        this(player, Config.DEFAULT_VISIBLE.get() ? Animations.NO_ANIMATION : Animations.HIDDEN);
    }

    public NameData(Player player, @NotNull Animation animation) {
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
            HiddenNames.sendToAll(new NameDataSyncPacket(data));
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
        json.add("displayName", COMPONENT_SERIALIZER.serialize(displayName, Component.class, null));
        json.addProperty("animation", animation.key().toString());
        return json;
    }

    public static NameData deserialize(JsonObject json) {
        UUID uuid = UUID.fromString(json.getAsJsonPrimitive("uuid").getAsString());
        Component component = COMPONENT_SERIALIZER.deserialize(json.get("displayName"), Component.class, null);
        Animation animation = HiddenNames.ANIMATION_REGISTRY.get(ResourceLocation.parse(json.getAsJsonPrimitive("animation").getAsString()));
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
