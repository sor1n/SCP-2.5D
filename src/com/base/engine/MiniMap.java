package com.base.engine;

import static org.lwjgl.opengl.GL11.*;

import com.base.engine.entities.Entity;

public class MiniMap extends GUI
{
	private Texture tex;
	private Level level;
	private float x, y;
	public float scaleX = 5f, scaleY = scaleX;
	private int borderSize = 6;
	private Vector2f playerPos;
	private boolean showEnemies = true;
	
	public MiniMap(Level level)
	{
		this.level = level;
		tex = new Texture(level.getLevelMap(), true);
		tex.setID(tex.flipY());
		this.x = Window.getWidth() / scaleX - tex.getWidth() - (borderSize / 2);
		this.y = Window.getHeight() / scaleY - tex.getHeight() - (borderSize / 2);
	}
	
	public void update(float delta)
	{
		playerPos = Transform.getCamera().getPos().getXZ();
	}
	
	public void render()
	{
		RenderUtil.drawRectangle(x * scaleX + (tex.getWidth() / 2) * scaleX - 2, y * scaleY + (tex.getHeight() / 2) * scaleY - 2, 4, 4, 255, 0, 0);
		//		glMatrixMode(GL_MODELVIEW);
		glPushMatrix();
		glTranslatef(x * scaleX + (tex.getWidth() / 2) * scaleX, y * scaleY + (tex.getHeight() / 2) * scaleY, 0);
		glRotatef(Transform.getCamera().getDir(), 0f, 0f, 1f);
		glTranslatef(-(x * scaleX + (tex.getWidth() / 2) * scaleX), -(y * scaleY + (tex.getHeight() / 2) * scaleY), -0);
		glEnable(GL_SCISSOR_TEST);
		
		if(showEnemies)
		{
			for(Entity e : level.getMonsters())
			{
				Vector2f pos = e.getTransform().getTranslation().getXZ();
				RenderUtil.drawRectangle((x * scaleX) + (pos.getX() * scaleX) - ((playerPos.getX() - 16) * scaleX) - 2, (y * scaleY) + (pos.getY() * scaleY) - ((playerPos.getY() - 16) * scaleY) - 2, 4, 4, e.getMinimapColor()[0], e.getMinimapColor()[1], e.getMinimapColor()[2]);
			}
		}
		RenderUtil.drawScaledTexturedRectangle(x - playerPos.getX() + 16, y - playerPos.getY() + 16, scaleX, scaleY, tex);
		glScissor((int)(x * scaleX), (int)(y * scaleY), (int)(tex.getWidth() * scaleX), (int)(tex.getHeight() * scaleY));
		glDisable(GL_SCISSOR_TEST);
		glPopMatrix();
		RenderUtil.drawRectangle(x * scaleX, y * scaleY, (int)(tex.getWidth() * scaleX), (int)(tex.getHeight() * scaleY), 0, 0, 0);
		RenderUtil.drawRectangle(x * scaleX - (borderSize / 2), y * scaleY - (borderSize / 2), (int)(tex.getHeight() * scaleX) + borderSize, (int)(tex.getHeight() * scaleY) + borderSize, 255, 255, 255);
	}
	
	public Level getLevel()
	{
		return level;
	}
	
	public void setShowEnemies(boolean b)
	{
		showEnemies = b;
	}
	
	public boolean isShowingEnemies()
	{
		return showEnemies;
	}
}
