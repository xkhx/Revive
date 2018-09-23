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
import org.bukkit.scheduler.BukkitRunnable;

public class FileUtil {

    /**
     * 获取某个复活点是否启动
     *
     * @param name 复活点名
     * @return 存在返回true否则返回false
     */
    public static boolean isEnableRevive(String name) {
        FileConfiguration fc = Revive.getReviveFile();
        return fc.get(name + ".enable") == null || fc.getBoolean(name + ".enable");
    }

    /**
     * 启动某个复活点
     *
     * @param name 复活点名
     */
    public static void enableRevive(String name) {
        FileConfiguration fc = Revive.getReviveFile();
        fc.set(name + ".enable", true);
        Revive.saveRevive(fc);
    }

    /**
     * 禁用某个复活点
     *
     * @param name 复活点名
     */
    public static void disableRevive(String name) {
        FileConfiguration fc = Revive.getReviveFile();
        fc.set(name + ".enable", false);
        Revive.saveRevive(fc);
    }

    /**
     * 添加一个复活点
     *
     * @param type   复活点类型
     * @param loc    复活点位置
     * @param loc1   区域复活点位置1(其他时候设置为null)
     * @param loc2   区域复活点位置2(其他时候设置为null)
     * @param name   复活点名称
     * @param value  复活点参数
     * @param height 复活点高度(仅在圆形复活点的时候 其他时候设置为null或"")
     */
    public static void addRevive(ReviveType type, Location loc, Location loc1, Location loc2, String name, String value, String height) {
        new BukkitRunnable() {
            @Override
            public void run() {
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
                if (type == ReviveType.GROUP) {
                    fc.set(name + ".group", value);
                } else if (type == ReviveType.CIRCLE) {
                    fc.set(name + ".radius", value);
                    fc.set(name + ".height", height);
                } else if (type == ReviveType.DOMAIN) {
                    String world1 = loc1.getWorld().getName();
                    String world2 = loc2.getWorld().getName();
                    double x1 = loc1.getX();
                    double x2 = loc2.getX();
                    double y1 = loc1.getY();
                    double y2 = loc2.getY();
                    double z1 = loc1.getZ();
                    double z2 = loc2.getZ();
                    fc.set(name + ".point.loc1.world", world1);
                    fc.set(name + ".point.loc2.world", world2);
                    fc.set(name + ".point.loc1.x", x1);
                    fc.set(name + ".point.loc2.x", x2);
                    fc.set(name + ".point.loc1.y", y1);
                    fc.set(name + ".point.loc2.y", y2);
                    fc.set(name + ".point.loc1.z", z1);
                    fc.set(name + ".point.loc2.z", z2);
                }
                Revive.saveRevive(fc);
            }
        }.runTaskAsynchronously(Revive.getInstance());
    }

    /**
     * 给指定复活点添加一个Title
     *
     * @param name     复活点名
     * @param title    要添加的title标题
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
     *
     * @param name 复活点名
     * @return 存在则返回true否则返回false
     */
    public static boolean hasTitle(String name) {
        FileConfiguration fc = Revive.getReviveFile();
        return fc.get(name + ".title") != null;
    }

    /**
     * 编辑指定复活点的title
     *
     * @param name     复活点名
     * @param title    要编辑的title标题
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
     *
     * @param name 复活点名
     */
    public static void delTitle(String name) {
        FileConfiguration fc = Revive.getReviveFile();
        fc.set(name + ".title", null);
        Revive.saveRevive(fc);
    }

    /**
     * 删除复活点
     *
     * @param name 复活点名
     */
    public static void delRevive(String name) {
        FileConfiguration fc = Revive.getReviveFile();
        fc.set(name, null);
        Revive.saveRevive(fc);
    }

    /**
     * 从yml获取Location
     *
     * @param key 键
     * @return {@link Location}
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
     *
     * @param name 复活点名
     * @return 复活点类型枚举
     */
    public static ReviveType getType(String name) {
        FileConfiguration fc = Revive.getReviveFile();
        return ReviveType.valueOf(fc.getString(name + ".type"));
    }

    /**
     * 获取排序后的复活点
     *
     * @param player 玩家
     * @return 排序后复活点
     */
    public static List<String> getSortReviveName(Player player) {
        List<String> sortList = new ArrayList<>();
        List<String> list = getReviveNameInWorld(player.getWorld().getName());
        List<Double> distances = new ArrayList<>();
        Map<Double, String> map = new HashMap<>();
        for (String name : list) {
            double distance = player.getLocation().distance(getLocation(name));
            distances.add(distance);
            map.put(distance, name);
        }

        Collections.sort(distances);

        for (double distance : distances) {
            sortList.add(map.get(distance));
        }

        map.clear();
        return sortList;
    }

    /**
     * 获取某个世界的复活点
     *
     * @param worldName 世界名
     * @return 复活的list
     */
    public static List<String> getReviveNameInWorld(String worldName) {
        List<String> list = new ArrayList<>();
        FileConfiguration config = Revive.getReviveFile();
        //变量配置
        for (String name : config.getKeys(false)) {
            //如果是指定世界
            if (config.getString(name + ".world") != null && config.getString(name + ".world").equalsIgnoreCase(worldName)) {
                list.add(name);
            }
        }
        return list;
    }

    /**
     * 获取复活点列表 worldName为null或all则返回全服否则返回目标世界
     *
     * @param worldName 世界名
     * @return 返回复活点List列表
     */
    public static List<String> getReviveList(String worldName) {
        List<String> list = new ArrayList<>();

        FileConfiguration config = Revive.getReviveFile();

        for (String name : config.getKeys(false)) {
            ReviveType type = getType(name);
            switch (type) {
                case DEFAULT:
                    if (worldName != null && config.getString(name + ".world") != null && !config.getString(name + ".world").equalsIgnoreCase(worldName)) {
                        break;
                    }
                    list.add("§2" + name + "(" + config.getString(name + ".world")
                            + " " + config.getDouble(name + ".x")
                            + " " + config.getDouble(name + ".y")
                            + " " + config.getDouble(name + ".z") + ")");
                    break;
                case GLOBAL:
                    if (worldName != null && config.getString(name + ".world") != null && !config.getString(name + ".world").equalsIgnoreCase(worldName)) {
                        break;
                    }
                    list.add("§c§l" + name + "(" + config.getString(name + ".world")
                            + " " + config.getDouble(name + ".x")
                            + " " + config.getDouble(name + ".y")
                            + " " + config.getDouble(name + ".z") + ")");
                    break;
                case GROUP:
                    if (worldName != null && config.getString(name + ".world") != null && !config.getString(name + ".world").equalsIgnoreCase(worldName)) {
                        break;
                    }
                    list.add("§b" + name + "(" + config.getString(name + ".world")
                            + " " + config.getDouble(name + ".x")
                            + " " + config.getDouble(name + ".y")
                            + " " + config.getDouble(name + ".z") + ") - "
                            + config.getDouble(name + ".group"));
                    break;
                case CIRCLE:
                    if (worldName != null && config.getString(name + ".world") != null && !config.getString(name + ".world").equalsIgnoreCase(worldName)) {
                        break;
                    }
                    list.add("§5" + name + "(" + config.getString(name + ".world")
                            + " " + config.getDouble(name + ".x")
                            + " " + config.getDouble(name + ".y")
                            + " " + config.getDouble(name + ".z") + ") - 半径:"
                            + config.getDouble(name + ".radius")
                            + " 高度:" + config.getDouble(name + ".height"));
                    break;
                case WORLD:
                    if (worldName != null && config.getString(name + ".world") != null && !config.getString(name + ".world").equalsIgnoreCase(worldName)) {
                        break;
                    }
                    list.add("§c" + name + "(" + config.getString(name + ".world")
                            + " " + config.getDouble(name + ".x")
                            + " " + config.getDouble(name + ".y")
                            + " " + config.getDouble(name + ".z") + ")");
                    break;
                case DOMAIN: //要改
                    if (worldName != null && config.getString(name + ".world") != null && !config.getString(name + ".world").equalsIgnoreCase(worldName)) {
                        break;
                    }
                    list.add("§5" + name + "(" + config.getString(name + ".world")
                            + " " + config.getDouble(name + ".x")
                            + " " + config.getDouble(name + ".y")
                            + " " + config.getDouble(name + ".z") + ") - 半径:"
                            + config.getDouble(name + ".radius")
                            + " 高度:" + config.getDouble(name + ".height"));
                    break;
                default:
                    break;
            }
        }

        return list;
    }

    /**
     * 判断是否存在
     *
     * @param key 键
     * @return 存在返回true否则返回false
     */
    public static boolean isExists(String key) {
        FileConfiguration fc = Revive.getReviveFile();
        return fc.get(key) != null;
    }

    /**
     * 加载一个File
     *
     * @param file 文件
     * @return {@link FileConfiguration}
     */
    public static FileConfiguration load(File file) {
        if (!file.exists()) {
            try {

                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return YamlConfiguration.loadConfiguration(file);
    }
}
