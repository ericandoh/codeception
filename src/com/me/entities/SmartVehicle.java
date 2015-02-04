package com.me.entities;

import java.util.ArrayList;
import java.util.HashMap;

import com.me.fakeai.Variable;
import com.me.render.GameRenderer;
import com.me.terrain.GameGrid;

public class SmartVehicle extends Vehicle {
	
	protected ArrayList<ChipVehicle> chips;			//the arraylist of chip vehicles in this combined vehicle
	
	private ArrayList<Vehicle> myVehicles;			//vehicles under its domain, including the chips
	
	//protected HashMap<String, Variable> myVariables;
	private boolean subBusy;
	private boolean ignoreBusy;
	
	public SmartVehicle(GameGrid sq, int i, Player p, double x, double y, double z, String t, String et) {
		super(sq, i, p, x, y, z, t, et);
		myVehicles = new ArrayList<Vehicle>();
		chips = new ArrayList<ChipVehicle>();
		subBusy = false;
		ignoreBusy = false;
	}
	public SmartVehicle(String line, Player p, GameGrid grid) {
		super(line, p, grid);
		subBusy = false;
	}
	public void cycle() {
		if (pos.zpos != 0 && upSpeed == 0) {
			moveGravity();
		}
		if (!state)
			return;
		if (busy > 0) {
			busy = currentAction.run(busy);
			//System.out.println("(Vehicle)"+myName+" is "+busy+" doing "+currentAction.getName());
			if (busy == 0 && myOwner != null)
				finish();
		}
		else if (currentAction != null) {
			finish();
		}
		for (int i = 0; i < myVehicles.size(); i++) {
			myVehicles.get(i).cycle();
		}
		if (!ignoreBusy && subBusy) {
			return;
		}
		//runProgram();
		for (ChipVehicle v: chips) {
			v.runActions();
		}
	}
	public boolean isBusy() {
		//if (busy > 0)
			//return true;
		if (subBusy)
			return true;
		return false;
	}
	public void notifySubBusy() {
		subBusy = true;
	}
	public void setIgnoreBusy(boolean b) {
		ignoreBusy = b;
	}
	public void turnOn(boolean isOrigin) {
		if (isOrigin && myOwner != null) {
			myOwner.turnOn();
			return;
		}
		if (state)
			return;
		if (health == 0)
			return;
		
		for (Vehicle v: myVehicles) {
			v.turnOn(false);
		}
		if (myPlayer.useOutput(input)) {
			state = true;
		}
		if (isOrigin)
			recalculateCenterOfMass();
	}
	public void turnOff(boolean isOrigin) {
		if (isOrigin && myOwner != null) {
			myOwner.turnOff();
			return;
		}
		if (!state)
			return;
		state = false;
		for (Vehicle v: myVehicles) {
			v.turnOff(false);
		}
		myPlayer.returnOutput(input);
		if (isOrigin)
			recalculateCenterOfMass();
	}
	/*public Actor controller(CustomButtonBuilder b) {
		//returns a JPanel that is shown when this unit is selected!
		//return new DescriptionPanel();
		
		String info = entityType + "\r\nTerrain: " + type + "\nWeight : " + weight 
				+ "\nPower Used: " + input + "\nSystem ID: " + id + "\nSystem Variables\n";
		for (String key : runner.getVariables().keySet()) {
			info = info + key + ", ";
		}
		Label label = b.getLabel(info);
		label.setWrap(true);
		
		return label;
	}*/
	public void updateRenderIcon(GameRenderer r) {
		if (health == 0 || !r.matchPlayer(myPlayer))
			return;
		for (Vehicle ent: myVehicles) {
			ent.updateRenderIcon(r);
		}
	}
	public boolean translate(double xshift, double yshift, double zshift) {
		if (pos.zpos + zshift < 0) {
			zshift = -pos.zpos;
		}
		else if (pos.zpos + zshift > MAX_HEIGHT) {
			zshift = MAX_HEIGHT - pos.zpos;
		}
		if (!canAddType(xshift, yshift, zshift, type) && Math.random() < 0.9) {
			return false;
		}
		if (!canAdd(xshift, yshift, zshift, type) && Math.random() < 0.9) {
			//WORK NEED: crash?
			return false;
		}
		for (Vehicle ent: myVehicles) {
			//ent.translate(xshift, yshift);
			ent.unprocTranslate(xshift, yshift, zshift);
		}
		squares.update(this, pos.xpos, pos.ypos, xshift, yshift, farthestDist);
		pos.xpos += xshift;
		pos.ypos += yshift;
		pos.zpos += zshift;
		return true;
	}
	/*
	 * This method should never be called on SmartVehicle cuz a SmartVehicle will NEVER be a subentity of another smartVehicle
	 * 
	 * public void unprocTranslate(double xshift, double yshift, double zshift) {
		pos.xpos += xshift;
		pos.ypos += yshift;
		pos.zpos += zshift;
		for (Vehicle x : myVehicles) {
			x.unprocTranslate(xshift, yshift, zshift);
		}
	}*/
	public boolean setDirection(double d) {
		//double D's awww yeahhhh
		//basically calculates every distance from center into polar, then adds degrees to polar
		//and then converts back into rect and then translates each and every point, taking care to
		//remove and add simultaneously (not one by one, because then we have conflicting issues and
		//the spaceship thinks its colliding with itself rofl
		
		double degChange = d - pos.direction;
		double xrel, yrel, deg, mag;
		double xshift, yshift;
		for (Entity ent: myEntities) {
			xrel = ent.getXPos() - pos.xpos;
			yrel = ent.getYPos() - pos.ypos;
			deg = getDegDiff(xrel, yrel) + degChange;
			mag = getDist(ent.getXPos(), ent.getYPos());
			xshift = mag * Math.cos(deg);
			yshift = mag * Math.sin(deg);
			if (!ent.canPassType(xshift-xrel, yshift-yrel, type)) {
				//System.out.println(ent + " cannot turn into this type!?!");
				return false;
			}
		}
		if (!squares.canPassRot(pos.xpos, pos.ypos, degChange, type, this)) {
			//System.out.println("Cannot turn: collision problems?!?");
			return false;
		}
		
		for (Vehicle p : myVehicles) {
			p.unprocRotate(pos.xpos, pos.ypos, degChange);
		}
	
		pos.direction = (d + 2 * Math.PI) % (2 * Math.PI);
		return true;
	}
	/*
	 * same as unprocTranslate
	 * 
	public void unprocRotate(double x, double y, double degChange) {
		double xrel = pos.xpos - x;
		double yrel = pos.ypos - y;
		double deg = getDegDiff(xrel, yrel) + degChange;
		double mag = getDist(x, y);
		double xshift = mag * Math.cos(deg);
		double yshift = mag * Math.sin(deg);
		pos.xpos = x + xshift;
		pos.ypos = y + yshift;
		pos.direction = (degChange + pos.direction + 2 * Math.PI) % (2 * Math.PI);
		for (Vehicle p : myVehicles) {
			p.unprocRotate(x, y, degChange);
		}
	}*/
	//public Vehicle returnHighestOwner() { return myOwner.returnHighestOwner() }
	public void checkDestroyed() {
		for (Vehicle ent: myVehicles) {
			if (ent.getHP() != 0)
				return;
		}
		turnOff();
		recalculateCenterOfMass();
		/*if (myOwner != null)
			myOwner.checkDestroyed();*/
	}
	public void addQueue(String x) {
		//System.out.println("(SmartVehicle) Adding to queue: "+x);
		if (x.equals("clear()")) {
			for (Vehicle v: myVehicles) {
				v.clear();
			}
			//clear();
			//return;
		}
		else if (x.equals("stop()")) {
			for (Vehicle v: myVehicles) {
				v.clear();
			}
			//clear();
			//return;
		}
		chips.get(0).addQueue(x);
		
		//if clear/stop, then clear busy!! (i.e. send the "clear" to all subs/make this entity not busy anymore)
	}
	@Override
	public String getQueueName() {
		return chips.get(0).getQueueName();
	}
	@Override
	public boolean isVar(String n) {
		if (super.isVar(n))
			return true;
		return chips.get(0).isVar(n);
	}
	@Override
	public Variable getVar(String n) {
		Variable v = super.getVar(n);
		if (v == null)
			return chips.get(0).getVar(n);
		return v;
	}
	
	public void addVar(String n, Variable val) {	
		chips.get(0).addVar(n, val);
	}
	@Override
	public boolean addCommand(String name, Variable[] params, Variable[] retVal) {
		boolean result = false;
		if (super.addCommand(name, params, retVal)) {
			result = true;
		}
		for (Vehicle v: myVehicles) {
			result = v.addCommand(name, params, retVal) || result;
		}
		return result;
		//}
		//return true;
	}
	/*public Action getCommand(String n, String[] paramString) {
		for (Vehicle v : myVehicles) {
			if (v.hasCommand(n))
				v.addQueue(combineStringParams(n, paramString));
		}
		return null;
	}*/
	/*private String combineStringParams(String n, String[] paramString) {
		if (paramString.length == 0) {
			return n+"()";
		}
		String paramSt = "(";
		for (String p : paramString) {
			paramSt = paramSt + p + ",";
		}
		paramSt = paramSt.substring(0, paramSt.length() - 1);
		return n + paramSt +")";
	}*/
	@Override
	public void select(boolean x) {
		super.select(x);
		for (Vehicle ent: myVehicles) {
			ent.select(x);
		}
	}
	/*public boolean hasCommand(String n) {
		for (VehicleCommand y: myCommands) {
			if (y.getName().equals(n))
				return true;
		}
		for (Vehicle v: myVehicles) {
			v = (Vehicle)ent;
			if (v.state && v.isProgram(n)) {
				return true;
			}
		}
		return false;
	}*/
	public boolean addVehicle(Vehicle ent) {
		return addVehicle(ent, true);
	}
	public boolean addVehicle(Vehicle ent, boolean autoAdd) {
		//ent = ent.returnHighestOwner();
		//if (myOwner != null) {
			//return myOwner.addVehicle(ent);
		//}
		//System.out.println("Adding, "+ent.toString() + " to me: "+toString());
		boolean reselect = false;
		
		if (!ent.type.equals(type) && ent.pos.zpos == 0 && pos.zpos == 0)
			return false;
		
		if (autoAdd) {
			myPlayer.removeVehicle(ent);
			myPlayer.removeVehicle(this);			//will re-add myself
		}
		
		if (selected || ent.selected) {
			reselect = true;
			myPlayer.resetPuppet();
		}
		
		
		if (ent instanceof SmartVehicle) {
			for (Vehicle y: ((SmartVehicle)ent).myVehicles) {
				y.setOwner(this);
				myVehicles.add(y);
			}
			for (ChipVehicle c: ((SmartVehicle)ent).chips) {
				chips.add(c);
			}
		}
		else {
			ent.setOwner(this);
			myVehicles.add(ent);
		}
		if (ent instanceof ChipVehicle) {
			chips.add((ChipVehicle) ent);
		}
		for (Entity x: ent.myEntities) {
			myEntities.add(x);
		}
		String[][] keySet = ent.getKeyData();
		boolean goNext;
		for (int i = 0; i < keySet.length; i++) {
			goNext = true;
			for (KeyBindingCommand k: keyCommands) {
				if (k.getCommName().equals(keySet[i][1])) {
					goNext = false;
					break;
				}
			}
			if (goNext)
				addKeySet(keySet[i][0], keySet[i][1]);
		}
		
		
		recalculateCenterOfMass();
		if (autoAdd) {
			myPlayer.addVehicle(this);
		}
		if (reselect) {
			myPlayer.setPuppet(this);
		}
		return true;
	}
	public void disband() {
		//squares.removeVehicle(this);
		//myPlayer.removeVehicle(this);
		myPlayer.resetPuppet();
		boolean alreadyChipped = false;
		Vehicle v;
		Vehicle oneChip = null;
		for (int i = myVehicles.size() - 1; i >= 0; i--) {
			v = myVehicles.get(i);
			if (v instanceof ChipVehicle) {
				if (alreadyChipped) {
					SmartVehicle x = new SmartVehicle(squares, 0, myPlayer, -1, -1, -1, v.type, "Smart Vehicle");
					x.addVehicle(v);
					myPlayer.addVehicle(x);
					//myVehicles.remove(i);
					chips.remove(v);
				}
				else {
					alreadyChipped = true;
					oneChip = v;
				}
				continue;
			}
			v.myOwner = null;
			myPlayer.addVehicle(v);
			//myVehicles.remove(i);
		}
		myVehicles.clear();
		chips.clear();
		myEntities.clear();
		chips.add((ChipVehicle)oneChip);
		myVehicles.add(oneChip);
		for (Entity e: oneChip.myEntities) {
			myEntities.add(e);
		}
		//do nothing
		recalculateCenterOfMass();
	}
	public void recalculateCenterOfMass() {
		float tempForce = force;
		float tempUpForce = upForce;
		float tempRotForce = rotForce;
		for (Vehicle v: myVehicles) {
			v.recalculateCenterOfMass();
			force += v.force;
			upForce += v.upForce;
			rotForce += v.rotForce;
		}
		super.recalculateCenterOfMass();
		force = tempForce;
		upForce = tempUpForce;
		rotForce = tempRotForce;
	}
	public void recalculateBusy() {
		subBusy = false;
		for (Vehicle v: myVehicles) {
			if (v.isBusy()) {
				subBusy = true;
				return;
			}
		}
	}
	public void handle(char keyVal) {
		if (!state || isBusy()) {
			//my vehicle is busy doing something else
			//maybe turn this as an option
			return;
		}
		for (KeyBindingCommand key : keyCommands) {
			if (key.getKey() == keyVal) {
				if (!addCommand(key.getFuncName(), key.getVals(), new Variable[1]))
					addQueue(key.getCommName());
			}
		}
	}
	public ArrayList<Vehicle> getVehicles() {
		return myVehicles;
	}
	public String encode() {
		return super.encode()+ignoreBusy + SPLITTER;
	}
	public String encodeConn() {
		String returnMe = super.encodeConn() + myVehicles.size() + SPLITTER + chips.size() + SPLITTER;
		for (Vehicle ent: myVehicles) {
			returnMe = returnMe + ent.getSerial() + SPLITTER;
		}
		for (ChipVehicle ent: chips) {
			returnMe = returnMe + ent.getSerial() + SPLITTER;
		}
		return returnMe;
	}
	public int decode(String line, Player p, GameGrid sq) {
		int q = super.decode(line, p, sq);
		String[] a = line.split(SPLITTER);
		ignoreBusy = Boolean.parseBoolean(a[q++]);
		return q;
	}
	public int decodeConn(String line, HashMap<Integer, Entity> ents, HashMap<Integer, Vehicle> vehs) {
		String[] a = line.split(SPLITTER);
		int q = super.decodeConn(line, ents, vehs);
		int vehSize = Integer.parseInt(a[q++]);
		int chipSize = Integer.parseInt(a[q++]);
		myVehicles = new ArrayList<Vehicle>(vehSize);
		chips = new ArrayList<ChipVehicle>(chipSize);
		Vehicle v;
		for (int count = 0; count < vehSize; count++) {
			v = vehs.get(Integer.parseInt(a[q++]));
			v.setOwner(this);
			myVehicles.add(v);
		}
		for (int count = 0; count < chipSize; count++) {
			v = vehs.get(Integer.parseInt(a[q++]));
			if (v instanceof ChipVehicle) {
				chips.add((ChipVehicle)v);
			}
		}
		for (Vehicle veh: myVehicles) {
			if (veh.currentAction != null)
				chips.get(0).setRets(veh.currentAction);
		}
		recalculateCenterOfMass();
		return q;
	}
	public String[] getListCommand() {
		String[] listName = new String[myVehicles.size()];
		int count = 0;
		for (Vehicle ent: myVehicles) {
			listName[count++] = ent.toString();
		}
		return listName;
	}
}
