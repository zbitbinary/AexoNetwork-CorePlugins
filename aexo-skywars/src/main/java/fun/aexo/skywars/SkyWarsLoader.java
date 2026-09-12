package fun.aexo.skywars;

import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import fun.aexo.skywars.listeners.*;
import fun.aexo.skywars.managers.LootChestManager;
import fun.aexo.skywars.managers.MapManager;
import fun.aexo.skywars.managers.RankManager;

import java.io.File;

public class SkyWarsLoader extends PluginBase {

    private static SkyWarsLoader instance;
    private Config statsConfig;

    public static SkyWarsLoader getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        this.getDataFolder().mkdirs();
        this.statsConfig = new Config(
            new File(this.getDataFolder(), "stats.yml"),
            Config.YAML
        );

        MapManager.convertBedrockToRedstoneOre(this.getServer().getDefaultLevel());

        // Initialize LuckPerms API integration
        RankManager.init();

        // Event Listeners
        this.getServer().getPluginManager().registerEvents(new KnockbackListener(), this);
        this.getServer().getPluginManager().registerEvents(new HubCompassListener(), this);
        this.getServer().getPluginManager().registerEvents(new LootListener(), this);
        this.getServer().getPluginManager().registerEvents(new GameFixesListener(), this);
        this.getServer().getPluginManager().registerEvents(new EnderpearlFixListener(), this);
        this.getServer().getPluginManager().registerEvents(new SpawnFixListener(), this);
        this.getServer().getPluginManager().registerEvents(new GameListener(), this);
        this.getServer().getPluginManager().registerEvents(new FormListener(), this);

        // Refill Timer: 2.5 Minutes (3000 Ticks)
        this.getServer().getScheduler().scheduleRepeatingTask(this, LootChestManager::refillAllChests, 3000);

        this.getLogger().info("Aexo SkyWars loaded successfully!");
    }

    @Override
    public void onDisable() {
        saveStats();
    }

    public Config getStatsConfig() {
        return statsConfig;
    }

    /**
     * Saves the player stats configuration to disk.
     */
    public void saveStats() {
        if (this.statsConfig != null) {
            this.statsConfig.save();
        }
    }
}
