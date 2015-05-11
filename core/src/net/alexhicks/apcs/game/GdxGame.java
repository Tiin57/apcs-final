package net.alexhicks.apcs.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import java.util.ArrayList;
import java.util.List;

public class GdxGame extends ApplicationAdapter implements ApplicationListener {

	// Do not use ArrayList or HashMap, use Array<> or other GDX classes
	// Garbage collection makes life better.
	public static final boolean DEBUG = false;
	private static final int MOVEMENT_CONSTANT = 4;
	private static final int DOT_TIME = 100000 * 10000 * 2;
	public static List<TimeCoord> coords = new ArrayList<TimeCoord>();
	private OrthographicCamera camera;
	private SpriteBatch batch;
	private Texture playerTexture, dotTexture, trailTexture;
	private AccelRectangle player;
	private Array<AccelRectangle> dots;
	private BitmapFont font;
	private long lastDotTime;
	private int score = 0;
	private long startTime;
	private List<Float[]> trail;

	@Override
	public void create() {
		this.startTime = TimeUtils.millis();
		this.batch = new SpriteBatch();
		this.playerTexture = new Texture(Gdx.files.internal(Textures.PLAYER.getLocation()));
		this.dotTexture = new Texture(Gdx.files.internal(Textures.DOT.getLocation()));
		this.trailTexture = new Texture(Gdx.files.internal(Textures.TRAIL.getLocation()));
		this.camera = new OrthographicCamera();
		this.dots = new Array<AccelRectangle>();
		this.trail = new ArrayList<Float[]>();
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(Textures.OPENSANS.getLocation()));
		FreeTypeFontParameter parameter = new FreeTypeFontParameter();
		parameter.size = 16;
		parameter.color = Color.BLACK;
		this.font = generator.generateFont(parameter);
		generator.dispose();
		camera.setToOrtho(false, 800, 480);
		this.player = new AccelRectangle();
		player.width = playerTexture.getWidth();
		player.height = playerTexture.getHeight();
		player.x = (camera.viewportWidth / 2) - (player.width / 2);
		player.y = (camera.viewportHeight / 2) - (player.height / 2);
		spawnDot();
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(1, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.draw(playerTexture, player.x, player.y);
		for (Rectangle dot : dots) {
			batch.draw(dotTexture, dot.x, dot.y);
		}
		for (Float[] f : trail) {
			batch.draw(trailTexture, f[0], f[1]);
		}
		font.draw(batch, "Score: " + score, 30, 30);
		if (DEBUG) {
			font.draw(batch, "Vertical: " + player.verticalAcceleration, 30, 60);
			font.draw(batch, "Horizontal: " + player.horizontalAcceleration, 30, 90);
		}
		batch.end();
		update();
		checkBoundaries(player);
		/*
		for (Rectangle dot : dots) {
			checkBoundaries(dot);
		}
		*/
	}

	private void update() {
		player.decelerate();
		if (Gdx.input.isKeyPressed(Keys.LEFT) || Gdx.input.isKeyPressed(Keys.A)) {
			player.accelerate(Direction.LEFT);
		}
		if (Gdx.input.isKeyPressed(Keys.RIGHT) || Gdx.input.isKeyPressed(Keys.D)) {
			player.accelerate(Direction.RIGHT);
		}
		if (Gdx.input.isKeyPressed(Keys.UP) || Gdx.input.isKeyPressed(Keys.W)) {
			player.accelerate(Direction.UP);
		}
		if (Gdx.input.isKeyPressed(Keys.DOWN) || Gdx.input.isKeyPressed(Keys.S)) {
			player.accelerate(Direction.DOWN);
		}
		player.move();
		coords.add(new TimeCoord(player.x, player.y, TimeUtils.timeSinceMillis(startTime)));
		if (TimeUtils.nanoTime() - lastDotTime > DOT_TIME) {
			spawnDot();
		}
		if (trail.size() == 50) {
			trail.remove(0);
		}
		trail.add(new Float[] {
			player.x + (player.width / 2) - (trailTexture.getWidth() / 2),
			player.y + (player.height / 2) - (trailTexture.getHeight() / 2)
		});
		for (int i = 0; i < dots.size; i++) {
			Rectangle dot = dots.get(i);
			/*
			dot.y += MathUtils.randomSign() * MOVEMENT_CONSTANT;
			dot.x += MathUtils.randomSign() * MOVEMENT_CONSTANT;
			*/
			if (dot.overlaps(player)) {
				dots.removeIndex(i);
				score++;
			}
		}
	}

	private void checkBoundaries(AccelRectangle rect) {
		if (rect.x < 0) {
			rect.x = 0;
			if (rect.horizontalAcceleration < 0) {
				rect.horizontalAcceleration = 0;
			}
		} else if (rect.x > camera.viewportWidth - rect.width) {
			rect.x = camera.viewportWidth - rect.width;
			if (rect.horizontalAcceleration > 0) {
				rect.horizontalAcceleration = 0;
			}
		}
		if (rect.y < 0) {
			rect.y = 0;
			if (rect.verticalAcceleration < 0) {
				rect.verticalAcceleration = 0;
			}
		} else if (rect.y > camera.viewportHeight - rect.height) {
			rect.y = camera.viewportHeight - rect.height;
			if (rect.verticalAcceleration > 0) {
				rect.verticalAcceleration = 0;
			}
		}
	}

	private void spawnDot() {
		AccelRectangle dot = new AccelRectangle();
		dot.width = playerTexture.getWidth();
		dot.height = playerTexture.getHeight();
		dot.x = MathUtils.random(dot.width, camera.viewportWidth - dot.width);
		dot.y = MathUtils.random(dot.height, camera.viewportHeight - dot.height);
		dots.add(dot);
		lastDotTime = TimeUtils.nanoTime();
	}
}
