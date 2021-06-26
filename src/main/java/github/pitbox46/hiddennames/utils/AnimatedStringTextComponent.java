package github.pitbox46.hiddennames.utils;

import net.minecraft.util.text.StringTextComponent;

public class AnimatedStringTextComponent extends StringTextComponent {
    public enum Animation {
        NONE,
        HIDDEN,
        BREATH,
        RAINBOW,
        CYCLE,
    }

    private final String msg;
    private final Animation anime;

    public AnimatedStringTextComponent(String msg, Animation anime) {
        super(msg);
        this.msg = msg;
        this.anime = anime;
    }

    public Animation getAnimation() {
        return anime;
    }

    @Override
    public AnimatedStringTextComponent copyRaw() {
        return new AnimatedStringTextComponent(this.msg, this.anime);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (!(object instanceof AnimatedStringTextComponent)) {
            return false;
        } else {
            AnimatedStringTextComponent animatedStringTextComponent = (AnimatedStringTextComponent) object;
            return this.anime == animatedStringTextComponent.getAnimation() && super.equals(object);
        }
    }

    @Override
    public String toString() {
        return "TextComponent{text='" + this.msg + '\'' + ", siblings=" + this.siblings + ", style=" + this.getStyle() + ", animation=" + this.anime + '}';
    }
}
