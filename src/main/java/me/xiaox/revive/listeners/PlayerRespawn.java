package me.xiaox.revive.listeners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitRunnable;

import me.xiaox.revive.Revive;
import me.xiaox.revive.enums.ReviveType;
import me.xiaox.revive.utils.FileUtil;
import me.xiaox.revive.utils.SendUtil;

public class PlayerRespawn implements Listener {

    public static List<Player> invincible = new ArrayList<>();

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (Revive.getConfigFile().getBoolean("settings.autorespawn", false)) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    event.getEntity().spigot().respawn();
                }
            }.runTask(Revive.getInstance());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();

        //无敌时间
        if (Revive.getConfigFile().getBoolean("settings.invincible.enable", false)) {
            invincible.add(player);
            new BukkitRunnable() {
                @Override
                public void run() {
                    invincible.remove(player);
                }
            }.runTaskLater(Revive.getInstance(), Revive.getConfigFile().getLong("settings.invincible.time") * 20L);
        }

        List<String> list = FileUtil.getSortReviveName(player);

        for (String name : list) {
            if (!FileUtil.isEnableRevive(name)) {
                continue;
            }
            ReviveType type = FileUtil.getType(name);

            Location loc = FileUtil.getLocation(name);

            if (type == ReviveType.DEFAULT) {
                event.setRespawnLocation(loc);

                if (FileUtil.hasTitle(name)) {
                    sendMessage(player, name);
                }
                return;
            }

            if ((type == ReviveType.GROUP && player.hasPermission("revive.point."
                    + player.getWorld().getName() + "." + name))
                    || (type == ReviveType.CIRCLE && player.hasPermission("revive.circle."
                    + player.getWorld().getName() + "." + name))
                    || (type == ReviveType.DOMAIN && player.hasPermission("revive.domain."
                    + player.getWorld().getName() + "." + name))) {

                event.setRespawnLocation(loc);

                if (FileUtil.hasTitle(name)) {
                    sendMessage(player, name);
                }
                return;
            }
        }

        String worldName = player.getWorld().getName();
        if (FileUtil.isExists(worldName) && FileUtil.getType(worldName) == ReviveType.WORLD) {
            if (!FileUtil.isEnableRevive(worldName)) {
                return;
            }

            Location loc = FileUtil.getLocation(worldName);

            event.setRespawnLocation(loc);

            if (FileUtil.hasTitle(worldName)) {
                sendMessage(player, worldName);
            }
            return;
        }
        if (FileUtil.isExists("spawn") && FileUtil.getType("spawn") == ReviveType.GLOBAL) {
            if (!FileUtil.isEnableRevive("spawn")) {
                return;
            }

            Location loc = FileUtil.getLocation("spawn");

            event.setRespawnLocation(loc);

            if (FileUtil.hasTitle("spawn")) {
                sendMessage(player, "spawn");
            }
        }

    }

    private void sendMessage(Player player, String name) {
        final String titleName = FileUtil.getTitleName(name);
        final String subTitle = FileUtil.getSubTitle(name);
        new BukkitRunnable() {
            @Override
            public void run() {
                SendUtil.sendTitle(player, 20, 40, 20, titleName, subTitle);
            }
        }.runTaskLater(Revive.getInstance(), 10L);
    }
}
