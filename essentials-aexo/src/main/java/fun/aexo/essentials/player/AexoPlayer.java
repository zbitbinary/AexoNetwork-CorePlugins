package fun.aexo.essentials.player;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.utils.TextFormat;
import fun.aexo.essentials.Loader;

public class AexoPlayer implements Listener {

    private final Loader plugin;

    public AexoPlayer(Loader plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // Custom join message and welcome chat message
        event.setJoinMessage(TextFormat.GREEN + "[+] " + player.getName());
        player.sendMessage(TextFormat.YELLOW + "Welcome to Aexo Network " + player.getName() + " Enjoy your stay");

        // Initialize HUD elements (BossBar)
        plugin.setupPlayerHud(player);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        event.setQuitMessage(TextFormat.RED + "[-] " + player.getName());

        // Cleanup BossBar reference from memory
        plugin.removeBossBar(player);
    }

    // Protection Handling

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        event.setCancelled(true);
        event.getPlayer().sendMessage(TextFormat.RED + "Sorry, you are not allowed to do this.");
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        event.setCancelled(true);
        event.getPlayer().sendMessage(TextFormat.RED + "Sorry, you are not allowed to do this.");
    }
}