package com.me.fakeai;

import java.util.ArrayList;


public class ListVariable extends Variable {
	private ArrayList<Variable> myVal;
	public ListVariable(ArrayList<Variable> e) {
		myVal = e;
	}
	public ListVariable(Variable[] e) {
		myVal = new ArrayList<Variable>();
		for (Variable b: e) {
			myVal.add(b);
		}
	}
	public ListVariable() {
		myVal = new ArrayList<Variable>(); 
	}
	public ArrayList<Variable> getVal() {
		
		return myVal;
	}
	public String getType() {
		return BASIC_LST;
	}
	public String toString() {
		String x = "";
		for (Variable v: myVal) {
			x += v.toString()+",";
		}
		if (x.length()>0)
			x = x.substring(0, x.length()-1);
		return "["+x+"]";
	}
	public boolean equals(Object obj) {
		if (obj instanceof ListVariable) {
			ArrayList<Variable> temp = ((ListVariable)obj).getVal();
			if (temp.size() != myVal.size())
				return false;
			for (int index = 0; index < myVal.size(); index++) {
				if (!myVal.get(index).equals(temp.get(index)))
					return false;
			}
			return true;
		}
		return super.equals(obj);
	}
}