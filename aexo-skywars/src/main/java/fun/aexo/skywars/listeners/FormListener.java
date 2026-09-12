package fun.aexo.skywars.listeners;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.form.response.FormResponseSimple;
import cn.nukkit.form.window.FormWindowSimple;
import cn.nukkit.utils.TextFormat;

public class FormListener implements Listener {

    @EventHandler
    public void onFormResponse(PlayerFormRespondedEvent event) {
        Player player = event.getPlayer();

        if (event.getWindow() instanceof FormWindowSimple form) {
            if (form.getTitle().contains("AEXO NETWORK")) {
                if (event.getResponse() == null || event.wasClosed()) return;

                FormResponseSimple response = (FormResponseSimple) event.getResponse();
                int buttonId = response.getClickedButtonId();

                switch (buttonId) {
                    case 0 -> {
                        player.sendMessage(TextFormat.GREEN + "Transferring to Main Hub...");
                        // Add WaterdogPE or proxy transfer execution here
                    }
                    case 1 -> player.sendMessage(TextFormat.YELLOW + "You are already in SkyWars!");
                    case 2 -> player.sendMessage(TextFormat.GRAY + "Closed menu.");
                }
            }
        }
    }
}
