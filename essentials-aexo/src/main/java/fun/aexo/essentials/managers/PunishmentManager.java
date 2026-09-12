package fun.aexo.essentials.managers;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerChatEvent;
import cn.nukkit.event.player.PlayerMoveEvent;
import cn.nukkit.utils.TextFormat;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PunishmentManager implements Listener {

    private static final Set<UUID> mutedPlayers = new HashSet<>();
    private static final Set<UUID> frozenPlayers = new HashSet<>();

    public static void mute(UUID uuid) { mutedPlayers.add(uuid); }
    public static void unmute(UUID uuid) { mutedPlayers.remove(uuid); }
    public static boolean isMuted(UUID uuid) { return mutedPlayers.contains(uuid); }

    public static void freeze(UUID uuid) { frozenPlayers.add(uuid); }
    public static void unfreeze(UUID uuid) { frozenPlayers.remove(uuid); }
    public static boolean isFrozen(UUID uuid) { return frozenPlayers.contains(uuid); }

    @EventHandler
    public void onChat(PlayerChatEvent event) {
        if (isMuted(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(TextFormat.RED + "You are currently muted and cannot speak in chat!");
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (isFrozen(event.getPlayer().getUniqueId())) {
            // Cancel movement if player changes position
            if (event.getFrom().getX() != event.getTo().getX() || event.getFrom().getZ() != event.getTo().getZ()) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(TextFormat.RED + "You are frozen by a staff member! Do not log out.");
            }
        }
    }
}