package github.pitbox46.hiddennames;

import github.pitbox46.hiddennames.network.BlocksHidePacket;
import github.pitbox46.hiddennames.network.PacketHandler;
import github.pitbox46.hiddennames.utils.CSVHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.fml.network.PacketDistributor;

public class HiddenNamesAPI {

    /**
     * Sets the visibility of a player's name
     * 
     * @param player  Player to set visibility of
     * @param visible Visibility to set
     */
    public static void setPlayerVisibility(PlayerEntity player, boolean visible) {
        CSVHandler.replaceEntry(HiddenNames.dataFile, player.getName().toString(), CSVHandler.Columns.NAME_VISIBLE,
                String.valueOf(visible));
        CSVHandler.updateClients(HiddenNames.dataFile, PacketHandler.CHANNEL);
    }

    /**
     * Enables or disables the hiding of name tags by blocks
     * 
     * @param blocksHide
     */
    public static void setConfigBlockHide(boolean blocksHide) {

        Config.BLOCKS_HIDE.set(blocksHide);
        Config.BLOCKS_HIDE.save();
        PacketHandler.CHANNEL.send(PacketDistributor.ALL.noArg(), new BlocksHidePacket(Config.BLOCKS_HIDE.get()));

    }

    /**
     * Sets the default visibility of a player's name
     * 
     * @param defaultVisible Default visibility to set
     */
    public static void setConfigDefaultVisibility(boolean defaultVisible) {
        Config.DEFAULT_VISIBLE.set(defaultVisible);
        Config.DEFAULT_VISIBLE.save();
    }

}
