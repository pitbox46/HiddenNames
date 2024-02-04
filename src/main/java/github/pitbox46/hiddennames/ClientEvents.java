package github.pitbox46.hiddennames;

import github.pitbox46.hiddennames.data.Animations;
import github.pitbox46.hiddennames.data.NameData;
import github.pitbox46.hiddennames.network.ClientPayloadHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientChatReceivedEvent;
import net.neoforged.neoforge.client.event.RenderNameTagEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class ClientEvents {
    @SubscribeEvent
    public static void onRenderNameplate(RenderNameTagEvent event) {
        Player localPlayer = Minecraft.getInstance().player;
        if (event.getEntity() instanceof Player) {
            NameData nameData = NameData.DATA.get(event.getEntity().getUUID());

            if (event.getEntity() == localPlayer && Config.SHOW_OWN.get() && !event.getEntity().isSpectator())
                event.setResult(Event.Result.ALLOW);
            if (nameData == null)
                return;
            if (nameData.getAnimation() != Animations.HIDDEN && ClientPayloadHandler.getInstance().doBlocksHide()) {
                Vec3 vector3d = localPlayer.getEyePosition(event.getPartialTick());
                Vec3 vector3d1 = event.getEntity().getEyePosition(event.getPartialTick());
                if (localPlayer.level().clip(new ClipContext(vector3d, vector3d1, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, localPlayer)).getType() != HitResult.Type.MISS) {
                    event.setResult(Event.Result.DENY);
                    return;
                }
            }
            if (!Config.RENDER_ANIMATIONS.get() && nameData.getAnimation() != Animations.HIDDEN) {
                return;
            }

            //The addition is an offset so each player doesn't have the same animation go at the same time
            nameData.getAnimation().renderer().accept(event, Minecraft.getInstance().level.getGameTime() + event.getEntity().getId() * 21L);
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
