package com.me.entities;

import java.util.ArrayList;

public class Item {
	private int amount;
	public Item() {
		amount = 1;
	}
	public Item(int a) {
		amount = a;
	}
	public String toString() {
		if (amount > 1)
			return amount + " " + getName() + "s";
		return amount + " " + getName();
	}
	public String getName() {
		return "Quodex";
	}
	public int getAmount() {
		return amount;
	}
	public void setAmount(int i) {
		amount = i;
	}
	public void addAmount(int i) {
		amount += i;
	}
	public int getTier() {
		return 0;
	}
	public static Item getItem(String id) {
		return getItem(id, 1);
	}
	public static Item getItem(String id, int amount) {
		Item returnMe;
		if (id.equals("IronOre")) {
			returnMe = new IronOre();
		}
		else if (id.equals("Log")) {
			returnMe = new Log();
		}
		else {
			returnMe = new UnnamedItem(id);
		}
		returnMe.setAmount(amount);
		return returnMe;
	}
	public static Item  getRefinedItem(String id, int amount) {
		Item returnMe;
		if (id.equals("IronOre")) {
			returnMe = new UnnamedItem("IronBar");
		}
		else {
			returnMe = new UnnamedItem(id);
		}
		returnMe.setAmount(amount);
		return returnMe;
	}
	public static Item decode(ArrayList<String> lines, int index) {
		//System.out.println(lines.get(index));
		String[] line = lines.get(index).split("\\$");
		//System.out.println(Arrays.toString(line));
		String name = line[0];
		int amount = Integer.parseInt(line[1]);
		return getItem(name, amount);
	}
}
class UnnamedItem extends Item {
	private String myName;
	public UnnamedItem(String a) {
		super();
		myName = a;
	}
	public UnnamedItem(String a, int b) {
		super(b);
		myName = a;
	}
	public String getName() {
		return myName;
	}
	
}
class OreItem extends Item {
	public int getTier() {
		return 1;
	}
}
class IronOre extends OreItem { 
	public String getName() {
		return "IronOre";
	}
}
class Log extends Item { 
	public String getName() {
		return "Log";
	}
}