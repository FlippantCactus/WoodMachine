package de.chillupx.machine;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import de.chillupx.WoodMachine;

public class MachineCreator {

	private List<Player> placeMode = new ArrayList<Player>();
	
	public void placeMode(Player player) {
		removeMode(player);
		placeMode.add(player);
	}
	
	public void removeMode(Player player) {
		if(isInPlaceMode(player))
			placeMode.remove(player);
	}
	
	public boolean isInPlaceMode(Player player) {
		return placeMode.contains(player);
	}

	public void createWoodMachine(Block cutting, Block dispenser, Player owner) {
		WoodMachine.getDatabaseHandler().createWoodMachine(new Machine(cutting, dispenser, owner));		
		removeMode(owner);
	}
}