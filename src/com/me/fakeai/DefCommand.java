package com.me.fakeai;

import com.me.entities.Player;
import com.me.entities.Vehicle;
import com.me.entities.ChipVehicle;

public class DefCommand extends Command {
	private String varName;
	private boolean saveToPlayer;
	public DefCommand(String varName, String[] paramString) {
		super(paramString);
		
		//format so nameVar is valid, ex. remove space
		//check if name is legit => WORK NEED
		this.varName = varName.replaceAll(" ", "");
		saveToPlayer = isCaps(varName.charAt(0));
	}
	public String runPrgm(Vehicle y, Player z, Variable[] retVal) {
		if (saveToPlayer) {
			z.addVar(varName, paramValues[0]);
		}
		else {
			((ChipVehicle)y).addVar(varName, paramValues[0]);
		}
		return CONT;
	}
	public int getNumParams() {
		return 1;
	}
	private static boolean isCaps(char x) {
		return (int)x >= (int)'A' && (int)x <= (int)'Z';
	}
	public static void addVar(Vehicle y, Player z, String name, Variable val) {
		if (isCaps(name.charAt(0))) {
			z.addVar(name, val);
		}
		else {
			((ChipVehicle)y).addVar(name, val);
		}
	}
}
