package com.me.fakeai;

public class StringVariable extends Variable {
	private String myVal;
	public StringVariable(String e) {
		myVal = e;
		/*if (e.length() < 2) {
			myVal = e;
		}
		else if (e.charAt(0) == '\'') {
			myVal = e.substring(1, e.length()-1);
		}
		else {
			myVal = e;
		}*/
	}
	public String getVal() {
		return myVal;
	}
	public String getType() {
		return BASIC_STRING;
	}
	public String toString() {
		return "'"+myVal+"'";
	}
	public boolean equals(Object obj) {
		if (obj instanceof StringVariable) {
			return myVal.equals(((StringVariable)obj).getVal());
		}
		return super.equals(obj);
	}
}