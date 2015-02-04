package com.me.entities;


import java.util.*;


import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.me.fakeai.Action;
import com.me.fakeai.ListVariable;
import com.me.fakeai.NumVariable;
import com.me.fakeai.Program;
import com.me.fakeai.UserProgram;
import com.me.fakeai.Variable;
import com.me.panels.CustomButtonBuilder;
import com.me.panels.GameScreen;
import com.me.panels.IngamePanel;
import com.me.terrain.GameGrid;




//All entities belonging to player are also listed here in addition to the big list of entities in gridpanel
//	the ones in gridpanel may or may NOT belong to player!
//	player also has a custom window that they can open from bottom panel which allows them to run a "mainframe" program
//	or a program that, yeah, runs everything. weeeee!!!
//	this mainframe may send programs/commands to owned entities or even names of entities to entities! :D
//	also all programs ever made on any machine are accessible/saved here!
//	in future, if I add a hard mode or somehting I MAY MAY make this an entity so if it gets destroyed...ouch you lost yo programs

public class Player {
	
	public static final int SCROLL_SPEED = 1;
	public static final double SCALE_FACTOR = 1.1;
	
	protected int totalOutput;		//in loules
	protected int usedOutput;
	protected int money;
	private ArrayList<Vehicle> myVehicles;
	protected int maxSizeStorage;	//how many entities I can store in player's personal storage; this can be upgraded by upgrading
									//the player home depot entity!
	
	//protected HashMap<String, Variable> myVariables;
	
	private HashMap<String, UserProgram> myCommands;
	
	private GameScreen myGame;
	
	private GameGrid squares;
	
	private NullVehicle myNull;
	
	//protected ArrayList<Program> myQueue;
	
	protected ArrayList<Item> myItems;
	protected int itemCount;
	protected int maxItemCount;
	
	private String myName;
	
	//private double xpos, ypos, zpos;
	private Vector3 position;
	
	private double scaleSquare;
	
	private boolean hasPuppet;
	private ArrayList<Vehicle> puppet;
	
	public Player(GameGrid g, float x, float y, float z) {
		totalOutput = 50;
		usedOutput = 0;
		money = 0;
		myVehicles = new ArrayList<Vehicle>();
		maxSizeStorage = 10;
		//myVariables = new HashMap<String, Variable>();
		myCommands = new HashMap<String, UserProgram>();
		//myGame = g;
		squares = g;
		myNull = new NullVehicle(this);
		//myQueue = new ArrayList<Program>();
		myItems = new ArrayList<Item>();
		itemCount = 0;
		maxItemCount = 5000;
		/*String content = "def clickmove(destx,desty)\nprevX=xpos+1\nprevY=ypos+1\nprevD=direction+0.1\n" +
				"while(!(close(destx,desty))&(!(prevX?xpos)|!(prevY?ypos)|!(prevD?direction)))\n" +
				"prevX=xpos\nprevY=ypos\nprevD=direction\nmove(destx,desty)\nendwhile";*/
		String content = "def clickmove(destx,desty)\nmove(destx,desty)";
		
		addCommand("clickmove", content);
		addVar("syskey", new ListVariable());
		addVar("syslst", new ListVariable());
		
		myItems.add(Item.getItem("Log", 1000));
		myItems.add(Item.getItem("IronBar", 1000));
		itemCount += 2000;
		
		myName = "Player";
		
		/*xpos = x;
		ypos = y;
		zpos = z;*/
		position = new Vector3(x, z, y);
		scaleSquare = 1;
		
		hasPuppet = false;
		puppet = new ArrayList<Vehicle>();
	}
	public Player(float x, float y, float z, double s, int t, int u, int m, int ms, NullVehicle v, 
			HashMap<String, UserProgram> c, ArrayList<Item> i, int mIC, GameGrid g) {
		/*xpos = x;
		ypos = y;
		zpos = z;*/
		
		//just save the friggin' null vehicle!
		
		position = new Vector3(x, z, y);
		scaleSquare = s;
		totalOutput = t;
		usedOutput = u;
		money = m;
		maxSizeStorage = ms;
		myCommands = c;
		//myGame = g;
		squares = g;
		/*myNull = new NullVehicle(this);
		myNull.setVariables(v);
		//myNull.setQueue(q);
		myNull.setQueueStrings(qN);*/
		myNull = v;
		myNull.setPlayer(this);
		
		myVehicles = new ArrayList<Vehicle>();
		myItems = i;
		itemCount = 0;
		maxItemCount = mIC;
		for (Item item : myItems) {
			itemCount += item.getAmount();
		}
		
		myName = "Player";
		
		hasPuppet = false;
		puppet = new ArrayList<Vehicle>();
	}
	/*public void updateRender(GameRenderer r) {
		myGame.updateRender(r);
	}*/
	public float getTime() {
		return squares.getTime();
	}
	public void cycle() {
		myNull.cycle();
		/*if (myQueue.size() == 0)
			return;
		Program prgm = myQueue.get(0);
		if (prgm.isDone()) {
			myQueue.remove(0);
			cycle();
		}
		else {
			prgm.run(myNull, this);
			myQueue.set(0, prgm);
		}*/
	}
	public String getQueueName() {
		return myNull.getQueueName();
		/*
		String returnMe = "";
		for (Program q: myQueue) {
			returnMe = returnMe + q.toString()+"\n";
		}
		return returnMe;*/
	}
	public void addQueue(String text) {
		//myQueue.add(v);
		myNull.addQueue(text);
	}
	public void expandOutput(int x) {
		totalOutput += x;
	}
	public void shrinkOutput(int x) {
		totalOutput -= x;
		recalibrate();
	}
	public boolean useOutput(int x) {
		if (x < 0) {
			totalOutput -= x;
			return true;
		}
		if (x > totalOutput - usedOutput) {
			return false;
		}
		usedOutput += x;
		return true;
	}
	public void returnOutput(int x) {
		if (x < 0) {
			totalOutput += x;
			if (totalOutput < 0) {
				totalOutput = 0;
				addText("Output Error: Inconsistency in outputs");
			}
		}
		else {
			usedOutput -= x;
			if (usedOutput < 0) {
				usedOutput = 0;
				addText("Output Error: Inconsistency in outputs");
			}
		}
		recalibrate();
	}
	public void recalibrate() {
		if (usedOutput > totalOutput) {
			addText("System Failure! Shutting down entities due to power loss");
			for (Vehicle v: myVehicles) {
				if (v.getInput() > 0)
					v.turnOff();
				if (usedOutput <= totalOutput)
					break;
			}
		}
	}
	/*
	public void addOutput(int x) {
		totalOutput += x;
	}
	public void removeOutput(int x) {
		totalOutput -= x;
	}*/
	public void addVar(String n, Variable y) {
		//myVariables.put(n, y);
		myNull.addVar(n, y);
	}
	public boolean isVar(String n) {
		if (isSysVariable(n))
			return true;
		//return myVariables.containsKey(n);
		return myNull.isVar(n);
	}
	public Variable getVar(String n) {
		if (n.equals("Money"))
			return new NumVariable(money);
		//add list of current entities
		//return myVariables.get(n);
		return myNull.getVar(n);
	}
	public boolean isSysVariable(String n) {
		if (n.equals("Money"))
			return true;
		return false;
	}
	public boolean isCommand(String text) {
		return myCommands.containsKey(text);
	}
	/*public String getProgram(String n, Variable[] params, Vehicle y, Player z) {
		for (String x: myCommands.keySet()) {
			if (n.equals(x)) {
				UserProgram mine = myCommands.get(x);
				mine.setParams(params, y, z);
				return mine.getCode();
			}
		}
		return "";
	}*/
	public Program getCommand(String name, Variable[] paramValues) {
		if (isCommand(name))
			return new Program(myCommands.get(name), paramValues);
		return null;
	}
	public void addCommand(String name, String text) {
		myCommands.put(name, new UserProgram(name, text));
	}
	public void removeCommand(String name) {
		myCommands.remove(name);
	}
	public String getTextCommand(String name) {
		for (String x: myCommands.keySet()) {
			if (name.equals(x)) {
				return myCommands.get(x).getTextCode();
			}
		}
		return "";
	}
	public String[] getListCommand() {
		String[] listName = new String[myCommands.size()];
		int index = 0;
		for (String x: myCommands.keySet()) {
			listName[index] = x;
			index++;
		}
		return listName;
	}
	
	
	/*public void addKeySet(String key, String comm) {
		((ListVariable)myNull.getVar("syskey")).getVal().add(new StringVariable(key));
		((ListVariable)myNull.getVar("syslst")).getVal().add(new StringVariable(comm));
	}
	public void resetKeySet() {
		myVariables.remove("syskey");
		myVariables.remove("syslst");
		addVar("syskey", new ListVariable());
		addVar("syslst", new ListVariable());
	}
	public String[][] getKeyData() {
		ArrayList<Variable> keys = ((ListVariable)myVariables.get("syskey")).getVal();
		ArrayList<Variable> lsts = ((ListVariable)myVariables.get("syslst")).getVal();
		Variable k, m;
		String[][] data = new String[Math.min(keys.size(), lsts.size())][2];
		for (int index = 0; index < Math.min(keys.size(), lsts.size()); index++) {
			k = keys.get(index);
			m = lsts.get(index);
			data[index][0] = ((StringVariable)k).getVal();
			data[index][1] = ((StringVariable)m).getVal();
		}
		return data;
	}*/
	
	public void handle(char keyVal) {
		//WORK NEED: if busy doesnt take in key => player option
		if (hasPuppet) {
			Vehicle v;
			for (int i = 0; i < puppet.size(); i++) {
				v = puppet.get(i);
				v.handle(keyVal);
			}
		}
		else {
			myNull.handle(keyVal);
		}
		//String equiv = KeyEvent.getKeyText(keyVal);
		//myPlayer.addText("Key Pressed: |"+keyVal+"|");
		/*if (!myVariables.containsKey("syskey")) {
			addVar("syskey", new ListVariable());
			addVar("syslst", new ListVariable());
			return;
		}*/
		
		
		/*
		ArrayList<Variable> keys = ((ListVariable)myVariables.get("syskey")).getVal();
		ArrayList<Variable> lsts = ((ListVariable)myVariables.get("syslst")).getVal();
		Variable k, m;
		String focus;
		for (int index = 0; index < Math.min(keys.size(), lsts.size()); index++) {
			k = keys.get(index);
			if (k instanceof StringVariable) {
				m = lsts.get(index);
				focus = ((StringVariable)k).getVal();
				if (focus.equals("SPACE") && keyVal == ' ' && m instanceof StringVariable) {
					addQueue(new Program(((StringVariable)m).getVal()));
					return;
				}
				else if (!focus.equals("") && focus.charAt(0) == keyVal && m instanceof StringVariable) {
					addQueue(new Program(((StringVariable)m).getVal()));
					return;
				}
			}
		}*/
	}
	public void handle(boolean [] inputKeys, boolean shiftDown) {
		if (hasPuppet) {
			puppet.get(puppet.size() - 1).handle(inputKeys[8], inputKeys[9], inputKeys[10], inputKeys[11], shiftDown);
		}
	}
	public double getXPos() {
		return position.x;
	}
	public double getYPos() {
		return position.z;
	}
	public double getZPos() {
		return position.y;
	}
	public Vector3 getPos() {
		return position;
	}
	public void setPos(Vector3 pos) {
		position = pos;
	}
	
	public boolean isVehicle(String text) {
		for (Vehicle v: myVehicles) {
			if (v.toString().equals(text)) {
				return true;
			}
		}
		return false;
	}
	public Vehicle getVehicle(String text) {
		for (Vehicle v: myVehicles) {
			if (v.toString().equals(text)) {
				return v;
			}
		}
		return null;
	}
	public ArrayList<Vehicle> getAllVehicle(String text, boolean name) {
		ArrayList<Vehicle> candidates = new ArrayList<Vehicle>();
		if (name) {
			for (Vehicle v : myVehicles) {
				if (v.getName().equals(text))
					candidates.add(v);
			}
		}
		else {
			for (Vehicle v : myVehicles) {
				if (v.identifier().equals(text))
					candidates.add(v);
			}
		}
		return candidates;
	}
	public ArrayList<Vehicle> getAllVehicles() {
		return myVehicles;
	}
	public void setGameScreen(GameScreen p) {
		myGame = p;
	}
	public GameScreen getGameScreen() {
		return myGame;
	}
	public NullVehicle getNullVehicle() {
		return myNull;
	}
	public void addVehicle(Vehicle x) {
		//adds an entity
		//System.out.println("Adding " + x + " to player roster + map");
		x.turnOn();
		myVehicles.add(x);
		//add to hashmap also WORK NEED
		squares.addVehicle(x);
		x.notifyAdd();
	}
	//for adding bullets/other vehicles that should not show up in roster but show up in the map
	public void addExtraVehicle(Vehicle x) {
		//adds an entity
		//System.out.println("Adding " + x + " to player roster + map");
		x.turnOn();
		//myVehicles.add(x);
		//add to hashmap also WORK NEED
		squares.addVehicle(x);
		x.notifyAdd();
	}
	public void removeVehicle(Vehicle x) {
		x.notifyRemove();
		Vehicle v;
		for (int i = 0; i < puppet.size(); i++) {
			v = puppet.get(i);
			if (x.returnHighestOwner() == v.returnHighestOwner())
				resetPuppet();
		}
		x.turnOff();
		myVehicles.remove(x);
		if (myGame != null)
			myGame.informRemoveVehicle(x);
		squares.removeVehicle(x);
	}
	
	
	public void addItem(Item y) {
		if (itemCount + y.getAmount() > maxItemCount)
			return;
		itemCount += y.getAmount();
		for (Item x: myItems) {
			if (x.getName().equals(y.getName())) {
				x.addAmount(y.getAmount());
				return;
			}
		}
		myItems.add(y);
	}
	public boolean removeItem(Item y) {
		for (Item x: myItems) {
			if (x.getName().equals(y.getName())) {
				if (x.getAmount() >= y.getAmount()) {
					x.addAmount(-y.getAmount());
					itemCount -= y.getAmount();
					if (x.getAmount() == 0) {
						myItems.remove(x);
					}
					return true;
				}
			}
		}
		return false;
	}
	public boolean canRemoveItem(Item y) {
		if (y.getAmount() <= 0)
			return true;
		for (Item x: myItems) {
			if (x.getName().equals(y.getName())) {
				if (x.getAmount() >= y.getAmount()) {
					return true;
				}
			}
		}
		return false;
	}
	public void changeMaxItemCount(int x) {
		maxItemCount += x;
	}
	public void refineOres(int tier, int level) {
		int refineAmount;
		Item x;
		for (int i = 0; i < myItems.size(); i++) {
			x = myItems.get(i); 
			if (level == 0)
				return;
			if (x.getTier() == tier) {
				refineAmount = Math.min(level, x.getAmount());
				level -= refineAmount;
				x.addAmount(-refineAmount);
				if (x.getAmount() == 0)
					myItems.remove(x);
				myItems.add(Item.getRefinedItem(x.getName(), refineAmount));
			}
		}
	}
	public boolean hasPuppet() {
		return hasPuppet && puppet.size() != 0;
	}
	public void setPuppet(Vehicle x) {
		if (myGame == null)
			return;
		//if (puppet != null)
		//	puppet.select(false);
		if (puppet.contains(x))
			return;
		puppet.add(x);
		x.select(true);
		hasPuppet = true;
	}
	public void resetPuppet() {
		if (myGame == null)
			return;
		for (Vehicle v : puppet) {
			v.select(false);
		}
		puppet.clear();
		hasPuppet = false;
	}
	public void clickMove(String coord) {
		for (Vehicle v : puppet) {
			v.addQueue("move("+coord+")");
		}
	}
	public String[] getListVehicle() {
		String[] listName = new String[myVehicles.size()];
		int index = 0;
		for (Vehicle v: myVehicles) {
			listName[index] = v.toString();
			index++;
		}
		return listName;
	}
	public String[] getListItem() {
		String[] listName = new String[myItems.size()];
		int index = 0;
		for (Item x: myItems) {
			listName[index] = x.toString();
			index++;
		}
		return listName;
	}
	public void addText(String text) {
		if (myGame == null)
			return;
		myGame.addText(text);
	}
	
	public void addQueueText(String text) {
		if (text.length() == 0)
			return;
		if (hasPuppet) {
			for (Vehicle v : puppet) {
				v.addQueue(text);
			}
		}
		else {
			addQueue(text);
		}
	}
	public void addWindow(IngamePanel v) {
		if (myGame == null)
			return;
		myGame.addPanel(v);
	}
	public Actor controller(CustomButtonBuilder b) {
		//returns a JPanel that is shown when this unit is selected!
		//return new DescriptionPanel();
		
		String info = "Output: " + usedOutput + "/" + totalOutput + "\nMoney: " + money + "\nMaximum Capacity: "+maxSizeStorage;
		Label label = b.getLabel(info);
		label.setWrap(true);
		
		return label;
	}
	public String getName() {
		return myName;
	}
	public String encode() {
		String returnMe = "$Playr\n";
		returnMe = returnMe+position.x + "$" + position.z + "$" + position.y + "$" + scaleSquare + "$" 
				+ totalOutput + "$" + usedOutput+"$"+money+"$"+maxSizeStorage+"$"+maxItemCount+"$"+"\n";
		returnMe = returnMe + myNull.encode();
		//for (String x: myNull.getVariables().keySet()) {
			//returnMe = returnMe + x + "$" + myNull.getVariables().get(x).toString() + "\n";
		//}
		//returnMe = returnMe + "$Progs\n";
		
		//for (int i = 0; i < myNull.getQueue().size(); i++) {
			//returnMe = returnMe + "$Liter\n" + myNull.getQueueStrings().get(i) + "\n";
			//returnMe = returnMe + "$Compi\n" + myNull.getQueue().get(i).encode() + "\n";
		//}
		returnMe = returnMe + "$UProg\n";
		for (String x: myCommands.keySet()) {
			returnMe = returnMe + "$Use\n" + x + "\n" + myCommands.get(x).encode();
		}
		returnMe = returnMe + "$PItem\n";
		for (Item x: myItems) {
			returnMe = returnMe + x.getName() + "$" + x.getAmount() + "\n";
		}
		returnMe = returnMe + "$PEnd";
		return returnMe;
	}
	public String encodeConn() {
		String returnMe = "";
		for (Vehicle v: myVehicles) {
			returnMe = returnMe + v.getSerial() + "\n";
		}
		return returnMe + "$EnEnd\n";
	}
	public void decodeConn(ArrayList<String>lines, int index, HashMap<Integer, Vehicle> ents) {
		Vehicle veh;
		for (int i = index; i < lines.size(); i++) {
			if (lines.get(i).equals("$EnEnd")) {
				break;
			}
			veh = ((Vehicle)ents.get(Integer.parseInt(lines.get(i))));
			myVehicles.add(veh);
			squares.addVehicle(veh);
		}
 	}
	public static Player decode(ArrayList<String> lines, int index, GameGrid g) {
		if (!lines.get(index++).equals("$Playr")) {
			System.out.println("Corrupted file!");
		}
		String[] secondLine = lines.get(index++).split("\\$");
		float xpos = Float.parseFloat(secondLine[0]);
		float ypos = Float.parseFloat(secondLine[1]);
		float zpos = Float.parseFloat(secondLine[2]);
		double scaleSquare = Double.parseDouble(secondLine[3]);
		int totalOutput = Integer.parseInt(secondLine[4]);
		int usedOutput = Integer.parseInt(secondLine[5]);
		int money = Integer.parseInt(secondLine[6]);
		int maxSizeStorage = Integer.parseInt(secondLine[7]);
		int maxItemCount = Integer.parseInt(secondLine[8]);
		NullVehicle x = new NullVehicle(null);
		x.decode(lines.get(index++), null, g);
		
		String line;
		/*HashMap<String, Variable> vars = new HashMap<String, Variable>();
		ArrayList<Action> queue = new ArrayList<Action>();
		ArrayList<String> qNames = new ArrayList<String>();*/
		HashMap<String, UserProgram> coms = new HashMap<String, UserProgram>();
		ArrayList<Item> items = new ArrayList<Item>();
		int i;
		/*
		for (i = index; i < lines.size(); i++) {
			line = lines.get(i);
			if (line.equals("$Progs")) {
				break;
			}
			locDiv = line.indexOf('$');
			vars.put(line.substring(0, locDiv), Action.getVar(line.substring(locDiv + 1)).getVar());
		}
		for (i = i + 1; i < lines.size(); i++) {
			line = lines.get(i);
			if (line.equals("$UProg")) {
				break;
			}
			if (line.equals("$Liter")) {
				queue.add(Action.getAction(lines.get(index+1)));
				qNames.add(lines.get(index+1));
			}
			if (line.equals("$Compi")) {
				queue.get(queue.size() - 1).decode(lines.get(index+1));
			}
		}*/
		for (i = index; i < lines.size(); i++) {
			line = lines.get(i);
			if (line.equals("$PItem")) {
				break;
			}
			if (line.equals("$Use")) {
				coms.put(lines.get(i + 1), UserProgram.decode(lines, i + 2));
			}
		}
		for (i = i + 1; i < lines.size(); i++) {
			line = lines.get(i);
			if (line.equals("$PEnd")) {
				break;
			}
			items.add(Item.decode(lines, i));
			
		}
		
		//initialize Entities in 2nd wave
		Player p = new Player(xpos, ypos, zpos, scaleSquare, totalOutput, usedOutput, money, maxSizeStorage, x, coms, items, maxItemCount, g);
		
		x.setPlayer(p);
		
		return p;
	}
}

/*
 * 
 * Storage will be implemented as a separate entity!!! Not as a whole (like energy) 
 * 
 * 
protected int totalStorage;		//in wrams
protected int usedStorage;

public boolean useStorage(int x) {
	if (x > totalStorage - usedStorage) {
		return false;
	}
	usedStorage += x;
	return true;
}
public void returnStorage(int x) {
	usedStorage -= x;
	if (usedStorage < 0) {
		usedStorage = 0;
		System.out.println("Storage Error: Inconsistency in storage capacity");
	}
}
public void addStorage(int x) {
	totalStorage += x;
}
public void removeStorage(int x) {
	totalStorage -= x;
}*/
