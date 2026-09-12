package fun.aexo.essentials.commands;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.level.Level;
import cn.nukkit.utils.TextFormat;

public class SethubCommand extends Command {

    public SethubCommand() {
        super("sethub", "Set the world spawn point to your position", "/sethub");
        this.setPermission("aexo.command.sethub");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!this.testPermission(sender)) return true;
        if (!(sender instanceof Player player)) {
            sender.sendMessage(TextFormat.RED + "This command can only be used in-game!");
            return true;
        }

        Level level = player.getLevel();
        level.setSpawnLocation(player.getPosition());

        player.sendMessage(TextFormat.GREEN + "Hub spawn location updated to your current location!");
        return true;
    }
}