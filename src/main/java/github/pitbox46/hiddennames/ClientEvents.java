package github.pitbox46.hiddennames;

import github.pitbox46.hiddennames.data.Animations;
import github.pitbox46.hiddennames.data.NameData;
import github.pitbox46.hiddennames.network.ClientProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderNameplateEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class ClientEvents {
    @SubscribeEvent
    public static void onRenderNameplate(RenderNameplateEvent event) {
        Player localPlayer = Minecraft.getInstance().player;

        if (event.getEntity() instanceof Player) {
            NameData nameData = NameData.DATA.get(event.getEntity().getUUID());

            if (nameData == null)
                return;

            if (nameData.getAnimation() != Animations.HIDDEN && ClientProxy.doBlocksHide()) {
                Vec3 vector3d = new Vec3(localPlayer.getX(), localPlayer.getEyePosition().y, localPlayer.getZ());
                Vec3 vector3d1 = new Vec3(event.getEntity().getX(), event.getEntity().getY(1) + 0.5F, event.getEntity().getZ());
                if (localPlayer.level.clip(new ClipContext(vector3d, vector3d1, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, localPlayer)).getType() != HitResult.Type.MISS) {
                    event.setResult(Event.Result.DENY);
                    return;
                }
            }
            //The addition is an offset so each player doesn't have the same animation go at the same time
            nameData.getAnimation().renderer().accept(event, Minecraft.getInstance().level.getGameTime() + event.getEntity().getId() * 21L);
        }
    }

    /**
     * Used for testing new animations.
     * Testing on a mob is much easier than testing on a player (requires two Minecraft instances).
     */
    @SubscribeEvent
    public static void onRenderMobNameplate(RenderNameplateEvent event) {
//        double tick = Minecraft.getInstance().player.world.getGameTime() + event.getPartialTicks();
//
//        if(event.getEntity() instanceof MobEntity && event.getContent().getUnformattedComponentText().equals("thisIsForTesting")) {
//
//        }
//        previousTick = tick;
    }

    @SubscribeEvent
    public void onNameFormat(PlayerEvent.NameFormat event) {
        if (NameData.DATA.get(event.getPlayer().getUUID()) != null && event.getPlayer() instanceof AbstractClientPlayer) {
            Component displayName = NameData.DATA.get(event.getPlayer().getUUID()).getDisplayName();
            event.setDisplayname(displayName);
            PlayerInfo playerInfo = Minecraft.getInstance().player.connection.getPlayerInfo(event.getPlayer().getUUID());
            if (playerInfo != null) {
                playerInfo.setTabListDisplayName(displayName);
            }
        }
    }
}
