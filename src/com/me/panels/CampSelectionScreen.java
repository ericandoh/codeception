package com.me.panels;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.me.codeception.CodeGame;
import com.me.io.IO;

public class CampSelectionScreen extends SelectionScreen {
	public static final String NAME = "CAMP_SELECTION";
	public CampSelectionScreen(CodeGame g) {
		super(g);
	}
	@Override
	public void addButtons() {
		int numButtons = 3;
		int bottom = Gdx.graphics.getHeight() * (16 - numButtons) / 32;
		int buttonWidth = Gdx.graphics.getWidth() / 4;
		int buttonHeight = Gdx.graphics.getHeight() / 16;
		
		Actor exitGame = builder.getButton("Exit", StartScreen.NAME, null);
		exitGame.setBounds(0, bottom, buttonWidth, buttonHeight);
		

		Actor delGame = builder.getButton("Restart Mission", null, null);
		delGame.setBounds(0, bottom + buttonHeight, buttonWidth, buttonHeight);
		delGame.addListener(new InputListener() {
			public boolean touchDown(InputEvent evt, float x, float y, int pointer, int button) {
				generateWorldButtons(true);
				return true;
			}
		});
		
		Actor openGame = builder.getButton("Open Mission", null, null);
		openGame.setBounds(0, bottom + buttonHeight*2, buttonWidth, buttonHeight);
		openGame.addListener(new InputListener() {
			public boolean touchDown(InputEvent evt, float x, float y, int pointer, int button) {
				generateWorldButtons(false);
				return true;
			}
		});
		
		stage.addActor(openGame);
		stage.addActor(delGame);
		stage.addActor(exitGame);
	}
	public void generateWorldButtons(boolean restart) {
		rightScreen.clearChildren();
		Actor header;
		//int width = Gdx.graphics.getWidth();
		int height = Gdx.graphics.getHeight();
		if (restart) {
			header = builder.getLabel("New Mission");
		}
		else {
			header = builder.getLabel("Open Mission");
		}
		header.setBounds(0, height * 14 / 16, getRightWidth(), height / 16);
		rightScreen.addActor(header);
		String[] names = IO.getCampaignFiles();
		float startHeight = height * 13 / 16 - names.length / 16;
		Actor worldButton;
		for (int i = 0; i < names.length; i++) {
			if (restart) {
				worldButton = builder.getButton("( Start New )"+names[i], GameScreen.NAME, GameScreen.BASE_CAMP+names[i]);
			}
			else {
				if (IO.hasCampaignSaveFile(names[i])) {
					worldButton = builder.getButton("(In Progress)"+names[i], GameScreen.NAME, GameScreen.USER_CAMP+names[i]);
				}
				else {
					worldButton = builder.getButton("( Start New )"+names[i], GameScreen.NAME, GameScreen.BASE_CAMP+names[i]);
				}
			}
			worldButton.setBounds(0, startHeight - i * height / 16f, getRightWidth(), height / 16);
			rightScreen.addActor(worldButton);
		}
	}
}
