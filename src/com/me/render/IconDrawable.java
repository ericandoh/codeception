package com.me.render;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.lights.Lights;
import com.me.entities.Position;

public class IconDrawable extends FacePlayerDrawable {
	public static final float ICON_SCROLL_SPEED = 0.02f;
	private float iconDirection;
	private float progress;
	public IconDrawable(Position p, int i, float farDst) {
		super(p, i, farDst);
		progress = 0f;
		iconDirection = ICON_SCROLL_SPEED;
		// TODO Auto-generated constructor stub
	}
	public void paintModel(GameRenderer r, ModelBatch mBatch, Lights lights) {
		progress += iconDirection;
		if (progress > 1f) {
			iconDirection = -ICON_SCROLL_SPEED;
		}
		else if (progress < 0f) {
			iconDirection = ICON_SCROLL_SPEED;
		}
		if (instance == null) {
			if (!remakeImage(r))
				return;
		}
		float xdist = (float) (r.getCamera().position.x - pos.xpos);
		float ydist = (float) (r.getCamera().position.z - pos.ypos);
		//float xdist = r.getCamera().direction.x;
		//float ydist = r.getCamera().direction.z;
		double dir = Math.atan2(xdist, ydist);
		instance.transform.setToTranslationAndScaling((float)pos.xpos, (float)(pos.zpos+0.65f+farDst), (float)pos.ypos, 
				progress, 1f, progress);
		instance.transform.rotate(0, 1, 0, (float)(dir * 180 / Math.PI));
		mBatch.render(instance, lights);
	}
	@Override
	public Model getModel(GameRenderer r) {
		return r.getFaceModel(id);
	}
}
