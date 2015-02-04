package com.me.fakeai;


public class BoolVariable extends Variable {
	private boolean myVal;
	public BoolVariable(boolean e) {
		myVal = e;
	}
	public boolean getVal() {
		return myVal;
	}
	public String getType() {
		return BASIC_BOOL;
	}
	public String toString() {
		return myVal + "";
	}
	public boolean equals(Object obj) {
		if (obj instanceof BoolVariable) {
			return myVal == ((BoolVariable)obj).getVal();
		}
		return super.equals(obj);
	}
}