package com.me.fakeai;


import com.me.entities.Player;
import com.me.entities.Vehicle;

public class Command extends Action {
	//a command is an action that takes in parameters
	
	protected Action[] paramActions;				//actions that return the variables I want
	protected Variable[] paramValues;
	protected int place;							//which params actions to run
	protected boolean runningParams;
	
	public Command(String[] paramStrings) {
		//ex. "run(3,5)"
		//split paramString by commas, then make Action[] paramActions
		paramActions = new Action[paramStrings.length];
		paramValues = new Variable[paramStrings.length];
		for (int i = 0; i < paramStrings.length; i++) {
			paramActions[i] = Action.getAction(paramStrings[i]);
		}
		
		place = 0;
		runningParams = true;
	}
	public String run(Vehicle y, Player z, Variable[] retVal) {
		String result;
		if (!runningParams) {
			if (paramValues.length < getNumParams()) {
				z.addText("Not enough parameters (Needed: "+getNumParams()+")");
				return PAUSE;
			}
			result = runPrgm(y, z, retVal);
			if (result.equals(CONT) || result.equals(PAUSE))
				runningParams = true;
			return result;
		}
		else {
			result = runParams(y, z, retVal);
			if (result.equals(CONT)) {
				if (paramValues.length < getNumParams()) {
					z.addText("Not enough parameters (Needed: "+getNumParams()+")");
					return PAUSE;
				}
				result = runPrgm(y, z, retVal);
				if (result.equals(CONT) || result.equals(PAUSE))
					runningParams = true;
				return result;
			}
			else {
				return result;
			}
		}
	}
	public String runParams(Vehicle y, Player z, Variable[] retVal) {
		//run paramValues
		String result;
		while(place < paramActions.length) {
			result = paramActions[place].run(y, z, retVal);
			if (result.equals(STOP)) {
				return STOP;
			}
			paramValues[place] = retVal[0];
			place++;
			if (result.equals(PAUSE)) {
				return STOP;
			}
		}
		place = 0;
		runningParams = false;
		return CONT;
	}
	public String runPrgm(Vehicle y, Player z, Variable[] retVal) {
		if (paramValues.length > 0)
			retVal[0] = paramValues[paramValues.length - 1];
		return CONT;
	}
	public int getNumParams() {
		return 0;
	}
	public String encode() {
		//probably only need to save place/values up to that place/paramaction@place, but would rather save all
		int numValues = paramValues.length;
		String retMe = "COMMAND"+SPLITTER+numValues+SPLITTER+place+SPLITTER;
		for (Variable i : paramValues) {
			if (i == null) {
				retMe = retMe + "null" + SPLITTER;
			}
			else {
				retMe = retMe + i.toString() + SPLITTER;
			}
		}
		retMe = retMe + runningParams + SPLITTER;
		if (runningParams) {
			return retMe + paramActions[place].encode();
		}
		else {
			return retMe;
		}
	}
	public int decode(String[] info, int index) {
		if (!info[index++].equals("COMMAND")) {
			System.out.println("Error in reading command information string!: "+info[index-1]);
		}
		int numValues = Integer.parseInt(info[index++]);
		paramValues = new Variable[numValues];
		place = Integer.parseInt(info[index++]);
		for(int i = 0; i < numValues; i++) {
			paramValues[i] = Action.getVar(info[index++]).getVar();
		}
		runningParams = Boolean.parseBoolean(info[index++]);
		if (runningParams) {
			paramActions[place].decode(info, index);
		}
		return index;
	}
}

