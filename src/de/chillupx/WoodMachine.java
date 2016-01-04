package de.chillupx;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import de.chillupx.commands.Command_WoodMachine;
import de.chillupx.config.ConfigField;
import de.chillupx.config.ConfigManager;
import de.chillupx.database.DatabaseHandler;
import de.chillupx.database.Database_MexDB;
import de.chillupx.listeners.Listener_MachineInteraction;
import de.chillupx.machine.MachineCreator;
import de.chillupx.machine.MachineStateManager;
import de.chillupx.threads.Thread_BreakTree;
import de.chillupx.threads.Thread_BreakTreeNoWorldGuard;
import de.chillupx.threads.Thread_MachineEffects;
import net.milkbowl.vault.economy.Economy;

public class WoodMachine extends JavaPlugin {

	private static DatabaseHandler databaseHandler;
	private static ConfigManager configManager;
	private static MachineCreator machineCreator;
	private static MachineStateManager mStateManager;
	
	private static Economy economy = null;
	private boolean forced = false;
	
	@Override
	public void onEnable() {
		//Do Init
		init();
		
		//Load Config
		getConfigManager().loadConfig();
		
		//DEPENDS
		if(getConfigManager().getBoolean(ConfigField.ECON_ENABLED)) {
			if(getServer().getPluginManager().getPlugin("Vault") == null) {
				getLogger().warning("[!!] Vault can not be found! Disable economy");
				getLogger().warning("[!!] features in config or install Vault.");
				getServer().getPluginManager().disablePlugin(this);
				this.forced = true;
				return;
			}
			else {
				if(!getServer().getPluginManager().getPlugin("Vault").isEnabled()) {
					getLogger().warning("[!!] Vault can not be found! Disable economy");
					getLogger().warning("[!!] features in config or install Vault.");
					getServer().getPluginManager().disablePlugin(this);
					this.forced = true;
					return;
				}
				else {
					if(!setupEconomy()) {
						getLogger().warning("[!!] No economy system can be found! Disable economy");
						getLogger().warning("[!!] features in config or install Vault supporting economy system.");
						getServer().getPluginManager().disablePlugin(this);
						this.forced = true;
						return;
					}
					else {
						getLogger().info("Hooked into Vault!");
					}
				}
			}
		}
		
		if(getConfigManager().getBoolean(ConfigField.WORLDGUARD_REGIONS)) {
			if(getServer().getPluginManager().getPlugin("WorldGuard") == null) {
				getLogger().warning("[!!] WorldGuard can not be found! Disable related");
				getLogger().warning("[!!] features in config or install it.");
				this.forced = true;
				getServer().getPluginManager().disablePlugin(this);
				return;
			}
			else {
				if(!getServer().getPluginManager().getPlugin("WorldGuard").isEnabled()) {
					getLogger().warning("[!!] WorldGuard can not be found! Disable");
					getLogger().warning("[!!] related features in config or install it.");
					this.forced = true;
					getServer().getPluginManager().disablePlugin(this);
					return;
				}
				else {
					getLogger().info("Hooked into WorldGuard!");
				}
			}
		}
		
		//Database
		databaseHandler.loadDatabase();
		
		//Commands
		this.getCommand("woodmachine").setExecutor(new Command_WoodMachine());
		
		//Listeners
		this.getServer().getPluginManager().registerEvents(new Listener_MachineInteraction(), this);
		
		//MACHINE STATES
		getMachineStateManager().loadFromString(getDatabaseHandler().getMachineStates());
		
		//Threads
		if(getConfigManager().getBoolean(ConfigField.WORLDGUARD_REGIONS))
			getServer().getScheduler().scheduleSyncRepeatingTask(this, new Thread_BreakTree(), 60, getConfigManager().getInt(ConfigField.CYCLE_TIME) * 20);
		else
			getServer().getScheduler().scheduleSyncRepeatingTask(this, new Thread_BreakTreeNoWorldGuard(), 60, getConfigManager().getInt(ConfigField.CYCLE_TIME) * 20);
		
		if(getConfigManager().getBoolean(ConfigField.ENABLE_EFFECTS))
			Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Thread_MachineEffects(), 20, 20);
		
		//Message
		getLogger().info("Plugin enabled.");
	}
	
	@Override
	public void onDisable() {
		//Save Machine States
		if(!this.forced)
		 getDatabaseHandler().storeMachineStates();
		
		//Message
		getLogger().info("Plugin disabled");
	}
	
	private boolean setupEconomy() {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }
        
        return (economy != null);
    }
	
	private void init() {
		configManager = new ConfigManager(this);
		databaseHandler = new Database_MexDB();
		machineCreator = new MachineCreator();
		mStateManager = new MachineStateManager();
	}
	
	public static Economy getEconomy() {
		return economy;
	}
	
	public static MachineCreator getMachineCreator() {
		return machineCreator;
	}
	
	public static MachineStateManager getMachineStateManager() {
		return mStateManager;
	}
	
	public static ConfigManager getConfigManager() {
		return configManager;
	}
	
	public static DatabaseHandler getDatabaseHandler() {
		return databaseHandler;
	}
}