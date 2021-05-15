package github.pitbox46.hiddennames;

import net.minecraftforge.common.ForgeConfigSpec;

public class Config {
    public static final String CATEGORY_GENERAL = "general";

    public static ForgeConfigSpec SERVER_CONFIG;

    public static ForgeConfigSpec.BooleanValue DEFAULT_VISIBLE;

    static {
        ForgeConfigSpec.Builder SERVER_BUILDER = new ForgeConfigSpec.Builder();

        SERVER_BUILDER.comment("General Settings").push(CATEGORY_GENERAL);

        DEFAULT_VISIBLE = SERVER_BUILDER.comment("Default name visibility")
                .define("default_visible", true);

        SERVER_BUILDER.pop();
        SERVER_CONFIG = SERVER_BUILDER.build();
    }
}
