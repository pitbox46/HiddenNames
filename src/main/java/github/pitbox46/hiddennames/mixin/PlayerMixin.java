package github.pitbox46.hiddennames.mixin;

import github.pitbox46.hiddennames.PlayerDuck;
import github.pitbox46.hiddennames.data.NameData;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.scores.PlayerTeam;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity implements PlayerDuck {
    @Shadow(remap = false) private Component displayname;
    @Shadow(remap = false) @Final private Collection<MutableComponent> prefixes;
    @Shadow(remap = false) @Final private Collection<MutableComponent> suffixes;
    @Shadow protected abstract MutableComponent decorateDisplayNameComponent(MutableComponent p_36219_);

    protected PlayerMixin(EntityType<? extends LivingEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Inject(at = @At(value = "RETURN"), method = "getDisplayName", cancellable = true)
    private void replaceDisplayName(CallbackInfoReturnable<Component> cir) {
        if (NameData.DATA.containsKey(getUUID())) {
            MutableComponent name = (MutableComponent) NameData.DATA.get(getUUID()).getDisplayName();
            if (name.getStyle().getClickEvent() == null) {
                name = decorateDisplayNameComponent(name);
            }
            cir.setReturnValue(name);
        }
    }

    @Override
    public Component hiddenNames$getUnmodifiedDisplayName() {
        if (this.displayname == null) this.displayname = net.neoforged.neoforge.event.EventHooks.getPlayerDisplayName((Player)(Object)this, this.getName());
        MutableComponent mutablecomponent = Component.literal("");
        mutablecomponent = prefixes.stream().reduce(mutablecomponent, MutableComponent::append);
        mutablecomponent = mutablecomponent.append(PlayerTeam.formatNameForTeam(this.getTeam(), this.displayname));
        mutablecomponent = suffixes.stream().reduce(mutablecomponent, MutableComponent::append);
        return this.decorateDisplayNameComponent(mutablecomponent);
    }
}