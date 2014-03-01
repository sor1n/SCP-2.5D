package com.base.engine;

import java.util.Random;

import com.base.engine.audio.SoundSystem;

public class Player
{
	public static final float START = 0, SCALE = 0.0625f, SIZEY = SCALE, SIZEX = (float)((double)SIZEY / (1.0379746835443037974683544303797 * 2.0));
	public static final float OFFSET_X = -0.1f, OFFSET_Y = 0f;
	public static final float TEX_MIN_X = -OFFSET_X, TEX_MAX_X = -1 - OFFSET_X, TEX_MIN_Y = -OFFSET_Y, TEX_MAX_Y = 1 - OFFSET_Y;

	public static final float MAP_WIDTH = 10f, MAP_HEIGHT = 10f, MAP_OFFSET_X = 5, MAP_OFFSET_Y = 5;

	public static final float MOUSE_SENSITIVITY = 0.5f, MOVE_SPEED = 7f;
	public static final float PLAYER_SIZE = 0.2f, SHOOT_DISTANCE = 1000f, GUN_DISTANCE = 0.105f, GUN_OFFSET = -0.0775f;
	public static final int DAMAGE_MIN = 20, DAMAGE_MAX = 60;
	public static final int MAX_HEALTH = 100;
	
	private Camera camera;
	private Random rand;
	private Inventory inventory;
	private int health;

	private static boolean mouseLocked = false;
	private Vector3f movementVector;
	
	public Player(Vector3f pos, Inventory inventory)
	{
		camera = new Camera(pos, new Vector3f(0, 0, 1), new Vector3f(0, 1, 0));
		if(inventory == null) this.inventory = new Inventory();
		else this.inventory = inventory;
		rand = new Random();
		health = MAX_HEALTH;
		movementVector = Vector3f.ZERO;
	}

	public int getDamage()
	{
		return rand.nextInt(DAMAGE_MAX - DAMAGE_MIN) + DAMAGE_MIN;
	}

	public void input(float delta)
	{
		inventory.input(delta);
		
		if(Input.getKeyDown(Input.KEY_E)) Game.getLevel().openDoors(camera.getPos(), true);
		
		if(Input.getKey(Input.KEY_ESCAPE))
		{
			Input.setCursor(true);
			mouseLocked = false;
		}
		if(Input.getMouseDown(0))
		{
			if(!mouseLocked)
			{
				Input.setMousePosition(Window.centerPosition);
				Input.setCursor(false);
				mouseLocked = true;
			}
			else
			{
				Vector2f lineStart = new Vector2f(camera.getPos().getX(), camera.getPos().getZ());
				Vector2f castDir = new Vector2f(camera.getForward().getX(), camera.getForward().getZ()).normalized();
				Vector2f lineEnd = lineStart.add(castDir.mul(SHOOT_DISTANCE));
				Game.getLevel().checkIntersections(lineStart, lineEnd, true);
			}
		}

		movementVector = Vector3f.ZERO;

		if(Input.getKey(Input.KEY_W)) movementVector = movementVector.add(camera.getForward());
		if(Input.getKey(Input.KEY_S)) movementVector = movementVector.sub(camera.getForward());
		if(Input.getKey(Input.KEY_A)) movementVector = movementVector.add(camera.getLeft());
		if(Input.getKey(Input.KEY_D)) movementVector = movementVector.add(camera.getRight());

		if(mouseLocked)
		{
			Vector2f deltaPos = Input.getMousePosition().sub(Window.centerPosition);

			boolean rotY = deltaPos.getX() != 0;
			boolean rotX = deltaPos.getY() != 0;

			if(rotY) camera.rotateY(deltaPos.getX() * MOUSE_SENSITIVITY);
			if(rotX) camera.rotateX(-deltaPos.getY() * MOUSE_SENSITIVITY);

			if(rotY || rotX) Input.setMousePosition(Window.centerPosition);
		}
	}

	public void update(float delta)
	{
		SoundSystem.updateListener(camera);
		inventory.update(delta);
		
		float movAmt = (float)(MOVE_SPEED * delta);
		movementVector.setY(0); //TODO: Y Dimension shiz
		if(movementVector.length() > 0) movementVector = movementVector.normalized();

		Vector3f oldPosition = camera.getPos();
		Vector3f newPosition = oldPosition.add(movementVector.mul(movAmt));
		Vector3f collisionVector = Game.getLevel().checkCollisions(oldPosition, newPosition, PLAYER_SIZE, PLAYER_SIZE);
		movementVector = movementVector.mul(collisionVector);

		if(movementVector.length() > 0) camera.move(movementVector, movAmt);
	}

	public void render()
	{
		
	}
	
	public void renderGUI()
	{
		inventory.render();
	}

	public Camera getCamera()
	{
		return camera;
	}

	public void damage(int i)
	{
		health -= i;
		if(health > MAX_HEALTH) health = 100;
		Game.consoleMessage(health);
		if(health <= 0)
		{
			Game.setRunning(false); //TODO: death
			Game.consoleMessage("Game over.");
		}
	}

	public int getHealth()
	{
		return health;
	}
	
	public Inventory getInventory()
	{
		return inventory;
	}
}
