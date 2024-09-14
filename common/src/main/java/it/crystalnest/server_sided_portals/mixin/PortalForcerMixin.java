package it.crystalnest.server_sided_portals.mixin;

import it.crystalnest.server_sided_portals.Constants;
import it.crystalnest.server_sided_portals.api.CustomPortalChecker;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.village.poi.PoiRecord;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.portal.PortalForcer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Injects into {@link PortalForcer} to alter portal creation and location.
 */
@Mixin(PortalForcer.class)
public abstract class PortalForcerMixin {
  /**
   * Shadowed {@link PortalForcer#level}.
   */
  @Final
  @Shadow
  private ServerLevel level;

  /**
   * Returns the correct {@link BlockState} to create a portal.
   *
   * @param level destination dimension.
   * @param state block state.
   * @return the correct {@link BlockState} to create a portal.
   */
  @Unique
  private BlockState getCorrectBlockState(ServerLevel level, BlockState state) {
    if (state.is(Blocks.OBSIDIAN)) {
      ResourceKey<Level> origin = Constants.DIMENSION_ORIGIN_THREAD.get();
      return CustomPortalChecker.getCustomPortalFrameBlock(CustomPortalChecker.isCustomDimension(origin) ? Objects.requireNonNull(level.getServer().getLevel(origin)) : level).defaultBlockState();
    }
    return state;
  }

  /**
   * Redirects the call to {@link ServerLevel#setBlockAndUpdate(BlockPos, BlockState)} inside the method {@link PortalForcer#createPortal(BlockPos, Axis)}.<br />
   * Calls the same redirected method, but with the correct {@link BlockState}.
   *
   * @param instance {@link ServerLevel} owning the redirected method.
   * @param pos block position.
   * @param state block state.
   * @return whether the {@link BlockState} has been set in the {@link ServerLevel}.
   */
  @Redirect(method = "createPortal", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z"))
  private boolean redirectSetBlockStateNoFlags$createPortal(ServerLevel instance, BlockPos pos, BlockState state) {
    return instance.setBlockAndUpdate(pos, getCorrectBlockState(instance, state));
  }

  /**
   * Redirects the call to {@link ServerLevel#setBlock(BlockPos, BlockState, int)} inside the method {@link PortalForcer#createPortal(BlockPos, Axis)}.<br />
   * Calls the same redirected method, but with the correct {@link BlockState}.
   *
   * @param instance {@link ServerLevel} owning the redirected method.
   * @param pos block position.
   * @param state block state.
   * @param flags update flags.
   * @return whether the {@link BlockState} has been set in the {@link ServerLevel}.
   */
  @Redirect(method = "createPortal", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z", ordinal = 0))
  private boolean redirectSetBlockStateWithFlags$createPortal(ServerLevel instance, BlockPos pos, BlockState state, int flags) {
    return instance.setBlock(pos, getCorrectBlockState(instance, state), flags);
  }

  /**
   * Redirects the call to {@link Stream#filter(Predicate)} inside the method {@link PortalForcer#findPortalAround(BlockPos, boolean, WorldBorder)}.<br />
   * Adds a new condition to the predicate to prevent teleporting from Nether Portals to custom portals and vice versa.
   *
   * @param instance stream of {@link PoiRecord}s owning the redirected method.
   * @param predicate whether the portal is within bounds.
   * @return filtered stream of {@link PoiRecord}s that represent matching portals.
   */
  @Redirect(method = "findPortalAround", at = @At(value = "INVOKE", target = "Ljava/util/stream/Stream;filter(Ljava/util/function/Predicate;)Ljava/util/stream/Stream;", ordinal = 1))
  private Stream<PoiRecord> redirectFilter(Stream<PoiRecord> instance, Predicate<? super PoiRecord> predicate) {
    return instance.filter(poi -> predicate.test(poi) && (level.dimension() != Level.OVERWORLD || CustomPortalChecker.isPortalForDimension(level, poi.getPos(), Constants.DIMENSION_ORIGIN_THREAD.get())));
  }
}