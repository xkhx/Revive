package me.xiaox.revive.commands;

import java.util.List;

import me.xiaox.revive.listeners.PlayerClick;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.xiaox.revive.Revive;
import me.xiaox.revive.enums.ReviveType;
import me.xiaox.revive.utils.FileUtil;
import me.xiaox.revive.utils.SendUtil;

public class Commands implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (cmd.getName().equalsIgnoreCase("re")) {
            //输入 /re是执行
            if (args.length == 0) {
                if (!sender.hasPermission("revive.re")) {
                    SendUtil.sendMessage(sender, "nopermission");
                    return true;
                }

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        String webText = Revive.getStringByWeb("https://gitee.com/D_xiaox/ReviveUpdate/raw/master/re.txt");
                        for (String text : webText.split("\\|")) {
                            sender.sendMessage(text);
                        }
                    }
                }.runTaskAsynchronously(Revive.getInstance());
                return true;
            }

            //输入 /re help时执行
            if (args[0].equalsIgnoreCase("help")) {
                if (!sender.hasPermission("revive.help")) {
                    SendUtil.sendMessage(sender, "nopermission");
                    return true;
                }
                SendUtil.sendMessage(sender, "help");
                return true;
            }

            //输入 /re add/set 时执行
            if (args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("set")) {
                if (!sender.hasPermission("revive.add")) {
                    SendUtil.sendMessage(sender, "nopermission");
                    return true;
                }
                if (!(sender instanceof Player)) return true;
                Player player = (Player) sender;
                if (args.length == 2) {
                    if (FileUtil.isExists(args[1])) {
                        SendUtil.sendMessage(sender, "isexists");
                        return true;
                    }
                    FileUtil.addRevive(ReviveType.DEFAULT, player.getLocation()
                            , null, null, args[1], "", "");
                    SendUtil.sendMessage(sender, "addsuccessful");
                    return true;
                }
                if (args.length >= 3) {
                    if (FileUtil.isExists(args[1])) {
                        SendUtil.sendMessage(sender, "isexists");
                        return true;
                    }
                    //group
                    if (args[2].equalsIgnoreCase("group") && args.length == 4) {
                        World world = player.getLocation().getWorld();
                        if (!Revive.getPermission().groupAdd(world, args[3], "revive.point."
                                + world.getName() + "." + args[1])) {
                            return true;
                        }
                        FileUtil.addRevive(ReviveType.GROUP, player.getLocation()
                                , null, null, args[1], args[3], "");
                        SendUtil.sendMessage(sender, "addsuccessful");
                        return true;
                    }
                    //circle
                    if (args[2].equalsIgnoreCase("circle") && args.length == 5) {
                        if (Revive.isInteger(args[3]) && Revive.isInteger(args[4])) {
                            FileUtil.addRevive(ReviveType.CIRCLE, player.getLocation()
                                    , null, null, args[1], args[3], args[4]);
                            SendUtil.sendMessage(sender, "addsuccessful");
                            return true;
                        }
                        SendUtil.sendMessage(sender, "parametererror");
                        return true;
                    }
                    //domain
                    if (args[2].equalsIgnoreCase("domain") && args.length == 3) {
                        if (!PlayerClick.locA.containsKey(player) || !PlayerClick.locB.containsKey(player)) {
                            SendUtil.sendMessage(sender, "noselectpoint");
                            return true;
                        }
                        FileUtil.addRevive(ReviveType.DOMAIN, player.getLocation()
                                , PlayerClick.locA.get(player)
                                , PlayerClick.locB.get(player), args[1], "", "");
                        PlayerClick.locA.remove(player);
                        PlayerClick.locB.remove(player);
                        SendUtil.sendMessage(sender, "addsuccessful");
                        return true;
                    }
                }
                SendUtil.sendMessage(sender, "parametererror");
                return true;
            }

            //输入 /re del/delete/remove 时执行   -   这里也要修改
            if (args[0].equalsIgnoreCase("del") || args[0].equalsIgnoreCase("delete") || args[0].equalsIgnoreCase("remove")) {
                if (!sender.hasPermission("revive.del")) {
                    SendUtil.sendMessage(sender, "nopermission");
                    return true;
                }
                if (args.length == 2) {
                    if (!FileUtil.isExists(args[1])) {
                        SendUtil.sendMessage(sender, "noexists");
                        return true;
                    }
                    FileUtil.delRevive(args[1]);
                    SendUtil.sendMessage(sender, "delsuccessful");
                    return true;
                }
                SendUtil.sendMessage(sender, "parametererror");
                return true;
            }

            //输入 /re spawn 时执行
            if (args[0].equalsIgnoreCase("spawn")) {
                if (!sender.hasPermission("revive.spawn")) {
                    SendUtil.sendMessage(sender, "nopermission");
                    return true;
                }
                if (!(sender instanceof Player)) return true;
                Player player = (Player) sender;
                if (args.length == 2) {
                    if (args[1].equalsIgnoreCase("all")) {
                        FileUtil.addRevive(ReviveType.GLOBAL, player.getLocation()
                                , null, null, "spawn", "", "");
                        SendUtil.sendMessage(sender, "addsuccessful");
                        return true;
                    }
                    FileUtil.addRevive(ReviveType.WORLD, player.getLocation()
                            , null, null, player.getLocation().getWorld().getName(), "", "");
                    SendUtil.sendMessage(sender, "addsuccessful");
                    return true;
                }
                SendUtil.sendMessage(sender, "parametererror");
                return true;
            }

            //输入/re list 时执行
            if (args[0].equalsIgnoreCase("list")) {
                if (!sender.hasPermission("revive.list")) {
                    SendUtil.sendMessage(sender, "nopermission");
                    return true;
                }
                if (args.length == 2) {
                    if (!args[1].equalsIgnoreCase("all")) {
                        List<String> list = FileUtil.getReviveList(args[1]);
                        if (list.size() == 0) {
                            SendUtil.sendMessage(sender, "nolist");
                            return true;
                        }
                        for (String msg : list) {
                            sender.sendMessage(msg);
                        }
                        return true;
                    }
                    List<String> list = FileUtil.getReviveList("all");
                    if (list.size() == 0) {
                        SendUtil.sendMessage(sender, "nolist");
                        return true;
                    }
                    for (String msg : list) {
                        sender.sendMessage(msg);
                    }
                    return true;
                }
                SendUtil.sendMessage(sender, "parametererror");
                return true;
            }

            //输入/re title 时执行
            if (args[0].equalsIgnoreCase("title")) {
                if (!sender.hasPermission("revive.title")) {
                    SendUtil.sendMessage(sender, "nopermission");
                    return true;
                }
                if (args.length == 3) {
                    if (args[1].equalsIgnoreCase("del") || args[1].equalsIgnoreCase("delete") || args[1].equalsIgnoreCase("remove")) {
                        if (!FileUtil.isExists(args[2])) {
                            SendUtil.sendMessage(sender, "noexists");
                            return true;
                        }
                        if (!FileUtil.hasTitle(args[2])) {
                            SendUtil.sendMessage(sender, "notitle");
                            return true;
                        }
                        FileUtil.delTitle(args[2]);
                        SendUtil.sendMessage(sender, "deltitlesuccessful");
                        return true;
                    }
                    SendUtil.sendMessage(sender, "parametererror");
                    return true;
                }
                if (args.length == 5) {
                    if (args[1].equalsIgnoreCase("add")) {
                        if (!FileUtil.isExists(args[2])) {
                            SendUtil.sendMessage(sender, "noexists");
                            return true;
                        }
                        if (FileUtil.hasTitle(args[2])) {
                            SendUtil.sendMessage(sender, "hastitle");
                            return true;
                        }
                        FileUtil.addTitle(args[2], args[3], args[4]);
                        SendUtil.sendMessage(sender, "titlesuccessful");
                        return true;
                    }
                    if (args[1].equalsIgnoreCase("edit")) {
                        if (!FileUtil.isExists(args[2])) {
                            SendUtil.sendMessage(sender, "noexists");
                            return true;
                        }
                        if (!FileUtil.hasTitle(args[2])) {
                            SendUtil.sendMessage(sender, "notitle");
                            return true;
                        }
                        FileUtil.editTitle(args[2], args[3], args[4]);
                        SendUtil.sendMessage(sender, "titlesuccessful");
                        return true;
                    }
                }
                SendUtil.sendMessage(sender, "parametererror");
                return true;
            }

            //如果输入/re tp
            if (args[0].equalsIgnoreCase("tp")) {
                if (!sender.hasPermission("revive.tp")) {
                    SendUtil.sendMessage(sender, "nopermission");
                    return true;
                }
                if (args.length == 2) {
                    Player p = (Player) sender;
                    if (!FileUtil.isExists(args[1])) {
                        SendUtil.sendMessage(sender, "noexists");
                        return true;
                    }
                    p.teleport(FileUtil.getLocation(args[1]));
                    SendUtil.sendMessage(sender, "tpsuccessful");
                    return true;
                }
                SendUtil.sendMessage(sender, "parametererror");
                return true;
            }

            //如果输入/re enable
            if (args[0].equalsIgnoreCase("enable")) {
                if (!sender.hasPermission("revive.enable")) {
                    SendUtil.sendMessage(sender, "nopermission");
                    return true;
                }
                if (args.length == 2) {
                    if (!FileUtil.isExists(args[1])) {
                        SendUtil.sendMessage(sender, "noexists");
                        return true;
                    }
                    if (FileUtil.isEnableRevive(args[1])) {
                        SendUtil.sendMessage(sender, "isenable");
                        return true;
                    }
                    FileUtil.enableRevive(args[1]);
                    SendUtil.sendMessage(sender, "enablesuccessful");
                    return true;
                }
                SendUtil.sendMessage(sender, "parametererror");
                return true;
            }
            //如果输入/re disable
            if (args[0].equalsIgnoreCase("disable")) {
                if (!sender.hasPermission("revive.enable")) {
                    SendUtil.sendMessage(sender, "nopermission");
                    return true;
                }
                if (args.length == 2) {
                    if (!FileUtil.isExists(args[1])) {
                        SendUtil.sendMessage(sender, "noexists");
                        return true;
                    }
                    if (!FileUtil.isEnableRevive(args[1])) {
                        SendUtil.sendMessage(sender, "noenable");
                        return true;
                    }
                    FileUtil.disableRevive(args[1]);
                    SendUtil.sendMessage(sender, "disablesuccessful");
                    return true;
                }
                SendUtil.sendMessage(sender, "parametererror");
                return true;
            }
            //如果输入/re getitem
            if (args[0].equalsIgnoreCase("getitem")) {
                if (!sender.hasPermission("revive.getitem")) {
                    SendUtil.sendMessage(sender, "nopermission");
                    return true;
                }
                if (!(sender instanceof Player)) return true;
                Player player = (Player) sender;
                player.getInventory().addItem(Revive.getPointItem());
                player.updateInventory();
                SendUtil.sendMessage(sender, "getitem");
            }
            //如果输入/re info
            if (args[0].equalsIgnoreCase("info")) {
                if (!sender.hasPermission("revive.info")) {
                    SendUtil.sendMessage(sender, "nopermission");
                    return true;
                }
                if (args.length == 2) {
                    if (!FileUtil.isExists(args[1])) {
                        SendUtil.sendMessage(sender, "noexists");
                        return true;
                    }
                    FileConfiguration config = Revive.getReviveFile();
                    String msg = Revive.getConfigFile().getString("reviveinfo")
                            .replace("&", "")
                            .replace("%revive%", getXYZ(FileUtil.getLocation(args[1])))
                            .replace("%xyz%", "")
                            .replace("%type%", config.getString(args[1] + ".type"))
                            .replace("%world%", config.getString(args[1] + ".world"));
                    sender.sendMessage(msg);
                }
            }
            //如果输入/re modify
            if (args[0].equalsIgnoreCase("modify")) {
                if (!sender.hasPermission("revive.modify")) {
                    SendUtil.sendMessage(sender, "nopermission");
                    return true;
                }
                if (args.length == 3) {
                    if (!FileUtil.isExists(args[1])) {
                        SendUtil.sendMessage(sender, "nopermission");
                        return true;
                    }
                    FileConfiguration config = Revive.getReviveFile();
                    if (FileUtil.getType(args[1]) != ReviveType.GROUP) {
                        SendUtil.sendMessage(sender, "nogroup");
                        return true;
                    }
                    if (!Revive.getPermission().groupAdd(Bukkit.getWorld(
                            config.getString(args[1] + ".world")), args[2], "revive.point."
                            + config.getString(args[1] + ".world") + "." + args[1])) {
                        return true;
                    }
                    Revive.getPermission().groupRemove(Bukkit.getWorld(
                            config.getString(args[1] + ".world"))
                            , config.getString(args[1] + ".group"), "revive.point."
                                    + config.getString(args[1] + ".world") + "." + args[1]);
                    config.set(args[1] + ".group", args[2]);
                    Revive.saveRevive(config);
                    SendUtil.sendMessage(sender, "modifygroup");
                    return true;
                }
            }
            //如果输入/re addhelp
            if (args[0].equalsIgnoreCase("addhelp")) {
                SendUtil.sendMessage(sender, "addhelp");
                return true;
            }

            SendUtil.sendMessage(sender, "parametererror");
        }
        return true;
    }

    private String getXYZ(Location loc) {
        return "(" + loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ() + ")";
    }

}
