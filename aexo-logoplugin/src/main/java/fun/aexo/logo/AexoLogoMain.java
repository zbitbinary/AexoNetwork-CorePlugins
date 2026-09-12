package fun.aexo.logo;

import cn.nukkit.entity.Entity;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.TextFormat;
import fun.aexo.logo.commands.SpawnLogoCommand;
import fun.aexo.logo.entity.AexoLogoEntity;

public class AexoLogoMain extends PluginBase {

    @Override
    public void onEnable() {
        // Register custom Bedrock client entity
        Entity.registerEntity(AexoLogoEntity.NETWORK_ID, AexoLogoEntity.class);

        // Register the standalone command
        this.getServer().getCommandMap().register("spawnlogo", new SpawnLogoCommand());

        this.getLogger().info(TextFormat.GREEN + "AexoLogo plugin enabled successfully!");
    }

    @Override
    public void onDisable() {
        this.getLogger().info(TextFormat.RED + "AexoLogo plugin disabled.");
    }
}