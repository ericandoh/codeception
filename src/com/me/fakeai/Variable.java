package com.me.fakeai;

public class Variable {
	public static final String BASIC_NULL = "null";
	public static final String BASIC_NUM = "num";
	public static final String BASIC_BOOL = "bool";
	public static final String BASIC_STRING = "str";
	public static final String BASIC_VEH = "ent";
	public static final String BASIC_LST = "list";
	
	public static final NullVariable NULL = new NullVariable();
	
	public boolean isNull() {
		return false;
	}
	public String getType() {
		return BASIC_NULL;
	}
	public String toString() {
		return "undef";
	}
	public boolean equals(Object obj) {
		return super.equals(obj);
	}
}