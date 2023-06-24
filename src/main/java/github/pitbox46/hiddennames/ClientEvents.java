package github.pitbox46.hiddennames;

import github.pitbox46.hiddennames.data.Animations;
import github.pitbox46.hiddennames.data.NameData;
import github.pitbox46.hiddennames.network.ClientProxy;
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
        if (event.getEntity() instanceof Player) {
            NameData nameData = NameData.DATA.get(event.getEntity().getUUID());

            if (event.getEntity() == localPlayer && Config.SHOW_OWN.get() && !event.getEntity().isSpectator())
                event.setResult(Event.Result.ALLOW);
            if (nameData == null)
                return;
            if (nameData.getAnimation() != Animations.HIDDEN && ClientProxy.doBlocksHide()) {
                Vec3 vector3d = localPlayer.getEyePosition(event.getPartialTick());
                Vec3 vector3d1 = event.getEntity().getEyePosition(event.getPartialTick());
                if (localPlayer.getLevel().clip(new ClipContext(vector3d, vector3d1, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, localPlayer)).getType() != HitResult.Type.MISS) {
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
            Component displayName = NameData.DATA.get(event.getEntity().getUUID()).getDisplayName();
            event.setDisplayname(displayName);
            PlayerInfo playerInfo = Minecraft.getInstance().player.connection.getPlayerInfo(event.getEntity().getUUID());
            if (playerInfo != null) {
                playerInfo.setTabListDisplayName(displayName);
            }
        }
    }

    @SubscribeEvent
    public static void onClientChatReceived(ClientChatReceivedEvent event) {
        Component component = event.getMessage();
        if(!NameData.DATA.containsKey(event.getSender())) {
            return;
        }
        if(component.getContents() instanceof TranslatableContents contents && contents.getArgs().length > 0) {
            if(contents.getArgs()[0] instanceof MutableComponent nameComponent && nameComponent.getSiblings().size() > 0) {
                nameComponent.getSiblings().remove(0);
                nameComponent.getSiblings().add(0, NameData.DATA.get(event.getSender()).getDisplayName());
            }
        }
        event.setMessage(component);
    }
}
