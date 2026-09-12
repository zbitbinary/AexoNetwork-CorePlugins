package fun.aexo.skywars.managers;

import cn.nukkit.Server;
import cn.nukkit.blockentity.BlockEntityChest;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.item.Item;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.utils.TextFormat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LootChestManager {

    private static final Set<String> populatedChests = new HashSet<>();
    private static final NukkitRandom random = new NukkitRandom();

    public static void populateChest(BlockEntityChest chest) {
        String chestKey = chest.getLevel().getName() + ":" + chest.getX() + ":" + chest.getY() + ":" + chest.getZ();
        
        if (populatedChests.contains(chestKey)) {
            return;
        }

        Inventory inv = chest.getInventory();
        inv.clearAll();

        List<Item> lootPool = new ArrayList<>();

        // Blocks & Projectiles
        lootPool.add(Item.get(Item.COBBLESTONE, 0, random.nextRange(16, 64)));
        lootPool.add(Item.get(Item.WOODEN_PLANKS, 0, random.nextRange(16, 64)));
        lootPool.add(Item.get(Item.SNOWBALL, 0, random.nextRange(8, 16)));
        lootPool.add(Item.get(Item.ARROW, 0, random.nextRange(12, 32)));

        // Weapons & Armor
        lootPool.add(Item.get(Item.IRON_SWORD));
        lootPool.add(Item.get(Item.BOW));
        lootPool.add(Item.get(Item.IRON_CHESTPLATE));
        lootPool.add(Item.get(Item.GOLDEN_APPLE, 0, random.nextRange(1, 3)));

        // Instant TNT
        Item tnt = Item.get(Item.TNT, 0, random.nextRange(2, 8));
        tnt.setCustomName(TextFormat.RED.toString() + TextFormat.BOLD + "Instant TNT");
        tnt.getNamedTag().putBoolean("instant_tnt", true);
        lootPool.add(tnt);

        Collections.shuffle(lootPool);
        int itemAmount = random.nextRange(4, 7);

        for (int i = 0; i < itemAmount && i < lootPool.size(); i++) {
            int slot = random.nextRange(0, inv.getSize() - 1);
            inv.setItem(slot, lootPool.get(i));
        }

        populatedChests.add(chestKey);
    }

    public static void refillAllChests() {
        populatedChests.clear();
        Server.getInstance().broadcastMessage(
            TextFormat.GREEN.toString() + TextFormat.BOLD + "Chests have been refilled!"
        );
    }

    public static void resetChests() {
        populatedChests.clear();
    }
}
