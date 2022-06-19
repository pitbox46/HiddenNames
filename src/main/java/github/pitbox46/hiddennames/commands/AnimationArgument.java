package github.pitbox46.hiddennames.commands;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import github.pitbox46.hiddennames.data.Animation;
import github.pitbox46.hiddennames.data.Animations;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.TextComponent;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class AnimationArgument implements ArgumentType<Animation> {
    private static final Dynamic2CommandExceptionType INVALID_ANIMATION = new Dynamic2CommandExceptionType(
            (found, constants) -> new TextComponent(String.format("Animation key must be one of %s, found %s", constants, found)));

    private AnimationArgument() {
    }

    public static AnimationArgument animationArgument() {
        return new AnimationArgument();
    }

    @Override
    public Animation parse(StringReader reader) throws CommandSyntaxException {
        String string = reader.readUnquotedString();
        Animation animation = Animations.getAnimationUnsafe(string);
        if (animation == null)
            throw INVALID_ANIMATION.create(string, Animations.getKeys().toString());
        return animation;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return SharedSuggestionProvider.suggest(Animations.getKeys(), builder);
    }

    @Override
    public Collection<String> getExamples() {
        return Animations.getKeys();
    }
}
