package com.me.entities;

import java.util.ArrayList;
import java.util.HashMap;


import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.me.fakeai.Action;
import com.me.fakeai.BoolVariable;
import com.me.fakeai.HPBarDrawable;
import com.me.fakeai.NumVariable;
import com.me.fakeai.StringVariable;
import com.me.fakeai.Variable;
import com.me.fakeai.VehicleCommand;
import com.me.panels.CustomButtonBuilder;
import com.me.render.Drawable;
import com.me.render.GameRenderer;
import com.me.render.IconDrawable;
import com.me.terrain.GameGrid;




public class Vehicle {
	
	public static final double MAX_HEIGHT = 100;
	
	public static int vehCount = 0;						//counter for vehicle
	
	public static final String LAND = "LAND";
	public static final String WATER = "WATER";
	
	//public static final String SPLITTER = (char)31 + "";
	public static final String SPLITTER = (char)7 + "";
	
	public static final float GRAVITY_SPEED = -0.03f;
	
	protected ArrayList<Entity> myEntities;				//list of entities
	
	protected Position pos;
	
	protected float farthestDist;					//entity that is the farthest distance away
	protected float farthestHeight;					//entity that is highest up (for purposes of calculating hp bar location)
	
	protected boolean state;						//true if on, false if off
	
	protected float weight;							//weight (force moved to engines)
	protected int input;							//needed power to stay on, loules
	
	protected float force;							//temporary variable used to calculate net forces
	protected float upForce;
	protected float rotForce;
	
	protected float velocity;
	protected float upSpeed;
	protected float rotSpeed;
	
	protected int health;							//from 1 to anything
	protected int maxHealth;						//from 1 to anything
	
	protected String type;							//land, water, or sky vehicle (an invalid string will signify land only)
													//this may change if the vehicle is multi-terrain (ex. can fly and float on water)
													//edit: this may NOT CHANGE!!!! unless entity method says it can (ex. parts that dont move)
	
	protected SmartVehicle myOwner;					//if any
	
	protected ArrayList<VehicleCommand> myCommands;
	
	protected Player myPlayer;
	
	protected String myName;
	protected String entityType;					//ex. "WoodBot"
	protected int id;								//unique # to this Vehicle
	
	protected boolean selected;						//if this vehicle is selected
	
	protected ArrayList <KeyBindingCommand> keyCommands;
	//private int waitTimer;
	protected HPBarDrawable hpDrawable;
	protected Drawable iconDrawable;
	
	protected int busy;
	protected VehicleCommand currentAction;
	
	protected GameGrid squares;
	public Vehicle(GameGrid s, int i, Player p, double x, double y, double z, String t, String et) {
		squares = s;
		input = i;
		myPlayer = p;
		pos = new Position(x, y, z, 0);
		
		//everything can be moved slowly!
		force = 0;
		upForce = 0;
		rotForce = 0;
		
		//will be reset
		velocity = 0;
		upSpeed = 0;
		rotSpeed = 0;
		health = 0;
		maxHealth = 0;
		
		type = t;
		myOwner = null;
		state = false;
		id = vehCount;
		vehCount++;
		entityType = et;
		//myName = VehicleHelper.getRandomName(entityType);
		myName = entityType;
		selected = false;
		squares = s;
		myPlayer = p;
		myEntities = new ArrayList<Entity>();
		weight = 0;
		keyCommands = new ArrayList <KeyBindingCommand>();
		myCommands = new ArrayList<VehicleCommand>();
		//myCommands = VehicleCommand.getListValidCommands(entityType, this);
		busy = 0;
		currentAction = null;
		//vehicle commands initialized after
		VehicleCommand.initializeVehicle(this, true);
	}
	public Vehicle(GameGrid s, int i, Player p, double x, double y, double z, String t, String et, float sf, float uf, float rf) {
		this(s, i, p, x, y, z, t, et);
		force = sf;
		upForce = uf;
		rotForce = rf;
	}
	public Vehicle(String line, Player p, GameGrid grid) {
		//makes a blank vehicle, for purposes of IO
		selected = false;
		busy = 0;
		decode(line, p, grid);
		//VehicleCommand.initializeVehicle(this, false);
	}
	public void notifyAdd() { }
	public void notifyRemove() { }
	public void cycle() {
		if (pos.zpos != 0 && upSpeed == 0)
			moveGravity();
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
		/*if (myQueue.size() > 0) {
			return;
		}*/
	}
	public boolean isBusy() {
		return busy > 0;
	}
	
	public String getCommandName() {
		return currentAction.getName();
	}
	public ArrayList<VehicleCommand> getCommands() {
		return myCommands;
	}
	public void turnOn() {
		turnOn(true);
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
		if (myPlayer.useOutput(input)) {
			state = true;
		}
		if (isOrigin)
			recalculateCenterOfMass();
	}
	public void turnOff() {
		turnOff(true);
	}
	public void turnOff(boolean isOrigin) {
		if (isOrigin && myOwner != null) {
			myOwner.turnOff();
			return;
		}
		if (!state)
			return;
		state = false;
		myPlayer.returnOutput(input);
		if (isOrigin)
			recalculateCenterOfMass();
	}
	public Actor controller(CustomButtonBuilder b) {
		System.out.println("Add display for speed?");
		//returns a JPanel that is shown when this unit is selected!
		//return new DescriptionPanel();
		String info = entityType + "\r\nTerrain: " + type + "\nWeight : " + weight 
				+ "\nPower Used: " + input + "\nSystem ID: " + id;
		//info = info + "\nUpForce:"+upForce+"\nPower"+force+"\nRotForce"+rotForce;
		Label label = b.getLabel(info);
		label.setWrap(true);
		return label;
	}
	public void updateRender(GameRenderer r) {
		for (Entity ent: myEntities) {
			ent.updateRender(r, selected);
		}
	}
	public void updateRenderHP(GameRenderer r) {
		if (health == 0 || !r.matchPlayer(myPlayer))
			return;
		if (hpDrawable == null) {
			hpDrawable = new HPBarDrawable(pos, ((float)health / maxHealth), (float)farthestHeight);
		}
		hpDrawable.updateHP((float)health / maxHealth);
		r.addToRender(hpDrawable);
	}
	public void updateRenderIcon(GameRenderer r) {
		if (health == 0 || !r.matchPlayer(myPlayer))
			return;
		if (isBusy()) {
			if (iconDrawable == null) {
				iconDrawable = new IconDrawable(pos, 0, (float)farthestHeight);
			}
			r.addToRender(iconDrawable);
		}
	}
	public double getDist(double finalX, double finalY) {
		return Math.sqrt(Math.pow(finalX - pos.xpos, 2) + Math.pow(finalY - pos.ypos, 2));
	}
	public double getDist(double finalX, double finalY, double finalZ) {
		return Math.sqrt(Math.pow(finalX - pos.xpos, 2) + Math.pow(finalY - pos.ypos, 2) + Math.pow(finalZ - pos.zpos, 2));
	}
	public double getDegDiff(double diffX, double diffY) {
		return (Math.atan2(diffY, diffX) + Math.PI * 2) % (Math.PI * 2);
	}
	public boolean isClose(double goalX, double goalY) {
		double dist = getDist(goalX, goalY);
		if (dist < 0.1) {
			return true;
			//do nothing
		}
		return false;
	}
	//returns true and true ONLY when it moves!
	public boolean moveVertGoal(double goalZ) {
		if (Math.abs(goalZ - pos.zpos) < 0.1)
			return false;
		return moveVert(goalZ > pos.zpos);
	}
	public boolean move(double goalX, double goalY, double goalZ) {
		if (myOwner != null || !state)
			return false;
		if (Math.round(Math.abs(goalZ - pos.zpos) / 0.01) * 0.01 <= 0.05) {
			return move(goalX, goalY);
		}
		else {
			if (Math.abs(goalZ - pos.zpos) < 0.1)
				return false;
			return moveVert(goalZ > pos.zpos);
		}
	}
	public boolean move(double goalX, double goalY) {
		if (myOwner != null || !state || velocity == 0)
			return false;
		double dist = getDist(goalX, goalY);
		if (isClose(goalX, goalY)) {
			return false;
			//do nothing
		}
		double diffX = goalX - pos.xpos;
		double diffY = goalY - pos.ypos;
		double goalAngle = getDegDiff(diffX, diffY);
		if (Math.abs(goalAngle - pos.direction) < rotSpeed / 2) {
			if (dist < velocity) {
				return translate(diffX, diffY, 0);
			}
			else {
				return move();
			}
		}
		else {
			return turn(goalAngle);
		}
	}
	public boolean canAdd(double xshift, double yshift, double zshift, String typ) {
		//does a rough check using distance method; if return true, can add for sure, else need more checks
		return squares.canPass(pos.xpos + xshift, pos.ypos + yshift, pos.zpos + zshift, typ, this);
	}
	public boolean canAddType(double xshift, double yshift, double zshift, String typ) {
		if (pos.zpos + zshift != 0)
			return true;
		for (Entity ent : myEntities) {
			if (!ent.canPassType(xshift, yshift, typ)) {
				return false;
			}
		}
		return true;
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
		for (Entity ent: myEntities) {
			//ent.translate(xshift, yshift);
			ent.unprocTranslate(xshift, yshift, zshift);
		}
		squares.update(this, pos.xpos, pos.ypos, xshift, yshift, farthestDist);
		pos.xpos += xshift;
		pos.ypos += yshift;
		pos.zpos += zshift;
		return true;
	}
	public void unprocTranslate(double xshift, double yshift, double zshift) {
		pos.xpos += xshift;
		pos.ypos += yshift;
		pos.zpos += zshift;
		for (Entity x : myEntities) {
			x.unprocTranslate(xshift, yshift, zshift);
		}
	}
	public boolean move() {
		if (myOwner != null || !state)
			return false;
		return translate(velocity*Math.cos(pos.direction), velocity*Math.sin(pos.direction), 0);
	}
	public boolean moveBack() {
		if (myOwner != null || !state)
			return false;
		return translate(-velocity*Math.cos(pos.direction), -velocity*Math.sin(pos.direction), 0);
	}
	public boolean moveGravity() {
		if (myOwner == null)
			return translate(0, 0, GRAVITY_SPEED);
		return false;
	}
	public boolean moveVert(boolean up) {
		if (myOwner != null || !state || upSpeed == 0)
			return false;
		if (up) {
			if (pos.zpos == MAX_HEIGHT)
				return false;
			return translate(0, 0, upSpeed);
		}
		else {
			if (pos.zpos == 0)
				return false;
			return translate(0, 0, -upSpeed);
		}
	}
	public boolean turn(double goal) {
		if (!state)
			return false;
		double diff = goal - pos.direction;
		if (Math.abs(diff) < 0.01) {
			return false;
		}
		else if (Math.abs(diff) <= rotSpeed || 2 * Math.PI - Math.abs(diff) <= rotSpeed) {
			return setDirection(goal);
		}
		else {
			diff = (diff + 2 * Math.PI) % (2 * Math.PI);
			if (diff < Math.PI) {
				return turnLeft();
			}
			else {
				return turnRight();
			}
		}
	}
	
	public boolean turnRight() {
		if (!state)
			return false;
		return setDirection(pos.direction - rotSpeed);
	}
	public boolean turnLeft() {
		if (!state)
			return false;
		return setDirection(pos.direction + rotSpeed);
	}
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
		
		for (Entity p : myEntities) {
			p.unprocRotate(pos.xpos, pos.ypos, degChange);
		}
	
		pos.direction = (d + 2 * Math.PI) % (2 * Math.PI);
		return true;
	}
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
		for (Entity p : myEntities) {
			p.unprocRotate(x, y, degChange);
		}
	}
	public boolean inRange(double x, double y, double z, Vehicle other, double dist) {
		return inRangeCheckDestroyed(x, y, z, other, dist, true);
	}
	public boolean inRangeCheckDestroyed(double x, double y, double z, Vehicle other, double dist, boolean incIfDest) {
		if (getDist(x, y, z) >= farthestDist + other.farthestDist + dist) {
			return false;
		}
		double xshift = x - pos.xpos;
		double yshift = y - pos.ypos;
		double zshift = z - pos.zpos;
		for (Entity ent: myEntities) {
			if (ent.isDestroyed() && !incIfDest)
				continue;
			for (Entity ent2 : other.myEntities) {
				if (ent2.inRangeOfVehicle(xshift, yshift, zshift, ent, dist) && (!ent2.isDestroyed() || incIfDest)) {
					return true;
				}
			}
		}
		return false;
	}
	public boolean inRangeRot(double x, double y, double degChange, Vehicle other, double dist) {
		if (getDist(x, y) >= farthestDist + other.farthestDist + dist) {
			return false;
		}
		double xrel, yrel, deg, mag;
		double xshift, yshift;
		for (Entity ent: myEntities) {
			if (ent.isDestroyed())
				continue;
			xrel = ent.getXPos() - x;
			yrel = ent.getYPos() - y;
			deg = getDegDiff(xrel, yrel) + degChange;
			mag = getDist(ent.getXPos(), ent.getYPos());
			xshift = mag * Math.cos(deg);
			yshift = mag * Math.sin(deg);
			for (Entity ent2 : other.myEntities) {
				if (!ent2.isDestroyed() && ent2.inRangeOfVehicle(xshift-xrel, yshift-yrel, 0, ent, dist)) {
					return true;
				}
			}
		}
		return false;
	}
	//used by Chunk for getNearbyEntities
	public ArrayList<Entity> inRangeEnt(Vehicle other, double dist) {
		ArrayList<Entity> candidates = new ArrayList<Entity>();
		if (getDist(other.getXPos(), other.getYPos(), other.getZPos()) >= farthestDist + other.farthestDist + dist) {
			return candidates;
		}
		for (Entity ent: myEntities) {
			for (Entity ent2 : other.myEntities) {
				if (ent2.inRangeOfVehicle(0, 0, 0, ent, dist)) {
					candidates.add(ent);
				}
			}
		}
		return candidates;
	}
	//called by entity's getDestroyed method. If every entity is destroyed, this vehicle is, too, destroyed
	public void checkDestroyed() {
		for (Entity ent: myEntities) {
			if (!ent.isDestroyed())
				return;
		}
		turnOff();
		if (myOwner != null)
			myOwner.checkDestroyed();
		else
			recalculateCenterOfMass();
	}
	public int getRepairs(int amount) {
		for (Entity ent : myEntities) {
			if (amount <= 0)
				return 0;
			amount = ent.getRepairs(amount);
		}
		return amount;
	}
	public Vehicle returnHighestOwner() {
		if (myOwner == null)
			return this;
		return myOwner;					//return myOwner.returnHighestOwner
	}
	public double getFractionHealth() {
		return 1.0*health/maxHealth;
	}
	public void takeDamage(int d) {
		boolean alreadyDead = health == 0;
		health -= d;
		if (health < 0) {
			health = 0;
		}
		if (myOwner != null && !alreadyDead) {
			myOwner.takeDamage(d);
		}
	}
	public void healDamage(int d) {
		health += d;
		if (myOwner != null) {
			myOwner.healDamage(d);
		}
	}
	public int getHP(){
		return health;
	}
	public void addQueue(String name) {
		//arguments inside MUST be variables!!!
		String[] args = Action.breakupLiterals(name);
		if (args.length > 0) {
			Variable[] params = new Variable[args.length - 1];
			for (int i = 1; i < args.length; i++) {
				if (isSysVariable(args[i])) {
					params[i - 1] = getSysVar(args[i]);
				}
				else {
					params[i - 1] = Action.getVar(args[i]).getVar();
				}
			}
			if (!addCommand(args[0], params, new Variable[1]))
				myPlayer.addText("Command not valid");
		}
	}
	public String getQueueName() {
		if (currentAction == null)
			return "";
		return currentAction.getName();
	}
	public boolean addCommand(String name) {
		return this.addCommand(name, null, new Variable[1]);
	}
	public boolean addCommand(String name, Variable[] params, Variable[] retVal) {
		//System.out.println("(Vehicle) Adding command: "+name);
		if(name.equals("stop") || name.equals("clear")) {
			clear();
			return true;
		}
		for (VehicleCommand c : myCommands) {
			if (c.getName().equals(name)) {
				//System.out.println("Set current: "+currentAction.getName());
				/*if (currentAction == null || currentAction.getName().equals("build"))
					System.out.println("LOL");*/
				/*for (Variable p: params) {
					System.out.println(p + ", ");
				}*/
				c.setParams(params, retVal);
				busy = c.beginAction();
				if (myOwner != null)
					myOwner.notifySubBusy();
				currentAction = c;
				return true;
			}
		}
		return false;
	}
	//clears WITHOUT finishing
	public void clear() {
		//System.out.println("Clearing...");
		currentAction = null;
		busy = 0;
		if (myOwner != null)
			myOwner.recalculateBusy();
	}
	public void finish() {
		if (currentAction != null) {
			currentAction.finish();
			//System.out.println("Finishing...");
			currentAction = null;
		}
		busy = 0;
		if (myOwner != null)
			myOwner.recalculateBusy();
	}
	public boolean isVar(String n) {
		if (isSysVariable(n))
			return true;
		return false;
	}
	public Variable getVar(String n) {
		Variable v = getSysVar(n);
		if (v == null)
			return null;
		return v;
	}
	public Variable getSysVar(String n) {
		if (n.equals("state"))
			return new BoolVariable(state);
		else if (n.equals("input"))
			return new NumVariable(input);
		else if (n.equals("xpos"))
			return new NumVariable(pos.xpos);
		else if (n.equals("ypos"))
			return new NumVariable(pos.ypos);
		else if (n.equals("zpos"))
			return new NumVariable(pos.zpos);
		else if (n.equals("velocity"))
			return new NumVariable(velocity);
		else if (n.equals("type"))
			return new StringVariable("'"+type+"'");
		else if (n.equals("direction"))
			return new NumVariable(pos.direction);
		else if (n.equals("rotSpeed"))
			return new NumVariable(rotSpeed);
		else if (n.equals("upSpeed"))
			return new NumVariable(upSpeed);
		else if (n.equals("hp"))
			return new NumVariable(health);
		else if (n.equals("maxhp"))
			return new NumVariable(maxHealth);
		else if (n.equals("name"))
			return new StringVariable("'"+myName+"'");
		else if (n.equals("entname"))
			return new StringVariable("'"+entityType+"'");
		else if (n.equals("sysent"))
			return new StringVariable("'"+myEntities.get(0).getXPos()+"/"+myEntities.get(0).getYPos()+"'");
		return null;
	}
	public boolean isSysVariable(String n) {
		if (n.equals("state"))
			return true;
		else if (n.equals("input"))
			return true;
		else if (n.equals("xpos"))
			return true;
		else if (n.equals("ypos"))
			return true;
		else if (n.equals("zpos"))
			return true;
		else if (n.equals("velocity"))
			return true;
		else if (n.equals("type"))
			return true;
		else if (n.equals("direction"))
			return true;
		else if (n.equals("rotSpeed"))
			return true;
		else if (n.equals("upSpeed"))
			return true;
		else if (n.equals("hp"))
			return true;
		else if (n.equals("maxhp"))
			return true;
		else if (n.equals("name"))
			return true;
		else if (n.equals("entname"))
			return true;
		else if (n.equals("sysent"))
			return true;
		return false;
	}
	//returns null if not found, CONT/etc if found
	/*public Action getCommand(String n, String[] paramString) {
		for (VehicleCommand y : myCommands) {
			if (y.getName().equals(n)) {
				return y.getAction(paramString);
			}
		}
		return null;
	}*/
	public boolean hasCommand(String n) {
		for (VehicleCommand y : myCommands) {
			if (y.getName().equals(n)) {
				return true;
			}
		}
		return false;
	}
	public void handle(boolean up, boolean down, boolean left, boolean right, boolean shiftDown) {
		if (isBusy())
			return;
		if (up) {
			if (shiftDown) {
				moveVert(true);
				//addQueue("up("+(int)Math.round(zpos+1)+")");
			}
			else {
				move();
				//int newX = (int)Math.round(xpos+1.4*Math.cos(direction));
				//int newY = (int)Math.round(ypos+1.4*Math.sin(direction));
				//addQueue("move("+newX+","+newY+")");
			}
		}
		if (down) {
			if (shiftDown) {
				moveVert(false);
				//addQueue("up("+(int)Math.round(zpos-1)+")");
			}
			else {
				moveBack();
				//int newX = (int)Math.round(xpos-1.4*Math.cos(direction));
				//int newY = (int)Math.round(ypos-1.4*Math.sin(direction));
				//addQueue("move("+newX+","+newY+")");
			}
		}
		if (left) {
			turnRight();
		}
		if (right) {
			turnLeft();
		}
	}
	protected class KeyBindingCommand {
		private char keyVal;
		private String funcName;
		private Variable[] vals;
		public KeyBindingCommand(String key, String comm) {
			if (key.toLowerCase().equals("space")) {
				keyVal = ' ';
			}
			else {
				keyVal = key.length() > 0 ? key.charAt(0) : (char)0;
			}
			String[] literals = Action.breakupLiterals(comm);
			funcName = literals[0];
			vals = new Variable[literals.length - 1];
			for (int i = 0; i < literals.length - 1; i++) {
				vals[i] = Action.getVar(literals[i+1]).getVar();
			}
		}
		public char getKey() {
			return keyVal;
		}
		public String getFuncName() {
			return funcName;
		}
		public Variable[] getVals() {
			return vals;
		}
		public String getKeyName() {
			if (keyVal == ' ')
				return "space";
			if (keyVal == 0)
				return "";
			return keyVal+"";
		}
		public String getCommName() {
			String ret = funcName+"(";
			for (Variable v: vals)
				ret = ret + v.toString() + ",";
			return vals.length == 0 ? ret+")" : ret.substring(0, ret.length() - 1) + ")";
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
				addCommand(key.getFuncName(), key.getVals(), new Variable[1]);
			}
		}
	}
	public void addKeySet(String key, String comm) {
		keyCommands.add(new KeyBindingCommand(key, comm));
	}
	public void resetKeySet() {
		keyCommands.clear();
	}
	public String[][] getKeyData() {
		String[][] data = new String[keyCommands.size()][2];
		int count = 0;
		for (KeyBindingCommand key : keyCommands) {
			data[count][0] = key.getKeyName();
			data[count++][1] = key.getCommName();
		}
		return data;
	}
	public void select(boolean x) {
		selected = x;
	}
	//unprotected adding Entity
	public boolean addEntity(Entity ent) {
		myEntities.add(ent);
		//myFaceEntities.add(ent);
		ent.setOwner(this);
		double oldxpos = pos.xpos;
		double oldypos = pos.ypos;
		//double oldzpos = zpos;
		recalculateCenterOfMass();
		squares.update(this, oldxpos, oldypos, pos.xpos - oldxpos, pos.ypos - oldypos, farthestDist);
		return true;
	}
	
	public double getWeight() {
		return weight;
	}
	public double getForce() {
		return force;
	}
	public double getInput() {
		return input;
	}
	public double getXPos() {
		return pos.xpos;
	}
	public double getYPos() {
		return pos.ypos;
	}
	public double getZPos() {
		return pos.zpos;
	}
	public double getVelocity() {
		return velocity;
	}
	public double getDirection() {
		return pos.direction;
	}
	
	public boolean getState() {
		return state;
	}
	public String getHealth() {
		return health + "/" + maxHealth;
	}
	public String getType() {
		return type;
	}
	public double getFarDist() {
		return farthestDist;
	}
	public void recalculateCenterOfMass() {
		double weightedDirection = 0;
		
		health = 0;
		maxHealth = 0;
		weight = 0;
		//force = 0;
		double weightedX = 0;
		double weightedY = 0;
		double weightedZ = 0;
		
		for (Entity ent: myEntities) {
			if (!ent.isDestroyed()) {
				health += ent.getHealth();
				//force += ent.getForce();
			}
			maxHealth += ent.getMaxHealth();
			weight += ent.getWeight();
			weightedX += ent.getXPos()*ent.getWeight();
			weightedY += ent.getYPos()*ent.getWeight();
			weightedZ += ent.getZPos()*ent.getWeight();
			weightedDirection += ent.getDirection();
		}
		if (myEntities.size() == 0) {
			return;
		}
		if (force != 0) {
			pos.direction = weightedDirection / myEntities.size();
		}
		if (weight != 0) {
			pos.xpos = weightedX/weight;
			pos.ypos = weightedY/weight;
			pos.zpos = weightedZ/weight;
			velocity = force/weight;
			upSpeed = upForce / weight;
			rotSpeed = rotForce / weight;
		}
		else {
			pos.xpos = weightedX/myEntities.size();
			pos.ypos = weightedY/myEntities.size();
			pos.zpos = weightedZ/myEntities.size();
			velocity = force / weight;
			upSpeed = upForce / weight;
			rotSpeed = rotForce / weight;
		}
		farthestDist = 0;
		farthestHeight = 0;
		float dist;
		for (Entity ent: myEntities) {
			dist = (float)getDist(ent.getXPos(), ent.getYPos());
			if (dist > farthestDist)
				farthestDist = dist;
			dist = (float)Math.abs(ent.getYPos() - pos.ypos);
			if (dist > farthestHeight)
				farthestHeight = 0;
		}
		farthestDist += 0.5;
		farthestHeight += 0.5;
	}
	public ArrayList<Entity> getEntities() {
		return myEntities;
	}
	public ArrayList<Vehicle> getNearbyVehicles() {
		return squares.getNearbyVehicles(returnHighestOwner(), myPlayer);
	}
	public ArrayList<Entity> getNearbyEntities() {
		return squares.getNearbyEntities(returnHighestOwner());
	}
	public GameGrid getSquares() {
		return squares;
	}
	public int getSerial() {
		return id;
	}
	public String encode() {
		String returnMe = entityType+SPLITTER+id+SPLITTER+myName+SPLITTER+state+SPLITTER+input+SPLITTER+pos.xpos+SPLITTER+pos.ypos
				+SPLITTER+pos.zpos+SPLITTER+type+SPLITTER+pos.direction
				+SPLITTER+force+SPLITTER+upForce+SPLITTER+rotForce+SPLITTER+busy+SPLITTER;
		returnMe = returnMe + keyCommands.size() + SPLITTER;
		for (KeyBindingCommand key: keyCommands) {
			returnMe = returnMe + key.getKeyName() + SPLITTER + key.getCommName() + SPLITTER;
		}
		if (currentAction == null) {
			returnMe = returnMe + "NULL" + SPLITTER;
		}
		else {
			returnMe = returnMe + currentAction.getName() + SPLITTER + currentAction.encode();
		}
		return returnMe;
	}
	public String encodeConn() {
		String returnMe = id+SPLITTER+myEntities.size()+SPLITTER;
		for (Entity ent: myEntities) {
			returnMe = returnMe + ent.getSerial() + SPLITTER;
		}
		return returnMe;
	}
	public int decode(String line, Player p, GameGrid sq) {
		//System.out.println(line);
		String[] a = line.split(SPLITTER);
		int q = 0;
		entityType = a[q++];
		id = Integer.parseInt(a[q++]);
		myName = a[q++];
		state = Boolean.parseBoolean(a[q++]);
		input = Integer.parseInt(a[q++]);
		double xp = Double.parseDouble(a[q++]);
		double yp = Double.parseDouble(a[q++]);
		double zp = Double.parseDouble(a[q++]);
		type = a[q++];
		double dir = Double.parseDouble(a[q++]);
		pos = new Position(xp, yp, zp, dir);
		force = Float.parseFloat(a[q++]);
		upForce = Float.parseFloat(a[q++]);
		rotForce = Float.parseFloat(a[q++]);
		busy = Integer.parseInt(a[q++]);
		int keySize = Integer.parseInt(a[q++]);
		keyCommands = new ArrayList<KeyBindingCommand>();
		for (int count = 0; count < keySize; count++) {
			keyCommands.add(new KeyBindingCommand(a[q++], a[q++]));
		}
		myCommands = new ArrayList<VehicleCommand>();
		VehicleCommand.initializeVehicle(this, false);
		//myCommands = VehicleCommand.getListValidCommands(entityType, this);
		String currentName = a[q++];
		if (!currentName.equals("NULL")) {
			for (VehicleCommand c: myCommands) {
				if (c.getName().equals(currentName)) {
					q = c.decode(a, q);
					currentAction = c;
					break;
				}
			}
		}
		squares = sq;
		myPlayer = p;
		return q;
	}
	public int decodeConn(String line, HashMap<Integer, Entity> ents, HashMap<Integer, Vehicle> vehs) {
		//System.out.println(line);
		String[] a = line.split(SPLITTER);
		int q = 0;
		q++;				//account for id
		int entSize = Integer.parseInt(a[q++]);
		myEntities = new ArrayList<Entity>(entSize);
		Entity ent;
		for (int count = 0; count < entSize; count++) {
			ent = ents.get(Integer.parseInt(a[q++]));
			ent.setOwner(this);
			myEntities.add(ent);
		}
		if (!(this instanceof SmartVehicle))
			recalculateCenterOfMass();
		return q;
	}
	public void setOwner(SmartVehicle v) {
		myOwner = v;
	}
	public boolean isNull() {
		return false;
	}
	public boolean isSelected() {
		return selected;
	}
	public String toString() {
		return myName;
	}
	public String identifier() {
		return entityType;
	}
	public void setName(String newName) {
		myName = newName;
	}
	public String getName() {
		return myName;
	}
	public Player getPlayer() {
		//probably can replace with isPlayeredBy() which returns a boolean, that way player variable is secure from attacks
		return myPlayer;
	}
}
/*
	
	
	
	
	
	public void addFaceEntity(Entity v) {
		myFaceEntities.add(v);
	}
	public void addRawEntity(Entity v) {
		myEntities.add(v);
	}
	
	public boolean isNull() {
		return false;
	}


/*

if (Math.abs(Math.round(x.getXPos())-Math.round(ent.getXPos())) <= 1 ) {
	if (Math.abs(Math.round(x.getYPos())-Math.round(ent.getYPos())) <= 1 ) {
		isCloseToAny = true;
		double shiftX = x.getXPos() - Math.round(x.getXPos());
		double shiftY = x.getYPos() - Math.round(x.getYPos());
		//squares[(int) Math.round(ent.getXPos())][(int) Math.round(ent.getYPos())].removeEntity(ent.getType());
		ent.setXPos(Math.round(ent.getXPos()) + shiftX);
		ent.setYPos(Math.round(ent.getYPos()) + shiftY);
		//squares[(int) Math.round(ent.getXPos())][(int) Math.round(ent.getYPos())].addEntity(ent, ent.getType());
	}
}

if (!(squares[(int) Math.round(ent.getXPos())][(int) Math.round(ent.getYPos())].canAddEntity(type))) {
			if (type.equals(SKY) && (ent.getType().equals(SKY))) {
			}
			else if (!(type.equals(SKY)) && !(ent.getType().equals(SKY))){
			}
			else {
				return false;
			}
		}
*/