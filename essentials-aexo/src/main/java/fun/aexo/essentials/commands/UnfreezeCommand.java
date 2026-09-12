package fun.aexo.essentials.commands;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;
import fun.aexo.essentials.managers.PunishmentManager;

public class UnfreezeCommand extends Command {

    public UnfreezeCommand() {
        super("unfreeze", "Unfreeze a player", "/unfreeze <player>");
        this.setPermission("aexo.command.unfreeze");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!this.testPermission(sender)) return true;
        if (args.length < 1) {
            sender.sendMessage(TextFormat.RED + "Usage: " + this.getUsage());
            return true;
        }

        Player target = Server.getInstance().getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(TextFormat.RED + "Player not found!");
            return true;
        }

        PunishmentManager.unfreeze(target.getUniqueId());
        target.sendMessage(TextFormat.GREEN + "You have been unfrozen.");
        sender.sendMessage(TextFormat.GREEN + "Unfroze " + target.getName() + ".");
        return true;
    }
}