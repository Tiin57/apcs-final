package net.alexhicks.apcs.game;

import com.badlogic.gdx.math.Rectangle;

public class AccelRectangle extends Rectangle {
	
	public static final float ACCELERATION_CONSTANT = 0.5f;
	public float verticalAcceleration = 0.0f;
	public float horizontalAcceleration = 0.0f;

	public void move() {
		this.x += horizontalAcceleration;
		this.y += verticalAcceleration;
	}
	
	public void accelerate(Direction d) {
		switch (d) {
			case UP:
				this.verticalAcceleration += ACCELERATION_CONSTANT;
				break;
			case DOWN:
				this.verticalAcceleration -= ACCELERATION_CONSTANT;
				break;
			case LEFT:
				this.horizontalAcceleration -= ACCELERATION_CONSTANT;
				break;
			case RIGHT:
				this.horizontalAcceleration += ACCELERATION_CONSTANT;
				break;
		}
	}
	
	public void decelerate() {
		this.verticalAcceleration = calculateDeceleration(verticalAcceleration);
		this.horizontalAcceleration = calculateDeceleration(horizontalAcceleration);
	}

	private float calculateDeceleration(float f) {
		return f > 0 ? f - (ACCELERATION_CONSTANT / 2) : f == 0 ? 0 : f + (ACCELERATION_CONSTANT / 2);
	}
}
