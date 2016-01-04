package de.chillupx.database;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.xemsdoom.mexdb.MexDB;
import com.xemsdoom.mexdb.exception.EmptyIndexException;
import com.xemsdoom.mexdb.system.Entry;

import de.chillupx.WoodMachine;
import de.chillupx.machine.Machine;
import de.chillupx.machine.MachineStateManager;

public class Database_MexDB implements DatabaseHandler {

	private MexDB database, machineStates;

	public void createWoodMachine(Machine machine) {
		Entry e = createEntry(String.valueOf(getNextId()));
		e.addValue("data", machine.toString());
		database.addEntry(e);
	}
	
	public List<Machine> getWoodMachines(Player player) {
		List<Machine> machines = new ArrayList<Machine>();
		for(String i : database.getIndices()) {
			Machine m = Machine.fromString(database.getString(i, "data"));
			if(m.getOwner().getUniqueId().toString().equals(player.getUniqueId().toString())) {
				machines.add(m);
			}
		}
		
		return machines;
	}
	
	private Entry createEntry(String input) {
		try {
			return new Entry(input);
		} catch (EmptyIndexException e) { e.printStackTrace(); }
		
		return null;
	}
	
	private int getNextId() {
		if(database.getIndices().size() < 1) {
			return 1;
		}
		
		int highest = 0;
		for(String index : database.getIndices()) {
			int i = Integer.valueOf(index);
			if(i > highest) highest = i;
		}
		
		return highest + 1;
	}

	@Override
	public void loadDatabase() {
		this.database = new MexDB("plugins/WoodMachine/data", "woodmachines");
		this.database.autopush(true);
		
		this.machineStates = new MexDB("plugins/WoodMachine/data", "machinestates");
		this.machineStates.autopush(true);
	}

	@Override
	public List<Machine> getAllWoodMachines() {
		List<Machine> machines = new ArrayList<Machine>();
		for(String i : database.getIndices()) {
			machines.add(Machine.fromString(database.getString(i, "data")));
		}
		
		return machines;
	}

	@Override
	public boolean isWoodMachine(Location loc) {
		for(Machine m : getAllWoodMachines()) {
			Location l = m.getDispenser().getLocation();
			if(l.getBlockX() == loc.getBlockX() && l.getBlockY() == loc.getBlockY() && l.getBlockZ() == loc.getBlockZ())
				return true;
		}
		
		return false;
	}

	@Override
	public Machine getWoodMachineByLocation(Location loc) {
		for(Machine m : getAllWoodMachines()) {
			Location l = m.getDispenser().getLocation();
			if(l.getBlockX() == loc.getBlockX() && l.getBlockY() == loc.getBlockY() && l.getBlockZ() == loc.getBlockZ()) {
				return m;
			}				
		}
		
		return null;
	}

	@Override
	public void removeWoodMachine(Machine m) {
		for(String i : database.getIndices()) {
			String data = database.getString(i, "data");
			if(data.equalsIgnoreCase(m.toString())) {
				database.removeEntry(i);
				break;
			}
		}
	}

	@Override
	public void storeMachineStates() {
		MachineStateManager msm = WoodMachine.getMachineStateManager();
		if(msm == null || machineStates == null)
			return;
		
		if(!machineStates.hasIndex("machinedata")) {
			Entry e = createEntry("machinedata");
			e.addValue("data", msm.toString());
			
			machineStates.addEntry(e);
		}
		else {
			machineStates.setValue("machinedata", "data",  msm.toString());
		}
	}

	@Override
	public String getMachineStates() {
		return machineStates.getString("machinedata", "data");
	}
}