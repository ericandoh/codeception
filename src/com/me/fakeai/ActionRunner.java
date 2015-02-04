package com.me.fakeai;

import java.util.ArrayList;
import java.util.HashMap;
import com.me.entities.Player;
import com.me.entities.Vehicle;
import com.me.terrain.GameGrid;

public class ActionRunner {
	
	public static final String SPLITTER = Vehicle.SPLITTER;
	public static String CONT = Action.CONT;				//runs without pause
	public static String STOP = Action.STOP;				//calling the program again calls this action again
	public static String PAUSE = Action.PAUSE;				//calling the program again calls the action that follows this action
	
	protected HashMap<String, Variable> myVariables;
	protected Player myPlayer;
	protected Vehicle myVeh;
	
	//protected Variable retVal;
	protected Action current;
	
	//protected ArrayList<Action> myQueue;
	protected ArrayList<String> queueNames;
	protected Variable[] retVal;
	
	public ActionRunner(Player p, Vehicle v) {
		myVariables = new HashMap<String, Variable>();
		myPlayer = p;
		myVeh = v;
		//myQueue = new ArrayList<Action>();
		queueNames = new ArrayList<String>();
		retVal = new Variable[1];
	}
	public ActionRunner() {
		retVal = new Variable[1];
	}
	public void runActions() {
		if (queueNames.size() <= 0)
			return;
		String result;
		do {
			if (current == null)
				current = Action.getAction(queueNames.get(0));
			result = current.run(myVeh, myPlayer, retVal);
			if (result.equals(Action.STOP)) {
				return;
			}
			queueNames.remove(0);
			current = null;
		} while (result.equals(Action.CONT) && queueNames.size() > 0);
	}
	public void setCurrent(Action c) {
		current = c;
	}
	public void addVar(String n, Variable y) {
		if (!myVariables.containsKey(n)) {
			if (!Action.isValidName(n, myVeh, myPlayer)) {
				myPlayer.addText("Not a valid name!: "+n);
				return;
			}
		}
		myVariables.put(n, y);
	}
	public boolean isVar(String n) {
		if (myVeh.isSysVariable(n))
			return true;
		for (String y: myVariables.keySet()) {
			if (y.equals(n))
				return true;
		}
		return false;
	}
	public Variable getVar(String n) {
		Variable v = myVeh.getSysVar(n);
		if (v == null)
			return myVariables.get(n);
		return v;
	}
	public void addQueue(String x) {
		/*if (x.equals("clear()")) {
			clearQueue();
			return;
		}
		if (x.equals("stop()")) {
			stop();
			return;
		}*/
		if (queueNames.size() < 500)
			queueNames.add(x);
	}
	public void clearQueue() {
		queueNames.clear();
		current = null;
	}
	public void stop() {
		if (queueNames.size() > 0) {
			queueNames.remove(0);
			current = null;
		}
	}
	public String getQueueName() {
		String returnMe = "";
		for (String q: queueNames) {
			returnMe = returnMe + q+"\n";
		}
		return returnMe;
	}
	public boolean isBusy() {
		return queueNames.size() != 0;
	}
	public void setRets(VehicleCommand c) {
		c.setRet(retVal);
	}
	public String encode() {
		String returnMe = myVariables.size() + SPLITTER;
		for (String x: myVariables.keySet()) {
			returnMe = returnMe + x + SPLITTER + myVariables.get(x).toString() + SPLITTER;
		}
		returnMe = returnMe + queueNames.size() + SPLITTER;
		for (int i = 0; i < queueNames.size(); i++) {
			returnMe = returnMe + queueNames.get(i) + SPLITTER;
		}
		if (current == null)
			returnMe = returnMe + "NULL" + SPLITTER;
		else
			returnMe = returnMe + "Action" + SPLITTER + current.encode() + SPLITTER;
		return returnMe;
	}
	public int decode(String[] a, int q, Player p, GameGrid sq) {
		int varLength = Integer.parseInt(a[q++]);
		myVariables = new HashMap<String, Variable>();
		for (int index = 0; index < varLength; index++) {
			myVariables.put(a[q++], Action.getVar(a[q++]).getVar());
		}
		int queueSize = Integer.parseInt(a[q++]);
		queueNames = new ArrayList<String>(queueSize);
		String queue;
		for (int count = 0; count < queueSize; count++) {
			queue = a[q++];
			queueNames.add(queue);
		}
		if (!a[q++].equals("NULL")) {
			if (queueNames.size() > 0) {
				current = Action.getAction(queueNames.get(0));
				q = current.decode(a, q);
			}
		}
		return q;
	}
	public void setVariables(HashMap<String, Variable> v) {
		myVariables = v;
	}
	public void setQueueStrings(ArrayList<String> qN) {
		queueNames = qN;
	}
	//for NullVeihcle
	public void setPlayer(Player p) {
		myPlayer = p;
	}
	public HashMap<String, Variable> getVariables() {
		return myVariables;
	}
	public ArrayList<String> getQueueStrings() {
		return queueNames;
	}
}
