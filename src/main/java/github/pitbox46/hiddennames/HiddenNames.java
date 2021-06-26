package github.pitbox46.hiddennames;

import github.pitbox46.hiddennames.commands.ModCommands;
import github.pitbox46.hiddennames.network.ClientProxy;
import github.pitbox46.hiddennames.network.CommonProxy;
import github.pitbox46.hiddennames.utils.AnimatedStringTextComponent;
import github.pitbox46.hiddennames.utils.CSVHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.*;
import net.minecraft.world.storage.FolderName;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderNameplateEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.loading.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.nio.file.Path;
import java.util.*;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(HiddenNames.MODID)
public class HiddenNames {
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();
    public static final String MODID = "hiddennames";
    public static CommonProxy PROXY;
    public static File dataFile;

    public HiddenNames() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.SERVER_CONFIG);
        PROXY = DistExecutor.safeRunForDist(() -> ClientProxy::new, () -> CommonProxy::new);
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(ServerEvents.class);
    }

    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        Path modFolder = event.getServer().func_240776_a_(new FolderName("hiddennames"));
        dataFile = new File(FileUtils.getOrCreateDirectory(modFolder, "hiddennames").toFile(), "data.csv");
        if(!dataFile.exists()) {
            Map<String, List<String>> table = new LinkedHashMap<>();
            for (CSVHandler.Columns c : CSVHandler.Columns.values()) {
                table.put(c.name, new ArrayList<>());
            }
            CSVObject.write(dataFile, new CSVObject(table));
        }
    }

    @SubscribeEvent
    public void onNameFormat(PlayerEvent.NameFormat event) {
        if(ClientProxy.getDisplayName(event.getPlayer().getUniqueID()) != null && event.getPlayer() instanceof AbstractClientPlayerEntity) {
            event.setDisplayname(ClientProxy.getDisplayName(event.getPlayer().getUniqueID()));
            NetworkPlayerInfo playerInfo = Minecraft.getInstance().player.connection.getPlayerInfo(event.getPlayer().getUniqueID());
            if(playerInfo != null) {
                playerInfo.setDisplayName(ClientProxy.getDisplayName(event.getPlayer().getUniqueID()));
            }
        }
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        ModCommands.register(event.getDispatcher());
    }

    @Mod.EventBusSubscriber(Dist.CLIENT)
    public static class ClientEvents {
        private static final Map<Entity,ArrayList<Color>> COLOR_MAP = new HashMap<>();
        private static double previousTick = 0;

        @SubscribeEvent
        public static void onRenderNameplate(RenderNameplateEvent event) {
            PlayerEntity player = Minecraft.getInstance().player;
            double tick = player.world.getGameTime() + event.getPartialTicks();

            if (event.getEntity() instanceof PlayerEntity) {
                AnimatedStringTextComponent displayName = ClientProxy.getDisplayName(event.getEntity().getUniqueID());
                if (displayName != null) {
                    AnimatedStringTextComponent.Animation anime = displayName.getAnimation();

                    if(anime != AnimatedStringTextComponent.Animation.HIDDEN && ClientProxy.doBlocksHide()) {
                        Vector3d vector3d = new Vector3d(player.getPosX(), player.getPosYEye(), player.getPosZ());
                        Vector3d vector3d1 = new Vector3d(event.getEntity().getPosX(), event.getEntity().getPosYHeight(1) + 0.5F, event.getEntity().getPosZ());
                        if(player.world.rayTraceBlocks(new RayTraceContext(vector3d, vector3d1, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, player)).getType() != RayTraceResult.Type.MISS) {
                            event.setResult(Event.Result.DENY);
                            return;
                        }
                    }

                    switch (anime) {
                        case HIDDEN: {
                            event.setResult(Event.Result.DENY);
                            break;
                        }
                        case BREATH: {
                            int amp = 60;
                            int cycle = 180;

                            Color color = displayName.getStyle().getColor();
                            if (color == null) {
                                color = Color.fromTextFormatting(TextFormatting.WHITE);
                            }
                            int primaryColor = color.getColor();
                            int red = ColorHelper.PackedColor.getRed(primaryColor);
                            int green = ColorHelper.PackedColor.getGreen(primaryColor);
                            int blue = ColorHelper.PackedColor.getBlue(primaryColor);

                            double sin = Math.sin((tick % 360) * 2 * Math.PI / cycle);
                            double newRed = red + (amp * sin);
                            double newGreen = green + (amp * sin);
                            double newBlue = blue + (amp * sin);

                            IFormattableTextComponent newName = displayName.deepCopy();
                            newName.setStyle(displayName.getStyle().setColor(Color.fromInt(ColorHelper.PackedColor.packColor(255, roundToByte(newRed), roundToByte(newGreen), roundToByte(newBlue)))));

                            event.setContent(newName);
                            break;
                        }
                        case RAINBOW: {
                            StringTextComponent newName = new StringTextComponent("");
                            int i = 0;
                            for (char c : displayName.getUnformattedComponentText().toCharArray()) {
                                newName.appendSibling(new StringTextComponent(String.valueOf(c)).setStyle(Style.EMPTY.setColor(Color.fromInt(MathHelper.hsvToRGB((float) (((tick + 3 * i) % 180) / 180), 1, 1)))));
                                i++;
                            }
                            event.setContent(newName);
                            break;
                        }
                        case CYCLE: {
                            int toNext = 60;

                            if(!COLOR_MAP.containsKey(event.getEntity())) {
                                ArrayList<Color> colors = new ArrayList<>(2);
                                colors.add(displayName.getStyle().getColor());
                                colors.add(Color.fromInt(((LivingEntity) event.getEntity()).getRNG().nextInt(16777216)));
                                COLOR_MAP.put(event.getEntity(), colors);
                            }
                            ArrayList<Color> colors = COLOR_MAP.get(event.getEntity());

                            if (tick % toNext < previousTick % toNext) {
                                colors.add(Color.fromInt(((LivingEntity) event.getEntity()).getRNG().nextInt(16777216)));
                                colors.remove(0);
                                COLOR_MAP.put(event.getEntity(), colors);
                            }

                            IFormattableTextComponent newName = displayName.deepCopy();
                            newName.setStyle(newName.getStyle().setColor(blendColors(colors.get(0), colors.get(1), (float) (toNext - tick % toNext) / toNext)));

                            event.setContent(newName);
                            previousTick = tick;
                            break;
                        }
                    }
                }
            }
        }

        /**
         * Used for testing new animations.
         * Testing on a mob is much easier than testing on a player (requires two Minecraft instances).
         */
        @SubscribeEvent
        public static void onRenderMobNameplate(RenderNameplateEvent event) {
//            double tick = Minecraft.getInstance().player.world.getGameTime() + event.getPartialTicks();
//
//            if(event.getEntity() instanceof MobEntity && event.getContent().getUnformattedComponentText().equals("thisIsForTesting")) {
//
//            }
//            previousTick = tick;
        }

        private static int roundToByte(double number) {
            if(number < 0) return 0;
            if(number > 255) return 255;
            return (int) Math.round(number);
        }

        private static Color blendColors(Color primary, Color secondary, float percent) {
            if(percent > 1) percent = 1;
            if(percent < 0) percent = 0;
            int red = (int) (ColorHelper.PackedColor.getRed(primary.getColor()) * percent + ColorHelper.PackedColor.getRed(secondary.getColor()) * (1 - percent));
            int green = (int) (ColorHelper.PackedColor.getGreen(primary.getColor()) * percent + ColorHelper.PackedColor.getGreen(secondary.getColor()) * (1 - percent));
            int blue = (int) (ColorHelper.PackedColor.getBlue(primary.getColor()) * percent + ColorHelper.PackedColor.getBlue(secondary.getColor()) * (1 - percent));

            return Color.fromInt(ColorHelper.PackedColor.packColor(255, red, green, blue));
        }
    }
}
