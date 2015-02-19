package com.goreacraft.plugins.goreaparty;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.YamlConfiguration;

public class Localization {
	
	private static File localizationFile; 
	private static YamlConfiguration localization = new YamlConfiguration();
	public static void load(){
		localizationFile = new File(Main.instance.getDataFolder(), Main.localizationfile);
		if(!localizationFile.exists())
			Main.instance.saveResource("Localization.yml", true);
		localization = YamlConfiguration.loadConfiguration(localizationFile);
	}

	/*public static String get(String string){
		List<String> list = getList(string);
		return list.size()>0 ? list.get(0) : "No localization entry for "+ string;
	}*/
	public static List<String> getList(String string){
		if(localization.getKeys(false).contains(string))
		return localization.getStringList(string);
		else{
			if(Main.debug)Main.instance.logger.warning("No message for '"+string+"' in Localization.yml");
			return new ArrayList<String>();
		}
	}

}
