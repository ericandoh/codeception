package com.me.entities;

import com.me.render.Drawable;
import com.me.render.GameRenderer;
import com.me.render.SequenceDrawable;


public class GameSprite {
	
	public static final int NUM_SPRITES = 1;
	public static final int DELAY = 5;
	public static final int MAX_SEQ_NUM = 5;
	
	//private double xpos, ypos;
	private Position pos;
	private int type;
	private int life;
	private int lifespan;
	private int[] cycle;

	private Drawable myDrawable;
	
	public GameSprite(double x, double y, int t, int l) {
		pos = new Position(x, y, 0);
		type = NUM_SPRITES >= t ? NUM_SPRITES - 1 : t;
		lifespan = l * DELAY;
		cycle = new int[MAX_SEQ_NUM];
		for (int i = 0; i < MAX_SEQ_NUM; i++) {
			cycle[i] = i;
		}
		life = 0;
		myDrawable = new SequenceDrawable(pos, type);
	}
	public GameSprite(double x, double y, int t, int l, int[] c) {
		this(x, y, t, l);
		cycle = c;
		myDrawable = new SequenceDrawable(pos, type);
	}
	/*public void paintComponent(Graphics g, double x, double y, double w, double h) {
		int choose = cycle[(life / DELAY) % (cycle.length)];
		g.drawImage(IMAGE[type][choose], (int)((xpos-x)*w), (int)((ypos-y)*h), (int)(w + 1), (int)(h + 1), null);
	}*/
	public void updateRender(GameRenderer r) {
		int choose = cycle[(life / DELAY) % (cycle.length)];
		((SequenceDrawable)myDrawable).updateSelect(choose);
		r.addToRender(myDrawable);
	}
	public void cycle() {
		if (life < lifespan)
			life++;
	}
	public boolean expired() {
		return life >= lifespan;
	}
}
