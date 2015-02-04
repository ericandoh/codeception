package com.me.terrain;

public class BiomePoint {
	protected int xpos, ypos;
	public BiomePoint (int x, int y) {
		xpos = x;
		ypos = y;
	}
	public int getX() {
		return xpos;
	}
	public int getY() {
		return ypos;
	}
	public Square getSquare(Square[] sq) {
		return sq[3];
	}
	public boolean canMix() {
		return true;
	}
	public static Square getByName(Square[] sqs, String n) {
		for (Square sq : sqs) {
			if (sq.toString().equals(n)) {
				return sq;
			}
		}
		return sqs[0];
	}
}
class BiomePointHandler {
	public BiomePoint returnBiomePoint (int x, int y, int t) {
		//specifies type of planet, then from there the specific biomes in the planet are randomized
		if (t == 0) {							//Earth!
			int num = (int)(Math.random()*5.0);
			if (num == 0) {
				return new GrassBiomePoint(x, y);
			}
			else if (num == 1) {
				return new MtnBiomePoint(x, y);
			}
			else if (num == 2) {
				return new OceanBiomePoint(x, y);
			}
			else if (num == 3) {
				return new ForestBiomePoint(x, y);
			}
			else if (num == 4) {
				return new DesertBiomePoint(x, y);
			}
			else {
				return null;
			}
		}
		else if (t == 1) {						//woodland terrain
			return null;
		}
		else if (t == 2) {						//lava-y
			return null;
		}
		else {
			return null;
		}
	}
}
class GrassBiomePoint extends BiomePoint {
	public GrassBiomePoint(int x, int y) {
		super(x, y);
	}
	public Square getSquare(Square[] sq) {
		int num = (int)(Math.random()*100);
		if (num <= 5) {
			return getByName(sq, "Rocky Grass Square");
		}
		else {
			return getByName(sq, "Grass Square");
		}
	}
}
class MtnBiomePoint extends BiomePoint {
	public MtnBiomePoint (int x, int y) {
		super(x,y);
	}
	public Square getSquare(Square[] sq) {
		int num = (int)(Math.random()*100);
		if (num <= 3) {
			return getByName(sq, "Fogstone Square");
		}
		else if (num <= 15) {
			return getByName(sq, "Forest Square");
		}
		else {
			return getByName(sq, "Rock Square");
		}
	}
}

class OceanBiomePoint extends BiomePoint {
	public OceanBiomePoint (int x, int y) {
		super(x,y);
	}
	public Square getSquare(Square[] sq) {
		return getByName(sq, "Ocean Square");
	}
	public boolean canMix() {
		return false;
	}
}
class ForestBiomePoint extends BiomePoint {
	public ForestBiomePoint (int x, int y) {
		super(x,y);
	}
	public Square getSquare(Square[] sq) {
		int num = (int)(Math.random()*100);
		if (num <= 3) {
			return getByName(sq, "Rock Square");
		}
		else if (num <=10) {
			return getByName(sq, "Grass Square");
		}
		else {
			return getByName(sq, "Forest Square");
		}
	}
}
class DesertBiomePoint extends BiomePoint {
	public DesertBiomePoint (int x, int y) {
		super(x,y);
	}
	public Square getSquare(Square[] sq) {
		int num = (int)(Math.random()*100);
		if (num <= 2) {
			return getByName(sq, "Rock Square");
		}
		else {
			return getByName(sq, "Sand Square");
		}
	}
}

/*
class LavaBiomePoint extends BiomePoint {
	public LavaBiomePoint (int x, int y) {
		super(x,y);
	}
	public Square getSquare(Square[] sq) {
		int num = (int)(Math.random()*10);
		if (num <= 3) {
			return sq[4];
		}
		else {
			return sq[6];
		}
	}
}*/