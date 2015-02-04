package com.me.fakeai;

import java.util.ArrayList;




//WORK NEED: if no parameters, shouldnt error up on that
public class UserProgram {
	private String name;
	private String[] lines;
	private String paramNames[];
	public UserProgram(String n, String text) {
		name = n;
		//text = text.replaceAll(" ", "");
		int locenter = text.indexOf("\n");
		int locparen = text.indexOf("(");
		int locparen2 = text.indexOf(")");
		if (text.substring(0,3).equals("def") && locparen < locparen2 && locparen2 < locenter) {
			lines = text.substring(locenter+1).split("\n");
			if (text.substring(locparen + 1, locparen2).equals("")) {
				paramNames = new String[0];
			}
			else {
				paramNames = text.substring(locparen + 1, locparen2).split(",");
			}
		}
		else {
			//Error
			System.out.println("User Program is not formatted correctly!");
			paramNames = new String[0];
		}
	}
	public UserProgram(String n, String[] l, String[] p) {
		name = n;
		lines = l;
		paramNames = p;
	}
	/*public void setParams(Variable[] params, Vehicle y, Player z) {
		if (y.isNull()) {
			int max = Math.min(paramNames.length, params.length);
			for (int index = 0; index < max; index++) {
				z.addVar(paramNames[index], params[index]);
			}
		}
		else {
			int max = Math.min(paramNames.length, params.length);
			for (int index = 0; index < max; index++) {
				y.addVar(paramNames[index], params[index]);
			}
		}
	}
	
	public String getCode() {
		return lines;
	}*/
	public String[] getParamNames() {
		return paramNames;
	}
	public String[] getLines() {
		return lines;
	}
	public String getExtended() {
		String line = "";
		for (String l : lines) {
			line = line + l + "\n";
		}
		return line.substring(0, line.length() - 1);
	}
	public String getTextCode() {
		String n = "def "+name+"(";
		if (paramNames.length == 0) {
			return n + ")\n" + getExtended();
		}
		for (String x: paramNames) {
			n = n + x + ", ";
		}
		return n.substring(0, n.length() - 2) + ")\n" + getExtended();
	}
	public String encode() {
		String returnMe = name+"\n";
		returnMe = returnMe + "$lines\n" + getExtended() + "\n$params\n";
		for (int index = 0; index < paramNames.length; index++) {
			returnMe = returnMe + paramNames[index] + "$";
		}
		returnMe = returnMe + "\n";
		return returnMe;
	}
	public static UserProgram decode(ArrayList<String> lines, int index) {
		String name = lines.get(index);
		if (!lines.get(index+1).equals("$lines")) {
			System.out.println("Corrupted");
		}
		String myLines = "";
		int i;
		for (i = index + 2; i < lines.size(); i++) {
			if (lines.get(i).equals("$params")) {
				break;
			}
			myLines	= myLines + lines.get(i) + "\n";
		}
		String[] paramNames = lines.get(i + 1).split("\\$");
		return new UserProgram(name, myLines.split("\n"), paramNames);
	}
}