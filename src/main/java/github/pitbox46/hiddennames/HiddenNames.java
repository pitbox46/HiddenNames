package github.pitbox46.hiddennames;

import github.pitbox46.hiddennames.commands.AnimationArgument;
import github.pitbox46.hiddennames.commands.ModCommands;
import github.pitbox46.hiddennames.network.BlocksHidePacket;
import github.pitbox46.hiddennames.network.ClientPayloadHandler;
import github.pitbox46.hiddennames.network.NameDataSyncPacket;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Supplier;

@Mod(HiddenNames.MODID)
public class HiddenNames {
    public static final String MODID = "hiddennames";
    private static final Logger LOGGER = LogManager.getLogger();
    public static JsonData JSON;

    public static final DeferredRegister<ArgumentTypeInfo<?, ?>> ARG_TYPE_INFO_REG = DeferredRegister.create(Registries.COMMAND_ARGUMENT_TYPE, MODID);
    public static final Supplier<ArgumentTypeInfo<AnimationArgument, SingletonArgumentInfo<AnimationArgument>.Template>> ANIMATION_ARG =
        ARG_TYPE_INFO_REG.register(
                "animation_arg",
                () -> ArgumentTypeInfos.registerByClass(AnimationArgument.class, SingletonArgumentInfo.contextFree(AnimationArgument::animationArgument))
        );

    public HiddenNames(IEventBus bus) {
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.SERVER_CONFIG);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.CLIENT_CONFIG);
        NeoForge.EVENT_BUS.register(this);
        NeoForge.EVENT_BUS.register(ServerEvents.class);
        bus.addListener(this::registerPackets);
        ARG_TYPE_INFO_REG.register(bus);
    }

    public void registerPackets(final RegisterPayloadHandlerEvent event) {
        IPayloadRegistrar registrar = event.registrar(MODID);
        registrar.play(BlocksHidePacket.ID, BlocksHidePacket::new, handler -> handler
                .client((payload, context) -> {
                    context.workHandler().submitAsync(() -> {
                        ClientPayloadHandler.getInstance().handle(payload, context);
                    }).exceptionally(e -> {
                        context.packetHandler().disconnect(Component.literal(e.getMessage()));
                        return null;
                    });
                }));
        registrar.play(NameDataSyncPacket.ID, NameDataSyncPacket::new, handler -> handler
                .client((payload, context) -> {
                    context.workHandler().submitAsync(() -> {
                        ClientPayloadHandler.getInstance().handle(payload, context);
                    }).exceptionally(e -> {
                        context.packetHandler().disconnect(Component.literal(e.getMessage()));
                        return null;
                    });
                }));
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        ModCommands.register(event.getDispatcher());
    }
}
