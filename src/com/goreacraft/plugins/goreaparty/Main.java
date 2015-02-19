package com.goreacraft.plugins.goreaparty;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import com.goreacraft.plugins.goreaparty.Events.ChatEvent;
import com.goreacraft.plugins.goreaparty.Events.DamageEvent;
import com.goreacraft.plugins.goreaparty.Events.KillMobsEvent;


public class Main extends JavaPlugin {
	
	
	public final Logger logger = Logger.getLogger("minecraft");

	public static File debugFile;
	
	public static File partysFile;
	//public static File playersFile;
	public static Main instance;
	public static int invitetime;
	public static int mps;
	public static int psize,maxpartyinv;
	public static int prad;
	public static boolean ff;
	public static double xpm;
	public static double xpp;
	public static double xpr;
	public static boolean debug;
	//public static YamlConfiguration players;
	//public static YamlConfiguration party;
	public boolean save;
	//private static File jsonfile; //= new File(Main.instance.getDataFolder(), "Partys.json");
	//public static HashMap<String,Integer> roles = new HashMap<String,Integer>();
	public static List<Party> partys = new ArrayList<Party>();

	public static String localizationfile;

	//public static JSONObject jsonpartys = new JSONObject();
	
	
	
	
	public void onEnable()	
    {
		instance=this;
		//jsonfile = new File(Main.instance.getDataFolder(), "Partys.json");
		PluginDescriptionFile pdfFile = this.getDescription();
    	this.logger.info(pdfFile.getName() + " Version " + pdfFile.getVersion() + " has been enabled! " + pdfFile.getWebsite());
    	//Localization.load();
		getConfig().options().copyDefaults(true);
      	getConfig().options().header("If you need help with this plugin you can contact goreacraft on teamspeak ip: goreacraft.com\n Website http://www.goreacraft.com");
      	saveConfig();

      	if(getConfig().getBoolean("Party chat enable"))getServer().getPluginManager().registerEvents(new ChatEvent(), this);
      	if(getConfig().getBoolean("EntityDeathEvent"))getServer().getPluginManager().registerEvents(new KillMobsEvent(), this);
      	if(getConfig().getBoolean("DamageEvent"))getServer().getPluginManager().registerEvents(new DamageEvent(), this);
      	partysFile = new File(getDataFolder(), "Partys.yml");
      	//playersFile = new File(getDataFolder(), "PlayersInParty.yml");
      	debugFile = new File(getDataFolder(), "Debug.yml");
      	loadconfigs();      	
      	commandhandler();
      	if(getConfig().getBoolean("Load partys", true)) loadPartys(); 
    }
	public static void loadconfigs()
	{
		localizationfile = instance.getConfig().getString("LocalizationFile");
		invitetime = instance.getConfig().getInt("Invite time");
		mps = instance.getConfig().getInt("Max party size");
		psize = instance.getConfig().getInt("Max party names size");
		prad = instance.getConfig().getInt("Max party radious");
		ff = instance.getConfig().getBoolean("EnableFriendlyFire",true);
		xpm = instance.getConfig().getDouble("Xp percent per party member");
		xpp = instance.getConfig().getDouble("Xp percent for party");
		xpr = instance.getConfig().getDouble("Xp random percent");
		debug = instance.getConfig().getBoolean("Debug", false);
		maxpartyinv = instance.getConfig().getInt("Max party invitations");
		Localization.load();
	}

	public void onDisable()
    {
		PluginDescriptionFile pdfFile = this.getDescription();
		if(savePartysToFile())this.logger.info(pdfFile.getName() + " saved partys file!");
		else this.logger.info(pdfFile.getName() + " saving partys file failed!");

    	this.logger.info(pdfFile.getName() + " Version " + pdfFile.getVersion() + " has been disabled!" + pdfFile.getWebsite());
    }
	
	
	

	public void setExecutor(String command, CommandExecutor ce){
		Bukkit.getPluginCommand(command).setExecutor(ce);
	}
	public void commandhandler(){		
		setExecutor("party", new PartyCommand(this));
		setExecutor("p", new PartyCommand(this));
	}
	
	static Player findPlayerByString(String name) 
	{
		for ( Player player : Bukkit.getServer().getOnlinePlayers())
		{
			if(player.getName().equals(name)) 
			{
				return player;
			}
		}		
		return null;
	}
	public void loadPartys() {
		if(!partys.isEmpty())
			partys.clear();
		YamlConfiguration data0 = YamlConfiguration.loadConfiguration(partysFile);
		for(String partyhash:data0.getKeys(false)){
			Party party = new Party();
			ConfigurationSection data = data0.getConfigurationSection(partyhash);
			ConfigurationSection playersdata = data.getConfigurationSection("players");
			if(playersdata.getKeys(false).isEmpty()) continue;
			for(String playeruuid:playersdata.getKeys(false)){
				String rank = playersdata.getString(playeruuid+".rank","member");
					party.addMember(Bukkit.getOfflinePlayer(UUID.fromString(playeruuid)), Role.valueOf(rank));
			}
			
			party.setSince(data.getLong("since", 0));
			party.setLootOption(Loot.valueOf(data.getString("loot", "random")));
			party.setXp(data.getBoolean("xp", true));
			party.setFF(data.getBoolean("ff", false));
			party.setName(data.getString("name",partyhash));
			partys.add(party);
		}
	}
	public boolean savePartysToFile() {
		//if(!partysFile.exists())
			try {
				partysFile.createNewFile();
			} catch (IOException e) {e.printStackTrace();}
		YamlConfiguration data0 = new YamlConfiguration();
		for(Party party:partys){
			//Random rand = new Random();
			//String u=(party.getName()+ "_"+ rand.nextInt(99999));
			String u = party.getName();
			data0.createSection(u+".players");
			ConfigurationSection data = data0.getConfigurationSection(u);
			ConfigurationSection partydata = data.getConfigurationSection("players");
			for(OfflinePlayer player:party.getMembers()){
				String uuid = player.getUniqueId().toString();
				partydata.createSection(uuid);
				partydata.set(uuid+".name", player.getName());
				partydata.set(uuid+".rank", party.getRank(player).toString());
				partydata.set(uuid+".chat", ChatEvent.chat.contains(player));
			}
			data.set("since", party.getSince());
			data.set("loot", party.getLootOption().toString());
			data.set("xp", party.getXp());
			data.set("ff", party.getFF());
			data.set("name", party.getName());
		}
		try {
			data0.save(partysFile);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
		
	}
	/*	@SuppressWarnings("unchecked")
	public void loadPartys2() {
		JSONParser parser = new JSONParser();
		JSONObject main = new JSONObject();
		try {
			main = (JSONObject) parser.parse(jsonfile.getPath());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		
		Iterator<String> it = main.keySet().iterator();
		while(it.hasNext()){
			String name = (String) it.next();
			JSONObject partydata = (JSONObject) main.get(name);
			Party party = new Party();
			party.setName(name);
			
			Iterator<String> uuids = partydata.keySet().iterator();
				while(uuids.hasNext()){
					String uuid = uuids.next();
					String rank = (String) ((JSONObject)partydata.get(uuid)).get("rank");
					OfflinePlayer op = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
						party.addMember(op, Role.valueOf(rank));
						if(getConfig().getBoolean("Party chat remember",false) && ((JSONObject)partydata.get(uuid)).containsKey("chat"))
							ChatEvent.chat.add(op.getUniqueId());
				}
		}
		
	}*/
	/*@SuppressWarnings("unchecked")
	public void savePartysToFile2() {
		JSONObject main = new JSONObject();
		for(Party party:partys){
			main.put(party.getName(), Util.makePartyJson(party));
		}
		if(!jsonfile.exists())
			jsonfile.createNewFile();
		FileWriter writer = null;
		try {
			writer = new FileWriter(jsonfile);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try {
			//main.writeJSONString(writer);
			writer.write(main.toJSONString());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			
			try {writer.flush();
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }	
	}*/
}
