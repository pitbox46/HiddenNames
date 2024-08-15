package github.pitbox46.hiddennames.mixin;

import github.pitbox46.hiddennames.HiddenNames;
import github.pitbox46.hiddennames.data.NameData;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.scores.PlayerTeam;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(PlayerTabOverlay.class)
public abstract class PlayerTabOverlayMixin {
    @Shadow protected abstract Component decorateName(PlayerInfo playerInfo, MutableComponent mutableComponent);

    /**
     * @author pitbox46
     * @reason easiest method
     */
    @Overwrite
    public Component getNameForDisplay(PlayerInfo playerInfo) {
        if (NameData.DATA.containsKey(playerInfo.getProfile().getId())) {
            Component name = HiddenNames.getCorrectedName(
                    NameData.DATA.get(playerInfo.getProfile().getId()).getDisplayName(),
                    playerInfo.getTeam()
            );
            return this.decorateName(playerInfo, PlayerTeam.formatNameForTeam(playerInfo.getTeam(), name));
        }

        return playerInfo.getTabListDisplayName() != null
                ? this.decorateName(playerInfo, playerInfo.getTabListDisplayName().copy())
                : this.decorateName(playerInfo, PlayerTeam.formatNameForTeam(playerInfo.getTeam(), Component.literal(playerInfo.getProfile().getName())));
    }
}
