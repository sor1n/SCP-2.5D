package com.base.engine.entities;

import java.util.ArrayList;
import java.util.Random;

import com.base.engine.Game;
import com.base.engine.Texture;
import com.base.engine.Time;
import com.base.engine.Transform;
import com.base.engine.Vector3f;

public class SCP096 extends Entity
{
	public static final double TIME_DELAY = 200.0;

	private double cryTime = 0;
	private Vector3f target = null;
	private double targetInt = 0;

	public SCP096(Transform transform)
	{
		super(transform);
		animations = new ArrayList<Texture>();
		animations.add(new Texture("096/096S.png"));
		animations.add(new Texture("096/096W1.png"));
		animations.add(new Texture("096/096W2.png"));
		animations.add(new Texture("096/096C1.png"));
		animations.add(new Texture("096/096C2.png"));
		animations.add(new Texture("096/096R1.png"));
		animations.add(new Texture("096/096R2.png"));
		animations.add(new Texture("096/096R3.png"));
		material.setTexture(animations.get(0));
		STOP_DISTANCE = .5f;
		MOVE_SPEED = 1f;
		canPassThroughWalls = false;
		ID = 96;
	}

	@Override
	protected void idleUpdate(Vector3f orientation, float distance)
	{
		MOVE_SPEED = 1f;
		cryTime = 0;
		super.idleUpdate(orientation, distance);
		if(isPlayerLooking()) state = STATE_DYING;
		else if(new Random().nextInt(200) < 20 && target == null)
		{
			targetInt = 0;
			int randX = new Random().nextInt(6) - 3;
			int randY = new Random().nextInt(6) - 3;
			target = new Vector3f(transform.getTranslation().getX() + randX, OFFSET_FROM_GROUND, transform.getTranslation().getZ() + randY);
		}
		if(target == null) material.setTexture(animations.get(0));
		else
		{
			targetInt += 0.1;
			moveTo(this, target);
			walkAnim(getTimeDecimal());
			if(targetInt > 1600) target = null;
		}
	}

	@Override
	protected void attackUpdate(Vector3f orientation, float distance) 
	{
		material.setTexture(animations.get(0));
		if(distance > STOP_DISTANCE) state = STATE_CHASE;
	}

	@Override
	protected void deadUpdate(Vector3f orientation, float distance)
	{

	}

	@Override
	protected void dyingUpdate(Vector3f orientation, float distance) //Crying
	{
		cry(getTimeDecimal());
	}

	@Override
	protected void chaseUpdate(Vector3f orientation, float distance)
	{
		MOVE_SPEED = 3f;
		chase(orientation, distance);
	}

	protected void walkAnim(double timeDecimals)
	{
		if(timeDecimals < 0.25) material.setTexture(animations.get(0));
		else if(timeDecimals < 0.5) material.setTexture(animations.get(1));
		else if(timeDecimals < 0.75) material.setTexture(animations.get(0));
		else material.setTexture(animations.get(2));
	}
	
	@Override
	protected void chaseAnim(double timeDecimals)
	{
		if(timeDecimals < 0.25) material.setTexture(animations.get(7));
		else if(timeDecimals < 0.5) material.setTexture(animations.get(6));
		else if(timeDecimals < 0.75) material.setTexture(animations.get(7));
		else material.setTexture(animations.get(5));
	}

	@Override
	public void update()
	{
		super.update();
	}

	public void render()
	{
		super.render();
	}

	private void cry(double timeDecimals)
	{
		if(timeDecimals < 0.25) material.setTexture(animations.get(3));
		else if(timeDecimals < 0.5) material.setTexture(animations.get(4));
		else if(timeDecimals < 0.75) material.setTexture(animations.get(3));
		else material.setTexture(animations.get(4));
		cryTime += 0.01;
		if(cryTime > TIME_DELAY) state = STATE_CHASE;
	}

	protected void chase(Vector3f orientation, float distance)
	{
		double time = ((double)Time.getTime() / (double)Time.SECOND);
		double timeDecimals = time - (double)((int)time);

		chaseAnim(timeDecimals);

		if(distance > STOP_DISTANCE)
		{
			float moveAmount = MOVE_SPEED * (float)Time.getDelta();

			Vector3f oldPos = transform.getTranslation();
			Vector3f newPos = transform.getTranslation().add(orientation.mul(moveAmount));

			Vector3f collisionVector = Game.getLevel().checkCollisions(oldPos, newPos, WIDTH, LENGTH);
			if(canPassThroughWalls) collisionVector = new Vector3f(1f, 0f, 1f);
			Vector3f movementVector = collisionVector.mul(orientation);
			if(movementVector.length() > 0) transform.setTranslation(transform.getTranslation().add(movementVector.mul(moveAmount)));
			if(movementVector.sub(orientation).length() != 0) Game.getLevel().removeDoor(transform.getTranslation());
		}
		else state = STATE_ATTACK;
	}
}