package github.pitbox46.hiddennames;

import github.pitbox46.hiddennames.commands.AnimationArgument;
import github.pitbox46.hiddennames.commands.ModCommands;
import github.pitbox46.hiddennames.data.Animation;
import github.pitbox46.hiddennames.data.Animations;
import github.pitbox46.hiddennames.network.BlocksHidePacket;
import github.pitbox46.hiddennames.network.ClientPayloadHandler;
import github.pitbox46.hiddennames.network.NameDataSyncPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.scores.Team;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.MainThreadPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

@Mod(HiddenNames.MODID)
public class HiddenNames {
    public static final String MODID = "hiddennames";
    private static final Logger LOGGER = LogManager.getLogger();
    public static JsonData JSON;

    //region Registries
    public static final ResourceKey<Registry<Animation>> ANIMATION_REGISTRY_KEY =
            ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(MODID, "animations"));
    public static final Registry<Animation> ANIMATION_REGISTRY = new RegistryBuilder<>(ANIMATION_REGISTRY_KEY)
            .sync(true)
            .defaultKey(ResourceLocation.fromNamespaceAndPath(MODID, "null"))
            .create();

    public static final DeferredRegister<ArgumentTypeInfo<?, ?>> ARG_TYPE_INFO_REG = DeferredRegister.create(Registries.COMMAND_ARGUMENT_TYPE, MODID);
    public static final Supplier<ArgumentTypeInfo<AnimationArgument, SingletonArgumentInfo<AnimationArgument>.Template>> ANIMATION_ARG =
        ARG_TYPE_INFO_REG.register(
                "animation_arg",
                () -> ArgumentTypeInfos.registerByClass(AnimationArgument.class, SingletonArgumentInfo.contextFree(AnimationArgument::animationArgument))
        );
    //endregion Registries

    public HiddenNames(IEventBus modEventBus, ModContainer container) {
        container.registerConfig(ModConfig.Type.SERVER, Config.SERVER_CONFIG);
        container.registerConfig(ModConfig.Type.CLIENT, Config.CLIENT_CONFIG);
        NeoForge.EVENT_BUS.register(this);
        NeoForge.EVENT_BUS.register(ServerEvents.class);
        container.getEventBus().<FMLClientSetupEvent>addListener(event -> ClientEvents.onClientSetup(event, container));
        container.getEventBus().addListener(this::registerPackets);
        container.getEventBus().addListener(this::registerRegistries);
        ARG_TYPE_INFO_REG.register(container.getEventBus());
        Animations.ANIMATIONS.register(container.getEventBus());
    }

    public void registerRegistries(NewRegistryEvent event) {
        event.register(ANIMATION_REGISTRY);
    }

    public void registerPackets(final RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(MODID);
        registrar.playToClient(
                BlocksHidePacket.TYPE,
                BlocksHidePacket.CODEC,
                new MainThreadPayloadHandler<>(ClientPayloadHandler::handle)
        );
        registrar.playToClient(
                NameDataSyncPacket.TYPE,
                NameDataSyncPacket.CODEC,
                new MainThreadPayloadHandler<>(ClientPayloadHandler::handle)
        );
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        ModCommands.register(event.getDispatcher(), event.getBuildContext());
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
