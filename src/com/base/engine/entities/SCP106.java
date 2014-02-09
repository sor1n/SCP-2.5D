package com.base.engine.entities;

import java.util.ArrayList;

import com.base.engine.Game;
import com.base.engine.Texture;
import com.base.engine.Time;
import com.base.engine.Transform;
import com.base.engine.Vector2f;
import com.base.engine.Vector3f;

public class SCP106 extends Entity
{
	private int collisionDelay = 0;

	public SCP106(Transform transform)
	{
		super(transform);
		animations = new ArrayList<Texture>();
		animations.add(new Texture("106/S.png"));
		animations.add(new Texture("106/W1.png"));
		animations.add(new Texture("106/W2.png"));
		material.setTexture(animations.get(0));
		STOP_DISTANCE = .5f;
		canPassThroughWalls = true;
		ID = 106;
		entityName = "SCP-106";
		rgbValue = new int[]{0xFF0000, 0, 6};
	}
	
	public SCP106()
	{
		this(new Transform());
	}

	@Override
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
	protected void dyingUpdate(Vector3f orientation, float distance)
	{

	}

	@Override
	protected void chaseUpdate(Vector3f orientation, float distance)
	{
		chase(orientation, distance);
	}

	@Override
	protected void chaseAnim(double timeDecimals)
	{
		if(timeDecimals < 0.25) material.setTexture(animations.get(0));
		else if(timeDecimals < 0.5) material.setTexture(animations.get(1));
		else if(timeDecimals < 0.75) material.setTexture(animations.get(0));
		else material.setTexture(animations.get(2));
	}

	public void render()
	{
		super.render();
	}

	protected void chase(Vector3f orientation, float distance)
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

			Vector3f collisionVector = Game.getLevel().checkCollisions(oldPos, newPos, WIDTH, LENGTH, true);
			if(collisionDelay <= 0 && (collisionVector.getX() == 0 || collisionVector.getZ() == 0))
			{
				collisionDelay = 1;
				int posX = (int)transform.getTranslation().getX();
				int posZ = (int)transform.getTranslation().getZ();
				if(!Game.getLevel().isAir(posX, posZ) && !Game.getLevel().isDoor(posX, posZ))
				{
					//new Corrosion(new Vector3f(((int)(posX) - (Level.SPOT_WIDTH / 2)), 0, (int)(posZ) - 0.01f), 0).spawn(Game.getLevel());
					//new Corrosion(new Vector3f(((int)(posX) + 0.01f), 0, (int)(posZ) + (Level.SPOT_LENGTH / 2)), 1).spawn(Game.getLevel());
					//new Corrosion(new Vector3f(((int)(posX) - (Level.SPOT_WIDTH / 2)), 0, (int)(posZ) + 0.01f + Level.SPOT_LENGTH), 2).spawn(Game.getLevel());
					//new Corrosion(new Vector3f(((int)(posX) - 0.01f - Level.SPOT_WIDTH), 0, (int)(posZ) + (Level.SPOT_LENGTH / 2)), 3).spawn(Game.getLevel());
				}
				if(Game.getLevel().getParticles().size() > 4)
				{
					Vector3f trans = Game.getLevel().getParticles().get(5).getTransform().getTranslation();
					Game.consoleMessage(Game.getLevel().isAir(trans.getXInt(), trans.getZInt()));
				}
			}
			if(collisionVector.getX() == 1 && collisionVector.getZ() == 1) collisionDelay = 0;
			if(canPassThroughWalls) collisionVector = new Vector3f(1f, 0f, 1f);
			Vector3f movementVector = collisionVector.mul(orientation);
			if(movementVector.length() > 0) transform.setTranslation(transform.getTranslation().add(movementVector.mul(moveAmount)));
			if(movementVector.sub(orientation).length() != 0 && canOpenDoors) Game.getLevel().openDoors(transform.getTranslation(), false);
		}
		else state = STATE_ATTACK;
	}
}
