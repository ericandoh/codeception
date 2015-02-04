package com.me.codeception;


import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.IdentityMap;
import com.me.io.IO;
import com.me.panels.*;
import com.me.terrain.GameGrid;

public class CodeGame extends Game {
	
	public static final int VERSION = 6; 
	
	public static final int INTERVAL = 1000/20;
	
	public static final String EXIT = "EXIT";
	
	/*public static final int VIRTUAL_WIDTH = 480;
    public static final int VIRTUAL_HEIGHT = 320;
    public static final float ASPECT_RATIO = (float)VIRTUAL_WIDTH/(float)VIRTUAL_HEIGHT;
	*/
	protected IdentityMap<String, Screen> screens;
	protected BitmapFont font;
	protected Skin skin;
	@Override
	public void create() {
		System.out.println("Version: "+VERSION);
		Texture.setEnforcePotImages(false);
		font = new BitmapFont();
		skin = new Skin(Gdx.files.internal("skins/uiskin.json"));
		screens = new IdentityMap<String, Screen>();
		
		//screens.put(key, value)
		screens.put(StartScreen.NAME, new StartScreen(this));
		screens.put(SPSelectionScreen.NAME, new SPSelectionScreen(this));
		screens.put(CampSelectionScreen.NAME, new CampSelectionScreen(this));
		screens.put(GameScreen.NAME, new GameScreen(this));
		//screens.put(CampScreen.NAME, new CampScreen(this));
		
		
		//setScreen(screens.get(StartScreen.NAME));
		switchScreen(StartScreen.NAME, null);
	}
	
	@Override
	public void dispose() {
		for (Screen screen : screens.values()) {
			screen.dispose();
		}
	}
	public BitmapFont getFont() {
		return font;
	}
	public Skin getSkin() {
		return skin;
	}
	public void switchScreen(String n, String info) {
		if (n == null) {
			System.out.println("Uh oh, someone tried to switch to a null screen!");
			return;
		}
		System.out.println("Switching to " + n);
		if (n.equals(EXIT)) {			
			Gdx.app.exit();
			return;
		}
		else if (n.equals(GameScreen.NAME) && info != null) {
			GameGrid p;
			if (info.substring(0, 4).equals(GameScreen.NEW)) {
				p = new GameGrid(info.substring(4), GameScreen.SAND);
				p.initialize();
			}
			else {
				String prefix = info.substring(0, 4);
				String name = info.substring(4);
				p = IO.readFile(name, prefix);
			}
			((GameScreen)(screens.get(GameScreen.NAME))).setPlayerAndMap(p, 0);
			//screens.get(GAME)....;
			//load game into screen
			//p.resumeGame();
		}
		//add handling for if there is extra info needed 
		setScreen(screens.get(n));
	}
	public boolean needsGL20(){
		return true;
	}
	public void addScreen(String x, Screen v) {
		screens.remove(x);
		screens.put(x, v);
	}
	public void removeScreen(String x) {
		screens.remove(x);
	}
}
