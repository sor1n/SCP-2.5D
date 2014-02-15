package com.base.engine.entities;

import java.util.ArrayList;
import java.util.Random;

import com.base.engine.EntityUtil;
import com.base.engine.Game;
import com.base.engine.Material;
import com.base.engine.Mesh;
import com.base.engine.Shader;
import com.base.engine.Texture;
import com.base.engine.Time;
import com.base.engine.Transform;
import com.base.engine.Util;
import com.base.engine.Vector2f;
import com.base.engine.Vector3f;
import com.base.engine.Vertex;

public class Entity 
{
	public static Class<?>[] monsters = {MTF.class, SCP106.class, SCP096.class};

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
	protected int ID;
	public String entityName;

	protected int[] rgbValue, miniMapColor;

	protected Mesh mesh;
	protected ArrayList<Texture> animations;

	protected Material material;
	protected Transform transform;
	protected Random rand;
	protected int state, health;
	protected boolean canLook, canAttack, canPassThroughWalls, canOpenDoors;
	protected double deathTime;

	public Entity(Transform transform)
	{
		Vertex[] vertices = new Vertex[]{new Vertex(new Vector3f(-SIZEX, START, START), new Vector2f(TEX_MAX_X, TEX_MAX_Y)),
				new Vertex(new Vector3f(-SIZEX, SIZEY, START), new Vector2f(TEX_MAX_X, TEX_MIN_Y)),
				new Vertex(new Vector3f(SIZEX, SIZEY, START), new Vector2f(TEX_MIN_X, TEX_MIN_Y)),
				new Vertex(new Vector3f(SIZEX, START, START), new Vector2f(TEX_MIN_X, TEX_MAX_Y))};
		int[] indices = new int[]{0, 1, 2,
				0, 2, 3};
		mesh = new Mesh(vertices, indices);
		animations = new ArrayList<Texture>();
		material = new Material(new Texture("test.png"));
		rand = new Random();
		this.transform = transform;
		canLook = canAttack = canPassThroughWalls = canOpenDoors = false;
		health = MAX_HEALTH;
		deathTime = 0;
		state = STATE_IDLE;
		ID = -1;
		miniMapColor = new int[]{255, 0, 0};
	}

	public Entity()
	{
		this(new Transform());
	}

	protected void idleUpdate(Vector3f orientation, float distance, float delta)
	{
	}

	protected void chaseUpdate(Vector3f orientation, float distance, float delta)
	{
	}

	protected void attackUpdate(Vector3f orientation, float distance, float delta)
	{
	}

	protected void dyingUpdate(Vector3f orientation, float distance, float delta)
	{
	}

	protected void deadUpdate(Vector3f orientation, float distance, float delta)
	{
	}

	protected void alignWithGround()
	{
		transform.getTranslation().setY(OFFSET_FROM_GROUND);
	}

	public void update(float delta)
	{
		Vector3f directionToCamera = Transform.getCamera().getPos().sub(transform.getTranslation());
		float distance = directionToCamera.length();
		Vector3f orientation = directionToCamera.div(distance);
		alignWithGround();
		EntityUtil.faceCamera(transform);

		switch(state)
		{
		case STATE_IDLE: idleUpdate(orientation, distance, delta); break;
		case STATE_CHASE: chaseUpdate(orientation, distance, delta); break;
		case STATE_ATTACK: attackUpdate(orientation, distance, delta); break;
		case STATE_DYING: dyingUpdate(orientation, distance, delta); break;
		case STATE_DEAD: deadUpdate(orientation, distance, delta); break;
		}
	}

	public void render()
	{
		if(material != null)
		{
			Shader shader = Game.getLevel().getShader();
			shader.updateUniforms(transform.getTransformation(), transform.getProjectedTransformation(), material);
			mesh.draw();
		}
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
	}

	public String toString()
	{
		return entityName + " - (" + (int)getTransform().getTranslation().getX() + ", " + (int)getTransform().getTranslation().getY() + ")";
	}

	public int getID()
	{
		return ID;
	}

	public boolean isSCP()
	{
		return (ID >= 0)? true : false;
	}

	protected void chase(Vector3f orientation, float distance, float delta)
	{
		double time = (double)Time.getTime();
		double timeDecimals = time - (double)((int)time);

		chaseAnim(timeDecimals);

		if(rand.nextDouble() < ATTACK_CHANCE * delta) state = STATE_ATTACK;

		if(distance > STOP_DISTANCE)
		{
			float moveAmount = MOVE_SPEED * delta;

			Vector3f oldPos = transform.getTranslation();
			Vector3f newPos = transform.getTranslation().add(orientation.mul(moveAmount));

			Vector3f collisionVector = Game.getLevel().checkCollisions(oldPos, newPos, WIDTH, LENGTH);
			if(canPassThroughWalls) collisionVector = new Vector3f(1f, 0f, 1f);
			Vector3f movementVector = collisionVector.mul(orientation);
			if(movementVector.length() > 0) transform.setTranslation(transform.getTranslation().add(movementVector.mul(moveAmount)));
			if(movementVector.sub(orientation).length() != 0 && canOpenDoors) Game.getLevel().openDoors(transform.getTranslation(), false);
		}
		else state = STATE_ATTACK;
	}

	protected void shoot(Vector3f orientation, float distance) {}

	public boolean isPlayerDirectlyLooking(Vector3f orientation, float viewingAngle)
	{
		Vector2f lineStart = new Vector2f(transform.getTranslation().getX(), transform.getTranslation().getZ());
		Vector2f castDirection = new Vector2f(orientation.getX(), orientation.getZ());
		Vector2f lineEnd = lineStart.add(castDirection.mul(SHOOT_DISTANCE));

		Vector2f collisionVector = Game.getLevel().checkIntersections(lineStart, lineEnd, false);
		Vector2f playerIntersect = new Vector2f(Transform.getCamera().getPos().getX(), Transform.getCamera().getPos().getZ());

		if(collisionVector == null || playerIntersect.sub(lineStart).length() < collisionVector.sub(lineStart).length())
		{
			Vector2f SlineStart = new Vector2f(Transform.getCamera().getPos().getX(), Transform.getCamera().getPos().getZ());
			Vector2f castDir = new Vector2f(Transform.getCamera().getForward().getX(), Transform.getCamera().getForward().getZ()).normalized();
			Vector2f SlineEnd = lineStart.add(castDir.mul(SHOOT_DISTANCE));
			if(Util.floatBetween(Transform.getCamera().getForward().getY(), -viewingAngle, viewingAngle)) return Game.getLevel().isLookingAtEntity(SlineStart, SlineEnd) == this;
		}
		return false;
	}

	public static double getTimeDecimal()
	{
		double time = (double)Time.getTime();
		return time - (double)((int)time);
	}

	protected void moveTo(Entity entity, Vector3f target, float delta)
	{
		Vector3f directionToCamera = target.sub(entity.transform.getTranslation());
		float distance = directionToCamera.length();
		Vector3f orientation = directionToCamera.div(distance);

		float moveAmount = MOVE_SPEED * delta;

		Vector3f oldPos = entity.transform.getTranslation();
		Vector3f newPos = entity.transform.getTranslation().add(orientation.mul(moveAmount));

		Vector3f collisionVector = Game.getLevel().checkCollisions(oldPos, newPos, WIDTH, LENGTH);
		if(canPassThroughWalls) collisionVector = new Vector3f(1f, 0f, 1f);
		Vector3f movementVector = collisionVector.mul(orientation);
		if(movementVector.length() > 0) entity.transform.setTranslation(entity.transform.getTranslation().add(movementVector.mul(moveAmount)));
		if(movementVector.sub(orientation).length() != 0 && canOpenDoors) Game.getLevel().openDoors(entity.transform.getTranslation(), false);
	}

	protected boolean isMoveTo(Entity entity, Vector3f target, float delta)
	{
		Vector3f directionToCamera = target.sub(entity.transform.getTranslation());
		float distance = directionToCamera.length();
		Vector3f orientation = directionToCamera.div(distance);

		float moveAmount = MOVE_SPEED * delta;

		Vector3f oldPos = entity.transform.getTranslation();
		Vector3f newPos = entity.transform.getTranslation().add(orientation.mul(moveAmount));

		Vector3f collisionVector = Game.getLevel().checkCollisions(oldPos, newPos, WIDTH, LENGTH);
		if(canPassThroughWalls) collisionVector = new Vector3f(1f, 0f, 1f);
		Vector3f movementVector = collisionVector.mul(orientation);
		if(movementVector.length() > 0) entity.transform.setTranslation(entity.transform.getTranslation().add(movementVector.mul(moveAmount)));
		if(movementVector.sub(orientation).length() != 0 && canOpenDoors) Game.getLevel().openDoors(entity.transform.getTranslation(), false);
		return collisionVector != Vector3f.ZERO;
	}

	public int[] getRGBValue()
	{
		return rgbValue;
	}
	
	public int[] getMinimapColor()
	{
		return miniMapColor;
	}

	public String getEntityName()
	{
		return entityName;
	}
	
	public Vector3f getDistanceFromPlayer()
	{
		return Transform.getCamera().getPos().sub(transform.getTranslation());
	}
}
