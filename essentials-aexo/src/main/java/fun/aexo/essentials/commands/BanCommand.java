package fun.aexo.essentials.commands;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;

public class BanCommand extends Command {

    public BanCommand() {
        super("ban", "Ban a player from the server", "/ban <player> [reason]");
        this.setPermission("aexo.command.ban");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!this.testPermission(sender)) return true;
        if (args.length < 1) {
            sender.sendMessage(TextFormat.RED + "Usage: " + this.getUsage());
            return true;
        }

        String targetName = args[0];
        String reason = args.length > 1 ? String.join(" ", java.util.Arrays.copyOfRange(args, 1, args.length)) : "Unfair Advantage / Cheating";

        String banScreen = TextFormat.DARK_RED + "" + TextFormat.BOLD + "AEXO NETWORK\n\n"
                + TextFormat.RESET + TextFormat.RED + "You are permanently banned from this server!\n\n"
                + TextFormat.YELLOW + "Reason: " + TextFormat.WHITE + reason + "\n"
                + TextFormat.YELLOW + "Banned by: " + TextFormat.WHITE + sender.getName() + "\n"
                + TextFormat.GRAY + "Appeal on Discord: discord.aexo.fun";

        Server.getInstance().getNameBans().addBan(targetName, reason, null, sender.getName());

        Player target = Server.getInstance().getPlayer(targetName);
        if (target != null && target.isOnline()) {
            target.kick(banScreen);
        }

        Server.getInstance().broadcastMessage(TextFormat.RED + targetName + " was permanently banned by " + sender.getName() + " for: " + reason);
        return true;
    }
}