package com.base.engine;

import java.util.Random;

public class Game 
{
	private static Level level;
	private static boolean isRunning;
	public static int levelNum = 0;
	public static int roomSize = 2;

	public Game()
	{
		nextLevel();
	}

	public void input()
	{
		level.input();
	}

	public void update()
	{
		if(isRunning) level.update();
	}

	public void render()
	{
		if(isRunning) level.render();
	}

	public static void setRunning(boolean b)
	{
		isRunning = b;
	}

	public static Level getLevel()
	{
		return level;
	}

	public static void nextLevel()
	{
		levelNum++;
		//level = new Level("level" + levelNum + ".png", "WolfCollection.png");
		switch(levelNum)
		{
		case 1:
			level = new Level("Room_Begin.png", "WolfCollection.png");
			break;
		default:
			int i = new Random().nextInt(roomSize) + 1;
			level = new Level("Room_" + i + ".png", "WolfCollection.png");
			break;
		}
		Transform.setProjection(70, Window.getWidth(), Window.getHeight(), 0.01f, 1000f);
		Transform.setCamera(level.getPlayer().getCamera());
		isRunning = true;
	}

	public static <T> void consoleMessage(T txt)
	{
		System.out.println("[SCP]: " + String.valueOf(txt));
	}
}
