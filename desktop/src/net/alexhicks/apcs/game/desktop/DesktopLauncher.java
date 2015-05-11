package net.alexhicks.apcs.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import net.alexhicks.apcs.game.GdxGame;

public class DesktopLauncher {

	public static void main(String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Game";
		config.width = 800;
		config.height = 480;
		LwjglApplication app = new LwjglApplication(new GdxGame(), config);
	}
}
