package me.xiaox.revive.listeners;

import me.xiaox.revive.Revive;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.Map;

public class PlayerClick implements Listener {
    public static Map<Player, Location> locA = new HashMap<>();
    public static Map<Player, Location> locB = new HashMap<>();

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.isCancelled()) return;
        if (event.getItem() != null && event.getItem().isSimilar(Revive.getPointItem())
                && event.getPlayer().hasPermission("revive.admin") && event.getClickedBlock() != null) {
            Player player = event.getPlayer();
            if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                locA.put(player, event.getClickedBlock().getLocation());
                player.sendMessage("§b[§cRevive§b]§6选取点A: " + getXYZ(event.getClickedBlock().getLocation()));
            } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                locB.put(player, event.getClickedBlock().getLocation());
                player.sendMessage("§b[§cRevive§b]§6选取点B: " + getXYZ(event.getClickedBlock().getLocation()));
            }
        }
    }

    private String getXYZ(Location loc) {
        return "(" + loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ() + ")";
    }
}
