package fun.aexo.skywars.listeners;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityChest;
import cn.nukkit.entity.item.EntityPrimedTNT;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.entity.EntityExplodeEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.item.Item;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;
import fun.aexo.skywars.managers.LootChestManager;

public class LootListener implements Listener {

    @EventHandler
    public void onChestOpen(PlayerInteractEvent event) {
        Block block = event.getBlock();
        if (block != null && (block.getId() == BlockID.CHEST || block.getId() == BlockID.TRAPPED_CHEST)) {
            BlockEntity be = block.getLevel().getBlockEntity(block);
            if (be instanceof BlockEntityChest chest) {
                LootChestManager.populateChest(chest);
            }
        }
    }

    @EventHandler
    public void onTNTPlace(BlockPlaceEvent event) {
        Item item = event.getItem();
        Block block = event.getBlock();

        if (item.getId() == Item.TNT || block.getId() == BlockID.TNT) {
            event.setCancelled(true);

            Player player = event.getPlayer();
            
            if (player.isSurvival()) {
                item.setCount(item.getCount() - 1);
                player.getInventory().setItemInHand(item);
            }

            CompoundTag nbt = new CompoundTag()
                .putList(new ListTag<DoubleTag>("Pos")
                    .add(new DoubleTag("", block.getX() + 0.5))
                    .add(new DoubleTag("", block.getY()))
                    .add(new DoubleTag("", block.getZ() + 0.5)))
                .putList(new ListTag<DoubleTag>("Motion")
                    .add(new DoubleTag("", 0))
                    .add(new DoubleTag("", 0))
                    .add(new DoubleTag("", 0)))
                .putList(new ListTag<FloatTag>("Rotation")
                    .add(new FloatTag("", 0))
                    .add(new FloatTag("", 0)))
                .putShort("Fuse", 40)
                .putString("tnt_owner", player.getName());

            EntityPrimedTNT tntEntity = new EntityPrimedTNT(block.getLevel().getChunk((int) block.getX() >> 4, (int) block.getZ() >> 4), nbt);
            tntEntity.spawnToAll();
        }
    }

    @EventHandler
    public void onExplode(EntityExplodeEvent event) {
        if (event.getEntity() instanceof EntityPrimedTNT) {
            event.getBlockList().clear();
        }
    }
}