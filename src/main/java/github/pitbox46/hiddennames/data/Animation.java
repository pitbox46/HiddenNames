package github.pitbox46.hiddennames.data;

import net.minecraftforge.client.event.RenderNameTagEvent;

import java.util.function.BiConsumer;

public record Animation(String key, BiConsumer<RenderNameTagEvent, Long> renderer) {
}
