package github.pitbox46.hiddennames.client.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import github.pitbox46.hiddennames.Config;
import github.pitbox46.hiddennames.client.ClientPayloadHandler;
import github.pitbox46.hiddennames.data.Animation;
import github.pitbox46.hiddennames.data.NameData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlayerRenderer.class)
public abstract class PlayerRendererMixin extends LivingEntityRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
    public PlayerRendererMixin(EntityRendererProvider.Context context, PlayerModel<AbstractClientPlayer> entityModel, float f) {
        super(context, entityModel, f);
    }

    @Redirect(
            method = "renderNameTag(Lnet/minecraft/client/player/AbstractClientPlayer;Lnet/minecraft/network/chat/Component;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IF)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/LivingEntityRenderer;renderNameTag(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/network/chat/Component;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IF)V", ordinal = 1)
    )
    private void renderNameTag(LivingEntityRenderer instance, Entity entity, Component component, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, float partialTick) {
        Player localPlayer = Minecraft.getInstance().player;

        if (entity instanceof AbstractClientPlayer player) {
            NameData nameData = NameData.DATA.get(player.getUUID());

            if (nameData==null || (!Config.RENDER_ANIMATIONS.get() && !nameData.getAnimation().isHidden())) {
                super.renderNameTag((AbstractClientPlayer) entity, component, poseStack, multiBufferSource, i, partialTick);
                return;
            }
            if (!nameData.getAnimation().isHidden() && ClientPayloadHandler.doBlocksHide()) {
                Vec3 vector3d = localPlayer.getEyePosition(partialTick);
                Vec3 vector3d1 = entity.getEyePosition(partialTick);
                if (localPlayer.level().clip(new ClipContext(vector3d, vector3d1, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, localPlayer)).getType()!=HitResult.Type.MISS) {
                    return;
                }
            }

            Animation.Return returnData = nameData.getAnimation().renderer().apply(new Animation.Input(
                    player,
                    component,
                    Minecraft.getInstance().level.getGameTime() + player.getId() * 21L
            ));
            if (returnData.show()) {
                super.renderNameTag(player, returnData.name(), poseStack, multiBufferSource, i, partialTick);
            }
        }
    }
}
