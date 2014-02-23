package com.base.engine;

import java.util.Iterator;
import java.util.List;

public class Game 
{
	private static Level level;
	private static boolean isRunning;
	public static int levelNum = 0;

	public Game()
	{
		nextLevel(0);
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

	public static void nextLevel(int lvl)
	{
		if(lvl < 0) lvl = 0;
		levelNum = lvl;
//		switch(levelNum)
//		{
//		case 1:
//			level = new Level("Room_Begin", TexturePack.TEX_DEFAULT);
//			break;
//		default:
//			int i = new Random().nextInt(Level.randomLevels.length);
//			level = new Level(Level.randomLevels[i].getLevelName(), Level.randomLevels[i].getTexturePack(), true);
//			break;
//		}
		if(Level.doesLevelExist(lvl)) level = new Level("Room_" + lvl, TexturePack.TEX_DEFAULT);
	}
	
//	public static void prevLevel(int lvl)
//	{
//		//levelNum--;
//		level = new Level(Level.levels.get(lvl).getLevelName(), Level.levels.get(lvl).getTexturePack());
//	}

	public static <T> void consoleMessage(T txt)
	{
		System.out.println("[SCP]: " + String.valueOf(txt));
	}
	
	public static <T> void consoleError(T txt)
	{
		System.err.println("[SCP]: " + String.valueOf(txt));
	}
	
	public static <T> void consoleIterator(List<?> txt)
	{
		Iterator<?> iterator = txt.iterator();
		while(iterator.hasNext()) System.out.println("[SCP]: " + String.valueOf(iterator.next()));
	}
	
	public static <T> void consoleIterator(List<?> txt, T extraText)
	{
		Iterator<?> iterator = txt.iterator();
		while(iterator.hasNext()) System.out.println("[SCP]: " + String.valueOf(iterator.next() + " | " + extraText));
	}
	
	public static void crashGame(String text)
	{
		consoleError(text);
		Window.dispose();
		System.exit(1);
	}
}
