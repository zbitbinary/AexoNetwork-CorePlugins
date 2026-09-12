package fun.aexo.essentials.commands;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;

public class DiscordCommand extends Command {

    public DiscordCommand() {
        super("discord", "Get the link to our official Discord server", "/discord");
        this.setAliases(new String[]{"dc"});
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        sender.sendMessage(TextFormat.DARK_GRAY + "----------------------------------------");
        sender.sendMessage(TextFormat.GOLD + "" + TextFormat.BOLD + "AEXO NETWORK DISCORD");
        sender.sendMessage(TextFormat.GRAY + "Join our community for updates, support, and giveaways!");
        sender.sendMessage(TextFormat.YELLOW + "Link: " + TextFormat.AQUA + "discord.gg/aexo");
        sender.sendMessage(TextFormat.DARK_GRAY + "----------------------------------------");
        return true;
    }
}