package com.base.engine;

public class Game 
{
	private static Level level;
	private static boolean isRunning;
	public static int levelNum = 0;
	
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
		level = new Level("level" + levelNum + ".png", "WolfCollection.png");
		Transform.setProjection(70, Window.getWidth(), Window.getHeight(), 0.01f, 1000f);
		Transform.setCamera(level.getPlayer().getCamera());
		isRunning = true;
	}
	
	public static <T> void consoleMessage(T txt)
	{
		System.out.println("[SCP]: " + String.valueOf(txt));
	}
}
