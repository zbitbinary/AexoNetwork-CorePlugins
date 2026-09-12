package fun.aexo.skywars.listeners;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerRespawnEvent;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import fun.aexo.skywars.SkyWarsLoader;
import fun.aexo.skywars.managers.RankManager;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class SpawnFixListener implements Listener {

    private static final List<Vector3> SPAWN_LOCATIONS = List.of(
        new Vector3(-45, 81, -24),
        new Vector3(-1, 81, -32),
        new Vector3(58, 81, -3),
        new Vector3(35, 80, 25),
        new Vector3(7, 81, 29),
        new Vector3(-38, 81, 17),
        new Vector3(-32, 88, -1),
        new Vector3(3, 88, 0),
        new Vector3(40, 88, -1)
    );

    private final AtomicInteger spawnIndex = new AtomicInteger(0);

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        teleportToNextSpawn(player);

        SkyWarsLoader.getInstance().getServer().getScheduler().scheduleDelayedTask(
            SkyWarsLoader.getInstance(),
            () -> RankManager.updatePlayerNameTag(player),
            1
        );
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRespawn(PlayerRespawnEvent event) {
        Position spawnPos = getNextSpawnPosition(event.getPlayer().getLevel());
        event.setRespawnPosition(spawnPos);
    }

    private void teleportToNextSpawn(Player player) {
        Position spawnPos = getNextSpawnPosition(player.getLevel());
        player.teleport(spawnPos);
    }

    private Position getNextSpawnPosition(Level level) {
        int index = Math.abs(spawnIndex.getAndIncrement() % SPAWN_LOCATIONS.size());
        Vector3 vec = SPAWN_LOCATIONS.get(index);
        return new Position(vec.x + 0.5, vec.y, vec.z + 0.5, level);
    }
}
