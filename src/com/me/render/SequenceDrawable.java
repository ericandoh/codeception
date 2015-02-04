package com.me.render;


import com.badlogic.gdx.graphics.g3d.Model;
import com.me.entities.Position;


public class SequenceDrawable extends Drawable {
	//protected TextureRegion[] myImages;
	private int select;
	public SequenceDrawable(Position p, int type) {
		super(p, type);
		//myImages = null;
		select = 0;
	}
	public void updateSelect(int s) {
		select = s;
	}
	@Override
	public Model getModel(GameRenderer r) {
		//r.getSprite(id);
		return null;
	}
}