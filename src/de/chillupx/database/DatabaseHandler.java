package de.chillupx.database;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import de.chillupx.machine.Machine;

public interface DatabaseHandler {

	public void loadDatabase();

	public void createWoodMachine(Machine machine);

	public List<Machine> getWoodMachines(Player player);

	public List<Machine> getAllWoodMachines();
	
	public boolean isWoodMachine(Location loc);

	public void removeWoodMachine(Machine m);

	public Machine getWoodMachineByLocation(Location loc);
	
	public void storeMachineStates();
	
	public String getMachineStates();
}