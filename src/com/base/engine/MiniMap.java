package com.base.engine;

import static org.lwjgl.opengl.GL11.GL_SCISSOR_TEST;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glRotatef;
import static org.lwjgl.opengl.GL11.glScissor;
import static org.lwjgl.opengl.GL11.glTranslatef;

import java.awt.Color;
import java.util.Arrays;

import com.base.engine.entities.Entity;

public class MiniMap extends GUI
{
	public static final int MAP_SIZE = 256;
	
	private Texture tex;
	private Level level;
	private float x, y;
	private float scaleX = 4.5f, scaleY = scaleX;
	private int borderSize = 6;
	private Vector2f playerPos;
	private boolean showEnemies = true;
	private int size = 32;
	
	private Vector2i oldPlayerPos = new Vector2i(0, 0);
	
	
	public MiniMap(Level level)
	{
		this.level = level;
		tex = new Texture(level.getLevelMap(), true);
		tex.setID(tex.flipY());
		this.x = Window.getWidth() / scaleX - size - (borderSize / 2);
		this.y = Window.getHeight() / scaleY - size - (borderSize / 2);
	}
	
	public void addMapPart(Level level)
	{
		Texture objTex1 = new Texture(this.level.getObjectsMap(), true);
		objTex1.setID(objTex1.flipY());
		Texture map1 = new Texture(this.level.getLevelMap(), true);
		map1.setID(map1.flipY());
		this.level = level;
		Texture objTex2 = new Texture(level.getObjectsMap(), true);
		objTex2.setID(objTex2.flipY());
		Texture map2 = new Texture(level.getLevelMap(), true);
		map2.setID(map2.flipY());
		Vector2i playerLoc2 = objTex2.getBluePixelIndex(2);
		oldPlayerPos = objTex1.getBluePixelIndex(2);
		//Game.consoleMessage(playerLoc1 + " | " + playerLoc2);
		if(playerLoc2 == Vector2i.NEG || oldPlayerPos == Vector2i.NEG) Game.crashGame("Invalid player locations in one or more level maps");
		tex = new Texture(MAP_SIZE, MAP_SIZE);
		int[] pix = new int[MAP_SIZE * MAP_SIZE];
		Arrays.fill(pix, 0x000000);
		for(int x = 0; x < map1.getWidth(); x++)
			for(int y = 0; y < map1.getHeight(); y++)
			{
				if(map1.getPixels()[x + (map1.getHeight() - y - 1) * map1.getWidth()] != Color.black.getRGB()) pix[x + y * MAP_SIZE] = map1.getPixels()[x + (map1.getHeight() - y - 1) * map1.getWidth()];
			}
		for(int x = 0; x < map2.getWidth(); x++)
			for(int y = 0; y < map2.getHeight(); y++)
			{
				if(map2.getPixels()[x + (map2.getHeight() - y - 1) * map2.getWidth()] != Color.black.getRGB()) pix[(x + playerLoc2.getX()) + (y + playerLoc2.getY()) * MAP_SIZE] = map2.getPixels()[x + (map2.getHeight() - y - 1) * map2.getWidth()];
			}
		tex.setID(tex.setPixels(pix, MAP_SIZE));
	}
	
	public void update(float delta)
	{
		playerPos = Transform.getCamera().getPos().getXZ();
	}
	
	public void render()
	{
		RenderUtil.drawRectangle(x * scaleX + (size / 2) * scaleX - 2, y * scaleY + (size / 2) * scaleY - 2, (int)scaleX, (int)scaleY, 255, 0, 0);
		glPushMatrix();
		glTranslatef(x * scaleX + (size / 2) * scaleX, y * scaleY + (size / 2) * scaleY, 0);
		glRotatef(Transform.getCamera().getDir(), 0f, 0f, 1f);
		glTranslatef(-(x * scaleX + (size / 2) * scaleX), -(y * scaleY + (size / 2) * scaleY), -0);
		glEnable(GL_SCISSOR_TEST);
		
		if(showEnemies)
		{
			for(Entity e : level.getMonsters())
			{
				Vector2f pos = e.getTransform().getTranslation().getXZ();
				RenderUtil.drawRectangle((x * scaleX) + (pos.getX() * scaleX) - ((playerPos.getX() - 16) * scaleX) - 2, (y * scaleY) + (pos.getY() * scaleY) - ((playerPos.getY() - 16) * scaleY) - 2, (int)scaleX, (int)scaleY, e.getMinimapColor()[0], e.getMinimapColor()[1], e.getMinimapColor()[2]);
			}
		}
		//TODO: Spacing
		RenderUtil.drawScaledTexturedRectangle(x - playerPos.getX() - oldPlayerPos.getX() + 16, y - playerPos.getY() - oldPlayerPos.getY() + 16, scaleX, scaleY, tex);
		glScissor((int)(x * scaleX), (int)(y * scaleY), (int)(size * scaleX), (int)(size * scaleY));
		glDisable(GL_SCISSOR_TEST);
		glPopMatrix();
		RenderUtil.drawRectangle(x * scaleX, y * scaleY, (int)(size * scaleX), (int)(size * scaleY), 0, 0, 0);
		RenderUtil.drawRectangle(x * scaleX - (borderSize / 2), y * scaleY - (borderSize / 2), (int)(size * scaleX) + borderSize, (int)(size * scaleY) + borderSize, 255, 255, 255);
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
	
	public float getSize()
	{
		return scaleX;
	}
	
	public void setSize(float i)
	{
		scaleX = scaleY = i;
	}
}
