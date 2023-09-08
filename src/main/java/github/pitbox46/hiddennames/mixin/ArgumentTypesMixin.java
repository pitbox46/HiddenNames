package github.pitbox46.hiddennames.mixin;

import github.pitbox46.hiddennames.commands.AnimationArgument;
import net.minecraft.commands.synchronization.ArgumentTypes;
import net.minecraft.commands.synchronization.EmptyArgumentSerializer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ArgumentTypes.class)
public class ArgumentTypesMixin {
    @Inject(method = "bootStrap", at = @At(value = "RETURN"))
    private static void afterBootstrap(CallbackInfo ci) {
        ArgumentTypes.register("animation", AnimationArgument.class, new EmptyArgumentSerializer<>(AnimationArgument::animationArgument));
    }
}
