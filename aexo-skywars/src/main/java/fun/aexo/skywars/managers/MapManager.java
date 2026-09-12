package fun.aexo.skywars.managers;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;

public class MapManager {

    /**
     * Replaces bedrock in the world with Redstone Ore (e.g. for SkyWars chest/refill markers).
     */
    public static void convertBedrockToRedstoneOre(Level level) {
        if (level == null) return;

        // Scans map boundaries around spawn
        int radius = 100;
        int minY = 0;
        int maxY = 128;

        Vector3 spawn = level.getSpawnLocation();

        for (int x = spawn.getFloorX() - radius; x <= spawn.getFloorX() + radius; x++) {
            for (int z = spawn.getFloorZ() - radius; z <= spawn.getFloorZ() + radius; z++) {
                for (int y = minY; y <= maxY; y++) {
                    Block block = level.getBlock(x, y, z);
                    
                    if (block.getId() == BlockID.BEDROCK) {
                        level.setBlock(new Position(x, y, z, level), Block.get(BlockID.REDSTONE_ORE), true, true);
                    }
                }
            }
        }
    }

    /**
     * Replaces Redstone Ore back into Bedrock.
     */
    public static void convertRedstoneOreToBedrock(Level level) {
        if (level == null) return;

        int radius = 100;
        int minY = 0;
        int maxY = 128;

        Vector3 spawn = level.getSpawnLocation();

        for (int x = spawn.getFloorX() - radius; x <= spawn.getFloorX() + radius; x++) {
            for (int z = spawn.getFloorZ() - radius; z <= spawn.getFloorZ() + radius; z++) {
                for (int y = minY; y <= maxY; y++) {
                    Block block = level.getBlock(x, y, z);

                    if (block.getId() == BlockID.REDSTONE_ORE || block.getId() == BlockID.GLOWING_REDSTONE_ORE) {
                        level.setBlock(new Position(x, y, z, level), Block.get(BlockID.BEDROCK), true, true);
                    }
                }
            }
        }
    }
}
