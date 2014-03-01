package com.base.engine.entities;

import java.util.ArrayList;
import java.util.Random;

import com.base.engine.Game;
import com.base.engine.Material;
import com.base.engine.Mesh;
import com.base.engine.Player;
import com.base.engine.Shader;
import com.base.engine.Texture;
import com.base.engine.Time;
import com.base.engine.Transform;
import com.base.engine.Vector2f;
import com.base.engine.Vector3f;
import com.base.engine.Vertex;

public class MTF extends Entity
{
	public MTF(Transform transform)
	{
		super(transform);
		animations = new ArrayList<Texture>();
		animations.add(new Texture("old/SSWVA1.png"));
		animations.add(new Texture("old/SSWVB1.png"));
		animations.add(new Texture("old/SSWVC1.png"));
		animations.add(new Texture("old/SSWVD1.png"));

		animations.add(new Texture("old/SSWVE0.png"));
		animations.add(new Texture("old/SSWVF0.png"));
		animations.add(new Texture("old/SSWVG0.png"));

		animations.add(new Texture("old/SSWVH0.png"));

		animations.add(new Texture("old/SSWVI0.png"));
		animations.add(new Texture("old/SSWVJ0.png"));
		animations.add(new Texture("old/SSWVK0.png"));
		animations.add(new Texture("old/SSWVL0.png"));

		animations.add(new Texture("old/SSWVM0.png"));


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
		canOpenDoors = true;
		health = MAX_HEALTH;
		material = new Material(animations.get(0));
		deathTime = 0;
		state = STATE_IDLE;
		ID = -1;
		rgbValue = new int[]{0, 0, 22};
		entityName = "MTF";
	}
	
	public MTF()
	{
		this(new Transform());
	}

	@Override
	protected void idleUpdate(Vector3f orientation, float distance, float delta)
	{
		double time = (double)Time.getTime();
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

	@Override
	protected void chaseUpdate(Vector3f orientation, float distance, float delta)
	{
		chase(orientation, distance, delta);
	}

	@Override
	protected void attackUpdate(Vector3f orientation, float distance, float delta)
	{
		double time = (double)Time.getTime();
		double timeDecimals = time - (double)((int)time);

		if(timeDecimals < 0.25) material.setTexture(animations.get(4));
		else if(timeDecimals < 0.5) material.setTexture(animations.get(5));
		else if(timeDecimals < 0.75) shoot(orientation, distance);
		else
		{
			canAttack = true;
			material.setTexture(animations.get(5));
		}
	}

	@Override
	protected void dyingUpdate(Vector3f orientation, float distance, float delta)
	{
		double time = (double)Time.getTime();
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

	@Override
	protected void deadUpdate(Vector3f orientation, float distance, float delta)
	{
		material.setTexture(animations.get(12));
		transform.setScale(1.7586206896551724137931034482759f, 0.28571428571428571428571428571429f, 1);
	}

	protected void alignWithGround()
	{
		transform.getTranslation().setY(OFFSET_FROM_GROUND);
	}

	@Override
	public void update(float delta)
	{
		Vector3f directionToCamera = Transform.getCamera().getPos().sub(transform.getTranslation());
		float distance = directionToCamera.length();
		Vector3f orientation = directionToCamera.div(distance);
		alignWithGround();
		faceCamera(transform);
		switch(state)
		{
		case STATE_IDLE: idleUpdate(orientation, distance, delta); break;
		case STATE_CHASE: chaseUpdate(orientation, distance, delta); break;
		case STATE_ATTACK: attackUpdate(orientation, distance, delta); break;
		case STATE_DYING: dyingUpdate(orientation, distance, delta); break;
		case STATE_DEAD: deadUpdate(orientation, distance, delta); break;
		}
	}

	@Override
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
	
	protected void shoot(Vector3f orientation, float distance)
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
}
