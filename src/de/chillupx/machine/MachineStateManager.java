package de.chillupx.machine;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import de.chillupx.WoodMachine;

@SuppressWarnings("unchecked")
public class MachineStateManager {

	private Map<Location, Integer> height = new HashMap<Location, Integer>(); 
	
	public int getHeight(Machine machine) {
		if(!height.containsKey(machine.getDispenser().getLocation())) {
			return 0;
		}
		
		return height.get(machine.getDispenser().getLocation());
	}
	
	public void setHeight(Machine machine, int i) {
		if(height.containsKey(machine.getDispenser().getLocation()))
			height.remove(machine.getDispenser().getLocation());
		
		height.put(machine.getDispenser().getLocation(), i);
	}
	
	public void nextHeight(Machine machine) {
		int current = getHeight(machine);
		setHeight(machine, current + 1);
	}
	
	@Override
	public String toString() {
		JSONArray states = new JSONArray();
		for(Entry<Location, Integer> e : height.entrySet()) {
			states.add(e.getKey().getBlockX() + ";" + e.getKey().getBlockY() + ";" + e.getKey().getBlockZ() + ";" + e.getKey().getWorld().getName() + ";" + e.getValue());
		}
		
		return states.toJSONString();
	}
	
	public void loadFromString(String input) {
		if(input == null)
			return;
		
		try {
			JSONArray arr = (JSONArray) new JSONParser().parse(input);
			Iterator<String> i = arr.iterator();
			while(i.hasNext()) {
				String[] d = i.next().split(";");
				Location l = new Location(Bukkit.getWorld(d[3]), Integer.valueOf(d[0]), Integer.valueOf(d[1]), Integer.valueOf(d[2]));
				if(!WoodMachine.getDatabaseHandler().isWoodMachine(l))
					continue;
				
				height.put(l, Integer.valueOf(d[4]));
			}
			
		} catch (ParseException e) { e.printStackTrace(); }
	}
}