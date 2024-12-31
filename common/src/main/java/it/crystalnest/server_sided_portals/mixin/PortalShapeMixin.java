package it.crystalnest.server_sided_portals.mixin;

import it.crystalnest.server_sided_portals.api.CustomPortalChecker;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.portal.PortalShape;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

/**
 * Injects into {@link PortalShape} to alter dimension travel.
 */
@Mixin(PortalShape.class)
public abstract class PortalShapeMixin implements CustomPortalChecker {
  /**
   * Shadowed {@link PortalShape#level}.
   */
  @Final
  @Shadow
  private LevelAccessor level;

  /**
   * Shadowed {@link PortalShape#rightDir}.
   */
  @Final
  @Shadow
  private Direction rightDir;

  /**
   * Shadowed {@link PortalShape#numPortalBlocks}.
   */
  @Shadow
  private int numPortalBlocks;

  /**
   * Shadowed {@link PortalShape#bottomLeft}.
   */
  @Shadow
  @Nullable
  private BlockPos bottomLeft;

  /**
   * Shadowed {@link PortalShape#width}.
   */
  @Final
  @Shadow
  private int width;

  /**
   * Shadowed {@link PortalShape#height}.
   */
  @Shadow
  private int height;

  /**
   * Related Custom Dimension.<br/>
   * Defaults to the {@link Level#NETHER Nether}.
   */
  @Unique
  private ResourceKey<Level> dimension = Level.NETHER;

  /**
   * Shadowed {@link PortalShape#isEmpty(BlockState)}.
   *
   * @return whether the state is valid.
   */
  @Shadow
  private static boolean isEmpty(BlockState state) {
    throw new UnsupportedOperationException("Tried to call a dummy body of a shadowed method: PortalShape#isEmpty(BlockState)");
  }

  /**
   * Shadowed {@link PortalShape#isValid()}.
   *
   * @return whether the portal is valid.
   */
  @Shadow
  public abstract boolean isValid();

  /**
   * Accessor to allow changes to {@link PortalShape#width}.
   *
   * @param width portal width.
   */
  @Mutable
  @Accessor("width")
  protected abstract void setWidth(int width);

  @Override
  public ResourceKey<Level> dimension() {
    return dimension;
  }

  /**
   * Injects at the end of the constructor.<br>
   * Checks if a Custom Portal can be created.
   *
   * @param level dimension.
   * @param pos block position.
   * @param axis portal alignment (X or Z).
   * @param ci {@link CallbackInfo}.
   */
  @Inject(method = "<init>(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction$Axis;)V", at = @At(value = "TAIL"))
  private void onInit(LevelAccessor level, BlockPos pos, Axis axis, CallbackInfo ci) {
    if (!level.isClientSide()) {
      ServerLevel serverLevel = (ServerLevel) level;
      if (this.isValid() && CustomPortalChecker.hasCustomPortalFrame(serverLevel)) {
        // If it's a Nether Portal, and we are in a Custom Dimension, prevent creating the portal.
        this.bottomLeft = null;
        this.setWidth(1);
        height = 1;
      } else if (!isValid() && (serverLevel.dimension() == Level.OVERWORLD || CustomPortalChecker.hasCustomPortalFrame(serverLevel))) {
        // If it's not a Nether Portal, and we are either in the Overworld or in a Custom Dimension, check whether it's a Custom Portal.
        for (ResourceKey<Level> dim : CustomPortalChecker.getDimensionsWithCustomPortal(serverLevel)) {
          // A Custom Portal can light up only in the Overworld or in the Custom Dimension it is for.
          if (serverLevel.dimension() == Level.OVERWORLD || dim == serverLevel.dimension()) {
            TagKey<Block> frameBlock = CustomPortalChecker.getCustomPortalFrameTag(dim);
            bottomLeft = calculateBottomLeftForCustomDimension(pos, frameBlock);
            if (bottomLeft != null) {
              setWidth(calculateWidthForCustomDimension(frameBlock));
              if (width > 0) {
                height = calculateHeightForCustomDimension(frameBlock);
                this.dimension = dim;
                // The first Custom Dimension to match breaks the loop and validates the Custom Portal.
                break;
              }
            }
          }
        }
        // If, after checking all Custom Dimensions, the portal is not valid, prevent creating the portal.
        if (bottomLeft == null) {
          bottomLeft = pos;
          setWidth(1);
          height = 1;
        }
      }
    }
  }

  /**
   * Copy-paste of {@link PortalShape#calculateBottomLeft(BlockPos)}, changed to use the proper frame block.
   *
   * @param pos block position.
   * @param frameBlock frame block.
   * @return bottom left corner position, {@code null} if it's not a valid Custom Portal.
   */
  @Unique
  @Nullable
  @SuppressWarnings({"ConstantValue", "StatementWithEmptyBody"})
  private BlockPos calculateBottomLeftForCustomDimension(BlockPos pos, TagKey<Block> frameBlock) {
    for (int i = Math.max(level.getMinBuildHeight(), pos.getY() - 21); pos.getY() > i && isEmpty(level.getBlockState(pos.below())); pos = pos.below());
    Direction direction = rightDir.getOpposite();
    int j = getDistanceUntilEdgeAboveFrameForCustomDimension(frameBlock, pos, direction) - 1;
    return j < 0 ? null : pos.relative(direction, j);
  }

  /**
   * Copy-paste of {@link PortalShape#calculateWidth()}, changed only to use the proper frame block.
   *
   * @return Custom Portal width.
   */
  @Unique
  private int calculateWidthForCustomDimension(TagKey<Block> frameBlock) {
    int i = getDistanceUntilEdgeAboveFrameForCustomDimension(frameBlock, bottomLeft, rightDir);
    return i >= 2 && i <= 21 ? i : 0;
  }

  /**
   * Copy-paste of {@link PortalShape#getDistanceUntilEdgeAboveFrame(BlockPos, Direction)}, changed only to use the proper frame block.
   *
   * @param frameBlock frame block.
   * @param pos block position.
   * @param direction portal alignment (X or Z).
   * @return portal width.
   */
  @Unique
  @SuppressWarnings("ConstantValue")
  private int getDistanceUntilEdgeAboveFrameForCustomDimension(TagKey<Block> frameBlock, BlockPos pos, Direction direction) {
    BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
    for (int i = 0; i <= 21; i++) {
      BlockState state = this.level.getBlockState(mutablePos.set(pos).move(direction, i));
      if (!isEmpty(state)) {
        if (state.is(frameBlock)) {
          return i;
        }
        break;
      }
      if (!this.level.getBlockState(mutablePos.move(Direction.DOWN)).is(frameBlock)) {
        break;
      }
    }
    return 0;
  }

  /**
   * Copy-paste of {@link PortalShape#calculateHeight()}, changed only to use the proper frame block.
   *
   * @return Custom Portal height.
   */
  @Unique
  private int calculateHeightForCustomDimension(TagKey<Block> frameBlock) {
    BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
    int i = this.getDistanceUntilTopForCustomDimension(frameBlock, pos);
    return i >= 3 && i <= 21 && hasTopFrameForCustomDimension(frameBlock, pos, i) ? i : 0;
  }

  /**
   * Copy-paste of {@link PortalShape#hasTopFrame(BlockPos.MutableBlockPos, int)}, changed only to use the proper frame block.
   *
   * @param pos block position.
   * @param height portal height.
   * @return whether the portal has a top frame edge.
   */
  @Unique
  private boolean hasTopFrameForCustomDimension(TagKey<Block> frameBlock, BlockPos.MutableBlockPos pos, int height) {
    for (int i = 0; i < this.width; ++i) {
      BlockPos.MutableBlockPos mutablePos = pos.set(Objects.requireNonNull(this.bottomLeft)).move(Direction.UP, height).move(this.rightDir, i);
      if (!this.level.getBlockState(mutablePos).is(frameBlock)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Copy-paste of {@link PortalShape#getDistanceUntilTop(BlockPos.MutableBlockPos)}, changed only to use the proper frame block.
   *
   * @param pos block position.
   * @return portal height.
   */
  @Unique
  private int getDistanceUntilTopForCustomDimension(TagKey<Block> frameBlock, BlockPos.MutableBlockPos pos) {
    for (int i = 0; i < 21; i++) {
      pos.set(Objects.requireNonNull(this.bottomLeft)).move(Direction.UP, i).move(this.rightDir, -1);
      if (!this.level.getBlockState(pos).is(frameBlock)) {
        return i;
      }
      pos.set(this.bottomLeft).move(Direction.UP, i).move(this.rightDir, this.width);
      if (!this.level.getBlockState(pos).is(frameBlock)) {
        return i;
      }
      for (int j = 0; j < this.width; ++j) {
        pos.set(this.bottomLeft).move(Direction.UP, i).move(this.rightDir, j);
        BlockState blockstate = this.level.getBlockState(pos);
        if (!isEmpty(blockstate)) {
          return i;
        }
        if (blockstate.is(Blocks.NETHER_PORTAL)) {
          ++this.numPortalBlocks;
        }
      }
    }
    return 21;
  }
}
