package fun.aexo.skywars.listeners;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.entity.item.EntityPrimedTNT;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.player.PlayerDeathEvent;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.event.player.PlayerRespawnEvent;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.window.FormWindowSimple;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.level.Sound;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.protocol.TransferPacket;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;

import fun.aexo.skywars.SkyWarsLoader;

public class GameListener implements Listener {

    private final Set<Vector3> playerPlacedBlocks = new HashSet<>();
    private final HashSet<Player> protectedPlayers = new HashSet<>();
    private final Map<Player, Integer> killStreaks = new HashMap<>();
    private final Random random = new Random();

    private final String[] killMessages = {
        "&c{victim} &7was blasted into the void by &a{killer}&7!",
        "&c{victim} &7thought they could out-pvp &a{killer}&7.",
        "&a{killer} &7sent &c{victim} &7straight to the lobby!",
        "&c{victim} &7got absolute combo'd by &a{killer}&7.",
        "&a{killer} &7sniped &c{victim} &7out of the sky!",
        "&c{victim} &7got knocked off their island by &a{killer}&7.",
        "&a{killer} &7disassembled &c{victim} &7in record time.",
        "&c{victim} &7tried to run, but &a{killer} &7was faster.",
        "&a{killer} &7showed &c{victim} &7how to play SkyWars.",
        "&c{victim} &7stared into the abyss thanks to &a{killer}&7.",
        "&a{killer} &7cleaned up &c{victim} &7like trash.",
        "&c{victim} &7lost a 1v1 against &a{killer}&7.",
        "&a{killer} &7sliced &c{victim} &7into pieces!",
        "&c{victim} &7couldn't escape &a{killer}'s &7bow shot.",
        "&a{killer} &7deleted &c{victim}'s &7health bar.",
        "&c{victim} &7was humbled by &a{killer}&7.",
        "&a{killer} &7snowballed &c{victim} &7into oblivion!",
        "&c{victim} &7met their demise at the hands of &a{killer}&7.",
        "&a{killer} &7claimed &c{victim}'s &7soul.",
        "&c{victim} &7failed to bridge against &a{killer}&7!",
        "&a{killer} &7sent &c{victim} &7to BitBinaries Lab",
        "&a{killer} &7banished &c{victim} &7from life."
    };

    public GameListener() {
        SkyWarsLoader.getInstance().getServer().getScheduler().scheduleRepeatingTask(
            SkyWarsLoader.getInstance(),
            this::updateAllActionBars,
            20
        );
    }

    private void updateAllActionBars() {
        Config config = SkyWarsLoader.getInstance().getStatsConfig();

        for (Player player : SkyWarsLoader.getInstance().getServer().getOnlinePlayers().values()) {
            String uuidStr = player.getUniqueId().toString();
            int kills = config.getInt(uuidStr + ".kills", 0);
            int deaths = config.getInt(uuidStr + ".deaths", 0);
            int streak = killStreaks.getOrDefault(player, 0);

            String actionBarText = TextFormat.LIGHT_PURPLE + "Kills: " + TextFormat.WHITE + kills
                    + TextFormat.GRAY + " | "
                    + TextFormat.LIGHT_PURPLE + "Deaths: " + TextFormat.WHITE + deaths
                    + TextFormat.GRAY + " | "
                    + TextFormat.GOLD + "Streak: " + TextFormat.YELLOW + streak
                    + TextFormat.GRAY + " | "
                    + TextFormat.YELLOW + "aexo.fun";

            player.sendActionBar(actionBarText);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        killStreaks.put(player, 0);

        Config config = SkyWarsLoader.getInstance().getStatsConfig();
        String uuidStr = player.getUniqueId().toString();

        if (!config.exists(uuidStr + ".kills")) {
            config.set(uuidStr + ".kills", 0);
        }
        if (!config.exists(uuidStr + ".deaths")) {
            config.set(uuidStr + ".deaths", 0);
        }
        if (!config.exists(uuidStr + ".name")) {
            config.set(uuidStr + ".name", player.getName());
        }
        SkyWarsLoader.getInstance().saveStats();

        giveKit(player);
        applySpawnProtection(player);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        protectedPlayers.remove(player);
        killStreaks.remove(player);
    }

    private void addKill(Player player) {
        Config config = SkyWarsLoader.getInstance().getStatsConfig();
        String path = player.getUniqueId().toString() + ".kills";
        int currentKills = config.getInt(path, 0);
        config.set(path, currentKills + 1);
        SkyWarsLoader.getInstance().saveStats();
    }

    private void addDeath(Player player) {
        Config config = SkyWarsLoader.getInstance().getStatsConfig();
        String path = player.getUniqueId().toString() + ".deaths";
        int currentDeaths = config.getInt(path, 0);
        config.set(path, currentDeaths + 1);
        SkyWarsLoader.getInstance().saveStats();
    }

    private void giveKit(Player player) {
        player.getInventory().clearAll();

        player.getInventory().setHelmet(Item.get(ItemID.DIAMOND_HELMET));
        player.getInventory().setChestplate(Item.get(ItemID.DIAMOND_CHESTPLATE));
        player.getInventory().setLeggings(Item.get(ItemID.DIAMOND_LEGGINGS));
        player.getInventory().setBoots(Item.get(ItemID.DIAMOND_BOOTS));

        player.getInventory().setItem(0, Item.get(ItemID.DIAMOND_SWORD));
        player.getInventory().setItem(1, Item.get(ItemID.IRON_PICKAXE));
        player.getInventory().setItem(2, Item.get(ItemID.GOLDEN_APPLE, 0, 3));
        player.getInventory().setItem(3, Item.get(ItemID.BOW));
        player.getInventory().setItem(4, Item.get(ItemID.SNOWBALL, 0, 16));
        player.getInventory().setItem(5, Item.get(ItemID.ENDER_PEARL, 0, 16));
        player.getInventory().setItem(6, Item.get(BlockID.STONE, 5, 64));
        player.getInventory().setItem(7, Item.get(BlockID.TNT, 0, 16));

        // 9th Hotbar Slot (Index 8) -> Hub Menu Compass
        Item hubCompass = Item.get(ItemID.COMPASS);
        hubCompass.setCustomName(TextFormat.GREEN + TextFormat.BOLD.toString() + "Hub Menu " + TextFormat.GRAY + "(Right-Click)");
        player.getInventory().setItem(8, hubCompass);

        player.getInventory().setItem(9, Item.get(ItemID.ARROW, 0, 16));

        player.getInventory().sendArmorContents(player);
    }

    private void refillKillRewards(Player killer) {
        killer.getInventory().addItem(Item.get(ItemID.GOLDEN_APPLE, 0, 2));
        killer.getInventory().addItem(Item.get(ItemID.SNOWBALL, 0, 16));
        killer.getInventory().addItem(Item.get(BlockID.STONE, 5, 64));
        killer.getInventory().addItem(Item.get(BlockID.TNT, 0, 8));
        killer.getInventory().addItem(Item.get(ItemID.ARROW, 0, 8));
        
        killer.setHealth(Math.min(killer.getHealth() + 4.0f, killer.getMaxHealth()));

        killer.sendMessage(TextFormat.GREEN + "+ Your Inventory has been Refilled!");
    }

    private void applySpawnProtection(Player player) {
        protectedPlayers.add(player);
        player.sendMessage(TextFormat.GREEN + "Spawn protection is active for 3 seconds!");

        SkyWarsLoader.getInstance().getServer().getScheduler().scheduleDelayedTask(
            SkyWarsLoader.getInstance(),
            () -> {
                if (protectedPlayers.remove(player) && player.isOnline()) {
                    player.sendMessage(TextFormat.RED + "Spawn protection has expired!");
                }
            },
            60
        );
    }

    // --- HUB COMPASS INTERACT ---
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Item item = event.getItem();

        if (item.getId() == ItemID.COMPASS && item.hasCustomName() && item.getCustomName().contains("Hub Menu")) {
            event.setCancelled(true);
            openHubForm(player);
        }
    }

    private void openHubForm(Player player) {
        FormWindowSimple form = new FormWindowSimple(
            TextFormat.BOLD.toString() + TextFormat.DARK_AQUA + "AEXO NETWORK",
            TextFormat.GRAY + "Select a destination server to transfer:"
        );

        form.addButton(new ElementButton(TextFormat.GREEN + "Main Hub"));
        form.addButton(new ElementButton(TextFormat.GOLD + "SkyWars Lobby"));
        form.addButton(new ElementButton(TextFormat.RED + "Close Menu"));

        player.showFormWindow(form);
    }

    // --- FORM SELECTION & TRANSFER PACKET LOGIC ---
    @EventHandler
    public void onFormResponse(PlayerFormRespondedEvent event) {
        if (event.wasClosed()) return;

        if (event.getWindow() instanceof FormWindowSimple form) {
            if (form.getTitle().contains("AEXO NETWORK")) {
                int buttonId = form.getResponse().getClickedButtonId();

                if (buttonId == 0) { // "Main Hub" Button
                    Player player = event.getPlayer();

                    TransferPacket pk = new TransferPacket();
                    pk.address = "play.aexo.fun";
                    pk.port = 25510;
                    player.dataPacket(pk);

                    player.sendMessage(TextFormat.GREEN + "Transferring to Main Hub (play.aexo.fun:25510)...");
                }
            }
        }
    }

    // --- SPAWN PROTECTION DAMAGE CHECK ---
    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (protectedPlayers.contains(player)) {
                event.setCancelled(true);
            }
        }
    }

    // --- INSTANT TNT SAFE CRASH FIX ---
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();

        if (block.getId() == BlockID.TNT) {
            event.setCancelled(true);

            Item itemInHand = player.getInventory().getItemInHand();
            if (itemInHand.getBlockId() == BlockID.TNT) {
                itemInHand.setCount(itemInHand.getCount() - 1);
                player.getInventory().setItemInHand(itemInHand);
            }

            spawnPrimedTNT(block.getLocation(), player);
            return;
        }

        Vector3 pos = new Vector3(block.x, block.y, block.z);
        playerPlacedBlocks.add(pos);

        SkyWarsLoader.getInstance().getServer().getScheduler().scheduleDelayedTask(
            SkyWarsLoader.getInstance(),
            () -> {
                if (block.getLevel().getBlock(pos).getId() != BlockID.AIR) {
                    block.getLevel().setBlock(pos, Block.get(BlockID.AIR), true, true);
                }
                playerPlacedBlocks.remove(pos);
            },
            400
        );
    }

    private void spawnPrimedTNT(Position pos, Player source) {
        CompoundTag nbt = new CompoundTag()
                .putList(new ListTag<DoubleTag>("Pos")
                        .add(new DoubleTag("", pos.x + 0.5))
                        .add(new DoubleTag("", pos.y))
                        .add(new DoubleTag("", pos.z + 0.5)))
                .putList(new ListTag<DoubleTag>("Motion")
                        .add(new DoubleTag("", 0))
                        .add(new DoubleTag("", 0))
                        .add(new DoubleTag("", 0)))
                .putList(new ListTag<FloatTag>("Rotation")
                        .add(new FloatTag("", 0))
                        .add(new FloatTag("", 0)))
                .putShort("Fuse", 40);

        EntityPrimedTNT tnt = new EntityPrimedTNT(pos.getLevel().getChunk((int) pos.x >> 4, (int) pos.z >> 4), nbt);
        tnt.spawnToAll();
        pos.getLevel().addSound(pos, Sound.RANDOM_FUSE);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Vector3 pos = new Vector3(block.x, block.y, block.z);
        Level level = block.getLevel();

        if (block.getId() == BlockID.REDSTONE_ORE || block.getId() == BlockID.GLOWING_REDSTONE_ORE) {
            Player player = event.getPlayer();
            event.setCancelled(true);

            // Eye of Ender insertion sound
            level.addSound(pos, Sound.BLOCK_END_PORTAL_FRAME_FILL);

            float currentAbsorption = player.getAbsorption();
            if (currentAbsorption < 10.0f) {
                float newAbsorption = Math.min(currentAbsorption + 2.0f, 10.0f);
                player.setAbsorption(newAbsorption);
                player.sendMessage(TextFormat.GREEN + "+1 Absorption Heart!");
            } else {
                player.sendMessage(TextFormat.RED + "You have the MAX amount of hearts!");
            }

            level.setBlock(pos, Block.get(BlockID.BEDROCK), true, true);

            SkyWarsLoader.getInstance().getServer().getScheduler().scheduleDelayedTask(
                SkyWarsLoader.getInstance(),
                () -> {
                    if (level.getBlock(pos).getId() == BlockID.BEDROCK) {
                        level.setBlock(pos, Block.get(BlockID.REDSTONE_ORE), true, true);
                    }
                },
                100
            );
            return;
        }

        if (!playerPlacedBlocks.contains(pos)) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(TextFormat.RED + "Stop breaking my map NERD!");
        } else {
            playerPlacedBlocks.remove(pos);
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        event.setDeathMessage("");
        event.setDrops(new Item[0]);

        killStreaks.put(victim, 0);
        addDeath(victim);

        if (victim.getLastDamageCause() instanceof EntityDamageByEntityEvent damageEvent) {
            if (damageEvent.getDamager() instanceof Player killer) {

                killer.getLevel().addSound(killer.getPosition(), Sound.MOB_ENDERDRAGON_DEATH);

                int currentStreak = killStreaks.getOrDefault(killer, 0) + 1;
                killStreaks.put(killer, currentStreak);
                addKill(killer);
                
                refillKillRewards(killer);

                if (currentStreak >= 10 && currentStreak % 10 == 0) {
                    String streakMessage = TextFormat.BOLD.toString() + TextFormat.GOLD + "STREAK! "
                            + TextFormat.YELLOW + killer.getName() + TextFormat.GRAY + " has a streak of "
                            + TextFormat.RED + currentStreak + " kills" + TextFormat.GRAY + "!";
                    SkyWarsLoader.getInstance().getServer().broadcastMessage(streakMessage);
                }

                String chosenMessage = killMessages[random.nextInt(killMessages.length)]
                        .replace("{victim}", victim.getName())
                        .replace("{killer}", killer.getName());
                SkyWarsLoader.getInstance().getServer().broadcastMessage(TextFormat.colorize('&', chosenMessage));
            } else {
                SkyWarsLoader.getInstance().getServer().broadcastMessage(
                        TextFormat.colorize('&', "&c" + victim.getName() + " &7eliminated themselves!"));
            }
        } else {
            SkyWarsLoader.getInstance().getServer().broadcastMessage(
                    TextFormat.colorize('&', "&c" + victim.getName() + " &7eliminated themselves!"));
        }

        SkyWarsLoader.getInstance().getServer().getScheduler().scheduleDelayedTask(
            SkyWarsLoader.getInstance(),
            () -> {
                if (victim.isOnline()) {
                    Position spawn = findSafeSpawnLocation(victim.getLevel());
                    victim.teleport(spawn);
                    giveKit(victim);
                    applySpawnProtection(victim);
                }
            },
            1
        );
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();

        Position safeSpawn = findSafeSpawnLocation(player.getLevel());
        event.setRespawnPosition(safeSpawn);

        SkyWarsLoader.getInstance().getServer().getScheduler().scheduleDelayedTask(
            SkyWarsLoader.getInstance(),
            () -> {
                giveKit(player);
                applySpawnProtection(player);
            },
            2
        );
    }

    private Position findSafeSpawnLocation(Level level) {
        if (level == null) level = SkyWarsLoader.getInstance().getServer().getDefaultLevel();

        int x = random.nextInt(40) - 20;
        int z = random.nextInt(40) - 20;
        int y = level.getHighestBlockAt(x, z);

        if (y < 5) y = 64;

        return new Position(x + 0.5, y + 1.5, z + 0.5, level);
    }
}
