package fun.aexo.logo.commands;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.Location;
import cn.nukkit.level.particle.FloatingTextParticle;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.utils.TextFormat;
import fun.aexo.logo.entity.AexoLogoEntity;

public class SpawnLogoCommand extends Command {

    public SpawnLogoCommand() {
        super("spawnlogo", "Spawns the 3D AEXO Logo Entity", "/spawnlogo", new String[]{"aexologo"});
        this.setPermission("aexo.logo.admin");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(TextFormat.RED + "This command can only be used in-game.");
            return false;
        }

        if (!player.hasPermission("aexo.logo.admin")) {
            player.sendMessage(TextFormat.RED + "You do not have permission to use this command.");
            return false;
        }

        Location loc = player.getLocation();

        // Build NBT tags containing position and facing direction
        CompoundTag nbt = new CompoundTag()
                .putList(new ListTag<DoubleTag>("Pos")
                        .add(new DoubleTag("", loc.x))
                        .add(new DoubleTag("", loc.y + 1.2))
                        .add(new DoubleTag("", loc.z)))
                .putList(new ListTag<FloatTag>("Rotation")
                        .add(new FloatTag("", (float) loc.yaw))
                        .add(new FloatTag("", (float) loc.pitch)));

        // Spawn custom geometry entity
        Entity logoEntity = Entity.createEntity(AexoLogoEntity.NETWORK_ID, player.getChunk(), nbt);
        if (logoEntity != null) {
            logoEntity.spawnToAll();
        }

        // Add Hive-style floating subtext underneath
        FloatingTextParticle subText = new FloatingTextParticle(
                loc.add(0, -0.2, 0),
                "",
                TextFormat.colorize('&', "&e&lWELCOME TO AEXO NETWORK\n&7play.aexo.fun")
        );
        player.getLevel().addParticle(subText);

        player.sendMessage(TextFormat.GREEN + "Spawned 3D AEXO Logo!");
        return true;
    }
}