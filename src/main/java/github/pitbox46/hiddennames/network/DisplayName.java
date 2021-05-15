package github.pitbox46.hiddennames.network;

import net.minecraft.util.text.ITextComponent;

import java.util.UUID;

public class DisplayName {
    private ITextComponent name;
    private boolean visible;

    public DisplayName(ITextComponent name, boolean visible) {
        this.name = name;
        this.visible = visible;
    }

    public ITextComponent getName() {
        return name;
    }

    public boolean isVisible() {
        return visible;
    }
}
