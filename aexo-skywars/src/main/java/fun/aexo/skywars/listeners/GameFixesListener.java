package fun.aexo.skywars.listeners;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityPrimedTNT;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.player.PlayerMoveEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.utils.TextFormat;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GameFixesListener implements Listener {

    private final Map<UUID, CombatTag> lastAttackerMap = new HashMap<>();
    private final Map<UUID, Integer> killStreakMap = new HashMap<>();

    private record CombatTag(UUID attackerId, String attackerName, long timestamp) {}

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        EntityDamageEvent.DamageCause cause = event.getCause();

        // Cancel standard fall damage
        if (cause == EntityDamageEvent.DamageCause.FALL) {
            event.setCancelled(true);
            return;
        }

        if (event instanceof EntityDamageByEntityEvent damageByEntity) {
            Entity damager = damageByEntity.getDamager();
            Player attacker = null;

            if (damager instanceof Player p) {
                attacker = p;
            } else if (damager instanceof EntityPrimedTNT tnt && tnt.namedTag.contains("tnt_owner")) {
                attacker = Server.getInstance().getPlayerExact(tnt.namedTag.getString("tnt_owner"));
            }

            if (attacker != null && !attacker.getUniqueId().equals(player.getUniqueId())) {
                lastAttackerMap.put(player.getUniqueId(), new CombatTag(attacker.getUniqueId(), attacker.getName(), System.currentTimeMillis()));
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (player.getY() < 0 && player.isAlive()) {
            handleVoidDeath(player);
        }
    }

    private void handleVoidDeath(Player player) {
        UUID victimId = player.getUniqueId();
        CombatTag tag = lastAttackerMap.get(victimId);

        boolean inCombat = tag != null && (System.currentTimeMillis() - tag.timestamp() <= 10000);

        if (inCombat) {
            Player killer = Server.getInstance().getPlayerExact(tag.attackerName());
            if (killer != null && killer.isOnline()) {
                int killerStreak = killStreakMap.getOrDefault(killer.getUniqueId(), 0) + 1;
                killStreakMap.put(killer.getUniqueId(), killerStreak);
                killer.sendMessage(TextFormat.GREEN + "You killed " + player.getName() + "! (Streak: " + killerStreak + ")");
            }
            killStreakMap.put(victimId, 0);
            player.sendMessage(TextFormat.RED + "You were knocked into the void!");
        } else {
            player.sendMessage(TextFormat.YELLOW + "You fell into the void! Killstreak preserved.");
        }

        lastAttackerMap.remove(victimId);
        player.setHealth(0);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        lastAttackerMap.remove(uuid);
    }
}