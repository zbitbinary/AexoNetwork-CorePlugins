package fun.aexo.lobby.listeners;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.player.PlayerInteractEntityEvent;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;
import fun.aexo.lobby.LobbyLoader;

public class NPCListener implements Listener {

    @EventHandler
    public void onNPCClick(PlayerInteractEntityEvent event) {
        if (event.getEntity().namedTag.contains("LobbyNPC")) {
            event.setCancelled(true);
            handleNPCTransfer(event.getPlayer(), event.getEntity().namedTag.getString("NPCKey"));
        }
    }

    @EventHandler
    public void onNPCDamage(EntityDamageEvent event) {
        if (event.getEntity().namedTag.contains("LobbyNPC")) {
            event.setCancelled(true);
            if (event instanceof EntityDamageByEntityEvent damageEvent) {
                if (damageEvent.getDamager() instanceof Player player) {
                    handleNPCTransfer(player, event.getEntity().namedTag.getString("NPCKey"));
                }
            }
        }
    }

    private void handleNPCTransfer(Player player, String npcKey) {
        Config npcConfig = LobbyLoader.getInstance().getNpcConfig();

        if (npcConfig.exists("spawns." + npcKey)) {
            String ip = npcConfig.getString("spawns." + npcKey + ".ip", "127.0.0.1");
            int port = npcConfig.getInt("spawns." + npcKey + ".port", 19132);
            String name = TextFormat.colorize('&', npcConfig.getString("spawns." + npcKey + ".name", npcKey));

            player.sendMessage(TextFormat.YELLOW + "Connecting to " + name + TextFormat.YELLOW + "...");
            player.transfer(ip, port);
        } else {
            player.sendMessage(TextFormat.RED + "Target server configuration for '" + npcKey + "' not found in npcs.yml!");
        }
    }
}
