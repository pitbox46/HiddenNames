package github.pitbox46.hiddennames;

import github.pitbox46.hiddennames.data.Animation;
import github.pitbox46.hiddennames.data.NameData;
import github.pitbox46.hiddennames.network.ClientPayloadHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderNameTagEvent;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber(value = Dist.CLIENT)
public class ClientEvents {
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

            Animation.Return returnData = nameData.getAnimation().renderer().apply(new Animation.Input(
                    player,
                    event.getContent(),
                    Minecraft.getInstance().level.getGameTime() + player.getId() * 21L
            ));
            if (returnData.show()) {
                event.setContent(returnData.name());
            } else {
                event.setCanRender(TriState.FALSE);
            }
        }
    }

    @SubscribeEvent
    public static void onNameFormat(PlayerEvent.NameFormat event) {
        if (NameData.DATA.get(event.getEntity().getUUID()) != null && event.getEntity() instanceof AbstractClientPlayer) {
            PlayerInfo playerInfo = Minecraft.getInstance().player.connection.getPlayerInfo(event.getEntity().getUUID());
            if (playerInfo != null) {
                playerInfo.setTabListDisplayName(NameData.DATA.get(event.getEntity().getUUID()).getDisplayName());
            }
        }
    }
}
