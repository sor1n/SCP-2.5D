package com.base.engine;

public class Map extends Particle
{
	public Map(Vector3f pos, int dir, Level level)
	{
		super(pos);
		material = new Material(new Texture(level.getLevelMap(), true));
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

	public void update()
	{
		//		transform.setTranslation(Transform.getCamera().getPos().add(Transform.getCamera().getForward().normalized().mul(GUN_DISTANCE)));
		//		transform.getTranslation().setY(transform.getTranslation().getY() + GUN_OFFSET);
		//
		//		playerTrans.setTranslation(Transform.getCamera().getPos().add(Transform.getCamera().getForward().normalized().mul(GUN_DISTANCE - 0.01f)));
		super.update();
		//playerTrans.getTranslation().setY(playerTrans.getTranslation().getY() + mapStartOffsetY + (Transform.getCamera().getPos().getZ() / 575) - .055f);
		//playerTrans.getTranslation().setX(playerTrans.getTranslation().getX() - mapStartOffsetX + (Transform.getCamera().getPos().getX() / 620) - .005f);
	}

	public void render()
	{
		super.render();
	}
}
