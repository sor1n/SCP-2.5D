package com.base.engine;

import java.io.File;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.newdawn.slick.opengl.ImageIOImageData;

import com.base.engine.audio.*;

public class Window 
{
	public static Vector2f centerPosition = new Vector2f(Window.getWidth()/2, Window.getHeight()/2);
	
	private static boolean vSync = true;
	
	public static void createWindow(int width, int height, String title)
	{
		Display.setTitle(title);
		try 
		{
			Display.setDisplayMode(new DisplayMode(width, height));
			Display.setIcon(new ByteBuffer[]{new ImageIOImageData().imageToByteBuffer(ImageIO.read(new File("res/Icon.png")), false, false, null)});
			Display.create();
			Keyboard.create();
			Mouse.create();
			SoundSystem.create();
		} 
		catch(Exception e) 
		{
			e.printStackTrace();
		}
	}

	public static void input()
	{
		if(Input.getKeyDown(Options.KEY_FULLSCREEN)) setDisplayMode(getWidth(), getHeight(), !Display.isFullscreen());
		if(Input.getKeyDown(Options.KEY_VSYNC))
		{
			vSync = !vSync;
			Display.setVSyncEnabled(vSync);
			Game.consoleMessage("vSync enabled: " + vSync);
		}
		if(Input.getKeyDown(Options.KEY_SCREENSHOT)) Util.captureScreenshot();
	}


	public static void render()
	{
		Display.update();
	}

	public static void dispose()
	{
		SoundSystem.destroy();
		Display.destroy();
		Keyboard.destroy();
		Mouse.destroy();
	}

	public static boolean isCloseRequested()
	{
		return Display.isCloseRequested();
	}

	public static int getWidth()
	{
		return Display.getDisplayMode().getWidth();
	}

	public static int getHeight()
	{
		return Display.getDisplayMode().getHeight();
	}

	public static String getTitle()
	{
		return Display.getTitle();
	}

	public static void setTitle(String newTitle)
	{
		Display.setTitle(newTitle);
	}

	/**
	 * Set the display mode to be used 
	 * 
	 * @param width The width of the display required
	 * @param height The height of the display required
	 * @param fullscreen True if we want fullscreen mode
	 */
	public static void setDisplayMode(int width, int height, boolean fullscreen)
	{ 
		// return if requested DisplayMode is already set
		if((Display.getDisplayMode().getWidth() == width) && (Display.getDisplayMode().getHeight() == height) && (Display.isFullscreen() == fullscreen)) return;
		try 
		{
			DisplayMode targetDisplayMode = null;
			if(fullscreen)
			{
				DisplayMode[] modes = Display.getAvailableDisplayModes();
				int freq = 0;
				for(int i= 0; i < modes.length; i++)
				{
					DisplayMode current = modes[i];
					if((current.getWidth() == width) && (current.getHeight() == height))
					{
						if((targetDisplayMode == null) || (current.getFrequency() >= freq))
						{
							if((targetDisplayMode == null) || (current.getBitsPerPixel() > targetDisplayMode.getBitsPerPixel()))
							{
								targetDisplayMode = current;
								freq = targetDisplayMode.getFrequency();
							}
						}

						// if we've found a match for bpp and frequence against the 
						// original display mode then it's probably best to go for this one
						// since it's most likely compatible with the monitor
						if((current.getBitsPerPixel() == Display.getDesktopDisplayMode().getBitsPerPixel()) && (current.getFrequency() == Display.getDesktopDisplayMode().getFrequency()))
						{
							targetDisplayMode = current;
							break;
						}
					}
				}
			} 
			else targetDisplayMode = new DisplayMode(width, height);

			if(targetDisplayMode == null)
			{
				Game.consoleError("Failed to find value mode: " + width + "x" + height + " fs=" + fullscreen);
				return;
			}
			Display.setDisplayMode(targetDisplayMode);
			Display.setFullscreen(fullscreen);
		} 
		catch(LWJGLException e)
		{
			Game.consoleError("Unable to setup mode "+ width + "x" + height + " fullscreen=" + fullscreen + e);
		}
	}
}
