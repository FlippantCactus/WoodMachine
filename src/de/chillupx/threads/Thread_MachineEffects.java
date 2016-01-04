package de.chillupx.threads;

import org.bukkit.Effect;

import de.chillupx.WoodMachine;
import de.chillupx.machine.Machine;

public class Thread_MachineEffects implements Runnable {

	@Override
	public void run() {
		for(Machine m : WoodMachine.getDatabaseHandler().getAllWoodMachines()) {
			if(m.isPowered()) {
				if(m.hasTool()) {
					m.getDispenser().getWorld().playEffect(m.getDispenser().getLocation().add(0.0D, 0.9D, 0.0D), Effect.SMOKE, 4, 20);
					m.getDispenser().getWorld().playEffect(m.getDispenser().getLocation().add(0.0D, 0.9D, 0.0D), Effect.SMOKE, 4, 20);
				}
				else {
					m.getDispenser().getWorld().playEffect(m.getDispenser().getLocation().add(0.5D, 0.5D, 0.5D), Effect.VILLAGER_THUNDERCLOUD, 0, 25);
				}
			}
		}
	}
}