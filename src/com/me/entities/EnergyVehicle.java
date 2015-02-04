package com.me.entities;

import com.me.terrain.GameGrid;

public class EnergyVehicle extends Vehicle {
	private int output;
	public EnergyVehicle(GameGrid s, int i, Player p, double x, double y,
			double z, String t, String et, int ex) {
		super(s, i, p, x, y, z, t, et);
		output = ex;
	}
	public EnergyVehicle(String n, Player p, GameGrid sq) {
		super(n, p, sq);
	}
	@Override
	public void notifyAdd() {
		myPlayer.expandOutput(output);
	}
	@Override
	public void notifyRemove() {
		myPlayer.shrinkOutput(output);
	}
	public String encode() {
		return super.encode() + output + SPLITTER;
	}
	public int decode(String line, Player p, GameGrid sq) {
		int q = super.decode(line, p, sq);
		String[] a = line.split(SPLITTER);
		output = Integer.parseInt(a[q++]);
		return q;
	}
}
