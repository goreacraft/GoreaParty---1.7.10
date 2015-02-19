package com.goreacraft.plugins.goreaparty;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Util {
	
	/**
	 * 
	 * Sends to player else to console
	 */
	static void send(CommandSender sender, String msg)
	{
		if(sender instanceof Player)
		{
			((Player) sender).sendMessage(msg);
		} else 
			System.out.println(msg);		
	}
	
	public void getLogo(CommandSender sender){
		send(sender, ChatColor.YELLOW + "......................................................." + ChatColor.GOLD + " Plugin made by: "+ ChatColor.YELLOW + ".......................................................");
		send(sender, ChatColor.YELLOW + "     o   \\ o /  _ o              \\ /               o_   \\ o /   o");
		send(sender, ChatColor.YELLOW + "    /|\\     |      /\\   __o        |        o__    /\\      |     /|\\");
		send(sender, ChatColor.YELLOW + "    / \\   / \\    | \\  /) |       /o\\       |  (\\   / |    / \\   / \\");
		send(sender, ChatColor.YELLOW + "......................................................." + ChatColor.GOLD + ChatColor.BOLD + " GoreaCraft  "+ ChatColor.YELLOW + ".......................................................");
	}
	public void getShortLogo(CommandSender sender){
		send(sender, ChatColor.YELLOW + ".................................................." + ChatColor.GOLD + " Plugin made by GoreaCraft "+ ChatColor.YELLOW + "..................................................");
	}
	public void getHelp(CommandSender sender){
		send(sender, ChatColor.GRAY+""+ChatColor.ITALIC + "\"[]\" is mandatory and \"<>\" is optional " );     	

		send(sender, ChatColor.DARK_AQUA + "/p create [party_name] :" + ChatColor.RESET + ChatColor.ITALIC +" Creates a new party");    	
		send(sender, ChatColor.DARK_AQUA + "/p [invite/i] [player] :" + ChatColor.RESET + ChatColor.ITALIC +" Invite a player to your party");
		send(sender, ChatColor.DARK_AQUA + "/p [join/j] <party_name> :" + ChatColor.RESET + ChatColor.ITALIC +" Join a party or list your invitations");
		send(sender, ChatColor.DARK_AQUA + "/p [chat/c] :" + ChatColor.RESET + ChatColor.ITALIC +" Toggle party chat");
		send(sender, ChatColor.DARK_AQUA + "/p list :" + ChatColor.RESET + ChatColor.ITALIC +" List partys");
		send(sender, ChatColor.DARK_AQUA + "/p info <party_name>:" + ChatColor.RESET + ChatColor.ITALIC +" More info about partys");
		send(sender, ChatColor.DARK_AQUA + "/p kick [player]:" + ChatColor.RESET + ChatColor.ITALIC +" Kick a player form your party");
		send(sender, ChatColor.DARK_AQUA + "/p leave :" + ChatColor.RESET + ChatColor.ITALIC +" Leave your party");
		send(sender, ChatColor.DARK_AQUA + "/p promote [member/officer/leader] [player]:" + ChatColor.RESET + ChatColor.ITALIC +" Set party ranks");
    	 
		send(sender, ChatColor.DARK_AQUA + "/p disband :" + ChatColor.RESET + ChatColor.ITALIC +" Disbands your party");  
		send(sender, ChatColor.DARK_AQUA + "/p loot :" + ChatColor.RESET + ChatColor.ITALIC +" Toggle loot option - WIP");
		send(sender, ChatColor.DARK_AQUA + "/p xp:" + ChatColor.RESET + ChatColor.ITALIC +" Toggle xp sharing - WIP");
		send(sender, ChatColor.DARK_AQUA + "/p reload :" + ChatColor.RESET + ChatColor.ITALIC +" Reloads the configs.");
	}



	public static Party getParty(OfflinePlayer p) {
		for(Party party:Main.partys)
			if(party.getMembers().contains(p))
				return party;
		return null;
	}
	public static Party getParty(String partyname) {
		for(Party party:Main.partys)
			if(party.getName().equals(partyname))
				return party;
		return null;
	}

	public static List<String> getAllPartyNames() {
		List<String> names = new ArrayList<String>();
		for(Party party:Main.partys)
			names.add(party.getName());
		return names;
	}
/*	
	@SuppressWarnings("unchecked")
	public static JSONObject makeJsonObject(Party party, OfflinePlayer op) {		
		JSONObject obj = new JSONObject();
			obj.put("rank", party.getRank(op).toString());
			obj.put("name", op.getName());
			obj.put("chat", ChatEvent.chat.contains(op.getUniqueId()));
		return obj;
	}

	@SuppressWarnings("unchecked")
	public static JSONObject makePartyJson(Party party) {
		JSONObject obj = new JSONObject();
		for(OfflinePlayer p:party.getMembers()){
			obj.put(p.getUniqueId(), makeJsonObject(party,p));
		}
		return obj;
	}*/

	public static OfflinePlayer getOfflinePlayer(String string) {
		for(OfflinePlayer p:Bukkit.getOfflinePlayers())
			if(p.getName().equals(string)) return p;
		return null;
	}
	
	public static void sendOrMail(CommandSender sender, List<String> list, Object... args) {
		if(sender instanceof OfflinePlayer){
			sendOrMail((OfflinePlayer)sender,list,args);			
		} else {
			for(String message:list){
				String msg = message;
				if(args != null)
					msg = String.format(message,args);
			Util.send(sender, msg);
			}
		}
	}

	/**
	 * 
	 * @param player
	 * @param list of messages
	 * @param args to replace 
	 */
	public static void sendOrMail(OfflinePlayer player, List<String> list, Object... args) {
		if(player.isOnline()){
			for(String message:list){
				String msg = message;
				if(args != null)
					msg = String.format(message,args);
				((Player)player).sendMessage(msg);
			}			
		} else {
			for(String message:list){
				String msg = message;
				if(args != null)
					msg = String.format(message,args);
				new MailPlayer(player.getName(),msg);
			}			
		}		
	}

	public static String[] makeNamesList(List<OfflinePlayer> invites) {
		String[] list = new String[invites.size()];
		for(int i = 0;i<invites.size();i++){
			list[i] = invites.get(i).getName();
		}
		return list;
	}

	public static List<String> getPlayerInvites(OfflinePlayer p) {
		List<String> list = new ArrayList<String>();
		for(Party party:Main.partys){
			if(party.getInvites().contains(p)) list.add(party.getName());
		}
		return list;
	}

	public static void removeInvites(OfflinePlayer player) {
		for(Party party:Main.partys){
			if(party.getInvites().contains(player))
				party.getInvites().remove(player);
		}		
	}

	public static String getBooleanColor(boolean ff) {
		if(ff) return ChatColor.GREEN+"Enabled";
		else return ChatColor.RED+"Disabled";
	}

	public static String longToDate(long since) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy MMM dd HH:mm:ss");	
		Date date = new Date(since);
		return sdf.format(date);

	}

	public static String[] makeNamesListPlayers(List<Player> list) {
		List<OfflinePlayer> temp =new ArrayList<OfflinePlayer>();
		temp.addAll(list);
		return makeNamesList(temp);

	}
	
}
