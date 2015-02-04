package com.me.fakeai;

import java.util.ArrayList;

import com.me.entities.NullVehicle;
import com.me.entities.Player;
import com.me.entities.Vehicle;
import com.me.entities.SmartVehicle;

public class Action {
	//an Action is an object that holds information about what to do
	
	public static String CONT = "CONT";				//runs without pause
	public static String STOP = "STOP";				//calling the program again calls this action again
	public static String PAUSE = "PAUSE";			//calling the program again calls the action that follows this action
	
	public static final Action CLEAR_ACT = new Action();
	public static final Action STOP_ACT = new Action();
	public static final Action NULL = new Action();
	
	
	public static final char PARAM_DIVIDER = ',';
	public static final String[] OPERATORS = {"==", "*", "&", "/", "%", "+", "|", "-", "=", "?", ">", "<", ">=", "<="};
	
	//public static final char SPLITTER = (char)7;
	public static final String SPLITTER = Vehicle.SPLITTER;
	
	public Action() {
		
	}
	//run() is a method that puts the return value into retVal and returns a String telling
	//the program what to do
	//Requirements: retVal is an array of length 1 (just used for returning variable values)
	public String run(Vehicle y, Player z, Variable[] retVal) {
		return CONT;
	}
	//encodes information about this action in a string
	//conventionally, add info at the beginning of the string
	public String encode() {
		return "";
	}
	//decodes information about this action, assuming formatting of encoding above.
	public void decode(String info) {
		decode(info.split(""+SPLITTER), 0);
	}
	public int decode(String[] info, int index) {
		return index;
	}
	
	public static Action[] getActions(String[] text) {
		return getActions(text, 0, text.length);
	}
	public static Action[] getActions(String[] text, int start, int end) {
		//if spaces/empty lines, remove those!
		Action act;
		Action[] tempActions = new Action[end - start];
		int count = 0;
		int index = start;
		//for (int i = 0; i < text.length; i++) {
		while (index < end) {
			//check if its a structure (if, for, while, etc)
			if (text[index].length() >= 2 && text[index].substring(0, 2).equals("if")) {
				IfCommand com = new IfCommand(text, index);
				tempActions[count] = com;
				count++;
				index = com.getLastIndex() + 1;
				continue;
			}
			else if (text[index].length() >= 5 && text[index].substring(0, 5).equals("while")) {
				WhileCommand com = new WhileCommand(text, index);
				tempActions[count] = com;
				count++;
				index = com.getLastIndex() + 1;
				continue;
			}
			else {
				act = getAction(text[index]);
				if (act == null) {
					index++;
					continue;
				}
				tempActions[count] = act;
			}
			count++;
			index++;
		}
		Action[] newActions = new Action[count];
		
		for (int i = 0; i < count; i++) {
			newActions[i] = tempActions[i];
		}
		return newActions;
	}
	public static Action getAction(String text) {
		//System.out.println("(Action)Getting action: "+text);
		if (text == null || text.length() == 0) {
			return Action.NULL;
		}
		if (isVariable(text)) {
			return getVar(text);
		}
		if (text.equals("do")) {
			return new DoCommand();
		}
		if (text.equals("enddo")) {
			return new EndDoCommand();
		}
		String [] literals = breakupLiterals(text);
		return getAction(literals);
	}
	public static Action getAction(String[] literals) {
		String name = literals[0];
		String [] params;
		
		if (name.equals("=")) {
			//there is an equal sign
			params = new String[literals.length - 2];
			for (int i = 0; i < params.length; i++) {
				params[i] = literals[i + 2];
			}
			return new DefCommand(literals[1], params);
		}
		params = new String[literals.length - 1];
		for (int i = 0; i < params.length; i++) {
			params[i] = literals[i + 1];
		}
		if (name.equals("*") || name.equals("&")) {
			return new MulCommand(params);
		}
		if (name.equals("/")) {
			return new DivCommand(params);
		}
		if (name.equals("%")) {
			return new ModCommand(params);
		}
		if (name.equals("+") || name.equals("|")) {
			return new AddCommand(params);
		}
		if (name.equals("-")) {
			return new SubCommand(params);
		}
		if (name.equals("?") || name.equals("==")) {
			return new EqualCommand(params);
		}
		if (name.equals(">")) {
			return new GreaterCommand(params);
		}
		if (name.equals("<")) {
			return new LesserCommand(params);
		}
		if (name.equals("<=")) {
			return new LesserEqCommand(params);
		}
		if (name.equals(">=")) {
			return new GreaterEqCommand(params);
		}
		if (name.equals("") || name.equals("do")) {
			return new Command(params);
		}
		else if (name.equals("print")) {
			return new PrintCommand(params);
		}
		else if (name.equals("len")) {
			return new LenCommand(params);
		}
		else if (name.equals("get")) {
			return new GetCommand(params);
		}
		else if (name.equals("set")) {
			return new SetCommand(params);
		}
		else if (name.equals("index")) {
			return new IndexCommand(params);
		}
		else if (name.equals("round")) {
			return new RoundCommand(params);
		}
		else if (name.equals("rand")) {
			return new RandCommand(params);
		}
		else if (name.equals("eq")) {
			return new EqualCommand(params);
		}
		else if (name.equals("!")) {
			return new NotCommand(params);
		}
		else if (name.equals("greater")) {
			return new GreaterCommand(params);
		}
		else if (name.equals("lesser")) {
			return new LesserCommand(params);
		}
		else if (name.equals("greatereq")) {
			return new GreaterEqCommand(params);
		}
		else if (name.equals("lessereq")) {
			return new LesserEqCommand(params);
		}
		else if (name.equals("indir")) {
			return new IndirCommand(params);
		}
		else if (name.equals("str")) {
			return new StrCommand(params[0]);
		}
		else if (name.equals("clear")) {
			return CLEAR_ACT;
		}
		else if (name.equals("stop")) {
			return STOP_ACT;
		}
		return new FetchCommandAction(name, params);
	}
	public static ValueAction getVar(String text) {
		if (text.length() == 0) 
			System.out.println("(Action)Unresolved issue with this thing calling a text of length 0!");
		if (text.charAt(0) == '\'') {
			int lastIndex = text.lastIndexOf('\''); 
			if (lastIndex < 1) {
				return new ErrorAction("Missing '");
			}
			return new ValueAction(new StringVariable(text.substring(1, text.lastIndexOf('\''))));
		}
		else if (text.charAt(0) == '[') {
			int lastIndex = text.lastIndexOf(']'); 
			if (lastIndex < 1) {
				return new ErrorAction("Missing ]");
			}
			String[] lst = breakupLiterals("list("+text.substring(1, text.lastIndexOf(']'))+")");
			String[] params = new String[lst.length - 1];
			for (int i = 0; i < params.length; i++) {
				params[i] = lst[i + 1];
			}
			return new ListValueAction(getActions(params));
		}
		else if (text.equals("true")) {
			return new ValueAction(new BoolVariable(true));
		}
		else if (text.equals("false")) {
			return new ValueAction(new BoolVariable(false));
		}
		else if (isNum(text))
			return new ValueAction(new NumVariable(Double.parseDouble(text)));
		else if (text.equals("null")) {
			return new ValueAction(Variable.NULL);
		}
		return new FetchValueAction(text);
	}
	private static boolean isNum(String text) {
		char y;
		for (int index = 0; index < text.length(); index++) {
			y = text.charAt(index);
			if (!(((int)y >= (int)'0' && (int)y <= (int)'9') || (int)y == (int)'.' || ((int)y == (int)'-' && index == 0)))
				return false;
		}
		return true;
	}
	private static boolean isVariable(String text) {
		for (int i = 0; i < text.length(); i++) {
			if (isAlphaNumeric(text.charAt(i)))
				continue;
			if (text.charAt(i) == '[' || text.charAt(i) == '\'')
				return true;
			if (text.charAt(i) == '-' && i == 0)
				continue;
			if (text.charAt(i) == '(')
				return false;
			for (String c: OPERATORS) {
				if (i+c.length() <= text.length() && text.substring(i, i+c.length()).equals(c))
					return false;
			}
		}
		return true;
	}
	public static boolean isValidName(String text, Vehicle y, Player z) {
		for (char c : text.toCharArray()) {
			if (!isAlphaNumeric(c))
				return false;
		}
		if (text.equals("endwhile") || text.equals("endif"))
			return false;
		else if (y.isSysVariable(text))
			return false;
		else if (z.isSysVariable(text)) {
			return false;
		}
		return true;
	}
	public static boolean isAlphaNumeric(char c) {
		return ('0' <= c && c <= '9') || ('a' <= c && c <= 'z') || ('A' <= c && c <= 'Z');
	}
	//used by Vehicle to break up literals for key binding commands
	public static String[] breakupLiterals(String text) {
		//System.out.println("(Action)Breaking up literals for: "+text);
		if (text.length() == 0)
			return new String[0];
		//find if we have an operator expression or a simple function call
		//if value...(no operators/paren/etc found)
		//if operator, return "+" and parameters in rest of list
		//if function call, return "fnc name" and parameters in rest of list
		//if list definition [1,2,3,4], then return '[' and parameters in list
		
		//[1,2,3,4]+do(blah)->[1,2,3,4]   and  +    and   do(blah) -> +    [1,2,3,4]    do(blah)
		//[1,2,3,4]-> [    and 1 and 2 and 3 and 4
		//hello(blah1, blah2) ->  hello     blah1      blah2
		//something -> function param1 param2 ....
		//1+2+3-> 1, +, 2+3 -> +, 1, 2+3
		int depth1 = 0;
		int depth2 = 0;
		int depth3 = 0;
		int count = 1;
		int operatorLength = 1;
		for (int i = 0; i < text.length(); i++) {
			if (text.charAt(i) == '(') {
				depth1++;
			}
			else if (text.charAt(i) == ')') {
				depth1--;
			}
			else if (text.charAt(i) == '[') {
				depth2++;
			}
			else if (text.charAt(i) == ']') {
				depth2--;
			}
			else if (text.charAt(i) == '\'') {
				if (depth3 == 0)
					depth3 = 1;
				else 
					depth3 = 0;
			}
			else if (text.charAt(i) == '-' && i == 0)
				continue;
			else if (text.charAt(i) == ' ' && depth3 != 1) {
				text = text.substring(0, i) + text.substring(i+1);
			}
			else if (depth1 + depth2 + depth3 == 0) {
				for (String x : OPERATORS) {
					operatorLength = x.length();
					if (text.length() >= i+operatorLength && text.substring(i, i+operatorLength).equals(x)) {
						String[] texts = new String[3];
						texts[0] = text.substring(i, i+operatorLength);
						texts[1] = text.substring(0, i);
						texts[2] = text.substring(i+operatorLength);
						return texts;		
					}
				}
			}
			else if (text.charAt(i) == PARAM_DIVIDER && depth1 == 1 && depth2 + depth3 == 0) {
				count++;
			}
		}
		
		depth1 = depth2 = depth3 = 0;
		int startParen = text.indexOf('(');
		int endParen = text.lastIndexOf(')');
		String[] texts;
		if (startParen == -1) {
			//no paren found
			texts = new String[1];
			texts[0] = text; 
			return texts;
		}
		if (endParen - startParen <= 1) {
			texts = new String[1];
			texts[0] = text.substring(0, startParen); 
			return texts;
		}
		texts = new String[count+1];
		texts[0] = text.substring(0, startParen);
		text = text.substring(startParen+1, endParen);
		count = 1;
		int last = 0;
		for (int i = 0; i < text.length(); i++) {
			if (text.charAt(i) == '(') {
				depth1++;
			}
			else if (text.charAt(i) == ')') {
				depth1--;
			}
			else if (text.charAt(i) == '[') {
				depth2++;
			}
			else if (text.charAt(i) == ']') {
				depth2--;
			}
			else if (text.charAt(i) == '\'') {
				if (depth3 == 0)
					depth3 = 1;
				else 
					depth3 = 0;
			}
			else if (text.charAt(i) == PARAM_DIVIDER && depth1 == 0 && depth2 + depth3 == 0) {
				texts[count] = text.substring(last, i);
				count++;
				last = i + 1;
			}
		}
		texts[count] = text.substring(last);
		return texts;
	}
	public static void main(String[] args) {
		FakePlayer p = new FakePlayer();
		//Vehicle y = new Vehicle(null, -10, p, 5, 5, 5, Entity.SKY, false, false, "PlayerUnit");
		NullVehicle nullVeh = new NullVehicle(p);
		Vehicle y = nullVeh.getChip();
		//this is because it just finds the first operator, not the most encompassing one. So we need to change so it
		//puts parenthesis around the +' operators or something so it read that first) -> 3+5 goes to +3,5
		Action figure;
		//figure = Action.getAction("print('hi')");
		//figure = Action.getAction("print(3)");
		//figure = Action.getAction("do(print(1),do(print(2),do(print(4)),print(3)))");
		//figure = Action.getAction("print(3+5)");
		//figure = Action.getAction("print(1+2+3+4)");
		//figure = Action.getAction("print(3+((5*2)%3))");
		//figure = Action.getAction("print((5*2)%3)");
		//figure = Action.getAction("print(false|true)");
		//figure = Action.getAction("do(x=3,print(x+5))");
		//figure = Action.getAction("cause an error");
		//figure = Action.getAction("print(' hi bob ')");
		//figure = Action.getAction("indir('print(5)')");
		//figure = Action.getAction("do(x=[1,2,3,4],print(get(x,2)))");
		//figure = Action.getAction("do(x=['print(3)','[1,2,3]',[1,2,3,4]],print(get(x,1)),print(get(get(x,2),2)))");
		//figure = Action.getAction("do(endwhile=5,print(endwhile))");
		//figure = Action.getAction("print()");
		//figure = Action.getAction("do(x=[],print(x),x=x+3,print(x))");
		//figure = Action.getAction("do(x=str(lol'x'peke),print(x),x=x+3,print(x))");
		figure = Action.getAction("print(-3-5)");
		//figure = Action.getAction(" ");
		
		Variable[] retVal = new Variable[1];
		figure.run(y, p, retVal);
		
		
		System.out.println("Now testing a program");
		UserProgram ming;
		//ming = new UserProgram("run", "def run()\nx=3\ny=5\nprint(x+y+4)");
		//ming = new UserProgram("run", "def run()\nif(false)\nprint(3)\nelseif(false)\nprint(5)\nelseif(true)\nprint(4)\nendif");
		/*ming = new UserProgram("run", "def run()\n" +
				"loop=true\n" +
				"count=0\n" +
				"while(loop)\n" +
				"count=count+1\n" +
				"print(count)\n" +
				"if(eq(round(rand(5)),1))\n" +
				"loop=false\n" +
				"print('ending')\n" +
				"elseif(count==3)\n" +
				"print(count)\n" + 
				"endif\n" +
				"endwhile\n");*/
		ming = new UserProgram("hi", "def hi()\n" +
				"print(3>2)\n" +
				"if(false)\n" +
				"print(1)\n" +
				"elseif(3<2)\n" +
				"print(2)\n" +
				"else\n" +
				"print(3)\n" +
				"endif"
				);
		figure = new Program(ming);
		for(int i = 0; i < 10; i++) {
			figure.run(y, p, retVal);
		}
		//figure.run(y, p, retVal);
		
		
		String[] literals = breakupLiterals("man");
		for (String x: literals)
			System.out.print(x+",");
		
	}
}
class FakePlayer extends Player {
	public FakePlayer() {
		super(null, 0, 0, 0);
	}
	public void addText(String text) {
		System.out.println("Player: "+text);
	}
}
class ErrorAction extends ValueAction {
	private String message;
	public ErrorAction(String msg) {
		super(null);
		message = msg;
	}
	public String run(Vehicle y, Player z, Variable[] retVal) {
		z.addText(message);
		return PAUSE;
	}
}
class ListValueAction extends ValueAction {
	private Action[] actions;
	private Variable[] values;
	private int place;
	public ListValueAction(Action[] actions) {
		super(null);
		this.actions = actions;
		place = 0;
		values = new Variable[actions.length];
	}
	public String run(Vehicle y, Player z, Variable[] retVal) {
		String result;
		while (place < actions.length) {
			result = actions[place].run(y, z, retVal);
			if (result.equals(STOP))  {
				return STOP;
			}
			values[place] = retVal[0];
			place++;
			if (result.equals(PAUSE)) {
				return STOP;
			}
		}
		//finish running
		place = 0;
		retVal[0] = new ListVariable(values);
		return CONT;
	}
	public Variable getVar() {
		if (var != null)
			return var;
		for(int i = 0; i < actions.length; i++) {
			if (!(actions[i] instanceof ValueAction)) {
				return Variable.NULL;
			}
			values[i] = ((ValueAction)actions[i]).getVar();
		}
		return new ListVariable(values);
	}
	public String encode() {
		int numValues = values.length;
		String retMe = "LIST"+SPLITTER+numValues+SPLITTER+place+SPLITTER;
		for (Variable i : values) {
			if (i == null) {
				retMe = retMe + "null" + SPLITTER;
			}
			else {
				retMe = retMe + i.toString() + SPLITTER;
			}
		}	
		return retMe + actions[place].encode();
	}
	public int decode(String[] info, int index) {
		if (!info[index++].equals("LIST")) {
			System.out.println("Error in reading list information string!: "+info[index-1]);
		}
		int numValues = Integer.parseInt(info[index++]);
		values = new Variable[numValues];
		place = Integer.parseInt(info[index++]);
		for(int i = 0; i < numValues; i++) {
			values[i] = Action.getVar(info[index++]).getVar();
		}
		index = actions[place].decode(info, index);
		return index;
	}
}
class FetchValueAction extends ValueAction {
	private String name;
	public FetchValueAction(String name) {
		super(Variable.NULL);
		this.name = name;
	}
	public String run(Vehicle y, Player z, Variable[] retVal) {
		if (y.isVar(name))
			var = y.getVar(name);
		else if (z.isVar(name))
			var = z.getVar(name);
		else
			z.addText("Unrecognized variable: "+name);
		return super.run(y, z, retVal);
	}
}
class FetchCommandAction extends Command {
	private String name;
	//private String[] paramString;
	private Program subAction;
	private String[] encodeString;
	private int encodeIndex;
	public FetchCommandAction(String name, String[] paramString) {
		super(paramString);
		this.name = name;
		subAction = null;
		encodeString = null;
		encodeIndex = 0;
	}
	public String runPrgm(Vehicle y, Player z, Variable[] retVal) {
		//try to run it from vehicle commands (move)
		//try to run it on vehicle subvehicles (shoot if gun is a subvehicle)
		//try to run it from list of player commands (player-based)
		if (subAction == null) {
			boolean result = y.returnHighestOwner().addCommand(name, paramValues, retVal);
			if (result)
				return CONT;
			subAction = z.getCommand(name, paramValues);
			if (subAction != null) {
				if (encodeString != null) {
					subAction.decode(encodeString, encodeIndex);
				}
				return subAction.run(y,  z, retVal);
			}
			z.addText("Command not recognized: "+name);
			return PAUSE;
		}
		else {
			return subAction.run(y, z, retVal);
		}
	}
	public int getNumParams() {
		return 0;
	}
	public String encode() {
		if (subAction == null) {
			return "FetchNull"+SPLITTER;
		}
		else {
			return "FetchAct"+SPLITTER+subAction.encode();
		}
	}
	public int decode(String[] info, int index) {
		if (info[index++].equals("FetchAct")) {
			encodeString = info;
			encodeIndex = index;
		}
		return index;
	}
}
class DoCommand extends Action {
	public String run(Vehicle y, Player z, Variable[] retVal) {
		if (y instanceof SmartVehicle) {
			((SmartVehicle)y).setIgnoreBusy(true);
		}
		return CONT;
	}
}
class EndDoCommand extends Action {
	public String run(Vehicle y, Player z, Variable[] retVal) {
		if (y instanceof SmartVehicle) {
			((SmartVehicle)y).setIgnoreBusy(false);
		}
		return CONT;
	}
}
class IfCommand extends Action {
	private Action[] paramSubActions;
	private Action[][] actions;
	private int lastIndex;
	private int place, ifPlace;
	public IfCommand(String[] text, int index) {
		if (!text[index].substring(0, 2).equals("if"))
			return;
		int start = index;
		int count = 1;
		int depth = -1;
		while(index < text.length && !(depth == 0 && text[index].length() >= 5 && text[index].substring(0, 5).equals("endif"))) {
			if (text[index].substring(0, 2).equals("if")) {
				depth++;
			}
			else if (depth == 0) {
				if (text[index].length() >= 6 && text[index].substring(0, 6).equals("elseif")) {
					count++;
				}
				else if (text[index].length() >= 4 && text[index].substring(0, 4).equals("else")) {
					count++;
				}
			}
			else if (text[index].length() >= 5 && text[index].substring(0, 5).equals("endif")) {
				depth--;
			}
			index++;
		}
		paramSubActions = new Action[count];
		actions = new Action[count][];
		index = start;
		int last = index;
		depth = -1;
		count = 0;
		paramSubActions[0] = Action.getAction(text[index].substring(3, text[index].length() - 1));
		while(index < text.length && !(depth == 0 && text[index].length() >= 5 && text[index].substring(0, 5).equals("endif"))) {
			if (text[index].substring(0, 2).equals("if")) {
				depth++;
			}
			else if (depth == 0) {
				if (text[index].length() >= 6 && text[index].substring(0, 6).equals("elseif")) {
					actions[count] = Action.getActions(text, last + 1, index);
					count++;
					last = index;
					paramSubActions[count] = Action.getAction(text[index].substring(7, text[index].length() - 1));
				}
				else if (text[index].length() >= 4 && text[index].substring(0, 4).equals("else")) {
					actions[count] = Action.getActions(text, last + 1, index);
					count++;
					last = index;
					paramSubActions[count] = Action.getAction("true");
				}
			}
			else if (text[index].length() >= 5 && text[index].substring(0, 5).equals("endif")) {
				depth--;
			}
			index++;
		}
		actions[count] = Action.getActions(text, last + 1, index);
		lastIndex = index;
		place = -1;
		ifPlace = 0;
	}
	public int getLastIndex() {
		return lastIndex;
	}
	public String run(Vehicle y, Player z, Variable[] retVal) {
		String result;
		if (place == -1) {
			result = paramSubActions[ifPlace].run(y, z, retVal);
			if (result.equals(STOP))
				return STOP;
			if (retVal[0] instanceof BoolVariable && ((BoolVariable)retVal[0]).getVal()) {
				place = 0;
			}
			else {
				ifPlace++;
				if (ifPlace >= paramSubActions.length) {
					place = -1;
					ifPlace = 0;
					return result;
				}
			}
			if (result.equals(PAUSE)) {
				return STOP;
			}
			else {
				return run(y, z, retVal);
			}
		}
		else {
			result = runPrgm(y, z, retVal);
			if (result.equals(STOP))
				return STOP;
			place = -1;
			ifPlace = 0;
			place = -1;
			ifPlace = 0;
			return result;
		}
	}
	public String runPrgm(Vehicle y, Player z, Variable[] retVal) {
		String result;
		while(place < actions[ifPlace].length) {
			result = actions[ifPlace][place].run(y, z, retVal);
			if (result.equals(STOP)) {
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
		return place+SPLITTER+ifPlace+SPLITTER+lastIndex+SPLITTER+"";
	}
	public int decode(String[] info, int index) {
		place = Integer.parseInt(info[index++]);
		ifPlace = Integer.parseInt(info[index++]);
		lastIndex = Integer.parseInt(info[index++]);
		return index;
	}
}

class WhileCommand extends Action {
	public static final int MAX_LOOPS = 500;			//max # of continuous loop line calls/boolean checks in a single time frame
	private Action paramAction;
	private Action[] paramActions;
	private int place, lastIndex;
	public WhileCommand(String[] text, int index) {
		paramAction = Action.getAction(text[index].substring(6, text[index].length() - 1));
		int start = index;
		while (index < text.length && !(text[index].length() >= 8 && text[index].substring(0, 8).equals("endwhile"))) {
			index++;
		}
		paramActions = Action.getActions(text, start + 1, index);
		lastIndex = index;
		place = -1;
	}
	public int getLastIndex() {
		return lastIndex;
	}
	public String run(Vehicle y, Player z, Variable[] retVal) {
		int loopCount = 0;
		while(loopCount < MAX_LOOPS) {
			if (place == -1) {
				String result = paramAction.run(y, z, retVal);
				if (result.equals(STOP))
					return STOP;
				if (retVal[0] instanceof BoolVariable && ((BoolVariable)retVal[0]).getVal()) {
					place = 0;
				}
				else {
					return CONT;
				}
				if (result.equals(PAUSE)) {
					return STOP;
				}
				else {
					//loop back around;
				}
			}
			else {
				String result;
				while(place < paramActions.length) {
					result = paramActions[place].run(y, z, retVal);
					if (result.equals(STOP)) {
						return STOP;
					}
					place++;
					if (result.equals(PAUSE)) {
						return STOP;
					}
				}
				place = -1;
			}
			loopCount++;
		}
		//forced loop stop because too many operations in one while loop - limit infinite while loop bug
		return STOP;
	}
	public String encode() {
		return place+SPLITTER+lastIndex+SPLITTER+"";
	}
	public int decode(String[] info, int index) {
		place = Integer.parseInt(info[index++]);
		lastIndex = Integer.parseInt(info[index++]);
		return index;
	}
}
class LenCommand extends Command {
	public LenCommand(String[] paramString) {
		super(paramString);
	}
	public String runPrgm(Vehicle y, Player z, Variable[] retVal) {
		Variable v = paramValues[0];
		if (v instanceof ListVariable) {
			retVal[0] = new NumVariable(((ListVariable)v).getVal().size());
		}
		else {
			z.addText("Param is not a list");
			return PAUSE;
		}
		return CONT;
	}
	public int getNumParams() {
		return 1;
	}
}
class GetCommand extends Command {
	public GetCommand(String[] paramString) {
		super(paramString);
	}
	public String runPrgm(Vehicle y, Player z, Variable[] retVal) {
		Variable v1 = paramValues[0];
		Variable v2 = paramValues[1];
		if (v1 instanceof ListVariable && v2 instanceof NumVariable) {
			retVal[0] = ((ListVariable)v1).getVal().get((int) ((NumVariable)v2).getVal());
		}
		else {
			z.addText("Param is not a list/num");
			return PAUSE;
		}
		return CONT;
	}
	public int getNumParams() {
		return 2;
	}
}
class SetCommand extends Command {
	public SetCommand(String[] paramString) {
		super(paramString);
	}
	public String runPrgm(Vehicle y, Player z, Variable[] retVal) {
		Variable v1 = paramValues[0];
		Variable v2 = paramValues[1];
		Variable v3 = paramValues[2];
		if (v1 instanceof ListVariable && v2 instanceof NumVariable) {
			retVal[0] = ((ListVariable)v1).getVal().set((int) ((NumVariable)v2).getVal(), v3);
		}
		else {
			z.addText("Param is not a num/list");
			return PAUSE;
		}
		return CONT;
	}
	public int getNumParams() {
		return 3;
	}
}

class AddCommand extends Command {
	public AddCommand(String[] paramString) {
		super(paramString);
	}
	public String runPrgm(Vehicle y, Player z, Variable[] retVal) {
		String result = runOneWay(y, z, retVal);
		if (!result.equals(CONT)) {
			Variable temp;
			temp = paramValues[0];
			paramValues[0] = paramValues[1];
			paramValues[1] = temp;
			result = runOneWay(y, z, retVal);
			if (result.equals(PAUSE)) {
				z.addText("Param is not valid");
				return PAUSE;
			}
			return CONT;
		}
		else {
			return CONT;
		}
	}
	public String runOneWay(Vehicle y, Player z, Variable[] retVal) {
		Variable v1 = paramValues[0];
		Variable v2 = paramValues[1];
		
		if (v1 instanceof NumVariable) {
			if (v2 instanceof NumVariable) {
				retVal[0] = new NumVariable(((NumVariable)v1).getVal() + ((NumVariable)v2).getVal());
			}
			else if (v2 instanceof BoolVariable) {
				retVal[0] = new NumVariable(((NumVariable)v1).getVal() + (((BoolVariable)v2).getVal() ? 1 : 0));
			}
			else {
				return PAUSE;
			}
		}
		else if (v1 instanceof ListVariable) { 
			if (v2 instanceof ListVariable) {
				ArrayList<Variable> vlist = new ArrayList<Variable>();
				vlist.addAll(((ListVariable)v1).getVal());
				vlist.addAll(((ListVariable)v2).getVal());
				retVal[0] = new ListVariable(vlist);
			}
			else {
				((ListVariable)v1).getVal().add(v2);
			}
		}
		else if (v1 instanceof StringVariable) {
			if (v2 instanceof StringVariable) {
				retVal[0] = new StringVariable(((StringVariable)v1).getVal() + ((StringVariable)v2).getVal());
			}
			else if (v2 instanceof NumVariable) {
				double val = ((NumVariable)v2).getVal();
				if (val == (int)val)
					retVal[0] = new StringVariable(((StringVariable)v1).getVal() + (int)val);
				else
					retVal[0] = new StringVariable(((StringVariable)v1).getVal() + val);
			}
			else if (v2 instanceof BoolVariable) {
				retVal[0] = new StringVariable(((StringVariable)v1).getVal() + ((BoolVariable)v2).getVal());
			}
			else {
				return PAUSE;
			}
		}
		else if (v1 instanceof BoolVariable) {
			if (v2 instanceof BoolVariable) {
				retVal[0] = new BoolVariable(((BoolVariable)v1).getVal() | ((BoolVariable)v2).getVal());
			}
			else {
				return PAUSE;
			}
		}
		else {
			return PAUSE;
		}
		return CONT;
	}
	public int getNumParams() {
		return 2;
	}
}
class SubCommand extends Command {
	public SubCommand(String[] paramString) {
		super(paramString);
	}
	public String runPrgm(Vehicle y, Player z, Variable[] retVal) {
		Variable v1 = paramValues[0];
		Variable v2 = paramValues[1];
		if (v1 instanceof NumVariable && v2 instanceof NumVariable) {
			retVal[0] = new NumVariable(((NumVariable)v1).getVal() - ((NumVariable)v2).getVal());
		}
		else if (v1 instanceof ListVariable && v2 instanceof NumVariable) {
			retVal[0] = ((ListVariable)v1).getVal().remove((int)((NumVariable)v2).getVal());
		}
		else {
			z.addText("Param is not valid");
			return PAUSE;
		}
		return CONT;
	}
	public int getNumParams() {
		return 2;
	}
}
class MulCommand extends Command {
	public MulCommand(String[] paramString) {
		super(paramString);
	}
	public String runPrgm(Vehicle y, Player z, Variable[] retVal) {
		Variable v1 = paramValues[0];
		Variable v2 = paramValues[1];
		if (v1 instanceof NumVariable && v2 instanceof NumVariable) {
			retVal[0] = new NumVariable(((NumVariable)v1).getVal() * ((NumVariable)v2).getVal());
		}
		else if (v1 instanceof BoolVariable && v2 instanceof BoolVariable) {
			retVal[0] = new BoolVariable(((BoolVariable)v1).getVal() && ((BoolVariable)v2).getVal());
		}
		else if (v1 instanceof NumVariable && v2 instanceof BoolVariable) {
			retVal[0] = new NumVariable(((NumVariable)v1).getVal() * (((BoolVariable)v2).getVal() ? 1 : 0));
		}
		else if (v1 instanceof BoolVariable && v2 instanceof NumVariable) {
			retVal[0] = new NumVariable((((BoolVariable)v1).getVal() ? 1 : 0) * ((NumVariable)v2).getVal());
		}
		else {
			z.addText("Param is not valid");
			return PAUSE;
		}
		return CONT;
	}
	public int getNumParams() {
		return 2;
	}
}
class DivCommand extends Command {
	public DivCommand(String[] paramString) {
		super(paramString);
	}
	public String runPrgm(Vehicle y, Player z, Variable[] retVal) {
		Variable v1 = paramValues[0];
		Variable v2 = paramValues[1];
		if (v1 instanceof NumVariable && v2 instanceof NumVariable) {
			retVal[0] = new NumVariable(((NumVariable)v1).getVal() / ((NumVariable)v2).getVal());
		}
		else {
			z.addText("Param is not a num/num");
			return PAUSE;
		}
		return CONT;
	}
	public int getNumParams() {
		return 2;
	}
}
class ModCommand extends Command {
	public ModCommand(String[] paramString) {
		super(paramString);
	}
	public String runPrgm(Vehicle y, Player z, Variable[] retVal) {
		Variable v1 = paramValues[0];
		Variable v2 = paramValues[1];
		if (v1 instanceof NumVariable && v2 instanceof NumVariable) {
			retVal[0] = new NumVariable(((NumVariable)v1).getVal() % ((NumVariable)v2).getVal());
		}
		else {
			z.addText("Param is not a num/num");
			return PAUSE;
		}
		return CONT;
	}
	public int getNumParams() {
		return 2;
	}
}
class IndexCommand extends Command {
	public IndexCommand(String[] paramString) {
		super(paramString);
	}
	public String runPrgm(Vehicle y, Player z, Variable[] retVal) {
		Variable v1 = paramValues[0];
		Variable v2 = paramValues[1];
		if (v1 instanceof ListVariable) {
			ArrayList<Variable> vlist = ((ListVariable)v1).getVal();
			Variable v;
			for (int i = 0; i < vlist.size(); i++) {
				v = vlist.get(i);
				if (v.equals(v2)) {
					retVal[0] = new NumVariable(i);
				}
			}
			retVal[0] = new NumVariable(-1);
		}
		else {
			z.addText("Param is not a num/num");
			return PAUSE;
		}
		return CONT;
	}
	public int getNumParams() {
		return 2;
	}
}
class RoundCommand extends Command {
	public RoundCommand(String[] paramString) {
		super(paramString);
	}
	public String runPrgm(Vehicle y, Player z, Variable[] retVal) {
		Variable v1 = paramValues[0];
		if (v1 instanceof NumVariable) {
			retVal[0] = new NumVariable(Math.round(((NumVariable)v1).getVal()));
		}
		else {
			z.addText("Param is not a num/num");
			return PAUSE;
		}
		return CONT;
	}
	public int getNumParams() {
		return 1;
	}
}
class RandCommand extends Command {
	public RandCommand(String[] paramString) {
		super(paramString);
	}
	public String runPrgm(Vehicle y, Player z, Variable[] retVal) {
		if (paramValues.length >= 1) {
			Variable v1 = paramValues[0];
			if (v1 instanceof NumVariable) {
				retVal[0] = new NumVariable(Math.random()*((NumVariable)v1).getVal());
				return CONT;
			}
			else {
				z.addText("Param is not a num/num");
				return PAUSE;
			}
		}
		else {
			retVal[0] = new NumVariable(Math.random());
			return CONT;
		}
	}
	public int getNumParams() {
		return 0;
	}
}
class PrintCommand extends Command {
	public PrintCommand(String[] paramString) {
		super(paramString);
	}
	public String runPrgm(Vehicle y, Player z, Variable[] retVal) {
		for (Variable v : paramValues) {
			if (v instanceof StringVariable) {
				z.addText(((StringVariable)v).getVal());
			}
			else {
				z.addText(v.toString());
			}
		}
		return CONT;
	}
	public int getNumParams() {
		return 0;
	}
}
class NotCommand extends Command {
	public NotCommand(String[] paramString) {
		super(paramString);
	}
	public String runPrgm(Vehicle y, Player z, Variable[] retVal) {
		Variable v1 = paramValues[0];
		if (v1 instanceof BoolVariable) {
			retVal[0] = new BoolVariable(!((BoolVariable)v1).getVal());
		}
		else if (v1 instanceof NumVariable) {
			retVal[0] = new BoolVariable(((NumVariable)v1).getVal() == 0 ? false : true);
		}
		else {
			z.addText("Param is not a bool");
			return PAUSE;
		}
		return CONT;
	}
	public int getNumParams() {
		return 1;
	}
}
class EqualCommand extends Command {
	public EqualCommand(String[] paramString) {
		super(paramString);
	}
	public String runPrgm(Vehicle y, Player z, Variable[] retVal) {
		Variable v1 = paramValues[0];
		Variable v2 = paramValues[1];
		
		retVal[0] = new BoolVariable(v1.equals(v2));
		
		return CONT;
	}
	public int getNumParams() {
		return 2;
	}
}
class GreaterCommand extends Command {
	public GreaterCommand(String[] paramString) {
		super(paramString);
	}
	public String runPrgm(Vehicle y, Player z, Variable[] retVal) {
		Variable v1 = paramValues[0];
		Variable v2 = paramValues[1];
		if (v1 instanceof NumVariable && v2 instanceof NumVariable) {
			retVal[0] = new BoolVariable(((NumVariable)v1).getVal() > ((NumVariable)v2).getVal());
		}
		else {
			z.addText("Param is not a num/num");
			return PAUSE;
		}
		return CONT;
	}
	public int getNumParams() {
		return 2;
	}
}
class LesserCommand extends Command {
	public LesserCommand(String[] paramString) {
		super(paramString);
	}
	public String runPrgm(Vehicle y, Player z, Variable[] retVal) {
		Variable v1 = paramValues[0];
		Variable v2 = paramValues[1];
		if (v1 instanceof NumVariable && v2 instanceof NumVariable) {
			retVal[0] = new BoolVariable(((NumVariable)v1).getVal() < ((NumVariable)v2).getVal());
		}
		else {
			z.addText("Param is not a num/num");
			return PAUSE;
		}
		return CONT;
	}
	public int getNumParams() {
		return 2;
	}
}
class GreaterEqCommand extends Command {
	public GreaterEqCommand(String[] paramString) {
		super(paramString);
	}
	public String runPrgm(Vehicle y, Player z, Variable[] retVal) {
		Variable v1 = paramValues[0];
		Variable v2 = paramValues[1];
		if (v1 instanceof NumVariable && v2 instanceof NumVariable) {
			retVal[0] = new BoolVariable(((NumVariable)v1).getVal() >= ((NumVariable)v2).getVal());
		}
		else {
			z.addText("Param is not a num/num");
			return PAUSE;
		}
		return CONT;
	}
	public int getNumParams() {
		return 2;
	}
}
class LesserEqCommand extends Command {
	public LesserEqCommand(String[] paramString) {
		super(paramString);
	}
	public String runPrgm(Vehicle y, Player z, Variable[] retVal) {
		Variable v1 = paramValues[0];
		Variable v2 = paramValues[1];
		if (v1 instanceof NumVariable && v2 instanceof NumVariable) {
			retVal[0] = new BoolVariable(((NumVariable)v1).getVal() <= ((NumVariable)v2).getVal());
		}
		else {
			z.addText("Param is not a num/num");
			return PAUSE;
		}
		return CONT;
	}
	public int getNumParams() {
		return 2;
	}
}
class IndirCommand extends Command {
	private Action subAction;
	public IndirCommand(String[] paramString) {
		super(paramString);
	}
	public String runPrgm(Vehicle y, Player z, Variable[] retVal) {
		if (subAction != null) {
			return subAction.run(y, z, retVal);
		}
		else {
			Variable v1 = paramValues[0];
			if (v1 instanceof StringVariable) {
				subAction = Action.getAction(((StringVariable)v1).getVal());
				return subAction.run(y, z, retVal);
			}
			else {
				z.addText("Param is not a str");
				return PAUSE;
			}
		}
	}
	public int getNumParams() {
		return 1;
	}
}
//a command that takes in a non-parenthesized string (ex. str(hi)->'hi')
class StrCommand extends ValueAction {
	public StrCommand(String paramString) {
		super(new StringVariable(paramString));
	}
}