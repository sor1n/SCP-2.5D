package com.base.engine;

import java.util.*;

import com.base.engine.audio.SoundSystem;

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
		Window.input();
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
		if(Level.doesLevelExist(lvl)) level = new Level("Room_" + lvl, TexturePack.TEX_DEFAULT);
	}
	
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
		SoundSystem.destroy();
		consoleError(text);
		new Exception().printStackTrace();
		System.exit(1);
	}
}
