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
import java.util.List;

import net.milkbowl.vault.permission.Permission;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public class MuezliAnnouncer extends JavaPlugin {

	public static MuezliAnnouncer plugin;
	public final BukkitLogger blo = new BukkitLogger(this);
	public final ConfigManager cfManager = new ConfigManager(this);
	public int counter = 0;
	public static Permission perms = null;
	public boolean isPlayer = false;
	public static PluginDescriptionFile pdfFile = null;

	@Override
	public void onDisable() {
		blo.enabled(false);
	}

	@Override
	public void onEnable() {
		ConfigManager cfManager = new ConfigManager(this);
		// set plugin to this instance.
		plugin = this;
		// run BukkitLogger class on enable.
		blo.enabled(true);
		// using vault setting up permissions.
		setupPermissions();
		// create config if it doesn't exsist
		createConfig();
		this.scheduleAnnouncerTask(cfManager.getInitialDelay(),
				cfManager.getMessageDelay());
		pdfFile = plugin.getDescription();
	}

	// Register Permissions via Vault.
	private boolean setupPermissions() {
		RegisteredServiceProvider<Permission> rsp = getServer()
				.getServicesManager().getRegistration(Permission.class);
		perms = rsp.getProvider();
		return perms != null;
	}

	// Creates the config file
	public void createConfig() {
		File file = new File(getDataFolder() + File.separator + "config.yml");
		if (!file.exists()) {
			getLogger().info("[MuezliPlugin] Creating default config file ...");
			saveDefaultConfig();
			getLogger().info("[MuezliPlugin] Config created successfully!");
		} else {
			getLogger().info("[MuezliPlugin] Config Already Exsists!");
		}
	}

	// increment announcements
	public void broadcastAnnounce() {
		if (this.getServer().getOnlinePlayers().length >= 1) {
			ConfigManager cfManager = new ConfigManager(this);
			if (counter >= cfManager.getMessages().length) {
				counter = 0;
			}
			doAnnounce(counter);
			counter++;
		}
	}

	// Announce the message parsed.
	public void doAnnounce(int index) {
		if (index < cfManager.getMessages().length) {
			getServer().broadcastMessage(
					cfManager.getMessagePrefix() + " "
							+ colorize(cfManager.getMessages()[index]));
		}
	}

	// set a scheuled announcer task.
	private BukkitTask scheduleAnnouncerTask(long initialWait,
			long repeatInterval) {
		return getServer().getScheduler().runTaskTimerAsynchronously(this,
				new Runnable() {
					@Override
					public void run() {
						broadcastAnnounce();
					}
				}, initialWait, repeatInterval);
	}

	// replace all colorcodes with Bukkit ColorChat string.
	public static String colorize(String announce) {
		// Color codes
		announce = announce.replaceAll("&0", ChatColor.BLACK.toString());
		announce = announce.replaceAll("&1", ChatColor.DARK_BLUE.toString());
		announce = announce.replaceAll("&2", ChatColor.DARK_GREEN.toString());
		announce = announce.replaceAll("&3", ChatColor.DARK_AQUA.toString());
		announce = announce.replaceAll("&4", ChatColor.DARK_RED.toString());
		announce = announce.replaceAll("&5", ChatColor.DARK_PURPLE.toString());
		announce = announce.replaceAll("&6", ChatColor.GOLD.toString());
		announce = announce.replaceAll("&7", ChatColor.GRAY.toString());
		announce = announce.replaceAll("&8", ChatColor.DARK_GRAY.toString());
		announce = announce.replaceAll("&9", ChatColor.BLUE.toString());
		announce = announce.replaceAll("&a", ChatColor.GREEN.toString());
		announce = announce.replaceAll("&b", ChatColor.AQUA.toString());
		announce = announce.replaceAll("&c", ChatColor.RED.toString());
		announce = announce.replaceAll("&d", ChatColor.LIGHT_PURPLE.toString());
		announce = announce.replaceAll("&e", ChatColor.YELLOW.toString());
		announce = announce.replaceAll("&f", ChatColor.WHITE.toString());
		announce = announce.replaceAll("&k", ChatColor.MAGIC.toString());
		announce = announce.replaceAll("&l", ChatColor.BOLD.toString());
		announce = announce
				.replaceAll("&m", ChatColor.STRIKETHROUGH.toString());
		announce = announce.replaceAll("&n", ChatColor.UNDERLINE.toString());
		announce = announce.replaceAll("&o", ChatColor.ITALIC.toString());
		announce = announce.replaceAll("&r", ChatColor.RESET.toString());
		return announce;
	}

	public boolean onCommand(CommandSender sender, Command cmd,
			String commandLabel, String[] args) {
		if (sender instanceof Player) {
			isPlayer = true;
		}
		if (commandLabel.equalsIgnoreCase("announce")) {
			if (isPlayer) {
				Player player = (Player) sender;
				if (args.length == 0) {
					return false;
				}
				if (args.length >= 1) {
					if (args[0].equalsIgnoreCase("reload")) {
						if (perms.has(player, "muezli.announce.reload")) {
							plugin.reloadConfig();
							player.sendMessage(ChatColor.GREEN
									+ "Config reloaded");
							plugin.getServer()
									.getConsoleSender()
									.sendMessage(
											ChatColor.GREEN + pdfFile.getName()
													+ " version "
													+ pdfFile.getVersion()
													+ " has been reloaded");
						} else {
							player.sendMessage(ChatColor.RED
									+ "you don't have permission to use that command");
						}
						return true;
					}
				}
				if (args[0].equalsIgnoreCase("add")) {
					if (perms.has(player, "muezli.announce.add")) {
						String lm = StringUtils.join(args, " ", 1, args.length);

						@SuppressWarnings("unchecked")
						List<String> announcementList = (List<String>) this
								.getConfig().getList("announcements");
						announcementList.add(lm);

						saveConfig();

						player.sendMessage(ChatColor.GREEN
								+ "Announcement Added.");
					} else {
						player.sendMessage(ChatColor.RED
								+ "you don't have permission to use that command");
					}
					return true;
				}
				if (args[0].equalsIgnoreCase("list")) {
					if (perms.has(player, "muezli.announce.list")) {

						@SuppressWarnings("unchecked")
						List<String> announcementList = (List<String>) this
								.getConfig().getList("announcements");

						for (int x = 0; x < announcementList.size(); x++) {
							player.sendMessage(ChatColor.GOLD + "" + x + ". "
									+ ChatColor.RESET
									+ colorize(announcementList.get(x)));
						}
					} else {
						player.sendMessage(ChatColor.RED
								+ "you don't have permission to use that command");
					}
					return true;
				}
				if (args[0].equalsIgnoreCase("set")) {
					if (args[1].equalsIgnoreCase("prefix")) {
						if (perms.has(player, "muezli.announce.add")) {
							String prefix = StringUtils.join(args, " ", 2,
									args.length);
							this.getConfig().set("announcementPrefix", prefix);
							saveConfig();

							player.sendMessage(ChatColor.GREEN
									+ "Prefix Changed.");
						} else {
							player.sendMessage(ChatColor.RED
									+ "you don't have permission to use that command");
						}
						return true;
					}
					if (args[1].equalsIgnoreCase("delay")) {
						if (perms.has(player, "muezli.announce.add")) {
							int delay = Integer.parseInt(args[2]);
							this.getConfig().set("announcementDelay", delay);
							saveConfig();

							player.sendMessage(ChatColor.GREEN
									+ "Announcement Delay Changed.");
						} else {
							player.sendMessage(ChatColor.RED
									+ "you don't have permission to use that command");
						}
						return true;
					}
				}
				if (args[0].equalsIgnoreCase("remove")) {

					@SuppressWarnings("unchecked")
					List<String> announcementList = (List<String>) this
							.getConfig().getList("announcements");

					if (perms.has(player, "muezli.announce.add")) {
						try {
							Integer.parseInt(args[1]);
							int indx = Integer.parseInt(args[1]);
							if (indx < announcementList.size()) {
								announcementList.remove(indx);
								saveConfig();
								player.sendMessage(ChatColor.GREEN
										+ "Successfully removed announcement "
										+ indx);
							} else {
								player.sendMessage(ChatColor.RED
										+ "Im sorry, I can't find that annoncement, try /announce list");
							}
						} catch (Exception e) {
							player.sendMessage(ChatColor.RED
									+ "Im sorry, you didn't give an announcement index");
						}
					} else {
						player.sendMessage(ChatColor.RED
								+ "you don't have permission to use that command");
					}
					return true;
				}
			} else {
				sender.sendMessage(ChatColor.RED + "You must be a player");
			}
		}
		return false;
	}

	// Disables the plugin
	public void disablePlugin() {
		Bukkit.getPluginManager().disablePlugin(this);
	}
}