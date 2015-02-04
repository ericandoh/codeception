package com.me.entities;

import com.badlogic.gdx.graphics.Color;
import com.me.render.Drawable;
import com.me.render.GameRenderer;
import com.me.render.EntityDrawable;
import com.me.render.ShadowRotatableDrawable;
import com.me.terrain.GameGrid;
import com.me.terrain.Square;



//All entities have the ability to move
//All entities have power consumption, which comes from a central bank of power (Tesla's wireless)
//	a. Energy measured in Loules (parody on Joules)
//	b. Entity shuts down (stops moving, etc.) if power output is not sufficient
//All entities have a cycle method, in which they do whatever they do in that cycle
//All entities have a health

//add method so if it cant move tries going left/right <--add to vehicle too
//add so Entity has a master - Player in this case, or possibly "Le World"

public class Entity {
	
	//private static final int DRAW_BOUNDARY = 10;
	//public static final String ROCK = "ROCK";
	
	public static int entCount = 0;
	
	//public static final BufferedImage SHADOW = ImageProcesser.getEntityBG();
	
	//public static final BufferedImage IMAGE =  ImageProcesser.getEntityImage(0);
	
	public static final String DEL_NAME = "Justin Bieber";
	
	public static final Color DEFAULT_VEH_COLOR = new Color(255, 255, 255, 0);
	
	public static final String SPLITTER = Vehicle.SPLITTER;
	
	private GameGrid squares;
	
	//private double xpos, ypos;	//position does not have to be fixed to one block
	//private double direction;		//measured in rads, cuz rads are rad
	
	private Position pos;
	
	private boolean destroyed;	//if true unoperable (replace this with simple comparison to hp == 0)
	private double weight;		//measured in wrams
	//private double force;			//measured in fewtons, 1 fewton = 1 wram * 1 unit/cycle
	
	private Vehicle myOwner;		//if any
	
	private int id;
	private Color myColor;
	//private BufferedImage myImage;
	private String entityType;
	
	private int health, maxHealth;
	
	private Drawable myDrawable;
	private Drawable shadowDrawable;
	
	public Entity(GameGrid s, Player p, double x, double y, double z, double w, int mH, String et) {
		squares = s;
		pos = new Position(x, y, z, 0);
		destroyed = false;
		weight = w;
		//force = f;
		myOwner = null;
		id = entCount;
		entCount++;
		//myColor = new Color((int)(Math.random()*255),(int)(Math.random()*255),(int)(Math.random()*255));
		//myColor = new Color(255, 255, 255, 0);
		myColor = DEFAULT_VEH_COLOR;
		entityType = et;
		health = mH;
		maxHealth = mH;
		myDrawable = new EntityDrawable(pos, VehicleHelper.getEntityID(entityType), destroyed);
	}
	public Entity(String line, GameGrid sq) {
		//for purposes of reconstructing entity
		decode(line, sq);
	}
	public Entity(int ix, GameGrid sq, double x, double y, double z, double dir, boolean d, double w, 
			Color col, int hp, int maxHP, String et) {
		
	}
	public void cycle() {
		//do nothing as of now
	}
	public void turnOn() {
		if (destroyed)
			return;
		if (myOwner != null)
			myOwner.returnHighestOwner().recalculateCenterOfMass();
	}
	public void turnOff() {
		if (destroyed)
			return;
		if (myOwner != null)
			myOwner.returnHighestOwner().recalculateCenterOfMass();
	}
	public void updateRender(GameRenderer r, boolean selected) {
		if (selected) {
			if (shadowDrawable == null) {
				shadowDrawable = new ShadowRotatableDrawable(pos, VehicleHelper.getEntityID(entityType)); 
			}
			//System.out.println("Add a shadow drawable object!");
			r.addToRender(shadowDrawable);
		}
		r.addToRender(myDrawable);
	}
	/*public void updateRenderPuppet(GameRenderer r) {
		r.addToRender(new ShadowRotatableDrawable(pos, VehicleHelper.getEntityID(entityType), myColor));
	}*/
	public double getDist(double finalX, double finalY) {
		return Math.sqrt(Math.pow(finalX - pos.xpos, 2) + Math.pow(finalY - pos.ypos, 2));
	}
	/*public double getDist(double finalX, double finalY, double finalZ) {
		return Math.sqrt(Math.pow(finalX - pos.xpos, 2) + Math.pow(finalY - pos.ypos, 2) + Math.pow(finalZ - pos.zpos, 2));
	}*/
	public double getDegDiff(double diffX, double diffY) {
		return (Math.atan2(diffY, diffX) + Math.PI * 2) % (Math.PI * 2);
	}
	public void unprocTranslate(double xshift, double yshift, double zshift) {
		pos.xpos += xshift;
		pos.ypos += yshift;
		pos.zpos += zshift;
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
	}
	public boolean changeDirection(double d) {
		//double D's awww yeahhhh
		pos.direction = (d + pos.direction + 2 * Math.PI) % (2 * Math.PI);
		return true;
	}
	public boolean canChangeType(String t) {
		return true;
	}
	public boolean changeType(String t) {
		return true;
	}
	public void takeDamage(int d) {
		if (destroyed)
			return;
		boolean alreadyDead = health == 0;
		health -= d;
		if (health < 0) {
			health = 0;
			getDestroyed();
		}
		if (myOwner != null && !alreadyDead) {
			myOwner.takeDamage(d);
		}
	}
	public void getDestroyed() {
		destroyed = true;
		((EntityDrawable)myDrawable).updateDestroyed(destroyed);
		if (myOwner != null)
			myOwner.checkDestroyed();
		//inform owner if any
		//WORK NEED: if crash and there's stuff below, stuff below gets destroyed too >:D
	}
	public int getRepairs(int amount) {
		if (health == maxHealth) {
			return amount;
		}
		int healed;
		health += amount;
		if (health > maxHealth) {
			healed = amount - health + maxHealth;
			amount = health - maxHealth;
			health = maxHealth;
		}
		else {
			healed = amount;
			amount = 0;
		}
		if (destroyed) {
			destroyed = false;
			((EntityDrawable)myDrawable).updateDestroyed(destroyed);
			myOwner.returnHighestOwner().recalculateCenterOfMass();
		}
		else {
			myOwner.healDamage(healed);
		}
		return amount;
	}
	public boolean isDestroyed() {
		return destroyed;
	}
	public double getWeight() {
		return weight;
	}
	public double getDirection() {
		return pos.direction;
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
	public int getHealth() {
		return health;
	}
	public int getMaxHealth() {
		return maxHealth;
	}
	public Vehicle getOwner() {
		return myOwner;
	}
	public void select(boolean x) {	}
	public boolean inRangeOfVehicle(double xshift, double yshift, double zshift, Entity ent, double dist) {
		if (Math.abs(ent.pos.zpos+zshift-pos.zpos) <= 1) {
			if (Math.abs(ent.pos.xpos+xshift-pos.xpos) <= dist ) {
				if (Math.abs(ent.pos.ypos+yshift-pos.ypos) <= dist ) {
					return true;
				}
			}
		}
		return false;
	}
	public boolean canPassType(double xshift, double yshift, String typ) {
		double realX = pos.xpos + xshift;
		//int realX = (int)(pos.xpos + xshift);
		double realY = pos.ypos + yshift;
		//int realY = (int)(pos.ypos + yshift);
		Square sq = squares.getSquare(realX, realY);
		if (sq == null)
			return false;
		return sq.canPass(typ);
	}
	public String toString() {
		return "Entity";
	}
	public String encode() {
		String returnMe = id+SPLITTER+pos.xpos+SPLITTER+pos.ypos+SPLITTER+pos.zpos+SPLITTER+weight+SPLITTER+pos.direction+SPLITTER
				+destroyed+SPLITTER+entityType+SPLITTER+myColor.toIntBits()
				+SPLITTER+health+SPLITTER+maxHealth+SPLITTER;
		//returnMe = returnMe + "$Vars\n";
		return returnMe;
		
		//extend this to vehicle so that it prints out also all vehicles, basically make hashmap from newly made list to match up subentities
		//after reading over list again!
	}
	public int getSerial() {
		return id;
	}
	public void decode(String line, GameGrid sq) {
		//System.out.println(line);
		String [] firstLines = line.split(SPLITTER);
		int index = 0;
		id = Integer.parseInt(firstLines[index++]);
		double xpos = Double.parseDouble(firstLines[index++]);
		double ypos = Double.parseDouble(firstLines[index++]);
		double zpos = Double.parseDouble(firstLines[index++]);
		weight = Double.parseDouble(firstLines[index++]);
		double direction = Double.parseDouble(firstLines[index++]);
		pos = new Position(xpos, ypos, zpos, direction);
		destroyed = Boolean.parseBoolean(firstLines[index++]);
		entityType = firstLines[index++];
		myColor = new Color(Integer.parseInt(firstLines[index++]));
		health = Integer.parseInt(firstLines[index++]);
		maxHealth = Integer.parseInt(firstLines[index++]);
		squares = sq;
		
		myDrawable = new EntityDrawable(pos, VehicleHelper.getEntityID(entityType), destroyed);
	}
	public void setOwner(Vehicle v) {
		myOwner = v;
	}
}

//class ProgrammableUnit extends Entity {

	

	
//}
