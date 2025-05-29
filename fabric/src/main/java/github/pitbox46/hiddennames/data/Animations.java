package github.pitbox46.hiddennames.data;

import github.pitbox46.hiddennames.HiddenNames;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.Random;
import java.util.function.Function;
import java.util.stream.IntStream;

public class Animations {
    public static void init() {}

    //We don't actually need these static fields for most of these. They're just for convenience if they're needed in the code somewhere later
    public static final Animation NO_ANIMATION = register(
            "null",
            (input) -> new Animation.Return(input.displayName(), true)
    );
    public static final Animation HIDDEN = register(
            "hidden",
            (input) -> new Animation.Return(input.ogName(), false)
    );
    public static final Animation BREATHE = register(
            "breathe",
            (input) -> {
                int amp = 60;
                int cycle = 180;

                Component displayName = input.displayName();

                TextColor color = displayName.getStyle().getColor();
                if (color == null) {
                    color = TextColor.fromLegacyFormat(ChatFormatting.WHITE);
                }
                int primaryColor = color.getValue();
                int red = ARGB.red(primaryColor);
                int green = ARGB.green(primaryColor);
                int blue = ARGB.blue(primaryColor);

                double sin = Math.sin((input.tick() % 360) * 2 * Math.PI / cycle);
                double newRed = red + (amp * sin);
                double newGreen = green + (amp * sin);
                double newBlue = blue + (amp * sin);

                MutableComponent newName = displayName.copy();
                newName.setStyle(displayName.getStyle().withColor(TextColor.fromRgb(ARGB.color(255, roundToByte(newRed), roundToByte(newGreen), roundToByte(newBlue)))));

                return new Animation.Return(newName, true);
            }
    );
    public static final Animation RAINBOW = register(
            "rainbow",
            (input) -> {
                Component displayName = input.displayName();

                MutableComponent newName = Component.literal("");
                int i = 0;
                for (char c : displayName.getString().toCharArray()) {
                    newName.append(Component.literal(String.valueOf(c)).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(Mth.hsvToRgb(((input.tick() + 3 * i) % 180) / 180F, 1, 1)))));
                    i++;
                }
                return new Animation.Return(newName, true);
            }
    );
    public static final Animation CYCLE = register(
            "cycle",
            (input) -> {
                Component displayName = input.displayName();

                int toNext = 80;

                Random rng = new Random(input.player().getId());
                IntStream stream = rng.ints();

                TextColor color1 = TextColor.fromRgb(stream.skip(input.tick() / toNext).findFirst().orElseThrow());
                TextColor color2 = TextColor.fromRgb(rng.nextInt());

                MutableComponent newName = displayName.copy();
                newName.setStyle(newName.getStyle().withColor(blendColors(color1, color2, (float) (toNext - input.tick() % toNext) / toNext)));

                return new Animation.Return(newName, true);
            }
    );
    public static final Animation RANDOM = register(
            "random",
            (input) -> {
                int toNext = 15;
                Random rng = new Random(input.player().getId() * (input.tick() / toNext));

                String name = RandomStringUtils.random(rng.nextInt(3, 10), 32, 127, false, false, null, rng);

                MutableComponent newName = Component.literal("");
                for (char c : name.toCharArray()) {
                    newName.append(Component.literal(String.valueOf(c)).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(rng.nextInt()))));
                }

                return new Animation.Return(newName, true);
            }
    );

    public static Animation register(String name, Function<Animation.Input, Animation.Return> renderer) {
        ResourceLocation rl = ResourceLocation.fromNamespaceAndPath(HiddenNames.MODID, name);
        return Registry.register(HiddenNames.ANIMATION_REGISTRY, rl, new Animation(rl, renderer));
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
        int red = (int) (ARGB.red(primary.getValue()) * percent + ARGB.red(secondary.getValue()) * (1 - percent));
        int green = (int) (ARGB.green(primary.getValue()) * percent + ARGB.green(secondary.getValue()) * (1 - percent));
        int blue = (int) (ARGB.blue(primary.getValue()) * percent + ARGB.blue(secondary.getValue()) * (1 - percent));

        return TextColor.fromRgb(ARGB.color(255, red, green, blue));
    }
}
