package com.me.fakeai;

public class NullVariable extends Variable {
	public boolean isNull() {
		return true;
	}
	public String getType() {
		return BASIC_NULL;
	}
	public String toString() {
		return "null";
	}
	public boolean equals(Object obj) {
		if (obj instanceof NullVariable) {
			return true;
		}
		return super.equals(obj);
	}
}