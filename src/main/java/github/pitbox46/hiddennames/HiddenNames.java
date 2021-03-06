package github.pitbox46.hiddennames;

import github.pitbox46.hiddennames.commands.AnimationArgument;
import github.pitbox46.hiddennames.commands.AnimationArgumentSerializer;
import github.pitbox46.hiddennames.commands.ModCommands;
import github.pitbox46.hiddennames.network.ClientProxy;
import github.pitbox46.hiddennames.network.CommonProxy;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(HiddenNames.MODID)
public class HiddenNames {
    public static final String MODID = "hiddennames";
    private static final Logger LOGGER = LogManager.getLogger();
    public static CommonProxy PROXY;
    public static JsonData JSON;

    public static final DeferredRegister<ArgumentTypeInfo<?, ?>> ARG_TYPE_INFO_REG = DeferredRegister.create(Registry.COMMAND_ARGUMENT_TYPE_REGISTRY, MODID);
    public static final RegistryObject<ArgumentTypeInfo<?, ?>> ANIMATION_ARG =
            ARG_TYPE_INFO_REG.register("animation_arg", () -> ArgumentTypeInfos.registerByClass(AnimationArgument.class, new AnimationArgumentSerializer()));

    public HiddenNames() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.SERVER_CONFIG);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.CLIENT_CONFIG);
        PROXY = DistExecutor.safeRunForDist(() -> ClientProxy::new, () -> CommonProxy::new);
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(ServerEvents.class);
        ARG_TYPE_INFO_REG.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        ModCommands.register(event.getDispatcher());
    }
}
