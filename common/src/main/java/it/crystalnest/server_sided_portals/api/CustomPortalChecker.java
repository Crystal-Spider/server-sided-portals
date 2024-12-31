package it.crystalnest.server_sided_portals.api;

import it.crystalnest.server_sided_portals.Constants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.NetherPortalBlock;
import net.minecraft.world.level.portal.PortalShape;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;
import java.util.Optional;

/**
 * Handles checking whether a portal frame is for a Custom Portal.
 */
public interface CustomPortalChecker {
  /**
   * Gets the Custom Dimension related to the Custom Portal at the given position.
   *
   * @param level dimension.
   * @param pos position.
   * @return portal related dimension.
   */
  static ResourceKey<Level> getPortalDimension(Level level, BlockPos pos) {
    return ((CustomPortalChecker) PortalShape.findAnyShape(level, pos, level.getBlockState(pos).getOptionalValue(NetherPortalBlock.AXIS).orElse(Axis.X))).dimension();
  }

  /**
   * Checks whether the Portal at the given position is for the given dimension.
   *
   * @param level current dimension.
   * @param pos position.
   * @param dimension target dimension.
   * @return whether the Portal at the given position is for the given dimension.
   */
  static boolean isPortalForDimension(Level level, BlockPos pos, ResourceKey<Level> dimension) {
    return getPortalDimension(level, pos) == dimension;
  }

  /**
   * Checks whether the Portal at the given position is for the specified dimension.
   *
   * @param level current dimension.
   * @param pos position.
   * @param dimension name of the target dimension.
   * @return whether the Portal at the given position is for the specified dimension.
   */
  static boolean isPortalForDimension(Level level, BlockPos pos, ResourceLocation dimension) {
    return getPortalDimension(level, pos).location().equals(dimension);
  }

  /**
   * Checks whether there is a Custom Portal in the given dimension at the given position.
   *
   * @param level dimension.
   * @param pos position.
   * @return whether there is a Custom Portal.
   */
  static boolean isCustomPortal(Level level, BlockPos pos) {
    return hasCustomPortalFrame(getPortalDimension(level, pos));
  }

  /**
   * Returns the list of dimensions with a Custom Portal Frame.
   *
   * @param level {@link ServerLevel}.
   * @return the list of dimensions with a Custom Portal Frame.
   */
  static List<ResourceKey<Level>> getDimensionsWithCustomPortal(ServerLevel level) {
    return level.getServer().levelKeys().stream().filter(CustomPortalChecker::hasCustomPortalFrame).toList();
  }

  /**
   * Whether the given dimension has a Custom Portal Frame.
   *
   * @param level dimension.
   * @return whether the given dimension has a Custom Portal Frame.
   */
  static boolean hasCustomPortalFrame(Level level) {
    return hasCustomPortalFrame(level.dimension());
  }

  /**
   * Whether the given dimension has a Custom Portal Frame.
   *
   * @param dimension dimension key.
   * @return whether the given dimension has a Custom Portal Frame.
   */
  static boolean hasCustomPortalFrame(ResourceKey<Level> dimension) {
    return BuiltInRegistries.BLOCK.get(getCustomPortalFrameTag(dimension)).isPresent();
  }

  /**
   * Returns the Block Tag for the Custom Portal Frame related to the given dimension.
   *
   * @param dimension dimension.
   * @return Block Tag for the Custom Portal Frame.
   */
  static TagKey<Block> getCustomPortalFrameTag(ResourceKey<Level> dimension) {
    return TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, dimension.location().getPath() + "_portal_frame"));
  }

  /**
   * Returns a random Block from the Custom Portal Frame tag related to the given dimension.
   *
   * @param level dimension.
   * @return a random Block from the Custom Portal Frame tag.
   */
  static Block getCustomPortalFrameBlock(Level level) {
    return BuiltInRegistries.BLOCK.get(getCustomPortalFrameTag(level.dimension())).map(holders -> holders.getRandomElement(level.getRandom()).orElse(Holder.direct(Blocks.OBSIDIAN)).value()).orElse(Blocks.OBSIDIAN);
  }

  /**
   * Whether the given dimension has a Custom Portal Igniter item.
   *
   * @param level dimension.
   * @return whether the given dimension has a Custom Portal Igniter item.
   */
  static boolean hasCustomPortalIgniter(Level level) {
    return hasCustomPortalIgniter(level.dimension());
  }

  /**
   * Whether the given dimension has a Custom Portal Igniter item.
   *
   * @param dimension dimension key.
   * @return whether the given dimension has a Custom Portal Igniter item.
   */
  static boolean hasCustomPortalIgniter(ResourceKey<Level> dimension) {
    return BuiltInRegistries.ITEM.get(getCustomPortalIgniterTag(dimension)).isPresent();
  }

  /**
   * Returns the Item Tag for the Custom Portal Igniter item related to the given dimension.
   *
   * @param dimension dimension.
   * @return Item Tag for the Custom Portal Igniter item.
   */
  static TagKey<Item> getCustomPortalIgniterTag(ResourceKey<Level> dimension) {
    return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, dimension.location().getPath() + "_portal_igniter"));
  }

  /**
   * Returns the {@link Optional} {@link HolderSet.Named} of {@link Item}s from the Custom Portal Igniter Item Tag related to the given dimension.
   *
   * @param dimension dimension key.
   * @return a random Block from the Custom Portal Frame tag.
   */
  static Optional<HolderSet.Named<Item>> getCustomPortalIgniterItems(ResourceKey<Level> dimension) {
    return BuiltInRegistries.ITEM.get(getCustomPortalIgniterTag(dimension));
  }

  /**
   * Custom Portal dimension.
   *
   * @return portal dimension.
   */
  ResourceKey<Level> dimension();

  /**
   * Sets the dimension related to this portal.<br>
   * Internal use only, calling this outside or after the portal initialization will result in an {@link IllegalStateException}.
   *
   * @param dimension dimension.
   * @throws IllegalStateException if called after initialization.
   */
  @ApiStatus.Internal
  void setDimension(ResourceKey<Level> dimension) throws IllegalStateException;
}
