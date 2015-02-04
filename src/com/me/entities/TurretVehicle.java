package com.me.entities;

import com.me.terrain.GameGrid;

public class TurretVehicle extends SmartVehicle {
	private float multiplier;
	public TurretVehicle(GameGrid sq, int i, Player p, double x, double y,
			double z, String t, String et, float mult) {
		super(sq, i, p, x, y, z, t, et);
		multiplier = mult;
	}
	public TurretVehicle(String n, Player p, GameGrid sq) {
		super(n, p, sq);
	}
	public float getMult() {
		return multiplier;
	}
	public String encode() {
		return super.encode() + multiplier + SPLITTER;
	}
	public int decode(String line, Player p, GameGrid sq) {
		int q = super.decode(line, p, sq);
		String[] a = line.split(SPLITTER);
		multiplier = Float.parseFloat(a[q++]);
		return q;
	}
}
