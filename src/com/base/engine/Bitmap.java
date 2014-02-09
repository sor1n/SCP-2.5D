package com.base.engine;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Bitmap
{
	private int width, height;
	private int[] pixels;
	private String path;
	
	public Bitmap(String fileName)
	{
		try
		{
			path = "bitmaps/" + fileName;
			BufferedImage image = ImageIO.read(this.getClass().getClassLoader().getResource("bitmaps/" + fileName));
			width = image.getWidth();
			height = image.getHeight();
			pixels = new int[width * height];
			image.getRGB(0, 0, width, height, pixels, 0, width);
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public Bitmap(int width, int height)
	{
		this.width = width;
		this.height = height;
		this.pixels = new int[width * height];
	}
	
	public Bitmap flipX()
	{
		int[] temp = new int[pixels.length];
		
		for(int i = 0; i < width; i++)
			for(int j = 0; j < height; j++)
			{
				temp[i + j * width] = pixels[(width - i - 1) + j * width];
			}
		pixels = temp;
		return this;
	}
	
	public Bitmap flipY()
	{
		int[] temp = new int[pixels.length];
		
		for(int i = 0; i < width; i++)
			for(int j = 0; j < height; j++)
			{
				temp[i + j * width] = pixels[i + (height - j - 1) * width];
			}
		pixels = temp;
		return this;
	}

	public int getWidth()
	{
		return width;
	}

	public int getHeight()
	{
		return height;
	}

	public int[] getPixels()
	{
		return pixels;
	}
	
	public int getPixel(int x, int y)
	{
		return pixels[x + y * width];
	}
	
	public void setPixel(int x, int y, int value)
	{
		pixels[x + y * width] = value;
	}
	
	public String getPath()
	{
		return path;
	}
}
