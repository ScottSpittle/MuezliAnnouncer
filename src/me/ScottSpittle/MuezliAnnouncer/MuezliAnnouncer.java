/*
   Copyright 2013 Scott Spittle, James Loyd

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package me.ScottSpittle.MuezliAnnouncer;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public class MuezliAnnouncer extends JavaPlugin{

	public static MuezliAnnouncer plugin;
	public final BukkitLogger blo = new BukkitLogger(this);
	public final ConfigManager cfManager = new ConfigManager(this);
	public int counter = 0;

	@Override
	public void onDisable(){
		blo.enabled(false);
	}

	@Override
	public void onEnable(){
        ConfigManager cfManager = new ConfigManager(this);
		//set plugin to this instance.
		plugin = this;
	    //run BukkitLogger class on enable.
		blo.enabled(true);
		//create config if it doesn't exsist
		createConfig();
        this.scheduleAnnouncerTask(cfManager.getInitialDelay(), cfManager.getMessageDelay());
	}
    
	//Creates the config file ..
	public void createConfig(){
		File file = new File(getDataFolder()+File.separator+"config.yml");
		if(!file.exists()){
			getLogger().info("[MuezliPlugin] Creating default config file ...");
			saveDefaultConfig();
			getLogger().info("[MuezliPlugin] Config created successfully!");
		}else {
			getLogger().info("[MuezliPlugin] Config Already Exsists!");
		}
	}

	//increment announcements
    public void broadcastAnnounce(){
    	if (this.getServer().getOnlinePlayers().length >= 1){
    		ConfigManager cfManager = new ConfigManager(this);
    		if(counter >= cfManager.getMessages().length){
    			counter = 0;
    		}
    		doAnnounce(counter);
    		counter++;
    	}
    }

	//Announce the message parsed.
    public void doAnnounce(int index){
        if(index < cfManager.getMessages().length){
            getServer().broadcastMessage(cfManager.getMessagePrefix() + " " + colorize(cfManager.getMessages()[index]));
        }
    }

	//set a scheuled announcer task.
	private BukkitTask scheduleAnnouncerTask(long initialWait, long repeatInterval){
        return getServer().getScheduler().runTaskTimerAsynchronously(this, new Runnable() {
        	@Override
            public void run() {
                broadcastAnnounce();
            }
        }, initialWait, repeatInterval);
    }
	
	//replace all colorcodes with Bukkit ColorChat string.
	public static String colorize(String announce) {
		// Color codes
		announce = announce.replaceAll("&0",	ChatColor.BLACK.toString());
		announce = announce.replaceAll("&1",	ChatColor.DARK_BLUE.toString());
		announce = announce.replaceAll("&2",	ChatColor.DARK_GREEN.toString());
		announce = announce.replaceAll("&3",	ChatColor.DARK_AQUA.toString());
		announce = announce.replaceAll("&4",	ChatColor.DARK_RED.toString());
		announce = announce.replaceAll("&5",	ChatColor.DARK_PURPLE.toString());
		announce = announce.replaceAll("&6",	ChatColor.GOLD.toString());
		announce = announce.replaceAll("&7",	ChatColor.GRAY.toString());
		announce = announce.replaceAll("&8",	ChatColor.DARK_GRAY.toString());
		announce = announce.replaceAll("&9",	ChatColor.BLUE.toString());
		announce = announce.replaceAll("&a",	ChatColor.GREEN.toString());
		announce = announce.replaceAll("&b",	ChatColor.AQUA.toString());
		announce = announce.replaceAll("&c",	ChatColor.RED.toString());
		announce = announce.replaceAll("&d",	ChatColor.LIGHT_PURPLE.toString());
		announce = announce.replaceAll("&e",	ChatColor.YELLOW.toString());
		announce = announce.replaceAll("&f",	ChatColor.WHITE.toString());
		announce = announce.replaceAll("&k",	ChatColor.MAGIC.toString());
		announce = announce.replaceAll("&l",	ChatColor.BOLD.toString());
		announce = announce.replaceAll("&m",	ChatColor.STRIKETHROUGH.toString());
		announce = announce.replaceAll("&n",	ChatColor.UNDERLINE.toString());
		announce = announce.replaceAll("&o",	ChatColor.ITALIC.toString());
		announce = announce.replaceAll("&r",	ChatColor.RESET.toString());
		return announce;
	}

	//Disables the plugin
	public void disablePlugin(){
		Bukkit.getPluginManager().disablePlugin(this);
	}
}