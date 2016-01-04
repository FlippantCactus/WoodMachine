package de.chillupx.config;

import java.io.File;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import de.chillupx.WoodMachine;

public class ConfigManager {

	private FileConfiguration config;
	private WoodMachine plugin;
	
	public ConfigManager(WoodMachine plugin) {
		this.plugin = plugin;
	}
	
	public void loadConfig() {
		config = plugin.getConfig();
		if(!new File("plugins/WoodMachine/config.yml").exists())
			plugin.saveDefaultConfig();
	}
	
	public String getString(ConfigField where) {
		return config.getString(where.getPath());
	}
	
	public int getInt(ConfigField where) {
		return config.getInt(where.getPath());
	}
	
	public boolean getBoolean(ConfigField where) {
		return config.getBoolean(where.getPath());
	}
	
	public double getDouble(ConfigField where) {
		return config.getDouble(where.getPath());
	}
	
	private int getDetailedDamageLog(ItemStack tool) {
		switch(tool.getType()){
			case DIAMOND_AXE: return getInt(ConfigField.TOOL_DAMAGE_DIAMOND_LOG);
			case GOLD_AXE: return getInt(ConfigField.TOOL_DAMAGE_GOLD_LOG);
			case IRON_AXE: return getInt(ConfigField.TOOL_DAMAGE_IRON_LOG);
			case STONE_AXE: return getInt(ConfigField.TOOL_DAMAGE_STONE_LOG);
			case WOOD_AXE: return getInt(ConfigField.TOOL_DAMAGE_WOOD_LOG);
			default: return getInt(ConfigField.TOOL_DAMAGE_ALL_LOG);
		}
	}
	
	private int getDetailedDamageLeave(ItemStack tool) {
		switch(tool.getType()){
			case DIAMOND_AXE: return getInt(ConfigField.TOOL_DAMAGE_DIAMOND_LEAVES);
			case GOLD_AXE: return getInt(ConfigField.TOOL_DAMAGE_GOLD_LEAVES);
			case IRON_AXE: return getInt(ConfigField.TOOL_DAMAGE_IRON_LEAVES);
			case STONE_AXE: return getInt(ConfigField.TOOL_DAMAGE_STONE_LEAVES);
			case WOOD_AXE: return getInt(ConfigField.TOOL_DAMAGE_WOOD_LEAVES);
			default: return getInt(ConfigField.TOOL_DAMAGE_ALL_LEAVES);
		}
	}
	
	public int getDamageLog(ItemStack tool) {
		if(getBoolean(ConfigField.TOOL_USE_DETAILED)){
			return getDetailedDamageLog(tool);
		}
		else {
			return getInt(ConfigField.TOOL_DAMAGE_ALL_LOG);
		}
	}
	
	public int getDamageLeave(ItemStack tool) {
		if(getBoolean(ConfigField.TOOL_USE_DETAILED)){
			return getDetailedDamageLeave(tool);
		}
		else {
			return getInt(ConfigField.TOOL_DAMAGE_ALL_LEAVES);
		}
	}
}