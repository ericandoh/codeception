package com.me.fakeai;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.lights.Lights;
import com.me.entities.Position;
import com.me.render.FacePlayerDrawable;
import com.me.render.GameRenderer;

public class HPBarDrawable extends FacePlayerDrawable {
	private float percent;
	public HPBarDrawable(Position p, float perc, float farDst) {
		super(p, 0, farDst);
		percent = perc;
		// TODO Auto-generated constructor stub
	}
	@Override
	public Model getModel(GameRenderer r) {
		return r.getHPModel();
	}
	@Override
	public void paintModel(GameRenderer r, ModelBatch mBatch, Lights lights) {
		if (instance == null) {
			if (!remakeImage(r))
				return;
		}
		float xdist = (float) (r.getCamera().position.x - pos.xpos);
		float ydist = (float) (r.getCamera().position.z - pos.ypos);
		//float xdist = r.getCamera().direction.x;
		//float ydist = r.getCamera().direction.z;
		double dir = Math.atan2(xdist, ydist);
		//instance.transform.setToScaling(percent, 1, percent);
		//instance.transform.setToScaling(2f, 2f, 2f);
		//instance.transform.translate((float)pos.xpos, (float)pos.zpos+1.5f, (float)pos.ypos);
		instance.transform.setToTranslationAndScaling((float)pos.xpos, (float)(pos.zpos+0.85f+farDst), (float)pos.ypos, 
				percent, 1f, percent);
		instance.transform.rotate(0, 1, 0, (float)(dir * 180 / Math.PI));
		mBatch.render(instance, lights);
	}
	public void updateHP(float percentage) {
		//instance = null;
		percent = percentage;
	}
}
