package com.me.entities;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.me.fakeai.ActionRunner;
import com.me.fakeai.Variable;
import com.me.fakeai.VehicleCommand;
import com.me.panels.CustomButtonBuilder;
import com.me.render.GameRenderer;
import com.me.render.IconDrawable;
import com.me.terrain.GameGrid;

public class ChipVehicle extends Vehicle {
	protected ActionRunner runner;
	public ChipVehicle(GameGrid s, int i, Player p, double x, double y,
			double z, String t, String et) {
		super(s, i, p, x, y, z, t, et);
		runner = new ActionRunner(p, this);
	}
	public ChipVehicle(String line, Player p, GameGrid grid) {
		super(line, p, grid);
	}
	public void runActions() {
		if (!state || busy > 0) {
			return;
		}
		runner.runActions();
	}
	public Actor controller(CustomButtonBuilder b) {
		//returns a JPanel that is shown when this unit is selected!
		//return new DescriptionPanel();
		
		String info = entityType + "\r\nTerrain: " + type + "\nWeight : " + weight 
				+ "\nPower Used: " + input + "\nSystem ID: " + id + "\nSystem Variables\n";
		for (String key : runner.getVariables().keySet()) {
			info = info + key + ", ";
		}
		Label label = b.getLabel(info);
		label.setWrap(true);
		
		return label;
	}
	public void updateRenderIcon(GameRenderer r) {
		if (health == 0 || !r.matchPlayer(myPlayer))
			return;
		if (isBusy() || runner.isBusy()) {
			if (iconDrawable == null) {
				iconDrawable = new IconDrawable(pos, 0, (float)farthestHeight);
			}
			r.addToRender(iconDrawable);
		}
	}
	public void addQueue(String x) {
		//System.out.println("(ChipVehicle) Adding to queue: "+x);
		if (x.equals("clear()")) {
			runner.clearQueue();
			clear();
			return;
		}
		else if (x.equals("stop()")) {
			runner.stop();
			clear();
		}
		runner.addQueue(x);
	}
	public String getQueueName() {
		return runner.getQueueName();
	}
	@Override
	public boolean isVar(String n) {
		if (super.isVar(n))
			return true;
		return runner.isVar(n);
	}
	@Override
	public Variable getVar(String n) {
		Variable v = super.getVar(n);
		if (v == null)
			return runner.getVar(n);
		return v;
	}
	public void addVar(String n, Variable val) {	
		runner.addVar(n, val);
	}
	public String encode() {
		return super.encode() + runner.encode();
	}
	public int decode(String line, Player p, GameGrid sq) {
		int q = super.decode(line, p, sq);
		String[] a = line.split(SPLITTER);
		runner = new ActionRunner(p, this);
		q = runner.decode(a, q, p, sq);
		return q;
	}
	public void setRets(VehicleCommand r) {
		runner.setRets(r);
	}
}
