package fun.aexo.lobby;

import java.io.File;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.entity.Entity;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;
import fun.aexo.lobby.listeners.LobbyListener;
import fun.aexo.lobby.listeners.NPCListener;
import fun.aexo.lobby.managers.RankManager;

public class LobbyLoader extends PluginBase {

    private static LobbyLoader instance;
    private Config tosConfig;
    private Config npcConfig;

    public static LobbyLoader getInstance() {
        return instance;
    }

    public Config getTosConfig() {
        return tosConfig;
    }

    public Config getNpcConfig() {
        return npcConfig;
    }

    @Override
    public void onEnable() {
        instance = this;
        this.saveDefaultConfig();

        this.tosConfig = new Config(new File(this.getDataFolder(), "tos_accepted.yml"), Config.YAML);
        
        this.saveResource("npcs.yml", false);
        this.npcConfig = new Config(new File(this.getDataFolder(), "npcs.yml"), Config.YAML);

        RankManager.init();

        this.getServer().getPluginManager().registerEvents(new LobbyListener(), this);
        this.getServer().getPluginManager().registerEvents(new NPCListener(), this);

        this.getLogger().info("LobbyCore Loaded Successfully!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("spawnnpc")) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(TextFormat.RED + "In-game only.");
                return true;
            }

            if (args.length < 2) {
                player.sendMessage(TextFormat.RED + "Usage: /spawnnpc <npcKeyFromConfig> <Human|Zombie|Villager>");
                return true;
            }

            String npcKey = args[0].toLowerCase();
            String entityType = args[1];

            if (!npcConfig.exists("spawns." + npcKey)) {
                player.sendMessage(TextFormat.RED + "NPC key '" + npcKey + "' does not exist in npcs.yml!");
                return true;
            }

            String name = TextFormat.colorize('&', npcConfig.getString("spawns." + npcKey + ".name", npcKey));

            CompoundTag nbt = new CompoundTag()
                    .putList(new ListTag<DoubleTag>("Pos")
                            .add(new DoubleTag("", player.x))
                            .add(new DoubleTag("", player.y))
                            .add(new DoubleTag("", player.z)))
                    .putList(new ListTag<DoubleTag>("Motion")
                            .add(new DoubleTag("", 0))
                            .add(new DoubleTag("", 0))
                            .add(new DoubleTag("", 0)))
                    .putList(new ListTag<FloatTag>("Rotation")
                            .add(new FloatTag("", (float) player.yaw))
                            .add(new FloatTag("", (float) player.pitch)))
                    .putString("LobbyNPC", "true")
                    .putString("NPCKey", npcKey)
                    .putBoolean("NoAI", true);

            if (entityType.equalsIgnoreCase("Human")) {
                CompoundTag skinTag = new CompoundTag()
                        .putByteArray("Data", player.getSkin().getSkinData().data)
                        .putString("ModelId", player.getSkin().getSkinId());
                
                if (player.getSkin().getCapeData() != null) {
                    skinTag.putByteArray("CapeData", player.getSkin().getCapeData().data);
                }
                
                nbt.putCompound("Skin", skinTag);
            }

            Entity entity = Entity.createEntity(entityType, player.getChunk(), nbt);
            if (entity != null) {
                entity.setNameTag(name + "\n" + TextFormat.GREEN + "[CLICK TO JOIN]");
                entity.setNameTagAlwaysVisible(true);
                entity.spawnToAll();
                player.sendMessage(TextFormat.GREEN + "Spawned " + entityType + " NPC for " + npcKey + " successfully!");
            } else {
                player.sendMessage(TextFormat.RED + "Invalid entity type!");
            }
            return true;
        }
        return false;
    }
}
