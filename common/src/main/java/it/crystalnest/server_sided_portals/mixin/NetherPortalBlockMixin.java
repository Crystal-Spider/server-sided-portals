package it.crystalnest.server_sided_portals.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import it.crystalnest.server_sided_portals.Constants;
import it.crystalnest.server_sided_portals.api.CustomPortalChecker;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.NetherPortalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.portal.TeleportTransition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

/**
 * Injects into {@link NetherPortalBlock} to alter Custom Portals mob spawn and dimension travel.
 */
@Mixin(NetherPortalBlock.class)
public abstract class NetherPortalBlockMixin {
  /**
   * Injects at the start of the method {@link NetherPortalBlock#getExitPortal(ServerLevel, Entity, BlockPos, BlockPos, boolean, WorldBorder)}.<br />
   * Sets the origin dimension for this entity.
   *
   * @param destination destination.
   * @param entity entity travelling.
   * @param pos entrance position.
   * @param exitPos exit position.
   * @param isNether whether the destination is the Nether.
   * @param worldBorder world border.
   * @param cir {@link CallbackInfoReturnable}.
   */
  @Inject(method = "getExitPortal", at = @At(value = "HEAD"))
  private void onGetExitPortal(ServerLevel destination, Entity entity, BlockPos pos, BlockPos exitPos, boolean isNether, WorldBorder worldBorder, CallbackInfoReturnable<TeleportTransition> cir) {
    Constants.DIMENSION_ORIGIN_THREAD.set(entity.level().dimension());
  }

  /**
   * Redirects the call to {@link MinecraftServer#getLevel(ResourceKey)} inside the method {@link NetherPortalBlock#getPortalDestination(ServerLevel, Entity, BlockPos)}.<br />
   * Corrects the destination dimension if needed.
   *
   * @param instance Minecraft server.
   * @param dimension Vanilla destination dimension.
   * @param level current dimension.
   * @param entity entity travelling.
   * @param pos entrance position.
   * @return the correct dimension the entity should travel to.
   */
  @Redirect(method = "getPortalDestination", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;getLevel(Lnet/minecraft/resources/ResourceKey;)Lnet/minecraft/server/level/ServerLevel;"))
  private ServerLevel onGetPortalDestination(MinecraftServer instance, ResourceKey<Level> dimension, ServerLevel level, Entity entity, BlockPos pos) {
    if (dimension == Level.NETHER && CustomPortalChecker.isCustomPortal(level, pos)) {
      return instance.getLevel(level.dimension() == Level.OVERWORLD ? Objects.requireNonNull(CustomPortalChecker.getPortalDimension(level, pos)) : Level.OVERWORLD);
    }
    return instance.getLevel(dimension);
  }

  /**
   * Modifies the condition returned by {@link BlockState#isValidSpawn(BlockGetter, BlockPos, EntityType)} inside the method {@link NetherPortalBlock#randomTick(BlockState, ServerLevel, BlockPos, RandomSource)}.<br />
   * Prevents Zombified Piglins spawn when it's a Custom Portal.
   *
   * @param original original condition value.
   * @param state block state.
   * @param level dimension.
   * @param pos position.
   * @param rand random source.
   * @return whether spawning is allowed.
   */
  @ModifyExpressionValue(method = "randomTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;isValidSpawn(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/EntityType;)Z"))
  private boolean modifyIsValidSpawn(boolean original, BlockState state, ServerLevel level, BlockPos pos, RandomSource rand) {
    return original && !CustomPortalChecker.isCustomPortal(level, pos.above());
  }
}
