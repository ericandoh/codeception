package com.me.panels;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.me.codeception.CodeGame;
import com.me.entities.Player;

public class EditPanel extends SelectionScreen {
	public static final String NAME = "EDIT_PRGM";
	public static final String NEW_PRGM = "def untitled()\n";
	//private String origin;
	private Player myPlayer;
	private CustomTextArea mainEdit;
	public EditPanel(CodeGame g, String origin, Player owner) {
		super(g);
		//this.origin = origin;
		
		int numButtons = 5;
		int bottom = Gdx.graphics.getHeight() * (16 - numButtons) / 32;
		int buttonWidth = Gdx.graphics.getWidth() / 4;
		int buttonHeight = Gdx.graphics.getHeight() / 16;
		
		Actor exitPrgm = builder.getButton("Exit", origin, null);
		exitPrgm.setBounds(0, bottom, buttonWidth, buttonHeight);
		
		stage.addActor(exitPrgm);
		
		myPlayer = owner;
	}
	public void addButtons() {
		int numButtons = 5;
		int bottom = Gdx.graphics.getHeight() * (16 - numButtons) / 32;
		int buttonWidth = Gdx.graphics.getWidth() / 4;
		int buttonHeight = Gdx.graphics.getHeight() / 16;
		
		
		Actor delPrgm = builder.getButton("Delete", null, null);
		delPrgm.setBounds(0, bottom + buttonHeight, buttonWidth, buttonHeight);
		delPrgm.addListener(new InputListener() {
			public boolean touchDown(InputEvent evt, float x, float y, int pointer, int button) {
				generateProgramSelection(false);
				return true;
			}
		});
		
		
		Actor savePrgm = builder.getButton("Save", null, null);
		savePrgm.setBounds(0, bottom + buttonHeight * 2, buttonWidth, buttonHeight);
		savePrgm.addListener(new InputListener() {
			public boolean touchDown(InputEvent evt, float x, float y, int pointer, int button) {
				savePrgm();
				return true;
			}
		});
		
		
		Actor openPrgm = builder.getButton("Open", null, null);
		openPrgm.setBounds(0, bottom + buttonHeight * 3, buttonWidth, buttonHeight);
		openPrgm.addListener(new InputListener() {
			public boolean touchDown(InputEvent evt, float x, float y, int pointer, int button) {
				generateProgramSelection(true);
				return true;
			}
		});
		
		Actor newPrgm = builder.getButton("New", null, null);
		newPrgm.setBounds(0, bottom + buttonHeight * 4, buttonWidth, buttonHeight);
		newPrgm.addListener(new InputListener() {
			public boolean touchDown(InputEvent evt, float x, float y, int pointer, int button) {
				openEditting(null);
				return true;
			}
		});
		stage.addActor(newPrgm);
		stage.addActor(openPrgm);
		stage.addActor(savePrgm);
		stage.addActor(delPrgm);
	}
	
	public void generateProgramSelection(boolean opening) {
		rightScreen.clearChildren();
		Actor header;
		//int width = Gdx.graphics.getWidth();
		int height = Gdx.graphics.getHeight();
		if (opening) {
			header = builder.getLabel("Open Program");
		}
		else {
			header = builder.getLabel("Delete Program");
		}
		header.setBounds(0, height * 14 / 16, getRightWidth(), height / 16);
		rightScreen.addActor(header);
		
		String[] names = myPlayer.getListCommand();
		float startHeight = (float) (height * 13.0 / 16 - names.length / 16.0);
		Actor worldButton;
		for (int i = 0; i < names.length; i++) {
			worldButton = null;
			if (opening) {
				worldButton = builder.getButton(names[i], null, null);
				worldButton.addListener(new EditPrgmListener(names[i]));
			}
			else {
				worldButton = builder.getButton(names[i], null, null);
				worldButton.addListener(new DelPrgmListener(names[i]));
			}
			worldButton.setBounds(0, (float)(startHeight - i * height / 16.0), getRightWidth(), height / 16);
			rightScreen.addActor(worldButton);
		}
	}
	public void openEditting(String info) {
		//int width = Gdx.graphics.getWidth();
		int height = Gdx.graphics.getHeight();
		rightScreen.clearChildren();
		Actor header;
		if (info == null) {
			header = builder.getLabel("Editting New Program");
		}
		else {
			header = builder.getLabel("Editting Program: " + info);
		}
		header.setBounds(0, height * 14 / 16, getRightWidth(), height / 16);
		rightScreen.addActor(header);
		
		if (info == null || info.length() == 0)
			mainEdit = builder.getTextArea(NEW_PRGM);
		else 
			mainEdit = builder.getTextArea(myPlayer.getTextCommand(info));
		
		//mainEdit.setBounds(0, height * 9 / 16, getRightWidth(), height * 5 / 16);
		
		ScrollPane scrollPane2 = new ScrollPane(mainEdit, game.getSkin());
		scrollPane2.setBounds(0, height * 3 / 16, getRightWidth(), height * 11 / 16);
		
		rightScreen.addActor(scrollPane2);
		//rightScreen.addActor(mainEdit);
		
		
		//possible interface for directly testing code...or not
		
	}
	public void savePrgm() {
		if (mainEdit == null)
			return;
		String code = mainEdit.getText();
		//System.out.println(code);
		if (code.equals(NEW_PRGM))
			return;
		String filtered = code.replaceAll(" ", "");
		int firstParen = filtered.indexOf("(");
		if (firstParen < 4) {
			//textDisplay.append("Not a valid command!\n");
			System.out.println("No parenthesis!?!");
			return;
		}
		String name = filtered.substring(3, firstParen);
		if (!filtered.substring(0, 3).equals("def")) {
			//textDisplay.append("Not a valid command: " + name + "\n");
			System.out.println("No def");
			return;
		}
		//System.out.println("Added");
		myPlayer.addCommand(name, code);
		//listNames.setListData(myPanel.getPlayer().getListCommand());
		//textDisplay.append("Saved file: " + name + "\n");
	}
	private class EditPrgmListener extends InputListener {
		private String info;
		public EditPrgmListener(String i) {
			info = i;
		}
		public boolean touchDown(InputEvent evt, float x, float y, int pointer, int button) {
			openEditting(info);
			return true;
		}
	}
	private class DelPrgmListener extends InputListener {
		private String info; 
		public DelPrgmListener(String i) {
			info = i;
		}
		public boolean touchDown(InputEvent evt, float x, float y, int pointer, int button) {
			//int width = Gdx.graphics.getWidth();
			int height = Gdx.graphics.getHeight();
			rightScreen.clearChildren();
			Actor header = builder.getLabel("Delete Program? : " + info);
			header.setBounds(0, height * 14 / 16, getRightWidth(), height / 16);
			rightScreen.addActor(header);
			
			Actor yesButton = builder.getButton("Yes", null, null);
			yesButton.addListener(new InputListener() {
				public boolean touchDown(InputEvent evt, float x, float y, int pointer, int button) {
					myPlayer.removeCommand(info);
					rightScreen.clearChildren();
					return true;
				}
			});
			yesButton.setBounds(0, height * 12 / 16, getRightWidth(), height / 16);
			rightScreen.addActor(yesButton);
			
			Actor noButton = builder.getButton("No", null, null);
			noButton.addListener(new ClearRight());
			noButton.setBounds(0, height * 10 / 16, getRightWidth(), height / 16);
			rightScreen.addActor(noButton);
			return true;
		}
	}
}
