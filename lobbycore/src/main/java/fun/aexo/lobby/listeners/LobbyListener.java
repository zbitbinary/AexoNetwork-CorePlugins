package fun.aexo.lobby.listeners;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.player.PlayerChatEvent;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.form.window.FormWindowModal;
import cn.nukkit.form.window.FormWindowSimple;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;
import fun.aexo.lobby.LobbyLoader;
import fun.aexo.lobby.managers.RankManager;
import fun.aexo.lobby.managers.ServerNavigator;

public class LobbyListener implements Listener {

    public static final int TOS_FORM_ID = 1002;
    private final Set<UUID> hiddenPlayers = new HashSet<>();

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // Send ToS Form if not previously accepted
        if (!LobbyLoader.getInstance().getTosConfig().getBoolean(player.getUniqueId().toString(), false)) {
            sendTosForm(player);
        }

        // Clear inventory and assign hotbar items
        player.getInventory().clearAll();

        // Slot 0: Server Navigator Compass
        Item compass = Item.get(ItemID.COMPASS, 0, 1);
        compass.setCustomName(TextFormat.BOLD.toString() + TextFormat.LIGHT_PURPLE + "Server Navigator " + TextFormat.GRAY + "(Right-Click)");
        player.getInventory().setItem(0, compass);

        // Slot 8: Player Visibility Toggle
        updateHiderItem(player);
    }

    private void updateHiderItem(Player player) {
        boolean isHidden = hiddenPlayers.contains(player.getUniqueId());
        Item dye = Item.get(ItemID.DYE, isHidden ? 8 : 10, 1); // 8 = Gray Dye, 10 = Lime Dye
        
        String status = isHidden ? TextFormat.RED + "DISABLED" : TextFormat.GREEN + "ENABLED";
        dye.setCustomName(TextFormat.BOLD.toString() + TextFormat.GOLD + "Player Visibility: " + status + TextFormat.GRAY + " (Right-Click)");
        
        player.getInventory().setItem(8, dye);
    }

    private void sendTosForm(Player player) {
        String content = TextFormat.YELLOW + "Welcome to AEXO Network! Please accept our Terms of Service:\n\n"
                + TextFormat.RED + "• No Use of Hacks / Cheats\n"
                + TextFormat.WHITE + "• Be Respectful to Others\n"
                + TextFormat.RED + "• No Toxicity or Harassment\n"
                + TextFormat.WHITE + "• No Unfair Teaming\n\n"
                + TextFormat.GRAY + "Read our Discord for full guidelines.";

        FormWindowModal tosForm = new FormWindowModal(
                TextFormat.BOLD.toString() + TextFormat.DARK_PURPLE + "AEXO NETWORK " + TextFormat.DARK_GRAY + "• " + TextFormat.RED + "ToS",
                content,
                TextFormat.GREEN + "Accept",
                TextFormat.RED + "Decline"
        );

        player.showFormWindow(tosForm, TOS_FORM_ID);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Item item = event.getItem();

        if (item == null || item.getId() == Item.AIR) return;

        // Open Navigator UI
        if (item.getId() == ItemID.COMPASS && item.hasCustomName() && item.getCustomName().contains("Server Navigator")) {
            ServerNavigator.openNavigator(player);
            event.setCancelled(true);
            return;
        }

        // Toggle Player Visibility
        if (item.getId() == ItemID.DYE && item.hasCustomName() && item.getCustomName().contains("Player Visibility")) {
            UUID uuid = player.getUniqueId();
            if (hiddenPlayers.contains(uuid)) {
                hiddenPlayers.remove(uuid);
                for (Player online : player.getServer().getOnlinePlayers().values()) {
                    player.showPlayer(online);
                }
                player.sendMessage(TextFormat.GREEN + "Players are now visible!");
            } else {
                hiddenPlayers.add(uuid);
                for (Player online : player.getServer().getOnlinePlayers().values()) {
                    player.hidePlayer(online);
                }
                player.sendMessage(TextFormat.RED + "Players are now hidden!");
            }
            updateHiderItem(player);
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPvPDamage(EntityDamageEvent event) {
        // Prevent all damage in Hub (PvP, Fall, Void, Knockback)
        if (event.getEntity() instanceof Player) {
            event.setCancelled(true);

            if (event instanceof EntityDamageByEntityEvent damageByEntityEvent) {
                if (damageByEntityEvent.getDamager() instanceof Player attacker) {
                    attacker.sendMessage(TextFormat.RED + "PvP is disabled in the Hub!");
                }
            }
        }
    }

    @EventHandler
    public void onFormResponse(PlayerFormRespondedEvent event) {
        Player player = event.getPlayer();

        // Handle Navigator Form Submissions dynamically from npcs.yml
        if (event.getFormID() == ServerNavigator.NAVIGATOR_FORM_ID && event.getWindow() instanceof FormWindowSimple window) {
            if (window.getResponse() == null) return;

            int clickedId = window.getResponse().getClickedButtonId();
            Config config = LobbyLoader.getInstance().getNpcConfig();

            if (config.exists("spawns")) {
                List<String> keys = new ArrayList<>(config.getSection("spawns").getKeys(false));
                if (clickedId >= 0 && clickedId < keys.size()) {
                    String targetKey = keys.get(clickedId);
                    String ip = config.getString("spawns." + targetKey + ".ip", "127.0.0.1");
                    int port = config.getInt("spawns." + targetKey + ".port", 19132);
                    String name = TextFormat.colorize('&', config.getString("spawns." + targetKey + ".name", targetKey));

                    player.sendMessage(TextFormat.YELLOW + "Connecting to " + name + TextFormat.YELLOW + "...");
                    player.transfer(ip, port);
                }
            }
            return;
        }

        // Handle Terms of Service Form Submissions
        if (event.getFormID() == TOS_FORM_ID && event.getWindow() instanceof FormWindowModal window) {
            if (window.getResponse() != null) {
                int buttonId = window.getResponse().getClickedButtonId();
                if (buttonId == 0) {
                    LobbyLoader.getInstance().getTosConfig().set(player.getUniqueId().toString(), true);
                    LobbyLoader.getInstance().getTosConfig().save();
                    player.sendMessage(TextFormat.GREEN + "Thank you for accepting the Terms of Service!");
                } else {
                    player.kick(TextFormat.RED + "You must accept the Terms of Service to play on AEXO Network.");
                }
            } else {
                player.kick(TextFormat.RED + "You must accept the Terms of Service to play on AEXO Network.");
            }
        }
    }

    @EventHandler
    public void onChat(PlayerChatEvent event) {
        Player player = event.getPlayer();
        String prefix = RankManager.getPrefix(player);
        event.setFormat(prefix + player.getDisplayName() + TextFormat.GRAY + ": " + TextFormat.WHITE + event.getMessage());
    }
}
