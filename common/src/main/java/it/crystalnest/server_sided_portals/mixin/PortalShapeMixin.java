package it.crystalnest.server_sided_portals.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import it.crystalnest.server_sided_portals.api.CustomPortalChecker;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.portal.PortalShape;
import org.apache.commons.lang3.mutable.MutableInt;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Injects into {@link PortalShape} to alter dimension travel.
 */
@Mixin(PortalShape.class)
public abstract class PortalShapeMixin implements CustomPortalChecker {
  /**
   * Whether the dimension has already been set and finalized.
   */
  @Unique
  private boolean dimensionSet = false;

  /**
   * Related Custom Dimension.<br>
   * Defaults to the {@link Level#NETHER Nether}.
   */
  @Unique
  private ResourceKey<Level> dimension = Level.NETHER;

  /**
   * Shadowed {@link PortalShape#isEmpty(BlockState)}.
   *
   * @return whether the block state is not a solid block.
   */
  @Shadow
  private static boolean isEmpty(BlockState state) {
    throw new UnsupportedOperationException("Tried to call a dummy body of a shadowed method: PortalShape#isEmpty(BlockState)");
  }

  /**
   * Modifies any return value from the method {@link PortalShape#findAnyShape(BlockGetter, BlockPos, Direction.Axis)}.<br>
   * Checks for custom dimensions too.
   *
   * @param original original returned value, the portal calculated with only obsidian and nether in mind.
   * @param level current dimension.
   * @param pos initial position.
   * @param axis portal orientation.
   * @return {@link PortalShape} that can either be invalid, the original, or a valid custom one.
   */
  @ModifyReturnValue(method = "findAnyShape", at = @At(value = "RETURN"))
  private static PortalShape modifyFindAnyShape(PortalShape original, BlockGetter level, BlockPos pos, Direction.Axis axis) {
    if (level instanceof ServerLevel serverLevel) {
      Direction direction = axis == Direction.Axis.X ? Direction.WEST : Direction.SOUTH;
      if (original.isValid() && CustomPortalChecker.isCustomDimension(serverLevel)) {
        // If it's a Nether Portal, and we are in a Custom Dimension, prevent creating the portal.
        return new PortalShape(axis, 0, direction, pos, 0, 0);
      }
      if (!original.isValid() && (serverLevel.dimension() == Level.OVERWORLD || CustomPortalChecker.isCustomDimension(serverLevel))) {
        // If it's not a Nether Portal, and we are either in the Overworld or in a Custom Dimension, check if it's a Custom Portal.
        int width = 0;
        BlockPos bottomLeft = null;
        for (ResourceKey<Level> dimension : CustomPortalChecker.getCustomDimensions(serverLevel)) {
          TagKey<Block> frameBlock = CustomPortalChecker.getCustomPortalFrameBlockTag(dimension);
          bottomLeft = calculateBottomLeftForCustomDimension(level, direction, pos, frameBlock);
          if (bottomLeft != null) {
            width = calculateWidthForCustomDimension(level, bottomLeft, direction, frameBlock);
            if (width > 0) {
              MutableInt portalBlocks = new MutableInt();
              // The first Custom Dimension to match breaks the loop and validates the Custom Portal.
              PortalShape portal = new PortalShape(axis, portalBlocks.getValue(), direction, bottomLeft, width, calculateHeightForCustomDimension(level, bottomLeft, direction, width, portalBlocks, frameBlock));
              ((CustomPortalChecker) portal).setDimension(dimension);
              return portal;
            }
          }
        }
        // If, after checking all Custom Dimensions, the portal is not valid, prevent creating the portal.
        if (bottomLeft == null) {
          return new PortalShape(axis, 0, direction, pos, 0, 0);
        }
        if (width == 0) {
          return new PortalShape(axis, 0, direction, bottomLeft, 0, 0);
        }
      }
    }
    if (original.isValid()) {
      ((CustomPortalChecker) original).setDimension(Level.NETHER);
    }
    return original;
  }

  /**
   * Copy-paste of {@link PortalShape#calculateBottomLeft(BlockGetter, Direction, BlockPos)}, changed to use the proper frame block(s).
   *
   * @param level current dimension.
   * @param direction checking direction.
   * @param pos initial position.
   * @param frameBlock frame block tag.
   * @return position of the bottom left corner.
   */
  @Unique
  @Nullable
  @SuppressWarnings({"ConstantValue", "StatementWithEmptyBody"})
  private static BlockPos calculateBottomLeftForCustomDimension(BlockGetter level, Direction direction, BlockPos pos, TagKey<Block> frameBlock) {
    for (int i = Math.max(level.getMinY(), pos.getY() - 21); pos.getY() > i && isEmpty(level.getBlockState(pos.below())); pos = pos.below()) ;
    Direction dir = direction.getOpposite();
    int j = getDistanceUntilEdgeAboveFrameForCustomDimension(level, pos, dir, frameBlock) - 1;
    return j < 0 ? null : pos.relative(dir, j);
  }

  /**
   * Copy-paste of {@link PortalShape#calculateWidth(BlockGetter, BlockPos, Direction)}, changed only to use the proper frame block(s).
   *
   * @param level current dimension.
   * @param pos initial position.
   * @param direction checking direction.
   * @param frameBlock frame block tag.
   * @return portal width.
   */
  @Unique
  private static int calculateWidthForCustomDimension(BlockGetter level, BlockPos pos, Direction direction, TagKey<Block> frameBlock) {
    int i = getDistanceUntilEdgeAboveFrameForCustomDimension(level, pos, direction, frameBlock);
    return i >= 2 && i <= 21 ? i : 0;
  }

  /**
   * Copy-paste of {@link PortalShape#getDistanceUntilEdgeAboveFrame(BlockGetter, BlockPos, Direction)}, changed only to use the proper frame block(s).
   *
   * @param level current dimension.
   * @param pos initial position.
   * @param direction checking direction.
   * @param frameBlock frame block tag.
   * @return distance until frame edge.
   */
  @Unique
  @SuppressWarnings("ConstantValue")
  private static int getDistanceUntilEdgeAboveFrameForCustomDimension(BlockGetter level, BlockPos pos, Direction direction, TagKey<Block> frameBlock) {
    BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
    for (int i = 0; i <= 21; i++) {
      BlockState state = level.getBlockState(mutablePos.set(pos).move(direction, i));
      if (!isEmpty(state)) {
        if (state.is(frameBlock)) {
          return i;
        }
        break;
      }
      if (!level.getBlockState(mutablePos.move(Direction.DOWN)).is(frameBlock)) {
        break;
      }
    }
    return 0;
  }

  /**
   * Copy-paste of {@link PortalShape#calculateHeight(BlockGetter, BlockPos, Direction, int, MutableInt)}, changed only to use the proper frame block(s).
   *
   * @param level current dimension.
   * @param pos initial position.
   * @param direction checking direction.
   * @param width portal width.
   * @param portalBlocks amount of portal blocks.
   * @param frameBlock frame block tag.
   * @return portal height.
   */
  @Unique
  private static int calculateHeightForCustomDimension(BlockGetter level, BlockPos pos, Direction direction, int width, MutableInt portalBlocks, TagKey<Block> frameBlock) {
    BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
    int i = getDistanceUntilTopForCustomDimension(level, pos, direction, mutablePos, width, portalBlocks, frameBlock);
    return i >= 3 && i <= 21 && hasTopFrameForCustomDimension(level, pos, direction, mutablePos, width, i, frameBlock) ? i : 0;
  }

  /**
   * Copy-paste of {@link PortalShape#getDistanceUntilTop(BlockGetter, BlockPos, Direction, BlockPos.MutableBlockPos, int, MutableInt)}, changed only to use the proper frame block(s).
   *
   * @param level current dimension.
   * @param pos initial position.
   * @param direction checking direction.
   * @param checkPos current position.
   * @param width portal width.
   * @param portalBlocks amount of portal blocks.
   * @param frameBlock frame block tag.
   * @return distance until frame top.
   */
  @Unique
  private static int getDistanceUntilTopForCustomDimension(BlockGetter level, BlockPos pos, Direction direction, BlockPos.MutableBlockPos checkPos, int width, MutableInt portalBlocks, TagKey<Block> frameBlock) {
    for (int i = 0; i < 21; i++) {
      checkPos.set(pos).move(Direction.UP, i).move(direction, -1);
      if (!level.getBlockState(checkPos).is(frameBlock)) {
        return i;
      }
      checkPos.set(pos).move(Direction.UP, i).move(direction, width);
      if (!level.getBlockState(checkPos).is(frameBlock)) {
        return i;
      }
      for (int j = 0; j < width; ++j) {
        checkPos.set(pos).move(Direction.UP, i).move(direction, j);
        BlockState blockstate = level.getBlockState(checkPos);
        if (!isEmpty(blockstate)) {
          return i;
        }
        if (blockstate.is(Blocks.NETHER_PORTAL)) {
          portalBlocks.increment();
        }
      }
    }
    return 21;
  }

  /**
   * Copy-paste of {@link PortalShape#hasTopFrame(BlockGetter, BlockPos, Direction, BlockPos.MutableBlockPos, int, int)}, changed only to use the proper frame block(s).
   *
   * @param level current dimension.
   * @param pos initial position.
   * @param direction checking direction.
   * @param checkPos current position.
   * @param width portal width.
   * @param distanceUntilTop distance until frame top.
   * @param frameBlock frame block tag.
   * @return whether the frame has a top.
   */
  @Unique
  private static boolean hasTopFrameForCustomDimension(BlockGetter level, BlockPos pos, Direction direction, BlockPos.MutableBlockPos checkPos, int width, int distanceUntilTop, TagKey<Block> frameBlock) {
    for (int i = 0; i < width; ++i) {
      BlockPos.MutableBlockPos mutablePos = checkPos.set(pos).move(Direction.UP, distanceUntilTop).move(direction, i);
      if (!level.getBlockState(mutablePos).is(frameBlock)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Shadowed {@link PortalShape#isValid()}.
   *
   * @return whether the portal is valid.
   */
  @Shadow
  public abstract boolean isValid();

  @Override
  public ResourceKey<Level> dimension() {
    return dimension;
  }

  @Override
  public void setDimension(ResourceKey<Level> dimension) throws IllegalStateException {
    if (!dimensionSet) {
      this.dimension = dimension;
      this.dimensionSet = true;
    } else {
      throw new IllegalStateException("Portal dimension was already set");
    }
  }

  /**
   * Injects at the end of the constructor.<br>
   * Finalizes the default dimension if the portal is not valid.
   *
   * @param axis portal orientation.
   * @param numPortalBlocks amount of portal blocks.
   * @param direction building direction.
   * @param bottomLeft position of the bottom left corner.
   * @param width portal width.
   * @param height portal height.
   * @param ci {@link CallbackInfo}.
   */
  @Inject(method = "<init>", at = @At(value = "TAIL"))
  private void finalizeDimension(Direction.Axis axis, int numPortalBlocks, Direction direction, BlockPos bottomLeft, int width, int height, CallbackInfo ci) {
    if (!this.isValid()) {
      this.setDimension(Level.NETHER);
    }
  }
}
