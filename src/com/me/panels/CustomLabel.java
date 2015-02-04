package com.me.panels;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.me.codeception.CodeGame;

public class CustomLabel extends Label {
	/*private String name;
	private TextureRegion region;
	private float fontX, fontY;*/
	//private CodeGame game;
	public CustomLabel(CodeGame g, String n) {
		super(n, g.getSkin());
		setWrap(true);
		/*name = n;
		region = b;*/
		//game = g;
		//resizeText();
	}
	public void append(String t) {
		//System.out.println("(CustomLabel)Appending string: "+t);
		String result;
		if (getText().equals("")) {
			result = t;
		}
		else {
			result = getText() + "\n" + t;
		}
		//String result = getText() + "\n" + t;
		if (result.split("\n").length > 20) {
			setText(result.substring(result.indexOf("\n") + 1));
		}
		else {
			setText(result);
		}
		//setHeight(getHeight() + this.getFontScaleY());
		//validate();
		//System.out.println("(CustomLabel) Commented out pack() line");
		//pack();
	}
	@Override
	public float getPrefWidth() {
		//return this.getStyle().font.getSpaceWidth() * getText().length();
		return this.getStyle().font.getMultiLineBounds(getText()).width;
	}
	/*
	public void setBounds(float x, float y, float width, float height) {
		super.setBounds(x, y, width, height);
		resizeText();
	}
	public void resizeText() {
		fontX = getX() + getWidth() / 2 - game.getFont().getBounds(name).width / 2;
		fontY = getY() + getHeight() / 2 + game.getFont().getBounds(name).height / 2;
	}*/
	/*
	public void draw(SpriteBatch batch, float parentAlpha) {
		batch.draw(region, getX(), getY(), getWidth(), getHeight());
		game.getFont().draw(batch, name, fontX, fontY);
	}*/
	
}
