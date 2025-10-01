package io.github.apace100.origins.power;

import net.minecraft.world.entity.player.Player;

/**
 * Base class for gameplay powers. Concrete implementations should override
 * {@link #tick(Player)} and other lifecycle hooks once full gameplay logic is
 * implemented.
 */
public abstract class Power {
    private final PowerType<?> type;

    protected Power(PowerType<?> type) {
        this.type = type;
    }

    public PowerType<?> getType() {
        return type;
    }

    /**
     * Called every tick while the power is active on a player.
     */
    public void tick(Player player) {
        // Default no-op
    }
}
