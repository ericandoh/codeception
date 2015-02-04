package com.me.fakeai;

import com.me.entities.Player;
import com.me.entities.Vehicle;

public class ValueAction extends Action {
	protected Variable var;
	public ValueAction(Variable v) {
		var = v;
	}
	public String run(Vehicle y, Player z, Variable[] retVal) {
		retVal[0] = var;
		return CONT;
	}
	//used for parsing purposes (file IO)
	public Variable getVar() {
		return var;
	}
}
