package ch.heap.bukkit.epilog;

import java.io.Console;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityBreakDoorEvent;
import org.bukkit.event.entity.EntityCombustByBlockEvent;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityCreatePortalEvent;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityPortalEnterEvent;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.entity.EntityPortalExitEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.entity.SheepDyeWoolEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.inventory.FurnaceExtractEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerChatTabCompleteEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLevelChangeEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRegisterChannelEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.event.player.PlayerUnleashEntityEvent;
import org.bukkit.event.player.PlayerUnregisterChannelEvent;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.event.world.WorldLoadEvent;

import ch.heap.bukkit.epilog.event.BarrelOpenedEvent;
import ch.heap.bukkit.epilog.event.CollectTrophyEvent;
import ch.heap.bukkit.epilog.event.CrouchGreetingEvent;
import ch.heap.bukkit.epilog.event.DoFarmEvent;
import ch.heap.bukkit.epilog.event.DuneBreakEvent;
import ch.heap.bukkit.epilog.event.OreBreakEvent;
import ch.heap.bukkit.epilog.event.SolveMansionPuzzleEvent;
import ch.heap.bukkit.epilog.event.UsingSpecialItemEvent;
import ch.heap.bukkit.epilog.event.VillagerTradeEvent;

import org.bukkit.event.server.ServerCommandEvent;

public class EventListener implements Listener {
	public Epilog epilog;

	private void handleEvent(Event event) {
		LogEvent logEvent = new LogEvent();
		logEvent.event = event;
		logEvent.time = System.currentTimeMillis();
		logEvent.experimentLabel = epilog.activeExperimentLabel;
		logEvent.needsData = true;
		epilog.postEvent(logEvent);
	}

	// Custom command listener from command blocks
	@EventHandler
	public void onServerCommandEvent(ServerCommandEvent event) {
		String command = event.getCommand();

		String[] arr = command.split(" ");

		if (arr[0].equals("epilog")) {
			MapEvent mapEvent = new MapEvent("test");

			epilog.getServer().getPluginManager().callEvent(mapEvent);
		}
	}

	// custom event listener
	@EventHandler
	public void onMapEvent(MapEvent event) {
		// Custom handle event that bypasses DataCollector due to custom event
		LogEvent logEvent = new LogEvent();
		logEvent.event = event;
		logEvent.eventName = "MapEvent";
		logEvent.data.put("Event", event.getMessage());
		logEvent.time = System.currentTimeMillis();
		logEvent.experimentLabel = epilog.activeExperimentLabel;
		logEvent.needsData = false;
		epilog.postEvent(logEvent);
	}

	// Default events ---------

	@EventHandler
	public void onWorldLoad(WorldLoadEvent event) {
		// epilog.remote.offerWorlds(event.getEventName(), event.getWorld());
		Bukkit.getServer().broadcastMessage("world loaded");
	}

	// block events
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		handleEvent(event);
	}

	@EventHandler
	public void onBlockDamage(BlockDamageEvent event) {
		handleEvent(event);
	}

	@EventHandler
	public void onBlockIgnite(BlockIgniteEvent event) {
		handleEvent(event);
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		handleEvent(event);
	}

	@EventHandler
	public void onSignChange(SignChangeEvent event) {
		handleEvent(event);
	}

	// entity events
	@EventHandler
	public void onEntityBreakDoor(EntityBreakDoorEvent event) {
		handleEvent(event);
	}

	// @EventHandler
	// public void onEntityChangeBlock(EntityChangeBlockEvent event)
	// {handleEvent(event);}
	@EventHandler
	public void onEntityCombustByBlock(EntityCombustByBlockEvent event) {
		handleEvent(event);
	}

	@EventHandler
	public void onEntityCombustByEntity(EntityCombustByEntityEvent event) {
		handleEvent(event);
	}

	@EventHandler
	public void onEntityCombust(EntityCombustEvent event) {
		handleEvent(event);
	}

	@EventHandler
	public void onEntityCreatePortal(EntityCreatePortalEvent event) {
		handleEvent(event);
	}

	@EventHandler
	public void onEntityDamageByBlock(EntityDamageByBlockEvent event) {
		handleEvent(event);
	}

	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		handleEvent(event);
	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		handleEvent(event);
	}

	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		handleEvent(event);
	}

	// @EventHandler
	// public void onEntityExplode(EntityExplodeEvent event) {handleEvent(event);}
	@EventHandler
	public void onEntityInteract(EntityInteractEvent event) {
		handleEvent(event);
	}

	@EventHandler
	public void onEntityPortalEnter(EntityPortalEnterEvent event) {
		handleEvent(event);
	}

	@EventHandler
	public void onEntityPortal(EntityPortalEvent event) {
		handleEvent(event);
	}

	@EventHandler
	public void onEntityPortalExit(EntityPortalExitEvent event) {
		handleEvent(event);
	}

	@EventHandler
	public void onEntityRegainHealth(EntityRegainHealthEvent event) {
		handleEvent(event);
	}

	@EventHandler
	public void onEntityShootBow(EntityShootBowEvent event) {
		handleEvent(event);
	}

	// @EventHandler
	// public void onEntityTame(EntityTameEvent event) {handleEvent(event);}
	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent event) {
		handleEvent(event);
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		handleEvent(event);
	}

	@EventHandler
	public void onPlayerLeashEntity(PlayerLeashEntityEvent event) {
		handleEvent(event);
	}

	@EventHandler
	public void onPotionSplash(PotionSplashEvent event) {
		handleEvent(event);
	}

	// @EventHandler
	// public void onProjectileHit(ProjectileHitEvent event) {handleEvent(event);}
	@EventHandler
	public void onProjectileLaunch(ProjectileLaunchEvent event) {
		handleEvent(event);
	}

	@EventHandler
	public void onSheepDyeWool(SheepDyeWoolEvent event) {
		handleEvent(event);
	}

	// hanging events
	@EventHandler
	public void onHangingPlace(HangingPlaceEvent event) {
		handleEvent(event);
	}

	// inventory events
	@EventHandler
	public void onFurnaceExtract(FurnaceExtractEvent event) {
		handleEvent(event);
	}

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {
		handleEvent(event);
	}

	@EventHandler
	public void onInventoryOpen(InventoryOpenEvent event) {
		handleEvent(event);
	}

	// @EventHandler
	// public void onCraftItem(CraftItemEvent event) {handleEvent(event);}; //
	// already handled by InventoryClickEvent
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		handleEvent(event);
	}

	@EventHandler
	public void onInventoryDrag(InventoryDragEvent event) {
		handleEvent(event);
	}

	// player events
	@EventHandler
	public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
		handleEvent(event);
	}

	@EventHandler
	public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
		handleEvent(event);
	}

	// @EventHandler
	// public void onPlayerAnimation(PlayerAnimationEvent event)
	// {handleEvent(event);}
	@EventHandler
	public void onPlayerBedEnter(PlayerBedEnterEvent event) {
		handleEvent(event);
	}

	@EventHandler
	public void onPlayerBedLeave(PlayerBedLeaveEvent event) {
		handleEvent(event);
	}

	@EventHandler
	public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
		handleEvent(event);
	}

	@EventHandler
	public void onPlayerBucketFill(PlayerBucketFillEvent event) {
		handleEvent(event);
	}

	@EventHandler
	public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
		handleEvent(event);
	}

	@EventHandler
	public void onPlayerChatTabComplete(PlayerChatTabCompleteEvent event) {
		handleEvent(event);
	}

	@EventHandler
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		handleEvent(event);
	}

	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		handleEvent(event);
	}

	@EventHandler
	public void onPlayerEditBook(PlayerEditBookEvent event) {
		handleEvent(event);
	}

	@EventHandler
	public void onPlayerEggThrow(PlayerEggThrowEvent event) {
		handleEvent(event);
	}

	@EventHandler
	public void onPlayerExpChange(PlayerExpChangeEvent event) {
		handleEvent(event);
	}

	@EventHandler
	public void onPlayerFish(PlayerFishEvent event) {
		handleEvent(event);
	}

	@EventHandler
	public void onPlayerGameModeChange(PlayerGameModeChangeEvent event) {
		handleEvent(event);
	}

	@EventHandler
	public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
		handleEvent(event);
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		handleEvent(event);
	}

	@EventHandler
	public void onPlayerItemBreak(PlayerItemBreakEvent event) {
		handleEvent(event);
	}

	@EventHandler
	public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
		handleEvent(event);
	}

	@EventHandler
	public void onPlayerItemHeld(PlayerItemHeldEvent event) {
		handleEvent(event);
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		handleEvent(event);
	}

	@EventHandler
	public void onPlayerKick(PlayerKickEvent event) {
		handleEvent(event);
	}

	@EventHandler
	public void onPlayerLevelChange(PlayerLevelChangeEvent event) {
		handleEvent(event);
	}

	@EventHandler
	public void onPlayerLogin(PlayerLoginEvent event) {
		handleEvent(event);
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		// handleEvent(event);
		// System.out.println("movement event");
	}

	//TODO move this to entity section
	@EventHandler
	public void onEntityPickupItem(EntityPickupItemEvent event) {
		handleEvent(event);
	}

	@EventHandler
	public void onPlayerPortal(PlayerPortalEvent event) {
		handleEvent(event);
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		handleEvent(event);
	}

	@EventHandler
	public void onPlayerRegisterChannel(PlayerRegisterChannelEvent event) {
		handleEvent(event);
	}

	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		handleEvent(event);
	}

	@EventHandler
	public void onPlayerShearEntity(PlayerShearEntityEvent event) {
		handleEvent(event);
	}

	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		handleEvent(event);
	}

	@EventHandler
	public void onPlayerToggleFlight(PlayerToggleFlightEvent event) {
		handleEvent(event);
	}

	@EventHandler
	public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
		handleEvent(event);
	}

	@EventHandler
	public void onPlayerToggleSprint(PlayerToggleSprintEvent event) {
		handleEvent(event);
	}

	@EventHandler
	public void onPlayerUnleashEntity(PlayerUnleashEntityEvent event) {
		handleEvent(event);
	}

	@EventHandler
	public void onPlayerUnregisterChannel(PlayerUnregisterChannelEvent event) {
		handleEvent(event);
	}

	@EventHandler
	public void onPlayerVelocity(PlayerVelocityEvent event) {
		handleEvent(event);
	}

	// world events
	@EventHandler
	public void onStructureGrow(StructureGrowEvent event) {
		handleEvent(event);
	}

	// custom events
	@EventHandler
	public void onCustomAction(CustomActionEvent event) {
		handleEvent(event);
	}

	@EventHandler
	public void onSpecialItemUsage(UsingSpecialItemEvent event) {
		handleEvent(event);
	}

	@EventHandler
	public void onBarrelOpened(BarrelOpenedEvent event) {
		handleEvent(event);
	}

	@EventHandler
	public void onCollectTrophy(CollectTrophyEvent event) {
		handleEvent(event);
	}

	@EventHandler
	public void onDoFarm(DoFarmEvent event) {
		handleEvent(event);
	}

	@EventHandler
	public void onDuneBreak(DuneBreakEvent event) {
		handleEvent(event);
	}

	@EventHandler
	public void onOreBreak(OreBreakEvent event) {
		handleEvent(event);
	}

	@EventHandler
	public void onSolveMansionPuzzle(SolveMansionPuzzleEvent event) {
		handleEvent(event);
	}

	@EventHandler
	public void onVillagerTrade(VillagerTradeEvent event) {
		handleEvent(event);
	}

	@EventHandler
	public void onCrouchGreeting(CrouchGreetingEvent event) {
		handleEvent(event);
	}
}
