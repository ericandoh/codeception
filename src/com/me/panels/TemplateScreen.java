package com.me.panels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.me.codeception.CodeGame;

public class TemplateScreen implements Screen {
	public static final String NAME = "GEN_SELECTION";
	
	
	private CodeGame game;
	
	private Stage stage;
	
	
	public TemplateScreen(CodeGame g) {
		game = g;
		create();
	}
	
	public void create() {
		int width = Gdx.graphics.getWidth();
		int height = Gdx.graphics.getHeight();

		//load textures
		
		//img = new Texture(Gdx.files.internal("pic.png"));
		
		stage = new Stage();
		//add actors
		
		//Image a = new Image(tex);
		//a.setBounds(0, 0, width, height);
		//CustomButtonBuilder buttonBuilder = new CustomButtonBuilder(game);
		//Actor b = buttonBuilder.getButton("Button Name", SelectionScreen.NAME, SelectionScreen.SINGLE);
		//b.setBounds(width * 2 / 3, height * 1 / 4 + height * 3 / 16, width / 4, height / 16);
		//stage.addActor(a);
		//stage.addActor(b);
		
		
		Gdx.input.setInputProcessor(stage);
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
	}
	@Override
	public void dispose() {
		stage.dispose();
	}
	@Override
	public void show() {
		
	}

	@Override
	public void hide() {
		
	}

	@Override
	public void pause() {
		
	}

	@Override
	public void resume() {
		
	}

	
	
	
}
