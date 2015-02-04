package com.me.terrain;
//generate only one of each square; link to references ONLY!
//dont forget to change BiomePoint!

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.me.entities.Item;
import com.me.entities.Player;
import com.me.entities.Vehicle;


public class Square {
	
	public static final int NUM_SQUARES = 9;
	
	public static final float BOUNDARY = 10f/64f;			//inset margin and outset margin
	//public static final float BOUNDARY = 0.1f;			//inset margin and outset margin
	
	private int id;
	
	private boolean landPassable, waterPassable;			//, skyPassable;
	
	private String myName;
	
	public Square(int i, boolean lp, boolean wp, boolean sp, String name) {
		id = i;
		landPassable = lp;
		waterPassable = wp;
		//skyPassable = sp;
		//myImage = ImageProcesser.getSquareImage(id);
		myName = name;
	}
	public boolean canPass(String x) {
		//"You shall not pass!" -Gandalf
		if (x.equals(Vehicle.LAND) && landPassable)
			return true;
		if (x.equals(Vehicle.WATER) && waterPassable)
			return true;
		return false;
	}
	public String toString() {
		return myName;
	}
	public int getID() {
		return id;
	}
	public static Square[] getAllSquares() {
		Square[] squares = new Square[NUM_SQUARES];
		//land, water, sky
		int i = 0;
		squares[i] = new Square(i++, false, false, true, "Square");
		squares[i] = new Square(i++, true, false, true, "Grass Square");
		squares[i] = new Square(i++, true, false, true, "Rocky Grass Square");
		squares[i] = new Square(i++, true, false, true, "Rock Square");					//glacial
		squares[i] = new Square(i++, true, false, true, "Fogstone Square");
		squares[i] = new Square(i++, true, false, true, "Forest Square");				//add to pine forest/deep forest rarely as well
		squares[i] = new Square(i++, false, true, true, "Ocean Square");
		squares[i] = new Square(i++, true, false, true, "Dirt Square");
		squares[i] = new Square(i++, true, false, true, "Sand Square");
		
		
		/*
		 * squares[i] = new Square(i++, true, false, true, "Cactus Sand Square");		//desert
		 * squares[i] = new Square(i++, true, false, true, "Cloudstone Square");		//mountain+cloud mountain
		 * squares[i] = new Square(i++, true, false, true, "ChromeStone Square");		//cloud mountain+glacial
		 * squares[i] = new Square(i++, true, false, true, "Shrubby Mountain Square");	//mountain
		 * squares[i] = new Square(i++, true, false, true, "Ice Square");				//glacial
		 * squares[i] = new Square(i++, false, true, true, "Aquastone Square");			//rarely in lakes (make small island floating off water)
		 * squares[i] = new Square(i++, false, true, true, "Coral Square");				//rarely in oceans, comes in clusters...?
		 * squares[i] = new Square(i++, true, false, true, "Bloodstone Square");		//lava biome
		 * squares[i] = new Square(i++, false, false, true, "Lava Square");				//lava biome
		 * squares[i] = new Square(i++, true, false, true, "Netrastone Square");		//lava biome
		 * squares[i] = new Square(i++, true, false, true, "Pine Forest Square");		//pine forest, cloud mountain, rarely in forest
		 * squares[i] = new Square(i++, true, false, true, "Pithstone Square");			//deep forest
		 * squares[i] = new Square(i++, true, false, true, "Tropical Forest Square");	//deep forest
		 * squares[i] = new Square(i++, true, false, true, "Giant Tree Square");		//deep forest
		 * 
		 * 
		 * Save random square data NOT temporarily in Drawable but PERMANENTLY!!!
		 * Then randomly generate a fourth type - city square - draws on any biome, but makes a city model on top of terrain
		 * So each square has a possible modifier on it! (this can also be changed...?)
		 * so city wreck, sea wreck, small town wreck, castle-like wreck, individual abandoned house, farm, church, enemy base
		 * 
		 * modifier can be built on: pavement, dirt road, stone road, stone bridge, obelisk, reinforced (like a base)
		 * contaminated
		 * 
		 * its MODIFIER that decides how passable terrain is, NOT the square!
		 * 
		 * Different square (not modifier) varieties have their own square ID! compare by name only!
		 */
		
		return squares;
	}
	public static TextureRegion[][] formatRegions(TextureRegion[][] pics) {
		System.out.println("Will be depreciated method");
		//Ore Rock = Rock
		TextureRegion[][] regions = new TextureRegion[NUM_SQUARES][];
		int sq = 0;
		int in;
		int col = 0;
		int size = 16;
		
		//space
		in = 0;
		regions[sq] = new TextureRegion[3];
		regions[sq][in++] = pics[col / size][col++ % size];
		regions[sq][in++] = pics[col / size][col++ % size];
		regions[sq][in++] = pics[col / size][col++ % size];
		sq++;
		
		//grass
		in = 0;
		regions[sq] = new TextureRegion[4];
		regions[sq][in++] = pics[col / size][col++ % size];
		regions[sq][in++] = pics[col / size][col++ % size];
		regions[sq][in++] = pics[col / size][col++ % size];
		regions[sq][in++] = pics[col / size][col++ % size];
		sq++;
		
		//rocks
		in = 0;
		regions[sq] = new TextureRegion[3];
		regions[sq][in++] = pics[col / size][col++ % size];
		regions[sq][in++] = pics[col / size][col++ % size];
		regions[sq][in++] = pics[col / size][col++ % size];
		sq++;
		
		//mountains
		in = 0;
		regions[sq] = new TextureRegion[3];
		regions[sq][in++] = pics[col / size][col++ % size];
		regions[sq][in++] = pics[col / size][col++ % size];
		regions[sq][in++] = pics[col / size][col++ % size];
		sq++;
		
		//ores
		in = 0;
		regions[sq] = new TextureRegion[3];
		regions[sq][in] = regions[sq - 1][in++];
		regions[sq][in] = regions[sq - 1][in++];
		regions[sq][in] = regions[sq - 1][in++];
		sq++;
		
		//woodlands
		in = 0;
		regions[sq] = new TextureRegion[3];
		regions[sq][in++] = pics[col / size][col++ % size];
		regions[sq][in++] = pics[col / size][col++ % size];
		regions[sq][in++] = pics[col / size][col++ % size];
		sq++;
		
		//oceans
		in = 0;
		regions[sq] = new TextureRegion[3];
		regions[sq][in++] = pics[col / size][col++ % size];
		regions[sq][in++] = pics[col / size][col++ % size];
		regions[sq][in++] = pics[col / size][col++ % size];
		sq++;
		
		//dirt
		in = 0;
		regions[sq] = new TextureRegion[2];
		regions[sq][in++] = pics[col / size][col++ % size];
		regions[sq][in++] = pics[col / size][col++ % size];
		sq++;
		
		//desert
		in = 0;
		regions[sq] = new TextureRegion[2];
		regions[sq][in++] = pics[col / size][col++ % size];
		regions[sq][in++] = pics[col / size][col++ % size];
		sq++;
		
		return regions;
	}
	public static void mine(int level, Vehicle y, int xpos, int ypos) {
		Player z = y.getPlayer();
		GameGrid squares = y.getSquares();
		if (squares.getID(xpos, ypos) == 4 && level >= 1) {
			if (Math.random() < 0.01)
				squares.setSquare(xpos, ypos, 3);
			//y.addWait(300);
			double rand = Math.random();
			if (rand < 0.005)
				z.addItem(Item.getItem("Topaz"));
			else if (rand < 0.02)
				z.addItem(Item.getItem("GoldOre"));
			else if (rand < 0.22)
				z.addItem(Item.getItem("CopperOre"));
			else if (rand < 0.42)
				z.addItem(Item.getItem("IronOre"));
			else if (rand < 0.8)
				z.addItem(Item.getItem("StoneBits"));
		}
	}
	public static void log(int level, Vehicle y, int xpos, int ypos) {
		Player z = y.getPlayer();
		GameGrid squares = y.getSquares();
		if (squares.getID(xpos, ypos) == 5) {
			if (Math.random() < 0.05)
				squares.setSquare(xpos, ypos, 7);
			//y.addWait(250);
			double rand = Math.random();
			if (rand < 0.5)
				z.addItem(Item.getItem("Log"));
		}
	}
}