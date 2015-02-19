package com.goreacraft.plugins.goreaparty.Events;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import com.goreacraft.plugins.goreaparty.Loot;
import com.goreacraft.plugins.goreaparty.Main;
import com.goreacraft.plugins.goreaparty.Party;
import com.goreacraft.plugins.goreaparty.Util;

public class KillMobsEvent implements Listener {

	@EventHandler//(priority=EventPriority.HIGHEST)
	   public void onEntityDeathEvent(EntityDeathEvent e)
	   {
			if(e.getEntity().getKiller() instanceof Player)
			{	
				Player p = e.getEntity().getKiller();
				Party party = Util.getParty(p);
				if(party!=null )//&& party.getLootOption()!=Loot.ffa)
				{
					List<Player> list = party.getMembersNearby(p, Main.prad);
					// set XP
					if(party.getXp()){
							int xp = e.getDroppedExp();
							float xp1 =  (float) ((xp*(Main.xpm*list.size()))/100);
							float xp2 =   (float) ((xp*(Main.xpp))/100);
							//TOTEST NEARBY PLAYERS RANGE
							for(Player m: list)
							{
								float xp3 = (float) ((xp*Math.random())*Main.xpr)/100;
								m.giveExp(Math.round ((xp/party.getOnlineMembers().size())+ xp1 + xp2 + xp3));
								if(Main.debug)m.sendMessage("Xp total: "+ xp+ "/"+ list.size()+"="+String.valueOf(xp/list.size()) + "+ MembersNearBonus:" + xp1+ "+  PartyBonus:"+ xp2 + " Random:"+ xp3+ " = RawXp get:"+ ((xp/list.size())+ xp1 + xp2+xp3)+" rounded:"+Math.round((xp/list.size())+ xp1 + xp2+xp3));								
							}
							e.setDroppedExp(0);
						}
					
					// set LOOT	
					if(party.getLootOption()!=Loot.ffa){
						if(e.getDrops()!=null || e.getDrops().isEmpty()) return;
						
						party.giveItems(list,e.getDrops(), p);
						e.getDrops().clear();
					}
				} 
			}			
	   }
}
