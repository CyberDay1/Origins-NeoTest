package io.github.apace100.origins.power.action.impl;
import io.github.apace100.origins.util.ResourceLocationCompat;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.power.action.Action;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
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
 * Datapack action that prevents block interactions against any block contained
 * in the configured block tag.
 */
public final class RestrictBlockUseAction implements Action<Player> {
    public static final ResourceLocation TYPE = ResourceLocationCompat.mod("restrict_block_use");
    private static final ThreadLocal<BlockContext> CONTEXT = new ThreadLocal<>();
    private static final Codec<RestrictBlockUseAction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        ResourceLocation.CODEC.fieldOf("tag").forGetter(action -> action.tag.location())
    ).apply(instance, id -> new RestrictBlockUseAction(TagKey.create(Registries.BLOCK, id))));

    private final TagKey<Block> tag;

    private RestrictBlockUseAction(TagKey<Block> tag) {
        this.tag = tag;
    }

    private TagKey<Block> tag() {
        return tag;
    }

    @Override
    public void execute(Player player) {
        if (player == null) {
            return;
        }

        Optional<BlockInteraction> interaction = Optional.ofNullable(CONTEXT.get())
            .map(BlockContext::interaction)
            .or(() -> resolveFromRaycast(player));
        if (interaction.isEmpty()) {
            return;
        }

        BlockInteraction target = interaction.get();
        BlockState state = target.state();
        if (!state.is(tag)) {
            return;
        }

        BlockContext context = CONTEXT.get();
        if (context != null) {
            context.cancel().run();
        }

        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.connection.send(new ClientboundBlockUpdatePacket(target.pos(), state));
        }
        player.swing(InteractionHand.MAIN_HAND, true);
        player.closeContainer();
    }

    public static RestrictBlockUseAction fromJson(ResourceLocation id, JsonObject json) {
        if (!json.has("tag")) {
            Origins.LOGGER.warn("Restrict block use action '{}' is missing required 'tag' field", id);
            return null;
        }

        String rawTag = GsonHelper.getAsString(json, "tag");
        ResourceLocation tagId;
        try {
            tagId = ResourceLocation.parse(rawTag);
        } catch (IllegalArgumentException exception) {
            Origins.LOGGER.warn("Restrict block use action '{}' has invalid tag id '{}': {}", id, rawTag, exception.getMessage());
            return null;
        }

        return new RestrictBlockUseAction(TagKey.create(Registries.BLOCK, tagId));
    }

    public static Codec<RestrictBlockUseAction> codec() {
        return CODEC;
    }

    public static void withContext(BlockState state, BlockPos pos, Runnable cancel, Runnable runnable) {
        CONTEXT.set(new BlockContext(new BlockInteraction(state, pos), cancel));
        try {
            runnable.run();
        } finally {
            CONTEXT.remove();
        }
    }

    private static Optional<BlockInteraction> resolveFromRaycast(Player player) {
        Level level = player.level();
        if (level == null) {
            return Optional.empty();
        }

        HitResult hitResult = player.pick(5.0D, 0.0F, false);
        if (!(hitResult instanceof BlockHitResult blockHit)) {
            return Optional.empty();
        }

        BlockPos pos = blockHit.getBlockPos();
        BlockState state = level.getBlockState(pos);
        return Optional.of(new BlockInteraction(state, pos));
    }

    private record BlockContext(BlockInteraction interaction, Runnable cancel) {
    }

    private record BlockInteraction(BlockState state, BlockPos pos) {
    }
}
