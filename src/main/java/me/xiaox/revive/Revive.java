package me.xiaox.revive;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Pattern;

import org.bstats.revive.Metrics;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;

import me.xiaox.revive.commands.Commands;
import me.xiaox.revive.hook.vault.VaultHook;
import me.xiaox.revive.listeners.PlayerRespawn;
import me.xiaox.revive.utils.FileUtil;
import me.xiaox.revive.utils.SendUtil;
import net.milkbowl.vault.permission.Permission;

public class Revive extends JavaPlugin {
	
	private static Revive instance;
	
	private static ProtocolManager pm;
	
	private static Permission perm = null;
	
	private static FileConfiguration config;
	private static FileConfiguration reviveConfig;
	
	public static Revive getInstance() {
		return instance;
	}
	
	public static ProtocolManager getProtocolManager() {
		return pm;
	}
	
	public static Permission getPremission() {
		return perm;
	}
	
	public static FileConfiguration getConfigFile() {
		return config;
	}
	public static FileConfiguration getReviveFile() {
		return reviveConfig;
	}
	
	public void initConfig() {
		saveDefaultConfig();
		reloadConfig();
		config = getConfig();
		File fileRevive = new File(getInstance().getDataFolder(),"revive.yml");
		reviveConfig = FileUtil.load(fileRevive);
		SendUtil.initSend();
	}
	public static void saveConfigFile() {
		getInstance().saveConfig();
		getInstance().reloadConfig();
		config = getInstance().getConfig();
	}
	public static void saveRevive(FileConfiguration fc) {
		File fileRevive = new File(getInstance().getDataFolder(),"revive.yml");
		try {
			fc.save(fileRevive);
		} catch (IOException e) {
			e.printStackTrace();
		}
		reviveConfig = FileUtil.load(fileRevive);
	}
	
	@Override
	public void onEnable() {
		instance = this;
		
		new Metrics(this);
		
		pm = ProtocolLibrary.getProtocolManager();
		
		perm = new VaultHook(instance).getPermission();
		
		initConfig();
		
		getServer().getPluginCommand("re").setExecutor(new Commands());
		
		getServer().getPluginManager().registerEvents(new PlayerRespawn(), this);
	}
	
	/**
	 * 获取网页内容
	 * @param url 网页地址
	 * @return 返回内容
	 */
	public static String getStringByWeb(String url) {
		StringBuilder webText = new StringBuilder();
		try {
			URL urlObject = new URL(url);
			URLConnection uc = urlObject.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream(),"UTF-8"));
			String inputLine = null;
			while((inputLine = in.readLine()) != null) {
				webText.append(inputLine);
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return webText.toString();
	}
	
	/**
	 * 判断是否为整数
	 * @param str 要判断的字符串
	 * @return 整数返回true不是则返回false
	 */
	public static boolean isInteger(String str) {  
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");  
        return pattern.matcher(str).matches();  
  }
}
