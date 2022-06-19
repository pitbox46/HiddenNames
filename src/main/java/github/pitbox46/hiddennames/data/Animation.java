package github.pitbox46.hiddennames.data;

import net.minecraftforge.client.event.RenderNameplateEvent;

import java.util.function.BiConsumer;

public record Animation(String key, BiConsumer<RenderNameplateEvent, Long> renderer) {
}
