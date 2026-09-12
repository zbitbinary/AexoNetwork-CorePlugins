package fun.aexo.lobby.npc;

import cn.nukkit.entity.EntityHuman;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.TextFormat;

public class ServerNPC extends EntityHuman {

    private String serverTarget = "lobby";

    public ServerNPC(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public void initEntity() {
        super.initEntity();
        if (this.namedTag.contains("ServerTarget")) {
            this.serverTarget = this.namedTag.getString("ServerTarget");
        }
        this.setNameTag(TextFormat.BOLD.toString() + TextFormat.GOLD + serverTarget.toUpperCase() + "\n" + TextFormat.GREEN + "[CLICK TO JOIN]");
        this.setNameTagAlwaysVisible(true);
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        this.namedTag.putString("ServerTarget", this.serverTarget);
    }

    public String getServerTarget() {
        return serverTarget;
    }

    public void setServerTarget(String serverTarget) {
        this.serverTarget = serverTarget;
    }
}