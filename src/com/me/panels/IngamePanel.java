package com.me.panels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.me.entities.Vehicle;

public abstract class IngamePanel extends Table {
	protected String switchBackType;
	protected GameScreen screen;
	protected Actor parent;
	public IngamePanel(GameScreen e, Skin sk) {
		super(sk);
		screen = e;
		switchBackType = e.getType();
		//setBounds(Gdx.graphics.getWidth() / 3, Gdx.graphics.getHeight() / 3, 30, 30);
		//setWidth(Gdx.graphics.getWidth() / 3);
		//setHeight(Gdx.graphics.getHeight() / 3);
		//setBounds(Gdx.graphics.getWidth() / 3, Gdx.graphics.getHeight(), Gdx.graphics.getWidth() / 3, Gdx.graphics.getHeight() / 3);
		setPosition(Gdx.graphics.getWidth() * 0.2f, Gdx.graphics.getHeight() * 0.2f);
		size(Gdx.graphics.getWidth() * 0.6f, Gdx.graphics.getHeight() * 0.6f);
		//setFillParent(true);
	}
	public boolean hasVehicle(Vehicle x) {
		return false;
	}
	/*public void setParentActor(Actor a) {
		parent = a;
	}
	public boolean remove() {
		return parent.remove();
	}*/
	/*public float getMinWidth() {
		return Gdx.graphics.getWidth() / 3;
	}
	public float getMinHeight() {
		return Gdx.graphics.getHeight() / 3;
	}*/
}
