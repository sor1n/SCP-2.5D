package com.base.engine;

import static org.lwjgl.opengl.GL11.GL_SCISSOR_TEST;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glRotatef;
import static org.lwjgl.opengl.GL11.glScissor;
import static org.lwjgl.opengl.GL11.glTranslatef;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.base.engine.entities.Entity;
import com.base.engine.itementities.ItemEntity;

public class MiniMap extends GUI
{
	private List<Level> levels;

	public static final int SIZE = 32;
	public static final int MAP_SIZE = 500;
	public static final int MAP_OFFSET = MAP_SIZE / 2;
	public int borderSize = 8;
	
	private Texture tex;
	private Level level;
	private float x, y;
	private float scale = 5f;
	private Vector2f playerPos;
	private boolean showEnemies = true, showItems = true;
	private int[] pix;

	public MiniMap(Level level)
	{
		levels = new ArrayList<Level>();
		x = Window.getWidth() / scale - SIZE - (borderSize / 2);
		y = Window.getHeight() / scale - SIZE - (borderSize / 2);
		tex = new Texture(MAP_SIZE, MAP_SIZE);
		pix = new int[MAP_SIZE * MAP_SIZE];
		addMapPart(level, null, null);
	}

	public void addMapPart(Level level, ExitPoint oldPoint, ExitPoint newPoint)
	{
		this.level = level;
		clearMap();
		levels.add(level);
		updateMap();
	}

	public void update(float delta)
	{
		playerPos = Transform.getCamera().getPos().getXZ().add(MAP_OFFSET);
	}

	public void render()
	{
		RenderUtil.pushMatrix();
		RenderUtil.drawRectangle(x * scale + (SIZE / 2) * scale - 2, y * scale + (SIZE / 2) * scale - 2, (int)scale, (int)scale, 255, 0, 0);
		RenderUtil.drawRectangle(x * scale + (SIZE / 2) * scale - 3, y * scale + (SIZE / 2) * scale - 3, (int)scale + 2, (int)scale + 2, 0, 0, 0);
		glTranslatef(x * scale + (SIZE / 2) * scale, y * scale + (SIZE / 2) * scale, 0);
		glRotatef(Transform.getCamera().getDir(), 0f, 0f, 1f);
		glTranslatef(-(x * scale + (SIZE / 2) * scale), -(y * scale + (SIZE / 2) * scale), -0);
		glEnable(GL_SCISSOR_TEST);

		if(showEnemies)
		{
			for(Entity e : level.getMonsters())
			{
				Vector2f pos = e.getTransform().getTranslation().getXZ().add(MAP_OFFSET);
				float rot = Entity.getFacingAngle(pos, pos.add(e.getDirection()));
				RenderUtil.drawTriangle((x * scale) + (pos.getX() * scale) - ((playerPos.getX() - 16) * scale) - 3, (y * scale) + (pos.getY() * scale) - ((playerPos.getY() - 16) * scale) - 3, (int)scale + 1, (int)scale + 1, e.getMinimapColor()[0], e.getMinimapColor()[1], e.getMinimapColor()[2], rot, 0f, 0f, 1f);
				RenderUtil.drawTriangle((x * scale) + (pos.getX() * scale) - ((playerPos.getX() - 16) * scale) - 4, (y * scale) + (pos.getY() * scale) - ((playerPos.getY() - 16) * scale) - 4, (int)scale + 3, (int)scale + 3, 0, 0, 0, rot, 0f, 0f, 1f);
			}
		}
		if(showItems)
		{
			for(ItemEntity e : level.getItemEntities())
			{
				Vector2f pos = e.getTransform().getTranslation().getXZ().add(MAP_OFFSET);
				RenderUtil.drawRectangle((x * scale) + (pos.getX() * scale) - ((playerPos.getX() - 16) * scale) - 1, (y * scale) + (pos.getY() * scale) - ((playerPos.getY() - 16) * scale) - 1, (int)scale - 2, (int)scale - 2, e.getMinimapColor()[0], e.getMinimapColor()[1], e.getMinimapColor()[2]);
				RenderUtil.drawRectangle((x * scale) + (pos.getX() * scale) - ((playerPos.getX() - 16) * scale) - 2, (y * scale) + (pos.getY() * scale) - ((playerPos.getY() - 16) * scale) - 2, (int)scale, (int)scale, 0, 0, 0);
			}
		}
		RenderUtil.drawScaledTexturedRectangle(x - playerPos.getX() + 16, y - playerPos.getY() + 16, scale, scale, tex);
		glScissor((int)(x * scale), (int)(y * scale), (int)(SIZE * scale), (int)(SIZE * scale));
		glDisable(GL_SCISSOR_TEST);
		RenderUtil.popMatrix();
		RenderUtil.drawRectangle(x * scale, y * scale, (int)(SIZE * scale), (int)(SIZE * scale), 0, 0, 0);
		RenderUtil.drawRectangle(x * scale - (borderSize / 2), y * scale - (borderSize / 2), (int)(SIZE * scale) + borderSize, (int)(SIZE * scale) + borderSize, 255, 255, 255);
	}

	private void addMap(Texture map)
	{
		addMap(map, Vector2i.ZERO);
	}

	private void addMap(Texture map, Vector2i offset)
	{
		for(int x = 0; x < map.getWidth(); x++)
			for(int y = 0; y < map.getHeight(); y++)
			{
				if(map.getPixels()[x + (map.getHeight() - y - 1) * map.getWidth()] != Color.black.getRGB()) setPixel(pix, x + MAP_OFFSET + offset.getX(), y + MAP_OFFSET + offset.getY(), MAP_SIZE, MAP_SIZE, map.getPixels()[x + (map.getHeight() - y - 1) * map.getWidth()], false); //pix[x + y * MAP_SIZE] = map1.getPixels()[x + (map1.getHeight() - y - 1) * map1.getWidth()];
			}
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
		return scale;
	}

	public void setSize(float i)
	{
		scale = i;
	}

	private void setPixel(int[] array, int x, int y, int width, int height, int id)
	{
		array[x + (height - y - 1) * width] = id;
	}

	private void setPixel(int[] array, int x, int y, int width, int height, int id, boolean turn)
	{
		array[x + y * width] = id;
	}

	private void updateMap()
	{
		for(int i = 0; i < levels.size(); i++)
		{
			Texture map = new Texture(levels.get(i).getLevelMap(), true);
			map.setID(map.flipY());
			if(isCurrentLevel(map)) addMap(map);
		}
		tex.setID(tex.setPixels(pix, MAP_SIZE));
	}

	public Vector2i calcOffset(ExitPoint oldPoint, ExitPoint newPoint)
	{
		Vector2i oldDoor = oldPoint.getExitPoint().getXZ().toInt();
		Vector2i newDoor = newPoint.getExitPoint().getXZ().toInt();
		return newDoor.sub(oldDoor);
	}

	private void clearMap()
	{
		Arrays.fill(pix, 0x000000);
		for(int x = 0; x < MAP_SIZE; x++)
			for(int y = 0; y < MAP_SIZE; y++) if(x == 0 || x == MAP_SIZE - 1 || y == 0 || y == MAP_SIZE - 1) setPixel(pix, x, y, MAP_SIZE, MAP_SIZE, 0xFF0000);
	}

	private boolean isCurrentLevel(Texture text)
	{
		return (level.getLevelMap().equalsIgnoreCase(text.getFileName()));
	}
}
