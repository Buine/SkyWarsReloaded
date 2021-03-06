package com.walrusone.skywars.listeners;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Egg;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import com.walrusone.skywars.SkyWarsReloaded;
import com.walrusone.skywars.game.Game;
import com.walrusone.skywars.game.GamePlayer;
import com.walrusone.skywars.game.Game.GameState;
import com.walrusone.skywars.menus.JoinMenu;
import com.walrusone.skywars.menus.KitMenu;
import com.walrusone.skywars.menus.LobbyMainMenu;
import com.walrusone.skywars.menus.MainMenu;
import com.walrusone.skywars.menus.SpecGameMenu;
import com.walrusone.skywars.utilities.BungeeUtil;
import com.walrusone.skywars.utilities.ItemUtils;
import com.walrusone.skywars.utilities.Messaging;
import com.walrusone.skywars.utilities.ParticleEffect;

public class PlayerListener implements Listener {
	
	private ItemStack spec;
	private ItemStack join;
	private ItemStack exit;
	private ItemStack kit;
	private ItemStack options;
	private ItemStack lobbyMenu;
	
	public PlayerListener() {
				
		String specItem = SkyWarsReloaded.get().getConfig().getString("gameItems.spectateItem");
		List<String> specItemData = new LinkedList<String>(Arrays.asList(specItem.split(" ")));
		String name = "name:" + new Messaging.MessageFormatter().format("menu.spectategame-item-name");
		specItemData.add(name);
		spec = ItemUtils.parseItem(specItemData);
		
		String joinItem = SkyWarsReloaded.get().getConfig().getString("gameItems.joinItem");
		List<String> joinItemData = new LinkedList<String>(Arrays.asList(joinItem.split(" ")));
		String joinName = "name:" + new Messaging.MessageFormatter().format("menu.join-item-name");
		joinItemData.add(joinName);
		join = ItemUtils.parseItem(joinItemData);
		
		String kitItem = SkyWarsReloaded.get().getConfig().getString("gameItems.kitMenuItem");
		List<String> kitItemData = new LinkedList<String>(Arrays.asList(kitItem.split(" ")));
		kit = ItemUtils.parseItem(kitItemData);
		
		String exitItem = SkyWarsReloaded.get().getConfig().getString("gameItems.exitGameItem");
		List<String> exitItemData = new LinkedList<String>(Arrays.asList(exitItem.split(" ")));
		exit = ItemUtils.parseItem(exitItemData);
		
		String optionsItem = SkyWarsReloaded.get().getConfig().getString("gameItems.optionsItem");
		List<String> optionsItemData = new LinkedList<String>(Arrays.asList(optionsItem.split(" ")));
		String optionsName = "name:" + new Messaging.MessageFormatter().format("menu.options-item-name");
		optionsItemData.add(optionsName);
		options = ItemUtils.parseItem(optionsItemData);
		
		String lobbyMenuItem = SkyWarsReloaded.get().getConfig().getString("gameItems.lobbyMenuItem");
		List<String> lobbyMenuItemData = new LinkedList<String>(Arrays.asList(lobbyMenuItem.split(" ")));
		String lobbyMenuName = "name:" + new Messaging.MessageFormatter().format("menu.lobbymenu-item-name");
		lobbyMenuItemData.add(lobbyMenuName);
		lobbyMenu = ItemUtils.parseItem(lobbyMenuItemData);
	}
	
	@EventHandler
	public void onTeleportEvent(PlayerTeleportEvent e) {
		final Player player = e.getPlayer();
		String world = SkyWarsReloaded.get().getConfig().getString("spawn.world");
		String fromWorld = e.getFrom().getWorld().getName();
		String toWorld = e.getTo().getWorld().getName();
		if (!toWorld.equalsIgnoreCase(fromWorld) && world.equalsIgnoreCase(toWorld)) {
			SkyWarsReloaded.get().getServer().getScheduler().scheduleSyncDelayedTask(SkyWarsReloaded.get(), new Runnable() {
				public void run() {
					if (player != null) {
						if (SkyWarsReloaded.get().getConfig().getBoolean("gameItems.giveSpectateItem")) {
							if (SkyWarsReloaded.perms.has(player, "swr.spectate")) {
								player.getInventory().setItem(SkyWarsReloaded.get().getConfig().getInt("gameItems.spectateItemSlot"), spec);
							}
						}
						if (SkyWarsReloaded.get().getConfig().getBoolean("gameItems.giveJoinItem")) {
								player.getInventory().setItem(SkyWarsReloaded.get().getConfig().getInt("gameItems.joinItemSlot"), join);
						}
						if (SkyWarsReloaded.get().getConfig().getBoolean("gameItems.giveLobbyMenuItem")) {
							player.getInventory().setItem(SkyWarsReloaded.get().getConfig().getInt("gameItems.lobbyMenuItemSlot"), lobbyMenu);
						}
						SkyWarsReloaded.getScore().getScoreboard(player);
					}
				}
			}, 3);
		} else if (!toWorld.equalsIgnoreCase(fromWorld) && !world.equalsIgnoreCase(toWorld)) {
			SkyWarsReloaded.get().getServer().getScheduler().scheduleSyncDelayedTask(SkyWarsReloaded.get(), new Runnable() {
				public void run() {
					player.getInventory().remove(spec);
					player.getInventory().remove(join);
					player.getInventory().remove(lobbyMenu);
					if (!SkyWarsReloaded.getPC().getPlayer(player.getUniqueId()).isSpectating()) {
						player.setScoreboard(SkyWarsReloaded.get().getServer().getScoreboardManager().getNewScoreboard());
					}
				}
			}, 3);
		}
	}
	
	@EventHandler
	public void onFoodChange(FoodLevelChangeEvent e) {
		 Entity ent = e.getEntity();
		if(ent instanceof Player) {
			 GamePlayer hungry = SkyWarsReloaded.getPC().getPlayer(((Player) ent).getUniqueId());
			 if (hungry.inGame()) {
				 Game game = hungry.getGame();
				 if (game != null) {
					 if (game.getState() == GameState.PREGAME) {
						 e.setCancelled(true);
					 }
				 }
			 }
		}
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageByEntityEvent e) {
		 Entity ent = e.getEntity();
		 if(ent instanceof Player) {
			 GamePlayer target = SkyWarsReloaded.getPC().getPlayer(((Player) ent).getUniqueId());
			 if (target.inGame()) {
				 Entity damager = e.getDamager();
				 if (e.getCause().equals(DamageCause.PROJECTILE)) {
					 if (damager instanceof Snowball) {
						 Snowball snowball = (Snowball) damager;
						 if(snowball.getShooter() instanceof Player) {
							 GamePlayer killer = SkyWarsReloaded.getPC().getPlayer(((Player) snowball.getShooter()).getUniqueId());
								 target.setTagged(killer);
								 return;
							 }
					 	} else if (damager instanceof Egg) {
							 Egg egg = (Egg) damager;
							 if(egg.getShooter() instanceof Player) {
								 GamePlayer killer = SkyWarsReloaded.getPC().getPlayer(((Player) egg.getShooter()).getUniqueId());
									 target.setTagged(killer);
									 return;
								 }
					 	} else if (damager instanceof Arrow) {
							 Arrow arrow = (Arrow) damager;
							 if(arrow.getShooter() instanceof Player) {
								 GamePlayer killer = SkyWarsReloaded.getPC().getPlayer(((Player) arrow.getShooter()).getUniqueId());
									 target.setTagged(killer);
									 return;
							 }
					 	} else if (damager instanceof EnderPearl) {
						 EnderPearl ePearl = (EnderPearl) damager;
						 if(ePearl.getShooter() instanceof Player) {
							 GamePlayer killer = SkyWarsReloaded.getPC().getPlayer(((Player) ePearl.getShooter()).getUniqueId());
								 target.setTagged(killer);
								 return;
							 }
						 } else if (damager instanceof ThrownPotion) {
							 ThrownPotion potion = (ThrownPotion) damager;
							 if(potion.getShooter() instanceof Player) {
								 GamePlayer killer = SkyWarsReloaded.getPC().getPlayer(((Player) potion.getShooter()).getUniqueId());
									 target.setTagged(killer);
									 return;
								 }
						 }
				 } else if (damager instanceof Player) {
					 GamePlayer killer = SkyWarsReloaded.getPC().getPlayer(((Player) damager).getUniqueId());
						 target.setTagged(killer);
						 return;
					}
			 }
		 }
	}
	
	@EventHandler (priority = EventPriority.HIGH)
	public void onPlayerJoin(PlayerJoinEvent e) {
		SkyWarsReloaded.getPC().addPlayer(e.getPlayer().getUniqueId());
		if (SkyWarsReloaded.getInvC().playerExists(e.getPlayer().getUniqueId().toString())) {
			SkyWarsReloaded.getInvC().restoreInventory(e.getPlayer());
			String world = SkyWarsReloaded.get().getConfig().getString("spawn.world");
			int x = SkyWarsReloaded.get().getConfig().getInt("spawn.x");
			int y = SkyWarsReloaded.get().getConfig().getInt("spawn.y");
			int z = SkyWarsReloaded.get().getConfig().getInt("spawn.z");
			float yaw = SkyWarsReloaded.get().getConfig().getInt("spawn.yaw");
			float pitch = SkyWarsReloaded.get().getConfig().getInt("spawn.pitch");
			Location spawn = new Location(SkyWarsReloaded.get().getServer().getWorld(world), x, y, z, yaw, pitch);
			e.getPlayer().teleport(spawn);
			final Player player = e.getPlayer();
			SkyWarsReloaded.get().getServer().getScheduler().scheduleSyncDelayedTask(SkyWarsReloaded.get(), new Runnable() {
				public void run() {
					for (PotionEffect effect : player.getActivePotionEffects()) {
				        player.removePotionEffect(effect.getType());
					}
					player.setFireTicks(0);
					
					player.setAllowFlight(false);
					player.setFlying(false);
				}
			}, 5L);
		}
		if (SkyWarsReloaded.get().getConfig().getBoolean("gameVariables.clearInventoryOnJoin")) {
			e.getPlayer().getInventory().clear();
		}
		if (SkyWarsReloaded.get().getConfig().getBoolean("gameVariables.clearXPOnJoin")) {
			e.getPlayer().setExp(0);
		}
		if (SkyWarsReloaded.get().getConfig().getBoolean("gameVariables.clearPotionEffectsOnJoin")) {
			for (PotionEffect effect : e.getPlayer().getActivePotionEffects()) {
		        e.getPlayer().removePotionEffect(effect.getType());
			}
		}
		final Player player = e.getPlayer();
		if (SkyWarsReloaded.get().getConfig().getBoolean("bungeeMode.enabled")) {
			SkyWarsReloaded.get().getServer().getScheduler().scheduleSyncDelayedTask(SkyWarsReloaded.get(), new Runnable() {
				public void run() {
					Game game = SkyWarsReloaded.getGC().getGame(1);
					if (!game.isFull() && game.getState() == GameState.PREGAME) {
						game.addPlayer(SkyWarsReloaded.getPC().getPlayer(player.getUniqueId()));
					} else {
						BungeeUtil.connectToServer(player, SkyWarsReloaded.get().getConfig().getString("bungeeMode.lobbyServer"));
					}
				}
			}, 5L);
		} else  {
			String world = SkyWarsReloaded.get().getConfig().getString("spawn.world");
			if (e.getPlayer().getWorld().getName().equalsIgnoreCase(world)) {
				if (SkyWarsReloaded.get().getConfig().getBoolean("gameItems.giveSpectateItem")) {
					if (SkyWarsReloaded.perms.has(e.getPlayer(), "swr.spectate")) {
						e.getPlayer().getInventory().setItem(SkyWarsReloaded.get().getConfig().getInt("gameItems.spectateItemSlot"), spec);
					}
				}
				if (SkyWarsReloaded.get().getConfig().getBoolean("gameItems.giveJoinItem")) {
						e.getPlayer().getInventory().setItem(SkyWarsReloaded.get().getConfig().getInt("gameItems.joinItemSlot"), join);
				}
				if (SkyWarsReloaded.get().getConfig().getBoolean("gameItems.giveLobbyMenuItem")) {
					e.getPlayer().getInventory().setItem(SkyWarsReloaded.get().getConfig().getInt("gameItems.lobbyMenuItemSlot"), lobbyMenu);
				}
			} else {
				e.getPlayer().getInventory().remove(spec);
				e.getPlayer().getInventory().remove(join);
				e.getPlayer().getInventory().remove(lobbyMenu);
			}
		} 
	}
	
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent e) {
		final GamePlayer gPlayer = SkyWarsReloaded.getPC().getPlayer(e.getPlayer().getUniqueId());
		if (gPlayer.isSpectating()) {
			SkyWarsReloaded.get().getServer().getScheduler().scheduleSyncDelayedTask(SkyWarsReloaded.get(), new Runnable() {
		        public void run() {
		        	if (gPlayer.getP() != null) {
		        		if (gPlayer.getSpecGame() != null) {
							gPlayer.spectateMode(true, gPlayer.getSpecGame(), gPlayer.getSpecGame().getSpawn(), false);
							gPlayer.getP().sendMessage(new Messaging.MessageFormatter().withPrefix().format("game.spectating"));
		        		}
		        	}
		        }
		      }, 3);
		} else {
			String world = SkyWarsReloaded.get().getConfig().getString("spawn.world");
			if (e.getPlayer().getWorld().getName().equalsIgnoreCase(world)) {
				if (SkyWarsReloaded.get().getConfig().getBoolean("gameItems.giveSpectateItem")) {
					if (SkyWarsReloaded.perms.has(e.getPlayer(), "swr.spectate")) {
						e.getPlayer().getInventory().setItem(SkyWarsReloaded.get().getConfig().getInt("gameItems.spectateItemSlot"), spec);
					}
				}
				if (SkyWarsReloaded.get().getConfig().getBoolean("gameItems.giveJoinItem")) {
						e.getPlayer().getInventory().setItem(SkyWarsReloaded.get().getConfig().getInt("gameItems.joinItemSlot"), join);
				}
				if (SkyWarsReloaded.get().getConfig().getBoolean("gameItems.giveLobbyMenuItem")) {
					e.getPlayer().getInventory().setItem(SkyWarsReloaded.get().getConfig().getInt("gameItems.lobbyMenuItemSlot"), lobbyMenu);
				}
			} else {
				e.getPlayer().getInventory().remove(spec);
				e.getPlayer().getInventory().remove(join);
				e.getPlayer().getInventory().remove(lobbyMenu);
			}
		}
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		GamePlayer gPlayer = SkyWarsReloaded.getPC().getPlayer(e.getPlayer().getUniqueId());
		if (gPlayer.inGame()) {
			Game game = gPlayer.getGame();
			gPlayer.getGame().deletePlayer(gPlayer, true, true);
			if (game.getState() == GameState.PLAYING) {
				game.checkForWinner();
			}
		}
		if (gPlayer.isSpectating()) {
			Game game = gPlayer.getSpecGame();
			game.removeSpectator(gPlayer);
		}
		SkyWarsReloaded.getPC().removePlayer(e.getPlayer().getUniqueId());
	}
	
	@EventHandler (priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onCommandInGame(PlayerCommandPreprocessEvent e) {
		GamePlayer gPlayer = SkyWarsReloaded.getPC().getPlayer(e.getPlayer().getUniqueId());
		if (gPlayer.inGame()) {
			String message = e.getMessage();
			boolean cAllow = false;
			List<String> whitelist = SkyWarsReloaded.get().getConfig().getStringList("commandWhitelist");
			for (String comm: whitelist) {
				if (comm.length() <= message.length()) {
					String command = message.substring(0, comm.length());
					if (command.equalsIgnoreCase(comm)) {
						cAllow = true;
					}
				}
			}
			if (cAllow) {
				return;
			} else {
				e.setCancelled(true);
				gPlayer.getP().sendMessage(new Messaging.MessageFormatter().format("error.no-perm-in-game"));
			}
		} else if (gPlayer.isSpectating()) {
			String message = e.getMessage();
			boolean cAllow = false;
			List<String> whitelist = SkyWarsReloaded.get().getConfig().getStringList("spectatorWhitelist");
			for (String comm: whitelist) {
				if (comm.length() <= message.length()) {
					String command = message.substring(0, comm.length());
					if (command.equalsIgnoreCase(comm)) {
						cAllow = true;
					}
				}
			}
			if (cAllow) {
				return;
			} else {
				e.setCancelled(true);
				gPlayer.getP().sendMessage(new Messaging.MessageFormatter().format("error.no-perm-spectating"));
			}
		}
	}
	
	@EventHandler 
    public void onPlayerInteract(PlayerInteractEvent e) {
		ItemStack item = e.getPlayer().getItemInHand();
        Player player = e.getPlayer();
        GamePlayer gPlayer = SkyWarsReloaded.getPC().getPlayer(player.getUniqueId());
             
        if (gPlayer.inGame()) {
            if ((e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) && (item.getData().getItemType().equals(kit.getData().getItemType()) &&  item.getEnchantments().keySet().equals(kit.getEnchantments().keySet()))) {
        		if (gPlayer.getGame().getState() == GameState.PREGAME) {
        			if (!gPlayer.hasKitSelected()) {
        				new KitMenu(gPlayer);
        				e.setCancelled(true);
        			} else {
        				player.sendMessage(new Messaging.MessageFormatter().format("error.already-has-kit"));
        			}
        		}
            } else if ((e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) && (item.getData().getItemType().equals(exit.getData().getItemType()) &&  item.getEnchantments().keySet().equals(exit.getEnchantments().keySet()))) {			
            	if (gPlayer.getGame().getState() == GameState.PREGAME) {	
            		e.setCancelled(true);
					gPlayer.getGame().deletePlayer(gPlayer, true, false);
            	}
            } else if ((e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) && (item.getData().getItemType().equals(options.getData().getItemType()) &&  item.getEnchantments().keySet().equals(options.getEnchantments().keySet()))) {
				if (gPlayer.getGame().getState() == GameState.PREGAME) {
					e.setCancelled(true);
					new MainMenu(gPlayer);
				}
            } else {
            	if (gPlayer.getGame().getState() == GameState.PREGAME) {
            		e.setCancelled(true);
            	}
            }
        }

        if (!gPlayer.inGame()) {
        	if (e.getAction() == Action.PHYSICAL && e.getClickedBlock().getType() == Material.STONE_PLATE) {
        		boolean pressure = SkyWarsReloaded.get().getConfig().getBoolean("pressurePlateJoin");
        		if (pressure) {
        			String spawnWorld = SkyWarsReloaded.get().getConfig().getString("spawn.world");
           			if (spawnWorld != null) {
           				boolean signJoinMode = SkyWarsReloaded.get().getConfig().getBoolean("signJoinMode");
           		  		if (!signJoinMode) {
           	            	String w = SkyWarsReloaded.get().getConfig().getString("spawn.world");
           	            	if (w != null) {
           	                	World world = SkyWarsReloaded.get().getServer().getWorld(w); 
           	                    if (!gPlayer.inGame() && player.getLocation().getWorld().equals(world)) {
           	                        Game game = SkyWarsReloaded.getGC().findGame();
           	                        if (game != null) {
           	                            game.addPlayer(gPlayer);
           	                        } else {
           	                        	SkyWarsReloaded.getGC().addToQueue(gPlayer);
           	                        	gPlayer.getP().sendMessage(new Messaging.MessageFormatter().format("game.no-game-available"));
           	                        }
           	                    }
           	            	} 
           	    		} else {
           	    			String w = SkyWarsReloaded.get().getConfig().getString("spawn.world");
           	            	if (w != null) {
           	                	World world = SkyWarsReloaded.get().getServer().getWorld(w); 
           	                    if (!gPlayer.inGame() && player.getLocation().getWorld().equals(world)) {
           	                		Game game = findGame();
           	        	    		int i = 0;
           	        	    		while (i < 3) {
           	                    		if (game != null && game.getState() == GameState.PREGAME && !game.isFull()) {
           	            	                game.addPlayer(gPlayer);
           	            	                break;
           	            	    		} else {
           	            	    			i++;
           	            	    			game = findGame();
           	            	    		}
           	        	    		}
           	                    }
           	            	}
           	        	}
           			} else {
           				e.getPlayer().sendMessage(ChatColor.RED + "YOU MUST SET SPAWN IN THE LOBBY WORLD WITH /SWR SETSPAWN BEFORE STARTING A GAME");
           				SkyWarsReloaded.get().getLogger().info("YOU MUST SET SPAWN IN THE LOBBY WORLD WITH /SWR SETSPAWN BEFORE STARTING A GAME");
           			}
             	} 
        	}
                   	
            if ((e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) && (item.getData().getItemType().equals(spec.getData().getItemType()) &&  item.getEnchantments().keySet().equals(spec.getEnchantments().keySet()))) {
         		boolean specEnabled = SkyWarsReloaded.get().getConfig().getBoolean("gameVariables.allowSpectating");
         		if (specEnabled) {
    					if (!gPlayer.isSpectating()) {
    						e.setCancelled(true);
    						new SpecGameMenu(gPlayer);
    					} 
    			}
         	} else if ((e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) && (item.getData().getItemType().equals(join.getData().getItemType()) &&  item.getEnchantments().keySet().equals(join.getEnchantments().keySet()))) {
    			if (!gPlayer.isSpectating()) {
    					e.setCancelled(true);
    					new JoinMenu(gPlayer);
    			}
         	} else if ((e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) && (item.getData().getItemType().equals(lobbyMenu.getData().getItemType()) &&  item.getEnchantments().keySet().equals(lobbyMenu.getEnchantments().keySet()))) {
    				e.setCancelled(true);
    				new LobbyMainMenu(gPlayer);
         	}
        }
        

    }
        
    private Game findGame() {
		Game game = null;
		int highest = 0;
		for (Game g: SkyWarsReloaded.getGC().getGames()) {
			if (highest <= g.getPlayers().size() && g.getState() == GameState.PREGAME && !g.isFull()) {
				highest = g.getPlayers().size();
				game = g;
			}
		}
		return game;
    }        
	
	@EventHandler
	public void onDropItem(PlayerDropItemEvent e) {
		Player p = e.getPlayer();
		GamePlayer gPlayer = SkyWarsReloaded.getPC().getPlayer(p.getUniqueId());
		ItemStack item = e.getItemDrop().getItemStack();
 	    if ((item.getData().getItemType().equals(kit.getData().getItemType()) &&  item.getEnchantments().keySet().equals(kit.getEnchantments().keySet()))) {
			e.setCancelled(true);
     	} else if ((item.getData().getItemType().equals(exit.getData().getItemType()) &&  item.getEnchantments().keySet().equals(exit.getEnchantments().keySet()))) {
			e.setCancelled(true);
     	} else if ((item.getData().getItemType().equals(spec.getData().getItemType()) &&  item.getEnchantments().keySet().equals(spec.getEnchantments().keySet()))) {
			e.setCancelled(true);
     	} else if ((item.getData().getItemType().equals(join.getData().getItemType()) &&  item.getEnchantments().keySet().equals(join.getEnchantments().keySet()))) {
			e.setCancelled(true);
		} else if ((item.getData().getItemType().equals(options.getData().getItemType()) &&  item.getEnchantments().keySet().equals(options.getEnchantments().keySet()))) {
			e.setCancelled(true);
		} else if ((item.getData().getItemType().equals(lobbyMenu.getData().getItemType()) &&  item.getEnchantments().keySet().equals(lobbyMenu.getEnchantments().keySet()))) {
			e.setCancelled(true);
		}
		if (gPlayer.inGame()) {
			Game game = gPlayer.getGame();
			GameState gs = game.getState();
			if (gs == GameState.PREGAME) {
				e.setCancelled(true);
			}
		}
		
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = false)
	public void onInventoryClick(InventoryClickEvent e) {
		ItemStack item = e.getCurrentItem();
		if (item != null) {
     	    if ((item.getData().getItemType().equals(kit.getData().getItemType()) &&  item.getEnchantments().keySet().equals(kit.getEnchantments().keySet()))) {
				e.setCancelled(true);
         	} else if ((item.getData().getItemType().equals(exit.getData().getItemType()) &&  item.getEnchantments().keySet().equals(exit.getEnchantments().keySet()))) {
				e.setCancelled(true);
	     	} else if ((item.getData().getItemType().equals(spec.getData().getItemType()) &&  item.getEnchantments().keySet().equals(spec.getEnchantments().keySet()))) {
				e.setCancelled(true);
	     	} else if ((item.getData().getItemType().equals(join.getData().getItemType()) &&  item.getEnchantments().keySet().equals(join.getEnchantments().keySet()))) {
				e.setCancelled(true);
			} else if ((item.getData().getItemType().equals(options.getData().getItemType()) &&  item.getEnchantments().keySet().equals(options.getEnchantments().keySet()))) {
				e.setCancelled(true);
			} else if ((item.getData().getItemType().equals(lobbyMenu.getData().getItemType()) &&  item.getEnchantments().keySet().equals(lobbyMenu.getEnchantments().keySet()))) {
				e.setCancelled(true);
			}
		}

	}

	@EventHandler
    public void onBlockPlaced(BlockPlaceEvent e) {
        Player player = e.getPlayer();
        GamePlayer gPlayer = SkyWarsReloaded.getPC().getPlayer(player.getUniqueId());

        if (gPlayer.inGame()) {
        	gPlayer.setBlocks(gPlayer.getBlocks() + 1);
        }
    }
	
	@EventHandler
    public void onEntityDamage(EntityDamageEvent e) {
		if (SkyWarsReloaded.get().getConfig().getBoolean("gameVariables.noFallDamage")) {
			if (e.getEntity() instanceof Player && e.getCause().equals(DamageCause.FALL)) {
				Player player = (Player) e.getEntity();
				GamePlayer gPlayer = SkyWarsReloaded.getPC().getPlayer(player.getUniqueId());
				if (gPlayer.inGame()) {
					e.setCancelled(true);
				}
			}
		} 
		
		if (SkyWarsReloaded.get().getConfig().getBoolean("gameVariables.noFallDamageLobby")) {
			if (e.getEntity() instanceof Player && e.getCause().equals(DamageCause.FALL)) {
				Player player = (Player) e.getEntity();
				String world = SkyWarsReloaded.get().getConfig().getString("spawn.world");
				if (player.getWorld().getName().equalsIgnoreCase(world)) {
					e.setCancelled(true);
				}
			}
		} 
		
		if (e.getEntity() instanceof Player && e.getCause().equals(DamageCause.FALL)) {
			Player player = (Player) e.getEntity();
			GamePlayer gPlayer = SkyWarsReloaded.getPC().getPlayer(player.getUniqueId());
			if (gPlayer.inGame()) {
				Game game = gPlayer.getGame();
				if (game != null) {
					if (game.getState() == GameState.PREGAME) {
						e.setCancelled(true);
					}
				}
			}
		}

		if (e.getEntity() instanceof Player && e.getCause().equals(DamageCause.VOID)) {
			e.setDamage(20);
		}
	}
	
	@EventHandler		(priority = EventPriority.HIGH)
	public void onDeath(PlayerDeathEvent e){
		final Player player = e.getEntity();
		final Location loc = player.getLocation();
		final GamePlayer target = SkyWarsReloaded.getPC().getPlayer(player.getUniqueId());
		Entity ent = e.getEntity();
		String world = SkyWarsReloaded.get().getConfig().getString("spawn.world");
		if(player.getLocation().getWorld().getName().equalsIgnoreCase(world)) {
			List<ItemStack> drops = e.getDrops();
			ListIterator<ItemStack> litr = drops.listIterator();
			while(litr.hasNext()){
	 	            ItemStack item = litr.next();
	     	    if ((item.getData().getItemType().equals(kit.getData().getItemType()) &&  item.getEnchantments().keySet().equals(kit.getEnchantments().keySet()))) {
					litr.remove();
	         	} else if ((item.getData().getItemType().equals(exit.getData().getItemType()) &&  item.getEnchantments().keySet().equals(exit.getEnchantments().keySet()))) {
	         		litr.remove();
		     	} else if ((item.getData().getItemType().equals(spec.getData().getItemType()) &&  item.getEnchantments().keySet().equals(spec.getEnchantments().keySet()))) {
		     		litr.remove();
		     	} else if ((item.getData().getItemType().equals(join.getData().getItemType()) &&  item.getEnchantments().keySet().equals(join.getEnchantments().keySet()))) {
		     		litr.remove();
				} else if ((item.getData().getItemType().equals(options.getData().getItemType()) &&  item.getEnchantments().keySet().equals(options.getEnchantments().keySet()))) {
					litr.remove();
				} else if ((item.getData().getItemType().equals(lobbyMenu.getData().getItemType()) &&  item.getEnchantments().keySet().equals(lobbyMenu.getEnchantments().keySet()))) {
					litr.remove();
				}
	        }
		}
		DamageCause damageCause = DamageCause.CUSTOM;
		if (ent.getLastDamageCause() != null) {
			damageCause = ent.getLastDamageCause().getCause();
		} 
		final DamageCause dCause = damageCause;
		if (target.inGame()) {
			e.setDeathMessage("");
			Game game = target.getGame();
			if (game.getState() == GameState.PLAYING) {
				target.getGame().onPlayerDeath(target, dCause, loc);
			}
		}
	}	
	
    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
    	GamePlayer gplayer = SkyWarsReloaded.getPC().getPlayer(e.getPlayer().getUniqueId());
    	if (gplayer.inGame()) {
    		GameState gs = gplayer.getGame().getState();
        	if (gs == GameState.PREGAME) {
        		e.setCancelled(true);
        	}
    	}
    }
    
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent event){
    	if (SkyWarsReloaded.get().getConfig().getBoolean("gameVariables.addScorePrefix")) {
    		if (SkyWarsReloaded.get().getConfig().getBoolean("gameVariables.useExternalChat")) {
    			GamePlayer gPlayer = SkyWarsReloaded.getPC().getPlayer(event.getPlayer().getUniqueId());
    			int scoreValue = gPlayer.getScore();
            	String score;
            	if (scoreValue < 0) {
                    score = ChatColor.RED + "(" + gPlayer.getScore() + ")";
            	} else {
            		score = ChatColor.GREEN + "(+" + gPlayer.getScore() + ")";
            	}
            	String format = event.getFormat();
           		String world = gPlayer.getP().getWorld().getName();
           		for (Iterator<Player> iterator = event.getRecipients().iterator(); iterator.hasNext();) {
           			String world2 = iterator.next().getWorld().getName();

           			if (!world.equals(world2)) {
                        iterator.remove();
                    }
           		}
            	event.setFormat(score + format);
    		} else {
    			GamePlayer gPlayer = SkyWarsReloaded.getPC().getPlayer(event.getPlayer().getUniqueId());
            	String name = event.getPlayer().getDisplayName();
            	String prefix = "";
            	if (SkyWarsReloaded.chat.getPlayerPrefix(event.getPlayer()) != null) {
                	prefix = SkyWarsReloaded.chat.getPlayerPrefix(event.getPlayer());
            	} 
            	String colorMessage = ChatColor.translateAlternateColorCodes('&', event.getMessage());
            	String message = "";
            	if (SkyWarsReloaded.perms.has(gPlayer.getP(), "swr.color")) {
                	message = colorMessage;
            	} else {
            		message = ChatColor.stripColor(colorMessage);
            		while (message.contains("&")) {
            			message = ChatColor.translateAlternateColorCodes('&', message);
                		message = ChatColor.stripColor(message);
            		}
            	}
            	int scoreValue = gPlayer.getScore();
            	String score;
            	if (scoreValue < 0) {
                    score = ChatColor.RED + "(" + gPlayer.getScore() + ")";
            	} else {
            		score = ChatColor.GREEN + "(+" + gPlayer.getScore() + ")";
            	}
           		event.setCancelled(true);
           		if (gPlayer != null) {
               		World world = gPlayer.getP().getWorld();
               		if (world != null) {
                   		for (Player p: world.getPlayers()) {
                			p.sendMessage(new Messaging.MessageFormatter()
            				.setVariable("score", score)
            				.setVariable("prefix", prefix)
            				.setVariable("player", name)
            				.setVariable("message", message)
            				.format("chat"));
                   		}
               		}
           		}
    		}
		}
    }
    
    @EventHandler
    public void onPlayerMoveEvent(PlayerMoveEvent event) {
        final Player sender = event.getPlayer();
        if (sender!= null) {
        	GamePlayer gPlayer = SkyWarsReloaded.getPC().getPlayer(sender.getUniqueId());
            if (gPlayer.inGame()) {
            	String effect = gPlayer.getEffect();
            	if (effect != null) {
                    if (effect.equalsIgnoreCase("normal")) {
                    } else if (effect.equalsIgnoreCase("flame")) {
                        Random random = new Random();
                        ParticleEffect.FLAME.display(random.nextFloat(), random.nextFloat(), random.nextFloat(), 0, random.nextInt((8 - 4) + 4) + 1, sender.getLocation(), 50);
                    }  else if (effect.equalsIgnoreCase("smoke")) {
                        Random random = new Random();
                        ParticleEffect.SMOKE_LARGE.display(random.nextFloat(), random.nextFloat(), random.nextFloat(), 0, random.nextInt((16 - 8) + 8) + 1, sender.getLocation(), 50);
                    } else if (effect.equalsIgnoreCase("portal")) {
                        Random random = new Random();
                        ParticleEffect.PORTAL.display(random.nextFloat(), random.nextFloat(), random.nextFloat(), 0, random.nextInt((140 - 80) + 80) + 1, sender.getLocation(), 50);
                    } else if (effect.equalsIgnoreCase("heart")) {
                        Random random = new Random();
                        ParticleEffect.HEART.display(random.nextFloat(), random.nextFloat(), random.nextFloat(), 0, random.nextInt((10 - 3) + 3) + 1, sender.getLocation(), 50);
                    } else if (effect.equalsIgnoreCase("critical")) {
                        Random random = new Random();
                        ParticleEffect.CRIT.display(random.nextFloat(), random.nextFloat(), random.nextFloat(), 0, random.nextInt((8 - 5) + 5) + 1, sender.getLocation(), 50);
                    }  else if (effect.equalsIgnoreCase("water")) {
                        Random random = new Random();
                        ParticleEffect.WATER_SPLASH.display(random.nextFloat(), random.nextFloat(), random.nextFloat(), 0, random.nextInt((14 - 8) + 8) + 1, sender.getLocation(), 50);
                    } else if (effect.equalsIgnoreCase("redstone")) {
                        Random random = new Random();
                        ParticleEffect.REDSTONE.display(random.nextFloat(), random.nextFloat(), random.nextFloat(), 0, random.nextInt((10 - 3) + 3) + 1, sender.getLocation(), 50);
                    } else if (effect.equalsIgnoreCase("sparks")) {
                        Random random = new Random();
                        ParticleEffect.FIREWORKS_SPARK.display(random.nextFloat(), random.nextFloat(), random.nextFloat(), 0, random.nextInt((8 - 4) + 4) + 1, sender.getLocation(), 50);
                    } else if (effect.equalsIgnoreCase("lava_drip")) {
                        Random random = new Random();
                        ParticleEffect.DRIP_LAVA.display(random.nextFloat(), random.nextFloat(), random.nextFloat(), 0, random.nextInt((8 - 4) + 4) + 1, sender.getLocation(), 50);
                    } else if (effect.equalsIgnoreCase("lava")) {
                        Random random = new Random();
                        ParticleEffect.LAVA.display(random.nextFloat(), random.nextFloat(), random.nextFloat(), 0, random.nextInt((16 - 4) + 4) + 1, sender.getLocation(), 50);
                    }  else if (effect.equalsIgnoreCase("alphabet")) {
                        Random random = new Random();
                        ParticleEffect.ENCHANTMENT_TABLE.display(random.nextFloat(), random.nextFloat(), random.nextFloat(), 0, random.nextInt((16 - 8) + 8) + 1, sender.getLocation(), 50);
                    } else if (effect.equalsIgnoreCase("happy")) {
                        Random random = new Random();
                        ParticleEffect.VILLAGER_HAPPY.display(random.nextFloat(), random.nextFloat(), random.nextFloat(), 0, random.nextInt((12 - 4) + 4) + 1, sender.getLocation(), 50);
                    } else if (effect.equalsIgnoreCase("magic")) {
                        Random random = new Random();
                        ParticleEffect.SPELL_WITCH.display(random.nextFloat(), random.nextFloat(), random.nextFloat(), 0, random.nextInt((10 - 7) + 7) + 1, sender.getLocation(), 50);
                    } else if (effect.equalsIgnoreCase("music")) {
                        Random random = new Random();
                        ParticleEffect.NOTE.display(random.nextFloat(), random.nextFloat(), random.nextFloat(), 0, random.nextInt((10 - 4) + 4) + 1, sender.getLocation(), 50);
                    } else if (effect.equalsIgnoreCase("angry")) {
                        Random random = new Random();
                        ParticleEffect.VILLAGER_ANGRY.display(random.nextFloat(), random.nextFloat(), random.nextFloat(), 0, random.nextInt((5 - 2) + 2) + 1, sender.getLocation(), 50);
                    } else if (effect.equalsIgnoreCase("clouds")) {
                        Random random = new Random();
                        ParticleEffect.CLOUD.display(random.nextFloat(), random.nextFloat(), random.nextFloat(), 0, random.nextInt((12 - 6) + 6) + 1, sender.getLocation(), 50);
                    } else if (effect.equalsIgnoreCase("potion")) {
                        Random random = new Random();
                        ParticleEffect.SPELL.display(random.nextFloat(), random.nextFloat(), random.nextFloat(), 0, random.nextInt((12 - 6) + 6) + 1, sender.getLocation(), 50);
                    } else if (effect.equalsIgnoreCase("poison")) {
                        Random random = new Random();
                        ParticleEffect.SPELL_INSTANT.display(random.nextFloat(), random.nextFloat(), random.nextFloat(), 0, random.nextInt((12 - 6) + 6) + 1, sender.getLocation(), 50);
                    } else if (effect.equalsIgnoreCase("snow")) {
                        Random random = new Random();
                        ParticleEffect.SNOWBALL.display(random.nextFloat(), random.nextFloat(), random.nextFloat(), 0, random.nextInt((12 - 6) + 6) + 1, sender.getLocation(), 50);
                    } else if (effect.equalsIgnoreCase("slime")) {
                        Random random = new Random();
                        ParticleEffect.SLIME.display(random.nextFloat(), random.nextFloat(), random.nextFloat(), 0, random.nextInt((12 - 6) + 6) + 1, sender.getLocation(), 50);
                    }
            	}
            }
        }
    }
    
}
