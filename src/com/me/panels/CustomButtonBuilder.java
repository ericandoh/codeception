package com.me.panels;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.me.codeception.CodeGame;

public class CustomButtonBuilder {
	//private TextButtonStyle style;
	//private Array<TextureRegion> buttons;
	private CodeGame game;
	public CustomButtonBuilder(CodeGame g) {
		game = g;
		/*Texture button = game.getImage(CodeGame.BUTTON_IMG);
		TextureRegion buttonUpTex = new TextureRegion(button, 0, 0, 64, 64);
		
		TextureRegion buttonDownTex = new TextureRegion(button, 64, 0, 64, 64);
		
		//TextureRegion buttonCheckedTex = new TextureRegion(button, 128, 0, 64, 64);
		buttons = new Array<TextureRegion>(2);
		buttons.add(buttonUpTex);
		buttons.add(buttonDownTex);*/
		//buttons.add(buttonCheckedTex);
		/*style = new TextButtonStyle(new SpriteDrawable(buttonUp), new SpriteDrawable(buttonDown), 
				new SpriteDrawable(buttonChecked), new BitmapFont());*/
	}
	public CustomStartButton getButton(String text, String dest, String info) {
		return new CustomStartButton(game, text, dest, info);
	}
	public CustomLabel getLabel(String text) {
		return new CustomLabel(game, text);
	}
	public CustomScrollableLabel getScrollableLabel(String text) {
		CustomScrollableLabel x = new CustomScrollableLabel(game);
		x.append(text);
		return x;
	}
	public TextField getTextField(String text) {
		TextField field = new TextField(text, game.getSkin());
		//field.addListener(new TextListener(field));
		return field;
	}
	public CustomTextArea getTextArea(String text) {
		return new CustomTextArea(game, text);
	}
	public Skin getSkin() {
		return game.getSkin();
	}
	public void switchScreen(String next, String info) {
		game.switchScreen(next, info);
	}
	/*
	private class TextListener extends InputListener {
		private TextField field;
		public TextListener(TextField f) {
			field = f;
		}
		public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
			field.getStage().setKeyboardFocus(null);
		}
	}*/
}
