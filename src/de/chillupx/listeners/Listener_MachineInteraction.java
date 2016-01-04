package de.chillupx.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.material.Dispenser;

import de.chillupx.WoodMachine;
import de.chillupx.config.ConfigField;
import de.chillupx.machine.Machine;

public class Listener_MachineInteraction implements Listener {

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		if(event.getBlock().getType() != Material.DISPENSER || !WoodMachine.getMachineCreator().isInPlaceMode(event.getPlayer())) {
			WoodMachine.getMachineCreator().removeMode(event.getPlayer());
			return;
		}
		
		Player player = event.getPlayer();
		Dispenser dispenser = (Dispenser) event.getBlock().getState().getData();
		
		BlockFace bf = dispenser.getFacing();
		if(bf == BlockFace.UP || bf == BlockFace.DOWN) {
			player.sendMessage(" ");
			player.sendMessage(ChatColor.GOLD + "--------------- WoodMachine Creator ---------------");
			player.sendMessage(ChatColor.YELLOW + "The dispenser must not face up or down!");
			player.sendMessage(" ");
			player.sendMessage(ChatColor.GOLD + "Next step:");
			player.sendMessage(ChatColor.YELLOW + "     Place dispenser again with right facing..");
			player.sendMessage(" ");
			
			event.setCancelled(true);
			return;
		}
		
		//ECONOMY
		String econMessage = null;
		if(WoodMachine.getConfigManager().getBoolean(ConfigField.ECON_ENABLED)) {
			double balance = WoodMachine.getEconomy().getBalance(event.getPlayer().getPlayer());
			double cost = WoodMachine.getConfigManager().getDouble(ConfigField.ECON_CREATION_PRICE);
			if(cost > balance) {
				player.sendMessage(" ");
				player.sendMessage(ChatColor.GOLD + "--------------- WoodMachine Creator ---------------");
				player.sendMessage(ChatColor.YELLOW + "You need at least " + cost + " " + WoodMachine.getEconomy().currencyNamePlural() + " for this machine!");
				player.sendMessage(ChatColor.RED + "                 *Creation failed*");
				player.sendMessage(" ");
				
				event.setCancelled(true);
				return;
			}
			
			econMessage = ChatColor.YELLOW + "Price for this machine: " + cost + " " + WoodMachine.getEconomy().currencyNamePlural();
			WoodMachine.getEconomy().withdrawPlayer(event.getPlayer().getPlayer(), cost);
		}
		
		Block cutting = event.getBlock().getRelative(bf);				
		WoodMachine.getMachineCreator().createWoodMachine(cutting, event.getBlock(), player);
		player.sendMessage(" ");
		player.sendMessage(ChatColor.GOLD + "--------------- WoodMachine Creator ---------------");
		player.sendMessage(ChatColor.YELLOW + "You successfully placed your WoodMachine!");
		if(econMessage != null)
			player.sendMessage(econMessage);
		player.sendMessage(ChatColor.GREEN + "                 *Created WoodMachine*");
		player.sendMessage(" ");
		player.sendMessage(ChatColor.GOLD + "Next steps:");
		player.sendMessage(ChatColor.YELLOW + "     Place an axe and saplings in the dispenser inventory");
		player.sendMessage(ChatColor.YELLOW + "     and power the dispenser with redstone to get started");
		player.sendMessage(" ");
	}
	
	@EventHandler
	public void dontDispense(BlockDispenseEvent event) {
		if(WoodMachine.getDatabaseHandler().isWoodMachine(event.getBlock().getLocation())) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onBlockDestroy(BlockBreakEvent event) {
		if(event.getBlock().getType() == Material.DISPENSER) {
			if(WoodMachine.getDatabaseHandler().isWoodMachine(event.getBlock().getLocation())) {
				Machine m = WoodMachine.getDatabaseHandler().getWoodMachineByLocation(event.getBlock().getLocation());
				if(m.getOwner().getUniqueId() != event.getPlayer().getUniqueId() && !event.getPlayer().hasPermission("woodmachine.breakothers")) {
					event.getPlayer().sendMessage(ChatColor.RED + "You can not destroy others WoodMachine!");
					event.setCancelled(true);
					return;
				}
				
				if(WoodMachine.getConfigManager().getBoolean(ConfigField.ECON_ENABLED)) {
					WoodMachine.getEconomy().depositPlayer(event.getPlayer().getPlayer(), WoodMachine.getConfigManager().getDouble(ConfigField.ECON_REFUND_AMOUNT));
				}				
				WoodMachine.getDatabaseHandler().removeWoodMachine(m);
				event.getPlayer().sendMessage(ChatColor.YELLOW + "You destroyed your WoodMachine.");				
			}
		}
	}
}