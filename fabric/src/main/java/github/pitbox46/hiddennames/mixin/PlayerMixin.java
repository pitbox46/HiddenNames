package github.pitbox46.hiddennames.mixin;

import github.pitbox46.hiddennames.HiddenNames;
import github.pitbox46.hiddennames.PlayerDuck;
import github.pitbox46.hiddennames.data.NameData;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity implements PlayerDuck {
    @Shadow protected abstract MutableComponent decorateDisplayNameComponent(MutableComponent p_36219_);

    @Shadow public abstract Component getName();

    protected PlayerMixin(EntityType<? extends LivingEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @ModifyArg(method = "getDisplayName", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/scores/PlayerTeam;formatNameForTeam(Lnet/minecraft/world/scores/Team;Lnet/minecraft/network/chat/Component;)Lnet/minecraft/network/chat/MutableComponent;"))
    private Component replaceDisplayName(Component pPlayerName) {
        if (NameData.DATA.containsKey(getUUID())) {
            pPlayerName = HiddenNames.getCorrectedName(NameData.DATA.get(getUUID()).getDisplayName(), getTeam());
        }
        return pPlayerName;
    }

    @Redirect(method = "getDisplayName", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;decorateDisplayNameComponent(Lnet/minecraft/network/chat/MutableComponent;)Lnet/minecraft/network/chat/MutableComponent;"))
    private MutableComponent replaceDecorateDisplayName(Player instance, MutableComponent pDisplayName) {
        if (pDisplayName.getStyle().getClickEvent() == null) {
            pDisplayName = decorateDisplayNameComponent(pDisplayName);
        }
        return pDisplayName;
    }

    @Override
    public Component hiddenNames$getUnmodifiedDisplayName() {
        return this.decorateDisplayNameComponent(this.getName().copy());
    }
}