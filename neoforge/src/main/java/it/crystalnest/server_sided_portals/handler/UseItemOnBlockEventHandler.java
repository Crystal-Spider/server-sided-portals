package it.crystalnest.server_sided_portals.handler;

import it.crystalnest.server_sided_portals.Constants;
import net.minecraft.world.InteractionResult;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.UseItemOnBlockEvent;

/**
 * Handles {@link UseItemOnBlockEvent}s.
 */
@EventBusSubscriber(modid = Constants.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public final class UseItemOnBlockEventHandler extends ItemUseHandler {
  /**
   * Singleton instance for this event handler.
   */
  private static final UseItemOnBlockEventHandler INSTANCE = new UseItemOnBlockEventHandler();

  private UseItemOnBlockEventHandler() {}

  /**
   * Handles the {@link UseItemOnBlockEvent}.
   *
   * @param event {@link UseItemOnBlockEvent}.
   */
  @SubscribeEvent
  public static void handle(UseItemOnBlockEvent event) {
    if (INSTANCE.handle(event.getLevel(), event.getPlayer(), event.getHand(), event.getUseOnContext().getClickedPos(), event.getUseOnContext().getClickedFace())) {
      event.cancelWithResult(InteractionResult.SUCCESS);
    }
  }
}
