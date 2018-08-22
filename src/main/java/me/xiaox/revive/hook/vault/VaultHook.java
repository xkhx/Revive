package me.xiaox.revive.hook.vault;

import org.bukkit.plugin.RegisteredServiceProvider;

import me.xiaox.revive.Revive;
import net.milkbowl.vault.permission.Permission;

public class VaultHook {
	private Permission perm;
	private Revive plugin;
	
	public VaultHook(Revive plugin) {
		this.plugin = plugin;
		initVault();
	}
	
	public Permission getPermission() {
		return perm;
	}
	
	private boolean initVault() {
        boolean hasNull = false;
        //初始化权限系统
        RegisteredServiceProvider<Permission> permissionProvider = plugin.getServer().getServicesManager().getRegistration(Permission.class);
        if(permissionProvider != null){
        	if ((perm = permissionProvider.getProvider()) == null) hasNull = true;
        }
        return !hasNull;
    }
}
