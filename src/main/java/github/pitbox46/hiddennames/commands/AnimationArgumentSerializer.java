package github.pitbox46.hiddennames.commands;

import com.google.gson.JsonObject;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.network.FriendlyByteBuf;

public class AnimationArgumentSerializer implements ArgumentTypeInfo<AnimationArgument, AnimationArgumentSerializer.Template> {

    @Override
    public void serializeToNetwork(Template template, FriendlyByteBuf buf) {
    }

    @Override
    public Template deserializeFromNetwork(FriendlyByteBuf buf) {
        return new Template();
    }

    @Override
    public void serializeToJson(Template template, JsonObject json) {
    }

    @Override
    public Template unpack(AnimationArgument arg) {
        return new Template();
    }

    public class Template implements ArgumentTypeInfo.Template<AnimationArgument> {

        @Override
        public AnimationArgument instantiate(CommandBuildContext ctx) {
            return AnimationArgument.animationArgument();
        }

        @Override
        public ArgumentTypeInfo<AnimationArgument, ?> type() {
            return AnimationArgumentSerializer.this;
        }
    }
}
