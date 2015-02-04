package com.me.fakeai;

//X to do: while and if loop, change to while()->endwhile, if()->endif
//O error fixing (right now ignores all lines with errors...?)
//X name locking
//O add methods: getAll()-Vehicle, 

import com.me.entities.Player;
import com.me.entities.Vehicle;


//a program is a command that executes many actions at once

public class Program extends Action {
	//private boolean runningParams;
	private int place;	//where you start
	private Action[] actions;
	private String[] paramNames;				//names of variables that will take values
	private Variable[] paramValues;
	private boolean ranParams;
	
	public Program(UserProgram prog) {
		this(prog, new Variable[0]);
	}
	public Program(UserProgram prog, Variable[] paramValues) {
		//super(paramString);
		
		
		this.paramNames = prog.getParamNames();
		this.paramValues = paramValues;
		String[] lines = prog.getLines();
		actions = Action.getActions(lines);
		ranParams = false;
		/*actions = new Action[lines.length];
		for (int i = 0; i < lines.length; i++) {
			actions[i] = Action.getAction(lines[i]);
		}*/
		place = 0;
		//runningParams = true;
	}
	@Override
	public String run(Vehicle y, Player z, Variable[] retVal) {
		if (!ranParams) {
			for (int i = 0; i < Math.min(paramNames.length, paramValues.length); i++) {
				DefCommand.addVar(y, z, paramNames[i], paramValues[i]);
			}
			ranParams = true;
		}
		String result;
		while (place < actions.length) {
			result = actions[place].run(y, z, retVal);
			if (result.equals(STOP))  {
				return STOP;
			}
			place++;
			if (result.equals(PAUSE)) {
				return STOP;
			}
		}
		place = 0;
		return CONT;
	}

	public String encode() {
		String retMe = super.encode() + "PRGM"+SPLITTER+place+SPLITTER+actions[place].encode();;
		return retMe;
	}
	public int decode(String[] info, int index) {
		ranParams = true;
		index = super.decode(info, index);
		if (!info[index++].equals("PRGM")) {
			System.out.println("Error in reading program information string!: "+info[index-1]);
		}
		place = Integer.parseInt(info[index++]);
		index = actions[place].decode(info, index);
		return index;
	}
}


	
	
	/*
	public String encode() {
		String returnMe = name+"\n";
		returnMe = returnMe + place + "$" + subbing + "$" + inMethod + "$" + runParams + "$" + lines.length + "\n";
		for (int index = 0; index < lines.length; index++) {
			returnMe = returnMe + lines[index] + "$#$" + returnVals[index] + "\n";
		}
		if (sub == null) {
			returnMe = returnMe + "$null\n";
		}
		else {
			returnMe = returnMe + "$subpr\n" + sub.encode();
		}
		return returnMe;
	}
	
	public static Program decode(ArrayList<String> lines, int index) {
		String name = lines.get(index);
		String [] secondLine = lines.get(index + 1).split("\\$");
		int place = Integer.parseInt(secondLine[0]);
		boolean subbing = Boolean.parseBoolean(secondLine[1]);
		boolean inMethod = Boolean.parseBoolean(secondLine[2]);
		boolean runParams = Boolean.parseBoolean(secondLine[3]);
		int lengthLines = Integer.parseInt(secondLine[4]);
		String[] myLines = new String[lengthLines];
		Variable[] returnVals = new Variable[lengthLines];
		String thisLine;
		int locDiv;
		Program sub = null;
		for (int i = index + 2; i < lines.size(); i++) {
			thisLine = lines.get(i);
			locDiv = thisLine.indexOf("$#$");
			if (lines.get(i).equals("$subpr")) {
				sub = decode(lines, i + 1);
				break;
			}
			else if (lines.get(i).equals("$null")) {
				break;
			}
			myLines[i - index - 2] = thisLine.substring(0, locDiv);
			returnVals[i - index - 2] = Program.getVar(thisLine.substring(locDiv + 3), null, null);
		}
		
		return new Program(name, place, subbing, inMethod, runParams, myLines, returnVals, sub);
		
		//check: null
		//Program n = new Program("");
		//check: printing, do
		//Program n = new Program("do(print(3),print(5),print(7))\nprint(9)");
		//Program n = new Program("print('bro')");
		//Program n = new Program("do(print(1),do(print(2),do(print(4)),print(3)))");
		//check: add,sub,divide,multiply,rem,round
		//Program n = new Program("print(round(div(mul(3,add(5,sub(5,3))),10)))");
		//check: and, or, >, <, etc.
		//Program n = new Program("print(or(equal(2,3),false))");
		//Program n = new Program("print(>(3,2))");
		//Program n = new Program("print(equal('hi','hello'))");
		//check: define variables
		//Program n = new Program("x=3\ny=3\nprint(add(x,2))\nprint(y)");
		//Program n = new Program("x=3\nprint(x)\nx=add(x,1)\nprint(x)");
		//check: if statements
		//Program n = new Program("if(true)\nprint(add(3,5))\nendif\nprint(4)");
		//Program n = new Program("if(false)\nprint(4)\nprint(6)\nendif\nprint(5)");
		//check: while statements
		//Program n = new Program("x=0\nwhile(lesser(x,3))\nx=add(x,1)\nprint(x)\nendwhile\nprint('bro')");
		//Program n = new Program("x=0\nwhile(false)\nx=add(x,1)\nprint(x)\nendwhile\nprint('bro')");
		//check: lists
		//Program n = new Program("x=list(1,2,3,4)");
		//Program n = new Program("do(print(get(list(1,add(3,5),3),1)),print(and(equal(3,3),true)),print(or(true,true)))");
		//Program n = new Program("n=list()\nprint(n)");
		//check: indirection
		//Program n = new Program("yolo=6\nprint(indir('yolo'))");
		//Program n = new Program("do(y=5,x=0,while(lesser(x,y),do(x=add(x,1),print(x))))");
		//check: list indexing
		//Program n = new Program("n=list(5,6,8,10)\nprint(index(n,11))");
		//Program n = new Program("");
		Program n = new Program("yolo=6\nindir('yolo=7')\nprint(indir('yolo'))");
		//GamePlayPanel g = new GamePlayPanel(null, null, 0);
		
		Square[][] map = new Square[1][1];
		map[0][0] = new Square(0, false, false, true, "Square");
		Player me = new Player(null, 0, 0, 0);
		Vehicle ent = new Vehicle(null, 0, me, 0, 0, 0, "troll", false, false, "BASIC");
		n.run(ent, me);
		//System.out.println(g.getText());*/