package github.pitbox46.hiddennames;

import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.NeoForgeConfigRegistry;
import github.pitbox46.hiddennames.commands.AnimationArgument;
import github.pitbox46.hiddennames.commands.ModCommands;
import github.pitbox46.hiddennames.data.Animation;
import github.pitbox46.hiddennames.data.Animations;
import github.pitbox46.hiddennames.data.NameData;
import github.pitbox46.hiddennames.network.BlocksHidePacket;
import github.pitbox46.hiddennames.network.NameDataSyncPacket;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.Team;
import net.neoforged.fml.config.ModConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public class HiddenNames implements ModInitializer {
    public static final String MODID = "hiddennames";
    public static final Logger LOGGER = LogManager.getLogger();
    public static JsonData JSON;
    public static MinecraftServer server = null;

    //region Registries
    public static final ResourceKey<Registry<Animation>> ANIMATION_REGISTRY_KEY =
            ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(MODID, "animations"));
    public static final Registry<Animation> ANIMATION_REGISTRY = FabricRegistryBuilder
            .createDefaulted(ANIMATION_REGISTRY_KEY, ResourceLocation.fromNamespaceAndPath(MODID, "null"))
            .attribute(RegistryAttribute.SYNCED)
            .buildAndRegister();
    //endregion Registries

    public void registerConfig() {
        NeoForgeConfigRegistry.INSTANCE.register(MODID, ModConfig.Type.SERVER, Config.SERVER_CONFIG);
        NeoForgeConfigRegistry.INSTANCE.register(MODID, ModConfig.Type.CLIENT, Config.CLIENT_CONFIG);
    }

    public void registerPackets() {
        PayloadTypeRegistry.playS2C().register(
                BlocksHidePacket.TYPE,
                BlocksHidePacket.CODEC
        );
        PayloadTypeRegistry.playS2C().register(
                NameDataSyncPacket.TYPE,
                NameDataSyncPacket.CODEC
        );
    }

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> ModCommands.register(dispatcher, registryAccess)));
        registerPackets();
        registerConfig();
        ArgumentTypeRegistry.registerArgumentType(
                ResourceLocation.fromNamespaceAndPath(MODID, "animation_arg"),
                AnimationArgument.class,
                SingletonArgumentInfo.contextFree(AnimationArgument::animationArgument)
        );
        Animations.init();

        ServerLifecycleEvents.SERVER_STARTED.register(s -> {
            server = s;
            HiddenNames.JSON = new JsonData(HiddenNames.MODID, "data.json", s);
            try {
                HiddenNames.JSON.getOrCreateFile();
                HiddenNames.JSON.readToData();
            } catch (IOException e) {
                LOGGER.error("Could not parse hiddennames/data.json");
                LOGGER.catching(e);
            }
        });
        ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            if (entity instanceof ServerPlayer player) {
                NameData.DATA.computeIfAbsent(player.getUUID(), uuid -> new NameData(player));
                ServerPlayNetworking.send(player, new BlocksHidePacket(Config.BLOCKS_HIDE.get()));
                NameData.sendSyncData();
            }
        });
        ServerWorldEvents.UNLOAD.register((s, world) -> {
            if (HiddenNames.JSON != null) {
                try {
                    HiddenNames.JSON.saveToJson();
                } catch (IOException e) {
                    LOGGER.error("Could not save hiddennames/data.json");
                    LOGGER.catching(e);
                }
            }
        });
    }

    public static void sendToAll(CustomPacketPayload payload) {
        if (HiddenNames.server == null) {
            HiddenNames.LOGGER.warn("Tried to sync data from client");
            return;
        }
        for (ServerPlayer player : HiddenNames.server.getPlayerList().getPlayers()) {
            ServerPlayNetworking.send(player, payload);
        }
    }

    /**
     *
     * @return The name with team colors if the config option is set to have team colors override
     */
    public static MutableComponent getCorrectedName(Component name, @Nullable Team team) {
        ChatFormatting color;
        MutableComponent nameCopy = name.copy();
        if (Config.TEAM_OVERRIDE.get() && team != null && (color = team.getColor()) != ChatFormatting.RESET) {
            nameCopy = nameCopy.withColor(color.getColor());
        }
        return nameCopy;
    }
}
