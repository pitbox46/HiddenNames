package github.pitbox46.hiddennames.client.mixin;

import github.pitbox46.hiddennames.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin {
    @Inject(method = "shouldShowName(Lnet/minecraft/world/entity/LivingEntity;)Z", at = @At(value = "RETURN", ordinal = 6), cancellable = true)
    private void shouldShowName(LivingEntity livingEntity, CallbackInfoReturnable<Boolean> cir) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer localPlayer = minecraft.player;
        boolean bl = !livingEntity.isInvisibleTo(localPlayer);
        cir.setReturnValue(
                Minecraft.renderNames() &&
                        (livingEntity != minecraft.getCameraEntity() || Config.SHOW_OWN.get()) &&
                        bl &&
                        !livingEntity.isVehicle()
        );
    }
}
