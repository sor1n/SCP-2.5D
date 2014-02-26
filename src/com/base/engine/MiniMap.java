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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.base.engine.entities.Entity;

public class MiniMap extends GUI
{
	private List<Level> levels;

	public static final int SIZE = 32;
	public static final int MAP_SIZE = 500;
	public static final int MAP_OFFSET = MAP_SIZE / 2;

	private Texture tex;
	private Level level;
	private float x, y;
	private float scaleX = 4.5f, scaleY = scaleX;
	private int borderSize = 8;
	private Vector2f playerPos;
	private boolean showEnemies = true;
	private int[] pix;

	public MiniMap(Level level)
	{
		levels = new ArrayList<Level>();
		x = Window.getWidth() / scaleX - SIZE - (borderSize / 2);
		y = Window.getHeight() / scaleY - SIZE - (borderSize / 2);
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
		glPushMatrix();
		RenderUtil.drawRectangle(x * scaleX + (SIZE / 2) * scaleX - 2, y * scaleY + (SIZE / 2) * scaleY - 2, (int)scaleX, (int)scaleY, 255, 0, 0);
		glTranslatef(x * scaleX + (SIZE / 2) * scaleX, y * scaleY + (SIZE / 2) * scaleY, 0);
		glRotatef(Transform.getCamera().getDir(), 0f, 0f, 1f);
		glTranslatef(-(x * scaleX + (SIZE / 2) * scaleX), -(y * scaleY + (SIZE / 2) * scaleY), -0);
		glEnable(GL_SCISSOR_TEST);

		if(showEnemies)
		{
			for(Entity e : level.getMonsters())
			{
				Vector2f pos = e.getTransform().getTranslation().getXZ().add(MAP_OFFSET);
				RenderUtil.drawRectangle((x * scaleX) + (pos.getX() * scaleX) - ((playerPos.getX() - 16) * scaleX) - 2, (y * scaleY) + (pos.getY() * scaleY) - ((playerPos.getY() - 16) * scaleY) - 2, (int)scaleX, (int)scaleY, e.getMinimapColor()[0], e.getMinimapColor()[1], e.getMinimapColor()[2]);
			}
		}
		RenderUtil.drawScaledTexturedRectangle(x - playerPos.getX() + 16, y - playerPos.getY() + 16, scaleX, scaleY, tex);
		glScissor((int)(x * scaleX), (int)(y * scaleY), (int)(SIZE * scaleX), (int)(SIZE * scaleY));
		glDisable(GL_SCISSOR_TEST);
		glPopMatrix();
		RenderUtil.drawRectangle(x * scaleX, y * scaleY, (int)(SIZE * scaleX), (int)(SIZE * scaleY), 0, 0, 0);
		RenderUtil.drawRectangle(x * scaleX - (borderSize / 2), y * scaleY - (borderSize / 2), (int)(SIZE * scaleX) + borderSize, (int)(SIZE * scaleY) + borderSize, 255, 255, 255);
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
		return scaleX;
	}

	public void setSize(float i)
	{
		scaleX = scaleY = i;
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
