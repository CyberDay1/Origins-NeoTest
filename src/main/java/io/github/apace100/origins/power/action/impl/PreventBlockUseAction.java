package io.github.apace100.origins.power.action.impl;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.power.action.Action;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.Optional;

/**
 * Datapack action that cancels block interactions against a configured block.
 */
public final class PreventBlockUseAction implements Action<Player> {
    public static final ResourceLocation TYPE = ResourceLocation.fromNamespaceAndPath(Origins.MOD_ID, "prevent_block_use");
    private static final ThreadLocal<BlockInteraction> CONTEXT = new ThreadLocal<>();
    private static final Codec<PreventBlockUseAction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        BuiltInRegistries.BLOCK.byNameCodec().fieldOf("block").forGetter(PreventBlockUseAction::block)
    ).apply(instance, PreventBlockUseAction::new));

    private final Block block;

    private PreventBlockUseAction(Block block) {
        this.block = block;
    }

    public Block block() {
        return block;
    }

    @Override
    public void execute(Player player) {
        if (player == null) {
            return;
        }

        Level level = player.level();
        if (level == null) {
            return;
        }

        Optional<BlockInteraction> interaction = Optional.ofNullable(CONTEXT.get())
            .or(() -> resolveFromRaycast(player, level));
        if (interaction.isEmpty()) {
            return;
        }

        BlockInteraction target = interaction.get();
        BlockState state = target.state();
        if (!state.is(block)) {
            return;
        }

        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.connection.send(new ClientboundBlockUpdatePacket(target.pos(), state));
        }
        player.swing(InteractionHand.MAIN_HAND, true);
        player.closeContainer();
    }

    public static PreventBlockUseAction fromJson(ResourceLocation id, JsonObject json) {
        if (!json.has("block")) {
            Origins.LOGGER.warn("Prevent block use action '{}' is missing required 'block' field", id);
            return null;
        }

        String raw = GsonHelper.getAsString(json, "block");
        ResourceLocation blockId;
        try {
            blockId = ResourceLocation.parse(raw);
        } catch (IllegalArgumentException exception) {
            Origins.LOGGER.warn("Prevent block use action '{}' has invalid block id '{}': {}", id, raw, exception.getMessage());
            return null;
        }

        Optional<Block> resolved = BuiltInRegistries.BLOCK.getOptional(blockId);
        if (resolved.isEmpty()) {
            Origins.LOGGER.warn("Prevent block use action '{}' references unknown block '{}'", id, blockId);
            return null;
        }

        return new PreventBlockUseAction(resolved.get());
    }

    public static Codec<PreventBlockUseAction> codec() {
        return CODEC;
    }

    public static void withContext(BlockState state, BlockPos pos, Runnable runnable) {
        CONTEXT.set(new BlockInteraction(state, pos));
        try {
            runnable.run();
        } finally {
            CONTEXT.remove();
        }
    }

    private static Optional<BlockInteraction> resolveFromRaycast(Player player, Level level) {
        HitResult hitResult = player.pick(5.0D, 0.0F, false);
        if (!(hitResult instanceof BlockHitResult blockHit)) {
            return Optional.empty();
        }

        BlockPos pos = blockHit.getBlockPos();
        BlockState state = level.getBlockState(pos);
        return Optional.of(new BlockInteraction(state, pos));
    }

    private record BlockInteraction(BlockState state, BlockPos pos) {
    }

}
