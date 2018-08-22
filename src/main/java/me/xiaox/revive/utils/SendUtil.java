package me.xiaox.revive.utils;

import java.lang.reflect.InvocationTargetException;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedChatComponent;

import me.xiaox.revive.Revive;

public class SendUtil {
	
	private static FileConfiguration config;
	private static String prefix;
	
	public static void initSend() {
		config = Revive.getConfigFile();
		prefix = config.getString("prefix");
	}
	
	/**
	 * 给sender发送一个消息
	 * @param sender 要发送的人
	 * @param key 配置键
	 */
	public static void sendMessage(CommandSender sender, String key) {
		sender.sendMessage(config.getString(key).replace("&", "§").replace("%prefix%", prefix.replace("&", "§")));
		return;
	}
	
	/**
	 * 发送一个Title给玩家
	 * @param player 目标玩家
	 * @param fadeIn 淡入时间 tick
	 * @param stay 停留时间 tick
	 * @param fadeOut 淡出时间 tick
	 * @param title 标题内容
	 * @param subTitle 副标题内容
	 */
	public static void sendTitle(Player player, Integer fadeIn, Integer stay, Integer fadeOut, String title, String subTitle) {
		ProtocolManager pm = Revive.getProtocolManager();
		PacketContainer packet = null;
		
		//设置标题
		if (title != null) {
			title = title.replace("&", "§").replace("%player%", player.getName());
			packet = pm.createPacket(PacketType.Play.Server.TITLE);
			packet.getTitleActions().write(0, EnumWrappers.TitleAction.TITLE); // EnumTitleAction
			packet.getChatComponents().write(0, WrappedChatComponent.fromText(title)); // 标题内容
			try {
				pm.sendServerPacket(player, packet, false); // 发送数据包
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		
		//设置副标题
		if (subTitle != null) {
			subTitle = subTitle.replace("&", "§").replace("%player%", player.getName());
			packet = pm.createPacket(PacketType.Play.Server.TITLE);
			packet.getTitleActions().write(0, EnumWrappers.TitleAction.SUBTITLE);
			packet.getChatComponents().write(0, WrappedChatComponent.fromText(subTitle));
			try {
				pm.sendServerPacket(player, packet, false); // 发送数据包
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		packet = pm.createPacket(PacketType.Play.Server.TITLE);
		packet.getTitleActions().write(0, EnumWrappers.TitleAction.TIMES);
		packet.getIntegers().write(0, fadeIn); // ---> c
		packet.getIntegers().write(1, stay); // ---> d
		packet.getIntegers().write(2, fadeOut); // ---> e
		try {
			pm.sendServerPacket(player, packet, false); // 发送数据包
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		
	}
}
