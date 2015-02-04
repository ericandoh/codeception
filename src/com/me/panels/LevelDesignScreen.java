package com.me.panels;

import com.me.codeception.CodeGame;
import com.me.render.LevelDesignRenderer;

public class LevelDesignScreen extends GameScreen {

	public LevelDesignScreen(CodeGame g) {
		super(g);
	}
	//override this method for the level design
	@Override
	public void addRenderer(int width, int mapHeight) {
		renderer = new LevelDesignRenderer(width, mapHeight, this);
	}
}
