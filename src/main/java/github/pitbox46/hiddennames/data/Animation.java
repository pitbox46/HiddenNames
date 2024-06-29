package github.pitbox46.hiddennames.data;

import github.pitbox46.hiddennames.HiddenNames;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.RenderNameTagEvent;

import java.util.function.BiConsumer;

public record Animation(ResourceLocation key, BiConsumer<RenderNameTagEvent, Long> renderer) {
    public static final ResourceLocation HIDDEN_KEY = ResourceLocation.fromNamespaceAndPath(HiddenNames.MODID, "hidden");

    public void render(RenderNameTagEvent event, long tick) {
        renderer().accept(event, tick);
    }

    public boolean isHidden() {
        return key().equals(HIDDEN_KEY);
    }
}
