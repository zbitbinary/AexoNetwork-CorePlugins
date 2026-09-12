package fun.aexo.essentials.commands;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;

public class KickCommand extends Command {

    public KickCommand() {
        super("kick", "Kick a player from the server", "/kick <player> [reason]");
        this.setPermission("aexo.command.kick");
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

        String reason = args.length > 1 ? String.join(" ", java.util.Arrays.copyOfRange(args, 1, args.length)) : "Violating server rules";
        
        String kickScreen = TextFormat.RED + "" + TextFormat.BOLD + "AEXO NETWORK\n\n"
                + TextFormat.RESET + TextFormat.GRAY + "You have been kicked from the server!\n"
                + TextFormat.YELLOW + "Reason: " + TextFormat.WHITE + reason + "\n"
                + TextFormat.YELLOW + "Kicked by: " + TextFormat.WHITE + sender.getName();

        target.kick(kickScreen);
        Server.getInstance().broadcastMessage(TextFormat.RED + target.getName() + " was kicked by " + sender.getName() + ". Reason: " + reason);
        return true;
    }
}