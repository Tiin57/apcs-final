package net.alexhicks.apcs.game;

import com.badlogic.gdx.math.Rectangle;

public class AccelRectangle extends Rectangle {

	public float verticalAcceleration = 0.0f;
	public float horizontalAcceleration = 0.0f;

	public void move() {
		this.x += horizontalAcceleration;
		this.y += verticalAcceleration;
	}
	
	public void accelerate(Direction d) {
		switch (d) {
			case UP:
				this.verticalAcceleration += GdxGame.ACCELERATION_CONSTANT;
				break;
			case DOWN:
				this.verticalAcceleration -= GdxGame.ACCELERATION_CONSTANT;
				break;
			case LEFT:
				this.horizontalAcceleration -= GdxGame.ACCELERATION_CONSTANT;
				break;
			case RIGHT:
				this.horizontalAcceleration += GdxGame.ACCELERATION_CONSTANT;
				break;
		}
	}
	
	public void decelerate() {
		this.verticalAcceleration = calculateDeceleration(verticalAcceleration);
		this.horizontalAcceleration = calculateDeceleration(horizontalAcceleration);
	}

	private float calculateDeceleration(float f) {
		return f > 0 ? f - (GdxGame.ACCELERATION_CONSTANT / 2) : f == 0 ? 0 : f + (GdxGame.ACCELERATION_CONSTANT / 2);
	}
}
