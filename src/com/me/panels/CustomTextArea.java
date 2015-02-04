package com.me.panels;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.me.codeception.CodeGame;


public class CustomTextArea extends Table {
	static private final String ENTER_DESKTOP = "\n";
	
	private Skin skin;
	private int line;
	
	public CustomTextArea(CodeGame g, String n) {
		super();
		////System.out.println("Making new CustomTextArea");
		skin = g.getSkin();
		line = 0;
		setFillParent(true);
		addListener(new InputListener() {
			public boolean keyDown(InputEvent event, int keycode) {
				if (!updateLineCount()) {
					////System.out.println("Nothing here has focus");
					return false;
				}
				if (keycode == Input.Keys.ENTER) {
					makeNewLine("", line);
					goToLine(line + 1);
					return true;
				}
				else if (keycode == Input.Keys.UP) {
					////System.out.println("Moving up a line");
					goToLine(line - 1);
					return true;
				}
				else if (keycode == Input.Keys.DOWN) {
					//System.out.println("Moving down a line");
					goToLine(line + 1);
					return true;
				}
				else if (keycode == Input.Keys.BACKSPACE && ((TextField)(getCells().get(line).getWidget())).getText().length() == 0) {
					deleteLine(line);
					return true;
				}
				return false;
			}
		});
		setText(n);
		//System.out.println("Finished setting text");
	}
	public boolean updateLineCount() {
		//System.out.print("Updating line count...");
		if (getStage() == null) {
			System.out.println("Uh oh stage is null!");
			return false;
		}
		for (int i = 0; i < getCells().size(); i++) {
			if (getStage().getKeyboardFocus() == getCells().get(i).getWidget()) {
				line = i;
				//System.out.println(" at line "+line);
				return true;
			}
		}
		return false;
	}
	//goes to the next line to type in (basically next TextField)
	public void goToLine(int newLine) {
		//System.out.println("Going to line "+newLine);
		if (newLine <= 0)
			line = 0;
		else if (line >= getCells().size() - 1)
			line = getCells().size() - 1;
		else
			line = newLine;
		/*while (line >= getCells().size()) {
			makeNewLine("");
		}*/
		getStage().setKeyboardFocus((Actor)getCells().get(line).getWidget());
	}
	public void makeNewLine(String lineText, int newLine) {
		//System.out.println("Making new line");
		String prevText = lineText;
		String nextText;
		TextField f;
		for (int i = newLine + 1; i < getCells().size(); i++) {
			f = ((TextField)(getCells().get(i).getWidget()));
			nextText = f.getText();
			f.setText(prevText);
			prevText = nextText;
		}
		makeNewLine(prevText);
	}
	public void makeNewLine(String lineText) {
		//System.out.println("Making new line");
		CustomTextFieldInTextArea field = new CustomTextFieldInTextArea(lineText);
		//.getCells().get(line).row();
		//.getCells().add(newLine, .add(field));
		//.row();
		//.getCells().remove(.getCells().size() - 1);
		add(field).expandX().left();
		row();
		//resize();
	}
	public void resize() {
		/*//System.out.println("resizing");
		int numLines = .getCells().size();
		float prefHeight;
		if (numLines == 0) {
			return;
		}
		else {
			prefHeight = ((TextField)(.getCells().get(0))).getPrefHeight();
		}
		int count = 0;
		TextField f;
		for (Actor a : .getChildren()) {
			if (a instanceof TextField) {
				f = (TextField)a;
				//f.setBounds(0, prefHeight * (numLines - count - 1), getWidth(), prefHeight);
				count++;
			}
		}
		if (getHeight() < prefHeight * numLines)
			setHeight(prefHeight*numLines);
		//pack();
		//validate();*/
	}
	public void deleteLine(int lineNum) {
		//System.out.println("Deleting line "+lineNum);
		getChildren().get(lineNum).remove();
		getCells().remove(lineNum);
		//.getCells().get(lineNum).getWidget().remove();
		//.getCells().get(0).getWidget()
		if (getCells().size() == 0) {
			makeNewLine("");
			line = 0;
		}
		getStage().setKeyboardFocus((Actor) getCells().get(getCells().size() - 1).getWidget());
		/*
		else if (.getCells().size() <= line) {
			getStage().setKeyboardFocus((Actor) .getCells().get(.getCells().size() - 1).getWidget());
		}
		else {
			getStage().setKeyboardFocus((Actor) .getCells().get(line).getWidget());
		}*/
		pack();
		validate();
	}
	//add a Listener so that you can take in text events and display them.
	//force a word wrap (or an enter) when enter is pressed. etc...
	public void setText(String text) {
		//System.out.println("Setting text : " + text);
		clearChildren();
		String[] lines;
		lines = text.split(ENTER_DESKTOP);
		for (String i : lines) {
			System.out.println("Making new line : " + i);
			makeNewLine(i);
		}
	}
	public String getText() {
		String text = "";
		TextField f;
		for (Actor a : getChildren()) {
			if (a instanceof TextField) {
				f = (TextField)a;
				text = text + f.getText() + ENTER_DESKTOP;
			}
		}
		//System.out.println("Returning text : "+text);
		return text;
	}
	/*public void addActor(Actor a) {
		if (a instanceof TextField) {
			super.addActor(a);
		}
	}*/
	@Override
	public void setBounds(float x, float y, float width, float height) {
		super.setBounds(x, y, width, height);
		pack();
		validate();
	}
	private class CustomTextFieldInTextArea extends TextField {
		private CustomTextFieldInTextArea(String t) {
			super(t, skin);
			//System.out.println("Making new text field with text " + t);
			//addListener(new SpecialTextAreaListener(this));
			//setFocusTraversal(false);
			//setDisabled(false);
			//setVisible(true);
		}
		
		/*public void next(boolean up) {
			//does nothing
		}*/
		/*
		private class SpecialTextAreaListener extends InputListener {
			private CustomTextFieldInTextArea owner;
			private SpecialTextAreaListener(CustomTextFieldInTextArea a) {
				owner = a;
			}
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				boolean returned = super.touchDown(event, x, y, pointer, button);
				//System.out.println("This component was touched");
				if (getStage().getKeyboardFocus() == owner) {
					line = .getChildren.indexOf(owner, true);
				}
				return returned;
			}
		}*/
		public float getPrefWidth() { 
			return Math.max(800f, getText().length() * getStyle().font.getSpaceWidth());
		}
	}
	
	
	
	
	
	
	
}