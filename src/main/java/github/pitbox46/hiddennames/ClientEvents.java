package github.pitbox46.hiddennames;

import github.pitbox46.hiddennames.data.Animation;
import github.pitbox46.hiddennames.data.Animations;
import github.pitbox46.hiddennames.data.NameData;
import github.pitbox46.hiddennames.network.ClientProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Team;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderNameTagEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class ClientEvents {
    @SubscribeEvent
    public static void onRenderNameplate(RenderNameTagEvent event) {
        Player localPlayer = Minecraft.getInstance().player;
        if (event.getEntity() instanceof Player player) {
            NameData nameData = NameData.DATA.get(event.getEntity().getUUID());

            if (event.getEntity() == localPlayer && Config.SHOW_OWN.get() && !event.getEntity().isSpectator())
                event.setResult(Event.Result.ALLOW);
            if (nameData == null)
                return;
            if (nameData.getAnimation() != Animations.HIDDEN && ClientProxy.doBlocksHide()) {
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
                event.setResult(Event.Result.DENY);
            }
        }
    }
}
