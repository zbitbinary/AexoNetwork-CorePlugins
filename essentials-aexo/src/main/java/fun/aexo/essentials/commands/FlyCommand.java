package fun.aexo.essentials.commands;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;

public class FlyCommand extends Command {

    public FlyCommand() {
        super("fly", "Toggle flight mode", "/fly [player]");
        this.setPermission("aexo.command.fly");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!this.testPermission(sender)) return true;

        Player target;

        // Handle target player argument
        if (args.length > 0) {
            if (!sender.hasPermission("aexo.command.fly.others")) {
                sender.sendMessage(TextFormat.RED + "You do not have permission to toggle fly mode for others!");
                return true;
            }

            target = Server.getInstance().getPlayerExact(args[0]);
            if (target == null) {
                sender.sendMessage(TextFormat.RED + "Player '" + args[0] + "' not found!");
                return true;
            }
        } else {
            // Default to sender
            if (!(sender instanceof Player)) {
                sender.sendMessage(TextFormat.RED + "This command can only be used in-game when no player is specified!");
                return true;
            }
            target = (Player) sender;
        }

        // Toggle flight state in Nukkit
        boolean canFly = !target.getAllowFlight();
        target.setAllowFlight(canFly);

        // Send feedback
        target.sendMessage(TextFormat.YELLOW + "Flight mode: " + (canFly ? TextFormat.GREEN + "ENABLED" : TextFormat.RED + "DISABLED"));

        if (target != sender) {
            sender.sendMessage(TextFormat.GREEN + "Toggled flight mode for " + TextFormat.YELLOW + target.getName() + TextFormat.GREEN + " to " + (canFly ? TextFormat.GREEN + "ENABLED" : TextFormat.RED + "DISABLED"));
        }

        return true;
    }
}