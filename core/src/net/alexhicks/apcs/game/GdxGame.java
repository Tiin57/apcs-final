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

public class GdxGame extends ApplicationAdapter implements ApplicationListener {

	// Do not use ArrayList or HashMap, use Array<> or other GDX classes
	// Garbage collection makes life better.
	private static final int MOVEMENT_CONSTANT = 4;
	private OrthographicCamera camera;
	private SpriteBatch batch;
	private Texture playerTexture, dotTexture;
	private Rectangle player;
	private Array<Rectangle> dots;
	private BitmapFont font;
	private long lastDotTime;
	private int score = 0;

	@Override
	public void create() {
		this.batch = new SpriteBatch();
		this.playerTexture = new Texture(Gdx.files.internal(Textures.PLAYER.getLocation()));
		this.dotTexture = new Texture(Gdx.files.internal(Textures.DOT.getLocation()));
		this.camera = new OrthographicCamera();
		this.dots = new Array<Rectangle>();
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(Textures.FONT.getLocation()));
		FreeTypeFontParameter parameter = new FreeTypeFontParameter();
		parameter.size = 16;
		parameter.color = Color.BLACK;
		this.font = generator.generateFont(parameter);
		generator.dispose();
		camera.setToOrtho(false, 800, 480);
		this.player = new Rectangle();
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
		font.draw(batch, "Score: " + score, 30, 30);
		batch.end();
		update();
		checkBoundaries(player);
		for (Rectangle dot : dots) {
			checkBoundaries(dot);
		}
	}

	private void update() {
		if (Gdx.input.isKeyPressed(Keys.LEFT) || Gdx.input.isKeyPressed(Keys.A)) {
			player.x -= MOVEMENT_CONSTANT;
		}
		if (Gdx.input.isKeyPressed(Keys.RIGHT) || Gdx.input.isKeyPressed(Keys.D)) {
			player.x += MOVEMENT_CONSTANT;
		}
		if (Gdx.input.isKeyPressed(Keys.UP) || Gdx.input.isKeyPressed(Keys.W)) {
			player.y += MOVEMENT_CONSTANT;
		}
		if (Gdx.input.isKeyPressed(Keys.DOWN) || Gdx.input.isKeyPressed(Keys.S)) {
			player.y -= MOVEMENT_CONSTANT;
		}
		if (TimeUtils.nanoTime() - lastDotTime > 100000 * 10000) {
			spawnDot();
		}
		for (int i = 0; i < dots.size; i++) {
			Rectangle dot = dots.get(i);
			dot.y += MathUtils.randomSign() * MOVEMENT_CONSTANT;
			dot.x += MathUtils.randomSign() * MOVEMENT_CONSTANT;
			if (dot.overlaps(player)) {
				dots.removeIndex(i);
				score++;
			}
		}
	}

	private void checkBoundaries(Rectangle rect) {
		if (rect.x < 0) {
			rect.x = 0;
		} else if (rect.x > camera.viewportWidth - rect.width) {
			rect.x = camera.viewportWidth - rect.width;
		}
		if (rect.y < 0) {
			rect.y = 0;
		} else if (rect.y > camera.viewportHeight - rect.height) {
			rect.y = camera.viewportHeight - rect.height;
		}
	}

	private void spawnDot() {
		Rectangle dot = new Rectangle();
		dot.width = playerTexture.getWidth();
		dot.height = playerTexture.getHeight();
		dot.x = MathUtils.random(dot.width, camera.viewportWidth - dot.width);
		dot.y = MathUtils.random(dot.height, camera.viewportHeight - dot.height);
		dots.add(dot);
		lastDotTime = TimeUtils.nanoTime();
	}
}
