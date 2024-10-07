package it.crystalnest.server_sided_portals.platform;

import it.crystalnest.cobweb.platform.model.Platform;
import it.crystalnest.server_sided_portals.platform.services.PlatformHelper;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;

/**
 * Forge platform helper.
 */
public final class ForgePlatformHelper implements PlatformHelper {
  @Override
  public Platform getPlatformName() {
    return Platform.FORGE;
  }

  @Override
  public boolean isModLoaded(String modId) {
    return ModList.get().isLoaded(modId);
  }

  @Override
  public boolean isDevEnv() {
    return !FMLLoader.isProduction();
  }
}