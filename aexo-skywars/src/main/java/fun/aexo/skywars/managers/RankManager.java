package fun.aexo.skywars.managers;

import cn.nukkit.Player;
import cn.nukkit.utils.TextFormat;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;

public class RankManager {

    private static LuckPerms luckPerms;

    // Call this in SkyWarsLoader onEnable() after registering event listeners
    public static void init() {
        try {
            luckPerms = LuckPermsProvider.get();
        } catch (IllegalStateException e) {
            luckPerms = null;
        }
    }

    /**
     * Gets the primary group name for a player (e.g. "admin", "vip", "default").
     */
    public static String getPrimaryGroup(Player player) {
        if (luckPerms == null) return "default";
        
        User user = luckPerms.getUserManager().getUser(player.getUniqueId());
        if (user != null) {
            return user.getPrimaryGroup();
        }
        return "default";
    }

    /**
     * Gets the prefix configured in LuckPerms meta/nodes.
     */
    public static String getPrefix(Player player) {
        if (luckPerms == null) return "";

        User user = luckPerms.getUserManager().getUser(player.getUniqueId());
        if (user != null) {
            String prefix = user.getCachedData().getMetaData().getPrefix();
            return prefix != null ? TextFormat.colorize('&', prefix) : "";
        }
        return "";
    }

    /**
     * Updates the player's nametag and display name using their LuckPerms prefix.
     */
    public static void updatePlayerNameTag(Player player) {
        String prefix = getPrefix(player);
        String formattedName = prefix + player.getName();
        
        player.setNameTag(formattedName);
        player.setDisplayName(formattedName);
    }
}
