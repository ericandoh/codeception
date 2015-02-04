package com.me.panels;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.Array;
import com.me.entities.Player;
import com.me.entities.Vehicle;

public class KeyBindingPanel extends IngamePanel {
	protected Vehicle vehicle;
	protected Array<Array<TextField>> fields;
	private CustomButtonBuilder builder;
	private Table selections;
	public KeyBindingPanel(GameScreen g, Vehicle v, CustomButtonBuilder b) {
		super(g, b.getSkin());
		vehicle = v;
		builder = b;
		
		
		//add(builder.getLabel("Keys")).fillX();
		add(builder.getLabel("Keys")).width(getWidth() / 2).fillX();
		add(builder.getLabel("Command")).width(getWidth() / 2).fillX();
		row();
		
		String[][] data = vehicle.getKeyData();

		fields = new Array<Array<TextField>>();;
		TextField field;
		selections = new Table();
		for (int i = 0; i < data.length; i++) {
			fields.add(new Array<TextField>());
			field = builder.getTextField(data[i][0]);
			fields.get(i).add(field);
			selections.add(field).fillX();
			field = builder.getTextField(data[i][1]);
			fields.get(i).add(field);
			selections.add(field).fillX();
			selections.row();
		}
		
		add(selections).width(getWidth()).colspan(2).fillX();
		row();
		
		Actor addLine = builder.getButton("Add", null, null);
		addLine.addListener(new InputListener() {
			public boolean touchDown(InputEvent evt, float x, float y, int pointer, int button) {
				addLine();
				return true;
			}
		});
		add(addLine).fillX();
		
		Actor deleteLine = builder.getButton("Delete", null, null);
		deleteLine.addListener(new InputListener() {
			public boolean touchDown(InputEvent evt, float x, float y, int pointer, int button) {
				deleteLine();
				return true;
			}
		});
		add(deleteLine).fillX();
		
		row();
		
		Actor exit = builder.getButton("EXIT", null, null);
		exit.addListener(new InputListener() {
			public boolean touchDown(InputEvent evt, float x, float y, int pointer, int button) {
				saveKeys();
				remove();
				//builder.switchScreen(switchBackType, null);
				return true;
			}
		});
		add(exit).colspan(2).fillX();
		//setBounds();
		//pack();
		//invalidate();
		//System.out.println(getWidth() + "/" + getHeight());
		//drawDebug(getStage());
	}
	public KeyBindingPanel(GameScreen g, Player p, CustomButtonBuilder b) {
		this(g, p.getNullVehicle(), b);
	}
	public void addLine() {
		TextField field;
		fields.add(new Array<TextField>());
		field = builder.getTextField("");
		fields.get(fields.size - 1).add(field);
		selections.add(field).fillX();
		field = builder.getTextField("");
		fields.get(fields.size - 1).add(field);
		selections.add(field).fillX();
		selections.row();
	}
	public void deleteLine() {
		//int loc1, loc2;
		for (int i = 0; i < fields.size; i++) {
			if (fields.get(i).get(0) == getStage().getKeyboardFocus() || fields.get(i).get(1) == getStage().getKeyboardFocus() ) {
				//remove line
				//loc1 = selections.getChildren().indexOf(fields.get(i).get(0), true);
				//loc2 = selections.getChildren().indexOf(fields.get(i).get(1), true);
				//selections.getChildren().get(loc1).remove();
				//selections.getChildren().get(loc2).remove();
				
				selections.getCells().remove(i * 2 + 1);
				selections.getCells().remove(i * 2);
				
				fields.get(i).get(0).remove();
				fields.get(i).get(1).remove();
				
				fields.removeIndex(i);
				
				selections.getStage().setKeyboardFocus(null);
				
				return;
			}
		}
	}
	public void saveKeys() {
		vehicle.resetKeySet();
		String key, val;
		for (int x = 0; x < fields.size; x++) {
			key = fields.get(x).get(0).getText();
			if (key == null || key.equals("")) {
				continue;
			}
			key = key.toLowerCase();
			if (!isTypedChar(key))
				continue;
			val = fields.get(x).get(1).getText();
			if (val == null) {
				val = "";
			}
			vehicle.addKeySet(key, val);
		}
	}
	public boolean isTypedChar(String t) {
		if (t.equals("space"))
			return true;
		if (t.length() > 1)
			return false;
		char c = t.charAt(0);
		return ('0' <= c && c <= '9') || ('a' <= c && c <= 'z');
	}
	public boolean hasVehicle(Vehicle x) {
		return vehicle == x;
	}
}
