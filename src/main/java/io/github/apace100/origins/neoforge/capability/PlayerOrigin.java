package io.github.apace100.origins.neoforge.capability;

import io.github.apace100.origins.Origins;
import io.github.apace100.origins.common.registry.ConfiguredPowers;
import io.github.apace100.origins.common.registry.OriginRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class PlayerOrigin {
    private static final String NBT_ORIGIN = "Origin";
    private static final String NBT_POWERS = "Powers";
    private static final String NBT_PHANTOMIZED = "Phantomized";

    private ResourceLocation originId; // e.g. origins:elytrian
    private final Set<ResourceLocation> powers = new HashSet<>();
    private boolean phantomized;

    public ResourceLocation getOriginId() {
        return originId;
    }

    public Optional<ResourceLocation> getOriginIdOptional() {
        return Optional.ofNullable(originId);
    }

    public void setOriginId(ResourceLocation id) {
        this.originId = id;
    }

    public boolean hasOrigin() {
        return originId != null;
    }

    public boolean hasChosen() {
        return hasOrigin();
    }

    public void clear() {
        this.originId = null;
        this.powers.clear();
        this.phantomized = false;
    }

    public Set<ResourceLocation> getPowers() {
        return powers;
    }

    public boolean hasPower(ResourceLocation powerId) {
        return powers.contains(powerId);
    }

    public void setPowers(Set<ResourceLocation> newPowers) {
        powers.clear();
        powers.addAll(newPowers);
    }

    public boolean isPhantomized() {
        return phantomized;
    }

    public void setPhantomized(boolean phantomized) {
        this.phantomized = phantomized;
    }

    public CompoundTag saveNBT() {
        CompoundTag tag = new CompoundTag();
        if (originId != null) {
            tag.putString(NBT_ORIGIN, originId.toString());
        }

        if (!powers.isEmpty()) {
            ListTag list = new ListTag();
            for (ResourceLocation powerId : powers) {
                list.add(StringTag.valueOf(powerId.toString()));
            }
            tag.put(NBT_POWERS, list);
        }

        if (phantomized) {
            tag.putBoolean(NBT_PHANTOMIZED, true);
        }

        return tag;
    }

    public void loadNBT(CompoundTag tag) {
        originId = null;
        if (tag.contains(NBT_ORIGIN, Tag.TAG_STRING)) {
            ResourceLocation parsed = ResourceLocation.tryParse(tag.getString(NBT_ORIGIN));
            if (parsed != null && OriginRegistry.get(parsed).isPresent()) {
                originId = parsed;
            } else if (parsed != null) {
                Origins.LOGGER.warn("Dropping unknown origin {} from player capability data", parsed);
            }
        }

        powers.clear();
        if (tag.contains(NBT_POWERS, Tag.TAG_LIST)) {
            ListTag list = tag.getList(NBT_POWERS, Tag.TAG_STRING);
            for (int i = 0; i < list.size(); i++) {
                ResourceLocation parsedPower = ResourceLocation.tryParse(list.getString(i));
                if (parsedPower != null && ConfiguredPowers.get(parsedPower).isPresent()) {
                    powers.add(parsedPower);
                } else if (parsedPower != null) {
                    Origins.LOGGER.warn("Skipping unknown power {} from player capability data", parsedPower);
                }
            }
        }

        phantomized = tag.getBoolean(NBT_PHANTOMIZED);
    }

    public void copyFrom(PlayerOrigin other) {
        this.originId = other.originId;
        this.powers.clear();
        this.powers.addAll(other.powers);
        this.phantomized = other.phantomized;
    }

    public CompoundTag save() {
        return saveNBT();
    }

    public void load(CompoundTag tag) {
        loadNBT(tag);
    }
}
