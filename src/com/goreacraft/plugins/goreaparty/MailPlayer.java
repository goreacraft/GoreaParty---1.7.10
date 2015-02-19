package com.goreacraft.plugins.goreaparty;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class MailPlayer {
	private String p;
	private String msg;
	private int delay = 1;

	MailPlayer(String p,String msg){
		this.p=p;
		this.msg=msg;
		run();
	}
	
	public void setDelay(int delay){
		this.delay=delay;
	}
	private void run(){
		new BukkitRunnable() {
			public void run() {
				if(p!=null)
					Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), ("mail send "+p + " " + msg));
			}
		}.runTaskLaterAsynchronously(Main.instance, delay);
	}
/*	private String replace(String msg){
		String n = msg.replaceAll("<name>", this.p);
		return n;
	}*/
}
