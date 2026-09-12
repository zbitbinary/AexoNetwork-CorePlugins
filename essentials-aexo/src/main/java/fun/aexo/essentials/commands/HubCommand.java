package fun.aexo.essentials.commands;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;

public class HubCommand extends Command {

    public HubCommand() {
        super("hub", "Teleport to the server spawn", "/hub");
        this.setAliases(new String[]{"spawn"});
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(TextFormat.RED + "This command can only be used in-game!");
            return true;
        }

        player.teleport(player.getLevel().getSpawnLocation());
        player.sendMessage(TextFormat.GREEN + "Teleported to Hub!");
        return true;
    }
}