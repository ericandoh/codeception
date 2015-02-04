package com.me.entities;

import com.me.terrain.GameGrid;

public class StorageVehicle extends Vehicle {
	private int size;
	public StorageVehicle(GameGrid s, int i, Player p, double x, double y,
			double z, String t, String et, int ex) {
		super(s, i, p, x, y, z, t, et);
		size = ex;
	}
	public StorageVehicle(String n, Player p, GameGrid sq) {
		super(n, p, sq);
	}
	@Override
	public void notifyAdd() {
		myPlayer.changeMaxItemCount(size);
	}
	@Override
	public void notifyRemove() {
		myPlayer.changeMaxItemCount(-size);
	}
	public String encode() {
		return super.encode() + size + SPLITTER;
	}
	public int decode(String line, Player p, GameGrid sq) {
		int q = super.decode(line, p, sq);
		String[] a = line.split(SPLITTER);
		size = Integer.parseInt(a[q++]);
		return q;
	}
}
