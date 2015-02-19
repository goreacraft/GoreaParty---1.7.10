package com.goreacraft.plugins.goreaparty;

import org.bukkit.ChatColor;

public enum Role {
	leader(90, ChatColor.RED),
	officer(50, ChatColor.YELLOW),
	member(10, ChatColor.GREEN);
	
	private int power;
	private ChatColor color;
	Role(int power){
		this.setPower(power);
		//this.color=ChatColor.RESET;
	}
	Role(int power, ChatColor color){
		this.setPower(power);
		this.setColor(color);
	}
	public int getPower() {
		return power;
	}
	public void setPower(int power) {
		this.power = power;
	}
	
	public ChatColor getColor() {
		return color;
	}
	public void setColor(ChatColor color) {
		this.color = color;
	}
	
	public Role getNext(){
		switch(this){
		case leader:
			return leader;
		case member:
			return officer;
		case officer:
			return leader;
		default:
			return member;		
		}
	}
	public Role getPrev() {
		switch(this){
		case leader:
			return officer;
		case member:
			return member;
		case officer:
			return member;
		default:
			return member;		
		}
	}
}
