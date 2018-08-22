package me.xiaox.revive.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import me.xiaox.revive.Revive;
import me.xiaox.revive.enums.ReviveType;

public class FileUtil {
	
	/**
	 * 获取某个复活点是否启动
	 * @param name 复活点名
	 * @return 存在返回true否则返回false
	 */
	public static boolean isEnableRevive(String name) {
		FileConfiguration fc = Revive.getReviveFile();
		return fc.get(name + ".enable") == null || fc.getBoolean(name + ".enable");
	}
	
	/**
	 * 启动某个复活点
	 * @param name 复活点名
	 */
	public static void enableRevive(String name) {
		FileConfiguration fc = Revive.getReviveFile();
		fc.set(name + ".enable", true);
		Revive.saveRevive(fc);
	}
	
	/**
	 * 禁用某个复活点
	 * @param name 复活点名
	 */
	public static void disableRevive(String name) {
		FileConfiguration fc = Revive.getReviveFile();
		fc.set(name + ".enable", false);
		Revive.saveRevive(fc);
	}
	
	/**
	 * 添加一个复活点
	 * @param type
	 * @param loc
	 * @param key
	 * @param group
	 */
	public static void addRevive(ReviveType type, Location loc, String name, String gr) {
		FileConfiguration fc = Revive.getReviveFile();
		String world = loc.getWorld().getName();
		double x = loc.getX();
		double y = loc.getY();
		double z = loc.getZ();
		float pitch = loc.getPitch();
		float yaw = loc.getYaw();
		fc.set(name + ".enable", true);
		fc.set(name + ".world", world);
		fc.set(name + ".x", x);
		fc.set(name + ".y", y);
		fc.set(name + ".z", z);
		fc.set(name + ".pitch", pitch);
		fc.set(name + ".yaw", yaw);
		fc.set(name + ".type", type.toString());
		if(type == ReviveType.GROUP) {
			fc.set(name + ".group", gr);
		}else if(type == ReviveType.RADIUS) {
			fc.set(name + ".radius", gr);
		}
		Revive.saveRevive(fc);
	}
	
	/**
	 * 给指定复活点添加一个Title
	 * @param name 复活点名
	 * @param title 要添加的title标题
	 * @param subtitle 要添加副标题
	 */
	public static void addTitle(String name, String title, String subtitle) {
		FileConfiguration fc = Revive.getReviveFile();
		fc.set(name + ".title.title", title);
		fc.set(name + ".title.subtitle", subtitle);
		Revive.saveRevive(fc);
	}
	public static String getTitleName(String name) {
		FileConfiguration fc = Revive.getReviveFile();
		return fc.getString(name + ".title.title");
	}
	public static String getSubTitle(String name) {
		FileConfiguration fc = Revive.getReviveFile();
		return fc.getString(name + ".title.subtitle");
	}
	
	/**
	 * 判断一个复活点是否存在title
	 * @param name 复活点名
	 * @return 存在则返回true否则返回false
	 */
	public static boolean hasTitle(String name) {
		FileConfiguration fc = Revive.getReviveFile();
		return fc.get(name + ".title") != null ? true:false;
	}
	
	/**
	 * 编辑指定复活点的title
	 * @param name 复活点名
	 * @param title 要编辑的title标题
	 * @param subtitle 要编辑的副标题
	 */
	public static void editTitle(String name, String title, String subtitle) {
		FileConfiguration fc = Revive.getReviveFile();
		fc.set(name + ".title.title", title);
		fc.set(name + ".title.subtitle", subtitle);
		Revive.saveRevive(fc);
	}
	
	/**
	 * 删除指定复活点的Title
	 * @param name 复活点名
	 */
	public static void delTitle(String name) {
		FileConfiguration fc = Revive.getReviveFile();
		fc.set(name + ".title", null);
		Revive.saveRevive(fc);
	}
	
	/**
	 * 删除复活点
	 * @param name 复活点名
	 */
	public static void delRevive(String name) {
		FileConfiguration fc = Revive.getReviveFile();
		fc.set(name, null);
		Revive.saveRevive(fc);
	}
	
	/**
	 * 从yml获取Location
	 * @param key
	 * @return
	 */
	public static Location getLocation(String key) {
		FileConfiguration fc = Revive.getReviveFile();
		String world = fc.getString(key + ".world");
		double x = fc.getDouble(key + ".x");
		double y = fc.getDouble(key + ".y");
		double z = fc.getDouble(key + ".z");
		float pitch = (float) fc.getDouble(key + ".pitch");
		float yaw = (float) fc.getDouble(key + ".yaw");
		return new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
	}
	
	/**
	 * 获取复活点类型
	 * @param name 复活点名
	 * @return 复活点类型枚举
	 */
	public static ReviveType getType(String name) {
		FileConfiguration fc = Revive.getReviveFile();
		return ReviveType.valueOf(fc.getString(name + ".type"));
	}
	
	/**
	 * 获取排序后的复活点
	 * @param player
	 * @param worldName
	 * @return
	 */
	public static List<String> getSortReviveName(Player player) {
		List<String> list = getReviveNameInWorld(player.getWorld().getName());
		List<Double> distances = new ArrayList<>();
		Map<Double, String> map = new HashMap<>();
		List<String> sortlist = new ArrayList<>();
		for(String name : list) {
			double distance = player.getLocation().distance(getLocation(name));
			distances.add(distance);
			map.put(distance, name);
		}
		
		Collections.sort(distances);
		
		for(double distance : distances) {
			sortlist.add(map.get(distance));
		}
		
		map.clear();
		return sortlist;
	}
	
	/**
	 * 获取某个世界的复活点
	 * @param worldName 世界名
	 * @return 复活的list
	 */
	public static List<String> getReviveNameInWorld(String worldName) {
		List<String> list = new ArrayList<String>();
		FileConfiguration config = Revive.getReviveFile();
		//变量配置
		for(String name : config.getKeys(false)) {
			//如果是指定世界
			if(config.getString(name + ".world") != null && config.getString(name + ".world").equalsIgnoreCase(worldName)) {
				list.add(name);
			}
		}
		return list;
	}
	
	/**
	 * 获取复活点的名字
	 * @param worldName 世界名
	 * @return
	 */
	/*
	public static <T> Map<ReviveType, List<String>> getReviveNameList(String worldName) {
		Map<ReviveType, List<String>> map = new HashMap<ReviveType, List<String>>();
		List<String> de = new ArrayList<String>();
		List<String> gl = new ArrayList<String>();
		List<String> gr = new ArrayList<String>();
		List<String> ra = new ArrayList<String>();
		List<String> wo = new ArrayList<String>();
		FileConfiguration config =Revive.getReviveFile();
		if(worldName == null || worldName.equalsIgnoreCase("") || worldName.equalsIgnoreCase("all")) {
			for(String name : Revive.getReviveFile().getKeys(false)) {
				ReviveType type = getType(name);
				switch (type) {
				case DEFAULT:
					de.add(name);
					break;
				case GLOBAL:
					gl.add(name);
					break;
				case GROUP:
					gr.add(name);
					break;
				case RADIUS:
					ra.add(name);
					break;
				case WORLD:
					wo.add(name);
					break;
				default:
					break;
				}
			}
			map.put(ReviveType.DEFAULT, de);
			map.put(ReviveType.GLOBAL, gl);
			map.put(ReviveType.GROUP, gr);
			map.put(ReviveType.RADIUS, ra);
			map.put(ReviveType.WORLD, wo);
			return map;
		}
		for(String name : Revive.getConfigFile().getKeys(false)) {
			if(config.getString(name + ".world") != null && config.getString(name + ".world").equalsIgnoreCase(worldName)) {
				ReviveType type = getType(name);
				switch (type) {
				case DEFAULT:
					de.add(name);
					break;
				case GLOBAL:
					gl.add(name);
					break;
				case GROUP:
					gr.add(name);
					break;
				case RADIUS:
					ra.add(name);
					break;
				case WORLD:
					wo.add(name);
					break;
				default:
					break;
				}
			}
		}
		map.put(ReviveType.DEFAULT, de);
		map.put(ReviveType.GLOBAL, gl);
		map.put(ReviveType.GROUP, gr);
		map.put(ReviveType.RADIUS, ra);
		map.put(ReviveType.WORLD, wo);
		return map;
	}
	*/
	
	/**
	 * 获取复活点列表 worldname为null或all则返回全服否则返回目标世界
	 * @param worldname
	 * @return 返回复活点List列表
	 */
	public static List<String> getReviveList(String worldName) {
		List<String> list = new ArrayList<String>();
		FileConfiguration config = Revive.getReviveFile();
		if(worldName == null || worldName.equalsIgnoreCase("") || worldName.equalsIgnoreCase("all")) {
			for(String name : Revive.getReviveFile().getKeys(false)) {
				ReviveType type = getType(name);
				switch (type) {
				case DEFAULT:
					list.add("§2" + name + "(" + config.getString(name + ".world") 
					+ " " + config.getDouble(name + ".x") 
					+ " " + config.getDouble(name + ".y") 
					+ " " + config.getDouble(name + ".z") + ")");
					break;
				case GLOBAL:
					list.add("§c§l" + name + "(" + config.getString(name + ".world") 
					+ " " + config.getDouble(name + ".x") 
					+ " " + config.getDouble(name + ".y") 
					+ " " + config.getDouble(name + ".z") + ")");
					break;
				case GROUP:
					list.add("§b" + name + "(" + config.getString(name + ".world") 
					+ " " + config.getDouble(name + ".x") 
					+ " " + config.getDouble(name + ".y") 
					+ " " + config.getDouble(name + ".z") + ") - " 
					+ config.getDouble(name + ".group"));
					break;
				case RADIUS:
					list.add("§5" + name + "(" + config.getString(name + ".world") 
					+ " " + config.getDouble(name + ".x") 
					+ " " + config.getDouble(name + ".y") 
					+ " " + config.getDouble(name + ".z") + ") - 半径:" 
					+ config.getDouble(name + ".radius"));
					break;
				case WORLD:
					list.add("§c" + name + "(" + config.getString(name + ".world") 
					+ " " + config.getDouble(name + ".x") 
					+ " " + config.getDouble(name + ".y") 
					+ " " + config.getDouble(name + ".z") + ")");
					break;
				default:
					break;
				}
			}
			return list;
		}
		for(String name : Revive.getReviveFile().getKeys(false)) {
			if(config.getString(name + ".world") != null && config.getString(name + ".world").equalsIgnoreCase(worldName)) {
				ReviveType type = getType(name);
				switch (type) {
				case DEFAULT:
					list.add("§2" + name + "(" + config.getString(name + ".world") 
					+ " " + config.getDouble(name + ".x") 
					+ " " + config.getDouble(name + ".y") 
					+ " " + config.getDouble(name + ".z") + ")");
					break;
				case GLOBAL:
					list.add("§c§l" + name + "(" + config.getString(name + ".world") 
					+ " " + config.getDouble(name + ".x") 
					+ " " + config.getDouble(name + ".y") 
					+ " " + config.getDouble(name + ".z") + ")");
					break;
				case GROUP:
					list.add("§b" + name + "(" + config.getString(name + ".world") 
					+ " " + config.getDouble(name + ".x") 
					+ " " + config.getDouble(name + ".y") 
					+ " " + config.getDouble(name + ".z") + ") - " 
					+ config.getDouble(name + ".group"));
					break;
				case RADIUS:
					list.add("§5" + name + "(" + config.getString(name + ".world") 
					+ " " + config.getDouble(name + ".x") 
					+ " " + config.getDouble(name + ".y") 
					+ " " + config.getDouble(name + ".z") + ") - 半径:" 
					+ config.getDouble(name + ".radius"));
					break;
				case WORLD:
					list.add("§c" + name + "(" + config.getString(name + ".world") 
					+ " " + config.getDouble(name + ".x") 
					+ " " + config.getDouble(name + ".y") 
					+ " " + config.getDouble(name + ".z") + ")");
					break;
				default:
					break;
				}
			}
		}
		return list;
	}
	
	/**
	 * 判断是否存在
	 * @param key
	 * @return 存在返回true否则返回false
	 */
	public static boolean isExists(String key) {
		FileConfiguration fc = Revive.getReviveFile();
		return fc.get(key) != null;
	}
	
	/**
	 * 加载一个File
	 * @param file
	 * @return 返回一个config
	 */
	public static FileConfiguration load(File file){
		if (!(file.exists())){
			try{
				file.createNewFile();
				}
			catch(IOException   e){
				e.printStackTrace();
				}
			}
		return YamlConfiguration.loadConfiguration(file);
	}
}
