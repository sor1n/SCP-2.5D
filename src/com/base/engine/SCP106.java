package com.base.engine;

import java.util.ArrayList;

public class SCP106 extends Monster
{
	public SCP106(Transform transform)
	{
		super(transform);
		{
			animations = new ArrayList<Texture>();
			animations.add(new Texture("106S.png"));
			animations.add(new Texture("106W1.png"));
			animations.add(new Texture("106W2.png"));
		}
		STOP_DISTANCE = .5f;
		canPassThroughWalls = true;
		//new PointLight(new BaseLight(new Vector3f(1f, 0f, 0f), 1f), new Attenuation(1, 1, 1), transform.getTranslation(), 2f);
		
		//TODO
		ID = 1;
	}
	
	@Override
	protected void idleUpdate(Vector3f orientation, float distance)
	{
		super.idleUpdate(orientation, distance);
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
		super.chaseUpdate(orientation, distance);
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
}
