package com.me.panels;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.me.codeception.CodeGame;

public class StartScreen implements Screen {
	
	public static final String NAME = "START";
	/*
	private static final int VIRTUAL_WIDTH = CodeGame.VIRTUAL_WIDTH;
    private static final int VIRTUAL_HEIGHT = CodeGame.VIRTUAL_HEIGHT;
    private static final float ASPECT_RATIO = (float)VIRTUAL_WIDTH/(float)VIRTUAL_HEIGHT;
	*/
	private CodeGame game;
	
	private Stage stage;
	
	private Texture background;
	
	public StartScreen(CodeGame g) {
		game = g;
		create();
	}
	public void create() {
		int width = Gdx.graphics.getWidth();
		int height = Gdx.graphics.getHeight();
		
		background = new Texture(Gdx.files.internal("graphics/start.png"));
		
		stage = new Stage();
		
		Image backImage = new Image(background);
		backImage.setBounds(0, 0, width, height);
		
		CustomButtonBuilder buttonBuilder = new CustomButtonBuilder(game);
		Actor startButton = buttonBuilder.getButton("Single Player", SPSelectionScreen.NAME, null);
		
		Actor multiButton = buttonBuilder.getButton("Multi Player", null, null);
		
		Actor campButton = buttonBuilder.getButton("Campaign", CampSelectionScreen.NAME, null);
		
		Actor exitButton = buttonBuilder.getButton("Exit", CodeGame.EXIT, null);
		
		startButton.setBounds(width * 2 / 3, height * 1 / 4 + height * 3 / 16, width / 4, height / 16);
		multiButton.setBounds(width * 2 / 3, height * 1 / 4 + height * 2/ 16, width / 4, height / 16);
		campButton.setBounds(width * 2 / 3, height * 1 / 4 + height / 16, width / 4, height / 16);
		exitButton.setBounds(width * 2 / 3, height * 1 / 4, width / 4, height / 16);
		
		stage.addActor(backImage);
		stage.addActor(startButton);
		stage.addActor(multiButton);
		stage.addActor(campButton);
		stage.addActor(exitButton);
	}
	@Override
	public void render(float delta) {
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		stage.act(Gdx.graphics.getDeltaTime());
		
		stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		stage.setViewport(width, height, false);
		//stage.setViewport(1200, 300, false);
		//stage.setViewport(width, height, keepAspectRatio)
		//stage.getCamera().position.set(width / 2, height / 2, 0);
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		background.dispose();
		// TODO Auto-generated method stub
		stage.dispose();
	}
	
}
