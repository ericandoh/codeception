package com.me.fakeai;

import java.util.ArrayList;

import com.me.entities.EngineVehicle;
import com.me.entities.Entity;
import com.me.entities.Item;
import com.me.entities.Player;
import com.me.entities.TurretVehicle;
import com.me.entities.Vehicle;
import com.me.entities.SmartVehicle;
import com.me.entities.VehicleHelper;
import com.me.terrain.GameGrid;
import com.me.terrain.Square;



public class VehicleCommand {
	
	protected static final String PAUSE = Action.PAUSE;
	protected static final String CONT = Action.CONT;
	protected static final String STOP = Action.STOP;
	
	public static final String[] VEHICLE = VehicleHelper.VEHICLE;
	public static final String[][] ENTITY = VehicleHelper.ENTITY;
	public static final String[] BULLET_TYPES = VehicleHelper.BULLET_TYPES;
	
	public static final String SPLITTER = Vehicle.SPLITTER;
	//private int numParams;
	//private int maxDelay;
	protected Variable[] params;
	protected Vehicle parent;
	protected Variable[] retVal;
	public VehicleCommand(Vehicle parent) {
		this.parent = parent;
	}
	public void setParams(Variable[] params, Variable[] retVal) {
		this.params = params;
		this.retVal = retVal;
	}
	public int beginAction() {
		//do nothing, called when method is called on vehicle
		return 0;
	}
	public int run(int busy) {
		return busy - 1;
	}
	public void finish() {
		//finishes
	}
	public String getName() {
		return "null";
	}
	public void setRet(Variable[] retVal) {
		this.retVal = retVal;
	}
	public String encode() {
		//save the params
		String ret = params.length + SPLITTER;
		for (Variable v: params) {
			ret = ret + v.toString() + SPLITTER;
		}
		return ret;
	}
	public int decode(String[] info, int index) {
		//decode the params
		int length = Integer.parseInt(info[index++]);
		params = new Variable[length];
		for (int i = 0; i < length; i++) {
			params[i] = Action.getVar(info[index++]).getVar();
		}
		return index;
	}
	public static void initializeVehicle(Vehicle veh, boolean isNew) {
		//if new, add key commands as well. else, dont. 
		ArrayList<VehicleCommand> list = veh.getCommands();
		String id = veh.identifier();
		
		int sight = 1;
		if (id.equals(VEHICLE[0])) {
			//null vehicle
			list.add(new ResetCommand(veh));
			list.add(new SendCommand(veh, 100));
			list.add(new WaitCommand(veh));
			if (isNew) {
				veh.addKeySet("r", "reset()");
			}
			return;
		}
		else if (id.equals(VEHICLE[1])) {
			//playerunit
			list.add(new LogCommand(veh, 1));
			String[] buildList = {VEHICLE[2], VEHICLE[3]};
			list.add(new BuildCommand(veh, buildList));
			list.add(new RepairCommand(veh, 5));
			sight = 2;
			if (isNew) {
				veh.addKeySet("z", "log()");
				veh.addKeySet("r", "repair()");
				veh.addKeySet("1", "build('"+VEHICLE[2]+"')");
				veh.addKeySet("2", "build('"+VEHICLE[3]+"')");
			}
		}
		else if (id.equals(VEHICLE[2])) {
			//workshop
			String[] buildList = {VEHICLE[2], VEHICLE[3], VEHICLE[4], VEHICLE[5], VEHICLE[6], VEHICLE[7], VEHICLE[8], VEHICLE[14]};
			list.add(new BuildCommand(veh, buildList));
			if (isNew) {
				for (int i = 0; i < buildList.length; i++) {
					veh.addKeySet((i+1) + "", "build('"+buildList[i] + "')");
				}
			}
		}
		else if (id.equals(VEHICLE[3])) {
			//furnace
			list.add(new RefineCommand(veh, 10, 1));
			if (isNew) {
				veh.addQueue("refine()");
				veh.addKeySet("r","refine()");
			}
		}
		/*else if (id.equals(VEHICLE[4])) {
			//storage
		}*/
		else if (id.equals(VEHICLE[5])) {
			//harvester
			list.add(new LogCommand(veh, 1));
			list.add(new MineCommand(veh, 1));
			if (isNew) {
				veh.addKeySet("x","log()");
				veh.addKeySet("z","mine()");
			}
		}
		else if (id.equals(VEHICLE[6])) {
			//forge (tier 2)
			list.add(new RefineCommand(veh, 10, 2));
			if (isNew) {
				veh.addQueue("refine2()");
				veh.addKeySet("r","refine2()");
			}
		}
		/*else if (id.equals(VEHICLE[7])) {
			//assembler
		}*/
		else if (id.equals(VEHICLE[8])) {
			//weaponshop
			String[] buildList = {VEHICLE[9], VEHICLE[10], VEHICLE[11], VEHICLE[12], VEHICLE[13]};
			list.add(new BuildCommand(veh, buildList));
			if (isNew) {
				for (int i = 0; i < buildList.length; i++) {
					veh.addKeySet((i+1) + "", "build('"+buildList[i] + "')");
				}
			}
		}
		/*else if (id.equals(VEHICLE[9])) {
			//small chip
		}*/
		else if (id.equals(VEHICLE[10])) {
			//pistol
			list.add(new ShootCommand(veh, 25, BULLET_TYPES[1]));
			if(isNew) {
				veh.addKeySet("z","shoot()");
			}
		}
		else if (id.equals(VEHICLE[11])) {
			//turret
		}
		else if (id.equals(VEHICLE[12])) {
			//gatling
			list.add(new ShootCommand(veh, 10, BULLET_TYPES[1]));
			if(isNew) {
				veh.addKeySet("z","shoot()");
			}
		}
		else if (id.equals(VEHICLE[13])) {
			//turret
			list.add(new ShootCommand(veh, 50, BULLET_TYPES[2]));
			if(isNew) {
				veh.addKeySet("z","shoot()");
			}
		}
		else if (id.equals(VEHICLE[14])) {
			//weaponshop
			String[] buildList = {VEHICLE[15], VEHICLE[16], VEHICLE[17], VEHICLE[18]};
			list.add(new BuildCommand(veh, buildList));
			if (isNew) {
				for (int i = 0; i < buildList.length; i++) {
					veh.addKeySet((i+1) + "", "build('"+buildList[i] + "')");
				}
			}
		}
		//else if (id.equals(VEHICLE[15])) {
			//armor
		//}
		//else if (id.equals(VEHICLE[16])) {
			//turbine
		//}
		//else if (id.equals(VEHICLE[17])) {
			//engine
		//}
		//else if (id.equals(VEHICLE[18])) {
			//propeller
		//}
		if (veh instanceof SmartVehicle) {
			list.add(new CombineCommand(veh));
			list.add(new DisbandCommand(veh));
			list.add(new VehicleCommand(veh));
			list.add(new CloseCommand(veh));
			//list.add(new SendCommand(veh, 300));
			list.add(new WaitCommand(veh));
			list.add(new SquareCommand(veh, sight));
			list.add(new GetVehCommand(veh, sight+1));
			list.add(new GetEnemyVehCommand(veh, sight+1));
			if (isNew) {
				veh.addKeySet("z", "combine()");
				veh.addKeySet("x", "disband()");
				//veh.addKeySet("p", "square(xpos,ypos)");
			}
		}
		if (veh instanceof EngineVehicle) {
			list.add(new MoveCommand(veh));
			list.add(new TurnCommand(veh));
			list.add(new UpCommand(veh));
			list.add(new DownCommand(veh));
		}
		//commands all vehicles have!
		list.add(new ResetCommand(veh));
		list.add(new PlayerCommand(veh));
		list.add(new ScrapCommand(veh));
		list.add(new NameCommand(veh));
		if (isNew) {
			veh.addKeySet("r", "reset()");
			//veh.addKeySet("p", "player()");
			veh.addKeySet("0", "scrap()");
		}
	}
}
class WaitCommand extends VehicleCommand {
	public WaitCommand(Vehicle y) {
		super(y);
	}
	public int beginAction() {
		if (params[0] instanceof NumVariable) {
			return (int)(((NumVariable)params[0]).getVal());
		}
		return 20;
	}
	public String getName() {
		return "wait";
	}
}
class ResetCommand extends VehicleCommand {
	public ResetCommand(Vehicle y) {
		super(y);
	}
	public int beginAction() {
		parent.getPlayer().resetPuppet();
		return 0;
	}
	public String getName() {
		return "reset";
	}
}
class PlayerCommand extends VehicleCommand {
	public PlayerCommand(Vehicle y) {
		super(y);
	}
	public int beginAction() {
		parent.getPlayer().setPuppet(parent);
		return 0;
	}
	public String getName() {
		return "player";
	}
}
class MoveCommand extends VehicleCommand {
	private double goalX, goalY;
	public MoveCommand(Vehicle y) {
		super(y);
	}
	@Override
	public int beginAction() {
		if (params.length == 0) {
			Vehicle y = parent.returnHighestOwner();
			goalX = y.getXPos() + Math.cos(y.getDirection());
			goalY = y.getYPos() + Math.sin(y.getDirection());
		}
		else if (params.length == 2 && params[0] instanceof NumVariable && params[1] instanceof NumVariable) {
			goalX = ((NumVariable)(params[0])).getVal();
			goalY = ((NumVariable)(params[1])).getVal();
		}
		return 1;
	}
	@Override
	public int run(int busy) {
		//System.out.println("moving...");
		boolean result = parent.returnHighestOwner().move(goalX, goalY);
		//System.out.println(result);
		if (!result)
			return 0;
		return 1;
	}
	public String getName() {
		return "move";
	}
	public int decode(String[] info, int index) {
		index = super.decode(info, index);
		if (params.length == 0) {
			Vehicle y = parent.returnHighestOwner();
			goalX = y.getXPos() + Math.cos(y.getDirection());
			goalY = y.getYPos() + Math.sin(y.getDirection());
		}
		else if (params.length == 2 && params[0] instanceof NumVariable && params[1] instanceof NumVariable) {
			goalX = ((NumVariable)(params[0])).getVal();
			goalY = ((NumVariable)(params[1])).getVal();
		}
		return index;
	}
}
class UpCommand extends VehicleCommand {
	private double goalZ;
	public UpCommand(Vehicle y) {
		super(y);
	}
	public int beginAction() {
		if (params.length == 0) {
			goalZ = parent.returnHighestOwner().getZPos() + 1f;
		}
		else if (params.length == 1 && params[0] instanceof NumVariable) {
			goalZ = ((NumVariable)params[0]).getVal();
		}
		return 1;
	}
	public int run(int busy) {
		boolean result = parent.returnHighestOwner().moveVertGoal(goalZ);
		if (!result)
			return 0;
		return 1;
	}
	public String getName() {
		return "up";
	}
	public int decode(String[] info, int index) {
		index = super.decode(info, index);
		if (params.length == 0) {
			goalZ = parent.returnHighestOwner().getZPos() + 1f;
		}
		else if (params.length == 1 && params[0] instanceof NumVariable) {
			goalZ = ((NumVariable)params[0]).getVal();
		}
		return index;
	}
}
class DownCommand extends VehicleCommand {
	private double goalZ;
	public DownCommand(Vehicle y) {
		super(y);
	}
	public int beginAction() {
		if (params.length == 0) {
			goalZ = parent.returnHighestOwner().getZPos() - 1f;
		}
		else if (params.length == 1 && params[0] instanceof NumVariable) {
			goalZ = ((NumVariable)params[0]).getVal();
		}
		return 1;
	}
	public int run(int busy) {
		boolean result = parent.returnHighestOwner().moveVertGoal(goalZ);
		if (!result)
			return 0;
		return 1;
	}
	public String getName() {
		return "down";
	}
	public int decode(String[] info, int index) {
		index = super.decode(info, index);
		if (params.length == 0) {
			goalZ = parent.returnHighestOwner().getZPos() - 1f;
		}
		else if (params.length == 1 && params[0] instanceof NumVariable) {
			goalZ = ((NumVariable)params[0]).getVal();
		}
		return index;
	}
}
class TurnCommand extends VehicleCommand {
	private static final double TURN_GOAL = Math.PI / 8;
	private double goalD;
	public TurnCommand(Vehicle y) {
		super(y);
	}
	public int beginAction() {
		if (params.length == 0) {
			goalD = parent.returnHighestOwner().getDirection() + TURN_GOAL;
		}
		else if (params.length == 1) {
			if (params[0] instanceof NumVariable) {
				goalD = ((NumVariable)params[0]).getVal();
			}
			else if (params[0] instanceof BoolVariable) {
				if (((BoolVariable)params[0]).getVal()) {
					goalD = parent.returnHighestOwner().getDirection() + TURN_GOAL;
				}
				else {
					goalD = parent.returnHighestOwner().getDirection() - TURN_GOAL;
				}
			}
			else if (params[0] instanceof StringVariable) {
				if (((StringVariable)params[0]).getVal().equals("left")) {
					goalD = parent.returnHighestOwner().getDirection() + TURN_GOAL;
				}
				else {
					goalD = parent.returnHighestOwner().getDirection() - TURN_GOAL;
				}
			}
		}
		return 1;
	}
	public int run(int busy) {
		boolean result = parent.returnHighestOwner().turn(goalD);
		if (!result)
			return 0;
		return 1;
	}
	public String getName() {
		return "turn";
	}
	public int decode(String[] info, int index) {
		index = super.decode(info, index);
		if (params.length == 0) {
			goalD = parent.returnHighestOwner().getDirection() + TURN_GOAL;
		}
		else if (params.length == 1) {
			if (params[0] instanceof NumVariable) {
				goalD = ((NumVariable)params[0]).getVal();
			}
			else if (params[0] instanceof BoolVariable) {
				if (((BoolVariable)params[0]).getVal()) {
					goalD = parent.returnHighestOwner().getDirection() + TURN_GOAL;
				}
				else {
					goalD = parent.returnHighestOwner().getDirection() - TURN_GOAL;
				}
			}
			else if (params[0] instanceof StringVariable) {
				if (((StringVariable)params[0]).getVal().equals("left")) {
					goalD = parent.returnHighestOwner().getDirection() + TURN_GOAL;
				}
				else {
					goalD = parent.returnHighestOwner().getDirection() - TURN_GOAL;
				}
			}
		}
		return index;
	}
}
class CloseCommand extends VehicleCommand {
	public CloseCommand(Vehicle y) {
		super(y);
	}
	public int beginAction() {
		if (params.length == 2 && params[0] instanceof NumVariable && params[1] instanceof NumVariable) {
			retVal[0] = new BoolVariable(parent.returnHighestOwner().isClose(((NumVariable)(params[0])).getVal(), ((NumVariable)(params[1])).getVal()));
		}
		return 0;
	}
	public String getName() {
		return "close";
	}
}
class CombineCommand extends VehicleCommand {
	public CombineCommand(Vehicle y) {
		super(y);
	}
	public int beginAction() {
		return 20;
	}
	public void finish() {
		if (parent.returnHighestOwner().isNull()) {
		}
		else if (params.length == 0 && parent.returnHighestOwner() instanceof SmartVehicle) {
			Vehicle p = parent.returnHighestOwner();
			ArrayList<Vehicle> veh = p.getNearbyVehicles();
			if (veh.size() != 0) {
				Vehicle veh1 = veh.get(0);
				((SmartVehicle)p).addVehicle(veh1);
			}
		}
	}
	public String getName() {
		return "combine";
	}
}
class DisbandCommand extends VehicleCommand {
	public DisbandCommand(Vehicle y) {
		super(y);
	}
	public int beginAction() {
		return 20;
	}
	public void finish() {
		if (parent.returnHighestOwner().isNull()) {
			return;
		}
		if (params.length == 0) {
			if (parent.returnHighestOwner() instanceof SmartVehicle) {
				((SmartVehicle)(parent.returnHighestOwner())).disband();
			}
		}
	}
	public String getName() {
		return "disband";
	}
}
class ScrapCommand extends VehicleCommand {
	public ScrapCommand(Vehicle y) {
		super(y);
	}
	public int beginAction() {
		return 20;
	}
	public void finish() {
		ArrayList<Item> cost = VehicleHelper.getVehicleScrapCost(parent.returnHighestOwner());
		Player p = parent.getPlayer();
		p.removeVehicle(parent.returnHighestOwner());
		for(Item i : cost) {
			p.addItem(i);
		}
	}
	public String getName() {
		return "scrap";
	}
}
class LogCommand extends VehicleCommand {
	private int level;
	public LogCommand(Vehicle y, int l) {
		super(y);
		level = l;
	}
	public int beginAction() {
		return 20;
	}
	public void finish() {
		int xpos = (int)(Math.round(parent.getXPos()));
		int ypos = (int)(Math.round(parent.getYPos()));
		Square.log(level, parent, xpos, ypos);
	}
	public String getName() {
		return "log";
	}
}
class BuildCommand extends VehicleCommand {
	private String vehName;
	private String[] possibles;
	public BuildCommand(Vehicle y, String[] p) {
		super(y);
		possibles = p;
	}
	public int beginAction() {
		if (params.length == 1 && params[0] instanceof StringVariable) {
			String param = ((StringVariable)(params[0])).getVal();
			if (!hasName(param)) {
				parent.getPlayer().addText("This vehicle cannot make "+param);
				vehName = null;
				retVal[0] = new StringVariable("FAIL");
				return 0;
			}
			ArrayList<Item> cost = VehicleHelper.getVehicleCost(param);
			for (Item i : cost) {
				if (!parent.getPlayer().canRemoveItem(i)) {
					parent.getPlayer().addText("Not enough. Needed: "+i.toString());
					vehName = null;
					retVal[0] = new StringVariable("LACK");
					return 0;
				}
			}
			vehName = param;
		}
		return 50;
	}
	public boolean hasName(String n) {
		for (String p: possibles) {
			if (p.equals(n))
				return true;
		}
		return false;
	}
	public void finish() {
		//finishes
		if (vehName == null)
			return;
		
		Vehicle makeNew = null;
		boolean success = false;
		for (Entity ent: parent.getEntities()) {
			for (int p = -1; p <= 1; p++) {
				for (int q = -1; q <= 1; q++) {
					makeNew = VehicleHelper.makeVehicle(parent.getSquares(), parent.getPlayer(), 
							ent.getXPos()+p, ent.getYPos()+q, ent.getZPos(), vehName, parent.identifier());
					if (makeNew == null) {
						parent.getPlayer().addText("This unit cannot make "+vehName+".");
						retVal[0] = new StringVariable("NAME");
						return;
					}
					else if (!makeNew.canAdd(0, 0, 0, makeNew.getType())) {
						continue;
					}
					else if (!makeNew.canAddType(0, 0, 0, makeNew.getType())) {
						continue;
					}
					success = true;
					break;
				}
				if (success)
					break;
			}
			if (success)
				break;
		}
		if (!success) {
			parent.getPlayer().addText("Another vehicle is in the way/Terrain is insuitable.");
			retVal[0] = new StringVariable("BLOCKED");
			return;
		}
		ArrayList<Item> cost = VehicleHelper.getVehicleCost(vehName);
		for (Item i : cost) {
			parent.getPlayer().removeItem(i);
		}
		parent.getPlayer().addVehicle(makeNew);
		parent.getPlayer().addText("Added new Vehicle: "+vehName);
	}
	public String getName() {
		return "build";
	}
	public int decode(String[] info, int index) {
		index = super.decode(info, index);
		if (params.length == 1 && params[0] instanceof StringVariable) {
			String param = ((StringVariable)(params[0])).getVal();
			vehName = param;
		}
		return index;
	}
}
class MineCommand extends VehicleCommand {
	private int level;
	public MineCommand(Vehicle y, int l) {
		super(y);
		level = l;
	}
	public int beginAction() {
		return 30;
	}
	public void finish() {
		//finishes
		int xpos = (int)(Math.round(parent.getXPos()));
		int ypos = (int)(Math.round(parent.getYPos()));
		Square.mine(level, parent, xpos, ypos);
	}
	public String getName() {
		return "mine";
	}
}
class ShootCommand extends VehicleCommand {
	private String bulletName;
	private int level;
	public ShootCommand(Vehicle y, int l, String bName) {
		super(y);
		level = l;
		bulletName = bName;
	}
	public int beginAction() {
		return level;
	}
	public void finish() {
		//y.addWait(level);
		double xpos = Math.round(parent.getXPos() + 1.3*Math.cos(parent.getDirection()));
		double ypos = Math.round(parent.getYPos() + 1.3*Math.sin(parent.getDirection()));
		Vehicle makeNew;
		if (parent.returnHighestOwner() instanceof TurretVehicle) {
			float mult = ((TurretVehicle)parent.returnHighestOwner()).getMult();
			makeNew = VehicleHelper.makeBullet(parent.getSquares(), parent.getPlayer(), xpos, ypos, parent.getZPos(), bulletName, mult);
		}
		else {
			makeNew = VehicleHelper.makeBullet(parent.getSquares(), parent.getPlayer(), xpos, ypos, parent.getZPos(), bulletName, 1f);
		}
		if (makeNew == null) {
			parent.getPlayer().addText("Cannot shoot!");
			return;
		}
		makeNew.setDirection(parent.getDirection());
		parent.getPlayer().addExtraVehicle(makeNew);
		//System.out.println("Add to player w/o adding it to roster?");
	}
	public String getName() {
		return "shoot";
	}
}
/*
class ShotCommand extends VehicleCommand {
	private int level;
	private int life;
	public ShotCommand(int l, int li) {
		super(0, -1);
		level = l;
		//change so life changes with level
		life = li;
	}
	public String runCommand(Vehicle y, Player z, Variable[] retVal, Variable[] params, VehicleAction act) {
		if (!y.move()) {
			//ArrayList<Vehicle> near = y.getNearbyVehicles();
			ArrayList<Entity> near = y.getNearbyEntities();
			for (Entity x: near) {
				//System.out.println("Boom on "+x);
				x.takeDamage(level);
				addExplosions(x.getXPos(), x.getYPos(), y.getSquares());
			}
			//make spectacular fireworks!
			//y.removeMyself();
			addExplosions(y.getXPos(), y.getYPos(), y.getSquares());
			z.removeVehicle(y);
			return PAUSE;
		}
		else if (life < 0) {
			//make spectacular fireworks!
			//y.removeMyself();
			addExplosions(y.getXPos(), y.getYPos(), y.getSquares());
			z.removeVehicle(y);
			return PAUSE;
		}
		life--;
		return STOP;
	}
	private void addExplosions(double x, double y, GameGrid sq) {
		double x1 = Math.random() - 0.5 + x;
		double x2 = Math.random() - 0.5 + x;
		double y1 = Math.random() - 0.5 + y;
		double y2 = Math.random() - 0.5 + y;
		int[] seq1 = {0, 1, 2, 3, 2, 1, 0};
		int[] seq2 = {4, 4, 0, 1, 2, 1, 0};
		sq.addSprite(new GameSprite(x1, y1, 0, 7, seq1));
		sq.addSprite(new GameSprite(x2, y2, 0, 7, seq2));
	}
	public String getName() {
		return "shot";
	}
}*/
class RepairCommand extends VehicleCommand {
	private int level;
	public RepairCommand(Vehicle y, int l) {
		super(y);
		level = l;		//how much hp is repaired
	}
	public int beginAction() {
		return 20;
	}
	public void finish() {
		System.out.println("Attempting repairs...");
		ArrayList<Entity> near = parent.getNearbyEntities();
		int prev = level;
		int next = 0;
		if (near.size() == 0)
			parent.getPlayer().addText("No vehicles in vicinity found");
		for (Entity ent : near) {
			if (ent.getOwner().getPlayer() != parent.getPlayer())
				continue;
			System.out.println("Repairing " + ent);
			ArrayList<Item> cost = VehicleHelper.getVehicleRepairCost(ent.getOwner());
			for (Item i : cost) {
				if (!parent.getPlayer().canRemoveItem(i)) {
					parent.getPlayer().addText("Not enough. Needed: "+i.toString());
					//return PAUSE;
					continue;
				}
			}
			next = ent.getRepairs(prev);
			if (prev == next) {
				parent.getPlayer().addText("No repairs needed on " + ent.getOwner());
			}
			else {
				for (Item i : cost) {
					parent.getPlayer().removeItem(i);
				}
				prev = next;
			}
		}	
	}
	public String getName() {
		return "repair";
	}
}
class NameCommand extends VehicleCommand {
	public NameCommand(Vehicle y) {
		super(y);
	}
	public int beginAction() {
		return 0;
	}
	public void finish() {
		if (params.length == 1 && params[0].getType().equals(Variable.BASIC_STRING)) {
			parent.setName(((StringVariable)params[0]).getVal());
		}
	}
	public String getName() {
		return "name";
	}
}
class SendCommand extends VehicleCommand {
	private int delay;
	public SendCommand(Vehicle y, int delay) {
		super(y);
		this.delay = delay;
	}
	public int beginAction() {
		return delay;
	}
	public void finish() {
		if (params.length >= 2 && params[0].getType().equals(Variable.BASIC_STRING) && params[1].getType().equals(Variable.BASIC_STRING)) {
			String name = ((StringVariable)params[0]).getVal();
			String comm = ((StringVariable)params[1]).getVal();
			ArrayList<Vehicle> vehs;
			if (VehicleHelper.getVehicleID(name) == 0) {
				//check by name
				vehs = parent.getPlayer().getAllVehicle(name, true);
			}
			else {
				//check by type
				vehs = parent.getPlayer().getAllVehicle(name, false);
			}
			if (params.length == 2) {
				for (Vehicle v : vehs) {
					v.addQueue(comm);
				}
			}
			else if (params.length == 3 && params[2].getType().equals(Variable.BASIC_NUM)) {
				double chance = ((NumVariable)params[2]).getVal();
				for (Vehicle v : vehs) {
					if (Math.random() < chance) {
						v.addQueue(comm);
					}
				}
			}
		}
	}
	public String getName() {
		return "send";
	}
}
class SquareCommand extends VehicleCommand {
	private int level;
	public SquareCommand(Vehicle y, int l) {
		super(y);
		level = l;
	}
	public int beginAction() {
		return 0;
	}
	public void finish() {
		double xp, yp;
		if (params.length == 0) {
			xp = parent.getXPos();
			yp = parent.getYPos();
			retVal[0] = new StringVariable(parent.getSquares().getSquare((int)xp, (int)yp).toString());
			return;
		}
		else if (params.length == 2 && params[0].getType().equals(Variable.BASIC_NUM) && params[1].getType().equals(Variable.BASIC_NUM)) {
			xp = ((NumVariable)params[0]).getVal();
			yp = ((NumVariable)params[1]).getVal();
			if (xp <= level && yp <= level) {
				xp += parent.getXPos();
				yp = parent.getYPos();
				retVal[0] = new StringVariable(parent.getSquares().getSquare((int)xp, (int)yp).toString());
			}
			else {
				parent.getPlayer().addText("Out of sight radius " + level);
				retVal[0] = new StringVariable("Unknown");
			}
		}
	}
	public String getName() {
		return "square";
	}
}
class GetVehCommand extends VehicleCommand {
	private double level;
	public GetVehCommand(Vehicle y, int l) {
		super(y);
		level = l;
	}
	public int beginAction() {
		return 0;
	}
	public void finish() {
		float xp, yp, zp;
		if (params.length == 0) {
			xp = (float)parent.getXPos();
			yp = (float)parent.getYPos();
			zp = (float)parent.getZPos();
			ArrayList<Vehicle> vehs = GameGrid.filterByPlayer(parent.getSquares().getVehicleWithHeight(xp, yp, zp, (float)level), 
					parent.getPlayer(), true);
			ArrayList<Variable> vars = new ArrayList<Variable>();
			StringVariable var;
			for (Vehicle v: vehs) {
				if (v != parent) {
					var = new StringVariable(v.toString());
					vars.add(var);
				}
			}
			retVal[0] = new ListVariable(vars);
		}
		else if (params.length == 1 && params[0].getType().equals(Variable.BASIC_STRING)) {
			String name = ((StringVariable)params[0]).getVal();
			ArrayList<Vehicle> vehs;
			if (VehicleHelper.getVehicleID(name) == 0) {
				//check by name
				vehs = parent.getPlayer().getAllVehicle(name, true);
			}
			else {
				//check by type
				vehs = parent.getPlayer().getAllVehicle(name, false);
			}
			ArrayList<Variable> vars = new ArrayList<Variable>();
			StringVariable var;
			for (Vehicle v: vehs) {
				if (v != parent) {
					var = new StringVariable(v.toString());
					vars.add(var);
				}
			}
			retVal[0] = new ListVariable(vars);
		}
		else if (params.length == 2 && params[0].getType().equals(Variable.BASIC_NUM) && params[1].getType().equals(Variable.BASIC_NUM)) {
			xp = (float)((NumVariable)params[0]).getVal();
			yp = (float)((NumVariable)params[1]).getVal();
			ArrayList<Variable> vars = new ArrayList<Variable>();
			if (xp <= level && yp <= level) {
				xp = (float)parent.getXPos();
				yp = (float)parent.getYPos();
				ArrayList<Vehicle> vehs = GameGrid.filterByPlayer(parent.getSquares().getVehicleWithHeight(xp, yp, 
						(float)parent.getZPos(), 0.5f), parent.getPlayer(), true);
				StringVariable var;
				for (Vehicle v: vehs) {
					if (v != parent) {
						var = new StringVariable(v.toString());
						vars.add(var);
					}
				}
				retVal[0] = new ListVariable(vars);
			}
			else {
				parent.getPlayer().addText("Out of sight radius " + level);
				retVal[0] = new ListVariable(vars);
			}
		}
	}
	public String getName() {
		return "getveh";
	}
}
class GetEnemyVehCommand extends VehicleCommand {
	//returns a list with four elements.
	//first element : true if there is an enemy nearby (sight radius), false if not
	//second element : list of names of those enemies
	//third element : list of xpos of those enemies
	//fourth element : list of ypos of those enemies
	private double level;
	public GetEnemyVehCommand(Vehicle y, int l) {
		super(y);
		level = l;
	}
	public int beginAction() {
		return 10;
	}
	public void finish() {
		float xp, yp, zp;
		if (params.length == 0) {
			xp = (float) parent.getXPos();
			yp = (float) parent.getYPos();
			zp = (float) parent.getZPos();
			ArrayList<Vehicle> vehs = GameGrid.filterByPlayer(parent.getSquares().getVehicleWithHeight(xp, yp, zp, 
					(float)level), parent.getPlayer(), false);
			
			ArrayList<Variable> vars = new ArrayList<Variable>();
			
			ArrayList<Variable> names = new ArrayList<Variable>();
			ArrayList<Variable> xposes = new ArrayList<Variable>();
			ArrayList<Variable> yposes = new ArrayList<Variable>();
			
			StringVariable name;
			NumVariable xpos, ypos;
			for (Vehicle v: vehs) {
				name = new StringVariable(v.identifier());
				xpos = new NumVariable(v.getXPos());
				ypos = new NumVariable(v.getYPos());
				names.add(name);
				xposes.add(xpos);
				yposes.add(ypos);
			}
			BoolVariable nearEnem = new BoolVariable(vehs.size() != 0);
			ListVariable namesVar = new ListVariable(names);
			ListVariable xposVar = new ListVariable(xposes);
			ListVariable yposVar = new ListVariable(yposes);
			vars.add(nearEnem);
			vars.add(namesVar);
			vars.add(xposVar);
			vars.add(yposVar);
			retVal[0] = new ListVariable(vars);
		}
	}
	public String getName() {
		return "getenemyveh";
	}
}
class RefineCommand extends VehicleCommand {
	private int level;
	private int tier;
	public RefineCommand(Vehicle y, int l, int t) {
		super(y);
		level = l;
		tier = t;
	}
	public int beginAction() {
		return 100;
	}
	public void finish() {
		parent.getPlayer().refineOres(tier, level);
	}
	public String getName() {
		return "refine";
	}
}
//class EntityCommand
/*
class ResetCommand extends VehicleCommand {
	public String runCommand(Vehicle y, Player z, Variable[] retVal, Variable[] params, VehicleAction act) {
		return CONT;
	}
}*/
