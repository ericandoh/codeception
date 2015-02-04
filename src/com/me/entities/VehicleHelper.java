package com.me.entities;

import java.util.ArrayList;

import com.me.terrain.GameGrid;


/*
 * 
 * 
 * This class helps construct an entity! :D
 * 
 * 
 * Things to edit when making an entity:
 * 1. This class: makeEntity, getEntityID, (getEntityCost if applicable)
 * 2. EntityCommand: getListValidCommands
 * 3. Picture file: char.png
 * 4. public static final int NUM_VEHICLES
 * 
 * and thats it!
 * 
 * General types: Vehicle(standard), SmartVehicle(combination), ChipVehicle(smart), ShotVehicle (projectile that moves), things that always do stuff in general
 * StorageVeihcle, EngineVehicle (adds forces), SpecArmor (things can pass through it), etc, WavePulse Generator
 * Handled by vehiclecommand: workshopping(building stuff/refining ores/etc), actions that are yeah, actions
 * Assembler=just a smartVehicle with a chip that can fly
 * 
 * List of Vehicles (* = needs to be implemented)
 * 
 * General
 * 	1PlayerUnit (Makes WoodBots, Workshops) - unmoveable, heavy, like a base
 * 	2Workshop* (Makes Harvesters, weaponshop/armory, storage units, furnaces)
 * 	3Furnace*  (Refines ores at a slow but certain rate, tier 1)
 * 	
 * Tier 1
 * 	4Storage Unit*
 * 	5Harvester*
 *  Connector		(sticky sides all 6 sides, super low health)
 * 	6Forge* (for tier 2) (can be upgraded to refinery which processes both)
 * 	7Assembler*		(flies around, picks up stuff, drops it so it can be assembled) (crappy flying thing that is 1 unit)
 * 	8Basic Weaponshop* (can be upgraded to next level for a fee, same with armories)
 * 		10Pistol*		(standard thingy that shoots)
 * 		11Turret*		(buffs guns attached to it, very frikkin heavy)
 * 		12Gatling*	(rapid but weak fire)
 * 		13Mine*		(lay these and watch your enemies burn)
 * 		9Small Chip (Less HP/Same Weight)
 * 	14Basic Armory*
 *		15Armor* 		(if you just build this it acts like a wall!)
 * 		16Turbine*	(wind power!)
 * 		17Engine*		(moves stuff)
 * 		18Propeller* (Includes wings for flight)
 * 	
 * 
 * Tier 2
 * 	Warehouse*
 * 	Retriever*		(upgraded Harvester)
 * 	Refinery*	(processes tier 3)
 * 	Advanced Weapon Design*
 * 		Control Tower*		(scan vigilantly!)
 * 		Laser Cannon*		(%hp shreddddd)
 * 		Missiles*			(boom pain ow)
 *		Anti-air 			(upgraded turrets that can aim up)
 * 		Transport*			(holds other vehicles + transports them)
 * 		Chip II (A little HP/Same Weight)
 * 	Advanced Armor Design*
 * 		Jet*				(faster flight!)
 * 		Speeder				(faster horizontal movement!)
 * 		Solar Panels*		(yay energy)
 * 		Deflector Shield*	(shield that damage) (friendly units can pass through)
 * 		Repair Bot*			(specialized for repair)
 * 
 * Tier 3
 * 	Portal Storage*			(now store your goods in another dimension!)
 * 	Hypokinestic Research Lab*  (for weapons)
 * 		Scanner*	(can be used offensively + defensively)
 * 		Stun Plasma*(stun that shit)
 * 		Ramm*		(ram into vehicles with this!)
 * 		Shock		(repeatedly zap enemy with energy)
 * 		Kinetesis*	(like an Assembler but doesn't have to move with the thing. Also faster)
 * 		Chip III (Very tanky HP/Same Weight)
 * 	Defense Contractor Designs*	(for armor)
 * 		Energy Shield*		(regenerates!)(friendly units can pass through)
 * 		Ramjet				(fast jet)
 * 		Silencer Engine*	(can't be easily detected)
 * 		Silencer Scanner*	(^^^)
 * 		Silencer Pistol*	(^^^)
 * 		WavePulse Repairer*	(constant repairs to all things in an area)
 * 
 * Tier 4 (nuclear) (must be unlocked - need nuclear refined materials to make refinery LOL)
 * 	Nuclear Research Facility*
 * 		Nuclear Refinery*
 * 		Nuclear Plant*
 * 		Nuclear Launch Missile Pad*		(Uses hella energy, probably want to keep it off until use in which case you turn off shit)
 * 	Fission Facility
 * 
 * Tier 5...and beyond!
 */

public class VehicleHelper {
	public static final String UNIVERSAL_BUILDER = "ALL";
	public static final int NUM_VEHICLES = 8;
	
	public static final String[] VEHICLE = {"NULL",
		"Player", 
		"Workshop", 
		"Furnace", 
		"StorageUnit", 
		"Harvester", 
		"Forge", 
		"Assembler", 
		"Basic Weaponshop", 
		"Small Chip", 
		"Pistol", 
		"Turret", 
		"Gatling", 
		"Mine",  
		"Basic Armory", 
		"Armor", 
		"Turbine", 
		"Engine", 
		"Propeller"};
	
	public static final String[][] ENTITY = { {"NULL"},
		{"PlayerChip"}, 
		{"Workshop"}, 
		{"Furnace"}, 
		{"StorageUnit"}, 
		{"Harvester"}, 
		{"Forge"}, 
		{"Assembler", "AssemblerEngine"}, 
		{"Basic Weaponshop I", "Basic Weaponshop II"}, 
		{"Small Chip"}, 
		{"Pistol"}, 
		{"Turret"}, 
		{"Gatling"}, 
		{"Mine"}, 
		{"Basic Armory I", "Basic Armory II"}, 
		{"Armor"}, 
		{"Turbine"}, 
		{"Engine"}, 
		{"Propeller"}};
	
	public static final String[] BULLET_TYPES = {"NULL", 
		"Bullet", 
		"Mine", 
		"Missile", 
		"Shredlets"};
	
	public static final String LAND = Vehicle.LAND;
	public static final String WATER = Vehicle.WATER;
	
	public static Vehicle makeVehicle(GameGrid sq, Player p, double x, double y, double z, String id) {
		return makeVehicle(sq, p, x, y, z, id, UNIVERSAL_BUILDER);
	}
	public static Vehicle makeVehicle(GameGrid sq, Player p, double x, double y, double z, String id, String builder) {
		Vehicle veh = null;
		Entity temp;
		//format (entity) : sq, p, x, y, z, weight, maxHealth, entity name
		//format (vehicle): sq, input, p, x, y, z, entity type, entity name 
		
		/*if (id.equals("BASIC")) {
			temp = new Entity(sq, p, x, y, z, 5, 0.5, 5, "BASIC");
			veh = new Vehicle(sq, 0, p, x, y, z, Entity.SKY, false, false, "BASIC");
			veh.addEntity(temp);
		}*/
		//boolean univ = builder.equals(UNIVERSAL_BUILDER);
		if (id.equals(VEHICLE[1])) {
			//playerunit
			temp = new Entity(sq, p, x, y, z, 1, 1000, ENTITY[1][0]);
			veh = new ChipVehicle(sq, 0, p, x, y, z, LAND, VEHICLE[1]);
			veh.addEntity(temp);
			SmartVehicle veh1 = new SmartVehicle(sq, 0, p, x, y, z, LAND, VEHICLE[1]+" Group");
			veh1.addVehicle(veh, false);
			veh = veh1;
		}
		else if (id.equals(VEHICLE[2])) {
			//workshop
			temp = new Entity(sq, p, x, y, z, 50, 50, ENTITY[2][0]);
			veh = new Vehicle(sq, 10, p, x, y, z, LAND, VEHICLE[2]);
			veh.addEntity(temp);
		}
		else if (id.equals(VEHICLE[3])) {
			//furnace
			temp = new Entity(sq, p, x, y, z, 100, 50, ENTITY[3][0]);
			veh = new Vehicle(sq, 10, p, x, y, z, LAND, VEHICLE[3]);
			veh.addEntity(temp);
		}
		else if (id.equals(VEHICLE[4])) {
			//storage
			temp = new Entity(sq, p, x, y, z, 50, 10, ENTITY[4][0]);
			veh = new StorageVehicle(sq, 1, p, x, y, z, LAND, VEHICLE[4], 1000);
			veh.addEntity(temp);
		}
		else if (id.equals(VEHICLE[5])) {
			//harvester
			temp = new Entity(sq, p, x, y, z, 20, 10, ENTITY[5][0]);
			veh = new Vehicle(sq, 5, p, x, y, z, LAND, VEHICLE[5], 0.5f, 0f, 0.2f);
			veh.addEntity(temp);
		}
		else if (id.equals(VEHICLE[6])) {
			//forge (tier 2)
			temp = new Entity(sq, p, x, y, z, 100, 30, ENTITY[6][0]);
			veh = new Vehicle(sq, 15, p, x, y, z, LAND, VEHICLE[6]);
			veh.addEntity(temp);
		}
		else if (id.equals(VEHICLE[7])) {
			//assembler
			temp = new Entity(sq, p, x, y, z, 1, 5, ENTITY[7][0]);
			Entity temp2 = new Entity(sq, p, x-1, y, z, 1, 10, ENTITY[7][1]); 
			veh = new ChipVehicle(sq, 5, p, x, y, z, LAND, VEHICLE[7]);
			Vehicle veh2 = new EngineVehicle(sq, 10, p, x-1, y, z, LAND, VEHICLE[7]+ " engine", 0.5, 0.1, 0.2);
			veh.addEntity(temp);
			veh2.addEntity(temp2);
			SmartVehicle veh1 = new SmartVehicle(sq, 0, p, x, y, z, LAND, VEHICLE[7]+" Group");
			veh1.addVehicle(veh, false);
			veh1.addVehicle(veh2, false);
			veh = veh1;
		}
		else if (id.equals(VEHICLE[8])) {
			//weaponshop
			temp = new Entity(sq, p, x, y, z, 120, 50, ENTITY[8][0]);
			Entity temp2 = new Entity(sq, p, x+1, y, z, 120, 50, ENTITY[8][1]);
			veh = new Vehicle(sq, 10, p, x, y, z, LAND, VEHICLE[8]);
			veh.addEntity(temp);
			veh.addEntity(temp2);
		}
		else if (id.equals(VEHICLE[9])) {
			//small chip
			temp = new Entity(sq, p, x, y, z, 1, 10, ENTITY[9][0]);
			veh = new ChipVehicle(sq, 5, p, x, y, z, LAND, VEHICLE[9]);
			veh.addEntity(temp);
			SmartVehicle veh1 = new SmartVehicle(sq, 0, p, x, y, z, LAND, VEHICLE[9]+" Group");
			veh1.addVehicle(veh, false);
			veh = veh1;
		}
		else if (id.equals(VEHICLE[10])) {
			//pistol
			temp = new Entity(sq, p, x, y, z, 5, 20, ENTITY[10][0]);
			veh = new Vehicle(sq, 10, p, x, y, z, LAND, VEHICLE[10]);
			veh.addEntity(temp);
		}
		else if (id.equals(VEHICLE[11])) {
			//turret (buffs guns attached)
			temp = new Entity(sq, p, x, y, z, 250, 80, ENTITY[11][0]);
			veh = new ChipVehicle(sq, 25, p, x, y, z, LAND, VEHICLE[11]);
			veh.addEntity(temp);
			TurretVehicle veh1 = new TurretVehicle(sq, 0, p, x, y, z, LAND, VEHICLE[11]+" Turret", 1.2f);
			veh1.addVehicle(veh, false);
			veh = veh1;
		}
		else if (id.equals(VEHICLE[12])) {
			//gatling
			temp = new Entity(sq, p, x, y, z, 10, 35, ENTITY[12][0]);
			veh = new Vehicle(sq, 35, p, x, y, z, LAND, VEHICLE[12]);
			veh.addEntity(temp);
		}
		else if (id.equals(VEHICLE[13])) {
			//mine
			temp = new Entity(sq, p, x, y, z, 1, 1, ENTITY[13][0]);
			veh = new Vehicle(sq, 0, p, x, y, z, LAND, VEHICLE[13]);
			veh.addEntity(temp);
		}
		else if (id.equals(VEHICLE[14])) {
			//armory
			temp = new Entity(sq, p, x, y, z, 350, 125, ENTITY[14][0]);
			Entity temp2 = new Entity(sq, p, x + 1, y, z, 350, 125, ENTITY[14][1]);
			veh = new Vehicle(sq, 75, p, x, y, z, LAND, VEHICLE[14]);
			veh.addEntity(temp);
			veh.addEntity(temp2);
		}
		else if (id.equals(VEHICLE[15])) {
			//armor
			temp = new Entity(sq, p, x, y, z, 3, 50, ENTITY[15][0]);
			veh = new Vehicle(sq, 0, p, x, y, z, LAND, VEHICLE[15]);
			veh.addEntity(temp);
		}
		else if (id.equals(VEHICLE[16])) {
			//turbine
			temp = new Entity(sq, p, x, y, z, 400, 50, ENTITY[16][0]);
			veh = new EnergyVehicle(sq, 0, p, x, y, z, LAND, VEHICLE[16], 30);
			veh.addEntity(temp);
		}
		else if (id.equals(VEHICLE[17])) {
			//engine
			temp = new Entity(sq, p, x, y, z, 10, 50, ENTITY[17][0]);
			veh = new EngineVehicle(sq, 10, p, x, y, z, LAND, VEHICLE[17], 0.1, 0, 0.05);
			veh.addEntity(temp);
		}
		else if (id.equals(VEHICLE[18])) {
			//propeller
			temp = new Entity(sq, p, x, y, z, 10, 25, ENTITY[18][0]);
			veh = new EngineVehicle(sq, 20, p, x, y, z, LAND, VEHICLE[18], 0.1, 0.05, 0.05);
			veh.addEntity(temp);
		}
		else if (id.equals(VEHICLE[19])) {
			//generic
			temp = new Entity(sq, p, x, y, z, 150, 75, ENTITY[19][0]);
			veh = new Vehicle(sq, 20, p, x, y, z, LAND, VEHICLE[19]);
			veh.addEntity(temp);
		}
		else {
			return null;
		}
		return veh;
	}
	public static Vehicle makeBullet(GameGrid sq, Player p, double x, double y, double z, String id, float multiplier) {
		Entity temp;
		Vehicle veh = null;
		//force, life, damage
		if (id.equals(BULLET_TYPES[0])) {
			temp = new Entity(sq, p, x, y, z, 1, 1, ENTITY[10][0]);
			veh = new BulletVehicle(sq, 0, p, x, y, z, LAND, id, 0.15f, 200, (int)(2*multiplier));
			veh.addEntity(temp);
		}
		else if (id.equals(BULLET_TYPES[1])) {
			temp = new Entity(sq, p, x, y, z, 1, 1, ENTITY[10][0]);
			veh = new BulletVehicle(sq, 0, p, x, y, z, LAND, id, 0.15f, 200, (int)(2*multiplier));
			veh.addEntity(temp);
		}
		else if (id.equals(BULLET_TYPES[2])) {
			temp = new Entity(sq, p, x, y, z, 1, 1, ENTITY[10][0]);
			veh = new BulletVehicle(sq, 0, p, x, y, z, LAND, id, 0f, 10000, (int)(10*multiplier));
			veh.addEntity(temp);
		}
		return veh;
	}
	public static Vehicle makeVehicle(String line, Player p, GameGrid grid) {
		String entType = line.substring(0, line.indexOf(Vehicle.SPLITTER));
		if (entType.endsWith(" Group"))
			return new SmartVehicle(line, p, grid);
		if (entType.endsWith(" Turret"))
			return new TurretVehicle(line, p, grid);
		if (entType.equals(VEHICLE[1]) || entType.equals(VEHICLE[7]) || entType.equals(VEHICLE[9]) || entType.equals(VEHICLE[11]))
			return new ChipVehicle(line, p, grid);
		if (entType.equals(VEHICLE[4]))
			return new StorageVehicle(line, p, grid);
		if (entType.equals(VEHICLE[7] + " engine") || entType.equals(VEHICLE[8]) || entType.equals(VEHICLE[17]) || entType.equals(VEHICLE[18]))
			return new EngineVehicle(line, p, grid);
		if (entType.equals(VEHICLE[16]))
			return new EnergyVehicle(line, p, grid);
		for (String x: BULLET_TYPES) {
			if (x.equals(entType))
				return new BulletVehicle(line, p, grid);
		}
		return new Vehicle(line, p, grid);
	}
	public static int getVehicleID(String id) {
		for (int i = 0; i < VEHICLE.length; i++) {
			if (id.equals(VEHICLE[i]))
				return i;
		}
		/*if (id.equals("NULL")) {
			return 0;
		}
		else if (id.equals("PlayerUnit")) {
			return 1;
		}
		else if (id.equals("PlayerChip")) {
			return 2;
		}
		else if (id.equals("WoodTurbine")) {
			return 3;
		}
		else if (id.equals("WoodBot")) {
			return 4;
		}
		else if (id.equals("IronEngine")) {
			return 5;
		}
		else if (id.equals("IronMiner")) {
			return 6;
		}
		else if (id.equals("IronGun")) {
			return 7;
		}
		else if (id.equals("IronBullet")) {
			return 8;
		}*/
		return 0;
	}
	public static int getEntityID(String id) {
		int count = 0;
		for (int i = 0; i < ENTITY.length; i++) {
			for (int j = 0; j < ENTITY[i].length; j++) {
				if (id.equals(ENTITY[i][j]))
					return count;
				count++;
			}
		}
		/*
		if (id.equals("NULL")) {
			return 0;
		}
		else if (id.equals("PlayerUnit")) {
			return 1;
		}
		else if (id.equals("WoodTurbine")) {
			return 2;
		}
		else if (id.equals("WoodBot")) {
			return 3;
		}
		else if (id.equals("IronEngine")) {
			return 4;
		}
		else if (id.equals("IronMiner")) {
			return 5;
		}
		else if (id.equals("IronGun")) {
			return 6;
		}
		else if (id.equals("IronBullet")) {
			return 7;
		}*/
		return 0;
	}
	public static ArrayList<Item> getVehicleCost(String id) {
		//change later into list of Items...?
		ArrayList<Item> items = new ArrayList<Item>();
		if (id.equals(VEHICLE[0])) {
			//null
			items.add(Item.getItem(""));
		}
		else if (id.equals(VEHICLE[1])) {
			//playerunit
			items.add(Item.getItem("Log", 123456789));
		}
		else if (id.equals(VEHICLE[2])) {
			//workshop
			items.add(Item.getItem("Log", 10));
		}
		else if (id.equals(VEHICLE[3])) {
			//furnace
			items.add(Item.getItem("Log", 10));
		}
		else if (id.equals(VEHICLE[4])) {
			//storage
			items.add(Item.getItem("IronBar", 5));
			items.add(Item.getItem("Log", 10));
		}
		else if (id.equals(VEHICLE[5])) {
			//harvester
			items.add(Item.getItem("IronBar", 5));
			items.add(Item.getItem("Log", 10));
		}
		else if (id.equals(VEHICLE[6])) {
			//forge
			items.add(Item.getItem("IronBar", 10));
		}
		else if (id.equals(VEHICLE[7])) {
			//assembler
			items.add(Item.getItem("IronBar", 10));
			items.add(Item.getItem("Log", 15));
		}
		else if (id.equals(VEHICLE[8])) {
			//weaponshop
			items.add(Item.getItem("IronBar", 15));
			items.add(Item.getItem("Log", 10));
		}
		else if (id.equals(VEHICLE[9])) {
			//small chip
			items.add(Item.getItem("IronBar", 5));
		}
		else if (id.equals(VEHICLE[10])) {
			//pistol
			items.add(Item.getItem("IronBar", 10));
		}
		else if (id.equals(VEHICLE[11])) {
			//turret
			items.add(Item.getItem("IronBar", 20));
		}
		else if (id.equals(VEHICLE[12])) {
			//gatling
			items.add(Item.getItem("IronBar", 20));
			items.add(Item.getItem("Log", 5));
		}
		else if (id.equals(VEHICLE[13])) {
			//mine
			items.add(Item.getItem("IronBar", 10));
			items.add(Item.getItem("Log", 10));
		}
		else if (id.equals(VEHICLE[14])) {
			//armory
			items.add(Item.getItem("IronBar", 35));
			items.add(Item.getItem("Log", 10));
		}
		else if (id.equals(VEHICLE[15])) {
			//armor
			items.add(Item.getItem("IronBar", 2));
		}
		else if (id.equals(VEHICLE[16])) {
			//turbine
			items.add(Item.getItem("IronBar", 5));
			items.add(Item.getItem("Log", 20));
		}
		else if (id.equals(VEHICLE[17])) {
			//engine
			items.add(Item.getItem("IronBar", 15));
			items.add(Item.getItem("Log", 10));
		}
		else if (id.equals(VEHICLE[18])) {
			//propeller
			items.add(Item.getItem("IronBar", 25));
			items.add(Item.getItem("Log", 5));
		}
		//return Item.getItem("");
		return items;
	}
	public static ArrayList<Item> getVehicleRepairCost(Vehicle v) {
		String id = v.identifier();
		ArrayList<Item> items = new ArrayList<Item>();
		if (v instanceof SmartVehicle) {
			SmartVehicle x = (SmartVehicle)v;
			for (Vehicle ent : x.getVehicles()) {
				items.addAll(getVehicleRepairCost((Vehicle)ent));
			}
		}
		else {
			items = getVehicleCost(id);
			for (Item i : items) {
				i.setAmount(i.getAmount()/5);
			}
		}
		return items;
	}
	public static ArrayList<Item> getVehicleScrapCost(Vehicle v) {
		String id = v.identifier();
		ArrayList<Item> items = new ArrayList<Item>();
		if (v instanceof SmartVehicle) {
			SmartVehicle x = (SmartVehicle)v;
			for (Vehicle ent : x.getVehicles()) {
				items.addAll(getVehicleScrapCost((Vehicle)ent));
			}
		}
		else {
			items = getVehicleCost(id);
			for (Item i : items) {
				i.setAmount(i.getAmount()/2);
			}
		}
		return items;
	}
	//for fun!
	public static String getRandomName(String type) {
		String[] adverb = {"A", "The", "Temporarily", "Very", "Not", "Better", "Extremely"};
		String[] adj = {"Broken", "Cold", "Fine", "Exalted", "Fragile", "Well-Built", "Chosen", "Rusty"};
		String[] suffix = {"Of Peace", "and More", "of Pride", "I", "II", "III", "IV", "V"};
		String[] modeled = {"Series", "Model", "Serial"};
		int adjS = (int)(Math.random()*adj.length);
		if (Math.random() < 0.2) {
			int advS = (int)(Math.random()*adverb.length);
			type = adverb[advS]+" "+adj[adjS]+" "+type;
		}
		else {
			type = adj[adjS]+" "+type;
		}
		if (Math.random() < 0.2) {
			int suffixS = (int)(Math.random()*suffix.length);
			type = type+" "+suffix[suffixS];
		}
		if (Math.random() < 0.2) {
			int modelS = (int)(Math.random()*modeled.length);
			type = type+" "+modeled[modelS]+" "+(int)(Math.random()*789);
			if (Math.random() < 0.3)
				type = type+(char)((int)'A'-1+Math.random()*((int)'Z'-(int)'A'+2));
		}
		return type;
	}
	public static String getRandomName() {
		return "Vehicle";
	}
	public static void main(String[] args) {
		for (int i = 0; i < 5; i++) {
			System.out.println(getRandomName("WoodBot"));
		}
	}
}
