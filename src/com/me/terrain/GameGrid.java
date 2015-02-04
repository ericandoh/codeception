package com.me.terrain;

import com.me.codeception.CodeGame;
import com.me.entities.*;
import com.me.io.IO;
import com.me.panels.GameScreen;
import com.me.render.GameRenderer;


import java.util.ArrayList;

public class GameGrid {
	
	//public static final int SCREEN_WIDTH_SQUARE = 100;			//original 40 by 20
	//public static final int SCREEN_HEIGHT_SQUARE = 50;
	
	//public static final int TOTAL_WIDTH_SQUARE = 800;
	//public static final int TOTAL_HEIGHT_SQUARE = 800;
	
	//public static final double getSquareWidth() = WIDTH*1.0/SCREEN_WIDTH_SQUARE;
	//public static final double getSquareHeight() = HEIGHT*1.0/SCREEN_HEIGHT_SQUARE;
	
	public static final int TOTAL_NUM_PLANETS = 5;
	
	public static final int CHUNK_SIZE = 32;		//must be a multiple of 2
	
	public static final int SQUARE_LENGTH = 2;
	
	public static final float TIME_PER_FRAME = 0.00001f;		//0.00001f
	
	private Chunk[][] chunks;
	
	private Square[] squares;
	
	//private int xpos, ypos;
	//private int offx, offy;
	//private GridPanel squares;
	//private ArrayList<Planet> myPlanets;
	private ArrayList<Player> myPlayers;
	private ArrayList<Vehicle> myVehicles;
	private ArrayList<Vehicle> tempAddVehicles;
	private ArrayList<Vehicle> tempSubVehicles;
	
	private ArrayList<GameSprite> mySprites;
	
	//private int screenWidthSquare, screenHeightSquare;
	private String myName;
	
	private int totalWidthSquare;
	private int totalHeightSquare;
	
	private ArrayList<GameScreen> listeners;
	
	//private Timer timer;
	private GameThread thread;
	
	private String gameType;
	
	private float time;						//0 - night, 1 - day
	
	public GameGrid(String n, String gType) {
		Entity.entCount = 0;
		myName = n;
		//setBackground(Color.black);
		//screenWidthSquare = INI_SCREEN_WIDTH_SQUARE;
		//screenHeightSquare = INI_SCREEN_HEIGHT_SQUARE;
		myPlayers = new ArrayList<Player>();
		myVehicles = new ArrayList<Vehicle>();
		tempAddVehicles = new ArrayList<Vehicle>();
		tempSubVehicles = new ArrayList<Vehicle>();
		mySprites = new ArrayList<GameSprite>();
		//squares = new GridPanel(TOTAL_WIDTH_SQUARE, TOTAL_HEIGHT_SQUARE);
		generateTerrain();
		listeners = new ArrayList<GameScreen>();
		//listeners.add(g);
		//timer = new Timer(CodeGame.INTERVAL, this);
		//else {
			//squares = new GridPanel(0, 0);
			//xpos = 0;
			//ypos = 0;
		//}
		
		//offx = 0;
		//offy = 0;
		//myPlayer.useOutput(-300);
		gameType = gType;
	}
	
	public GameGrid(ArrayList<Player> p, int[][] sq, String n, String gType) {
		myName = n;
		//setBackground(Color.black);
		//xpos = x;
		//ypos = y;
		//offx = 0;
		//offy = 0;
		//screenWidthSquare = w;
		//screenHeightSquare = h;
		myPlayers = p;
		myVehicles = new ArrayList<Vehicle>();
		tempAddVehicles = new ArrayList<Vehicle>();
		tempSubVehicles = new ArrayList<Vehicle>();
		mySprites = new ArrayList<GameSprite>();
		squares = Square.getAllSquares();
		
		//timer = new Timer(CodeGame.INTERVAL, this);
		listeners = new ArrayList<GameScreen>();
		
		totalWidthSquare = sq.length;
		totalHeightSquare = sq[0].length;
		Square[][] sqs = new Square[totalWidthSquare][totalHeightSquare];
		for (int a = 0; a < totalWidthSquare; a++) {
			for (int b = 0; b < totalHeightSquare; b++) {
				sqs[a][b] = squares[sq[a][b]];
			}
		}
		
		chunks = new Chunk[totalWidthSquare / CHUNK_SIZE + 1][totalHeightSquare / CHUNK_SIZE + 1];
		for (int a = 0; a < chunks.length; a ++) {
			for (int b = 0; b < chunks[0].length; b ++) {
				chunks[a][b] = new Chunk(sqs, a * CHUNK_SIZE, b * CHUNK_SIZE, CHUNK_SIZE); 
			}
		}
		gameType = gType;
	}
	public void addGameListener(GameScreen g) {
		listeners.add(g);
	}
	private void generateTerrain() {
		
		squares = Square.getAllSquares();
		
		totalWidthSquare = 500;
		totalHeightSquare = 500;
		
		Square[][] temp = new Square[totalWidthSquare][totalHeightSquare];
		
		for (int x = 0; x < totalWidthSquare; x++) {
			for (int y = 0; y < totalHeightSquare; y++) {
				temp[x][y] = squares[0];
			}
		}
		
		ArrayList<Planet> myPlanets = new ArrayList <Planet>();
		makePlanets(TOTAL_NUM_PLANETS, myPlanets, temp);
		
		chunks = new Chunk[totalWidthSquare / CHUNK_SIZE + 1][totalHeightSquare / CHUNK_SIZE + 1];
		for (int x = 0; x < chunks.length; x ++) {
			for (int y = 0; y < chunks[0].length; y ++) {
				chunks[x][y] = new Chunk(temp, x * CHUNK_SIZE, y * CHUNK_SIZE, CHUNK_SIZE);
			}
		}
		
		int offx = (int) (myPlanets.get(0).getX() * SQUARE_LENGTH);
		int offy = (int) (myPlanets.get(0).getY() * SQUARE_LENGTH);
		myPlayers.add(new Player(this, offx, offy, 0.2f));
		//GameScreen panel = new GameScreen(null);
		myPlayers.add(new Player(this, offx+10, offy+10, 0.2f));
		//panel.rebuild(null, this, 1);
		System.out.println("Terrain generation has finished");
		System.out.println("Starting at " + (int) (myPlayers.get(0).getXPos()) + "/" + (int)(myPlayers.get(0).getYPos()));
 	}
	public void makePlanets(int number, ArrayList<Planet> myPlanets, Square[][] temp) {
		Planet addMe;
		for (int loop = 0; loop < number; loop++){
			addMe = new Planet (myPlanets, totalWidthSquare, totalHeightSquare);
			if (addMe.getX() != -1) {
				myPlanets.add(addMe);
			}
		}
		System.out.println("Setting into map structure...");
		for (int loop2 = 0; loop2 < myPlanets.size(); loop2++){
			myPlanets.get(loop2).addPlanet(squares, temp);
		}
	}
	public void initialize() {
		System.out.println("Adding entities/vehicles to the game...");
		//myEntities.add(new Entity(squares, 1, myPlayer, xpos/SQUARE_WIDTH+10, ypos/SQUARE_HEIGHT+10, 5, 0.7, 1, Entity.SKY));
		//myPlayer.addVehicle(VehicleHelper.makeVehicle(this, myPlayer, xpos/getSquareWidth(), ypos/getSquareHeight(), "PlayerUnit"));
		Player p = myPlayers.get(0);
		p.addVehicle(VehicleHelper.makeVehicle(this, p, p.getXPos(), p.getYPos(), 5, VehicleHelper.VEHICLE[1]));
		
		p = myPlayers.get(1);
		p.addItem(Item.getItem("IronBar", 1000));
		String program = "def chaos()\n" +
				"count=4\n" +
				"while(count>0)\n" +
				"count=count-1\n" +
				"build('IronGun')\n" +
				"clickmove(xpos,ypos+2)\n" +
				"build('WoodTurbine')\n" +
				"clickmove(xpos,ypos+2)\n" +
				"endwhile\n" +
				"send('IronGun','chaos2()')\n";
		p.addCommand("chaos", program);
		program = "def chaos2()\n" +
				"count=20\n" +
				"while(count>0)\n" +
				"count=count-1\n" +
				"turn(true)\n" +
				"turn(true)\n" +
				"turn(true)\n" +
				"turn(true)\n" +
				"shoot()\n" +
				"endwhile\n";
		p.addCommand("chaos2", program);
		p.addVehicle(VehicleHelper.makeVehicle(this, p, p.getXPos(), p.getYPos(), 5,  VehicleHelper.VEHICLE[1]));
		p.addQueue("print(5)");
		p.addQueue("send('PlayerUnit','chaos()')");
		
		/*
		double screenWidthSquare = MapPanel.DEFAULT_WIDTH_SQUARE * p.getScale();
		double screenHeightSquare = MapPanel.DEFAULT_HEIGHT_SQUARE * p.getScale();
		
		double xpos = p.getXPos()*screenWidthSquare - MapPanel.WIDTH/2;
		double ypos = p.getYPos()*screenHeightSquare - MapPanel.HEIGHT/2;
		xpos = xpos / screenWidthSquare;
		ypos = ypos / screenHeightSquare;	
		*/	
	}
	private class GameThread extends Thread {
		private boolean isRunning;
		private long lastTime;
		private long diffTime;
		public GameThread() {
			isRunning = true;
		}
		public void run() { 
			lastTime = System.nanoTime() / 1000;
			while(isRunning) {
				cycle();
				diffTime = System.nanoTime() / 1000 - lastTime;
				lastTime = diffTime + lastTime;
				try {
					if (diffTime >= CodeGame.INTERVAL)
						continue;
					else
						sleep(CodeGame.INTERVAL - diffTime);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			interrupt();
		}
		public void stopThread() {
			isRunning = false;
		}
	}
	public void pauseGame() {
		for (GameScreen p : listeners) {
			p.addText("Paused Game...");
		}
		//timer.stop();
		thread.stopThread();
		//change this to make a flag that gets raised at end of thread run method
	}
	
	public void resumeGame() {
		for (GameScreen p : listeners) {
			p.addText("Resuming Game...");
		}
		thread = new GameThread();
		thread.start();
		//timer.start();
		/*if (isInterrupted())
			run();
		else
			start();*/
		
	}
	public void updateRender(GameRenderer r) {
		int[] nums = r.getChunkRenderCoords();
		int startrow = nums[0];
		int startcol = nums[1];
		int endrow = nums[2];
		int endcol = nums[3];
		
		/*
		if (prev[0] < 0)
			prev[0] = 0;
		if (prev[1] < 0)
			prev[1] = 0;
		if (prev[2] > totalWidthSquare / CHUNK_SIZE)
			prev[2] = totalWidthSquare / CHUNK_SIZE;
		if (prev[3] > totalHeightSquare / CHUNK_SIZE)
			prev[3] = totalHeightSquare / CHUNK_SIZE;*/
		if (startrow < 0)
			startrow = 0;
		if (startcol < 0)
			startcol = 0;
		if (endrow > totalWidthSquare / CHUNK_SIZE)
			endrow = totalWidthSquare / CHUNK_SIZE;
		if (endcol > totalHeightSquare / CHUNK_SIZE)
			endcol = totalHeightSquare / CHUNK_SIZE;
		
		
		/*
		for (int row = prev[0]; row < startrow; row++) {
			for (int col = prev[1]; col < startcol; col++) {
				chunks[row][col].removeRender();
			}
			for (int col = endcol; col < prev[3]; col++) {
				chunks[row][col].removeRender();
			}
		}
		for (int row = endrow; row < prev[2]; row++) {
			for (int col = prev[1]; col < startcol; col++) {
				chunks[row][col].removeRender();
			}
			for (int col = endcol; col < prev[3]; col++) {
				chunks[row][col].removeRender();
			}
		}*/
		//System.out.println("Rendering start coords" + startrow + "/" + startcol);
		//System.out.println("Rendering end coords" + endrow + "/" + endcol);
		//paint only if chunk has player's entities here! (FOG OF WAR)
		for (int row = startrow; row < endrow; row ++){
			for (int col = startcol; col < endcol; col ++) {
				chunks[row][col].updateRender(r);
			}
		}
		//paints all vehicles in chunk range!
		for (int row = startrow; row < endrow; row ++){
			for (int col = startcol; col < endcol; col ++) {
				//chunks[row][col].paintComponent(g, (int)(row*getSquareWidth()-xpos), (int)(col*getSquareHeight()-ypos), getSquareWidth(), getSquareHeight());
				//chunks[row][col].paintComponentVehicle(g, xpos, ypos, screenWidthSquare, screenHeightSquare);
				chunks[row][col].updateRenderVehicle(r);
				//squares[row][col].paintComponent(g, (int)(row*getSquareWidth()-xpos), (int)(col*getSquareHeight()-ypos), getSquareWidth(), getSquareHeight());
			}
		}
		if (r.getHPMode()) {
			for (int row = startrow; row < endrow; row ++){
				for (int col = startcol; col < endcol; col ++) {
					//chunks[row][col].paintComponent(g, (int)(row*getSquareWidth()-xpos), (int)(col*getSquareHeight()-ypos), getSquareWidth(), getSquareHeight());
					//chunks[row][col].paintComponentVehicle(g, xpos, ypos, screenWidthSquare, screenHeightSquare);
					chunks[row][col].updateRenderVehicleHP(r);
					//squares[row][col].paintComponent(g, (int)(row*getSquareWidth()-xpos), (int)(col*getSquareHeight()-ypos), getSquareWidth(), getSquareHeight());
				}
			}
		}
		if (r.getIconMode()) {
			for (int row = startrow; row < endrow; row ++){
				for (int col = startcol; col < endcol; col ++) {
					//chunks[row][col].paintComponent(g, (int)(row*getSquareWidth()-xpos), (int)(col*getSquareHeight()-ypos), getSquareWidth(), getSquareHeight());
					//chunks[row][col].paintComponentVehicle(g, xpos, ypos, screenWidthSquare, screenHeightSquare);
					chunks[row][col].updateRenderVehicleIcon(r);
					//squares[row][col].paintComponent(g, (int)(row*getSquareWidth()-xpos), (int)(col*getSquareHeight()-ypos), getSquareWidth(), getSquareHeight());
				}
			}
		}
		/*for (Entity t: myVehicles) {
			if (t.inRange(startrow, endrow, startcol, endcol)) {
				t.paintComponent(g, xpos, ypos, getSquareWidth(), getSquareHeight());
			}
		}*/
		for (GameSprite s : mySprites) {
			//s.paintComponent(g, xpos, ypos, screenWidthSquare, screenHeightSquare);
			s.updateRender(r);
		}
	}
	
	public void cycle() {
		if (tempAddVehicles.size() != 0) {
			for(Vehicle ent: tempAddVehicles) {
				myVehicles.add(ent);
				/*if (ent.getType().equals(Entity.SKY)) {
					myEntities.add(ent);
				}
				else {
					myEntities.add(0, ent);
				}*/
			}
			//myEntities.addAll(tempAddEntities);
			tempAddVehicles.clear();
		}
		if (tempSubVehicles.size() != 0) {
			for (Vehicle e: tempSubVehicles) {
				if (myVehicles.contains(e))
					myVehicles.remove(e);
			}
			tempSubVehicles.clear();
		}
		
		for (Player p : myPlayers) {
			p.cycle();
		}
		//myPlayer.cycle();
		for (Vehicle t: myVehicles) {
			t.cycle();
		}
		for (int s = mySprites.size() - 1; s >= 0; s--) {
			if (mySprites.get(s).expired())
				mySprites.remove(s);
			else
				mySprites.get(s).cycle();
		}
		
		time += TIME_PER_FRAME;
		if (time > 1)
			time = 0;
	}
	
	public float getTime() {
		return time;
	}
	
	/*public void moveDeg(double direction, double mag) {
		xpos += Math.round(mag*(double)getSquareWidth()*Math.cos(direction));
		ypos += Math.round(mag*(double)getSquareHeight()*Math.sin(direction));
	}*/
	/*
	public double getSquareWidth(Player p) {
		return MapPanel.DEFAULT_WIDTH_SQUARE * p.getScale();
	}
	public double getSquareHeight(Player p) {
		return MapPanel.DEFAULT_HEIGHT_SQUARE * p.getScale();
	}*/
	//these two methods used by entity for checking coords...?
	/*
	public int getTotalWidth() {
		return totalWidthSquare;
	}
	public int getTotalHeight() {
		return totalHeightSquare;
	}*/
	public void addSprite(GameSprite p) {
		mySprites.add(p);
	}
	/*
	public void increaseSize() {
		int tempxpos = (int) ((xpos+MAX_SCREEN_WIDTH_SQUARE/2)/(double)getSquareWidth());
		int tempypos = (int) ((ypos+MAX_SCREEN_HEIGHT_SQUARE/2)/(double)getSquareHeight());
		screenHeightSquare = (int)(screenHeightSquare * SCALE_FACTOR);
		screenWidthSquare = (int)(screenHeightSquare * SCALE_W_OVER_H);
		if (screenWidthSquare > MAX_SCREEN_WIDTH_SQUARE)
			screenWidthSquare = MAX_SCREEN_WIDTH_SQUARE;
		if (screenHeightSquare > MAX_SCREEN_HEIGHT_SQUARE)
			screenHeightSquare = MAX_SCREEN_HEIGHT_SQUARE;
		xpos = (int) (tempxpos*getSquareWidth()-MAX_SCREEN_WIDTH_SQUARE/2);
		ypos = (int) (tempypos*getSquareHeight()-MAX_SCREEN_HEIGHT_SQUARE/2);
	}
	public void decreaseSize() {
		int tempxpos = (int) ((xpos+MAX_SCREEN_WIDTH_SQUARE/2)/(double)getSquareWidth());
		int tempypos = (int) ((ypos+MAX_SCREEN_HEIGHT_SQUARE/2)/(double)getSquareHeight());
		screenHeightSquare = (int)(screenHeightSquare * 1.0 / SCALE_FACTOR);
		screenWidthSquare = (int)(screenHeightSquare * SCALE_W_OVER_H);
		if (screenWidthSquare < MIN_SCREEN_WIDTH_SQUARE)
			screenWidthSquare = MIN_SCREEN_WIDTH_SQUARE;
		if (screenHeightSquare < MIN_SCREEN_HEIGHT_SQUARE)
			screenHeightSquare = MIN_SCREEN_HEIGHT_SQUARE;
		xpos = (int) (tempxpos*getSquareWidth()-MAX_SCREEN_WIDTH_SQUARE/2);
		ypos = (int) (tempypos*getSquareHeight()-MAX_SCREEN_HEIGHT_SQUARE/2);
	}*/
	public void addVehicle(Vehicle v) {
		tempAddVehicles.add(v);
		
		double range = v.getFarDist();
		double x = v.getXPos() / SQUARE_LENGTH;
		double y = v.getYPos() / SQUARE_LENGTH;
		int xlowChunk = (int)((x - range) / CHUNK_SIZE);
		int xhighChunk = (int)((x + range) / CHUNK_SIZE);
		int ylowChunk = (int)((y - range) / CHUNK_SIZE);
		int yhighChunk = (int)((y + range) / CHUNK_SIZE);
		//System.out.println("Adding vehicle "+v+" at chunks ["+xlowChunk+","+xhighChunk+"] to ["+ylowChunk+","+yhighChunk+"]");
		for (int a = xlowChunk; a <= xhighChunk; a++) {
			for (int b = ylowChunk; b <= yhighChunk; b++) {
				chunks[a][b].addVehicle(v);
			}
		}
		
		//chunks[(int)(x / CHUNK_SIZE)][(int)(y / CHUNK_SIZE)].addVehicle(v);
		//wtf is this
	}
	public void removeVehicle(Vehicle v) {
		tempSubVehicles.add(v);
		
		double range = v.getFarDist();
		double x = v.getXPos() / SQUARE_LENGTH;
		double y = v.getYPos() / SQUARE_LENGTH;
		int xlowChunk = (int)((x - range) / CHUNK_SIZE);
		int xhighChunk = (int)((x + range) / CHUNK_SIZE);
		int ylowChunk = (int)((y - range) / CHUNK_SIZE);
		int yhighChunk = (int)((y + range) / CHUNK_SIZE);
		//System.out.println("Removing vehicle "+v+" at chunks ["+xlowChunk+","+xhighChunk+"] to ["+ylowChunk+","+yhighChunk+"]");
		for (int a = xlowChunk; a <= xhighChunk; a++) {
			for (int b = ylowChunk; b <= yhighChunk; b++) {
				chunks[a][b].removeVehicle(v);
			}
		}
	}
	
	
	private float getDst(float x1, float y1, float x2, float y2) {
		return (float)(Math.sqrt(Math.pow((x1-x2), 2) + Math.pow((y1-y2), 2)));
	}
	private float getDst(float x1, float y1, float z1, float x2, float y2, float z2) {
		return (float)(Math.sqrt(Math.pow((x1-x2), 2) + Math.pow((y1-y2), 2) + Math.pow((z1-z2), 2)));
	}
	//used by renderer to get list of vehicles that could be picked by camera (in a circle less than far dist)
	//used by radarpanel to get list of vehicles in radar screen
	public ArrayList<Vehicle> getVehicleIgnoreHeight(float xpick, float ypick, float far) {
		//scale to square coordinates
		float x = xpick / SQUARE_LENGTH;
		float y = ypick / SQUARE_LENGTH;
		far = far / SQUARE_LENGTH;
		ArrayList<Vehicle> returnMe = new ArrayList<Vehicle>();
		ArrayList<Vehicle> temp;
		//cycle through relevant chunks
		for (int p = (int) ((x - far) / CHUNK_SIZE); p <= (int)((x + far) / CHUNK_SIZE); p++) {
			for (int q = (int) ((y - far) / CHUNK_SIZE); q <= (int)((y + far) / CHUNK_SIZE); q++) {
				if (p < 0 || p >= chunks.length || q < 0 || q >= chunks[0].length)
					continue;
				temp = chunks[p][q].getVehicle();
				for (Vehicle v : temp) {
					if (returnMe.contains(v))
						continue;
					if (getDst(xpick, ypick, (float)v.getXPos(), (float)v.getYPos()) < far) {
						returnMe.add(v);
					}
				}
			}
		}
		return returnMe;
	}
	//used by fakeai for getveh and getenemyveh commands
	public ArrayList<Vehicle> getVehicleWithHeight(float x, float y, float z, float far) {
		//scale to square coordinates
		x = x / SQUARE_LENGTH;
		y = y / SQUARE_LENGTH;
		far = far / SQUARE_LENGTH;
		ArrayList<Vehicle> returnMe = new ArrayList<Vehicle>();
		ArrayList<Vehicle> temp;
		//cycle through relevant chunks
		for (int p = (int) ((x - far) / CHUNK_SIZE); p <= (int)((x + far) / CHUNK_SIZE); p++) {
			for (int q = (int) ((y - far) / CHUNK_SIZE); q <= (int)((y + far) / CHUNK_SIZE); q++) {
				if (p < 0 || p >= chunks.length || q < 0 || q >= chunks[0].length)
					continue;
				temp = chunks[p][q].getVehicle();
				for (Vehicle v : temp) {
					if (returnMe.contains(v))
						continue;
					if (getDst(x, y, z, (float)v.getXPos(), (float)v.getYPos(), (float)v.getZPos()) < far) {
						returnMe.add(v);
					}
				}
			}
		}
		return returnMe;
	}
	public static ArrayList<Vehicle> filterByPlayer(ArrayList<Vehicle> vehs, Player p, boolean includePlayer) {
		if (includePlayer) {
			for (int i = vehs.size() - 1; i >= 0; i--) {
				if (vehs.get(i).getPlayer() != p)
					vehs.remove(i);
			}
		}
		else {
			for (int i = vehs.size() - 1; i >= 0; i--) {
				if (vehs.get(i).getPlayer() == p)
					vehs.remove(i);
			}
		}
		return vehs;
	}
	
	/*
	public ArrayList<Vehicle> getVehicle(float x, float y, float near, float far) {
		x = x / SQUARE_LENGTH;
		y = y / SQUARE_LENGTH;
		near = near / SQUARE_LENGTH;
		far = far / SQUARE_LENGTH;
		ArrayList<Vehicle> returnMe = new ArrayList<Vehicle>();
		for (int p = (int) ((x - far) / CHUNK_SIZE); p <= (int)((x + far) / CHUNK_SIZE); p++) {
			for (int q = (int) ((y - far) / CHUNK_SIZE); q <= (int)((y + far) / CHUNK_SIZE); q++) {
				if (p < 0 || p >= chunks.length || q < 0 || q >= chunks[0].length)
					continue;
				returnMe.addAll(chunks[p][q].getVehicle());
			}
		}
		return returnMe;
	}*/
	/*public ArrayList<Vehicle> getVehicle(double x, double y) {
		ArrayList<Vehicle> returnMe = new ArrayList<Vehicle>();
		ArrayList<Vehicle> candidates = chunks[(int)(x / CHUNK_SIZE)][(int)(y / CHUNK_SIZE)].getVehicle();
		for (Vehicle veh : candidates) {
			if (veh.getDist(x, y) <= veh.getFarDist()) {
				for (Entity ent : veh.getEntities()) {
					if (Math.abs(ent.getXPos() - x) <= 0.5 && Math.abs(ent.getYPos() - y) <= 0.5) {
						returnMe.add(veh);
						break;
					}
				}
			}
		}
		return returnMe;
	}*/
	/*
	public ArrayList<Vehicle> getVehicle(double x, double y, double dist, Player p, boolean inc) {
		ArrayList<Vehicle> returnMe = new ArrayList<Vehicle>();
		ArrayList<Vehicle> candidates = chunks[(int)(x / CHUNK_SIZE)][(int)(y / CHUNK_SIZE)].getVehicle();
		if (inc) {
			for (Vehicle veh : candidates) {
				if (veh.getPlayer() == p && veh.getDist(x, y) <= veh.getFarDist()+dist) {
					for (Entity ent : veh.getEntities()) {
						if (Math.abs(ent.getXPos() - x) <= 0.5+dist && Math.abs(ent.getYPos() - y) <= 0.5+dist) {
							returnMe.add(veh);
							break;
						}
					}
				}
			}
		} 
		else {
			for (Vehicle veh : candidates) {
				if (veh.getPlayer() != p && veh.getDist(x, y) <= veh.getFarDist()+dist) {
					for (Entity ent : veh.getEntities()) {
						if (Math.abs(ent.getXPos() - x) <= 0.5+dist && Math.abs(ent.getYPos() - y) <= 0.5+dist) {
							returnMe.add(veh);
							break;
						}
					}
				}
			}
		}
		return returnMe;
	}*/
	//used for getting all vehicles inside a highlighted box
	/*
	public ArrayList<Vehicle> getVehicle(double x1, double y1, double x2, double y2, Player p) {
		double x = (x1 + x2)/2;
		double y = (y1 + y2)/2;
		double xdist = Math.abs(x1 - x2) / 2;
		double ydist = Math.abs(y1 - y2) / 2;
		double tdist = Math.sqrt(Math.pow(xdist, 2)+Math.pow(ydist, 2));
		ArrayList<Vehicle> returnMe = new ArrayList<Vehicle>();
		ArrayList<Vehicle> candidates = chunks[(int)(x / CHUNK_SIZE)][(int)(y / CHUNK_SIZE)].getVehicle();
		boolean shouldAdd;
		for (Vehicle veh : candidates) {
			if (veh.getPlayer() == p && veh.getDist(x, y) <= veh.getFarDist()+tdist) {
				shouldAdd = true;
				for (Entity ent : veh.getEntities()) {
					if (!(Math.abs(ent.getXPos() - x) <= 0.5+xdist && Math.abs(ent.getYPos() - y) <= 0.5+ydist)) {
						shouldAdd = false;
						break;
					}
				}
				if (shouldAdd) {
					returnMe.add(veh);
				}
			}
		}
		return returnMe;
	}*/
	//used by getVeh command in getting a vehicle at a specific coord...?
	/*
	public ArrayList<Vehicle> getVehicleFiltered(double x, double y, Player p) {
		ArrayList<Vehicle> newVehs = new ArrayList<Vehicle>(); 
		ArrayList<Vehicle> vehs = getVehicle(x, y);
		for (Vehicle veh : vehs) {
			if (p == veh.getPlayer()) {
				newVehs.add(veh);
			}
		}
		return newVehs;
	}*/
	
	public boolean canPass(double x, double y, double z, String type, Vehicle veh) {
		//also CHECK FOR TYPE SHIFT!!!
		//check for type mismatch (boat on land), collisions (boom!)
		return chunks[(int)((x / SQUARE_LENGTH) / CHUNK_SIZE)][(int)((y / SQUARE_LENGTH) / CHUNK_SIZE)].canPass(x, y, z, type, veh);
	}
	public boolean canPassRot(double x, double y, double degChange, String type, Vehicle veh) {
		//also CHECK FOR TYPE SHIFT!!!
		//check for type mismatch (boat on land), collisions (boom!)
		return chunks[(int)((x / SQUARE_LENGTH) / CHUNK_SIZE)][(int)((y / SQUARE_LENGTH) / CHUNK_SIZE)].canPassRot(x, y, degChange, type, veh);
	}
	public void update(Vehicle v, double x, double y, double xshift, double yshift, double range) {
		//update vehicle into new position with shift and type!
		x = x / SQUARE_LENGTH;
		y = y / SQUARE_LENGTH;
		xshift = xshift / SQUARE_LENGTH;
		yshift = yshift / SQUARE_LENGTH;
		range = range / SQUARE_LENGTH;
		int xlowChunk = (int)((x - range) / CHUNK_SIZE);
		int xhighChunk = (int)((x + range) / CHUNK_SIZE);
		int ylowChunk = (int)((y - range) / CHUNK_SIZE);
		int yhighChunk = (int)((y + range) / CHUNK_SIZE);
		int newxlowChunk = (int)((x - range + xshift) / CHUNK_SIZE);
		int newxhighChunk = (int)((x + range + xshift) / CHUNK_SIZE);
		int newylowChunk = (int)((y - range + yshift) / CHUNK_SIZE);
		int newyhighChunk = (int)((y + range + yshift) / CHUNK_SIZE);
		for (int a = xlowChunk; a <= xhighChunk; a++) {
			for (int b = ylowChunk; b <= yhighChunk; b++) {
				if (newxlowChunk <= a && a <= newxhighChunk && newylowChunk <= b && b <= newyhighChunk) {
					//do nothing - no need to update. Repetitious squares
				}
				else {
					chunks[a][b].removeVehicle(v);
				}
			}
		}
		for (int c = newxlowChunk; c <= newxhighChunk; c++) {
			for (int d = newylowChunk; d <= newyhighChunk; d++) {
				if (xlowChunk <= c && c <= xhighChunk && ylowChunk <= d && d <= yhighChunk) {
					//do nothing - no need to update. Repetitious squares
				}
				else {
					chunks[c][d].addVehicle(v);
				}
			}
		}
	}
	public ArrayList<Vehicle> getNearbyVehicles(Vehicle v, Player p) {
		return chunks[(int)((v.getXPos() / SQUARE_LENGTH) / CHUNK_SIZE)][(int)((v.getYPos() / SQUARE_LENGTH) / CHUNK_SIZE)].getNearby(v, p);
	}
	public ArrayList<Entity> getNearbyEntities(Vehicle v) {
		return chunks[(int)((v.getXPos() / SQUARE_LENGTH) / CHUNK_SIZE)][(int)((v.getYPos() / SQUARE_LENGTH) / CHUNK_SIZE)].getNearbyEntities(v);
	}
	public int getID(int x, int y) {
		return getSquare(x, y).getID();
	}
	public Player getPlayer(int index) {
		if (index < 0 || index >= myPlayers.size()) {
			return null;
		}
		return myPlayers.get(index);
	}
	public ArrayList<Player> getAllPlayers() {
		return myPlayers;
	}
	public void setSquare(int x, int y, int id) {
		setSquare(x, y, squares[id]);
	}
	public Square getSquare(double xp, double yp) {
		int x = (int)Math.round(xp / SQUARE_LENGTH);
		int y = (int)Math.round(yp / SQUARE_LENGTH);
		if ( x < 0 || y < 0 || x >= totalWidthSquare || y >= totalHeightSquare ) {
			return null;
		}
		else {
			return chunks[(int)(x / CHUNK_SIZE)][(int)(y / CHUNK_SIZE)].getSquare(x % CHUNK_SIZE, y % CHUNK_SIZE);
		}
	}
	public void setSquare(int x, int y, Square unit){
		x = x / SQUARE_LENGTH;
		y = y / SQUARE_LENGTH;
		if ( x < 0 || y < 0 || x >= totalWidthSquare || y >= totalHeightSquare ) {
		}
		else {
			//squares[x][y] = unit;
			chunks[(int)(x / CHUNK_SIZE)][(int)(y / CHUNK_SIZE)].setSquare(x % CHUNK_SIZE, y % CHUNK_SIZE, unit);
		}
	}
	
	public Square[][] getArraySquares() {
		Square[][] sq = new Square[totalWidthSquare][totalHeightSquare];
		for (int row = 0; row < chunks.length; row ++){
			for (int col = 0; col < chunks[0].length; col ++) {
				chunks[row][col].addIntoArray(sq, row*CHUNK_SIZE, col*CHUNK_SIZE);
			}
		}
		return sq;
	}
	public void addAllPlayers(ArrayList<Player> p) {
		myPlayers = p;
	}
	
	
	public void saveFile() {
		IO.saveFile(this, myName, gameType);
	}
	public String getGameType() {
		return gameType;
	}
	public String encode() {
		return Entity.entCount + "\n";
	}
	public String encodeConn() {
		String returnMe = "";
		for (Vehicle ent: myVehicles) {
			returnMe = returnMe + ent.getSerial() + "$";
		}
		return returnMe + "\n";
	}
	public static GameGrid decode(String line, ArrayList<Player> p, int[][] sq, String n, String gType) {
		String[] firstLine = line.split("\\$");
		//int xpos = Integer.parseInt(firstLine[0]);
		//int ypos = Integer.parseInt(firstLine[1]);
		//int screenWidthSquare = Integer.parseInt(firstLine[2]);
		//int screenHeightSquare = Integer.parseInt(firstLine[3]);
		Entity.entCount = Integer.parseInt(firstLine[0]);
		return new GameGrid(p, sq, n, gType);
	}
	/*public void decodeConn(ArrayList<String>lines, int index, HashMap<Integer, Vehicle> ents) {
		System.out.println("Does this even work?");
		String [] ids = lines.get(index).split("\\$");
		Vehicle ent;
		for (String x: ids) {
			ent = ents.get(Integer.parseInt(x));
			myPlayer.addVehicle(ent);
		}
 	}*/
	/*
	public String getMapString() {
		return "";
	}
	public String getEntString() {
		return "";
	}
	public String getPlayerString() {
		return "";
	}
	public String getGameString() {
		return "";
	}*/
}
