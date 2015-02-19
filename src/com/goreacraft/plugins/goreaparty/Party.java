package com.goreacraft.plugins.goreaparty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.goreacraft.plugins.goreaparty.Events.ChatEvent;

public class Party {

	private HashMap<OfflinePlayer, Role> party = new HashMap<OfflinePlayer, Role>();
	private String name;
	private Loot loot = Loot.random;
	private boolean ff = false;
	private boolean xp = true;
	private long since;
	private List<OfflinePlayer> invites = new ArrayList<OfflinePlayer>();
	
	public Party(){		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<OfflinePlayer> getMembers() {
		return party.keySet();
	}
	public List<Player> getOnlineMembers() {
		List<Player> list = new ArrayList<Player>();
		for(OfflinePlayer p:getMembers())
			if(p.isOnline()) list.add(p.getPlayer());
		return list;
	}
	public HashMap<OfflinePlayer, Role> getParty() {
		return this.party;
	}
	public List<OfflinePlayer> getInvites(){
		return this.invites;
	}

	public void addMember(OfflinePlayer player, Role role) {
		getParty().put(player, role);
		if(invites.contains(player)) {
			//new member join
			Util.removeInvites(player);
			sendMessageAll(Localization.getList("Party New member Message"),player.getName(),role, this.getName(), this.party.size(), Main.mps);
		}
	}
	public void removeMember(OfflinePlayer player) {
		//TODO if leader what to do?
		getParty().remove(player);
		
		if(getMembers().size()==0){
			
			Main.partys.remove(this);
			Util.sendOrMail(player,Localization.getList("Party Ruined"), player.getName(), this.getName());
			if(Main.debug)Main.instance.logger.fine("["+Main.instance.getDescription().getName()+"]" + " Party:"+this.getName() + " disbanded");
			return;
		}
		
		if(ChatEvent.chat.contains(player.getUniqueId())) ChatEvent.chat.remove(player.getUniqueId());
		
		if(getLeader()==null){
			OfflinePlayer newleader = null;
			List<OfflinePlayer> officers = getOfflinePlayersByRank(Role.officer);
			List<OfflinePlayer> members = getOfflinePlayersByRank(Role.member);
			if(!officers.isEmpty()){
				int i = (int)(Math.random()*officers.size());
				newleader = officers.get(i);				
				party.put(newleader, Role.leader);				
			} else			
			if(!members.isEmpty()){
				int i = (int)(Math.random()*members.size());
				newleader = members.get(i);				
				party.put(newleader, Role.leader);
			}

			// %1$s newleader name, %2$s old leader name, %3$s party, %4$s party size
			sendMessageAll(Localization.getList("Leader Left Party"),newleader.getName(), player.getName(), name, this.party.size());
			
		}
		// %1$s player name, %2$s party name, %3$s party size
		sendMessageAll(Localization.getList("Member Party Leave"), player.getName(), this.getName(), this.party.size());
		
		Util.sendOrMail(player,Localization.getList("Party Leave"), player.getName(), this.getName(), this.party.size());
	}
	

	public void sendMessageAll(List<String> list,Object... args) {
		for(String message:list){
		String msg = String.format(message,args);
			for(Player p:getOnlineMembers()){
				p.sendMessage(msg);	
			}
		}
	}
	public void sendMessageAll(String message,Object... args) {
		String msg = message;
		if(args!=null)
			msg = String.format(message,args);
		for(Player p:getOnlineMembers()){
			p.sendMessage(msg);	
		}
		
	}

	public Loot getLootOption() {
		return loot;
	}

	public void setLootOption(Loot loot) {		
		this.loot = loot;
		
	}

	/**
	 * 
	 * @return player leader or null
	 */
	public OfflinePlayer getLeader() {
		for(OfflinePlayer p:party.keySet()){
			if(party.get(p).equals(Role.leader))
				return p;
		}
		return null;
	}

	
	public boolean giveItems(List<Player> plist, List<ItemStack> list, Player damager){
		
		switch(loot){
		case byturn:
			giveItems2(plist,list);
			break;
		case leader:
			//if leader near
			OfflinePlayer offleader = this.getLeader();
			Player winer = null;
			
			if(list.contains(offleader)){
				winer = ((Player)offleader);
			} else winer = damager;
			
			HashMap<Integer, ItemStack> rest = winer.getInventory().addItem((ItemStack[]) list.toArray());
					if(!rest.isEmpty()) 
						for(int i:rest.keySet() )
							winer.getWorld().dropItem(winer.getLocation(), rest.get(i));
			break;
		case random:
			giveItems2(getOnlinePlayersByRank(Role.member),list);
			break;
		case ffa:
		default:
			break;		
		}
		
		return true;
	}
	
	List<OfflinePlayer> getOfflinePlayersByRank(Role rank){
		List<OfflinePlayer> list = new ArrayList<OfflinePlayer>();
		for(OfflinePlayer p:getMembers())
			if(party.get(p).equals(rank)) list.add(p);
		Collections.shuffle(list);
		return list;
	}
	List<String> getOfflinePlayersByRankAndColor(Role rank){
		List<String> list = new ArrayList<String>();
		for(OfflinePlayer p:getMembers())
			if(party.get(p).equals(rank)) 
				{
				if(p.isOnline())
				list.add( ChatColor.GREEN + p.getName());
				else list.add( ChatColor.GRAY+ p.getName());
				}	
		return list;
	}
	List<Player> getOnlinePlayersByRank(Role rank){
		List<Player> list = new ArrayList<Player>();
		for(Player p:getOnlineMembers())
			if(party.get(p).equals(rank)) list.add(p);
		Collections.shuffle(list);
		return list;
	}
	
	void giveItems2(List<Player> players, List<ItemStack> list){
		for(Player p:players){
			if(list.isEmpty()) return;
			int i = (int)(Math.random()*list.size());
			 HashMap<Integer, ItemStack> remain = p.getInventory().addItem(list.get(i));
			 if(remain.isEmpty())list.remove(i);
			 else list.set(remain.keySet().iterator().next(), remain.values().iterator().next());
		}
		//if(!list.isEmpty())
	}

	public Role getRank(OfflinePlayer p) {		
		return party.get(p);
	}

	public boolean getFF() {
		return this.ff;
	}
	public void setFF(boolean ff) {		
		this.ff=ff;
	}
	public List<String> getMembersByColor() {
		List<String> list = new ArrayList<String>();
		for(OfflinePlayer e: this.party.keySet()){
			if(e.isOnline())
			list.add(this.party.get(e).getColor()+e.getName());
			else list.add(ChatColor.ITALIC+""+ this.party.get(e).getColor()+e.getName());
		}
		return list;
	}

	public int getPower(OfflinePlayer p) {
		return this.getParty().get(p).getPower();
	}

	public boolean getXp() {
		return xp;
	}

	public void setXp(boolean xp) {
		this.xp = xp;
	}

	public long getSince() {
		return since;
	}

	public void setSince(long since) {
		this.since = since;
	}
	public List<Player> getMembersNearby(Player center, double range){
		List<Player> on = new ArrayList<Player>();
			for(Player p : this.getOnlineMembers())					
				if(center.getWorld().equals(p.getWorld()))
					if(center.getLocation().distance(p.getLocation())<range)				
						on.add(p);
		return on;
	}

	public void giveXp(Player p, int droppedExp) {
		// TODO Auto-generated method stub
		
	}
}
