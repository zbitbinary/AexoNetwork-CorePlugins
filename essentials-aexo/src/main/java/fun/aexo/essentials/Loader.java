package fun.aexo.essentials;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import cn.nukkit.Player;
import cn.nukkit.command.CommandMap;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.DummyBossBar;
import cn.nukkit.utils.TextFormat;

import fun.aexo.essentials.commands.*;
import fun.aexo.essentials.managers.PunishmentManager;
import fun.aexo.essentials.misc.LoginStreak;
import fun.aexo.essentials.player.AexoPlayer;
import fun.aexo.essentials.player.KnockbackListener;

public class Loader extends PluginBase {

    private final Map<UUID, DummyBossBar> bossBars = new HashMap<>();

    @Override
    public void onEnable() {
        this.getLogger().info("Essentials Enabled");

        // Register Event Listeners
        this.getServer().getPluginManager().registerEvents(new AexoPlayer(this), this);
        this.getServer().getPluginManager().registerEvents(new KnockbackListener(), this);
        this.getServer().getPluginManager().registerEvents(new LoginStreak(this), this);
        this.getServer().getPluginManager().registerEvents(new PunishmentManager(), this);

        // Register Commands directly to avoid override issues
        registerCommands();

        // Repeating task for Action Bar and BossBar updates
        this.getServer().getScheduler().scheduleRepeatingTask(this, () -> {
            for (Player player : this.getServer().getOnlinePlayers().values()) {
                if (player != null && player.isOnline()) {
                    // 1. Action Bar
                    player.sendActionBar(TextFormat.YELLOW + "You are Connected to: " + TextFormat.RED + "Lobby 1");

                    // 2. BossBar Update
                    updateBossBar(player);
                }
            }
        }, 20); // 20 ticks = 1 second
    }

    private void registerCommands() {
        CommandMap map = this.getServer().getCommandMap();

        map.registerAll("aexo", Arrays.asList(
            new MuteCommand(),
            new UnmuteCommand(),
            new KickCommand(),
            new BanCommand(),
            new UnbanCommand(),
            new FreezeCommand(),
            new UnfreezeCommand(),
            new FlyCommand(),
            new SethubCommand(),
            new HubCommand(),
            new NickCommand(),
            new UnnickCommand(),
            new DiscordCommand()
        ));
    }

    public void setupPlayerHud(Player player) {
        createBossBar(player);
    }

    private void createBossBar(Player player) {
        String title = TextFormat.GOLD + "Playing: " + TextFormat.YELLOW + "aexo.fun " + TextFormat.GRAY + "| " + TextFormat.GREEN + "Lobby 1";
        
        DummyBossBar bossBar = new DummyBossBar.Builder(player)
                .text(title)
                .length(100.0f)
                .build();

        bossBar.create();
        bossBars.put(player.getUniqueId(), bossBar);
    }

    private void updateBossBar(Player player) {
        DummyBossBar bossBar = bossBars.get(player.getUniqueId());
        if (bossBar != null) {
            String title = TextFormat.GOLD + "Playing: " + TextFormat.YELLOW + "aexo.fun " + TextFormat.GRAY + "| " + TextFormat.GREEN + "Lobby 1";
            bossBar.setText(title);
            bossBar.setLength(100.0f);
        }
    }

    public void removeBossBar(Player player) {
        DummyBossBar bossBar = bossBars.remove(player.getUniqueId());
        if (bossBar != null) {
            bossBar.destroy();
        }
    }
}