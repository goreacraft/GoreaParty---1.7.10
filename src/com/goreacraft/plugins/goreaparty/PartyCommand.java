package com.goreacraft.plugins.goreaparty;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.goreacraft.plugins.goreaparty.Events.ChatEvent;

public class PartyCommand implements CommandExecutor {

	public PartyCommand(Main plugin) {
		plugin = Main.instance;
		}

	boolean save = Main.instance.getConfig().getBoolean("Save to file on every change",false);
	
	
	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if(args.length>=1)
			{
				if(args[0].equalsIgnoreCase("debug"))
				{
					if(!isAllowed(sender)) return true;
					//if(!p.isOp()){
					if(isAllowed(sender)){
						
						Util.send(sender, "How did you found about this command? This should be a secret. Shhhh...");
						return true;
					}
					
					Util.send(sender, "Saved to file Debug.yml ");
					return true;
					
				}else
//TODO ======================================================================================================= LEAVE
			if(args[0].equalsIgnoreCase("leave"))
				{
				Player p = getPlayer(sender); if( p==null){Util.send(sender, "Player only command"); return true;}
				
				Party party = Util.getParty(p);
					if(party!=null)
					{
						party.removeMember(p);
						return true;
					} else {
						p.sendMessage("You are not in a party");
						//not in party
							}
					return true;
				}else
//TODO ======================================================================================================= HELP
			if(args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?"))
			{
				getLogo(sender);
				showpartyhelp(sender);
				return true;
			} else
//TODO ======================================================================================================= RELOAD
			if(args[0].equalsIgnoreCase("reload"))
			{
				if(!isAllowed(sender)) return true;
				Main.instance.reloadConfig();
				Main.loadconfigs();
				//Main.instance.loadPartys();
				Util.send(sender, ChatColor.GREEN +"[GoreaParty]" + ChatColor.RESET+ "Plugin configs reloaded from files!");
				return true;
				
			} else
				if(args[0].equalsIgnoreCase("load"))
				{
					if(!isAllowed(sender)) return true;
									
						Main.instance.loadPartys();
					
					Util.send(sender, ChatColor.GREEN +"[GoreaParty]" + ChatColor.RESET+ "Partys loaded from file!");
					return true;
					
				} else
			if(args[0].equalsIgnoreCase("save"))
			{
				if(!isAllowed(sender)) return true;
								
					Main.instance.savePartysToFile();
				
				Util.send(sender, ChatColor.GREEN +"[GoreaParty]" + ChatColor.RESET+ "Partys saved to file!");
				return true;
				
			} else
//TODO ======================================================================================================= CHAT
				if(args[0].equalsIgnoreCase("chat") || args[0].equalsIgnoreCase("c"))
				{
					Player p = getPlayer(sender); if( p==null){Util.send(sender, "Player only command"); return true;}
					if(Util.getParty(p) != null)
					{
						if(!ChatEvent.chat.contains(p.getUniqueId()))
						{
							ChatEvent.chat.add(p.getUniqueId());
							p.sendMessage(ChatColor.DARK_AQUA + "Party chat enabled");
							return true;
						} else {
							ChatEvent.chat.remove(p.getUniqueId());
							p.sendMessage(ChatColor.DARK_AQUA + "Party chat disabled");
							return true;
						}
						
					} else { 
						Util.sendOrMail(sender, Localization.getList("You Not In Party"), p.getName(),p.getName());
						return true;
					}
					
				} else
//TODO ======================================================================================================= LIST
				if(args[0].equalsIgnoreCase("list"))
				{
					if(Main.partys.size()>0)
					{
						Util.send(sender,ChatColor.YELLOW + "List of all partys registered:");
						for(Party g : Main.partys)
						{								
							Util.send(sender, "Party: "+ChatColor.DARK_AQUA + g.getName() +ChatColor.RESET+ " Members: " + g.getMembersByColor());						
						}
						List<String> rolescolors = new ArrayList<String>();
						for(Role role:Role.values()){
							rolescolors.add(role.getColor()+role.toString().toUpperCase());
						}
						
						Util.send(sender, rolescolors.toString());
					} else {Util.send(sender, "No partys registered");}					
				} else
//TODO ======================================================================================================= RANKS
				if(args[0].equalsIgnoreCase("ranks"))
				{
					Util.send(sender,ChatColor.YELLOW + "List of all party ranks available:");					
					List<String> rolescolors = new ArrayList<String>();
						for(Role role:Role.values()){
							rolescolors.add(role.getColor()+role.toString().toUpperCase());
						}
						
						Util.send(sender, rolescolors.toString());
									
				}else
//TODO ======================================================================================================= INFO
				if(args[0].equalsIgnoreCase("info"))
				{
					Party party = null;
					if(args.length==1)
					{	
						Player p = getPlayer(sender); if( p==null){Util.send(sender, "Player only command"); return true;}
						
						party = Util.getParty(p);
						if(party==null)
						{
							Util.sendOrMail(sender, Localization.getList("You Not In Party"), p.getName(),p.getName());
							return true;
						} 
						//return true;
					} else
					if(args.length==2)
					{
						if(Util.getAllPartyNames().contains(args[1]))
						{
							party = Util.getParty(args[1]);							
						} else {
							Util.send(sender, "There is no party with this name, see \"/p list\"");
							return true;
							}
					} else {
						Util.send(sender, "Use \"/p info <partyname>\" - partyname is optional if you want a particular one");
						return true;
					}
					
					Util.send(sender, ChatColor.GRAY + "" + ChatColor.ITALIC + "Party name: " + ChatColor.YELLOW + party.getName()+ChatColor.GRAY + "" + ChatColor.ITALIC +  " Players [online/total/max]: "+ ChatColor.RESET+"["+ party.getOnlineMembers().size()+"/"+party.getMembers().size()+"/"+Main.mps+"]");
					Util.send(sender, ChatColor.GRAY + "" + ChatColor.ITALIC + "Loot: " +ChatColor.DARK_PURPLE+ party.getLootOption().getName() +ChatColor.GRAY + " FriendlyFire: "+ ChatColor.DARK_PURPLE + Util.getBooleanColor(party.getFF())+ChatColor.GRAY + " ShareXp: "+ ChatColor.DARK_PURPLE + Util.getBooleanColor(party.getXp()));
					for(Role rank:Role.values()){
						Util.send(sender, ChatColor.GRAY + "" + ChatColor.ITALIC + rank.toString().toUpperCase()+": " + StringUtils.join(party.getOfflinePlayersByRankAndColor(rank).toArray()));
					}
					//Util.send(sender, ChatColor.GRAY + "" + ChatColor.ITALIC + "Leader: " + party.getLeader().getName());
					//Util.send(sender, ChatColor.GRAY + "" + ChatColor.ITALIC + "Officers: " + StringUtils.join(Util.makeNamesList(party.getOfflinePlayersByRank(Role.officer))));
					//Util.send(sender, ChatColor.GRAY + "" + ChatColor.ITALIC + "Members: " + StringUtils.join(Util.makeNamesList(party.getOfflinePlayersByRank(Role.member))));
					Util.send(sender, ChatColor.GRAY + "" + ChatColor.ITALIC + "Invites: " + StringUtils.join(Util.makeNamesList(party.getInvites())));
					Util.send(sender, ChatColor.GRAY + "" + ChatColor.ITALIC + "Founded on: " + Util.longToDate(party.getSince()));
					
					return true;
				} else
//TODO ======================================================================================================= INVITE
				if(args[0].equalsIgnoreCase("invite") || args[0].equalsIgnoreCase("i"))
				{
					Player p = getPlayer(sender); if( p==null){Util.send(sender, "Player only command"); return true;}
					
					if(args.length==1)return false;
					
					Party party = Util.getParty(p);
					if(party==null)
					{
						Util.sendOrMail(sender, Localization.getList("Player Not In Party"), p.getName(),p.getName());
						return true;
					} 
					if(party.getParty().get(p).getPower()<=10){
						Util.sendOrMail(sender, Localization.getList("No Invite Permission"), p.getName(), party.getName());
						return true;
					}
					if(party.getMembers().size()>=Main.psize){
						Util.sendOrMail(sender, Localization.getList("Max Party Size Reached"), p.getName(), party.getName(), party.getMembers().size());
						return true;
					}
					OfflinePlayer target =Util.getOfflinePlayer(args[1]);
					if(target==null){
						Util.sendOrMail(sender, Localization.getList("No Player With This Name"), p.getName(), args[1]);
						return true;
					}
					Party tparty = Util.getParty(target);
					if(tparty != null){
						Util.sendOrMail(sender, Localization.getList("Player Already In Party"),target.getName(), tparty.getName());						
						return true;
					}
					if(party.getInvites().contains(target)){
						Util.sendOrMail(sender, Localization.getList("Party Invite Exists"),target.getName(), p.getName(), party.getName());
						return true;
					}
					if(party.getInvites().size()>=Main.maxpartyinv){
						Util.sendOrMail(sender, Localization.getList("Party Invite Max Reached"),p.getName(), party.getName(), Main.maxpartyinv, party.getInvites().size());
						return true;
					}
													
						Util.sendOrMail(target, Localization.getList("Party Invite"),target.getName(), p.getName(), party.getName());
							
						party.getInvites().add(target);
						Util.sendOrMail(sender,Localization.getList("Party Invite Send"),target.getName(), p.getName(), party.getName());
						//p.sendMessage("Party invitation send to " +target.getName());
					return true;
				} else

//TODO ======================================================================================================= PROMOTE
				if(args[0].equalsIgnoreCase("promote"))
				{
					Player p = getPlayer(sender); if( p==null){Util.send(sender, "Player only command"); return true;}
					if(args.length==1)return false;					
					
					Party party = Util.getParty(p);
					if(party==null)
					{
						Util.sendOrMail(sender, Localization.getList("You Are Not In Party"), p.getName());
						return true;
					}
					
					OfflinePlayer target =Util.getOfflinePlayer(args[1]);
					
					if(target==null){
						Util.sendOrMail(sender, Localization.getList("No Player With This Name"), p.getName(), args[1]);
						return true;
					}
					if(p.equals(target)){
						Util.send(sender, "You can't promote yourself");
						return true;
					}
					Party tparty = Util.getParty(target);
					if(tparty == null){
						Util.sendOrMail(sender, Localization.getList("Player Not In Party"), p.getName(), target.getName());						
						return true;
					}
					if(!party.equals(tparty)){
						Util.sendOrMail(sender, Localization.getList("Player Not In Your Party"), p.getName(),party.getName(), target.getName(), tparty.getName());						
						return true;					
					}
					Role next = party.getRank(target).getNext();
					if(party.getPower(p)<next.getPower()){
						Util.sendOrMail(sender, Localization.getList("No Promote Permission"), p.getName(), party.getName(),party.getRank(target), next, target.getName());
						return true;
					}
					if(next.equals(Role.leader)){
						if(args.length==3){
							if(args[2].equals("leader")){
								if(!party.getRank(p).equals(Role.leader)){
									Util.sendOrMail(sender, Localization.getList("Fail Rank Promote Rank"), p.getName(), party.getRank(p).toString(), party.getName(), next.toString());
									return true;
								}
								//exchange leader
								party.addMember(p, Role.officer);
								party.addMember(target, next);
								return true;
							}							
						}
						Util.sendOrMail(sender, Localization.getList("How To Promote Leader"), p.getName(), party.getName(), target.getName());						
						return true;
					}
					party.addMember(target, next);
					return true;
				} else
//TODO ======================================================================================================= DEMOTE
				if(args[0].equalsIgnoreCase("demote"))
				{
					Player p = getPlayer(sender); if( p==null){Util.send(sender, "Player only command"); return true;}
					if(args.length==1)return false;					
					
					Party party = Util.getParty(p);
					if(party==null)
					{
						Util.sendOrMail(sender, Localization.getList("You Are Not In Party"), p.getName());
						return true;
					}
					
					OfflinePlayer target =Util.getOfflinePlayer(args[1]);
					
					if(target==null){
						Util.sendOrMail(sender, Localization.getList("No Player With This Name"), p.getName(), args[1]);
						return true;
					}
					if(p.equals(target)){
						Util.send(sender, "You can't demote yourself");
						return true;
					}
					Party tparty = Util.getParty(target);
					if(tparty == null){
						Util.sendOrMail(sender, Localization.getList("Player Not In Party"), p.getName(), target.getName());						
						return true;
					}
					if(!party.equals(tparty)){
						Util.sendOrMail(sender, Localization.getList("Player Not In Your Party"), p.getName(),party.getName(), target.getName(), tparty.getName());						
						return true;					
					}
					Role prev = party.getRank(target).getPrev();
					if(party.getPower(p)<=party.getPower(target)){
						Util.sendOrMail(sender, Localization.getList("No Demote Permission"), p.getName(), party.getName(),party.getRank(target), prev, target.getName());
						return true;
					}
					if(party.getRank(target).equals(prev)){
						Util.sendOrMail(sender, Localization.getList("No Point Demote"), p.getName(), party.getName(),party.getRank(target), prev, target.getName());
						return true;
					}

					party.addMember(target, prev);
					return true;
				} else
//TODO ======================================================================================================= LOOT
				if(args[0].equalsIgnoreCase("loot"))
				{
					Player p = getPlayer(sender); if( p==null){Util.send(sender, "Player only command"); return true;}
					Party party = Util.getParty(p);
					if(party==null)
					{
						Util.sendOrMail(sender, Localization.getList("You Are Not In Party"), p.getName());
						return true;
					} 
					if(party.getPower(p)<Role.officer.getPower()){
						Util.sendOrMail(sender, Localization.getList("No Loot Permission"), p.getName(), party.getName());
						return true;
					}
					party.sendMessageAll(Localization.getList("Party Loot Change"),p.getName(), party.getLootOption().getName(), party.getLootOption().getNext().getName(), party.getName());
					party.setLootOption(party.getLootOption().getNext());
					return true;
				} else
//TODO ======================================================================================================= CHANGE FF				
				if(args[0].equalsIgnoreCase("ff"))
				{
					Player p = getPlayer(sender); if( p==null){Util.send(sender, "Player only command"); return true;}
					Party party = Util.getParty(p);
					if(party==null)
					{
						Util.sendOrMail(sender, Localization.getList("You Are Not In Party"), p.getName());
						return true;
					} 
					if(party.getPower(p)<Role.officer.getPower()){
						Util.sendOrMail(sender, Localization.getList("No FF Permission"), p.getName(), party.getName());
						return true;
					}
					boolean ff = party.getFF();
					party.sendMessageAll(Localization.getList("Party FF Change"),p.getName(), party.getFF(), !ff, party.getName());
					party.setFF(!ff);
					return true;
				} else
//TODO ======================================================================================================= CHANGE XP				
				if(args[0].equalsIgnoreCase("xp"))
				{
					Player p = getPlayer(sender); if( p==null){Util.send(sender, "Player only command"); return true;}
					Party party = Util.getParty(p);
					if(party==null)
					{
						Util.sendOrMail(sender, Localization.getList("You Are Not In Party"), p.getName());
						return true;
					} 
					if(party.getPower(p)<Role.officer.getPower()){
						Util.sendOrMail(sender, Localization.getList("No XP Permission"), p.getName(), party.getName());
						return true;
					}
					boolean xp = party.getXp();
					party.sendMessageAll(Localization.getList("Party XP Change"),p.getName(), party.getXp(), !xp, party.getName());
					party.setXp(!xp);
					return true;
				} else
//TODO ======================================================================================================= RENAME				
				if(args[0].equalsIgnoreCase("rename"))
				{
					Player p = getPlayer(sender); if( p==null){Util.send(sender, "Player only command"); return true;}
					Party party = Util.getParty(p);
					if(party==null)
					{
						Util.sendOrMail(sender, Localization.getList("You Are Not In Party"), p.getName());
						return true;
					} 
					if(party.getPower(p)< 75){
						Util.sendOrMail(sender, Localization.getList("No Rename Permission"), p.getName(), party.getName());
						return true;
					}
					
					if(args.length==1)return false;
					
					String partyname = args[1];
					if(Util.getAllPartyNames().contains(partyname)){
						Util.send(sender, ChatColor.RED+"There is a party existing with name: "+ partyname);
						return true;
					}
					if((partyname.length()>=Main.psize))
					{
						p.sendMessage("Party name not allowed longer then " + Main.psize);
					return true;						
					} 
					
					party.sendMessageAll(Localization.getList("Party Name Change"), p.getName(), party.getName(), partyname);
					party.setName(partyname);
					return true;
				} else
//TODO ======================================================================================================= KICK
				if(args[0].equalsIgnoreCase("kick"))
				{
					Player p = getPlayer(sender); if( p==null){Util.send(sender, "Player only command"); return true;}
					if(args.length==1)return false;					
					
					Party party = Util.getParty(p);
					if(party==null)
					{
						Util.sendOrMail(sender, Localization.getList("You Are Not In Party"), p.getName());
						return true;
					}
					
					OfflinePlayer target =Util.getOfflinePlayer(args[1]);
					
					if(target==null){
						Util.sendOrMail(sender, Localization.getList("No Player With This Name"), p.getName(), args[1]);
						return true;
					}
					if(p.equals(target)){
						Util.send(sender, "You can't kick yourself");
						return true;
					}
					Party tparty = Util.getParty(target);
					if(tparty == null){
						Util.sendOrMail(sender, Localization.getList("Player Not In Party"), p.getName(), target.getName());						
						return true;
					}
					if(!party.equals(tparty)){
						Util.sendOrMail(sender, Localization.getList("Player Not In Your Party"), p.getName(),party.getName(), target.getName(), tparty.getName());						
						return true;					
					}
					if(party.getPower(p)<=party.getPower(target)){
						Util.sendOrMail(sender, Localization.getList("Fail Rank Kick Rank"), p.getName(), party.getPower(p), target.getName(), party.getPower(target));
						return true;
					}
					party.removeMember(target);
					return true;
				} else
//TODO ======================================================================================================= JOIN
				if(args[0].equalsIgnoreCase("join") ||args[0].equalsIgnoreCase("j"))
				{
					Player p = getPlayer(sender); if( p==null){Util.send(sender, "Player only command"); return true;}
					
					Party party = Util.getParty(p);
						if( party != null){
							Util.sendOrMail(sender, Localization.getList("Player Already In Party"), p.getName(), party.getName());
							return true;
						}
					List<String> invites = Util.getPlayerInvites(p);
					
					if(args.length==1)
					{
						if(invites.isEmpty()) {
							Util.sendOrMail(sender, Localization.getList("No Party Invitations"), p.getName());
							return true;						
						}
						Util.sendOrMail(sender, Localization.getList("List Party Invitations"), p.getName(), ChatColor.YELLOW + StringUtils.join(invites.toArray()));						
						return true;
					} else 
					if(args.length==2)
					{
						String partyname = args[1];
						party = Util.getParty(partyname);
						if(party==null){
							Util.sendOrMail(sender, Localization.getList("No Party With This Name"), p.getName(), partyname, Util.getAllPartyNames());
							return true;
						}
						
						if(!party.getInvites().contains(p)){
							Util.sendOrMail(sender, Localization.getList("No Party Invitations From"), p.getName(), invites.toString());
							return true;
						}
						if(party.getMembers().size()>=Main.psize){
							Util.sendOrMail(sender, Localization.getList("Max Party Size Reached"), p.getName(), party.getName(), party.getMembers().size());
							return true;
						}
						
						party.addMember(p, Role.member);
						return true;
					}
					return false;
				} else
//TODO ======================================================================================================= CREATE
				if(args[0].equalsIgnoreCase("create"))
				{
					Player p = getPlayer(sender); if( p==null){Util.send(sender, "Player only command"); return true;}
					String perm = Localization.get("Create Party Permission");
					if(!p.hasPermission(perm) && !p.isOp())
					{
						Util.sendOrMail(sender, Localization.getList("No Party Create Permission"), p.getName(), perm);
						return true;
						
					}
					if(args.length>=2)
					{
						Party party1 = Util.getParty(p);
						//System.out.println("Party: " + party1);
						if( party1 == null)
						{
							//System.out.println("1");
							String partyname = args[1];
							if(Util.getAllPartyNames().contains(partyname)){
								Util.send(sender, ChatColor.RED+"There is a party existing with name: "+ partyname);
								return true;
							}
							if(!(partyname.length()>=Main.psize))
							{
								Party party = new Party();
								party.addMember((OfflinePlayer)p, Role.leader);
								party.setName(partyname);
								party.setSince(Calendar.getInstance().getTimeInMillis());
								Main.partys.add(party);
								
									if(save){										
										Main.instance.savePartysToFile();										
									}
									p.sendMessage("Party created");
									return true;
									
								
							} else p.sendMessage("Party name not allowed longer then " + Main.psize);
						}else p.sendMessage("You are already in a party");
						
					}
					
					return true;
				}
				
			}
		
	return false;
}
		
		
	private static Player getPlayer(CommandSender sender) {
		if(sender instanceof Player)
			return (Player)sender;
		else
		return null;
	}


	private static boolean isAllowed(CommandSender sender) {
		if(sender instanceof Player){
			if(((Player)sender).isOp() || ((Player)sender).hasPermission(Localization.get("Op Party Permission"))) return true;
			else {
				Util.send(sender, Localization.get("Message No Command Permission"));
				return false;
			}
		} 		
		return true;
	}

	
	private static final void getLogo(CommandSender sender){
		if(getPlayer(sender) == null) return;
		Util.send(sender, ChatColor.YELLOW + "......................................................." + ChatColor.GOLD + " Plugin made by: "+ ChatColor.YELLOW + ".......................................................");
		Util.send(sender, ChatColor.YELLOW + "     o   \\ o /  _ o              \\ /               o_   \\ o /   o");
		Util.send(sender, ChatColor.YELLOW + "    /|\\     |      /\\   __o        |        o__    /\\      |     /|\\");
		Util.send(sender, ChatColor.YELLOW + "    / \\   / \\    | \\  /) |       /o\\       |  (\\   / |    / \\   / \\");
		Util.send(sender, ChatColor.YELLOW + "......................................................." + ChatColor.GOLD + ChatColor.BOLD + " GoreaCraft  "+ ChatColor.YELLOW + ".......................................................");
	}
	private static final void showpartyhelp(CommandSender sender) {	
		Util.send(sender, ChatColor.GRAY+""+ChatColor.ITALIC + "\"[]\" is mandatory and \"<>\" is optional " );     	
    	
		Util.send(sender, ChatColor.DARK_AQUA + "/p create [party_name] : " + ChatColor.RESET + StringUtils.join(Localization.getList("create").toArray()));    	
		Util.send(sender, ChatColor.DARK_AQUA + "/p invite [player] : " + ChatColor.RESET + StringUtils.join(Localization.getList("invite").toArray()));
		Util.send(sender, ChatColor.DARK_AQUA + "/p join [party_name] : " + ChatColor.RESET + StringUtils.join(Localization.getList("join").toArray()));
		Util.send(sender, ChatColor.DARK_AQUA + "/p chat : " + ChatColor.RESET + StringUtils.join(Localization.getList("chat").toArray()));
		Util.send(sender, ChatColor.DARK_AQUA + "/p list : " + ChatColor.RESET + StringUtils.join(Localization.getList("list").toArray()));
		Util.send(sender, ChatColor.DARK_AQUA + "/p ranks : " + ChatColor.RESET + StringUtils.join(Localization.getList("ranks").toArray()));
		Util.send(sender, ChatColor.DARK_AQUA + "/p info <party_name>: " + ChatColor.RESET + StringUtils.join(Localization.getList("info").toArray()));
		Util.send(sender, ChatColor.DARK_AQUA + "/p kick [player]: " + ChatColor.RESET + StringUtils.join(Localization.getList("kick").toArray()));
		Util.send(sender, ChatColor.DARK_AQUA + "/p leave : " + ChatColor.RESET + StringUtils.join(Localization.getList("leave").toArray()));
		Util.send(sender, ChatColor.DARK_AQUA + "/p promote [player]: " + ChatColor.RESET + StringUtils.join(Localization.getList("promote").toArray()));
		Util.send(sender, ChatColor.DARK_AQUA + "/p demote [player]: " + ChatColor.RESET + StringUtils.join(Localization.getList("demote").toArray()));    	 
		Util.send(sender, ChatColor.DARK_AQUA + "/p rename [new_party_name] : " + ChatColor.RESET + StringUtils.join(Localization.getList("rename").toArray()));  
		Util.send(sender, ChatColor.DARK_AQUA + "/p loot : " + ChatColor.RESET + StringUtils.join(Localization.getList("loot").toArray()));
		Util.send(sender, ChatColor.DARK_AQUA + "/p ff: " + ChatColor.RESET + StringUtils.join(Localization.getList("ff").toArray()));
		Util.send(sender, ChatColor.DARK_AQUA + "/p xp: " + ChatColor.RESET + StringUtils.join(Localization.getList("xp").toArray()));
		if(!isAllowed(sender)){
			Util.send(sender,ChatColor.ITALIC+""+ChatColor.GRAY+StringUtils.join(Localization.getList("OpHelp").toArray()));
			return;
		}
		Util.send(sender, ChatColor.DARK_AQUA + "/p reload : " + ChatColor.RESET + StringUtils.join(Localization.getList("reload").toArray()));
		Util.send(sender, ChatColor.DARK_AQUA + "/p load : " + ChatColor.RESET + StringUtils.join(Localization.getList("load").toArray()));
		Util.send(sender, ChatColor.DARK_AQUA + "/p save : " + ChatColor.RESET + StringUtils.join(Localization.getList("save").toArray()));
		
	}


}
