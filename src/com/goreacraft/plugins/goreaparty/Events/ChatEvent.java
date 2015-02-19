package com.goreacraft.plugins.goreaparty.Events;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.goreacraft.plugins.goreaparty.Party;
import com.goreacraft.plugins.goreaparty.Util;

public class ChatEvent implements Listener {

	public static List<UUID> chat = new ArrayList<UUID>();
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	void onPlayerChat(AsyncPlayerChatEvent e) 
	{
		Player p = e.getPlayer();
		if(chat.contains(p.getUniqueId()))
		{
			Party party = Util.getParty(e.getPlayer());
			if(party!=null){
				if(party.getOnlineMembers().size()>1)
				{
						ChatColor add = party.getRank(p).getColor();
						//Make messages more dynamic with prefix
						party.sendMessageAll(ChatColor.DARK_AQUA+"" +ChatColor.ITALIC+ "[Party] "+ add +p.getName()+ ChatColor.RESET +": "+ e.getMessage(), p);
							
				} else {
					e.getPlayer().sendMessage(ChatColor.GRAY  + "Talking alone in party... Are you a lunatic?");
					e.getPlayer().sendMessage(ChatColor.DARK_AQUA + "Type \"/p chat\" to enable global chat");
				}			
				e.setCancelled(true);
			} else{
				chat.remove(e.getPlayer().getUniqueId());
			}
			
		}
	}
	
}
