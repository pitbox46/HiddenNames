package github.pitbox46.hiddennames;

import github.pitbox46.hiddennames.commands.AnimationArgument;
import github.pitbox46.hiddennames.commands.ModCommands;
import github.pitbox46.hiddennames.network.ClientProxy;
import github.pitbox46.hiddennames.network.CommonProxy;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Team;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

@Mod(HiddenNames.MODID)
public class HiddenNames {
    public static final String MODID = "hiddennames";
    private static final Logger LOGGER = LogManager.getLogger();
    public static CommonProxy PROXY;
    public static JsonData JSON;

    public static final DeferredRegister<ArgumentTypeInfo<?, ?>> ARG_TYPE_INFO_REG = DeferredRegister.create(Registries.COMMAND_ARGUMENT_TYPE, MODID);
    public static final RegistryObject<ArgumentTypeInfo<AnimationArgument, SingletonArgumentInfo<AnimationArgument>.Template>> ANIMATION_ARG =
        ARG_TYPE_INFO_REG.register(
                "animation_arg",
                () -> ArgumentTypeInfos.registerByClass(AnimationArgument.class, SingletonArgumentInfo.contextFree(AnimationArgument::animationArgument))
        );

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

    /**
     *
     * @return The name with team colors if the config option is set to have team colors override
     */
    public static MutableComponent getCorrectedName(Component name, @Nullable Team team) {
        ChatFormatting color;
        MutableComponent nameCopy = name.copy();
        if (Config.TEAM_OVERRIDE.get() && team != null && (color = team.getColor()) != ChatFormatting.RESET) {
            nameCopy = nameCopy.withStyle(color);
        }
        return nameCopy;
    }

    public static Component getFullNameplate(Component name, @Nullable Team team) {
        if (team instanceof PlayerTeam playerTeam) {
            ChatFormatting chatformatting = team.getColor();
            MutableComponent prefix = playerTeam.getPlayerPrefix().copy();
            MutableComponent suffix = playerTeam.getPlayerSuffix().copy();
            if (chatformatting != ChatFormatting.RESET) {
                prefix.withStyle(chatformatting);
                suffix.withStyle(chatformatting);
            }
            return Component.empty().append(prefix).append(name).append(suffix);
        }
        return name;
    }
}
