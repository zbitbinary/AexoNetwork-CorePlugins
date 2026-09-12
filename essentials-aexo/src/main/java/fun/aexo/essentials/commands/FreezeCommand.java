package fun.aexo.essentials.commands;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;
import fun.aexo.essentials.managers.PunishmentManager;

public class FreezeCommand extends Command {

    public FreezeCommand() {
        super("freeze", "Freeze a player in place", "/freeze <player>");
        this.setPermission("aexo.command.freeze");
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

        PunishmentManager.freeze(target.getUniqueId());
        target.sendMessage(TextFormat.RED + "" + TextFormat.BOLD + "YOU HAVE BEEN FROZEN BY A STAFF MEMBER!\nDO NOT LOG OUT OR YOU WILL BE BANNED!");
        sender.sendMessage(TextFormat.GREEN + "Froze " + target.getName() + ".");
        return true;
    }
}