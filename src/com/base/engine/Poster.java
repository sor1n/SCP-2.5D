package com.base.engine;

public class Poster extends Particle
{
	public Poster(Vector3f pos, int dir, Texture texture)
	{
		super(pos);
		material = new Material(texture);
		transform = new Transform(pos, Vector3f.ZERO, new Vector3f(Level.SPOT_WIDTH + 1f, Level.SPOT_HEIGHT + .35f, Level.SPOT_LENGTH + 1f));
		switch(dir)
		{
		default:
		case 0: 
			transform.setRotation(0f, 0f, 0f);
			break;
		case 1:
			transform.setRotation(0f, 90f, 0f);
			break;
		case 2:
			transform.setRotation(0f, 180f, 0f);
			break;
		case 3:
			transform.setRotation(0f, 270f, 0f);
			break;
		}
	}

	@Override
	public void update(float delta)
	{
		super.update(delta);
	}

	@Override
	public void render()
	{
		super.render();
	}
}
