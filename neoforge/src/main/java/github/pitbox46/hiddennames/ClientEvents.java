package github.pitbox46.hiddennames;

import github.pitbox46.hiddennames.data.Animation;
import github.pitbox46.hiddennames.data.NameData;
import github.pitbox46.hiddennames.network.ClientPayloadHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Team;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RenderNameTagEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.util.TriState;

@EventBusSubscriber(value = Dist.CLIENT)
public class ClientEvents {
    public static void onClientSetup(FMLClientSetupEvent clientSetupEvent, ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    public static void onRenderNameplate(RenderNameTagEvent event) {
        Player localPlayer = Minecraft.getInstance().player;
        if (event.getEntity() instanceof Player player) {
            NameData nameData = NameData.DATA.get(event.getEntity().getUUID());

            if (player == localPlayer && Config.SHOW_OWN.get() && !player.isSpectator()) {
                event.setCanRender(TriState.TRUE);
            }
            if (nameData == null) {
                return;
            }
            if (!nameData.getAnimation().isHidden() && ClientPayloadHandler.doBlocksHide()) {
                Vec3 vector3d = localPlayer.getEyePosition(event.getPartialTick());
                Vec3 vector3d1 = player.getEyePosition(event.getPartialTick());
                if (localPlayer.level().clip(new ClipContext(vector3d, vector3d1, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, localPlayer)).getType() != HitResult.Type.MISS) {
                    event.setCanRender(TriState.FALSE);
                    return;
                }
            }
            if (!Config.RENDER_ANIMATIONS.get() && !nameData.getAnimation().isHidden()) {
                return;
            }

            Team team = player.getTeam();

            Animation.Return returnData = nameData.getAnimation().renderer().apply(new Animation.Input(
                    player,
                    event.getContent(),
                    HiddenNames.getCorrectedName(NameData.DATA.get(player.getUUID()).getDisplayName(), team),
                    Minecraft.getInstance().level.getGameTime() + player.getId() * 21L
            ));
            if (returnData.show()) {
                event.setContent(HiddenNames.getFullNameplate(returnData.name(), team));
            } else {
                event.setCanRender(TriState.FALSE);
            }
        }
    }
}
