package github.pitbox46.hiddennames;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    static ModConfigSpec.Builder SERVER_BUILDER = new ModConfigSpec.Builder();
    public static ModConfigSpec.BooleanValue DEFAULT_VISIBLE = SERVER_BUILDER
            .comment("General Settings").push("general")
            .comment("Default name visibility")
            .define("default_visible", true);;
    public static ModConfigSpec.BooleanValue BLOCKS_HIDE = SERVER_BUILDER
            .comment("Blocks hide nameplates. False is normal Minecraft nameplate rendering")
            .define("blocks_hide", false);
    public static ModConfigSpec.IntValue CHANGE_OWN_NAME_LEVEL = SERVER_BUILDER
            .comment("The perm level to be able to change your own name")
            .defineInRange("change_own_name_level", 2, 0, 4);
    public static ModConfigSpec.BooleanValue TEAM_OVERRIDE = SERVER_BUILDER
            .comment("Team color overrides individual player color")
            .define("team_override", true);
    public static ModConfigSpec SERVER_CONFIG = SERVER_BUILDER.build();

    static ModConfigSpec.Builder CLIENT_BUILDER = new ModConfigSpec.Builder();
    public static ModConfigSpec.BooleanValue SHOW_OWN = CLIENT_BUILDER
            .comment("Client Settings").push("general")
            .comment("Show your own nametag when in 3rd person view")
            .define("show_own", false);
    public static ModConfigSpec.BooleanValue RENDER_ANIMATIONS = CLIENT_BUILDER
            .comment("Render animations. Will only show colored nametags (or none if hidden) if false")
            .define("render_animations", true);
    public static ModConfigSpec CLIENT_CONFIG = CLIENT_BUILDER.build();
}
