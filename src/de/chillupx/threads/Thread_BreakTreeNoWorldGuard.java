package de.chillupx.threads;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.Dispenser;
import org.bukkit.inventory.ItemStack;

import de.chillupx.WoodMachine;
import de.chillupx.config.ConfigField;
import de.chillupx.machine.Machine;

public class Thread_BreakTreeNoWorldGuard implements Runnable {

	@Override
	public void run() {
		for(Machine m : WoodMachine.getDatabaseHandler().getAllWoodMachines()) {
			if(m.getDispenser().isBlockPowered() || m.getDispenser().isBlockIndirectlyPowered()) {
				int height = WoodMachine.getMachineStateManager().getHeight(m);
				
				List<Block> removedBlocks = removeBlocks(m, height);
				if(removedBlocks.size() == 0) {
					WoodMachine.getMachineStateManager().setHeight(m, 0);
				}
				else if(removedBlocks.size() > 0) {
					WoodMachine.getMachineStateManager().nextHeight(m);
				}
				
				if(WoodMachine.getMachineStateManager().getHeight(m) > (WoodMachine.getConfigManager().getInt(ConfigField.MAX_HEIGHT) - 1))
					WoodMachine.getMachineStateManager().setHeight(m, 0);
			}
		}
	}
	
	private List<Block> getBlocksAround(Block block, int height) {
		List<Block> around = new ArrayList<Block>();
		
		if(height != 0)
			around.add(block);
		
		double bound = WoodMachine.getConfigManager().getDouble(ConfigField.RADIUS);
		
		Location a = block.getLocation().add(bound, 0.0D, bound);
		Location b = block.getLocation().add((-1.0D * (bound)), 0.0D, (-1.0D * (bound)));
		
		for(int xB = b.getBlockX(); xB <= a.getBlockX(); xB++) {
			for(int xZ = b.getBlockZ(); xZ <= a.getBlockZ(); xZ++) {
				Location where = new Location(a.getWorld(), xB, a.getBlockY() + height, xZ);
				around.add(where.getBlock());
			}
		}
		
		if(WoodMachine.getConfigManager().getBoolean(ConfigField.BIG_TREE_GETTER)) {
			double extraWidth = WoodMachine.getConfigManager().getDouble(ConfigField.BIG_TREE_GETTER_WIDTH);
			Location bigA = a.add(extraWidth, 0.0D, extraWidth);
			Location bigB = b.add(extraWidth * -1.0D, 0.0D, extraWidth * -1.0D);
			
			for(int xB = bigB.getBlockX(); xB <= bigA.getBlockX(); xB++) {
				for(int xZ = bigB.getBlockZ(); xZ <= bigA.getBlockZ(); xZ++) {
					Location where = new Location(bigA.getWorld(), xB, bigA.getBlockY() + height, xZ);
					
					if(where.getBlock().getType() == Material.LOG && !around.contains(where.getBlock()))
						around.add(where.getBlock());
				}
			}
		}
		
		return around;
	}
	
	@SuppressWarnings("deprecation")
	private List<Block> removeBlocks(Machine m, int height) {
		Dispenser disp = (Dispenser) m.getDispenser().getState();
		ItemStack tool = m.getTool();
		
		if(tool == null) {
			return new ArrayList<Block>();
		}
		
		if(m.getCuttingFace().getType() == Material.AIR && height == 0) {
			ItemStack sapling = null;
			for(ItemStack content : disp.getInventory().getContents()) {
				if(content == null)
					continue;
				
				if(content.getType() == Material.SAPLING) {
					sapling = content;
					break;
				}
			}
			
			if(sapling != null) {
				sapling.setAmount(sapling.getAmount() - 1);
				disp.getInventory().remove(sapling);
				disp.getInventory().addItem(sapling);
				
				m.getCuttingFace().setType(Material.SAPLING);
				m.getCuttingFace().setData(sapling.getData().getData());
			}
			
			return new ArrayList<Block>();
		}
			
		List<Block> removedBlocks = new ArrayList<Block>();
		for(Block block : getBlocksAround(m.getCuttingFace(), height)) {		
			if(block.getType() == Material.LOG || block.getType() == Material.LEAVES) {
				//ECONOMY
				if(WoodMachine.getConfigManager().getBoolean(ConfigField.ECON_ENABLED)) {
					double balance = WoodMachine.getEconomy().getBalance(m.getOwner());
					double cost = 0.0D;
					if(block.getType() == Material.LOG)
						cost = WoodMachine.getConfigManager().getDouble(ConfigField.ECON_PRICE_PER_BLOCK_LOG);
					else if(block.getType() == Material.LEAVES)
						cost = WoodMachine.getConfigManager().getDouble(ConfigField.ECON_PRICE_PER_BLOCK_LEAVES);
					
					if(cost > balance)
						break;
					
					WoodMachine.getEconomy().withdrawPlayer(m.getOwner(), cost);
				}			
				
				//DROPS
				Collection<ItemStack> drops = block.getDrops(tool);
				Iterator<ItemStack> items = drops.iterator();
				while(items.hasNext()) {
					Map<Integer, ItemStack> notFitted = disp.getInventory().addItem(items.next());
					if(notFitted.size() > 0) {
						Chest c = findChest(disp.getBlock());
						if(c != null) {
							for(ItemStack notFit : notFitted.values()) {
								c.getInventory().addItem(notFit);
							}
						}
					}
				}
				
				//DAMAGE TOOL
				short damage = 0;
				if(block.getType() == Material.LOG)
					damage = (short) WoodMachine.getConfigManager().getDamageLog(tool);
				if(block.getType() == Material.LEAVES) {
					damage = (short) WoodMachine.getConfigManager().getDamageLeave(tool);
				}
				
				tool.setDurability((short) (tool.getDurability() + damage));
				if(tool.getDurability() > getToolDurability(tool)) {
					for(ItemStack is : disp.getInventory().getContents()) {
						if(is == null)
							continue;
						
						if(is.getType() == tool.getType() && is.getDurability() == tool.getDurability()) {
							disp.getInventory().remove(tool);
							break;
						}
					}
				}
				
				//DO
				block.setType(Material.AIR);
				block.getWorld().playEffect(block.getLocation(), Effect.MAGIC_CRIT, 0, 20);
				block.getWorld().playEffect(block.getLocation(), Effect.MAGIC_CRIT, 0, 20);
				removedBlocks.add(block);
			}
		}
		
		return removedBlocks;
	}
	
	private short getToolDurability(ItemStack tool) {
		switch(tool.getType()) {
			case DIAMOND_AXE: return 1561;
			case GOLD_AXE: return 32;
			case IRON_AXE: return 250;
			case STONE_AXE: return 131;
			case WOOD_AXE: return 59;
			default: return 3000;
		}
	}
	
	private Chest findChest(Block b) {
		Block t = b.getRelative(BlockFace.UP);
		if(t.getType() == Material.CHEST)
			return (Chest) t.getState();
		
		t = b.getRelative(BlockFace.DOWN);
		if(t.getType() == Material.CHEST)
			return (Chest) t.getState();
		
		t = b.getRelative(BlockFace.NORTH);
		if(t.getType() == Material.CHEST)
			return (Chest) t.getState();
		
		t = b.getRelative(BlockFace.EAST);
		if(t.getType() == Material.CHEST)
			return (Chest) t.getState();
		
		t = b.getRelative(BlockFace.SOUTH);
		if(t.getType() == Material.CHEST)
			return (Chest) t.getState();
		
		t = b.getRelative(BlockFace.WEST);
		if(t.getType() == Material.CHEST)
			return (Chest) t.getState();
		
		return null;
	}
}