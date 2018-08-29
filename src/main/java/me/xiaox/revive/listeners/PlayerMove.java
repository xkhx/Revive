package me.xiaox.revive.listeners;

import me.xiaox.revive.Revive;
import me.xiaox.revive.enums.ReviveType;
import me.xiaox.revive.utils.FileUtil;
import me.xiaox.revive.utils.SendUtil;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMove implements Listener {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.isCancelled()) return;
        if (event.getFrom().getX() == event.getTo().getX() && event.getFrom().getY() == event.getTo().getY() && event.getFrom().getZ() == event.getTo().getZ())
            return;
        Player player = event.getPlayer();
        FileConfiguration config = Revive.getReviveFile();
        for (String name : FileUtil.getReviveNameInWorld(player.getLocation().getWorld().getName())) {
            if (FileUtil.getType(name) == ReviveType.CIRCLE
                    && inCircle(player.getLocation(), FileUtil.getLocation(name)
                    , config.getInt(name + ".radius"), config.getInt(name + ".height"))) {
                String permission = "revive.circle." + player.getLocation().getWorld().getName() + name;
                if (!player.hasPermission(permission)) {
                    Revive.getPremission().playerAdd(player, permission);
                    SendUtil.sendMessage(event.getPlayer(), "fxpoint");
                }
                return;
            }
            if (FileUtil.getType(name) == ReviveType.DOMAIN && hasRegion(player.getLocation(), name)) {
                String permission = "revive.domain." + player.getLocation().getWorld().getName() + name;
                if (!player.hasPermission(permission)) {
                    Revive.getPremission().playerAdd(player, permission);
                    SendUtil.sendMessage(event.getPlayer(), "fxpoint");
                }
                return;
            }
        }
    }

    private boolean inCircle(Location playerLocation, Location center, int redius, int height) {
        double distance = Math.sqrt(Math.pow(
                playerLocation.getBlockX() - center.getBlockX(), 2)
                + Math.pow(playerLocation.getBlockZ() - center.getBlockZ(), 2));
        double locationHeight = center.getBlockY() + height;
        double centerY = center.getBlockY();
        double playerLocationY = playerLocation.getBlockY();

        return distance <= redius && playerLocationY >= centerY && playerLocationY <= locationHeight;
    }

    private boolean hasRegion(Location location, String name) {
        FileConfiguration config = Revive.getReviveFile();
        String worldname = config.getString(name + ".point.loc1.world");
        int x1 = config.getInt(name + ".point.loc1.x");
        int x2 = config.getInt(name + ".point.loc2.x");
        int y1 = config.getInt(name + ".point.loc1.y");
        int y2 = config.getInt(name + ".point.loc2.y");
        int z1 = config.getInt(name + ".point.loc1.z");
        int z2 = config.getInt(name + ".point.loc2.z");
        int miny = Math.min(y1, y2) - 1;
        int maxy = Math.max(y1, y2) + 1;
        int minz = Math.min(z1, z2) - 1;
        int maxz = Math.max(z1, z2) + 1;
        int minx = Math.min(x1, x2) - 1;
        int maxx = Math.max(x1, x2) + 1;
        if (location.getWorld().getName().equals(worldname)) {
            if (location.getX() > minx && location.getX() < maxx) {
                if (location.getY() > miny && location.getY() < maxy) {
                    return location.getZ() > minz && location.getZ() < maxz;
                }
            }
        }
        return false;
    }

}
