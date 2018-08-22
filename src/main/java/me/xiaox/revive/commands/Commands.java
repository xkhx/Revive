package me.xiaox.revive.commands;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.xiaox.revive.Revive;
import me.xiaox.revive.enums.ReviveType;
import me.xiaox.revive.utils.FileUtil;
import me.xiaox.revive.utils.SendUtil;

public class Commands implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if(cmd.getName().equalsIgnoreCase("re")) {
			//输入 /re是执行
			if(args.length == 0) {
				if(!sender.hasPermission("revive.re")) {
					SendUtil.sendMessage(sender, "nopermission");
					return true;
				}
				final CommandSender cSender = sender;
				new BukkitRunnable() {
					@Override
					public void run() {
						String webtext = Revive.getStringByWeb("https://gitee.com/D_xiaox/ReviveUpdate/raw/master/re.txt");
						for(String text : webtext.split("\\|")) {
							cSender.sendMessage(text);
						}
					}
				}.runTaskAsynchronously(Revive.getInstance());
				return true;
			}
			
			//输入 /re help时执行
			if(args[0].equalsIgnoreCase("help")) {
				if(!sender.hasPermission("revive.help")) {
					SendUtil.sendMessage(sender, "nopermission");
					return true;
				}
				SendUtil.sendMessage(sender, "help");
				return true;
			}
			
			//输入 /re add/set 时执行
			if(args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("set")) {
				if(!sender.hasPermission("revive.add")) {
					SendUtil.sendMessage(sender, "nopermission");
					return true;
				}
				if(!(sender instanceof Player)) return true;
				Player player = (Player) sender;
				if(args.length == 2) {
					if(FileUtil.isExists(args[1])) {
						SendUtil.sendMessage(sender, "isexists");
						return true;
					}
					FileUtil.addRevive(ReviveType.DEFAULT, player.getLocation(), args[1], "");;
					SendUtil.sendMessage(sender, "addsuccessful");
					return true;
				}
				if(args.length == 3) {
					if(FileUtil.isExists(args[1])) {
						SendUtil.sendMessage(sender, "isexists");
						return true;
					}
					if(Revive.isInteger(args[2])) {
						FileUtil.addRevive(ReviveType.RADIUS, player.getLocation(), args[1], args[2]);;
						SendUtil.sendMessage(sender, "addsuccessful");
						return true;
					}
					String world = null;
					if(!Revive.getPremission().groupAdd(world, args[2], "revive.point." 
					+ player.getLocation().getWorld().getName() + "." + args[1])) {
						//这啥
						return true;
					}
					FileUtil.addRevive(ReviveType.GROUP, player.getLocation(), args[1], args[2]);;
					SendUtil.sendMessage(sender, "addsuccessful");
					return true;
				}
				SendUtil.sendMessage(sender, "parametererror");
				return true;
			}
			
			//输入 /re del/delete/remove 时执行
			if(args[0].equalsIgnoreCase("del") || args[0].equalsIgnoreCase("delete") || args[0].equalsIgnoreCase("remove")) {
				if(!sender.hasPermission("revive.del")) {
					SendUtil.sendMessage(sender, "nopermission");
					return true;
				}
				if(args.length == 2) {
					if(!FileUtil.isExists(args[1])) {
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
			if(args[0].equalsIgnoreCase("spawn")) {
				if(!sender.hasPermission("revive.spawn")) {
					SendUtil.sendMessage(sender, "nopermission");
					return true;
				}
				if(!(sender instanceof Player)) return true;
				Player player = (Player) sender;
				if(args.length == 2) {
					if(args[1].equalsIgnoreCase("all")) {
						FileUtil.addRevive(ReviveType.GLOBAL, player.getLocation(), "spawn", "");;
						SendUtil.sendMessage(sender, "addsuccessful");
						return true;
					}
					FileUtil.addRevive(ReviveType.WORLD, player.getLocation(), player.getLocation().getWorld().getName(), "");;
					SendUtil.sendMessage(sender, "addsuccessful");
					return true;
				}
				SendUtil.sendMessage(sender, "parametererror");
				return true;
			}
			
			//输入/re list 时执行
			if(args[0].equalsIgnoreCase("list")) {
				if(!sender.hasPermission("revive.list")) {
					SendUtil.sendMessage(sender, "nopermission");
					return true;
				}
				if(args.length == 2) {
					if(!args[1].equalsIgnoreCase("all")) {
						List<String> list = FileUtil.getReviveList(args[1]);
						if(list.size() == 0) {
							SendUtil.sendMessage(sender, "nolist");
							return true;
						}
						for(String msg : list) {
							sender.sendMessage(msg);
						}
						return true;
					}
					List<String> list = FileUtil.getReviveList("all");
					if(list.size() == 0) {
						SendUtil.sendMessage(sender, "nolist");
						return true;
					}
					for(String msg : list) {
						sender.sendMessage(msg);
					}
					return true;
				}
				SendUtil.sendMessage(sender, "parametererror");
				return true;
			}
			
			//输入/re title 时执行
			if(args[0].equalsIgnoreCase("title")) {
				if(!sender.hasPermission("revive.title")) {
					SendUtil.sendMessage(sender, "nopermission");
					return true;
				}
				if(args.length == 3) {
					if(args[1].equalsIgnoreCase("del") || args[1].equalsIgnoreCase("delete") || args[1].equalsIgnoreCase("remove")) {
						if(!FileUtil.isExists(args[2])) {
							SendUtil.sendMessage(sender, "noexists");
							return true;
						}
						if(!FileUtil.hasTitle(args[2])) {
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
				if(args.length == 5) {
					if(args[1].equalsIgnoreCase("add")) {
						if(!FileUtil.isExists(args[2])) {
							SendUtil.sendMessage(sender, "noexists");
							return true;
						}
						if(FileUtil.hasTitle(args[2])) {
							SendUtil.sendMessage(sender, "hastitle");
							return true;
						}
						FileUtil.addTitle(args[2], args[3], args[4]);
						SendUtil.sendMessage(sender, "titlesuccessful");
						return true;
					}
					if(args[1].equalsIgnoreCase("edit")) {
						if(!FileUtil.isExists(args[2])) {
							SendUtil.sendMessage(sender, "noexists");
							return true;
						}
						if(!FileUtil.hasTitle(args[2])) {
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
			if(args[0].equalsIgnoreCase("tp")) {
				if(!sender.hasPermission("revive.tp")) {
					SendUtil.sendMessage(sender, "nopermission");
					return true;
				}
				if(args.length == 2) {
					Player p = (Player) sender;
					if(!FileUtil.isExists(args[1])) {
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
			if(args[0].equalsIgnoreCase("enable")) {
				if(!sender.hasPermission("revive.enable")) {
					SendUtil.sendMessage(sender, "nopermission");
					return true;
				}
				if(args.length == 2) {
					if(!FileUtil.isExists(args[1])) {
						SendUtil.sendMessage(sender, "noexists");
						return true;
					}
					if(FileUtil.isEnableRevive(args[1])) {
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
			if(args[0].equalsIgnoreCase("disable")) {
				if(!sender.hasPermission("revive.enable")) {
					SendUtil.sendMessage(sender, "nopermission");
					return true;
				}
				if(args.length == 2) {
					if(!FileUtil.isExists(args[1])) {
						SendUtil.sendMessage(sender, "noexists");
						return true;
					}
					if(!FileUtil.isEnableRevive(args[1])) {
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
			
			SendUtil.sendMessage(sender, "parametererror");
		}
		return true;
	}

}
