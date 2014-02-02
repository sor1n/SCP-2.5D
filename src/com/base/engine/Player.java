package com.base.engine;

import java.util.Random;

public class Player
{
	public static final float START = 0, SCALE = 0.0625f, SIZEY = SCALE, SIZEX = (float)((double)SIZEY / (1.0379746835443037974683544303797 * 2.0));
	public static final float OFFSET_X = -0.1f, OFFSET_Y = 0f;
	public static final float TEX_MIN_X = -OFFSET_X, TEX_MAX_X = -1 - OFFSET_X, TEX_MIN_Y = -OFFSET_Y, TEX_MAX_Y = 1 - OFFSET_Y;
	
	public static final float MOUSE_SENSITIVITY = 0.5f, MOVE_SPEED = 7f;
	public static final float PLAYER_SIZE = 0.2f, SHOOT_DISTANCE = 1000f, GUN_DISTANCE = 0.105f, GUN_OFFSET = -0.0775f;
	public static final int DAMAGE_MIN = 20, DAMAGE_MAX = 60;
	public static final int MAX_HEALTH = 100;

	private static Mesh mesh;
	private static Material gunMaterial;
	private static Texture[] anims;
	
	private Transform gunTransform;
	private Camera camera;
	private Random rand;
	private int health;

	private static boolean mouseLocked = false;
	private Vector2f centerPosition = new Vector2f(Window.getWidth()/2, Window.getHeight()/2);
	private Vector3f movementVector;

	public Player(Vector3f pos)
	{
		if(mesh == null)
		{
			Vertex[] vertices = new Vertex[]{new Vertex(new Vector3f(-SIZEX, START, START), new Vector2f(TEX_MAX_X, TEX_MAX_Y)),
					new Vertex(new Vector3f(-SIZEX, SIZEY, START), new Vector2f(TEX_MAX_X, TEX_MIN_Y)),
					new Vertex(new Vector3f(SIZEX, SIZEY, START), new Vector2f(TEX_MIN_X, TEX_MIN_Y)),
					new Vertex(new Vector3f(SIZEX, START, START), new Vector2f(TEX_MIN_X, TEX_MAX_Y))};
			int[] indices = new int[]{0, 1, 2,
					0, 2, 3};
			mesh = new Mesh(vertices, indices);
		}
		if(anims == null) anims = new Texture[]{new Texture("PISGFA1.png"), new Texture("PISGFA2.png")};
		if(gunMaterial == null) gunMaterial = new Material(anims[1]);
		camera = new Camera(pos, new Vector3f(0, 0, 1), new Vector3f(0, 1, 0));
		rand = new Random();
		health = MAX_HEALTH;
		gunTransform = new Transform(new Vector3f(7, 7, 0), Vector3f.ZERO, Vector3f.ONE);
		movementVector = Vector3f.ZERO;
	}

	public int getDamage()
	{
		return rand.nextInt(DAMAGE_MAX - DAMAGE_MIN) + DAMAGE_MIN;
	}

	public void input()
	{
		double time = ((double)Time.getTime() / (double)Time.SECOND);
		double timeDecimals = time - (double)((int)time);
		
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
				Input.setMousePosition(centerPosition);
				Input.setCursor(false);
				mouseLocked = true;
			}
			else
			{
				gunMaterial.setTexture(anims[0]);
				Vector2f lineStart = new Vector2f(camera.getPos().getX(), camera.getPos().getZ());
				Vector2f castDir = new Vector2f(camera.getForward().getX(), camera.getForward().getZ()).normalized();
				Vector2f lineEnd = lineStart.add(castDir.mul(SHOOT_DISTANCE));
				Game.getLevel().checkIntersections(lineStart, lineEnd, true);
			}
		}
		if(!Input.getMouseDown(0)) gunMaterial.setTexture(anims[1]);

		movementVector = Vector3f.ZERO;

		if(Input.getKey(Input.KEY_W)) movementVector = movementVector.add(camera.getForward());
		//camera.move(camera.getForward(), movAmt);
		if(Input.getKey(Input.KEY_S)) movementVector = movementVector.sub(camera.getForward());
		//camera.move(camera.getForward(), -movAmt);
		if(Input.getKey(Input.KEY_A)) movementVector = movementVector.add(camera.getLeft());
		//camera.move(camera.getLeft(), movAmt);
		if(Input.getKey(Input.KEY_D)) movementVector = movementVector.add(camera.getRight());
		//camera.move(camera.getRight(), movAmt);

		if(mouseLocked)
		{
			Vector2f deltaPos = Input.getMousePosition().sub(centerPosition);

			boolean rotY = deltaPos.getX() != 0;
			boolean rotX = deltaPos.getY() != 0;

			if(rotY) camera.rotateY(deltaPos.getX() * MOUSE_SENSITIVITY);
			if(rotX) camera.rotateX(-deltaPos.getY() * MOUSE_SENSITIVITY);

			if(rotY || rotX) Input.setMousePosition(centerPosition);
		}
	}

	public void update()
	{
		float movAmt = (float)(MOVE_SPEED * Time.getDelta());
		movementVector.setY(0);
		if(movementVector.length() > 0) movementVector = movementVector.normalized();

		Vector3f oldPosition = camera.getPos();
		Vector3f newPosition = oldPosition.add(movementVector.mul(movAmt));
		Vector3f collisionVector = Game.getLevel().checkCollisions(oldPosition, newPosition, PLAYER_SIZE, PLAYER_SIZE);
		movementVector = movementVector.mul(collisionVector);

		if(movementVector.length() > 0) camera.move(movementVector, movAmt);
		
		//Gun Movement
		gunTransform.setTranslation(camera.getPos().add(camera.getForward().normalized().mul(GUN_DISTANCE)));
		gunTransform.getTranslation().setY(gunTransform.getTranslation().getY() + GUN_OFFSET);
		EntityUtil.faceCamera(gunTransform);
	}

	public void render()
	{
		//Shader shader = Game.getLevel().getShader();
		//shader.updateUniforms(gunTransform.getTransformation(), gunTransform.getProjectedTransformation(), gunMaterial);
		//mesh.draw();
	}

	public Camera getCamera()
	{
		return camera;
	}

	public void damage(int i)
	{
		health -= i;
		if(health > MAX_HEALTH) health = 100;
		System.out.println(health);
		if(health <= 0)
		{
			Game.setRunning(false);
			System.out.println("Game over.");
		}
	}
	
	public int getHealth()
	{
		return health;
	}
}
