package fun.aexo.essentials.misc;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;
import fun.aexo.essentials.Loader;

import java.io.File;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LoginStreak implements Listener {

    private final Loader plugin;
    private final Config dataConfig;
    private final Map<UUID, StreakData> streakMap = new HashMap<>();

    public LoginStreak(Loader plugin) {
        this.plugin = plugin;
        
        // Save streak data in plugins/AexoEssentials/streaks.yml
        File file = new File(plugin.getDataFolder(), "streaks.yml");
        this.dataConfig = new Config(file, Config.YAML);
        loadData();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        LocalDate today = LocalDate.now();

        StreakData data = streakMap.getOrDefault(uuid, new StreakData(0, today.minusDays(1).toString()));

        LocalDate lastLogin = LocalDate.parse(data.lastLoginDate);
        long daysBetween = ChronoUnit.DAYS.between(lastLogin, today);

        if (daysBetween == 1) {
            // Logged in consecutive day -> Increment streak
            data.streak++;
            data.lastLoginDate = today.toString();
            rewardPlayer(player, data.streak);
        } else if (daysBetween > 1) {
            // Missed one or more days -> Reset streak
            data.streak = 1;
            data.lastLoginDate = today.toString();
            player.sendMessage(TextFormat.RED + "You lost your login streak! Starting over at Day 1.");
            rewardPlayer(player, data.streak);
        } else {
            // Already logged in today -> Remind player of current streak
            player.sendMessage(TextFormat.GRAY + "Current Login Streak: " + TextFormat.GOLD + data.streak + " Days");
            return;
        }

        streakMap.put(uuid, data);
        saveData(uuid, data);
    }

    private void rewardPlayer(Player player, int streak) {
        player.sendMessage(TextFormat.GREEN + "========================================");
        player.sendMessage(TextFormat.YELLOW + "DAILY LOGIN STREAK: " + TextFormat.GOLD + streak + " Day(s)!");
        
        // Example Reward Tiers
        switch (streak) {
            case 1 -> player.sendMessage(TextFormat.GREEN + "+ 100 Coins");
            case 3 -> player.sendMessage(TextFormat.GREEN + "+ 500 Coins & Special Key");
            case 7 -> player.sendMessage(TextFormat.AQUA + "WEEKLY STREAK REWARD: + 2,000 Coins!");
            default -> player.sendMessage(TextFormat.GREEN + "+ " + (streak * 100) + " Coins");
        }
        
        player.sendMessage(TextFormat.GREEN + "========================================");
    }

    private void loadData() {
        for (String key : dataConfig.getKeys(false)) {
            UUID uuid = UUID.fromString(key);
            int streak = dataConfig.getInt(key + ".streak", 0);
            String lastLogin = dataConfig.getString(key + ".lastLogin", LocalDate.now().minusDays(1).toString());
            streakMap.put(uuid, new StreakData(streak, lastLogin));
        }
    }

    private void saveData(UUID uuid, StreakData data) {
        dataConfig.set(uuid.toString() + ".streak", data.streak);
        dataConfig.set(uuid.toString() + ".lastLogin", data.lastLoginDate);
        dataConfig.save();
    }

    public int getStreak(Player player) {
        StreakData data = streakMap.get(player.getUniqueId());
        return data != null ? data.streak : 0;
    }

    private static class StreakData {
        int streak;
        String lastLoginDate;

        StreakData(int streak, String lastLoginDate) {
            this.streak = streak;
            this.lastLoginDate = lastLoginDate;
        }
    }
}