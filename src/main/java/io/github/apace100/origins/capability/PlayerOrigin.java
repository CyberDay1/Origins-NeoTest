package io.github.apace100.origins.capability;

import net.minecraft.nbt.CompoundTag;

public final class PlayerOrigin {
    private String originId = "";
    // TODO: attach power data here later

    public String getOriginId() { return originId; }
    public void setOriginId(String id) { this.originId = id == null ? "" : id; }

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putString("OriginId", originId);
        return tag;
    }

    public void load(CompoundTag tag) {
        if (tag == null) return;
        this.originId = tag.getString("OriginId");
    }

    public boolean hasChosen() { return !originId.isEmpty(); }
    public void clear() { originId = ""; }
}
