package fun.aexo.essentials.commands;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;
import fun.aexo.essentials.managers.PunishmentManager;

public class MuteCommand extends Command {

    public MuteCommand() {
        super("mute", "Mute a player from chat", "/mute <player> [reason]");
        this.setPermission("aexo.command.mute");
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

        String reason = args.length > 1 ? String.join(" ", java.util.Arrays.copyOfRange(args, 1, args.length)) : "No reason specified";
        PunishmentManager.mute(target.getUniqueId());

        target.sendMessage(TextFormat.RED + "You have been muted by " + sender.getName() + "!\nReason: " + reason);
        sender.sendMessage(TextFormat.GREEN + "Muted " + target.getName() + " for: " + reason);
        return true;
    }
}