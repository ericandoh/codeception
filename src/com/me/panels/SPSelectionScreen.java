package com.me.panels;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.me.codeception.CodeGame;
import com.me.io.IO;

public class SPSelectionScreen extends SelectionScreen {
	public static final String NAME = "SP_SELECTION";
	public SPSelectionScreen(CodeGame g) {
		super(g);
	}
	public void addButtons() {
		int numButtons = 4;
		int bottom = Gdx.graphics.getHeight() * (16 - numButtons) / 32;
		int buttonWidth = Gdx.graphics.getWidth() / 4;
		int buttonHeight = Gdx.graphics.getHeight() / 16;
		
		Actor exitGame = builder.getButton("Exit", StartScreen.NAME, null);
		exitGame.setBounds(0, bottom, buttonWidth, buttonHeight);
		
		Actor delGame = builder.getButton("Delete Game", null, null);
		delGame.setBounds(0, bottom + buttonHeight, buttonWidth, buttonHeight);
		delGame.addListener(new InputListener() {
			public boolean touchDown(InputEvent evt, float x, float y, int pointer, int button) {
				generateWorldButtons(false);
				return true;
			}
		});
		
		Actor openGame = builder.getButton("Open Game", null, null);
		openGame.setBounds(0, bottom + buttonHeight * 2, buttonWidth, buttonHeight);
		openGame.addListener(new InputListener() {
			public boolean touchDown(InputEvent evt, float x, float y, int pointer, int button) {
				generateWorldButtons(true);
				return true;
			}
		});
		
		Actor newGame = builder.getButton("New Game", null, null);
		newGame.setBounds(0, bottom + buttonHeight * 3, buttonWidth, buttonHeight);
		newGame.addListener(new InputListener() {
			public boolean touchDown(InputEvent evt, float x, float y, int pointer, int button) {
				generateNewWorldButtons();
				return true;
			}
		});
		
		
		
		stage.addActor(newGame);
		stage.addActor(openGame);
		stage.addActor(delGame);
		stage.addActor(exitGame);
		//stage.addActor(b);
	}
	public void generateWorldButtons(boolean create) {
		rightScreen.clearChildren();
		Actor header;
		//int width = Gdx.graphics.getWidth();
		int height = Gdx.graphics.getHeight();
		if (create) {
			header = builder.getLabel("Open World");
		}
		else {
			header = builder.getLabel("Delete World");
		}
		header.setBounds(0, height * 14 / 16, getRightWidth(), height / 16);
		rightScreen.addActor(header);
		String[] names = IO.getListFiles();
		float startHeight = (float) (height * 13.0 / 16 - names.length / 16.0);
		Actor worldButton;
		for (int i = 0; i < names.length; i++) {
			if (create) {
				worldButton = builder.getButton(names[i], GameScreen.NAME, GameScreen.SAND + names[i]);
			}
			else {
				worldButton = builder.getButton(names[i], null, null);
				worldButton.addListener(new DelWorldButtonListener(names[i]));
			}
			worldButton.setBounds(0, (float)(startHeight - i * height / 16.0), getRightWidth(), height / 16);
			rightScreen.addActor(worldButton);
		}
	}
	private class DelWorldButtonListener extends InputListener {
		private String info; 
		public DelWorldButtonListener(String i) {
			info = i;
		}
		public boolean touchDown(InputEvent evt, float x, float y, int pointer, int button) {
			//int width = Gdx.graphics.getWidth();
			int height = Gdx.graphics.getHeight();
			rightScreen.clearChildren();
			Actor header = builder.getLabel("Delete World? : " + info);
			header.setBounds(0, height * 14 / 16, getRightWidth(), height / 16);
			rightScreen.addActor(header);
			
			Actor yesButton = builder.getButton("Yes", null, null);
			yesButton.addListener(new InputListener() {
				public boolean touchDown(InputEvent evt, float x, float y, int pointer, int button) {
					IO.deleteWorld(info);
					game.switchScreen(StartScreen.NAME, null);
					return true;
				}
			});
			yesButton.setBounds(0, height * 12 / 16, getRightWidth(), height / 16);
			rightScreen.addActor(yesButton);
			
			Actor noButton = builder.getButton("No", StartScreen.NAME, null);
			noButton.setBounds(0, height * 10 / 16, getRightWidth(), height / 16);
			rightScreen.addActor(noButton);
			return true;
		}
	}
	public void generateNewWorldButtons() {
		rightScreen.clearChildren();
		//int width = Gdx.graphics.getWidth();
		int height = Gdx.graphics.getHeight();
		Actor header = builder.getLabel("Make a new World");
		header.setBounds(0, height * 14 / 16, getRightWidth(), height / 16);
		rightScreen.addActor(header);
		
		//TextField inputName = new TextField("LOL", game.getSkin());
		TextField inputName = builder.getTextField("Game 1"); 
		inputName.setBounds(0, height * 12 / 16, getRightWidth(), height / 16);
		rightScreen.addActor(inputName);
		
		Actor openButton = builder.getButton("Open", null, null);
		openButton.addListener(new OpenButtonListener(inputName));
		openButton.setBounds(0, height * 10 / 16, getRightWidth(), height / 16);
		rightScreen.addActor(openButton);
	}
	private class OpenButtonListener extends InputListener {
		private TextField field;
		private String text;
		public OpenButtonListener(TextField f) {
			field = f;
		}
		public boolean touchDown(InputEvent evt, float x, float y, int pointer, int button) {
			text = field.getText();
			boolean warning = false;
			for (String t : IO.getListFiles()) {
				if (text.equals(t)) {
					warning = true;
				}
			}
			
			if (!warning) {
				game.switchScreen(GameScreen.NAME, convertNewWorldName(text));
				return true;
			}
			
			//int width = Gdx.graphics.getWidth();
			int height = Gdx.graphics.getHeight();
			rightScreen.clearChildren();
			Actor header = builder.getLabel("Overwrite World? : " + text);
			header.setBounds(0, height * 14 / 16, getRightWidth(), height / 16);
			rightScreen.addActor(header);
			
			Actor yesButton = builder.getButton("Yes", null, null);
			yesButton.addListener(new InputListener() {
				public boolean touchDown(InputEvent evt, float x, float y, int pointer, int button) {
					game.switchScreen(GameScreen.NAME, convertNewWorldName(text));
					return true;
				}
			});
			yesButton.setBounds(0, height * 12 / 16, getRightWidth(), height / 16);
			rightScreen.addActor(yesButton);
			
			Actor noButton = builder.getButton("No", StartScreen.NAME, null);
			noButton.setBounds(0, height * 10 / 16, getRightWidth(), height / 16);
			rightScreen.addActor(noButton);
			return true;
		}
	}
	private String convertNewWorldName(String text) {
		return GameScreen.NEW + text;
	}
}
