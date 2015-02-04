package com.me.terrain;

import java.util.ArrayList;

import com.me.entities.Entity;
import com.me.entities.Player;
import com.me.entities.Position;
import com.me.entities.Vehicle;
import com.me.render.GameRenderer;
import com.me.render.SquaresDrawable;



public class Chunk {
	
	public static final int SQUARE_LENGTH = GameGrid.SQUARE_LENGTH;
	public static final int CHUNK_SIZE = GameGrid.CHUNK_SIZE;
	
	private Square[][] squares;
	private ArrayList<Vehicle> entities;
	private SquaresDrawable myDrawable;
	private Position pos;
	//list of entities in this chunk
	public Chunk(Square[][] map, int x, int y, int length) {
		squares = new Square[length][length];
		int lowXBound = Math.max(x, 0);
		int lowYBound = Math.max(y, 0);
		for (int p = lowXBound; p < Math.min(x + length, map.length); p++) {
			for (int q = lowYBound; q < Math.min(y + length, map[0].length); q++) {
				squares[p - lowXBound][q - lowYBound] = map[p][q];
			}
		}
		entities = new ArrayList<Vehicle>();
		pos = new Position(x, y, 0);
		myDrawable = new SquaresDrawable(pos, squares);
	}
	public Chunk(Square sq, int x, int y, int length) {
		squares = new Square[length][length];
		int lowXBound = Math.max(x, 0);
		int lowYBound = Math.max(y, 0);
		for (int p = lowXBound; p < x + length; p++) {
			for (int q = lowYBound; q < y + length; q++) {
				squares[p - lowXBound][q - lowYBound] = sq;
			}
		}
		entities = new ArrayList<Vehicle>();
		pos = new Position(x, y, 0);
		myDrawable = new SquaresDrawable(pos, squares);
	}
	public void updateRender(GameRenderer r) {
		boolean centered = r.inRangeChunk((float)((pos.xpos+CHUNK_SIZE/2)*SQUARE_LENGTH), 
				(float)((pos.ypos+CHUNK_SIZE/2)*SQUARE_LENGTH), CHUNK_SIZE*2);
		myDrawable.setCentered(centered);
		r.addToRenderSprite(myDrawable);
		if (centered)
			r.addToRender(myDrawable);
	}
	public void updateRenderVehicle(GameRenderer r) {
		for (Vehicle veh : entities) {
			//veh.paintComponent(g, xpos, ypos, squareWidth, squareHeight);
			veh.updateRender(r);
		}
	}
	public void updateRenderVehicleHP(GameRenderer r) {
		for (Vehicle veh : entities) {
			//veh.paintComponent(g, xpos, ypos, squareWidth, squareHeight);
			veh.updateRenderHP(r);
		}
	}
	public void updateRenderVehicleIcon(GameRenderer r) {
		for (Vehicle veh : entities) {
			//veh.paintComponent(g, xpos, ypos, squareWidth, squareHeight);
			veh.updateRenderIcon(r);
		}
	}
	public void addVehicle(Vehicle x) {
		entities.add(x);
	}
	public void removeVehicle(Vehicle x) {
		entities.remove(x);
	}
	public ArrayList<Vehicle> getVehicle() {
		ArrayList<Vehicle> vehs = new ArrayList<Vehicle>();
		vehs.addAll(entities);
		return vehs;
	}
	public boolean canPass(double x, double y, double z, String type, Vehicle veh) {
		//check if can pass type (ex. lava)
		for (Vehicle v: entities) {
			if (v != veh && veh.inRangeCheckDestroyed(x, y, z, v, 0.9, false)) {
				return false;
			}
		}
		return true;
	}
	public boolean canPassRot(double x, double y, double degChange, String type, Vehicle veh) {
		//check if can pass type (ex. lava)
		Vehicle v;
		for (int i = 0; i < entities.size(); i++) {
			v = entities.get(i);
			if (v != veh && veh.inRangeRot(x, y, degChange, v, 0.9)) {
				return false;
			}
		}
		
		/* This caused a concurrent mod exception...? 
		for (Vehicle v: entities) {
			if (v != veh && veh.inRangeRot(x, y, degChange, v, 0.9)) {
				return false;
			}
		}*/
		return true;
	}
	public ArrayList<Vehicle> getNearby(Vehicle veh, Player p) {
		//check if can pass type (ex. lava)
		/*ArrayList<Vehicle> list = veh.getType().equals(Entity.SKY) ? skyEntities : groundEntities;
		ArrayList<Vehicle> candidates = new ArrayList<Vehicle>();
		for (Vehicle v: list) {
			if (v != veh && v.inRange(veh.getXPos(), veh.getYPos(), veh, 1.2)) {
				candidates.add(v);
			}
		}*/
		ArrayList<Vehicle> candidates = new ArrayList<Vehicle>();
		for (Vehicle v: entities) {
			if (v.getPlayer() == p && v.inRange(veh.getXPos(), veh.getYPos(), veh.getZPos(), veh, 1.2) && v != veh) {
				if (v.isSelected()) {
					candidates.add(0, v);
				}
				else {
					candidates.add(v);
				}
			}
		}
		return candidates;
	}
	public ArrayList<Entity> getNearbyEntities(Vehicle veh) {
		ArrayList<Entity> candidates = new ArrayList<Entity>();
		for (Vehicle v: entities) {
			if (v != veh) {
				candidates.addAll(v.inRangeEnt(veh, 1.5));
			}
		}
		return candidates;
	}
	public Square getSquare(int x, int y) {
		return squares[x][y];
	}
	public void setSquare(int x, int y, Square unit) {
		squares[x][y] = unit;
		myDrawable.updateSquare(x, y);
	}
	public void addIntoArray(Square[][] sq, int x, int y) {
		for (int p = x; p < Math.min(squares.length + x, sq.length); p++) {
			for (int q = y; q < Math.min(squares[0].length + y, sq[0].length); q++) {
				sq[p][q] = squares[p - x][q - y];
			}
		}
	}
}
