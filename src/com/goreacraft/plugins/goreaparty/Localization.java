package com.goreacraft.plugins.goreaparty;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.configuration.file.YamlConfiguration;

public class Localization {
	
	private static File localizationFile; 
	//private static YamlConfiguration localization = new YamlConfiguration();
	static HashMap<String,List<String>> localization = new HashMap<String,List<String>>();
	public static void load(){
		localizationFile = new File(Main.instance.getDataFolder(), Main.localizationfile);
		if(!localizationFile.exists())
			Main.instance.saveResource("Localization.yml", true);
		YamlConfiguration loc = YamlConfiguration.loadConfiguration(localizationFile);
		for(String key: loc.getKeys(false)){
			localization.put(key, loc.getStringList(key));
		}
	}

	public static String get(String string){
		List<String> list = getList(string);
		if( list.size()>0)
			return list.get(0); 
			else {
			Main.instance.logger.severe("No entry for '"+string+"' in Localization.yml");
			}
		return string;
	}
	public static List<String> getList(String string){
		if(localization.keySet().contains(string))
		return localization.get(string);
		else{
			if(Main.debug)Main.instance.logger.warning("No message for '"+string+"' in Localization.yml");
			return new ArrayList<String>();
		}
	}

}
