package fun.aexo.lobby.managers;

import cn.nukkit.Player;
import cn.nukkit.utils.TextFormat;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;

public class RankManager {

    private static LuckPerms luckPerms;

    public static void init() {
        try {
            luckPerms = LuckPermsProvider.get();
        } catch (IllegalStateException e) {
            luckPerms = null;
        }
    }

    public static String getPrefix(Player player) {
        if (luckPerms == null) return TextFormat.GRAY + "[Player] ";
        
        User user = luckPerms.getUserManager().getUser(player.getUniqueId());
        if (user == null) return TextFormat.GRAY + "[Player] ";

        String prefix = user.getCachedData().getMetaData().getPrefix();
        return prefix != null ? TextFormat.colorize('&', prefix) : TextFormat.GRAY + "[Player] ";
    }

    public static String getPrimaryGroup(Player player) {
        if (luckPerms == null) return "Default";
        
        User user = luckPerms.getUserManager().getUser(player.getUniqueId());
        if (user == null) return "Default";

        return user.getPrimaryGroup().toUpperCase();
    }
}