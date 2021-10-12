package ch.heap.bukkit.epilog;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.FurnaceExtractEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerLevelChangeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.projectiles.BlockProjectileSource;
import org.bukkit.projectiles.ProjectileSource;

import ch.heap.bukkit.epilog.event.BarrelOpenedEvent;
import ch.heap.bukkit.epilog.event.CollectTrophyEvent;
import ch.heap.bukkit.epilog.event.CrouchGreetingEvent;
import ch.heap.bukkit.epilog.event.DoFarmEvent;
import ch.heap.bukkit.epilog.event.DuneBreakEvent;
import ch.heap.bukkit.epilog.event.MazeEscapeEvent;
import ch.heap.bukkit.epilog.event.OreBreakEvent;
import ch.heap.bukkit.epilog.event.SolveMansionPuzzleEvent;
import ch.heap.bukkit.epilog.event.UsingSpecialItemEvent;
import ch.heap.bukkit.epilog.event.VillagerTradeEvent;

public class DataCollector {
	Epilog epilog = null;

	public ArrayList<ItemTypeStringProvider> itemTypeStringProviders = new ArrayList<ItemTypeStringProvider>();

	public DataCollector(Epilog epilog) {
		this.epilog = epilog;
	}

	public void addData(LogEvent logEvent) {
		logEvent.needsData = false;
		// check if event is valid
		Event event = logEvent.event;
		if (event == null) {
			logEvent.ignore = true;
			return;
		}
		logEvent.eventName = event.getEventName();
		if (event instanceof Cancellable) {
			if (((Cancellable) event).isCancelled()) {
				logEvent.ignore = true;
				return;
			}
		}
		if (event instanceof EntityDamageEvent || event instanceof EntityRegainHealthEvent) {
			addDamageData(logEvent, (EntityEvent) event);
		} else if (event instanceof AsyncPlayerChatEvent) {
			AsyncPlayerChatEvent chatEvent = (AsyncPlayerChatEvent) event;
			logEvent.player = chatEvent.getPlayer();
			if (this.epilog.logChats) {
				logEvent.data.put("msg", chatEvent.getMessage());
			}
			Map<String, Object> data = logEvent.data;
			Location loc = chatEvent.getPlayer().getLocation();
			data.put("x", loc.getX());
			data.put("y", loc.getY());
			data.put("z", loc.getZ());
			data.put("zone", MazeEscapeZones.getPrimaryZone(loc.toVector()));
		} else if (event instanceof MazeEscapeEvent) {
			addMazeEscapeData(logEvent, event);
		} else {
			// add data by introspection
			addGenericData(logEvent, event);
		}
		if (logEvent.player == null) {
			logEvent.ignore = true;
			return;
		}
	}

	public void addItemTypeStringProvider(Object obj, Method method) {
		this.itemTypeStringProviders.add(new ItemTypeStringProvider(obj, method));
	}

	public String itemTypeString(ItemStack item) {
		String ans = null;
		for (ItemTypeStringProvider sp : this.itemTypeStringProviders) {
			ans = sp.stringForItem(item);
			if (ans != null)
				return ans;
		}
		ans = item.getType().toString();
		if (item.hasItemMeta() && !item.getItemMeta().getDisplayName().isEmpty()) {
			ans += ":" + item.getItemMeta().getDisplayName();
		}
		return ans;
	}

	private static void addDamageData(LogEvent logEvent, EntityEvent event) {
		Map<String, Object> data = logEvent.data;
		Entity e1 = event.getEntity();
		double health = -1;
		if (e1 instanceof LivingEntity) {
			health = ((LivingEntity) e1).getHealth()
					/ ((LivingEntity) e1).getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
			data.put("health", health);
		}

		boolean e1IsPlayer = e1 instanceof Player;
		if (e1IsPlayer) {
			logEvent.player = (Player) e1;
		}

		if (event instanceof EntityRegainHealthEvent) {
			logEvent.eventName = "PlayerRegainHealthEvent";
			EntityRegainHealthEvent ehe = (EntityRegainHealthEvent) event;
			data.put("damage", -ehe.getAmount());
			data.put("cause", ehe.getRegainReason().name());
			return;
		}

		EntityDamageEvent ede = (EntityDamageEvent) event;
		data.put("damage", ede.getDamage());

		Block block = null;

		data.put("cause", ede.getCause().name());
		if (event instanceof EntityDamageByEntityEvent) {
			EntityDamageByEntityEvent evt = (EntityDamageByEntityEvent) event;
			Entity e2 = evt.getDamager();
			Projectile projectile = null;
			if (e2 instanceof Projectile) {
				projectile = (Projectile) e2;
				data.put("projectile", projectile.getType().name());
				ProjectileSource shooter = projectile.getShooter();
				if (shooter instanceof Entity) {
					e2 = (Entity) shooter;
				} else if (shooter instanceof BlockProjectileSource) {
					block = ((BlockProjectileSource) shooter).getBlock();
				}
			}
			if (block == null) {
				boolean e2IsPlayer = e2 instanceof Player;
				if (e1IsPlayer) {
					data.put("entityID", e2.getUniqueId().toString());
					data.put("entity", e2.getType().name());
					if (e2IsPlayer) {
						logEvent.eventName = "PlayerDamageByPlayerEvent";
					} else {
						logEvent.eventName = "PlayerDamageByEntityEvent";
					}
				} else if (e2IsPlayer) {
					logEvent.eventName = "EntityDamageByPlayerEvent";
					logEvent.player = (Player) e2;
					data.put("entityID", e1.getUniqueId().toString());
					data.put("entity", e1.getType().name());
				}
			}
		} else if (event instanceof EntityDamageByBlockEvent) {
			EntityDamageByBlockEvent evt = (EntityDamageByBlockEvent) event;
			block = evt.getDamager();
		} else if (event instanceof EntityDamageEvent) {
			if (e1IsPlayer)
				logEvent.eventName = "PlayerDamageEvent";
		}
		if (block != null) {
			if (e1IsPlayer)
				logEvent.eventName = "PlayerDamageByBlockEvent";
			data.put("material", block.getType().name());
			data.put("blockX", block.getX());
			data.put("blockY", block.getY());
			data.put("blockZ", block.getZ());
		}
	}

	private void addMazeEscapeData(LogEvent logEvent, Event event) {
		Map<String, Object> data = logEvent.data;
		if (event instanceof BarrelOpenedEvent) {
			BarrelOpenedEvent typedEvent = (BarrelOpenedEvent) event;
			logEvent.player = typedEvent.getPlayer();
			Location loc = typedEvent.getLocation();
			String zone = MazeEscapeZones.getPrimaryZone(loc.toVector());
			data.put("x", loc.getX());
			data.put("y", loc.getY());
			data.put("z", loc.getZ());
			data.put("zone", zone);
		} else if (event instanceof CollectTrophyEvent) {
			CollectTrophyEvent typedEvent = (CollectTrophyEvent) event;
			logEvent.player = typedEvent.getPlayer();
			Location loc = typedEvent.getLocation();
			String zone = MazeEscapeZones.getPrimaryZone(loc.toVector());
			data.put("x", loc.getX());
			data.put("y", loc.getY());
			data.put("z", loc.getZ());
			data.put("zone", zone);
			data.put("trophy", typedEvent.getTrophyNumber());
		} else if (event instanceof DoFarmEvent) {
			DoFarmEvent typedEvent = (DoFarmEvent) event;
			logEvent.player = typedEvent.getPlayer();
			Location loc = typedEvent.getLocation();
			String zone = MazeEscapeZones.getPrimaryZone(loc.toVector());
			data.put("x", loc.getX());
			data.put("y", loc.getY());
			data.put("z", loc.getZ());
			data.put("zone", zone);
			data.put("action", typedEvent.getType().type);
		} else if (event instanceof DuneBreakEvent) {
			DuneBreakEvent typedEvent = (DuneBreakEvent) event;
			logEvent.player = typedEvent.getPlayer();
			Location loc = typedEvent.getLocation();
			String zone = MazeEscapeZones.getPrimaryZone(loc.toVector());
			data.put("x", loc.getX());
			data.put("y", loc.getY());
			data.put("z", loc.getZ());
			data.put("zone", zone);
		} else if (event instanceof OreBreakEvent) {
			OreBreakEvent typedEvent = (OreBreakEvent) event;
			logEvent.player = typedEvent.getPlayer();
			Location loc = typedEvent.getLocation();
			String zone = MazeEscapeZones.getPrimaryZone(loc.toVector());
			data.put("x", loc.getX());
			data.put("y", loc.getY());
			data.put("z", loc.getZ());
			data.put("zone", zone);
			data.put("ore", typedEvent.getMaterial().toString());
		} else if (event instanceof SolveMansionPuzzleEvent) {
			SolveMansionPuzzleEvent typedEvent = (SolveMansionPuzzleEvent) event;
			logEvent.player = typedEvent.getPlayer();
			Location loc = typedEvent.getLocation();
			String zone = MazeEscapeZones.getPrimaryZone(loc.toVector());
			data.put("x", loc.getX());
			data.put("y", loc.getY());
			data.put("z", loc.getZ());
			data.put("zone", zone);
			data.put("puzzle", typedEvent.getType().type);
		} else if (event instanceof VillagerTradeEvent) {
			VillagerTradeEvent typedEvent = (VillagerTradeEvent) event;
			logEvent.player = typedEvent.getPlayer();
			Location loc = typedEvent.getLocation();
			String zone = MazeEscapeZones.getPrimaryZone(loc.toVector());
			data.put("x", loc.getX());
			data.put("y", loc.getY());
			data.put("z", loc.getZ());
			data.put("zone", zone);
			String displayName = (typedEvent.getAcquiredItemStack().hasItemMeta() && !typedEvent.getAcquiredItemStack().getItemMeta().getDisplayName().isEmpty())
				? typedEvent.getAcquiredItemStack().getItemMeta().getDisplayName() 
				: typedEvent.getAcquiredItemStack().getType().toString()
			;
			data.put("item", displayName);
		} else if (event instanceof CrouchGreetingEvent) {
			CrouchGreetingEvent typedEvent = (CrouchGreetingEvent) event;
			logEvent.player = typedEvent.getPlayer();
			Location loc = typedEvent.getLocation();
			String zone = MazeEscapeZones.getPrimaryZone(loc.toVector());
			data.put("x", loc.getX());
			data.put("y", loc.getY());
			data.put("z", loc.getZ());
			data.put("zone", zone);
			data.put("lookingAt", typedEvent.getLookingAt().getUniqueId().toString());
		} else if (event instanceof UsingSpecialItemEvent) {
			UsingSpecialItemEvent typedEvent = (UsingSpecialItemEvent) event;
			logEvent.player = typedEvent.player;
			Location loc = typedEvent.location;
			data.put("x", loc.getX());
			data.put("y", loc.getY());
			data.put("z", loc.getZ());
			data.put("special", typedEvent.action);
			data.put("zone", MazeEscapeZones.getPrimaryZone(loc.toVector()));
		} 
	}

	private void addGenericData(LogEvent logEvent, Event event) {
		Map<String, Object> data = logEvent.data;

		Player player = null;
		Object entity = null;
		Item itemEntity = null;
		Block block = null;
		Material material = null;
		BlockFace blockFace = null;
		ItemStack itemStack = null;
		boolean doIntrospection = false;

		// figure out what kind of data we can get
		if (event instanceof PlayerToggleFlightEvent) {
			data.put("isFlying", ((PlayerToggleFlightEvent) event).isFlying());
		} else if (event instanceof PlayerToggleSprintEvent) {
			data.put("isSprinting", ((PlayerToggleSprintEvent) event).isSprinting());
		} else if (event instanceof PlayerToggleSneakEvent) {
			data.put("isSneaking", ((PlayerToggleSneakEvent) event).isSneaking());
		} else if (event instanceof PlayerItemHeldEvent) {
			data.put("newSlot", ((PlayerItemHeldEvent) event).getNewSlot());
		} else if (event instanceof PlayerExpChangeEvent) {
			PlayerExpChangeEvent pexcEvent = (PlayerExpChangeEvent) event;
			//TODO I'm not 100% sure what the expression calculates
			data.put("totalExperience", pexcEvent.getAmount() + pexcEvent.getPlayer().getTotalExperience());
		} else if (event instanceof PlayerInteractEvent) {
			blockFace = ((PlayerInteractEvent) event).getBlockFace();
			block = ((PlayerInteractEvent) event).getClickedBlock();
			data.put("action", ((PlayerInteractEvent) event).getAction().name());
		} else if (event instanceof FurnaceExtractEvent) {
			material = ((FurnaceExtractEvent) event).getItemType();
			block = ((FurnaceExtractEvent) event).getBlock();
			data.put("amount", ((FurnaceExtractEvent) event).getItemAmount());
		} else if (event instanceof PlayerLevelChangeEvent) {
			data.put("newLevel", ((PlayerLevelChangeEvent) event).getNewLevel());
		} else if (event instanceof PlayerTeleportEvent) { // is instance of PlayerMoveEvent
			data.put("cause", ((PlayerTeleportEvent) event).getCause().name());
		} else if (event instanceof FoodLevelChangeEvent) { // entity event
			data.put("foodLevel", ((FoodLevelChangeEvent) event).getFoodLevel());
		} else if (event instanceof PlayerCommandPreprocessEvent) {
			String cmd = ((PlayerCommandPreprocessEvent) event).getMessage();
			data.put("cmd", cmd.split(" ", 2)[0]);
		} else if (event instanceof PlayerItemConsumeEvent) { 
			PlayerItemConsumeEvent typedEvent = (PlayerItemConsumeEvent) event;
			player = typedEvent.getPlayer();
			itemStack = typedEvent.getItem();
		} else if (event instanceof CraftItemEvent) {
			itemStack = ((CraftItemEvent) event).getRecipe().getResult();
			entity = ((CraftItemEvent) event).getWhoClicked();
			doIntrospection = true;
		} else if (event instanceof ProjectileLaunchEvent) {
			Projectile projectile = ((ProjectileLaunchEvent) event).getEntity();
			data.put("projectileType", projectile.getType().name());
			ProjectileSource shooter = projectile.getShooter();
			if (shooter instanceof Entity)
				entity = shooter;
			doIntrospection = true;
		} else if (event instanceof InventoryDragEvent) {
			InventoryDragEvent typedEvent = (InventoryDragEvent) event;
			entity = typedEvent.getWhoClicked();
			itemStack = typedEvent.getCursor();
		} else if (event instanceof InventoryClickEvent) {
			InventoryClickEvent typedEvent = (InventoryClickEvent) event;
			entity = typedEvent.getWhoClicked();
			data.put("slot", typedEvent.getSlot());
			data.put("slotType", typedEvent.getSlotType().toString());
			data.put("currentItem", typedEvent.getCurrentItem() != null ? this.itemTypeString(typedEvent.getCurrentItem()) : null);
			data.put("currentAmount", typedEvent.getCurrentItem() != null ? typedEvent.getCurrentItem().getAmount() : -1);
			data.put("cursorItem", typedEvent.getCursor() != null ? this.itemTypeString(typedEvent.getCursor()) : null);
			data.put("cursorAmount", typedEvent.getCursor() != null ? typedEvent.getCursor().getAmount() : -1);
		} else if (event instanceof PlayerDropItemEvent) { 
			PlayerDropItemEvent typedEvent = (PlayerDropItemEvent) event;
			player = (Player) typedEvent.getPlayer();
			itemEntity = typedEvent.getItemDrop();
			itemStack = typedEvent.getItemDrop().getItemStack();
			Location loc = itemEntity.getLocation();
			data.put("x", loc.getX());
			data.put("y", loc.getY());
			data.put("z", loc.getZ());
			data.put("zone", MazeEscapeZones.getPrimaryZone(loc.toVector()));
		} else if (event instanceof EntityPickupItemEvent) { 
			EntityPickupItemEvent typedEvent = (EntityPickupItemEvent) event;
			if (typedEvent.getEntity().getType() == EntityType.PLAYER) {
				logEvent.eventName = "PlayerPickupItemEvent";
				player = (Player) typedEvent.getEntity();
				itemEntity = typedEvent.getItem();
				itemStack = typedEvent.getItem().getItemStack();
				Location loc = itemEntity.getLocation();
				UUID droppedBy = epilog.exchangeItemListener.itemDroppedByMap.get(typedEvent.getItem().getUniqueId());
				data.put("droppedBy", droppedBy != null ? droppedBy.toString() : null);
				data.put("x", loc.getX());
				data.put("y", loc.getY());
				data.put("z", loc.getZ());
				data.put("zone", MazeEscapeZones.getPrimaryZone(loc.toVector()));
			}
		} else if (event instanceof BlockBreakEvent) {
			BlockBreakEvent typedEvent = (BlockBreakEvent) event;
			player = typedEvent.getPlayer();
			block = typedEvent.getBlock();
			itemStack = typedEvent.getPlayer().getInventory().getItemInMainHand();
		} else if (event instanceof BlockPlaceEvent) { 
			BlockPlaceEvent typedEvent = (BlockPlaceEvent) event;
			player = typedEvent.getPlayer();
			block = typedEvent.getBlockPlaced();
			itemStack = typedEvent.getItemInHand();
		} else if (event instanceof CustomActionEvent) { 
			CustomActionEvent typedEvent = (CustomActionEvent) event;
			player = typedEvent.getPlayer();
			Location loc = player.getLocation();
			data.put("action", typedEvent.getAction());
			data.put("x", loc.getX());
			data.put("y", loc.getY());
			data.put("z", loc.getZ());
			data.put("zone", MazeEscapeZones.getPrimaryZone(loc.toVector()));
		} else {
			doIntrospection = true;
			for (Method method : event.getClass().getMethods()) {
				String methodName = method.getName();
				try {
					if (methodName.equals("getBlock")) {
						block = (Block) method.invoke(event);
					} else if (methodName.equals("getMaterial")) {
						material = (Material) method.invoke(event);
					} else if (methodName.equals("getBlockFace")) {
						blockFace = (BlockFace) method.invoke(event);
					} else if (methodName.equals("getItem") || methodName.equals("getItemStack")
							|| methodName.equals("getItemDrop")) {
						Object item = method.invoke(event);
						if (item instanceof Item) {
							itemStack = ((Item) item).getItemStack();
							itemEntity = (Item) item;
						}
						else
							itemStack = (ItemStack) item;
					} else if (methodName.equals("getPlayer")) {
						player = (Player) method.invoke(event);
					} else if (methodName.equals("getEntity")) {
						entity = method.invoke(event);
					}
				} catch (Exception e) {
					this.epilog.getLogger().warning("unable to get generic data for event: " + event.getEventName());
					e.printStackTrace();
				}
			}
		}

		if (doIntrospection == false) {
			try {
				entity = (Player) event.getClass().getMethod("getPlayer", (Class<?>[]) null).invoke(event);
			} catch (Exception e) {
			}
			try {
				entity = event.getClass().getMethod("getEntity", (Class<?>[]) null).invoke(event);
			} catch (Exception e) {
			}
		}

		// fill out generic data fields
		if (block != null) {
			data.put("blockX", block.getX());
			data.put("blockY", block.getY());
			data.put("blockZ", block.getZ());
			if (material == null) {
				material = block.getType();
			}
		}
		if (logEvent.material != null) {
			material = logEvent.material;
		}
		if (material != null) {
			data.put("material", material.name());
		}
		if (blockFace != null) {
			data.put("blockFace", blockFace.name());
		}
		if (itemStack != null) {
			data.put("item", this.itemTypeString(itemStack));
			data.put("amount", itemStack.getAmount());
		}
		if (itemEntity != null) {
			data.put("itemID", itemEntity.getEntityId());
			data.put("tags", new ArrayList<>(itemEntity.getScoreboardTags()));
		}
		if (player != null) {
			logEvent.player = player;
		} else if (entity instanceof Player) {
			logEvent.player = (Player) entity;
		}
		// if (material!=null) {
		// System.out.println(event.getEventName() + ": " + material.name());
		// }
	}

	public List<String> getOnlinePlayers() {
		return playerArray(this.epilog.getServer().getOnlinePlayers());
	}

	// collect server data
	public void addServerMetaData(LogEvent logEvent) {
		Server server = this.epilog.getServer();
		Map<String, Object> data = new HashMap<>();

		// get loaded plugins
		List<Map<String, Object>> plugins = new ArrayList<>();
		for (Plugin plugin : server.getPluginManager().getPlugins()) {
			plugins.add(getPluginMetaData(plugin));
		}
		data.put("plugins", plugins);

		// TODO fix comments
		// collect some more possibly usefull properties
		data.put("name", server.getName()); // String
		data.put("version", server.getVersion()); // String
		data.put("bukkitVersion", server.getBukkitVersion()); // String
		data.put("maxPlayers", server.getMaxPlayers()); // int
		data.put("port", server.getPort()); // int
		data.put("viewDistance", server.getViewDistance()); // int
		data.put("ip", server.getIp()); // String
		data.put("serverName", server.getName()); // String
		// TODO this method was removed in (I think?) Bukkit 1.14
		// data.put("serverId", server.getId()); // String
		data.put("worldType", server.getWorldType()); // String
		data.put("generateStructures", server.getGenerateStructures() ? 1 : 0); // boolean
		data.put("allowEnd", server.getAllowEnd() ? 1 : 0); // boolean
		data.put("allowNether", server.getAllowNether() ? 1 : 0); // boolean
		data.put("whitelistedPlayers", playerArray(server.getWhitelistedPlayers())); // Set<OfflinePlayer>
		data.put("connectionThrottle", server.getConnectionThrottle()); // long
		data.put("ticksPerAnimalSpawns", server.getTicksPerAnimalSpawns()); // int
		data.put("ticksPerMonsterSpawns", server.getTicksPerMonsterSpawns()); // int
		data.put("spawnRadius", server.getSpawnRadius()); // int
		data.put("onlineMode", server.getOnlineMode() ? 1 : 0); // boolean
		data.put("allowFlight", server.getAllowFlight() ? 1 : 0); // boolean

		data.put("ipBans", asList(server.getIPBans())); // Set<String>
		data.put("bannedPlayers", playerArray(server.getBannedPlayers())); // Set<OfflinePlayer>
		data.put("operators", playerArray(server.getOperators())); // Set<OfflinePlayer>

		logEvent.data.put("serverMeta", data);
	}

	private static Map<String, Object> getPluginMetaData(Plugin plugin) {
		Map<String, Object> data = new HashMap<>();
		PluginDescriptionFile desc = plugin.getDescription();
		data.put("name", desc.getName());
		data.put("version", desc.getVersion());
		// TODO why do we need this ???
		// if (desc.getCommands()!=null) {
		// data.put("commands", desc.getCommands().keySet().toArray());
		// }
		return data;
	}

	public Map<String, Object> getWorlds(World only) {
		Server server = this.epilog.getServer();
		Map<String, Object> worlds = new HashMap<>();
		for (World world : server.getWorlds()) {
			if (only != null && !world.equals(only))
				continue;
			worlds.put(world.getUID().toString(), getWorldMetaData(world));
		}
		return worlds;
	}

	private static Map<String, Object> getWorldMetaData(World world) {
		Map<String, Object> data = new HashMap<>();

		// collect some more possibly usefull properties
		// data.put("entities", world.getEntities()); // List<Entity>
		// data.put("livingEntities", world.getLivingEntities()); // List<LivingEntity>
		// data.put("players", world.getPlayers()); // List<Player>
		data.put("name", world.getName()); // String
		data.put("uuid", world.getUID().toString()); // UUID
		data.put("time", world.getTime()); // long
		data.put("fullTime", world.getFullTime()); // long
		// data.put("weatherDuration", world.getWeatherDuration()); // int
		// data.put("thunderDuration", world.getThunderDuration()); // int
		data.put("environment", world.getEnvironment().name()); // Environment
		data.put("seed", world.getSeed()); // long
		data.put("pvp", world.getPVP() ? 1 : 0); // boolean
		// data.put("generator", world.getGenerator()); // ChunkGenerator
		// data.put("populators", world.getPopulators()); // List<BlockPopulator>
		data.put("allowAnimals", world.getAllowAnimals() ? 1 : 0); // boolean
		data.put("allowMonsters", world.getAllowMonsters() ? 1 : 0); // boolean
		data.put("maxHeight", world.getMaxHeight()); // int
		data.put("seaLevel", world.getSeaLevel()); // int
		data.put("keepSpawnInMemory", world.getKeepSpawnInMemory() ? 1 : 0); // boolean
		data.put("difficulty", world.getDifficulty().name()); // Difficulty
		// data.put("worldFolder", world.getWorldFolder()); // File
		data.put("worldType", world.getWorldType().name()); // WorldType
		data.put("ticksPerAnimalSpawns", world.getTicksPerAnimalSpawns()); // long
		data.put("ticksPerMonsterSpawns", world.getTicksPerMonsterSpawns()); // long

		data.put("uuid", world.getUID().toString()); // string
		Map<String, Object> spawnLocation = new HashMap<>();
		Location loc = world.getSpawnLocation();
		spawnLocation.put("x", loc.getX());
		spawnLocation.put("y", loc.getY());
		spawnLocation.put("z", loc.getZ());
		spawnLocation.put("pitch", loc.getPitch());
		spawnLocation.put("yaw", loc.getYaw());
		data.put("spawnLocation", spawnLocation);

		return data;
	}

	private static <T> List<T> asList(Collection<T> set) {
		List<T> result = new ArrayList<>();
		result.addAll(set);
		return result;
	}

	private static List<String> playerArray(Collection<? extends OfflinePlayer> playerSet) {
		List<String> result = new ArrayList<>();
		for (OfflinePlayer p : playerSet) {
			result.add(p.getUniqueId().toString());
		}
		return result;
	}

	private class ItemTypeStringProvider {
		private Object obj;
		private Method method;

		public ItemTypeStringProvider(Object obj, Method method) {
			this.obj = obj;
			this.method = method;
		}

		public String stringForItem(ItemStack item) {
			String ans = null;
			try {
				ans = (String) this.method.invoke(obj, item);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return ans;
		}
	}
}
