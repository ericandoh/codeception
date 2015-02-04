/*
 * Saver.java
 * 
 * Saves a GridPanel object into data
 * It makes the following text files under a directory of the save file name:
 * 
 * map, entities, player, game
 * 
 * 
 */

package com.me.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


import com.me.codeception.CodeGame;
import com.me.entities.Entity;
import com.me.entities.Player;
import com.me.entities.Vehicle;
import com.me.entities.SmartVehicle;
import com.me.entities.VehicleHelper;
import com.me.panels.GameScreen;
import com.me.terrain.GameGrid;
import com.me.terrain.Square;



public class IO {
	
	public static final String EXT = ".sfi";
	public static final String SAVE_EXT = "saves\\";
	
	public static final String BASE_CAMP_EXT = "campaign\\";
	public static final String USER_CAMP_EXT = "campaignsaves\\";
	
	public static final String MAP = "map" + EXT;
	public static final String VEH = "veh" + EXT;
	public static final String ENT = "ent" + EXT;
	public static final String PLAYER = "player" + EXT;
	public static final String GAME = "sys" + EXT;
	public static final String CONN = "conn" + EXT;
	
	public static void saveFile(GameGrid p, String name, String type) {
		ArrayList<Player> me = p.getAllPlayers();
		ArrayList<ArrayList<Vehicle>>ents = new ArrayList<ArrayList<Vehicle>>();
		for (Player player : me) {
			ents.add(player.getAllVehicles());
		}
		
		String ext;
		if (type.equals(GameScreen.USER_CAMP)) {
			ext = System.getProperty("user.dir") + "\\" + USER_CAMP_EXT;
		}
		else {
			ext = System.getProperty("user.dir") + "\\" + SAVE_EXT;
		}		
		
		File bigDir = new File(ext);
		if (!bigDir.exists()) {
			if (!bigDir.mkdir()) {
				System.out.println("Could not make directory!");
				return;
			}
		}
		else {
			//System.out.println("Do you wish to overwrite...?");
		}
		if (!bigDir.isDirectory()) {
			System.out.println("Not a directory");
			return;
		}
		
		File mainDir = new File(ext + name);
		if (!mainDir.exists()) {
			if (!mainDir.mkdir()) {
				System.out.println("Could not make directory!");
				return;
			}
		}
		else {
			System.out.println("Do you wish to overwrite...?");
		}
		if (!mainDir.isDirectory()) {
			System.out.println("Not a directory");
			return;
		}
		File mapfile = new File(mainDir.getPath() + "\\" + MAP);
		Square[][] sq = p.getArraySquares();
		try {
			BufferedWriter mapWriter = new BufferedWriter(new FileWriter(mapfile, false));
			mapWriter.write("Map " + CodeGame.VERSION);
			mapWriter.newLine();
			for (int row = 0; row < sq.length; row++) {
				for (int col = 0; col < sq.length; col++) {
					mapWriter.write(sq[row][col].getID() + " ");
				}
				mapWriter.newLine();
			}
			mapWriter.write("End");
			mapWriter.close();
		} catch (IOException e) {
			System.out.println("IO Error!");
			e.printStackTrace();
		}
		
		ArrayList<Entity> entities = new ArrayList<Entity>();
		
		//change so that all entities (face and real) are encoded here with id's-done
		File vehfile = new File(mainDir.getPath() + "\\" + VEH);
		try {
			BufferedWriter vehWriter = new BufferedWriter(new FileWriter(vehfile, false));
			vehWriter.write("Vehicles " + CodeGame.VERSION);
			vehWriter.newLine();
			String output;
			for (ArrayList<Vehicle> vehlist: ents) {
				vehWriter.write("$Player Vehicles");
				vehWriter.newLine();
				for (Vehicle ent : vehlist) {
					if (ent instanceof SmartVehicle) {
						ArrayList<Vehicle> smallVList = ((SmartVehicle)ent).getVehicles();
						for (Vehicle v: smallVList) {
							output = v.encode();
							vehWriter.write(output);
							vehWriter.newLine();
						}
					}
					output = ent.encode().replaceAll("\n","\r\n");
					vehWriter.write(output);
					vehWriter.newLine();
					entities.addAll(ent.getEntities());
				}
			}
			vehWriter.write("End");
			vehWriter.close();
		} catch (IOException e) {
			System.out.println("IO Error!");
			e.printStackTrace();
		}
		
		File entfile = new File(mainDir.getPath() + "\\" + ENT);
		try {
			BufferedWriter entWriter = new BufferedWriter(new FileWriter(entfile, false));
			entWriter.write("Entities " + CodeGame.VERSION);
			entWriter.newLine();
			String output;
			for (Entity ent: entities) {
				output = ent.encode();
				entWriter.write(output);
				entWriter.newLine();
			}
			entWriter.write("End");
			entWriter.close();
		} catch (IOException e) {
			System.out.println("IO Error!");
			e.printStackTrace();
		}
		
		
		File playerfile = new File(mainDir.getPath() + "\\" + PLAYER);
		try {
			BufferedWriter playerWriter = new BufferedWriter(new FileWriter(playerfile, false));
			playerWriter.write("Player Info " + CodeGame.VERSION);
			playerWriter.newLine();
			
			String output;
			for (Player player : me) {
				playerWriter.write("$NewPlayr");
				playerWriter.newLine();
				output = player.encode().replaceAll("\n","\r\n");
				playerWriter.write(output);
				playerWriter.newLine();
			}
			playerWriter.write("End");
			playerWriter.close();
		} catch (IOException e) {
			System.out.println("IO Error!");
			e.printStackTrace();
		}
		
		File gamefile = new File(mainDir.getPath() + "\\" + GAME);
		try {
			BufferedWriter gameWriter = new BufferedWriter(new FileWriter(gamefile, false));
			gameWriter.write("Game Info " + CodeGame.VERSION);
			gameWriter.newLine();
			
			gameWriter.write(p.encode().replaceAll("\n","\r\n"));
			gameWriter.newLine();
			gameWriter.write("End");
			gameWriter.close();
		} catch (IOException e) {
			System.out.println("IO Error!");
			e.printStackTrace();
		}
		
		
		//Vehic - what has what under it (set owners as you do this also)
		//Game - what entities in main list of game, put entities into squares as you do this
		//Playr - what entities belong to player list
		
		
		File connfile = new File(mainDir.getPath() + "\\" + CONN);
		try {
			BufferedWriter connWriter = new BufferedWriter(new FileWriter(connfile, false));
			connWriter.write("Conn Info " + CodeGame.VERSION);
			connWriter.newLine();
			
			connWriter.write("$Vehic");
			connWriter.newLine();
			
			ArrayList<Vehicle> all = new ArrayList<Vehicle>();
			for (ArrayList<Vehicle> vehlist: ents) {
				all.addAll(vehlist);
			}
			for (Vehicle ent: all) {
				if (ent instanceof SmartVehicle) {
					for (Vehicle g : ((SmartVehicle)ent).getVehicles()) {
						connWriter.write(g.encodeConn());
						connWriter.newLine();
					}
				}
				connWriter.write(ent.encodeConn());
				connWriter.newLine();
			}
			connWriter.write("$Game");
			connWriter.newLine();
			connWriter.write(p.encodeConn().replaceAll("\n","\r\n"));
			connWriter.newLine();
			for (Player player : me) {
				connWriter.write("$Playr");
				connWriter.newLine();
				connWriter.write(player.encodeConn().replaceAll("\n","\r\n"));
				connWriter.newLine();
			}
			connWriter.write("End");
			connWriter.close();
		} catch (IOException e) {
			System.out.println("IO Error!");
			e.printStackTrace();
		}
		
		/*
		if (!str.contains("."))
			str += ".png";
		File directory = new File( System.getProperty("user.dir") + "\\sprites");
		if (!directory.exists())
			directory.mkdir();
		else if (!directory.isDirectory())
			return getDefaultImage();
		File image = new File(directory.getPath() + "\\" + str);
		if (image.exists() && image.isFile() && image.canRead()) {
			try {
				return ImageIO.read(image);
			} catch (IOException e) {
				return getDefaultImage();
			}
		}
		return getDefaultImage();
		 */
	}
	public static GameGrid readFile(String name, String type) {
		int sq[][] = null;
		HashMap<Integer, Entity> entMap = new HashMap<Integer, Entity>();
		HashMap<Integer, Vehicle> vehMap = new HashMap<Integer, Vehicle>();
		ArrayList<Vehicle> vehs = new ArrayList<Vehicle>();
		ArrayList<Player> me = new ArrayList<Player>();
		GameGrid panel = null;
		File mainDir;
		
		/*if (type.equals(GameScreen.CAMP)) {
			if (hasCampaignSaveFile(name)) {
				mainDir = new File(System.getProperty("user.dir") + "\\" + USER_CAMP_EXT + name);
			}
			else {
				mainDir = new File(System.getProperty("user.dir") + "\\" + BASE_CAMP_EXT + name);
			}
		}*/
		if (type.equals(GameScreen.BASE_CAMP)) {
			mainDir = new File(System.getProperty("user.dir") + "\\" + BASE_CAMP_EXT + name);
			type = GameScreen.USER_CAMP;
		}
		else if (type.equals(GameScreen.USER_CAMP)) {
			mainDir = new File(System.getProperty("user.dir") + "\\" + USER_CAMP_EXT + name);
		}
		else {
			mainDir = new File(System.getProperty("user.dir") + "\\" + SAVE_EXT + name);
		}
		if (!mainDir.exists()) {
			System.out.println(name + " does not exist");
			return null;
		}
		if (!mainDir.isDirectory()) {
			System.out.println("Not a directory");
			return null;
		}
		File mapfile = new File(mainDir.getPath() + "\\" + MAP);
		try {
			BufferedReader mapReader = new BufferedReader(new FileReader(mapfile));
			String line;
			ArrayList<String> lines = new ArrayList<String>();
			while ((line = mapReader.readLine()) != null) {
				lines.add(line);
			}
			String start = lines.get(0);
			String end = lines.get(lines.size() - 1);
			if (!start.equals("Map " + CodeGame.VERSION)) {
				System.out.println("Version may not match/Text file may be corrupted");
			}
			if (!end.equals("End")) {
				System.out.println("File missing End marker!");
			}
			int rows = lines.size() - 2;
			int cols = lines.get(1).split(" ").length;
			sq = new int[rows][cols];
			String [] oneLine;
			for (int row = 1; row < lines.size() - 1; row++) {
				oneLine = lines.get(row).split(" ");
				for (int col = 0; col < oneLine.length; col++) {
					//sq[row - 1][col] = Square.getSquare(Integer.parseInt(oneLine[col]));
					sq[row-1][col] = Integer.parseInt(oneLine[col]);	//tmeporary
				}
			}
			mapReader.close();
		} catch (FileNotFoundException e) {
			System.out.println("File not found!");
		}
		catch (IOException e) {
			System.out.println("IO Error!");
			e.printStackTrace();
		}
		
		
		
		File gamefile = new File(mainDir.getPath() + "\\" + GAME);
		try {
			BufferedReader gameReader = new BufferedReader(new FileReader(gamefile));
			
			String line;
			ArrayList<String> lines = new ArrayList<String>();
			while ((line = gameReader.readLine()) != null) {
				lines.add(line);
			}
			String start = lines.get(0);
			String end = lines.get(lines.size() - 1);
			if (!start.equals("Game Info " + CodeGame.VERSION)) {
				System.out.println("Version may not match/Text file may be corrupted");
			}
			if (!end.equals("End")) {
				System.out.println("File missing End marker!");
			}
			panel = GameGrid.decode(lines.get(1), me, sq, name, type);
			//g.setGrid(panel, type);
			
			gameReader.close();
		} catch (FileNotFoundException e) {
			System.out.println("File not found!");
		}
		catch (IOException e) {
			System.out.println("IO Error!");
			e.printStackTrace();
		}
		
		
		File playerfile = new File(mainDir.getPath() + "\\" + PLAYER);
		try {
			BufferedReader playerReader = new BufferedReader(new FileReader(playerfile));
			
			String line;
			ArrayList<String> lines = new ArrayList<String>();
			while ((line = playerReader.readLine()) != null) {
				lines.add(line);
			}
			String start = lines.get(0);
			String end = lines.get(lines.size() - 1);
			if (!start.equals("Player Info " + CodeGame.VERSION)) {
				System.out.println("Version may not match/Text file may be corrupted");
			}
			if (!end.equals("End")) {
				System.out.println("File missing End marker!");
			}
			//for (int row = 1; row < lines.size() - 1; row++) {
				
			//}
			//me = Player.decode(lines, 1, g);
			Player player;
			for (int row = 1; row < lines.size() - 1; row++) {
				if (lines.get(row).equals("$NewPlayr")) {
					player = Player.decode(lines, row+1, panel);
					//v = Vehicle.decode(lines, row + 1, me, panel);
					//ents.put(idNum, v);
					me.add(player);
					//WARNING : panel is currently null. change later. 
				}
			}
			playerReader.close();
		} catch (FileNotFoundException e) {
			System.out.println("File not found!");
		}
		catch (IOException e) {
			System.out.println("IO Error!");
			e.printStackTrace();
		}
		
		panel.addAllPlayers(me);
		
		//didnt set owners
		//fix encoding for vehicles/extended subclasses
		
		
		//didnt set what entities squares were holding
		
		//5th file = entity connections
		
		//what game sees
		//what player sees
		//what entities sees
		
		//problem - what about entity variables? maybe put into main hashmap, and then change val later? (so obj reference same but val changed)
		
		
		//here, add entities to map as you make them!
		
		
		File vehfile = new File(mainDir.getPath() + "\\" + VEH);
		//ents = new HashMap<Integer, Groupable>();
		//vehs = new ArrayList<Vehicle>();
		Vehicle v;
		
		try {
			BufferedReader vehReader = new BufferedReader(new FileReader(vehfile));
		
			String line;
			ArrayList<String> lines = new ArrayList<String>();
			while ((line = vehReader.readLine()) != null) {
				lines.add(line);
			}
			String start = lines.get(0);
			String end = lines.get(lines.size() - 1);
			if (!start.equals("Vehicles " + CodeGame.VERSION)) {
				System.out.println("Version may not match/Text file may be corrupted");
			}
			if (!end.equals("End")) {
				System.out.println("File missing End marker!");
			}
			
			//int idNum;
			int numPlayer = -1;
			for (int row = 1; row < lines.size() - 1; row++) {
				if (lines.get(row).equals("$Player Vehicles")) {
					numPlayer++;
				}
				else {
					//idNum = Integer.parseInt(lines.get(row+1));
					//v = Vehicle.decode(lines, row + 1, me.get(numPlayer), panel);
					v = VehicleHelper.makeVehicle(lines.get(row), me.get(numPlayer), panel);
					vehMap.put(v.getSerial(), v);
					vehs.add(v);
					//WARNING : panel is currently null. change later. 
				}
			}
			vehReader.close();
		} catch (FileNotFoundException e) {
			System.out.println("File not found!");
		} catch (IOException e) {
			System.out.println("IO Error!");
			e.printStackTrace();
		}
		
		
		
		
		File entfile = new File(mainDir.getPath() + "\\" + ENT);
		try {
			BufferedReader entReader = new BufferedReader(new FileReader(entfile));
			
			String line;
			ArrayList<String> lines = new ArrayList<String>();
			while ((line = entReader.readLine()) != null) {
				lines.add(line);
			}
			String start = lines.get(0);
			String end = lines.get(lines.size() - 1);
			if (!start.equals("Entities " + CodeGame.VERSION)) {
				System.out.println("Version may not match/Text file may be corrupted");
			}
			if (!end.equals("End")) {
				System.out.println("File missing End marker!");
			}
			int idNum;
			for (int row = 1; row < lines.size() - 1; row++) {
				idNum = Integer.parseInt(lines.get(row).substring(0, lines.get(row).indexOf(Entity.SPLITTER)));
				entMap.put(idNum, new Entity(lines.get(row), panel));
					//same warning as above : panel is null as of now. move the other block of code up
			}
			entReader.close();
		} catch (FileNotFoundException e) {
			System.out.println("File not found!");
		}
		catch (IOException e) {
			System.out.println("IO Error!");
			e.printStackTrace();
		}

		/*File gamefile = new File(mainDir.getPath() + "\\" + GAME);
		try {
			BufferedReader gameReader = new BufferedReader(new FileReader(gamefile));
			
			String line;
			ArrayList<String> lines = new ArrayList<String>();
			while ((line = gameReader.readLine()) != null) {
				lines.add(line);
			}
			String start = lines.get(0);
			String end = lines.get(lines.size() - 1);
			if (!start.equals("Game Info " + CodeGame.VERSION)) {
				System.out.println("Version may not match/Text file may be corrupted");
			}
			if (!end.equals("End")) {
				System.out.println("File missing End marker!");
			}
			panel = GridPanel.decode(lines.get(1), me, sq, name);
			g.setGrid(panel);
			
			gameReader.close();
		} catch (FileNotFoundException e) {
			System.out.println("File not found!");
		}
		catch (IOException e) {
			System.out.println("IO Error!");
			e.printStackTrace();
		}*/
		
		
		
		//problem - what about entity variables? maybe put into main hashmap, and then change val later? (so obj reference same but val changed)
		File connfile = new File(mainDir.getPath() + "\\" + CONN);
		try {
			BufferedReader connReader = new BufferedReader(new FileReader(connfile));
			
			String line;
			ArrayList<String> lines = new ArrayList<String>();
			while ((line = connReader.readLine()) != null) {
				lines.add(line);
			}
			String start = lines.get(0);
			String end = lines.get(lines.size() - 1);
			if (!start.equals("Conn Info " + CodeGame.VERSION)) {
				System.out.println("Version may not match/Text file may be corrupted");
			}
			if (!end.equals("End")) {
				System.out.println("File missing End marker!");
			}
			
			//Vehic - what has what under it (set owners as you do this also)
			//Game - what entities in main list of game, put entities into squares as you do this
			//Playr - what entities belong to player list
			int numPlayer = 0;
			int saveType = 0;
			int id;
			for (int index = 1; index < lines.size(); index++) {
				if (lines.get(index).equals("$Vehic")) {
					saveType = 0;
					//set owners as you do this too
				}
				else if (lines.get(index).equals("$Game")) {
					//put entities into squares as you do this NOT DONE WORK NEED
					//not needed...junk code below lols
					//Vehicle.decodeConn(lines, index + 1, ents);
					//panel.decodeConn();
					saveType = 1;
				}
				else if (lines.get(index).equals("$Playr")) {
					saveType = 2;
					me.get(numPlayer).decodeConn(lines, index + 1, vehMap);
					numPlayer++;
				}
				else if (saveType == 0) {
					id = Integer.parseInt(lines.get(index).substring(0, lines.get(index).indexOf(Vehicle.SPLITTER))); 
					vehMap.get(id).decodeConn(lines.get(index), entMap, vehMap);
				}
			}
			connReader.close();
		} catch (FileNotFoundException e) {
			System.out.println("File not found!");
		}
		catch (IOException e) {
			System.out.println("IO Error!");
			e.printStackTrace();
		}
		
		return panel;
	}
	public static String[] getListFiles() {
		return getListFiles(SAVE_EXT);
	}
	public static String[] getListFiles(String ext) {
		File dir = new File(System.getProperty("user.dir") + "\\" + ext);
		File[] subDirs = dir.listFiles(new FileFilter() {
			public boolean accept(File pathname) {	
				return pathname.isDirectory();
			}
		});
		if (subDirs == null) {
			return new String[0];
		}
		String [] names = new String[subDirs.length];
		for (int index = 0; index < names.length; index++) {
			names[index] = subDirs[index].getName();
		}
		return names;
	}
	public static String[] getCampaignFiles() {
		return getListFiles(BASE_CAMP_EXT);
	}
	public static String[] getCampaignSaveFiles() {
		return getListFiles(USER_CAMP_EXT);
	}
	public static boolean hasCampaignSaveFile(String name) {
		String[] list = getCampaignSaveFiles();
		for (String tempname : list) {
			if (tempname.equals(name)) {
				return true;
			}
		}
		return false;
	}
	public static void deleteWorld(String name) {
		try {
			File dir = new File(System.getProperty("user.dir") + "\\" + SAVE_EXT + name);
			delete(dir);
		} catch (IOException e) {
			System.out.println("Failed to delete world "+name+". World may be corrupted!");
		}
	}
	public static void delete(File file) throws IOException {
		if (file.isDirectory()) {
			if (file.list().length==0) {
				file.delete();
			}
			else {
				String[] files = file.list();
				for (String temp: files) {
					delete(new File(file, temp));
				}
				if (file.list().length==0) {
					file.delete();
				}
			}
		}
		else {
			file.delete();
		}
	}
	/*
	public static void saveObjects(String dir, ArrayList<Object> saves) {
		File file = new File(System.getProperty("user.dir") + "\\" + dir);
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(file, false));
			
			writer.write("Conn Info " + CodeGame.VERSION);
			writer.newLine();
			
			writer.close();
		} catch (IOException e) {
			System.out.println("IO Error!");
			e.printStackTrace();
		}
	}
	public static ArrayList<Object> loadObjects(String dir) {
		File file = new File(System.getProperty("user.dir") + "\\" + dir);
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line;
			ArrayList<String> lines = new ArrayList<String>();
			ArrayList<Object> saves = new ArrayList<Object>();
			while ((line = reader.readLine()) != null) {
				lines.add(line);
			}
			//do stuff
			reader.close();
		} catch (FileNotFoundException e) {
			System.out.println("File not found!");
		}
		catch (IOException e) {
			System.out.println("IO Error!");
			e.printStackTrace();
		}
	}*/
	
	public static void main(String[] args) {
		GameGrid myGrid = new GameGrid("world1", GameScreen.SAND);
		//myGrid.initialize();
		myGrid.saveFile();
	}
}
