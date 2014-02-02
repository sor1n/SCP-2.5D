package com.base.engine;

import java.util.ArrayList;
import java.util.Random;

public class Monster 
{
	public static String[] monsterNames = {"MONSTER", "SCP-106"};

	public static final float START = 0, SCALE = 0.7f, SIZEY = SCALE, SIZEX = (float)((double)SIZEY / (1.9310344827586206896551724137931 * 2.0));
	public static final float OFFSET_X = 0f, OFFSET_Y = 0f;
	public static final float TEX_MIN_X = -OFFSET_X, TEX_MAX_X = -1 - OFFSET_X, TEX_MIN_Y = -OFFSET_Y, TEX_MAX_Y = 1 - OFFSET_Y;

	public static final int STATE_IDLE = 0, STATE_CHASE = 1, STATE_ATTACK = 2, STATE_DYING = 3, STATE_DEAD = 4;
	public float OFFSET_FROM_GROUND = 0f;
	public float MOVE_SPEED = 1f;
	public float STOP_DISTANCE = 1.5f;
	public float WIDTH = 0.2f, LENGTH = 0.2f;
	public float SHOOT_DISTANCE = 1000f, SHOT_ANGLE = 10f;
	public float ATTACK_CHANCE = 0.5f;
	public int MAX_HEALTH = 100;
	public int DAMAGE_MIN = 5, DAMAGE_MAX = 30;
	public int ID = 0;

	protected Mesh mesh;
	protected ArrayList<Texture> animations;

	protected Material material;
	protected Transform transform;
	protected Random rand;
	protected int state, health;
	protected boolean canLook, canAttack, canPassThroughWalls;
	protected double deathTime;

	public Monster(Transform transform)
	{
		animations = new ArrayList<Texture>();
		animations.add(new Texture("SSWVA1.png"));
		animations.add(new Texture("SSWVB1.png"));
		animations.add(new Texture("SSWVC1.png"));
		animations.add(new Texture("SSWVD1.png"));

		animations.add(new Texture("SSWVE0.png"));
		animations.add(new Texture("SSWVF0.png"));
		animations.add(new Texture("SSWVG0.png"));

		animations.add(new Texture("SSWVH0.png"));

		animations.add(new Texture("SSWVI0.png"));
		animations.add(new Texture("SSWVJ0.png"));
		animations.add(new Texture("SSWVK0.png"));
		animations.add(new Texture("SSWVL0.png"));

		animations.add(new Texture("SSWVM0.png"));


		Vertex[] vertices = new Vertex[]{new Vertex(new Vector3f(-SIZEX, START, START), new Vector2f(TEX_MAX_X, TEX_MAX_Y)),
				new Vertex(new Vector3f(-SIZEX, SIZEY, START), new Vector2f(TEX_MAX_X, TEX_MIN_Y)),
				new Vertex(new Vector3f(SIZEX, SIZEY, START), new Vector2f(TEX_MIN_X, TEX_MIN_Y)),
				new Vertex(new Vector3f(SIZEX, START, START), new Vector2f(TEX_MIN_X, TEX_MAX_Y))};
		int[] indices = new int[]{0, 1, 2,
				0, 2, 3};
		mesh = new Mesh(vertices, indices);

		rand = new Random();
		this.transform = transform;
		canLook = canAttack = canPassThroughWalls = false;
		health = MAX_HEALTH;
		material = new Material(animations.get(0));
		deathTime = 0;
		state = STATE_IDLE;
	}

	protected void idleUpdate(Vector3f orientation, float distance)
	{
		double time = ((double)Time.getTime() / (double)Time.SECOND);
		double timeDecimals = time - (double)((int)time);

		if(timeDecimals < 0.5)
		{
			canLook = true;
			material.setTexture(animations.get(0));
		}
		else
		{
			material.setTexture(animations.get(1));
			if(canLook)
			{
				Vector2f lineStart = new Vector2f(transform.getTranslation().getX(), transform.getTranslation().getZ());
				Vector2f castDirection = new Vector2f(orientation.getX(), orientation.getZ());
				Vector2f lineEnd = lineStart.add(castDirection.mul(SHOOT_DISTANCE));

				Vector2f collisionVector = Game.getLevel().checkIntersections(lineStart, lineEnd, false);
				Vector2f playerIntersect = new Vector2f(Transform.getCamera().getPos().getX(), Transform.getCamera().getPos().getZ());

				if(collisionVector == null || playerIntersect.sub(lineStart).length() < collisionVector.sub(lineStart).length()) state = STATE_CHASE;
				canLook = false;
			}
		}
	}

	protected void chaseUpdate(Vector3f orientation, float distance)
	{
		double time = ((double)Time.getTime() / (double)Time.SECOND);
		double timeDecimals = time - (double)((int)time);

		chaseAnim(timeDecimals);

		if(rand.nextDouble() < ATTACK_CHANCE * Time.getDelta()) state = STATE_ATTACK;

		if(distance > STOP_DISTANCE)
		{
			float moveAmount = MOVE_SPEED * (float)Time.getDelta();

			Vector3f oldPos = transform.getTranslation();
			Vector3f newPos = transform.getTranslation().add(orientation.mul(moveAmount));

			Vector3f collisionVector = Game.getLevel().checkCollisions(oldPos, newPos, WIDTH, LENGTH);
			if(canPassThroughWalls) collisionVector = new Vector3f(1f, 0f, 1f);
			Vector3f movementVector = collisionVector.mul(orientation);
			if(movementVector.length() > 0) transform.setTranslation(transform.getTranslation().add(movementVector.mul(moveAmount)));
			if(movementVector.sub(orientation).length() != 0) Game.getLevel().openDoors(transform.getTranslation(), false);
		}
		else state = STATE_ATTACK;
	}

	protected void attackUpdate(Vector3f orientation, float distance)
	{
		double time = ((double)Time.getTime() / (double)Time.SECOND);
		double timeDecimals = time - (double)((int)time);

		if(timeDecimals < 0.25) material.setTexture(animations.get(4));
		else if(timeDecimals < 0.5) material.setTexture(animations.get(5));
		else if(timeDecimals < 0.75)
		{
			material.setTexture(animations.get(6));
			if(canAttack)
			{
				Vector2f lineStart = new Vector2f(transform.getTranslation().getX(), transform.getTranslation().getZ());
				Vector2f castDirection = new Vector2f(orientation.getX(), orientation.getZ()).rotate((rand.nextFloat() - 0.5f) * SHOT_ANGLE);
				Vector2f lineEnd = lineStart.add(castDirection.mul(SHOOT_DISTANCE));

				Vector2f collisionVector = Game.getLevel().checkIntersections(lineStart, lineEnd, false);
				Vector2f playerIntersect = Game.getLevel().lineIntersectRect(lineStart, lineEnd, new Vector2f(Transform.getCamera().getPos().getX(), Transform.getCamera().getPos().getZ()), new Vector2f(Player.PLAYER_SIZE, Player.PLAYER_SIZE));

				if(playerIntersect != null && (collisionVector == null || playerIntersect.sub(lineStart).length() < collisionVector.sub(lineStart).length()))
				{
					Game.getLevel().getPlayer().damage(getDamage());
				}
				state = STATE_CHASE;
				canAttack = false;
			}
		}
		else
		{
			canAttack = true;
			material.setTexture(animations.get(5));
		}
	}

	protected void dyingUpdate(Vector3f orientation, float distance)
	{
		double time = ((double)Time.getTime() / (double)Time.SECOND);
		if(deathTime == 0) deathTime = time;
		final float time1 = 0.1f, time2 = 0.3f, time3 = 0.45f, time4 = 0.6f;

		if(time < deathTime + time1)
		{
			material.setTexture(animations.get(8));
			transform.setScale(1, 0.96428571428571428571428571428571f, 1);
		}
		else if(time < deathTime + time2)
		{
			material.setTexture(animations.get(9));
			transform.setScale(1.7f, 0.9f, 1);
		}
		else if(time < deathTime + time3)
		{
			material.setTexture(animations.get(10));
			transform.setScale(1.7f, 0.9f, 1);
		}
		else if(time < deathTime + time4)
		{
			material.setTexture(animations.get(11));
			transform.setScale(1.7f, 0.5f, 1);
		}
		else state = STATE_DEAD;
	}

	protected void deadUpdate(Vector3f orientation, float distance)
	{
		material.setTexture(animations.get(12));
		transform.setScale(1.7586206896551724137931034482759f, 0.28571428571428571428571428571429f, 1);
	}

	protected void alignWithGround()
	{
		transform.getTranslation().setY(OFFSET_FROM_GROUND);
	}

	public void update()
	{
		Vector3f directionToCamera = Transform.getCamera().getPos().sub(transform.getTranslation());
		float distance = directionToCamera.length();
		Vector3f orientation = directionToCamera.div(distance);
		alignWithGround();
		EntityUtil.faceCamera(transform);

		switch(state)
		{
		case STATE_IDLE: idleUpdate(orientation, distance); break;
		case STATE_CHASE: chaseUpdate(orientation, distance); break;
		case STATE_ATTACK: attackUpdate(orientation, distance); break;
		case STATE_DYING: dyingUpdate(orientation, distance); break;
		case STATE_DEAD: deadUpdate(orientation, distance); break;
		}
	}

	public void render()
	{
		Shader shader = Game.getLevel().getShader();
		shader.updateUniforms(transform.getTransformation(), transform.getProjectedTransformation(), material);
		mesh.draw();
	}

	public void damage(int i)
	{
		if(state == STATE_IDLE) state = STATE_CHASE;
		health -= i;
		if(health <= 0) state = STATE_DYING;
	}

	public Transform getTransform()
	{
		return transform;
	}

	public Vector2f getSize()
	{
		return new Vector2f(WIDTH, LENGTH);
	}

	public int getDamage()
	{
		return rand.nextInt(DAMAGE_MAX - DAMAGE_MIN) + DAMAGE_MIN;
	}

	protected void chaseAnim(double timeDecimals)
	{
		if(timeDecimals < 0.25) material.setTexture(animations.get(0));
		else if(timeDecimals < 0.5) material.setTexture(animations.get(1));
		else if(timeDecimals < 0.75) material.setTexture(animations.get(2));
		else material.setTexture(animations.get(3));
	}

	public String toString()
	{
		return monsterNames[ID] + " - (" + (int)getTransform().getTranslation().getX() + ", " + (int)getTransform().getTranslation().getY() + ")";
	}
}
