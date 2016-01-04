package de.chillupx.machine;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.Dispenser;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

@SuppressWarnings("unchecked")
public class Machine {

	private Block cuttingFace;
	private Block dispenser;
	private OfflinePlayer owner;
	
	public Machine(Block cuttingFace, Block dispenser, Player owner) {
		this.cuttingFace = cuttingFace; this.owner = owner; this.dispenser = dispenser;
	}
	
	public Machine(Block cuttingFace, Block dispenser, OfflinePlayer off) {
		this.cuttingFace = cuttingFace; this.owner = off; this.dispenser = dispenser;
	}
	
	public Block getCuttingFace() {
		return this.cuttingFace;
	}
	
	public OfflinePlayer getOwner() {
		return this.owner;
	}
	
	public Block getDispenser() {
		return this.dispenser;
	}
	
	public boolean isPowered() {
		return( getDispenser().isBlockPowered() || getDispenser().isBlockIndirectlyPowered());
	}
	
	public ItemStack getTool() {
		Dispenser disp = (Dispenser) getDispenser().getState();
		Inventory inv =  disp.getInventory();
		for(ItemStack item : inv.getContents()) {
			if(item == null)
				continue;
			
			if(item.getType() == Material.DIAMOND_AXE || item.getType() == Material.GOLD_AXE || 
					item.getType() == Material.IRON_AXE || item.getType() == Material.STONE_AXE || item.getType() == Material.WOOD_AXE) {
				return item;
			}
		}
		
		return null;
	}
	
	public boolean hasTool() {
		return getTool() != null;
	}
	
	@Override
	public String toString() {
		JSONObject machine = new JSONObject();
		
		//CUTTING FACE
		JSONObject cutFace = new JSONObject();
		cutFace.put("world", getCuttingFace().getWorld().getName());
		cutFace.put("x", getCuttingFace().getLocation().getBlockX() + "I");
		cutFace.put("y", getCuttingFace().getLocation().getBlockY() + "I");
		cutFace.put("z", getCuttingFace().getLocation().getBlockZ() + "I");
		machine.put("cuttingface", cutFace);
		
		//Owner
		machine.put("owner", getOwner().getUniqueId().toString());
		
		//DISPENSER
		JSONObject disp = new JSONObject();
		disp.put("world", getDispenser().getWorld().getName());
		disp.put("x", getDispenser().getLocation().getBlockX() + "I");
		disp.put("y", getDispenser().getLocation().getBlockY() + "I");
		disp.put("z", getDispenser().getLocation().getBlockZ() + "I");
		machine.put("dispenser", disp);
		
		return machine.toJSONString();
	}
	
	public static Machine fromString(String input) {
		JSONObject machine = null;
		try {
			machine = (JSONObject) new JSONParser().parse(input);
		} catch (ParseException e) { e.printStackTrace(); }
		
		JSONObject cutObj = (JSONObject) machine.get("cuttingface");
		Block cutFace = Bukkit.getWorld((String) cutObj.get("world")).getBlockAt((Integer) getNumber((String) cutObj.get("x")),
													(Integer) getNumber((String) cutObj.get("y")), (Integer) getNumber((String) cutObj.get("z")));	
		
		OfflinePlayer owner = Bukkit.getOfflinePlayer(UUID.fromString((String) machine.get("owner")));
		
		JSONObject dis = (JSONObject) machine.get("dispenser");
		Block dispenser = Bukkit.getWorld((String) dis.get("world")).getBlockAt((Integer) getNumber((String) dis.get("x")),
				(Integer) getNumber((String) dis.get("y")), (Integer) getNumber((String) dis.get("z")));	
		return new Machine(cutFace, dispenser, owner);
	}
	
	private static Object getNumber(String input) {
		if(input.contains("I")) {
			return Integer.valueOf(input.replaceAll("I", ""));
		}
		
		return null;
	}
}