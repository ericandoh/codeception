package com.me.codeception;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.IdentityMap;
import com.me.panels.CampSelectionScreen;
import com.me.panels.GameScreen;
import com.me.panels.LevelDesignScreen;
import com.me.panels.SPSelectionScreen;
import com.me.panels.StartScreen;

public class CodeGameDesigner extends CodeGame {
	public void create() {
		Texture.setEnforcePotImages(false);
		font = new BitmapFont();
		skin = new Skin(Gdx.files.internal("skins/uiskin.json"));
		screens = new IdentityMap<String, Screen>();
		
		//screens.put(key, value)
		screens.put(StartScreen.NAME, new StartScreen(this));
		screens.put(SPSelectionScreen.NAME, new SPSelectionScreen(this));
		screens.put(CampSelectionScreen.NAME, new CampSelectionScreen(this));
		screens.put(GameScreen.NAME, new LevelDesignScreen(this));
		//screens.put(CampScreen.NAME, new CampScreen(this));
		
		
		//setScreen(screens.get(StartScreen.NAME));
		switchScreen(StartScreen.NAME, null);
	}
}
