package fun.aexo.essentials.commands;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;

import java.util.Random;

public class NickCommand extends Command {

    private final String[] randomNames = {"Shadow", "Vortex", "Apex", "Cipher", "Blaze", "Phantom", "Rogue", "Zenith"};
    private final Random random = new Random();

    public NickCommand() {
        super("nick", "Change your name or auto-generate one", "/nick [custom_name]");
        this.setPermission("aexo.command.nick");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!this.testPermission(sender)) return true;
        if (!(sender instanceof Player player)) {
            sender.sendMessage(TextFormat.RED + "This command can only be used in-game!");
            return true;
        }

        String chosenNick;

        if (args.length > 0) {
            // Join args in case they entered a name with color codes or spaces
            chosenNick = String.join(" ", args);

            // Translate '&' color codes (e.g. &cRedNick -> §cRedNick)
            chosenNick = TextFormat.colorize('&', chosenNick);
        } else {
            // Fallback: Generate a random custom name if no name was provided
            String base = randomNames[random.nextInt(randomNames.length)];
            int tag = 100 + random.nextInt(900);
            chosenNick = TextFormat.YELLOW + base + "_" + tag;
        }

        // Apply display name and update chat name
        player.setDisplayName(chosenNick + TextFormat.RESET);
        player.setNameTag(chosenNick + TextFormat.RESET);

        player.sendMessage(TextFormat.GREEN + "Your nickname and nametag have been updated to: " + chosenNick);
        return true;
    }
}