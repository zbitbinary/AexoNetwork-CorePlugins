package fun.aexo.essentials.commands;

import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;

public class UnbanCommand extends Command {

    public UnbanCommand() {
        super("unban", "Unban a player", "/unban <player>");
        this.setPermission("aexo.command.unban");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!this.testPermission(sender)) return true;
        if (args.length < 1) {
            sender.sendMessage(TextFormat.RED + "Usage: " + this.getUsage());
            return true;
        }

        String targetName = args[0];

        if (!Server.getInstance().getNameBans().isBanned(targetName)) {
            sender.sendMessage(TextFormat.RED + "That player is not banned!");
            return true;
        }

        Server.getInstance().getNameBans().remove(targetName);
        sender.sendMessage(TextFormat.GREEN + "Successfully unbanned " + targetName + ".");
        return true;
    }
}