package com.me.fakeai;

public class NumVariable extends Variable {
	private double myVal;
	public NumVariable(double e) {
		myVal = e;
	}
	public double getVal() {
		return myVal;
	}
	public String getType() {
		return BASIC_NUM;
	}
	public String toString() {
		return myVal + "";
	}
	public boolean equals(Object obj) {
		if (obj instanceof NumVariable) {
			return Math.abs(myVal - ((NumVariable)obj).getVal()) < 0.01;
		}
		return super.equals(obj);
	}
}