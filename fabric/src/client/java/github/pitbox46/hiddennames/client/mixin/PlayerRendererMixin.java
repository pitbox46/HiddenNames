package github.pitbox46.hiddennames.client.mixin;

import github.pitbox46.hiddennames.Config;
import github.pitbox46.hiddennames.HiddenNames;
import github.pitbox46.hiddennames.client.ClientPayloadHandler;
import github.pitbox46.hiddennames.data.Animation;
import github.pitbox46.hiddennames.data.NameData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Team;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerRenderer.class)
public abstract class PlayerRendererMixin extends LivingEntityRenderer<AbstractClientPlayer, PlayerRenderState, PlayerModel> {
    public PlayerRendererMixin(EntityRendererProvider.Context context, PlayerModel entityModel, float f) {
        super(context, entityModel, f);
    }

    @Inject(method = "extractRenderState",
            at = @At("TAIL"))
    public void extractRenderState(AbstractClientPlayer player, PlayerRenderState playerRenderState, float partialTick, CallbackInfo info) {
        var nameData = NameData.DATA.get(player.getUUID());
        if (nameData == null || (!Config.RENDER_ANIMATIONS.get() && !nameData.getAnimation().isHidden())) {
            return;
        }

        Player localPlayer = Minecraft.getInstance().player;
        if (localPlayer != null && !nameData.getAnimation().isHidden() && ClientPayloadHandler.doBlocksHide()) {
            Vec3 vector3d = localPlayer.getEyePosition(partialTick);
            Vec3 vector3d1 = player.getEyePosition(partialTick);
            if (localPlayer.level().clip(new ClipContext(vector3d, vector3d1, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, localPlayer)).getType() != HitResult.Type.MISS) {
                hideNameTag(playerRenderState);
            }
        }
        Team team = player.getTeam();

        Animation.Return returnData = nameData.getAnimation()
                .renderer()
                .apply(new Animation.Input(
                        player,
                        playerRenderState.nameTag,
                        HiddenNames.getCorrectedName(nameData.getDisplayName(), team),
                        Minecraft.getInstance().level.getGameTime() + player.getId() * 21L
                ));

        if (returnData.show()) {
            playerRenderState.nameTag = HiddenNames.getFullNameplate(returnData.name(), team);
        } else {
            hideNameTag(playerRenderState);
        }
    }

    @Unique
    private void hideNameTag(PlayerRenderState playerRenderState) {
        playerRenderState.nameTag = null;
        playerRenderState.nameTagAttachment = null;
    }
}
