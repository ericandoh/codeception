package com.me.entities;

import java.util.ArrayList;

import com.me.terrain.GameGrid;

public class BulletVehicle extends Vehicle {
	private int life;
	private int damage;
	public BulletVehicle(GameGrid s, int i, Player p, double x, double y,
			double z, String t, String et, float f, int l, int dmg) {
		super(s, i, p, x, y, z, t, et);
		force = f;
		//velocity = force / weight;
		life = l;
		damage = dmg;
	}
	public BulletVehicle(String n, Player p, GameGrid sq) {
		super(n, p, sq);
	}
	@Override
	public void cycle() {
		boolean result = move();
		if (!result) {
			ArrayList<Entity> near = getNearbyEntities();
			for (Entity x: near) {
				//System.out.println("Boom on "+x);
				x.takeDamage(damage);
				addExplosions(pos.xpos, pos.ypos);
			}
			//make spectacular fireworks!
			//y.removeMyself();
			addExplosions(pos.xpos, pos.ypos);
			myPlayer.removeVehicle(this);
			return;
		}
		if (life < 0) {
			addExplosions(pos.xpos, pos.ypos);
			myPlayer.removeVehicle(this);
		}
		else {
			life--;
		}
	}
	private void addExplosions(double x, double y) {
		double x1 = Math.random() - 0.5 + x;
		double x2 = Math.random() - 0.5 + x;
		double y1 = Math.random() - 0.5 + y;
		double y2 = Math.random() - 0.5 + y;
		int[] seq1 = {0, 1, 2, 3, 2, 1, 0};
		int[] seq2 = {4, 4, 0, 1, 2, 1, 0};
		squares.addSprite(new GameSprite(x1, y1, 0, 7, seq1));
		squares.addSprite(new GameSprite(x2, y2, 0, 7, seq2));
	}
	public String encode() {
		return super.encode() + life + SPLITTER + damage + SPLITTER;
	}
	public int decode(String line, Player p, GameGrid sq) {
		int q = super.decode(line, p, sq);
		String[] a = line.split(SPLITTER);
		life = Integer.parseInt(a[q++]);
		damage = Integer.parseInt(a[q++]);
		return q;
	}
}
