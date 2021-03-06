package com.walrusone.skywars.commands;

import java.io.File;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.walrusone.skywars.SkyWarsReloaded;
import com.walrusone.skywars.utilities.Messaging;

public class SaveMapCmd extends BaseCmd { 
	
	public SaveMapCmd() {
		forcePlayer = false;
		cmdName = "save";
		argLength = 2; //counting cmdName
		usage = "<mapname>";
		desc = ":: Saves a map named <mapname>";

	}

	@Override
	public boolean run() {
		String worldName = args[1].toLowerCase();
		for (World world: SkyWarsReloaded.get().getServer().getWorlds()) {
			if (world.getName().equalsIgnoreCase(worldName)) {
				World editWorld = SkyWarsReloaded.get().getServer().getWorld(worldName);
				for (Player player: editWorld.getPlayers()) {
					String world2 = SkyWarsReloaded.get().getConfig().getString("spawn.world");
					int x = SkyWarsReloaded.get().getConfig().getInt("spawn.x");
					int y = SkyWarsReloaded.get().getConfig().getInt("spawn.y");
					int z = SkyWarsReloaded.get().getConfig().getInt("spawn.z");
					Location spawn;
					spawn = new Location(SkyWarsReloaded.get().getServer().getWorld(world2), x, y, z);
					player.teleport(spawn);
				}
				editWorld.save();
				SkyWarsReloaded.getMV().getMVWorldManager().unloadWorld(worldName);
				File dataDirectory = new File (SkyWarsReloaded.get().getDataFolder(), "maps");
				File target = new File (dataDirectory, worldName);
				SkyWarsReloaded.getWC().deleteWorld(target);
				File source = new File (SkyWarsReloaded.get().getServer().getWorldContainer().getAbsolutePath(), worldName);
				SkyWarsReloaded.getWC().copyWorld(source, target);
				SkyWarsReloaded.getWC().deleteWorld(source);
				sender.sendMessage(new Messaging.MessageFormatter().setVariable("mapname", worldName).format("maps.saved"));
				if (SkyWarsReloaded.getMC().mapRegistered(worldName)) {
					sender.sendMessage(new Messaging.MessageFormatter().format("maps.register-reminder"));
				}
				return true;
			}
		}
		sender.sendMessage(new Messaging.MessageFormatter().setVariable("mapname", worldName).format("error.map-not-in-edit"));
		return true;
	}

}
