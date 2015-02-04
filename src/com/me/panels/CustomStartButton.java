package com.me.panels;


import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.me.codeception.CodeGame;

public class CustomStartButton extends TextButton {
	private String next, info;
	private CodeGame game;
	//private float fontX, fontY;
	public CustomStartButton(CodeGame g, String name, String n, String i) {
		super(name, g.getSkin());
		next = n;
		info = i;
		game = g;
		//setTouchable(Touchable.enabled);
		if (next != null) {
			addListener(new InputListener() {
				//enter exit
				/*public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
					select = 1;
				}
				public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
					select = 0;
				}*/
				/*public void touchUp(InputEvent evt, float x, float y, int pointer, int button) {
					//received even when not over actor
					select = 0;
				}*/
				/*public boolean mouseMoved(InputEvent evt, float x, float y) {
					select = 1;
					return true;
				}*/
				public boolean touchDown(InputEvent evt, float x, float y, int pointer, int button) {
					//System.out.println("Pressed, switching to " + next + " with info "+info);
					//select = 2;
					//select = 0;
					game.switchScreen(next, info);
					return true;
				}
			});
		}
		/*
		else {
			addListener(new InputListener() {
				//enter exit
				public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
					select = 1;
				}
				public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
					select = 0;
				}
			});
		}
		resizeText();*/
	}
	/*
	public void setBounds(float x, float y, float width, float height) {
		super.setBounds(x, y, width, height);
		resizeText();
	}
	public void resizeText() {
		fontX = getX() + getWidth() / 2 - game.getFont().getBounds(name).width / 2;
		fontY = getY() + getHeight() / 2 + game.getFont().getBounds(name).height / 2;
	}
	public void draw(SpriteBatch batch, float parentAlpha) {
		batch.draw(regions.get(select), getX(), getY(), getWidth(), getHeight());
		game.getFont().draw(batch, name, fontX, fontY);
	}*/
}