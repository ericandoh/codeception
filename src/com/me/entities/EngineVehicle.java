package com.me.entities;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.me.panels.CustomButtonBuilder;
import com.me.terrain.GameGrid;

public class EngineVehicle extends Vehicle {
	//private float force;
	public EngineVehicle(GameGrid s, int i, Player p, double x, double y,
			double z, String t, String et, double sf, double uf, double rf) {
		super(s, i, p, x, y, z, t, et);
		force = (float) sf;
		upForce = (float) uf;
		rotForce = (float) rf;
	}
	public EngineVehicle(String line, Player p, GameGrid grid) {
		super(line, p, grid);
	}

	public Actor controller(CustomButtonBuilder b) {
		//returns a JPanel that is shown when this unit is selected!
		//return new DescriptionPanel();
		String info = entityType + "\r\nTerrain: " + type + "\nWeight : " + weight 
				+ "\nForce: " + force + "\nPower Used: " + input + "\nSystem ID: " + id;
		Label label = b.getLabel(info);
		label.setWrap(true);
		
		return label;
	}
}
