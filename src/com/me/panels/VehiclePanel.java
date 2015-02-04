package com.me.panels;


import java.util.ArrayList;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.me.entities.Vehicle;
import com.me.entities.SmartVehicle;

public class VehiclePanel extends IngamePanel {
	protected Vehicle vehicle;
	private CustomStartButton onOff;
	private CustomLabel display;
	private TextField field;
	private CustomButtonBuilder builder;
	public VehiclePanel(GameScreen g, Vehicle v, CustomButtonBuilder b) {
		super(g, b.getSkin());
		vehicle = v;
		builder = b;
		//on+off button (top left)
		onOff = builder.getButton(v.getState() ? "ON" : "OFF", null, null);
		onOff.addListener(new InputListener() { 
			public boolean touchDown(InputEvent evt, float x, float y, int pointer, int button) {
				if (vehicle.getState()) {
					vehicle.turnOff();
				}
				else {
					vehicle.turnOn();
				}
				if (vehicle.getState()) {
					onOff.setText("ON");
				}
				else {
					onOff.setText("OFF");
				}
				return true;
			}
		});
		add(onOff).width(getWidth() / 4).fillX();
		//name (right of onoff)
		Actor nameLabel = builder.getLabel(vehicle.getName());
		add(nameLabel).width(getWidth() / 4).fillX();
		//HP Bar (right of name)
		HPBar bar = new HPBar(builder);
		add(bar).width(getWidth() / 4).fillX();
		//exit (right of HP)
		Actor exit = builder.getButton("EXIT", null, null);
		exit.addListener(new InputListener() {
			public boolean touchDown(InputEvent evt, float x, float y, int pointer, int button) {
				remove();
				//builder.switchScreen(switchBackType, null);
				return true;
			}
		});
		add(exit).width(getWidth() / 4).fillX();
		row();
		
		//--------------
		
		
		//get custom pane from vehicle (below top bar)
		Actor custom = vehicle.controller(builder);
		ScrollPane pane = new ScrollPane(custom);
		add(pane).height(getHeight() / 2).fillX();
		//textpane/area to submit to vehicle (field at way bottom) (area to right of custom veh pane, with clear button on bottom)
		display = builder.getLabel(vehicle.getQueueName());
		add(display).height(getHeight() / 2).fillX();
		//subentity panel (to right of text area) (no open button - just click)
		//if no subentities, this panel does NOT exist!!!
		
		//make method in Vehicle for getting only relevant ones
		
		Table subentities = new Table();
		
		Label subent = builder.getLabel("Subentities");
		
		subentities.add(subent).fillX();
		
		ArrayList<Vehicle> vehs;
		if (vehicle instanceof SmartVehicle) {
			vehs = ((SmartVehicle)vehicle).getVehicles();
		}
		else {
			vehs = new ArrayList<Vehicle>();
		}
		
		Actor button;
		for (Vehicle veh : vehs) {
			button = builder.getButton(veh.getName(), null, null);
			button.addListener(new VehListener(veh));
			subentities.row();
			subentities.add(button).fillX();
		}
		subentities.setFillParent(true);
		
		ScrollPane pane2 = new ScrollPane(subentities);
		
		add(pane2).height(getHeight() / 2).fillX();
		//shortcut commands-buttons on right with text - "build('Blah')" List of commands that are keyed-in, wihtout key ref
		//can refer to key: "build('Blah')[b]"
		Table subcommands = new Table();
		
		Label subcom = builder.getLabel("Commands");
		
		subcommands.add(subcom).fillX();
		
		String[][] data = vehicle.getKeyData();
		for (String[] pair : data) {
			button = builder.getButton(pair[1], null, null);
			button.addListener(new CommandListener(pair[1]));
			subcommands.row();
			subcommands.add(button).fillX();
			subcommands.row();
		}
		subcommands.setFillParent(true);
		
		ScrollPane pane3 = new ScrollPane(subcommands);
		
		add(pane3).height(getHeight() / 2).fillX();
		row();
		
		
		//--------------
		
		
		
		field = builder.getTextField("");
		field.addListener(new InputListener() {
			public boolean keyDown(InputEvent event, int keyCode) {
				if (keyCode == Input.Keys.ENTER) {
					String text = field.getText();
					vehicle.addQueue(text);
					display.append(text);
					field.setText("");
					return true;
				}
				else {
					return super.keyDown(event, keyCode);
				}
			}
		});
		add(field).colspan(4).fillX();
		//setBounds();
		//pack();
	}
	private class VehListener extends InputListener {
		private Vehicle personal;
		public VehListener(Vehicle v) {
			personal = v;
		}
		public boolean touchDown(InputEvent evt, float x, float y, int pointer, int button) {
			remove();
			screen.addPanel(new VehiclePanel(screen, personal, builder));
			return true;
		}
	}
	private class CommandListener extends InputListener {
		private String personal;
		public CommandListener(String c) {
			personal = c;
		}
		public boolean touchDown(InputEvent evt, float x, float y, int pointer, int button) {
			vehicle.addQueue(personal);
			return true;
		}
	}
	public boolean hasVehicle(Vehicle x) {
		return vehicle == x;
	}
	private class HPBar extends Label {
		private ShapeRenderer shapes;
		public HPBar(CustomButtonBuilder b) {
			super(vehicle.getHealth(), b.getSkin());
			shapes = new ShapeRenderer();
		}
		public void draw(SpriteBatch batch, float parentAlpha) {
			
			validate();
			
			batch.end();
			
			shapes.begin(ShapeType.Filled);
			shapes.setColor(Color.RED);
			shapes.rect(getX(), getY(), getWidth(), getHeight());
			shapes.setColor(Color.GREEN);
			shapes.rect(getX(), getY(), (float) (getWidth() * vehicle.getFractionHealth()), getHeight());
			shapes.end();
			
			
			batch.begin();
			float fontX = getX() + getWidth()/2 - getTextBounds().width/2;
			float fontY = getY() + getHeight()/2 + getTextBounds().height/2;
			
			this.getStyle().font.draw(batch, getText(), fontX, fontY);
		}
	}
	
}
