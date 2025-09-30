package io.github.apace100.origins.platform.capabilities;

import net.minecraft.nbt.CompoundTag;

public class PlayerOrigin {
    private String originId = "origins:empty";

    public String getOriginId() {
        return originId;
    }

    public void setOriginId(String originId) {
        this.originId = originId;
    }

    public CompoundTag saveNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putString("OriginId", originId);
        return tag;
    }

    public void loadNBT(CompoundTag tag) {
        if (tag.contains("OriginId")) {
            originId = tag.getString("OriginId");
        }
    }
}
