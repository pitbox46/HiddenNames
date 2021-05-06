package github.pitbox46.hiddennames;

import net.minecraftforge.common.ForgeConfigSpec;

public class Config {
    public static final String CATEGORY_GENERAL = "general";

    public static ForgeConfigSpec SERVER_CONFIG;

    public static ForgeConfigSpec.BooleanValue NAMES_VISIBLE;

    static {
        ForgeConfigSpec.Builder SERVER_BUILDER = new ForgeConfigSpec.Builder();

        SERVER_BUILDER.comment("General Settings").push(CATEGORY_GENERAL);

        NAMES_VISIBLE = SERVER_BUILDER.comment("Player nameplates are visible")
                .define("names_visible", true);

        SERVER_BUILDER.pop();
        SERVER_CONFIG = SERVER_BUILDER.build();
    }
}
