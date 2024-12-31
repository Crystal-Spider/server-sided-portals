package it.crystalnest.server_sided_portals.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import it.crystalnest.server_sided_portals.api.CustomPortalChecker;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.portal.PortalShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Optional;

/**
 * Injects into {@link BaseFireBlock} to alter dimension travel.
 */
@Mixin(BaseFireBlock.class)
public abstract class BaseFireBlockMixin {
  /**
   * Checks whether there is any Custom Portal Frame Block at the given position.
   *
   * @param level dimension.
   * @param pos position.
   * @return whether there is a valid Custom Portal Frame Block.
   */
  @Unique
  private static boolean checkCustomPortalFrame(Level level, BlockPos pos) {
    return level instanceof ServerLevel server && CustomPortalChecker.getDimensionsWithCustomPortal(server).stream().map(CustomPortalChecker::getCustomPortalFrameTag).anyMatch(tag -> level.getBlockState(pos).is(tag));
  }

  /**
   * Modifies the value of the check at {@link BaseFireBlock#inPortalDimension(Level)} inside the method {@link BaseFireBlock#isPortal(Level, BlockPos, Direction)}.<br>
   * Checks also whether the dimension is suitable for a Custom Portal.
   *
   * @param original original check value.
   * @param level dimension.
   * @return check result.
   */
  @ModifyExpressionValue(method = "isPortal", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/BaseFireBlock;inPortalDimension(Lnet/minecraft/world/level/Level;)Z"))
  private static boolean modifyInPortalDimension$isPortal(boolean original, Level level) {
    return original || CustomPortalChecker.hasCustomPortalFrame(level);
  }

  /**
   * Redirects the call to {@link Level#getBlockState(BlockPos)} inside the method {@link BaseFireBlock#isPortal(Level, BlockPos, Direction)}.<br>
   * If the {@link BlockState} is for a Custom Portal Dimension, returns {@link Blocks#OBSIDIAN Obsidian} instead.
   *
   * @param instance {@link Level} owning the redirected method.
   * @param pos position.
   * @return {@link Blocks#OBSIDIAN Obsidian} or the original {@link BlockState}.
   */
  @Redirect(method = "isPortal", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"))
  private static BlockState redirectGetBlockState(Level instance, BlockPos pos) {
    return checkCustomPortalFrame(instance, pos) ? Blocks.OBSIDIAN.defaultBlockState() : instance.getBlockState(pos);
  }

  /**
   * Modifies the value of the check at {@link BaseFireBlock#inPortalDimension(Level)} inside the method {@link BaseFireBlock#onPlace(BlockState, Level, BlockPos, BlockState, boolean)}.<br>
   * Checks also whether the dimension is suitable for a Custom Portal.
   *
   * @param original original check value.
   * @param state current {@link BlockState}.
   * @param level dimension.
   * @param pos {@link BlockPos position}.
   * @param oldState previous {@link BlockState}.
   * @param isMoving whether the block is moving.
   * @return check result.
   */
  @ModifyExpressionValue(method = "onPlace", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/BaseFireBlock;inPortalDimension(Lnet/minecraft/world/level/Level;)Z"))
  private boolean modifyInPortalDimension$onPlace(boolean original, BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
    return original || CustomPortalChecker.hasCustomPortalFrame(level);
  }

  /**
   * Redirects the call to {@link Optional#isPresent()} inside the method {@link BaseFireBlock#onPlace(BlockState, Level, BlockPos, BlockState, boolean)}.<br>
   * Checks also whether the portal can be lit up by fire.
   *
   * @param instance {@link Optional} {@link PortalShape} ({@link CustomPortalChecker}).
   * @return whether the portal can be lit up.
   */
  @Redirect(method = "onPlace", at = @At(value = "INVOKE", target = "Ljava/util/Optional;isPresent()Z"))
  private boolean redirectIsPresent(Optional<PortalShape> instance) {
    return instance.isPresent() && !CustomPortalChecker.hasCustomPortalIgniter(((CustomPortalChecker) instance.get()).dimension());
  }
}
