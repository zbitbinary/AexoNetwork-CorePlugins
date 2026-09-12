package fun.aexo.skywars.listeners;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.item.Item;
import cn.nukkit.utils.TextFormat;
import fun.aexo.skywars.SkyWarsLoader;

public class HubCompassListener implements Listener {

    private static final String HUB_ITEM_NAME = TextFormat.RESET + "" + TextFormat.RED + "Back to Hub " + TextFormat.GRAY + "(Right-Click)";
    
    private static final String HUB_IP = "127.0.0.1";
    private static final int HUB_PORT = 19132;

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        SkyWarsLoader.getInstance().getServer().getScheduler().scheduleDelayedTask(
            SkyWarsLoader.getInstance(),
            () -> giveHubCompass(player),
            1
        );
    }

    private void giveHubCompass(Player player) {
        if (!player.isOnline()) return;

        Item compass = Item.get(Item.COMPASS);
        compass.setCustomName(HUB_ITEM_NAME);

        player.getInventory().setItem(8, compass);
        player.getInventory().sendContents(player);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Item item = event.getItem();

        if (item != null && item.getId() == Item.COMPASS && HUB_ITEM_NAME.equals(item.getCustomName())) {
            event.setCancelled(true);

            player.sendMessage(TextFormat.GREEN + "Connecting to the Hub...");
            player.transfer(HUB_IP, HUB_PORT);
        }
    }
}
