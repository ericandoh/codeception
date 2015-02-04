package com.me.panels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.me.codeception.CodeGame;
import com.me.entities.Player;
import com.me.entities.Vehicle;
import com.me.render.GameRenderer;
import com.me.terrain.GameGrid;


public class GameScreen implements Screen {
	public static final String NAME = "GAME";
	
	public static final String CONT = "CONT";
	
	public static final String NEW = "new_";
	public static final String SAND = "sand";
	public static final String BASE_CAMP = "bamp";
	public static final String USER_CAMP = "uamp";
	//public static final String CAMP = "camp";
	
	protected CodeGame game;
	
	private Stage stage;
	
	private CustomButtonBuilder buttonBuilder;
	
	private CustomScrollableLabel display;
	private TextField field;
	private RadarPanel radar;
	
	//private boolean up, down, left, right;
	private boolean[] arrows;
	
	protected Player myPlayer;
	private GameGrid myGrid;
	
	protected GameRenderer renderer;
	
	//private ArrayList<IngamePanel> activeWindows;
	
	private InputMultiplexer plexer;
	
	private boolean entered;
	
	public GameScreen(CodeGame g) {
		game = g;
		arrows = new boolean[4];
		arrows[0] = false;
		arrows[1] = false;
		arrows[2] = false;
		arrows[3] = false;
		entered = false;
		create();
	}
	public void setPlayerAndMap(GameGrid x, int select) {
		System.out.println("Setting player + map");
		myGrid = x;
		myPlayer = myGrid.getPlayer(select);
		myPlayer.setGameScreen(this);
		
		int width = Gdx.graphics.getWidth();
		int height = Gdx.graphics.getHeight();
		int mapHeight = height * 4 / 5;
		int bottomBarHeight = height - mapHeight;
		
		//radar = new RadarPanel(myPlayer, width, height);
		radar = new RadarPanel(myPlayer, myGrid, renderer.getCamera(), renderer);
		radar.setBounds(0, 0, width/10, bottomBarHeight);
		
		stage.addActor(radar);
		
		//activeWindows = new ArrayList<IngamePanel>();
		
		//JPanel playerStat = myPlayer.controller();
		//playerStat.setBackground(Color.yellow);
		//playerStat.setBounds(MapPanel.WIDTH*1/10, 0, MapPanel.WIDTH*2/10, bottomBarHeight);
		//stage.addActor(playerStat);
		renderer.setPlayerAndMap(myPlayer, x);
		game.addScreen(EditPanel.NAME, new EditPanel(game, getType(), myPlayer));
	}
	public void losePlayerAndMap() {
		//sets up variables for garbage collection
		System.out.println("Losing player and map :(");
		myGrid = null;
		myPlayer = null;
		radar.remove();
		renderer.losePlayerAndMap();
		game.removeScreen(EditPanel.NAME);
	}
	public void create() {
		int width = Gdx.graphics.getWidth();
		int height = Gdx.graphics.getHeight();
		int mapHeight = height * 4 / 5;
		//load textures
		
		//img = new Texture(Gdx.files.internal("pic.png"));
		
		stage = new Stage();
		//add actors
		buttonBuilder = new CustomButtonBuilder(game);
		
		
		//map = new MapPanel(myGrid, myPlayer, this);
		//map.setBounds(0, 0, MapPanel.WIDTH, MapPanel.HEIGHT);
		
		
		int bottomBarHeight = height - mapHeight;
		
		
		
		
		Group bottomCommandBar = new Group();
		bottomCommandBar.setBounds(width*3/10, 0, width*4/10, bottomBarHeight);
		
		display = buttonBuilder.getScrollableLabel("Welcome back!");
		//display.setBounds(0, 0, bottomCommandBar.getWidth(), bottomCommandBar.getHeight() * 4 / 5);
		
		ScrollPane scrollPane2 = new ScrollPane(display, game.getSkin());
		scrollPane2.setBounds(0, bottomBarHeight / 5, bottomCommandBar.getWidth(), bottomBarHeight * 4 / 5);
		
		display.setPane(scrollPane2);
		//scrollPane2.setWidget(display);
		
		field = buttonBuilder.getTextField("");
		field.setBounds(0, 0, bottomCommandBar.getWidth(), bottomBarHeight / 5);
		field.addListener(new InputListener() {
			public boolean keyDown(InputEvent event, int keyCode) {
				if (keyCode == Input.Keys.ENTER) {
					String text = field.getText();
					//System.out.println("appending:"+text+":end");
					if (text.length() == 0) {
						//System.out.println("Lost focus");
						entered = true;
						stage.setKeyboardFocus(null);
					}
					else {
						//System.out.println("(GameScreen)Appending text: "+text);
						display.append(text);
						field.setText("");
						//stage.setKeyboardFocus(null);
					}
					myPlayer.addQueueText(text);
					return true;
				}
				else {
					return super.keyDown(event, keyCode);
				}
			}
		});
		
		
		bottomCommandBar.addActor(scrollPane2);
		bottomCommandBar.addActor(field);
		
		
		//buttons
		
		Group buttons = new Group();
		buttons.setBounds(width*7/10, 0, width*3/10, bottomBarHeight);
		
		int buttonWidth = (width * 2 / 10) / 3;
		
		//buttons will be: edit/write, view entity by list, items in storage, quests, accomplishments, and special skills
		
		Actor edit = buttonBuilder.getButton("EDIT", EditPanel.NAME, null);
		edit.setBounds(0, bottomBarHeight / 2, buttonWidth, bottomBarHeight / 2);
		
		Actor view = buttonBuilder.getButton("VIEW", null, null);
		view.addListener(new InputListener() {
			public boolean touchDown(InputEvent evt, float x, float y, int pointer, int button) {
				addViewWindow();
				return true;
			}
		});
		view.setBounds(buttonWidth, bottomBarHeight / 2, buttonWidth, bottomBarHeight / 2);
		
		Actor items = buttonBuilder.getButton("ITEMS", null, null);
		items.addListener(new InputListener() {
			public boolean touchDown(InputEvent evt, float x, float y, int pointer, int button) {
				addItemsWindow();
				return true;
			}
		});
		items.setBounds(buttonWidth * 2, bottomBarHeight / 2, buttonWidth, bottomBarHeight / 2);
		
		
		Actor quests = buttonBuilder.getButton("QUESTS", null, null);
		quests.setBounds(0, 0, buttonWidth, bottomBarHeight / 2);
		
		Actor keybindings = buttonBuilder.getButton("KEYS", null, null);
		keybindings.addListener(new InputListener() {
			public boolean touchDown(InputEvent evt, float x, float y, int pointer, int button) {
				addKeyBindingWindow();
				return true;
			}
		});
		keybindings.setBounds(buttonWidth, 0, buttonWidth, bottomBarHeight / 2);
		
		Actor skills = buttonBuilder.getButton("STATS", null, null);
		skills.addListener(new InputListener() {
			public boolean touchDown(InputEvent evt, float x, float y, int pointer, int button) {
				addStatsWindow();
				return true;
			}
		});
		skills.setBounds(buttonWidth * 2, 0, buttonWidth, bottomBarHeight / 2);
		
		buttons.addActor(edit);
		buttons.addActor(view);
		buttons.addActor(items);
		buttons.addActor(quests);
		buttons.addActor(keybindings);
		
		
		
		
		buttons.addActor(skills);
		
		Actor menuButton = buttonBuilder.getButton("MENU", null, null);
		menuButton.addListener(new InputListener() {
			public boolean touchDown(InputEvent evt, float x, float y, int pointer, int button) {
				addMenuWindow();
				return true;
			}
		});
		menuButton.setBounds(width * 9 / 10, 0, width / 10, bottomBarHeight);
		
		buttons.addActor(menuButton);
		//menuButton.addActionListener(new MenuListener(this));
		
		stage.addActor(bottomCommandBar);
		stage.addActor(buttons);
		stage.addActor(menuButton);
		
		stage.addListener(new InputListener() {
			public boolean keyDown(InputEvent event, int keycode) {
				if (keycode == Input.Keys.ENTER && stage.getKeyboardFocus() == null && !entered) {
					stage.setKeyboardFocus(field);
					return true;
				}
				if (entered)
					entered = false;
				return false;
			}
		});
		
		
		//Image a = new Image(tex);
		//a.setBounds(0, 0, width, height);
		//
		//Actor b = buttonBuilder.getButton("Button Name", SelectionScreen.NAME, SelectionScreen.SINGLE);
		//b.setBounds(width * 2 / 3, height * 1 / 4 + height * 3 / 16, width / 4, height / 16);
		//stage.addActor(a);
		//stage.addActor(b);
		
		addRenderer(width, mapHeight);
		
		plexer = new InputMultiplexer();
		//plexer.addProcessor(renderer.control);
		plexer.addProcessor(stage);
		plexer.addProcessor(renderer);
		
		Gdx.input.setInputProcessor(plexer);
		//stage.addListener(new ArrowListener());	
	}
	//override this method for the level design
	public void addRenderer(int width, int mapHeight) {
		renderer = new GameRenderer(width, mapHeight, this);
	}
	public void addKeyBindingWindow() {
		addPanel(new KeyBindingPanel(this, myPlayer, buttonBuilder));
	}
	public void addViewWindow() {
		addPanel(new ViewVehWindow(this));
	}
	public void addVehicleWindow(Vehicle v) {
		addPanel(new VehiclePanel(this, v, buttonBuilder));
	}
	private class ViewVehWindow extends IngamePanel {
		public ViewVehWindow(GameScreen e) {
			super(e, buttonBuilder.getSkin());
			add(buttonBuilder.getLabel("Entities")).width(getWidth()).fillX();
			row();
			
			Table listEntities = new Table();
			Actor a;
			for (Vehicle v : myPlayer.getAllVehicles()) {
				a = buttonBuilder.getButton(v.getName(), null, null);
				a.addListener(new VehicleOpener(this, v));
				listEntities.add(a).fillX();
				listEntities.row();
			}
			ScrollPane scrollPane = new ScrollPane(listEntities);
			add(scrollPane).fillX();
			row();
			
			Actor exit = buttonBuilder.getButton("EXIT", null, null);
			exit.addListener(new InputListener() {
				public boolean touchDown(InputEvent evt, float x, float y, int pointer, int button) {
					remove();
					return true;
				}
			});
			add(exit).fillX();
		}
		public boolean hasVehicle(Vehicle x) {
			return true;
		}
		private class VehicleOpener extends InputListener {
			private Vehicle vehicle;
			private Actor parent;
			public VehicleOpener(Actor p, Vehicle v) {
				parent = p;
				vehicle = v;
			}
			public boolean touchDown(InputEvent evt, float x, float y, int pointer, int button) {
				parent.remove();
				addVehicleWindow(vehicle);
				return true;
			}
		}
	}
	public void addItemsWindow() {
		addPanel(new ViewItemWindow(this));
	}
	private class ViewItemWindow extends IngamePanel {
		public ViewItemWindow(GameScreen e) {
			super(e, buttonBuilder.getSkin());
			add(buttonBuilder.getLabel("Items")).width(getWidth()).fillX();
			row();
			
			Table listEntities = new Table();
			Actor a;
			for (String item : myPlayer.getListItem()) {
				a = buttonBuilder.getLabel(item);
				
				listEntities.add(a).fillX();
				listEntities.row();
			}
			ScrollPane scrollPane = new ScrollPane(listEntities);
			add(scrollPane).fillX();
			row();
			
			Actor exit = buttonBuilder.getButton("EXIT", null, null);
			exit.addListener(new InputListener() {
				public boolean touchDown(InputEvent evt, float x, float y, int pointer, int button) {
					remove();
					return true;
				}
			});
			add(exit).fillX();
		}
	}
	public void addMenuWindow() {
		addPanel(new MenuWindow(this));
	}
	private class MenuWindow extends IngamePanel {
		public MenuWindow(GameScreen e) {
			super(e, buttonBuilder.getSkin());
			
			Actor resume = buttonBuilder.getButton("RESUME", null, null);
			resume.addListener(new InputListener() {
				public boolean touchDown(InputEvent evt, float x, float y, int pointer, int button) {
					remove();
					return true;
				}
			});
			add(resume).width(getWidth()).fillX();
			row();
			
			Actor save = buttonBuilder.getButton("SAVE", null, null);
			save.addListener(new InputListener() {
				public boolean touchDown(InputEvent evt, float x, float y, int pointer, int button) {
					saveGame();
					remove();
					return true;
				}
			});
			add(save).fillX();
			row();
			
			Actor settings = buttonBuilder.getButton("SETTINGS", null, null);
			settings.addListener(new InputListener() {
				public boolean touchDown(InputEvent evt, float x, float y, int pointer, int button) {
					showSettings();
					remove();
					return true;
				}
			});
			add(settings).fillX();
			row();
			
			Actor quit = buttonBuilder.getButton("QUIT", null, null);
			quit.addListener(new InputListener() {
				public boolean touchDown(InputEvent evt, float x, float y, int pointer, int button) {
					reallyQuit();
					remove();
					return true;
				}
			});
			add(quit).fillX();
		}
	}
	public void addStatsWindow() {
		addPanel(new VehiclePanel(this, myPlayer.getNullVehicle(), buttonBuilder));
	}
	public void saveGame() {
		myGrid.pauseGame();
		myGrid.saveFile();
		myGrid.resumeGame();
	}
	public void quitGame() {
		game.switchScreen(StartScreen.NAME, null);
	}
	public void reallyQuit() {
		addPanel(new ReallyQuitWindow(this));
	}
	private class ReallyQuitWindow extends IngamePanel {
		public ReallyQuitWindow(GameScreen e) {
			super(e, buttonBuilder.getSkin());
			
			Actor msg = buttonBuilder.getLabel("Save before Quit?");
			add(msg).width(getWidth()).fillX();
			row();
			
			Actor yes = buttonBuilder.getButton("Yes", null, null);
			yes.addListener(new InputListener() {
				public boolean touchDown(InputEvent evt, float x, float y, int pointer, int button) {
					saveGame();
					remove();
					quitGame();
					return true;
				}
			});
			add(yes).fillX();
			row();
			
			Actor no = buttonBuilder.getButton("No", null, null);
			no.addListener(new InputListener() {
				public boolean touchDown(InputEvent evt, float x, float y, int pointer, int button) {
					remove();
					quitGame();
					return true;
				}
			});
			add(no).fillX();
			row();
			
			Actor cancel = buttonBuilder.getButton("Cancel", null, null);
			cancel.addListener(new InputListener() {
				public boolean touchDown(InputEvent evt, float x, float y, int pointer, int button) {
					remove();
					return true;
				}
			});
			add(cancel).fillX();
		}
	}
	public void showSettings() {
		addPanel(new SettingsWindow(this));
	}
	private class SettingsWindow extends IngamePanel {
		private final String[] SELECT_OPTIONS = {"None", "HP Bars Only", "Busy Icon Only", "Both HP Bar and Busy Icon"};
		private Slider slider;
		private SelectBox select;
		public SettingsWindow(GameScreen e) {
			super(e, buttonBuilder.getSkin());
			
			//Slider for graphic settings
			//Render HP Bar/In Work - SelectBox (drop down list)
			
			Actor msg = buttonBuilder.getLabel("Render Distances");
			add(msg).width(getWidth()).fillX();
			row();
			
			slider = new Slider(0f, 10f, 1f, false, buttonBuilder.getSkin());
			float val = GameRenderer.SQUARE_VIEW_DST / GameRenderer.MED_SQUARE_VIEW_DST * 5;
			val = val < 0 ? 0 : val;
			val = val > 10f ? 10f : val;
			slider.setValue(val);
			add(slider).fillX();
			row();
			
			Actor msg2 = buttonBuilder.getLabel("Entity Info Options");
			add(msg2).width(getWidth()).fillX();
			row();
			
			//String[] selectOptions = {"None", "HP Bars Only", "Busy Icon Only", "Both HP Bar and Busy Icon"};
			int current = (renderer.getIconMode() ? 1 : 0) * 2 + (renderer.getHPMode() ? 1 : 0);
			select = new SelectBox(SELECT_OPTIONS, buttonBuilder.getSkin());
			select.setSelection(current);
			add(select).width(getWidth()).fillX();
			row();
			
			Actor exit = buttonBuilder.getButton("Close", null, null);
			exit.addListener(new InputListener() {
				public boolean touchDown(InputEvent evt, float x, float y, int pointer, int button) {
					remove();
					return true;
				}
			});
			add(exit).fillX();
		}
		@Override
		public boolean remove() {
			float render = slider.getValue();
			
			GameRenderer.SQUARE_VIEW_DST = (render / 5) * GameRenderer.MED_SQUARE_VIEW_DST;
			GameRenderer.CHUNK_RENDER_DST = Math.max((int)((render / 5) * GameRenderer.MED_CHUNK_RENDER_DST), 0);
			
			int option = select.getSelectionIndex();
			if (option < 2)
				renderer.setIconMode(false);
			else
				renderer.setIconMode(true);
			
			if (option % 2 == 0)
				renderer.setHPMode(false);
			else
				renderer.setHPMode(true);
			
			return super.remove();
		}
	}
	@Override
	public void render(float delta) {
		// TODO Auto-generated method stub
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		stage.act(Gdx.graphics.getDeltaTime());
		
		renderer.draw();
		
		stage.draw();
		
		radar.draw();
		
		
		
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		stage.setViewport(width, height, false);
	}
	@Override
	public void dispose() {
		stage.dispose();
	}
	@Override
	public void show() {
		// TODO Auto-generated method stub
		Gdx.input.setInputProcessor(plexer);
		if (myGrid != null)
			myGrid.resumeGame();
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		if (myGrid != null)
			myGrid.pauseGame();
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		if (myGrid != null)
			myGrid.pauseGame();
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		if (myGrid != null)
			myGrid.resumeGame();
	}
	public void addText(String line) {
		display.append(line);
	}
	public void informRemoveVehicle(Vehicle x) {
		for (Actor child : stage.getActors()) {
			if (child instanceof IngamePanel) {
				if (((IngamePanel)child).hasVehicle(x)) {
					child.remove();
					return;
				}
			}
		}
		//myGrid.removeVehicle(x);
		//WORK NEED: dispose all other jframes that are open that are invovled with this entity
	}
	public void addPanel(IngamePanel a) {
		/*
		int width = Gdx.graphics.getWidth();
		int height = Gdx.graphics.getHeight();
		Group parent = new Group();
		parent.setBounds(width / 3, height / 3, width / 3, height / 3);
		
		a.setParentActor(parent);
		
		parent.addActor(a);
		*/
		stage.addActor(a);
	}
	public boolean[] getArrows() {
		return arrows;
	}
	public CustomButtonBuilder getBuilder() {
		return buttonBuilder;
	}
	public String getType() {
		return GameScreen.NAME;
	}
	/*
	private class ArrowListener extends InputListener {
		public boolean keyDown(InputEvent event, int keycode) {
			if (keycode == Input.Keys.UP) {
				arrows[0] = true;
				return true;
			}
			else if (keycode == Input.Keys.DOWN) {
				arrows[1] = true;
				return true;
			}
			else if (keycode == Input.Keys.LEFT) {
				arrows[2] = true;
				return true;
			}
			else if (keycode == Input.Keys.RIGHT) {
				arrows[3] = true;
				return true;
			}
			return false;
		}
		public boolean keyUp(InputEvent event, int keycode) {
			if (keycode == Input.Keys.UP) {
				arrows[0] = false;
				return true;
			}
			else if (keycode == Input.Keys.DOWN) {
				arrows[1] = false;
				return true;
			}
			else if (keycode == Input.Keys.LEFT) {
				arrows[2] = false;
				return true;
			}
			else if (keycode == Input.Keys.RIGHT) {
				arrows[3] = false;
				return true;
			}
			return false;
		}
		//public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
			//System.out.println("TOUCHDOWN");
			//return false;
		//}
	}*/
}
