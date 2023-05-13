package github.pitbox46.hiddennames;

import net.minecraftforge.common.ForgeConfigSpec;

public class Config {
    public static ForgeConfigSpec SERVER_CONFIG;
    public static ForgeConfigSpec CLIENT_CONFIG;

    public static ForgeConfigSpec.BooleanValue DEFAULT_VISIBLE;
    public static ForgeConfigSpec.BooleanValue BLOCKS_HIDE;
    public static ForgeConfigSpec.BooleanValue SHOW_OWN;
    public static ForgeConfigSpec.BooleanValue RENDER_ANIMATIONS;

    //SERVER
    static {
        ForgeConfigSpec.Builder SERVER_BUILDER = new ForgeConfigSpec.Builder();

        SERVER_BUILDER.comment("General Settings").push("general");

        DEFAULT_VISIBLE = SERVER_BUILDER.comment("Default name visibility")
                .define("default_visible", true);

        BLOCKS_HIDE = SERVER_BUILDER.comment("Blocks hide nameplates. False is normal Minecraft nameplate rendering")
                .define("blocks_hide", false);

        SERVER_BUILDER.pop();
        SERVER_CONFIG = SERVER_BUILDER.build();
    }
    //CLIENT
    static {
        ForgeConfigSpec.Builder CLIENT_BUILDER = new ForgeConfigSpec.Builder();

        CLIENT_BUILDER.comment("Client Settings").push("general");

        SHOW_OWN = CLIENT_BUILDER.comment("Show your own nametag when in 3rd person view")
                .define("show_own", false);
        RENDER_ANIMATIONS = CLIENT_BUILDER.comment("Render animations. Will only show colored nametags (or none if hidden) if false")
                .define("render_animations", true);

        CLIENT_BUILDER.pop();
        CLIENT_CONFIG = CLIENT_BUILDER.build();
    }
}
