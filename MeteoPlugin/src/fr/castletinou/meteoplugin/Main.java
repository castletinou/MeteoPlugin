package fr.castletinou.meteoplugin;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;



public class Main extends JavaPlugin {
	private static Plugin plugin;
	public static Main instance;


	@Override
	public void onEnable() {
		System.out.println("[MeteoPlugin] Le plugin s'allume !");
		plugin = this;
		getCommand("boussole").setExecutor(new CommandBoussole());
		getServer().getPluginManager().registerEvents(new Listeners(), this);
	}
	
	@Override
	public void onDisable() {
		System.out.println("[MeteoPlugin] Le plugin s'éteint");
	
	}
	
	public static Plugin getPlugin() {
	    return plugin;
	  }
}
