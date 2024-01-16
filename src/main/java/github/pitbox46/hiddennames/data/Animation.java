package github.pitbox46.hiddennames.data;


import net.neoforged.neoforge.client.event.RenderNameTagEvent;

import java.util.function.BiConsumer;

public record Animation(String key, BiConsumer<RenderNameTagEvent, Long> renderer) {
    public void render(RenderNameTagEvent event, long tick) {
        renderer().accept(event, tick);
    }
}
