package com.me.entities;

import java.util.ArrayList;
import java.util.HashMap;


import com.badlogic.gdx.scenes.scene2d.Actor;
import com.me.fakeai.NumVariable;
import com.me.fakeai.Variable;
import com.me.panels.CustomButtonBuilder;


public class NullVehicle extends SmartVehicle {

	public NullVehicle(Player p) {
		super(null, 0, p, -1, -1, -1, Vehicle.LAND, "NULL");
		chips.add(new ChipVehicle(null, 0, p, -1, -1, -1, Vehicle.LAND, "NULL_PARSER"));
		state = true;
	}
	/*public String getQueueName() {
		return "";
	}
	public void addQueue(Program v) { 	}*/
	public void cycle() {
		chips.get(0).runner.runActions();
	}
	public boolean isClose(double goalX, double goalY) {
		return true;	//wherever you go, there will ALWAYS BE NOTHING!
	}
	public boolean move(double goalX, double goalY) {
		return false;	//nothing cannot move
	}
	public boolean translate(double newX, double newY) {
		return false;
	}
	public boolean checkPass(int x, int y) {
		return false;
	}
	public boolean move() {
		return false;
	}
	public boolean turn(double goal) {
		return false;
	}
	public boolean setDirection(double d) {
		return false;
	}
	public boolean turnRight() {
		return false;
	}
	public boolean turnLeft() {
		return false;
	}
	public int getRepairs(int amount) {
		return amount;
	}
	public Vehicle returnHighestOwner() {
		return null;
	}
	public void takeDamage(int d) {	}
	public void healDamage(int d) {	}
	public boolean changeType(String t) {
		return false;
	}
	public boolean canChangeType(String t) {
		return false;
	}
	public void getDestroyed() {	}
	public boolean compWithType(String t) {
		return false;
	}
	public boolean addEntity(Entity ent) {
		return false;
	}
	public boolean addVehicle(Vehicle ent) {
		return false;
	}
	public void recalculateCenterOfMass() {	}
	public String toString() {
		//return "null entity";
		return "Player";
	}
	/*public String runProgram(String n, Variable[] params, Entity y, Player z, Variable[] returns, int place) {
		return Program.CONT;
	}*/
	public void setPlayer(Player p) {
		myPlayer = p;
		chips.get(0).runner.setPlayer(p);
	}
	public boolean isNull() {
		return true;
	}
	public Variable getVar(String n) {
		if (n.equals("xpos"))
			return new NumVariable(myPlayer.getXPos());
		else if (n.equals("ypos"))
			return new NumVariable(myPlayer.getYPos());
		else if (n.equals("zpos"))
			return new NumVariable(myPlayer.getZPos());
		else
			return chips.get(0).runner.getVar(n);
	}
	public void setVariables(HashMap<String, Variable> v) {
		chips.get(0).runner.setVariables(v);
	}
	public void setQueueStrings(ArrayList<String> qN) {
		chips.get(0).runner.setQueueStrings(qN);
	}
	public HashMap<String, Variable> getVariables() {
		return chips.get(0).runner.getVariables();
	}
	public ArrayList<String> getQueueStrings() {
		return chips.get(0).runner.getQueueStrings();
	}
	public ChipVehicle getChip() {
		return chips.get(0);
	}
	public void setOwner() { }
	public void setName(String newName) {	}
	
	
	//------methods below this line for vehiclepanel for player---------------
	
	public boolean getState() {
		return true;
	}
	public void turnOn() { }
	public void turnOff() { }
	public String getName() {
		return myPlayer.getName();
	}
	public Actor controller(CustomButtonBuilder b) {
		//returns a JPanel that is shown when this unit is selected!
		//return new DescriptionPanel();
		
		return myPlayer.controller(b); 
	}
	public ArrayList<Vehicle> getVehicles() {
		return new ArrayList<Vehicle>(0);
	}
	public String getHealth() {
		double avgFrac = 0;
		int count = 0;
		for (Vehicle v : myPlayer.getAllVehicles()) {
			count++;
			avgFrac += v.getFractionHealth();
		}
		return "Total: "+Math.round(avgFrac * 100 / count) + "/100";
	}
	public double getFractionHealth() {
		double avgFrac = 0;
		int count = 0;
		for (Vehicle v : myPlayer.getAllVehicles()) {
			count++;
			avgFrac += v.getFractionHealth();
		}
		return avgFrac / count;
	}
}