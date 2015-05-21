package net.alexhicks.apcs.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.files.FileHandle;
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
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

public class GdxGame extends ApplicationAdapter implements ApplicationListener {

	// Do not use ArrayList or HashMap, use Array<> or other GDX classes
	// Garbage collection makes life better.
	public static final boolean DEBUG = false;
	public static final String TITLE = "DOT " + GdxGame.VERSION;
	public static final String VERSION = "v1.0-alpha2.1";
	private static final int DOT_TIME = 100000 * 10000;
	private static final int WINNING_SCORE = 10;
	public static Array<TimeCoord> coords = new Array<TimeCoord>();
	public OrthographicCamera camera;
	public SpriteBatch batch;
	public Texture playerTexture, dotTexture, trailTexture;
	public AccelRectangle player;
	public Array<AccelRectangle> dots;
	public BitmapFont font;
	private long lastDotTime;
	private boolean isOver = false;
	public int score = 0;
	public float highScore = 0.0f;
	public long startTime, endTime;
	public Array<Float[]> trail;
	private int dotCount = 0;
	private GlyphLayout glyph;
	public boolean isPaused = false;
	private int pauseTimer = 0;
	
	@Override
	public void create() {
		this.startTime = TimeUtils.millis();
		this.batch = new SpriteBatch();
		this.playerTexture = new Texture(Gdx.files.internal(Textures.PLAYER.getLocation()));
		this.dotTexture = new Texture(Gdx.files.internal(Textures.DOT.getLocation()));
		this.trailTexture = new Texture(Gdx.files.internal(Textures.TRAIL.getLocation()));
		this.camera = new OrthographicCamera();
		this.dots = new Array<AccelRectangle>();
		this.trail = new Array<Float[]>();
		this.glyph = new GlyphLayout();
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(Textures.OPENSANS.getLocation()));
		FreeTypeFontParameter parameter = new FreeTypeFontParameter();
		parameter.size = 16;
		parameter.color = Color.BLACK;
		this.font = generator.generateFont(parameter);
		generator.dispose();
		camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		resetPlayer();
		spawnDot();
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(1, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		if (isOver) {
			String x = "Game Over!";
			glyph.setText(font, x);
			font.draw(batch, x, (camera.viewportWidth / 2), (camera.viewportHeight / 2) - (glyph.height * 1));
			x = "Press the spacebar or ENTER to restart!";
			glyph.setText(font, x);
			font.draw(batch, x, (camera.viewportWidth / 2), (camera.viewportHeight / 2) - (glyph.height * 3));
			x = "Score: " + (generateScore() + "").substring(0, 4) + " dots per second";
			glyph.setText(font, x);
			font.draw(batch, x, (camera.viewportWidth / 2), (camera.viewportHeight / 2) + (glyph.height * 1));
			x = "High Score: " + (highScore + "").substring(0, 4) + " dots per second";
			glyph.setText(font, x);
			font.draw(batch, x, (camera.viewportWidth / 2), (camera.viewportHeight / 2) + (glyph.height * 3));
		} else {
			batch.draw(playerTexture, player.x, player.y);
			for (Rectangle dot : dots) {
				batch.draw(dotTexture, dot.x, dot.y);
			}
			for (Float[] f : trail) {
				batch.draw(trailTexture, f[0], f[1]);
			}
			font.draw(batch, "Score: " + score + " / " + WINNING_SCORE, 30, 30);
			if (DEBUG) {
				font.draw(batch, "Vertical: " + player.verticalAcceleration, 30, 90);
				font.draw(batch, "Horizontal: " + player.horizontalAcceleration, 30, 120);
			}
			if (isPaused) {
				font.draw(batch, "Paused.", 30, 150);
			}
		}
		batch.end();
		if (isOver) {
			updateOver();
		} else {
			updatePause();
			if (!isPaused) {
				update();
			}
			checkBoundaries();
		}
	}
	
	private void resetPlayer() {
		this.player = new AccelRectangle();
		player.width = playerTexture.getWidth();
		player.height = playerTexture.getHeight();
		player.x = (camera.viewportWidth / 2) - (player.width / 2);
		player.y = (camera.viewportHeight / 2) - (player.height / 2);
	}
	
	private void updateOver() {
		if (Gdx.input.isKeyPressed(Keys.SPACE) || Gdx.input.isKeyPressed(Keys.ENTER)) {
			score = 0;
			isOver = false;
			resetPlayer();
			trail = new Array<Float[]>();
			dotCount = 0;
			startTime = TimeUtils.millis();
		}
	}
	
	private void updatePause() {
		pauseTimer++;
		if (pauseTimer > 50) {
			if (Gdx.input.isKeyPressed(Keys.SPACE)) {
				isPaused = !isPaused;
				pauseTimer = 0;
			}
		}
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
		if (trail.size == 50) {
			trail.removeIndex(0);
		}
		trail.add(new Float[]{
			player.x + (player.width / 2) - (trailTexture.getWidth() / 2),
			player.y + (player.height / 2) - (trailTexture.getHeight() / 2)
		});
		for (int i = 0; i < dots.size; i++) {
			Rectangle dot = dots.get(i);
			if (dot.overlaps(player)) {
				dots.removeIndex(i);
				score++;
			}
		}
		if (score == WINNING_SCORE) {
			gameOver();
		}
	}

	private void checkBoundaries() {
		if (player.x < 0) {
			player.x = 0;
			if (player.horizontalAcceleration < 0) {
				player.horizontalAcceleration = 0;
			}
		} else if (player.x > camera.viewportWidth - player.width) {
			player.x = camera.viewportWidth - player.width;
			if (player.horizontalAcceleration > 0) {
				player.horizontalAcceleration = 0;
			}
		}
		if (player.y < 0) {
			player.y = 0;
			if (player.verticalAcceleration < 0) {
				player.verticalAcceleration = 0;
			}
		} else if (player.y > camera.viewportHeight - player.height) {
			player.y = camera.viewportHeight - player.height;
			if (player.verticalAcceleration > 0) {
				player.verticalAcceleration = 0;
			}
		}
		/*for (int i = 25; i < trail.size; i++) {
			Float[] f = trail.get(i);
			float x = f[0];
			float y = f[1];
			if (x < player.x + player.width && x + trailTexture.getWidth() > player.x
					&& y < player.y + player.height && y + trailTexture.getHeight() > player.y) {
				gameOver();
			}
		}*/
	}
	
	private float generateScore() {
		return ((score + 0.0f) / (endTime - startTime) * 1000);
	}
	
	private float saveHighScore() {
		FileHandle saveFile = Gdx.files.local("highscore.txt");
		System.out.println(saveFile.file().getAbsolutePath());
		if (!saveFile.exists()) {
			saveFile.write(false);
		}
		try {
			String s = saveFile.readString();
			float score = generateScore();
			if (s != null && !s.equals("")) {
				float f = Float.valueOf(s);
				if (score < f) {
					return f;
				}
			}
			saveFile.writeString(score + "\n", false);
			return score;
		} catch (NumberFormatException ex) {
			ex.printStackTrace();
		}
		return 0.0f;
	}

	private void gameOver() {
		isOver = true;
		endTime = TimeUtils.millis();
		highScore = saveHighScore();
		pauseTimer = 0;
	}

	private void spawnDot() {
		if (dotCount == WINNING_SCORE) {
			return;
		}
		AccelRectangle dot = new AccelRectangle();
		dot.width = playerTexture.getWidth();
		dot.height = playerTexture.getHeight();
		dot.x = MathUtils.random(dot.width, camera.viewportWidth - dot.width);
		dot.y = MathUtils.random(dot.height, camera.viewportHeight - dot.height);
		dots.add(dot);
		lastDotTime = TimeUtils.nanoTime();
		dotCount += 1;
	}
}
