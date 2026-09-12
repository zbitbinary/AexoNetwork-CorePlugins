package fun.aexo.lobby.managers;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.window.FormWindowSimple;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;
import fun.aexo.lobby.LobbyLoader;

import java.util.ArrayList;
import java.util.List;

public class ServerNavigator {

    public static final int NAVIGATOR_FORM_ID = 1001;

    public static void openNavigator(Player player) {
        FormWindowSimple form = new FormWindowSimple(
                TextFormat.BOLD.toString() + TextFormat.DARK_PURPLE + "AEXO NETWORK " + TextFormat.DARK_GRAY + "• " + TextFormat.LIGHT_PURPLE + "NAVIGATOR",
                TextFormat.GRAY + "Select a server to connect:"
        );

        Config config = LobbyLoader.getInstance().getNpcConfig();

        if (config.exists("spawns")) {
            for (String key : config.getSection("spawns").getKeys(false)) {
                String name = TextFormat.colorize('&', config.getString("spawns." + key + ".name", key));
                form.addButton(new ElementButton(name + "\n" + TextFormat.DARK_GRAY + "Click to join"));
            }
        } else {
            form.setContent(TextFormat.RED + "No servers configured in npcs.yml!");
        }

        player.showFormWindow(form, NAVIGATOR_FORM_ID);
    }
}
