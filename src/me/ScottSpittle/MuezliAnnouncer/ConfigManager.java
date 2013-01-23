package me.ScottSpittle.MuezliAnnouncer;

import java.util.Arrays;

public class ConfigManager {
	
	public static MuezliAnnouncer plugin;
	
	public ConfigManager(MuezliAnnouncer instance){
		plugin = instance;
	}
    
    public long getInitialDelay(){
        return plugin.getConfig().getLong("initialDelay");
    }

    public long getMessageDelay(){
        return plugin.getConfig().getLong("announcementDelay");
    }

    public String getMessagePrefix(){
        return plugin.getConfig().getString("announcementPrefix").replaceAll("&", "§");
    }

    public String[] getMessages(){
        return Arrays.copyOf(plugin.getConfig().getList("announcements").toArray(), plugin.getConfig().getList("announcements").toArray().length, String[].class);
    }
}
