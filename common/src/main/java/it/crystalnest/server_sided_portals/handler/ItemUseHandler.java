package it.crystalnest.server_sided_portals.handler;

import it.crystalnest.server_sided_portals.api.CustomPortalChecker;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.portal.PortalShape;

import java.util.Optional;

/**
 * Handler for item use events.
 */
public abstract class ItemUseHandler {
  /**
   * Handles the player using an item on a block.<br>
   * Checks whether it should light up a Custom Portal.
   *
   * @param level {@link Level} dimension.
   * @param player {@link Player} player.
   * @param hand {@link InteractionHand} hand used.
   * @param pos {@link BlockPos} position of the right-clicked block.
   * @param face {@link Direction} face of the right-clicked block.
   * @return whether the interaction was consumed.
   */
  protected boolean handle(Level level, Player player, InteractionHand hand, BlockPos pos, Direction face) {
    if (level instanceof ServerLevel server && !player.isSpectator()) {
      Optional<TagKey<Block>> frame = CustomPortalChecker.getDimensionsWithCustomPortal(server).stream().map(CustomPortalChecker::getCustomPortalFrameTag).filter(tag -> server.getBlockState(pos).is(tag)).findAny();
      Optional<TagKey<Item>> igniter = CustomPortalChecker.getDimensionsWithCustomPortal(server).stream().map(CustomPortalChecker::getCustomPortalIgniterTag).filter(tag -> player.getItemInHand(hand).is(tag)).findAny();
      if (frame.isPresent() && igniter.isPresent()) {
        Optional<PortalShape> portal = PortalShape.findEmptyPortalShape(server, pos.relative(face), Direction.Axis.X);
        if (portal.isPresent()) {
          ResourceKey<Level> dimension = ((CustomPortalChecker) portal.get()).dimension();
          if (CustomPortalChecker.getCustomPortalFrameTag(dimension).equals(frame.get()) && CustomPortalChecker.getCustomPortalIgniterTag(dimension).equals(igniter.get())) {
            player.swing(hand, true);
            portal.get().createPortalBlocks(level);
            return true;
          }
        }
      }
    }
    return false;
  }
}
