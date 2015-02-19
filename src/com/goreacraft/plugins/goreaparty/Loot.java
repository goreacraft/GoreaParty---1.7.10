package com.goreacraft.plugins.goreaparty;


public enum Loot {
	random("Random"),
	leader("Leader"),
	byturn("ByTurn"),
	ffa("FreeForAll");	
	
	private String name;
	
	Loot(String name){
		this.setName(name);
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public Loot getNext(){	
		switch(this){
		case random:
			return leader;
		case leader:
			return byturn;
		case byturn:
			return ffa;
		case ffa:
		default:
			return random;		
		}
	}
}
