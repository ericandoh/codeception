package com.me.render;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.lights.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.lights.Lights;
import com.badlogic.gdx.graphics.g3d.materials.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.materials.Material;
import com.badlogic.gdx.graphics.g3d.materials.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.model.NodePart;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;
import com.badlogic.gdx.utils.Array;
import com.me.entities.Entity;
import com.me.entities.Player;
import com.me.entities.Vehicle;
import com.me.panels.GameScreen;
import com.me.panels.KeyBindingPanel;
import com.me.panels.VehiclePanel;
import com.me.terrain.GameGrid;
import com.me.terrain.Square;

/* Renderer.java
 * 
 * A Renderer object holds an array of objects to be rendered, an array of pictures, and other info needed to paint the objects
 * ( like position, etc )
 * 
 * This may also be modified to provide functionality for really low res 3D! :) or maybe porting to opengl, etc
 * 
 * 
 * 
 * ------
 * 
 * Try:
 * 
 * Canvas in both start + gameplaypanels, invisible + pause this renderer when start paneling
 * 
 */

//draw order:
//squares, entities, entity shadows, sprites, highlight rectangle

public class GameRenderer implements InputProcessor {

	public static final double BACKGROUND_MOVE_RATIO = 0.5;

	public static final float DEG_TURN = 5f; // 3 is normal
	public static final float STEP = 0.5f; // 0.08 is kinda slow
	public static final float MIN_HEIGHT = 0.03f;

	public static final float MED_SQUARE_VIEW_DST = 25f;
	public static int MED_CHUNK_RENDER_DST = 2;

	public static float SQUARE_VIEW_DST = MED_SQUARE_VIEW_DST / 2;
	public static int CHUNK_RENDER_DST = MED_CHUNK_RENDER_DST / 2;

	public static final int REGION_NUM = 16; // how many tiles per side in tile
												// texture

	public static final float SHADE_INCREMENT = 0.05f; // how much the light
														// changes each cycle
	public static float BRIGHTNESS = 1f; // transparency of lighting, should
											// always be 1f

	public static final float MIN_BRIGHTNESS = 0.15f;
	public static final float MAX_BRIGHTNESS = 0.6f;
	public static final float AMP_BRIGHTNESS = 2f * (MAX_BRIGHTNESS - MIN_BRIGHTNESS);
	public static final float DIR_BRIGHTNESS = 0.5f;

	private OrthographicCamera ortho;
	private PerspectiveCamera camera;

	public CameraInputController control;

	private boolean flatMode; // true if looking straight down
	private boolean hpMode, iconMode;

	// private Model model2;
	// private ModelInstance instance2;
	// private Array<ModelInstance> instances;

	private ModelBatch modelBatch;
	private SpriteBatch spriteBatch, bgBatch;

	private AssetManager assets;

	private Lights lights;
	private boolean loading;

	private Model simpleBox, shadowBox, hpBarModel, iconModel; // undefSquare;
	private Model[] spriteModels;
	private ModelBuilder builder;

	private Texture square, sprite; // , vehicle, deadVehicle;
	private TextureRegion[][] squares, sprites; // , vehicles, deadVehicles;

	private Texture backgroundTex, drawRectTex;
	private TiledDrawable background;
	// private Sprite background;
	// private Mesh[] backgroundMesh;
	private Matrix4 rotateMatrix;
	private Plane flat;

	private Array<Drawable> spriteDrawList, modelDrawList;
	// private Array<Drawable> spriteDrawList;

	// private int xlow, ylow, rectWidth, rectHeight;

	private Player myPlayer;
	private GameGrid map;
	private boolean close;

	private int width, height;

	private float firstX, firstY;
	private boolean dragging;
	private float secondX, secondY;

	private boolean[] inputKeys;

	private GameScreen screen;

	private float lastShadeTime;

	// private float currentShade;

	public GameRenderer(int w, int h, GameScreen s) {
		screen = s;
		close = false;
		// width = w;
		// height = h;
		width = Gdx.graphics.getWidth();
		height = Gdx.graphics.getHeight();
		inputKeys = new boolean[12];
		for (int i = 0; i < inputKeys.length; i++) {
			inputKeys[i] = false;
		}
		flat = new Plane(new Vector3(0, 1, 0), 0);
		create();
	}

	public void setPlayerAndMap(Player p, GameGrid g) {
		map = g;
		myPlayer = p;
		Vector3 pos = myPlayer.getPos();
		camera.position.set(pos);
		camera.update();
	}

	public void losePlayerAndMap() {
		map = null;
		myPlayer = null;
	}

	public int[] getChunkRenderCoords() {
		double xpos = myPlayer.getXPos() / GameGrid.SQUARE_LENGTH;
		double ypos = myPlayer.getYPos() / GameGrid.SQUARE_LENGTH;
		int[] num = new int[4];
		// int view = CHUNK_RENDER_DST;
		int x = (int) (xpos / GameGrid.CHUNK_SIZE);
		int y = (int) (ypos / GameGrid.CHUNK_SIZE);
		num[0] = x - CHUNK_RENDER_DST;
		num[1] = y - CHUNK_RENDER_DST;

		num[2] = x + CHUNK_RENDER_DST + 1;
		num[3] = y + CHUNK_RENDER_DST + 1;
		return num;
	}

	public void addToRender(Drawable d) {
		modelDrawList.add(d);
	}

	public void addToRenderSprite(Drawable d) {
		spriteDrawList.add(d);
	}

	public void reset() {
		spriteDrawList.clear();
		modelDrawList.clear();
	}

	public void create() {
		dragging = false;

		square = new Texture(Gdx.files.internal("graphics/square/tiles.png"));
		// squares = TextureRegion.split(square, 64, 64);

		squares = Square.formatRegions(TextureRegion.split(square, 64, 64));

		sprite = new Texture(Gdx.files.internal("graphics/sprites.png"));
		sprites = TextureRegion.split(sprite, 64, 64);

		/*
		 * vehicle = new Texture(Gdx.files.internal("graphics/chars.png"));
		 * vehicles = TextureRegion.split(vehicle, 64, 64);
		 * 
		 * deadVehicle = new
		 * Texture(Gdx.files.internal("graphics/deadchars.png")); deadVehicles =
		 * TextureRegion.split(deadVehicle, 64, 64);
		 */

		backgroundTex = new Texture(
				Gdx.files.internal("graphics/background.png"));
		backgroundTex.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);

		drawRectTex = new Texture(Gdx.files.internal("graphics/drawrect.png"));

		background = new TiledDrawable(new TextureRegion(backgroundTex));

		modelBatch = new ModelBatch();
		spriteBatch = new SpriteBatch();

		camera = new PerspectiveCamera(67, width, height);
		if (myPlayer != null) {
			// camera.position.set((float)myPlayer.getXPos(), 1f,
			// (float)myPlayer.getYPos());
			camera.position.set(myPlayer.getPos());
		} else
			camera.position.set(0.1f, 0.1f, 0.1f);
		camera.lookAt(camera.position.x + camera.position.y, 0,
				camera.position.z + camera.position.y);
		camera.near = 0.01f;
		camera.far = 200f;
		camera.update();

		flatMode = false;

		iconMode = true;
		hpMode = true;

		ortho = new OrthographicCamera();
		ortho.setToOrtho(false, width, height);

		control = new CameraInputController(camera);
		// Gdx.input.setInputProcessor(control);

		// viewPort = new Rectangle(0, 0, width, height);
		lastShadeTime = 0;

		lights = new Lights();
		// lights.ambientLight.set(0.4f, 0.4f, 0.4f, 1f); //ambient light 0.4,
		// 0.4, 0.4
		lights.ambientLight.set(lastShadeTime, lastShadeTime, lastShadeTime,
				BRIGHTNESS); // ambient light 0.4, 0.4, 0.4
		lights.add(new DirectionalLight().set(DIR_BRIGHTNESS, DIR_BRIGHTNESS, DIR_BRIGHTNESS, -1f, -0.8f,
				-0.2f)); // directional light for distincting shapes...?
		// lights.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f,
		// -0.2f)); //directional light, color 0.8, 0.8, 0.8, -1 -0.8 -0.2
		// direction
		// lights.add(new DirectionalLight().set(1f, 1f, 1f, 0f, -0.2f, -1f));
		// //directional light, color 0.8, 0.8, 0.8, -1 -0.8 -0.2 direction

		Matrix4 view = new Matrix4();
		view.setToOrtho2D(0, 0, width, height);
		spriteBatch.setProjectionMatrix(view);
		spriteBatch.setColor(lastShadeTime, lastShadeTime, lastShadeTime,
				BRIGHTNESS);

		rotateMatrix = new Matrix4();
		rotateMatrix.setToRotation(new Vector3(1, 0, 0), 90);

		bgBatch = new SpriteBatch();
		bgBatch.disableBlending();
		assets = new AssetManager();

		
		//assets.load("graphics/model/model0.obj", Model.class);
		//assets.load("graphics/model/model1.obj", Model.class);
		assets.load("graphics/model/model1.g3db", Model.class);
		assets.load("graphics/model/model2.g3db", Model.class);
		assets.load("graphics/model/model3.g3db", Model.class);
		assets.load("graphics/model/model4.g3db", Model.class);
		/*assets.load("graphics/model/model5.obj", Model.class);
		assets.load("graphics/model/model18.obj", Model.class);
		assets.load("graphics/model/model19.obj", Model.class);*/
		/**/

		// load square assets here
		// if (assets.isLoaded("graphics/square/sq"+id+".obj"))
		// assets.load("graphics/square/square1.obj", Model.class);

		assets.load("graphics/square/sq2v0.obj", Model.class);
		assets.load("graphics/square/sq2v1.obj", Model.class);
		assets.load("graphics/square/sq2v2.obj", Model.class);
		assets.load("graphics/square/sq3v0.obj", Model.class);
		assets.load("graphics/square/sq3v1.obj", Model.class);
		assets.load("graphics/square/sq3v2.obj", Model.class);
		assets.load("graphics/square/sq4v0.obj", Model.class);
		assets.load("graphics/square/sq5v0.obj", Model.class);
		assets.load("graphics/square/sq5v1.obj", Model.class);
		assets.load("graphics/square/sq5v2.obj", Model.class);
		assets.load("graphics/square/sq8v0.obj", Model.class);
		assets.load("graphics/square/sq8v1.obj", Model.class);

		// squareModels = new Model[9];
		spriteModels = new Model[0];

		/*
		 * for (int i = 0; i < VehicleHelper.NUM_VEHICLES; i++) {
		 * System.out.println("Loading model "+i);
		 * assets.load("graphics/model"+i+"/model"+i+".obj", Model.class); }
		 */
		builder = new ModelBuilder();
		simpleBox = builder.createBox(1f, 1f, 1f,
				new Material(ColorAttribute.createDiffuse(Color.GREEN)),
				Usage.Position | Usage.Normal);
		/*
		 * shadowBox = builder.createBox(1.1f, 1.1f, 1.1f, new Material(new
		 * ColorAttribute(ColorAttribute.Ambient, 100f, 100f, 100f, 0.5f)),
		 * Usage.Position | Usage.Normal);
		 */

		float hlf = 0.6f;
		float yOff = 0f;
		shadowBox = builder.createRect(-hlf, yOff, -hlf, -hlf, yOff, hlf, hlf,
				yOff, hlf, hlf, yOff, -hlf, 0, 1, 0, new Material(
						ColorAttribute.createDiffuse(Color.RED)),
				Usage.Position | Usage.Normal);

		hlf = 0.5f;
		yOff = 0.1f;
		hpBarModel = builder.createRect(-hlf, -yOff, 0, hlf, -yOff, 0, hlf,
				yOff, 0, -hlf, yOff, 0, 0, 0, 1,
				new Material(ColorAttribute.createDiffuse(Color.GREEN)),
				Usage.Position | Usage.Normal);

		iconModel = builder.createRect(-hlf, -yOff, 0, hlf, -yOff, 0, hlf,
				yOff, 0, -hlf, yOff, 0, 0, 0, 1,
				new Material(ColorAttribute.createDiffuse(Color.YELLOW)),
				Usage.Position | Usage.Normal);

		/*
		 * iconModel = builder.createSphere(0.3f, 0.3f, 0.3f, 5, 5, new
		 * Material(ColorAttribute.createDiffuse(Color.YELLOW)), Usage.Position
		 * | Usage.Normal);
		 */

		// shadowBox = (new
		// ObjLoader()).loadModel(Gdx.files.internal("graphics/square/square2.obj"),
		// true);

		// float hlf = (Square.BOUNDARY + 0.5f)*GameGrid.SQUARE_LENGTH;

		/*
		 * undefSquare = builder.createRect(-hlf, 0, -hlf, -hlf, 0, hlf, hlf, 0,
		 * hlf, hlf, 0, -hlf, 0, 1, 0, new
		 * Material(ColorAttribute.createDiffuse(Color.GREEN)), Usage.Position |
		 * Usage.Normal);
		 */
		// undefSquare = new Model();

		for (Node node : simpleBox.nodes) {
			node.translation.y += 0.5;
		}
		/*
		 * for (Node node: shadowBox.nodes) { node.translation.y += 0.5; }
		 */
		loading = true;

		modelDrawList = new Array<Drawable>();
		spriteDrawList = new Array<Drawable>();

		// Gdx.input.setInputProcessor(this);

	}

	/*
	 * private void doneLoading() { //Model ship =
	 * assets.get("data/ship/ship.obj", Model.class);
	 * 
	 * for (float x = -5f; x <= 5f; x += 2f) { for (float z = -5f; z <= 5f; z +=
	 * 2f) { ModelInstance shipInstance = new ModelInstance(ship);
	 * shipInstance.transform.setToTranslation(x, 0, z);
	 * instances.add(shipInstance); } }
	 * 
	 * 
	 * //ModelInstance shipInstance = new ModelInstance(ship);
	 * //instances.add(shipInstance); loading = false; }
	 */
	public Model getModSquares(int id, int v) {
		if (assets.isLoaded("graphics/square/sq" + id + "v" + v + ".obj")) {
			return assets.get("graphics/square/sq" + id + "v" + v + ".obj",
					Model.class);
		} else if (assets.isLoaded("graphics/square/sq" + id + "v0.obj")) {
			return assets.get("graphics/square/sq" + id + "v0.obj", Model.class);
		}
		return null;
	}

	public TextureRegion[][] getTexSquares() {
		/*
		 * if (squareModels[id] == null) { float hlf = SquaresDrawable.STRETCH /
		 * 2f; float yOff = 0f; squareModels[id] = builder.createRect(-hlf,
		 * yOff, -hlf, -hlf, yOff, hlf, hlf, yOff, hlf, hlf, yOff, -hlf, 0, 1,
		 * 0, new Material(new TextureAttribute(TextureAttribute.Diffuse,
		 * square)), Usage.Position | Usage.Normal | Usage.TextureCoordinates);
		 * 
		 * Matrix3 m1 = new Matrix3(); m1.setToScaling(1f / REGION_NUM, 1f /
		 * REGION_NUM); Matrix3 m2 = new Matrix3();
		 * m2.setToTranslation((float)(id % REGION_NUM) / REGION_NUM, (float)(id
		 * / REGION_NUM) / REGION_NUM);
		 * 
		 * squareModels[id].meshes.get(0).transformUV(m2.mul(m1)); }
		 */
		// return squareModels[id];
		return squares;
	}

	public Model getSprite(int id) {
		if (spriteModels[id] == null) {
			float hlf = 0.6f;
			float yOff = 0f;
			spriteModels[id] = builder.createRect(
					-hlf,
					yOff,
					-hlf,
					-hlf,
					yOff,
					hlf,
					hlf,
					yOff,
					hlf,
					hlf,
					yOff,
					-hlf,
					0,
					1,
					0,
					new Material(new TextureAttribute(TextureAttribute.Diffuse,
							sprites[id / REGION_NUM][id % REGION_NUM]
									.getTexture())), Usage.Position
							| Usage.Normal | Usage.TextureCoordinates);
		}
		return spriteModels[id];
	}

	public Model getVehicleModel(int id, boolean destroyed) {
		/*
		 * if (loading) return null;
		 */
		
		/*if(id==1) {
			return assets.get("graphics/model/model" + id + ".g3db", Model.class);
		}*/
			
		
		if (assets.isLoaded("graphics/model/model" + id + ".g3db"))
			return assets.get("graphics/model/model" + id + ".g3db", Model.class);
		return simpleBox;
		// return assets.get("graphics/model1/model1.obj", Model.class);
	}

	public Model getShadowModel(int id) {
		/*
		 * if (loading) return null;
		 */
		return shadowBox;
	}

	public Model getFaceModel(int id) {
		// id = 0: HP Bar
		// id = 1: Busy Icon (later replace with separate sprite sheet +
		// different icons depending on vehicleaction
		// for sprites, override FacePlayerDrawable + new method for returning
		// SpriteModels
		if (id == 0)
			return iconModel;
		return simpleBox;
	}

	public Model getHPModel() {
		return hpBarModel;
		// return simpleBox;
	}

	/*
	 * public void resize(int width, int height) { // this won't happen }
	 */
	// is the map square in range of camera for a high def model?
	public boolean inRangeCamera(float x, float y) {
		return Vector3.dst(x, 0, y, camera.position.x, 0, camera.position.z) <= SQUARE_VIEW_DST;
	}

	// is the map chunk in range of camera for a high def model?
	public boolean inRangeChunk(float x, float y, int chunkSize) {

		return Math.abs(camera.position.x - x) <= chunkSize / 2
				+ SQUARE_VIEW_DST
				&& Math.abs(camera.position.z - y) <= chunkSize / 2
						+ SQUARE_VIEW_DST;
	}

	public void draw() {

		float currentTime = myPlayer.getTime();
		if (currentTime > lastShadeTime + SHADE_INCREMENT
				|| currentTime <= SHADE_INCREMENT) {
			lastShadeTime = currentTime;
			float shadeValue = AMP_BRIGHTNESS * Math.abs(0.5f - lastShadeTime)
					+ MIN_BRIGHTNESS;
			lights.ambientLight.set(shadeValue, shadeValue, shadeValue,
					BRIGHTNESS);
			spriteBatch
					.setColor(shadeValue, shadeValue, shadeValue, BRIGHTNESS);
		}

		if (inputKeys[0]) {
			if (flatMode) {
				camera.translate(-STEP, 0, 0);
				camera.update();
				myPlayer.setPos(camera.position);
			} else {
				camera.rotateAround(camera.position, new Vector3(0, 1, 0),
						DEG_TURN);
				camera.update();
				myPlayer.setPos(camera.position);
			}
		} else if (inputKeys[1]) {
			if (flatMode) {
				camera.translate(STEP, 0, 0);
				camera.update();
				myPlayer.setPos(camera.position);
			} else {
				camera.rotateAround(camera.position, new Vector3(0, 1, 0),
						-DEG_TURN);
				camera.update();
				myPlayer.setPos(camera.position);
			}
		} else if (inputKeys[2]) {
			if (flatMode) {
				camera.translate(0, 0, -STEP);
				camera.update();
				myPlayer.setPos(camera.position);
			} else {
				camera.translate(camera.direction.x * STEP, 0,
						camera.direction.z * STEP);
				camera.update();
				myPlayer.setPos(camera.position);
			}
		} else if (inputKeys[3]) {
			if (flatMode) {
				camera.translate(0, 0, STEP);
				camera.update();
				myPlayer.setPos(camera.position);
			} else {
				camera.translate(-camera.direction.x * STEP, 0,
						-camera.direction.z * STEP);
				camera.update();
				myPlayer.setPos(camera.position);
			}
		} else if (inputKeys[4]) {
			/*
			 * camera.rotateAround(camera.position, new
			 * Vector3(-camera.direction.z, 0, camera.direction.x),
			 * (float)DEG_TURN); camera.update();
			 * myPlayer.setPos(camera.position);
			 */
		} else if (inputKeys[5]) {
			/*
			 * camera.rotateAround(camera.position, new
			 * Vector3(-camera.direction.z, 0, camera.direction.x),
			 * (float)-DEG_TURN); camera.update();
			 * myPlayer.setPos(camera.position);
			 */
		} else if (inputKeys[6]) {
			if (flatMode) {
				camera.translate(0, STEP, 0);
				if (camera.position.y < MIN_HEIGHT)
					camera.position.y = MIN_HEIGHT;
			} else {
				camera.translate(0, STEP, 0);
			}
			camera.update();
			myPlayer.setPos(camera.position);
		} else if (inputKeys[7]) {
			if (flatMode) {
				camera.translate(0, -STEP, 0);
			} else {
				camera.translate(0, -STEP, 0);
				if (camera.position.y < MIN_HEIGHT)
					camera.position.y = MIN_HEIGHT;
			}
			camera.update();
			myPlayer.setPos(camera.position);
		}
		myPlayer.handle(
				inputKeys,
				Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)
						|| Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT));
		// control.update();

		if (loading) {
			if (assets.update()) {
				loading = false;
				map.updateRender(this);
			}
		} else {
			map.updateRender(this);
		}

		Gdx.gl.glViewport(0, 0, width, height); // not needed????
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

		bgBatch.setProjectionMatrix(ortho.combined);
		bgBatch.begin();
		background
				.draw(bgBatch,
						(float) (-Math.PI
								+ (Math.atan2(camera.direction.x,
										camera.direction.z)) * width
								/ (2 * Math.PI) % width - width / 2),
						-(float) ((camera.direction.y * height + height) / 2
								% height - height / 2), width * 2, height * 2);
		bgBatch.end();

		spriteBatch.setProjectionMatrix(camera.combined);
		spriteBatch.setTransformMatrix(rotateMatrix);
		spriteBatch.begin();
		for (Drawable d : spriteDrawList) {
			d.paintSprite(this, spriteBatch, lights);
		}
		if (dragging) {
			spriteBatch.draw(drawRectTex, firstX, firstY, secondX - firstX,
					secondY - firstY);
		}
		spriteBatch.end();

		modelBatch.begin(camera);
		for (Drawable d : modelDrawList) {
			d.paintModel(this, modelBatch, lights);
		}
		modelBatch.end();

		spriteDrawList.clear();
		modelDrawList.clear();

		if (close) {
			System.out.println("Exiting");
			dispose();
		}
	}

	public void dispose() {
		if (modelBatch == null)
			return;
		modelBatch.dispose();
		modelBatch = null;
		spriteBatch.dispose();
		bgBatch.dispose();
		// instances.clear();
		modelDrawList.clear();
		// spriteDrawList.clear();
		assets.dispose();
		square.dispose();
		sprite.dispose();
		// vehicle.dispose();
		// deadVehicle.dispose();
		backgroundTex.dispose();
	}

	public void setClose(boolean t) {
		close = true;
	}

	private boolean setKeys(int keycode, boolean s) {
		if (keycode == Input.Keys.A) {
			inputKeys[0] = s;
			return true;
		} else if (keycode == Input.Keys.D) {
			inputKeys[1] = s;
			return true;
		} else if (keycode == Input.Keys.W) {
			inputKeys[2] = s;
			return true;
		} else if (keycode == Input.Keys.S) {
			inputKeys[3] = s;
			return true;
		} else if (keycode == Input.Keys.Q) {
			inputKeys[4] = s;
			return true;
		} else if (keycode == Input.Keys.Z) {
			inputKeys[5] = s;
			return true;
		} else if (keycode == Input.Keys.E) {
			inputKeys[6] = s;
			return true;
		} else if (keycode == Input.Keys.C) {
			inputKeys[7] = s;
			return true;
		} else if (keycode == Input.Keys.UP) {
			inputKeys[8] = s;
			return true;
		} else if (keycode == Input.Keys.DOWN) {
			inputKeys[9] = s;
			return true;
		} else if (keycode == Input.Keys.LEFT) {
			inputKeys[10] = s;
			return true;
		} else if (keycode == Input.Keys.RIGHT) {
			inputKeys[11] = s;
			return true;
		}
		return false;
	}

	@Override
	public boolean keyDown(int keycode) {
		if (keycode == Input.Keys.F5) {
			// change view
			if (flatMode) {
				// unflat mode this
				camera.position.y = 5f;
				camera.direction.x = 1f;
				camera.direction.y = 0f;
				camera.direction.z = 0f;
				camera.up.x = 0f;
				camera.up.y = 1f;
				camera.up.z = 0f;
				camera.lookAt(camera.position.x + camera.position.y, 0,
						camera.position.z + camera.position.y);
				camera.update();
				myPlayer.setPos(camera.position);
			} else {
				camera.position.y = 30f;
				camera.lookAt(camera.position.x, 0, camera.position.z);
				camera.up.x = 0f;
				camera.up.y = 0;
				camera.up.z = -1f;
				camera.update();
				myPlayer.setPos(camera.position);
			}
			flatMode = !flatMode;
		}
		return setKeys(keycode, true);
	}

	@Override
	public boolean keyUp(int keycode) {
		return setKeys(keycode, false);
	}

	@Override
	public boolean keyTyped(char character) {
		if (character == 'h') {
			/*
			 * Vehicle y = myPlayer.getAllVehicles().get(0);
			 * System.out.println("Vehicle coords, then entityt coords for "
			 * +y.identifier()); System.out.println(y.getXPos() + "/" +
			 * y.getYPos() + "/" + y.getZPos()); Entity x =
			 * myPlayer.getAllVehicles().get(0).getEntities().get(0);
			 * System.out.println(x.getXPos() + "/" + x.getYPos() + "/" +
			 * x.getZPos());
			 */
			// System.out.println("Help is on the wayyyyy but help neverrr  came");
			return true;
		}
		myPlayer.handle(character);
		return false;
	}

	/*
	 * private double convertMouseX(int mouseX) { mouseX -=
	 * MainGame.MOUSE_OFFSETX; //double screenWidthSquare =
	 * MapPanel.DEFAULT_WIDTH_SQUARE * myPlayer.getScale(); double xpos =
	 * myPlayer.getXPos()*screenWidthSquare - MapPanel.WIDTH/2; xpos = xpos /
	 * screenWidthSquare; return mouseX / screenWidthSquare + xpos; } private
	 * double convertMouseY(int mouseY) { mouseY -= MainGame.MOUSE_OFFSETY;
	 * //double screenHeightSquare = MapPanel.DEFAULT_HEIGHT_SQUARE *
	 * myPlayer.getScale(); double ypos = myPlayer.getYPos()*screenHeightSquare
	 * - MapPanel.HEIGHT/2; ypos = ypos / screenHeightSquare; return mouseY /
	 * screenHeightSquare + ypos; }
	 */
	private Vehicle getSelectVehicle(float x, float y) {

		Ray ray = camera.getPickRay(x, y);

		Vector3 vec = new Vector3();
		Vector3 intersection = new Vector3();

		double nearDist = camera.far + 1;
		Vehicle ret = null;
		ArrayList<Vehicle> vehs = map.getVehicleIgnoreHeight(camera.position.x,
				camera.position.z, camera.far);
		for (Vehicle veh : vehs) {
			for (Entity ent : veh.getEntities()) {
				vec.set((float) ent.getXPos(), (float) ent.getZPos(),
						(float) ent.getYPos());
				if (Intersector.intersectRaySphere(ray, vec, 1, intersection)) {
					if (Vector3.dst(camera.position.x, camera.position.y,
							camera.position.z, vec.x, vec.y, vec.z) < nearDist)
						ret = ent.getOwner().returnHighestOwner();
					break;
				}
			}
		}
		return ret;
	}

	private ArrayList<Vehicle> getSelectVehicle(float x1, float y1, float x2,
			float y2) {
		ArrayList<Vehicle> retVehs = new ArrayList<Vehicle>();
		float centerX = (x1 + x2) / 2f;
		float centerY = (y1 + y2) / 2f;
		float dst = (float) (Math.sqrt(Math.pow(x1 - x2, 2)
				+ Math.pow(y1 - y2, 2)) / 2f);
		ArrayList<Vehicle> vehs = map.getVehicleIgnoreHeight(centerX, centerY,
				dst);
		for (Vehicle veh : vehs) {
			for (Entity ent : veh.getEntities()) {
				if (ent.getXPos() >= x1 && ent.getXPos() <= x2
						&& ent.getYPos() >= y1 && ent.getYPos() <= y2) {
					retVehs.add(ent.getOwner().returnHighestOwner());
					break;
				}
			}
		}
		return retVehs;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return true;
	}

	private Vector3 getPlaneCoord(int screenX, int screenY) {
		Vector3 vec = new Vector3();
		Intersector.intersectRayPlane(camera.getPickRay(screenX, screenY),
				flat, vec);
		return vec;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		Vector3 coor = getPlaneCoord(screenX, screenY);
		if (dragging && button == Input.Buttons.LEFT) {
			dragging = false;
			float temp;
			if (firstX > secondX) {
				temp = firstX;
				firstX = secondX;
				secondX = temp;
			}
			if (firstY > secondY) {
				temp = firstY;
				firstY = secondY;
				secondY = temp;
			}
			// find all vehicles from firstX, firstY to secondX, secondY
			ArrayList<Vehicle> vehs = getSelectVehicle(firstX, firstY, secondX,
					secondY);
			if (!Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
				myPlayer.resetPuppet();
			}
			for (Vehicle v : vehs) {
				myPlayer.setPuppet(v);
			}

		} else {
			// Vector3 coor = getPlaneCoord(screenX, screenY);
			int x = Math.round(coor.x);
			int y = Math.round(coor.z);
			if (Vector3.dst(coor.x, coor.y, coor.z, camera.position.x,
					camera.position.y, camera.position.z) > 200) {
				x = Math.round(camera.position.x);
				y = Math.round(camera.position.z);
			}
			// int z = Math.round(coor.y);
			Vehicle v = getSelectVehicle(screenX, screenY);
			if (button == Input.Buttons.RIGHT) {
				if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) && v != null
						&& v.getPlayer() == myPlayer) {
					// double click
					// open vehicle window
					screen.addPanel(new VehiclePanel(screen, v, screen
							.getBuilder()));
				} else if (myPlayer.hasPuppet()) {
					String coord = x + "," + y;
					myPlayer.clickMove(coord);
				}
				return true;
			} else if (button == Input.Buttons.LEFT) {
				if (v == null) {
					Square sq = map.getSquare(x, y);
					if (sq == null)
						myPlayer.addText("The Vast Emptiness of Space and Beyond"
								+ " at (" + x + ", " + y + ")");
					else
						myPlayer.addText(sq.toString() + " at (" + x + ", " + y
								+ ")");
					if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
						// open player key binding window
						screen.addPanel(new KeyBindingPanel(screen, myPlayer,
								screen.getBuilder()));
					} else {
						myPlayer.resetPuppet();
					}
				} else {
					if (v.getPlayer() == myPlayer) {
						myPlayer.addText(v.toString() + " at ("
								+ Math.round(v.getXPos()) + ", "
								+ Math.round(v.getYPos()) + ")");
						if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
							// open key bindings
							screen.addPanel(new KeyBindingPanel(screen, v,
									screen.getBuilder()));
						} else if (Gdx.input
								.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
							myPlayer.setPuppet(v.returnHighestOwner());
						} else {
							// System.out.println("(GameRenderer)Setting puppet");
							myPlayer.resetPuppet();
							myPlayer.setPuppet(v.returnHighestOwner());
						}
					} else {
						myPlayer.addText("Enemy structure at ("
								+ Math.round(v.getXPos()) + ", "
								+ Math.round(v.getYPos()) + ")");
					}
				}
			}
		}
		return true;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
			if (!dragging) {
				Vector3 coor = getPlaneCoord(screenX, screenY);
				firstX = coor.x;
				firstY = coor.z;
				dragging = true;
			}
			Vector3 coor = getPlaneCoord(screenX, screenY);
			secondX = coor.x;
			secondY = coor.z;
			/*
			 * float temp; if (firstX > secondX) { temp = firstX; firstX =
			 * secondX; secondX = temp; } if (firstY > secondY) { temp = firstY;
			 * firstY = secondY; secondY = temp; }
			 */
		}
		return true;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		/*
		 * if (amount < 0) { myPlayer.increaseSize(); } else {
		 * myPlayer.decreaseSize(); } return true;
		 */
		return false;
	}

	public PerspectiveCamera getCamera() {
		return camera;
	}

	public boolean getFlatMode() {
		return flatMode;
	}

	public boolean getHPMode() {
		return hpMode;
	}

	public boolean getIconMode() {
		return iconMode;
	}

	public void setHPMode(boolean x) {
		hpMode = x;
	}

	public void setIconMode(boolean x) {
		iconMode = x;
	}

	public boolean matchPlayer(Player p) {
		return p == myPlayer;
	}
}