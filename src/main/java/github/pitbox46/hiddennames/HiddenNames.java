package github.pitbox46.hiddennames;

import github.pitbox46.hiddennames.commands.ModCommands;
import github.pitbox46.hiddennames.network.ClientProxy;
import github.pitbox46.hiddennames.network.CommonProxy;
import github.pitbox46.hiddennames.utils.AnimatedStringTextComponent;
import github.pitbox46.hiddennames.utils.CSVHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.network.chat.*;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
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
import net.minecraftforge.fml.loading.FileUtils;
import net.minecraftforge.fmlserverevents.FMLServerStartingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.nio.file.Path;
import java.util.*;

@Mod(HiddenNames.MODID)
public class HiddenNames {
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
        Path modFolder = event.getServer().getWorldPath(new LevelResource("hiddennames"));
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
        if(ClientProxy.getDisplayName(event.getPlayer().getUUID()) != null && event.getPlayer() instanceof AbstractClientPlayer) {
            event.setDisplayname(ClientProxy.getDisplayName(event.getPlayer().getUUID()));
            PlayerInfo playerInfo = Minecraft.getInstance().player.connection.getPlayerInfo(event.getPlayer().getUUID());
            if(playerInfo != null) {
                playerInfo.setTabListDisplayName(ClientProxy.getDisplayName(event.getPlayer().getUUID()));
            }
        }
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        ModCommands.register(event.getDispatcher());
    }

    @Mod.EventBusSubscriber(Dist.CLIENT)
    public static class ClientEvents {
        private static final Map<Entity,ArrayList<TextColor>> COLOR_MAP = new HashMap<>();
        private static double previousTick = 0;

        @SubscribeEvent
        public static void onRenderNameplate(RenderNameplateEvent event) {
            Player player = Minecraft.getInstance().player;
            double tick = player.level.getGameTime() + event.getPartialTicks();

            if (event.getEntity() instanceof Player) {
                AnimatedStringTextComponent displayName = ClientProxy.getDisplayName(event.getEntity().getUUID());
                if (displayName != null) {
                    AnimatedStringTextComponent.Animation anime = displayName.getAnimation();

                    if(anime != AnimatedStringTextComponent.Animation.HIDDEN && ClientProxy.doBlocksHide()) {
                        Vec3 vector3d = new Vec3(player.getX(), player.getEyePosition().y, player.getZ());
                        Vec3 vector3d1 = new Vec3(event.getEntity().getX(), event.getEntity().getY(1) + 0.5F, event.getEntity().getZ());
                        if(player.level.clip(new ClipContext(vector3d, vector3d1, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player)).getType() != HitResult.Type.MISS) {
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

                            TextColor color = displayName.getStyle().getColor();
                            if (color == null) {
                                color = TextColor.fromLegacyFormat(ChatFormatting.WHITE);
                            }
                            int primaryColor = color.getValue();
                            int red = FastColor.ARGB32.red(primaryColor);
                            int green = FastColor.ARGB32.green(primaryColor);
                            int blue = FastColor.ARGB32.blue(primaryColor);

                            double sin = Math.sin((tick % 360) * 2 * Math.PI / cycle);
                            double newRed = red + (amp * sin);
                            double newGreen = green + (amp * sin);
                            double newBlue = blue + (amp * sin);

                            MutableComponent newName = displayName.copy();
                            newName.setStyle(displayName.getStyle().withColor(TextColor.fromRgb(FastColor.ARGB32.color(255, roundToByte(newRed), roundToByte(newGreen), roundToByte(newBlue)))));

                            event.setContent(newName);
                            break;
                        }
                        case RAINBOW: {
                            TextComponent newName = new TextComponent("");
                            int i = 0;
                            for (char c : displayName.getContents().toCharArray()) {
                                newName.append(new TextComponent(String.valueOf(c)).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(Mth.hsvToRgb((float) (((tick + 3 * i) % 180) / 180), 1, 1)))));
                                i++;
                            }
                            event.setContent(newName);
                            break;
                        }
                        case CYCLE: {
                            int toNext = 60;

                            if(!COLOR_MAP.containsKey(event.getEntity())) {
                                ArrayList<TextColor> colors = new ArrayList<>(2);
                                colors.add(displayName.getStyle().getColor());
                                colors.add(TextColor.fromRgb(((LivingEntity) event.getEntity()).getRandom().nextInt(16777216)));
                                COLOR_MAP.put(event.getEntity(), colors);
                            }
                            ArrayList<TextColor> colors = COLOR_MAP.get(event.getEntity());

                            if (tick % toNext < previousTick % toNext) {
                                colors.add(TextColor.fromRgb(((LivingEntity) event.getEntity()).getRandom().nextInt(16777216)));
                                colors.remove(0);
                                COLOR_MAP.put(event.getEntity(), colors);
                            }

                            MutableComponent newName = displayName.plainCopy();
                            newName.setStyle(newName.getStyle().withColor(blendColors(colors.get(0), colors.get(1), (float) (toNext - tick % toNext) / toNext)));

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

        private static TextColor blendColors(TextColor primary, TextColor secondary, float percent) {
            if(percent > 1) percent = 1;
            if(percent < 0) percent = 0;
            int red = (int) (FastColor.ARGB32.red(primary.getValue()) * percent + FastColor.ARGB32.red(secondary.getValue()) * (1 - percent));
            int green = (int) (FastColor.ARGB32.green(primary.getValue()) * percent + FastColor.ARGB32.green(secondary.getValue()) * (1 - percent));
            int blue = (int) (FastColor.ARGB32.blue(primary.getValue()) * percent + FastColor.ARGB32.blue(secondary.getValue()) * (1 - percent));

            return TextColor.fromRgb(FastColor.ARGB32.color(255, red, green, blue));
        }
    }
}
