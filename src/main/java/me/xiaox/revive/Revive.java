package me.xiaox.revive;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

import me.xiaox.revive.enums.ReviveType;
import me.xiaox.revive.listeners.PlayerClick;
import org.bstats.revive.Metrics;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;

import me.xiaox.revive.commands.Commands;
import me.xiaox.revive.hook.vault.VaultHook;
import me.xiaox.revive.listeners.PlayerRespawn;
import me.xiaox.revive.utils.FileUtil;
import me.xiaox.revive.utils.SendUtil;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.scheduler.BukkitRunnable;

public class Revive extends JavaPlugin {

    private static Revive instance;

    private static ProtocolManager pm;

    private static Permission perm = null;

    private static ItemStack pointItem;

    private static FileConfiguration config;
    private static FileConfiguration reviveConfig;

    public static Revive getInstance() {
        return instance;
    }

    public static ProtocolManager getProtocolManager() {
        return pm;
    }

    public static Permission getPermission() {
        return perm;
    }

    public static FileConfiguration getConfigFile() {
        return config;
    }

    public static FileConfiguration getReviveFile() {
        return reviveConfig;
    }

    private void initConfig() {
        saveDefaultConfig();
        config = getConfig();
        File fileRevive = new File(getInstance().getDataFolder(), "revive.yml");
        reviveConfig = FileUtil.load(fileRevive);
        SendUtil.initSend();
    }

    /*
    public static void saveConfigFile() {
        getInstance().saveConfig();
        getInstance().reloadConfig();
        config = getInstance().getConfig();
    }
    */

    public static void saveRevive(FileConfiguration fc) {
        File fileRevive = new File(getInstance().getDataFolder(), "revive.yml");
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

        initPointInTool();

        getServer().getPluginCommand("re").setExecutor(new Commands());

        getServer().getPluginManager().registerEvents(new PlayerRespawn(), this);
        getServer().getPluginManager().registerEvents(new PlayerClick(), this);

        //运行玩家区域检查
        doTick();
    }

    /**
     * 获取网页内容
     *
     * @param url 网页地址
     * @return 返回内容
     */
    public static String getStringByWeb(String url) {
        StringBuilder webText = new StringBuilder();
        try {
            URL urlObject = new URL(url);
            URLConnection uc = urlObject.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream(), StandardCharsets.UTF_8));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
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
     *
     * @param str 要判断的字符串
     * @return 整数返回true不是则返回false
     */
    public static boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }

    private static void initPointInTool() {
        pointItem = new ItemStack(Material.STICK, 1);
        ItemMeta im = pointItem.getItemMeta();
        im.setDisplayName("§6Revive选点工具");
        pointItem.setItemMeta(im);
    }

    /**
     * 获取选点工具
     * @return 选点物品
     */
    public static ItemStack getPointItem() {
        return pointItem;
    }

    //异步判断玩家所在区域
    private void doTick() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : getServer().getOnlinePlayers()) {
                    FileConfiguration config = Revive.getReviveFile();
                    for (String name : FileUtil.getReviveNameInWorld(player.getLocation().getWorld().getName())) {
                        if (FileUtil.getType(name) == ReviveType.CIRCLE
                                && inCircle(player.getLocation(), FileUtil.getLocation(name)
                                , config.getInt(name + ".radius"), config.getInt(name + ".height"))) {
                            String permission = "revive.circle." + player.getLocation().getWorld().getName() + name;

                            //同步
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    if (!player.hasPermission(permission)) {
                                        Revive.getPermission().playerAdd(player, permission);
                                        SendUtil.sendMessage(player, "fxpoint");
                                    }
                                }
                            }.runTask(Revive.getInstance());
                            return;
                        }
                        if (FileUtil.getType(name) == ReviveType.DOMAIN && hasRegion(player.getLocation(), name)) {
                            String permission = "revive.domain." + player.getLocation().getWorld().getName() + name;

                            //同步
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    if (!player.hasPermission(permission)) {
                                        Revive.getPermission().playerAdd(player, permission);
                                        SendUtil.sendMessage(player, "fxpoint");
                                    }
                                }
                            }.runTask(Revive.getInstance());
                            return;
                        }
                    }
                }
            }
        }.runTaskTimerAsynchronously(this, 10L, 10L);
    }

    //是否在圆形
    private boolean inCircle(Location playerLocation, Location center, int redius, int height) {
        double distance = Math.sqrt(Math.pow(
                playerLocation.getBlockX() - center.getBlockX(), 2)
                + Math.pow(playerLocation.getBlockZ() - center.getBlockZ(), 2));
        double locationHeight = center.getBlockY() + height;
        double centerY = center.getBlockY();
        double playerLocationY = playerLocation.getBlockY();

        return distance <= redius && playerLocationY >= centerY && playerLocationY <= locationHeight;
    }

    //是否在区域
    private boolean hasRegion(Location location, String name) {
        FileConfiguration config = Revive.getReviveFile();
        String worldName = config.getString(name + ".point.loc1.world");
        int x1 = config.getInt(name + ".point.loc1.x");
        int x2 = config.getInt(name + ".point.loc2.x");
        int y1 = config.getInt(name + ".point.loc1.y");
        int y2 = config.getInt(name + ".point.loc2.y");
        int z1 = config.getInt(name + ".point.loc1.z");
        int z2 = config.getInt(name + ".point.loc2.z");
        int minY = Math.min(y1, y2) - 1;
        int maxY = Math.max(y1, y2) + 1;
        int minZ = Math.min(z1, z2) - 1;
        int maxZ = Math.max(z1, z2) + 1;
        int minX = Math.min(x1, x2) - 1;
        int maxX = Math.max(x1, x2) + 1;
        if (location.getWorld().getName().equals(worldName)) {
            if (location.getX() > minX && location.getX() < maxX) {
                if (location.getY() > minY && location.getY() < maxY) {
                    return location.getZ() > minZ && location.getZ() < maxZ;
                }
            }
        }
        return false;
    }
}
