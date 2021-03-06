package com.base.engine.entities;

import java.util.ArrayList;
import java.util.Random;

import com.base.engine.Game;
import com.base.engine.audio.*;
import com.base.engine.Texture;
import com.base.engine.Time;
import com.base.engine.Transform;
import com.base.engine.Util;
import com.base.engine.Vector3f;

public class SCP096 extends Entity
{
	public float viewingAngle = 0.2f;

	private Vector3f target = null;
	private double targetInt = 0;

	private int soundDelay = 0;

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
		entityName = "SCP-096";
		rgbValue = new int[]{0xFF0000, 0, 21};
		miniMapColor = new int[]{255, 233, 163};
	}

	public SCP096()
	{
		this(new Transform());
	}

	@Override
	protected void idleUpdate(Vector3f orientation, float distance, float delta)
	{
		MOVE_SPEED = 1f;
		super.idleUpdate(orientation, distance, delta);
		if(isPlayerDirectlyLooking(orientation, viewingAngle)) SoundSystem.playSound(SoundSystem.SCP096_CRY, transform.getTranslation(), 35f);
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
			moveTo(this, target, delta);
			walkAnim(getTimeDecimal());
			if(targetInt > 1600) target = null;
		}
		if(SoundSystem.isPlaying(SoundSystem.SCP096_CRY)) state = STATE_DYING;
	}

	@Override
	protected void attackUpdate(Vector3f orientation, float distance, float delta) 
	{
		material.setTexture(animations.get(0));
		if(distance > STOP_DISTANCE) state = STATE_CHASE;
	}

	@Override
	protected void deadUpdate(Vector3f orientation, float distance, float delta)
	{

	}

	@Override
	protected void dyingUpdate(Vector3f orientation, float distance, float delta) //Crying
	{
		cry(getTimeDecimal());
	}

	@Override
	protected void chaseUpdate(Vector3f orientation, float distance, float delta)
	{
		MOVE_SPEED = getDistanceToPlayer().getZ() + getDistanceToPlayer().getX() + 2f;
		chase(orientation, distance, delta);
		if(!SoundSystem.isPlaying(SoundSystem.SCP096_SCREAM) && soundDelay <= 0)
		{
			SoundSystem.playSound(SoundSystem.SCP096_SCREAM, transform.getTranslation(), 30f, Util.clamp(new Random().nextFloat() + 0.8f, 0.8f, 1f));
			soundDelay = new Random().nextInt(20000) + 10000;
		}
		if(soundDelay > 0) soundDelay--;
	}

	@Override
	public void update(float delta)
	{
		super.update(delta);
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
		if(!SoundSystem.isPlaying(SoundSystem.SCP096_CRY)) state = STATE_CHASE;
		else SoundSystem.setPosition(SoundSystem.SCP096_CRY, transform.getTranslation());
	}

	protected void chase(Vector3f orientation, float distance, float delta)
	{
		double time = (double)Time.getTime();
		double timeDecimals = time - (double)((int)time);

		chaseAnim(timeDecimals);

		if(distance > STOP_DISTANCE)
		{
			float moveAmount = MOVE_SPEED * delta;

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