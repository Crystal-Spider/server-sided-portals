package it.crystalnest.server_sided_portals.handler;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Handles {@link UseBlockCallback} events.
 */
public final class UseBlockHandler extends ItemUseHandler {
  /**
   * Singleton instance for this event handler.
   */
  private static final UseBlockHandler INSTANCE = new UseBlockHandler();

  private UseBlockHandler() {}

  /**
   * Handles {@link UseBlockCallback} events.
   *
   * @param player {@link Player} trying to use the block.
   * @param level {@link Level} where the interaction is taking place.
   * @param hand {@link InteractionHand} used.
   * @param blockHitResult {@link BlockHitResult}.
   * @return result of the interaction.
   */
  public static InteractionResult handle(Player player, Level level, InteractionHand hand, BlockHitResult blockHitResult) {
    return !player.getItemInHand(hand).isEmpty() && INSTANCE.handle(level, player, hand, blockHitResult.getBlockPos(), blockHitResult.getDirection()) ? InteractionResult.SUCCESS : InteractionResult.PASS;
  }
}
