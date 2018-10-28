package me.xiaox.revive.listeners;

import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class InvincibleTime implements Listener {

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if ((event.getDamager() instanceof Player || event.getDamager() instanceof Projectile)
                && (event.getEntity() instanceof Player || event.getEntity() instanceof Projectile)) {
            Player player = null;
            Player damager = null;
            if (event.getEntity() instanceof Player) {
                player = (Player) event.getEntity();
            }
            if (event.getDamager() instanceof Player) {
                damager = (Player) event.getDamager();
            }
            if (event.getEntity() instanceof Projectile) {
                Projectile projectile = (Projectile) event.getEntity();
                if (projectile.getShooter() instanceof Player) {
                    player = (Player) projectile.getShooter();
                }
            }
            if (event.getDamager() instanceof Projectile) {
                Projectile projectile = (Projectile) event.getDamager();
                if (projectile.getShooter() instanceof Player) {
                    damager = (Player) projectile.getShooter();
                }
            }
            //如果被攻击者是无敌时间
            if (PlayerRespawn.invincible.contains(player)) {
                event.setCancelled(true);
            }
            //如果攻击者是无敌时间
            if (PlayerRespawn.invincible.contains(damager)) {
                event.setCancelled(true);
            }
        }
    }
}
