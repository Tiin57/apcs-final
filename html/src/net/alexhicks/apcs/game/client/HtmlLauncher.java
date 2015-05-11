package net.alexhicks.apcs.game.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import net.alexhicks.apcs.game.GdxGame;

public class HtmlLauncher extends GwtApplication {

	@Override
	public GwtApplicationConfiguration getConfig() {
		return new GwtApplicationConfiguration(800, 480);
	}

	@Override
	public ApplicationListener getApplicationListener() {
		return new GdxGame();
	}
}
