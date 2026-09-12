package fun.aexo.logo.entity;

import cn.nukkit.entity.Entity;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.Identifier;

public class AexoLogoEntity extends Entity {

    public static final String NAMESPACE = "aexo";
    public static final String PATH = "logo";
    public static final String NETWORK_ID = NAMESPACE + ":" + PATH;

    public AexoLogoEntity(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return 0;
    }

    @Override
    public Identifier getIdentifier() {
        // Pass namespace and path separately
        return Identifier.of(NAMESPACE, PATH);
    }

    @Override
    public float getWidth() {
        return 3.0f;
    }

    @Override
    public float getHeight() {
        return 1.5f;
    }

    @Override
    public void initEntity() {
        super.initEntity();
        this.setImmobile(true);
        this.setNameTagVisible(false);
    }
}