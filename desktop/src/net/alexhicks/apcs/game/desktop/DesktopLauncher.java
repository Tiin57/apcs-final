package net.alexhicks.apcs.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.LifecycleListener;
import net.alexhicks.apcs.game.GdxGame;
import net.alexhicks.apcs.game.TimeCoord;

public class DesktopLauncher {

	public static void main(String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "D.O.T. - Defendable Oblique Trinomial";
		config.width = 800;
		config.height = 480;
		LwjglApplication app = new LwjglApplication(new GdxGame(), config);
		app.addLifecycleListener(new LifecycleListener() {

			@Override
			public void pause() { }

			@Override
			public void resume() { }

			@Override
			public void dispose() {
				String json = "[";
				for (TimeCoord c : GdxGame.coords) {
					json += "[" + c.x + ", " + c.y + ", " + c.time + "],";
				}
				json = json.substring(0, json.length() - 1) + "]";
				System.out.println(json);
			}
		});
	}
}
