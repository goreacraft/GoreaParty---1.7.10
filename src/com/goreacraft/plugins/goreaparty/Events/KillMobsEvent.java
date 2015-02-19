package com.goreacraft.plugins.goreaparty.Events;

import java.util.List;
import java.util.Random;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import com.goreacraft.plugins.goreaparty.Loot;
import com.goreacraft.plugins.goreaparty.Main;
import com.goreacraft.plugins.goreaparty.Party;
import com.goreacraft.plugins.goreaparty.Util;

public class KillMobsEvent implements Listener {

	@EventHandler(priority=EventPriority.HIGH)
	   public void onEntityDeathEvent(EntityDeathEvent e)
	   {
			if(e.getEntity().getKiller() instanceof Player && !(e.getEntity() instanceof Player) )
			{	
				Player p = e.getEntity().getKiller();
				Party party = Util.getParty((OfflinePlayer)p);
				if(party!=null )//&& party.getLootOption()!=Loot.ffa)
				{
					List<Player> list = party.getMembersNearby(p, Main.prad);
					//p.sendMessage("Nearby: "+StringUtils.join(Util.makeNamesListPlayers(list),","));
					// set XP
					if(party.getXp() && e.getDroppedExp()>0){
							double xp = (double) e.getDroppedExp();
							//p.sendMessage("Xp initial: "+xp);
							double xp1 =   ((xp/100*(Main.xpm*(list.size()-1))));
							double xp2 =     ((xp/100*Main.xpp));
							
							//int total = 0;
							for(Player m: list)
							{
								double xp3 = (xp/100*(new Random().nextDouble()*Main.xpr));
								double pxp=Math.round ((xp/list.size())+ xp1 + xp2 + xp3);
								m.giveExp((int) pxp);
								//total +=pxp;
																
								//p.sendMessage("Xp/size:"+(xp/list.size()) + " Xp1:"+xp1 + " Xp2:"+xp2 +" Xp3:"+xp3+ " =" + ((xp/list.size())+ xp1 + xp2 + xp3));
								//p.sendMessage(m.getName()+ ": "+pxp);
							}
							//p.sendMessage("Xp total: "+total);
							e.setDroppedExp(0);
						}
					
					// set LOOT	
					//p.sendMessage("Loot:"+ party.getLootOption().getName());
					//p.sendMessage("Drops :"+ (e.getDrops()==null) + " " + (e.getDrops().isEmpty()) + " "+(e.getDrops()==null || e.getDrops().isEmpty()));
					if(party.getLootOption()!=Loot.ffa){
						if(e.getDrops()==null || e.getDrops().isEmpty()) return;
						//p.sendMessage("Nearby: "+StringUtils.join(Util.makeNamesListPlayers(list),","));
						party.giveItems(list,e.getDrops(), p);
						e.getDrops().clear();
						//p.sendMessage("Drops:"+ e.getDrops().size());
					}
				} 
			}			
	   }
}
