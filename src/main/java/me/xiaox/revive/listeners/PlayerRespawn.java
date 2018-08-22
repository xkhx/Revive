package me.xiaox.revive.listeners;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitRunnable;

import me.xiaox.revive.Revive;
import me.xiaox.revive.enums.ReviveType;
import me.xiaox.revive.utils.FileUtil;
import me.xiaox.revive.utils.SendUtil;

public class PlayerRespawn implements Listener {
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		final Player player = event.getPlayer();
		List<String> list = FileUtil.getSortReviveName(player);
		//System.out.println(list);
		for(String name : list) {
			if(!FileUtil.isEnableRevive(name)) {
				continue;
			}
			ReviveType type = FileUtil.getType(name);
			if(type != ReviveType.GROUP && type != ReviveType.RADIUS && type != ReviveType.WORLD) {
				event.setRespawnLocation(FileUtil.getLocation(name));
				if(FileUtil.hasTitle(name)) {
					final String titleName = FileUtil.getTitleName(name);
					final String subTitle = FileUtil.getSubTitle(name);
					new BukkitRunnable() {
						@Override
						public void run() {
							SendUtil.sendTitle(player, 20, 40, 20, titleName, subTitle);
						}
					}.runTaskLater(Revive.getInstance(), 10L);
				}
				return;
			}
			if((type == ReviveType.GROUP && player.hasPermission("revive.point." 
			+ player.getWorld().getName() + "." + name)) 
					|| (type == ReviveType.RADIUS && player.hasPermission("revive.radius." 
			+ player.getWorld().getName() + "." + name ))) {
				event.setRespawnLocation(FileUtil.getLocation(name));
				if(FileUtil.hasTitle(name)) {
					final String titleName = FileUtil.getTitleName(name);
					final String subTitle = FileUtil.getSubTitle(name);
					new BukkitRunnable() {
						@Override
						public void run() {
							SendUtil.sendTitle(player, 20, 40, 20, titleName, subTitle);
						}
					}.runTaskLater(Revive.getInstance(), 10L);
				}
				return;
			}
		}
		String worldName = player.getWorld().getName();
		if(FileUtil.isExists(worldName) && FileUtil.getType(worldName) == ReviveType.WORLD) {
			if(!FileUtil.isEnableRevive(worldName)) {
				return;
			}
			final String titleName = FileUtil.getTitleName(worldName);
			final String subTitle = FileUtil.getSubTitle(worldName);
			new BukkitRunnable() {
				@Override
				public void run() {
					SendUtil.sendTitle(player, 20, 40, 20, titleName, subTitle);
				}
			}.runTaskLater(Revive.getInstance(), 10L);
			return;
		}
		if(FileUtil.isExists("spawn") && FileUtil.getType("spawn") == ReviveType.GLOBAL) {
			if(!FileUtil.isEnableRevive("spawn")) {
				return;
			}
			event.setRespawnLocation(FileUtil.getLocation("spawn"));
			if(FileUtil.hasTitle("spawn")) {
				final String titleName = FileUtil.getTitleName("spawn");
				final String subTitle = FileUtil.getSubTitle("spawn");
				new BukkitRunnable() {
					@Override
					public void run() {
						SendUtil.sendTitle(player, 20, 40, 20, titleName, subTitle);
					}
				}.runTaskLater(Revive.getInstance(), 10L);
			}
			return;
		}
	}
}
