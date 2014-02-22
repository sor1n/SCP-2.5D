package com.base.engine;

import java.util.Random;

public class Game 
{
	private static Level level;
	private static boolean isRunning;
	public static int levelNum = 0;

	public Game()
	{
		nextLevel();
		isRunning = true;
	}

	public void input(float delta)
	{
		if(isRunning && level != null) level.input(delta);
	}

	public void update(float delta)
	{
		if(isRunning && level != null) level.update(delta);
	}

	public void render()
	{
		if(isRunning && level != null) level.render();
	}
	
	public void renderGUI()
	{
		if(isRunning && level != null) level.renderGUI();
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
		switch(levelNum)
		{
		case 1:
			level = new Level("Room_Begin", TexturePack.TEX_DEFAULT);
			break;
		default:
			int i = new Random().nextInt(Level.randomLevels.length);
			level = new Level(Level.randomLevels[i].getLevelName(), Level.randomLevels[i].getTexturePack(), true);
			break;
		}
	}

	public static <T> void consoleMessage(T txt)
	{
		System.out.println("[SCP]: " + String.valueOf(txt));
	}
	
	public static <T> void consoleError(T txt)
	{
		System.err.println("[SCP]: " + String.valueOf(txt));
	}
	
	public static void crashGame(String text)
	{
		consoleError(text);
		Window.dispose();
		System.exit(1);
	}
}
