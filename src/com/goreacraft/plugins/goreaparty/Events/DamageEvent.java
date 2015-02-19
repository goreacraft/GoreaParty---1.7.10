package com.goreacraft.plugins.goreaparty.Events;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.goreacraft.plugins.goreaparty.Main;
import com.goreacraft.plugins.goreaparty.Party;
import com.goreacraft.plugins.goreaparty.Util;

public class DamageEvent implements Listener {
	@EventHandler//(priority=EventPriority.HIGHEST)
	   public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent e)
	   {
		if(Main.ff)
			if(e.getDamager() instanceof Player && e.getEntity() instanceof Player)
			{
				Player p = (Player) e.getDamager();
				Player t = (Player) e.getEntity();
				Party pp = Util.getParty(p);
				
				if(pp == null) return;
				//System.out.println(pp.getName());
				Party pt = Util.getParty(t);
				if(pt == null) return;
				//System.out.println(pt.getName());
					if(pp.equals(pt) && !pp.getFF())
					{
						e.setCancelled(true);
						p.sendMessage(ChatColor.RED + t.getName() + " is in your party");
					
					}

			}
	   }
}
