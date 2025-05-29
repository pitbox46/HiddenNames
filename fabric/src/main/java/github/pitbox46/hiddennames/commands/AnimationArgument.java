package github.pitbox46.hiddennames.commands;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import github.pitbox46.hiddennames.HiddenNames;
import github.pitbox46.hiddennames.data.Animation;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class AnimationArgument implements ArgumentType<Animation> {
    private static final Dynamic2CommandExceptionType INVALID_ANIMATION = new Dynamic2CommandExceptionType(
            (found, constants) -> Component.literal(String.format("Animation key must be one of %s, found %s", constants, found))
    );

    private AnimationArgument() {
    }

    public static AnimationArgument animationArgument() {
        return new AnimationArgument();
    }

    @Override
    public Animation parse(StringReader reader) throws CommandSyntaxException {
        ResourceLocation key = ResourceLocation.read(reader);
        if (!HiddenNames.ANIMATION_REGISTRY.keySet().contains(key)) {
            throw INVALID_ANIMATION.create(key, HiddenNames.ANIMATION_REGISTRY.keySet().stream().map(ResourceLocation::toString).toList());
        }
        return HiddenNames.ANIMATION_REGISTRY.getValue(key);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return SharedSuggestionProvider.suggest(HiddenNames.ANIMATION_REGISTRY.keySet().stream().map(ResourceLocation::toString), builder);
    }

    @Override
    public Collection<String> getExamples() {
        return HiddenNames.ANIMATION_REGISTRY.keySet().stream().limit(5).map(ResourceLocation::toString).toList();
    }
}
