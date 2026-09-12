package fun.aexo.essentials.commands;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;

public class UnnickCommand extends Command {

    public UnnickCommand() {
        super("unnick", "Reset your nickname back to your real username", "/unnick");
        this.setPermission("aexo.command.nick");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!this.testPermission(sender)) return true;
        if (!(sender instanceof Player player)) {
            sender.sendMessage(TextFormat.RED + "This command can only be used in-game!");
            return true;
        }

        // Reset display name and nametag to original username
        player.setDisplayName(player.getName());
        player.setNameTag(player.getName());

        player.sendMessage(TextFormat.GREEN + "Your nickname has been reset to your real username (" + player.getName() + ").");
        return true;
    }
}