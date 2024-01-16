package github.pitbox46.hiddennames;


import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    public static ModConfigSpec SERVER_CONFIG;
    public static ModConfigSpec CLIENT_CONFIG;

    public static ModConfigSpec.BooleanValue DEFAULT_VISIBLE;
    public static ModConfigSpec.BooleanValue BLOCKS_HIDE;
    public static ModConfigSpec.BooleanValue SHOW_OWN;
    public static ModConfigSpec.BooleanValue RENDER_ANIMATIONS;

    //SERVER
    static {
        ModConfigSpec.Builder SERVER_BUILDER = new ModConfigSpec.Builder();

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
        ModConfigSpec.Builder CLIENT_BUILDER = new ModConfigSpec.Builder();

        CLIENT_BUILDER.comment("Client Settings").push("general");

        SHOW_OWN = CLIENT_BUILDER.comment("Show your own nametag when in 3rd person view")
                .define("show_own", false);
        RENDER_ANIMATIONS = CLIENT_BUILDER.comment("Render animations. Will only show colored nametags (or none if hidden) if false")
                .define("render_animations", true);

        CLIENT_BUILDER.pop();
        CLIENT_CONFIG = CLIENT_BUILDER.build();
    }
}
