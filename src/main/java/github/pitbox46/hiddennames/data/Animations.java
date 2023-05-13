package github.pitbox46.hiddennames.data;

import github.pitbox46.hiddennames.Config;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.eventbus.api.Event;
import org.apache.commons.lang3.RandomStringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.IntStream;

public class Animations {
    private static final Map<String, Animation> ANIMATIONS = new HashMap<>();

    //We don't actually need these static fields for most of these. They're just for convenience if they're needed in the code somewhere later
    public static final Animation NO_ANIMATION = regAnimation(new Animation("null", (event, tick) -> {
        event.setContent(NameData.DATA.get(event.getEntity().getUUID()).getDisplayName());
    }));
    public static final Animation HIDDEN = regAnimation(new Animation("hidden", (event, tick) -> event.setResult(Event.Result.DENY)));
    public static final Animation BREATHE = regAnimation(new Animation("breathe", (event, tick) -> {
        int amp = 60;
        int cycle = 180;

        Component displayName = NameData.DATA.get(event.getEntity().getUUID()).getDisplayName();


        TextColor color = displayName.getStyle().getColor();
        if (color == null) {
            color = TextColor.fromLegacyFormat(ChatFormatting.WHITE);
        }
        int primaryColor = color.getValue();
        int red = FastColor.ARGB32.red(primaryColor);
        int green = FastColor.ARGB32.green(primaryColor);
        int blue = FastColor.ARGB32.blue(primaryColor);

        double sin = Math.sin((tick % 360) * 2 * Math.PI / cycle);
        double newRed = red + (amp * sin);
        double newGreen = green + (amp * sin);
        double newBlue = blue + (amp * sin);

        MutableComponent newName = displayName.copy();
        newName.setStyle(displayName.getStyle().withColor(TextColor.fromRgb(FastColor.ARGB32.color(255, roundToByte(newRed), roundToByte(newGreen), roundToByte(newBlue)))));

        event.setContent(newName);
    }));
    public static final Animation RAINBOW = regAnimation(new Animation("rainbow", (event, tick) -> {
        Component displayName = NameData.DATA.get(event.getEntity().getUUID()).getDisplayName();

        MutableComponent newName = Component.literal("");
        int i = 0;
        for (char c : displayName.getString().toCharArray()) {
            newName.append(Component.literal(String.valueOf(c)).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(Mth.hsvToRgb(((tick + 3 * i) % 180) / 180F, 1, 1)))));
            i++;
        }
        event.setContent(newName);
    }));
    public static final Animation CYCLE = regAnimation(new Animation("cycle", (event, tick) -> {
        Component displayName = NameData.DATA.get(event.getEntity().getUUID()).getDisplayName();

        int toNext = 80;

        Random rng = new Random(event.getEntity().getId());
        IntStream stream = rng.ints();

        TextColor color1 = TextColor.fromRgb(stream.skip(tick / toNext).findFirst().orElseThrow());
        TextColor color2 = TextColor.fromRgb(rng.nextInt());

        MutableComponent newName = displayName.copy();
        newName.setStyle(newName.getStyle().withColor(blendColors(color1, color2, (float) (toNext - tick % toNext) / toNext)));

        event.setContent(newName);
    }));

    /**
     * This method is all you will have to call to register an animation.
     * @param animation
     * @return
     */
    public static Animation regAnimation(Animation animation) {
        ANIMATIONS.put(animation.key(), animation);
        return animation;
    }

    @Nonnull
    public static Animation getAnimation(String key) {
        return ANIMATIONS.getOrDefault(key, NO_ANIMATION);
    }

    /**
     * Does not return {@link Animations#NO_ANIMATION} if there is no value (returns null instead)
     */
    @Nullable
    public static Animation getAnimationUnsafe(String key) {
        return ANIMATIONS.get(key);
    }

    public static Set<String> getKeys() {
        return ANIMATIONS.keySet();
    }

    //Helpers
    private static int roundToByte(double number) {
        if (number < 0) return 0;
        if (number > 255) return 255;
        return (int) Math.round(number);
    }

    private static TextColor blendColors(TextColor primary, TextColor secondary, float percent) {
        if (percent > 1) percent = 1;
        if (percent < 0) percent = 0;
        int red = (int) (FastColor.ARGB32.red(primary.getValue()) * percent + FastColor.ARGB32.red(secondary.getValue()) * (1 - percent));
        int green = (int) (FastColor.ARGB32.green(primary.getValue()) * percent + FastColor.ARGB32.green(secondary.getValue()) * (1 - percent));
        int blue = (int) (FastColor.ARGB32.blue(primary.getValue()) * percent + FastColor.ARGB32.blue(secondary.getValue()) * (1 - percent));

        return TextColor.fromRgb(FastColor.ARGB32.color(255, red, green, blue));
    }
}
