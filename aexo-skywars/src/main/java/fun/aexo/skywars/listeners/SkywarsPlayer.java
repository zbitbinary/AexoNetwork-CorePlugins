package fun.aexo.skywars.listeners;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.utils.TextFormat;

public class SkywarsPlayer implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();


        event.setJoinMessage(TextFormat.GREEN + "[+] " + event.getPlayer().getName());
    }

    @EventHandler
    public void onPlayerQUit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        event.setQuitMessage(TextFormat.RED + "[-] " + event.getPlayer().getName());
    }


    
}
