package com.me.panels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.me.codeception.CodeGame;

public class SelectionScreen implements Screen {
	public static final String NAME = "GEN_SELECTION";
	
	protected CodeGame game;
	
	protected Stage stage;
	
	private Texture bar;
	
	protected WidgetGroup rightScreen;
	
	protected CustomButtonBuilder builder;
	
	public SelectionScreen(CodeGame g) {
		game = g;
		create();
	}
	
	public void create() {
		int width = Gdx.graphics.getWidth();
		int height = Gdx.graphics.getHeight();
		
		Texture.setEnforcePotImages(false);
		//load textures
		bar = new Texture(Gdx.files.internal("graphics/border.png"));
		
		stage = new Stage();
		//add actors
		rightScreen = new WidgetGroup();
		rightScreen.setBounds(width / 3, 0, width * 2 / 3, height);
		
		Image barImg = new Image(bar);
		barImg.setBounds(width / 4, 0, width / 30, height);
		
		builder = new CustomButtonBuilder(game);
		addButtons();
		//Actor b = buttonBuilder.getButton("Button Name", SelectionScreen.NAME, SelectionScreen.SINGLE);
		//b.setBounds(width * 2 / 3, height * 1 / 4 + height * 3 / 16, width / 4, height / 16);
		
		stage.addActor(rightScreen);
		stage.addActor(barImg);
		//stage.addActor(b);
		
		
		Gdx.input.setInputProcessor(stage);
	}
	public void addButtons() {
		int numButtons = 2;
		int bottom = Gdx.graphics.getHeight() * (16 - numButtons) / 32;
		int buttonWidth = Gdx.graphics.getWidth() / 4;
		int buttonHeight = Gdx.graphics.getHeight() / 16;
		Actor newGame = builder.getButton("Sample", null, null);
		newGame.setBounds(0, bottom, buttonWidth, buttonHeight);
		
		Actor openGame = builder.getButton("Sample 2", null, null);
		openGame.setBounds(0, bottom + buttonHeight, buttonWidth, buttonHeight);
		
		
		stage.addActor(newGame);
		stage.addActor(openGame);
		//stage.addActor(b);
	}
	public float getRightWidth() {
		return Gdx.graphics.getWidth() * 6 / 11;
	}
	@Override
	public void render(float delta) {
		// TODO Auto-generated method stub
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		stage.act(Gdx.graphics.getDeltaTime());
		
		stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		stage.setViewport(width, height, false);
	}
	@Override
	public void dispose() {
		bar.dispose();
		stage.dispose();
	}
	@Override
	public void show() {
		// TODO Auto-generated method stub
		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		rightScreen.clearChildren();
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}
	protected class ClearRight extends InputListener {
		public boolean touchDown(InputEvent evt, float x, float y, int pointer, int button) {
			rightScreen.clearChildren();
			return true;
		}
	}
	
	
	
}
