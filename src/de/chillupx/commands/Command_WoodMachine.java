package de.chillupx.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import de.chillupx.WoodMachine;
import de.chillupx.machine.Machine;

public class Command_WoodMachine implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(sender instanceof ConsoleCommandSender) {
			sender.sendMessage("This command is only for players!");
			return true;
		}
		
		Player player = (Player) sender;
		if(args.length == 0) {
			player.sendMessage(" ");
			player.sendMessage(ChatColor.GOLD + "--------------- Your WoodMachines ---------------");
			
			List<Machine> machines = WoodMachine.getDatabaseHandler().getWoodMachines(player);
			if(machines == null || machines.size() == 0) {
				player.sendMessage(ChatColor.YELLOW + "You dont have any WoodMachines.");
				return true;
			}
			
			int i = 1;
			for(Machine m : machines) {
				player.sendMessage(ChatColor.GOLD + "#"+ i + ChatColor.YELLOW + " @(world="+ m.getDispenser().getWorld().getName() +" / x=" + m.getDispenser().getLocation().getBlockX() + " / y=" + m.getDispenser().getLocation().getBlockY() + " / z=" + m.getDispenser().getLocation().getBlockZ() +  ")");
				i++;
			}
			
			player.sendMessage(" ");
			return true;
		}
		else if(args.length == 1 && args[0].equalsIgnoreCase("create")) {
			if(!player.hasPermission("woodmachine.create")) {
				player.sendMessage(ChatColor.RED + "You do not have permissios!");
				return true;
			}
			
			WoodMachine.getMachineCreator().placeMode(player);
			
			player.sendMessage(" ");
			player.sendMessage(ChatColor.GOLD + "--------------- WoodMachine Creator ---------------");
			player.sendMessage(ChatColor.YELLOW + "You are now in the WoodMachine creation mode.");
			player.sendMessage(" ");
			player.sendMessage(ChatColor.GOLD + "Next step:");
			player.sendMessage(ChatColor.YELLOW + "     Place a dispenser to create the WoodMachine");
			player.sendMessage(" ");
			return true;
		}
		
		return false;
	}
}
