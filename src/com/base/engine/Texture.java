package com.base.engine;

import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_REPEAT;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_RGBA8;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameterf;
import static org.lwjgl.opengl.GL11.glTexParameteri;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

public class Texture
{
	private int id, width, height;
	private String fileName;
	private boolean outside;
	private BufferedImage image;
	private int[] pixels;

	public Texture(BufferedImage img)
	{
		this.image = img;
		this.width = image.getWidth();
		this.height = image.getHeight();
	}

	public Texture(int width, int height)
	{
		this.width = width;
		this.height = height;
		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
	}

	public Texture(String fileName)
	{
		this(fileName, false);
	}

	public Texture(String fileName, boolean outside)
	{
		this.outside = outside;
		this.fileName = fileName;
		id = loadTexture();
	}

	public void bind()
	{
		glBindTexture(GL_TEXTURE_2D, id);
	}

	public int getID()
	{
		return id;
	}

	private int loadTexture()
	{
		//String[] splitArray = fileName.split("\\.");
		//String ext = splitArray[splitArray.length - 1];

		try
		{
			if(outside) image = ImageIO.read(this.getClass().getClassLoader().getResource(fileName));
			else image = ImageIO.read(this.getClass().getClassLoader().getResource("textures/" + fileName));

			width = image.getWidth();
			height = image.getHeight();

			boolean hasAlpha = image.getColorModel().hasAlpha();

			pixels = image.getRGB(0, 0, image.getWidth(),
					image.getHeight(), null, 0, image.getWidth());

			ByteBuffer buffer = Util.createByteBuffer(image.getWidth() * image.getHeight() * 4);

			for (int y = 0; y < image.getHeight(); y++)
			{
				for (int x = 0; x < image.getWidth(); x++)
				{
					int pixel = pixels[y * image.getWidth() + x];

					buffer.put((byte) ((pixel >> 16) & 0xFF));
					buffer.put((byte) ((pixel >> 8) & 0xFF));
					buffer.put((byte) ((pixel >> 0) & 0xFF));
					if (hasAlpha)
						buffer.put((byte) ((pixel >> 24) & 0xFF));
					else
						buffer.put((byte) (0xFF));
				}
			}

			buffer.flip();

			//                        this.width = image.getWidth();
			//                        this.height = image.getHeight();
			//                        this.id = Engine.getRenderer().createTexture(width, height, buffer, true, true);
			//                        this.frameBuffer = 0;
			//                        this.pixels = null;

			int texture = glGenTextures();
			glBindTexture(GL_TEXTURE_2D, texture);

			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

			glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
			glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
			glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, image.getWidth(), image.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);

			return texture;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.exit(1);
		}

		return 0;
	}

	public int flipX()
	{
		try
		{
			boolean hasAlpha = image.getColorModel().hasAlpha();

			pixels = image.getRGB(0, 0, image.getWidth(),
					image.getHeight(), null, 0, image.getWidth());

			ByteBuffer buffer = Util.createByteBuffer(image.getWidth() * image.getHeight() * 4);

			for (int y = 0; y < image.getHeight(); y++)
			{
				for (int x = 0; x < image.getWidth(); x++)
				{
					int pixel = pixels[(image.getWidth() - x - 1) + y * image.getHeight()];

					buffer.put((byte) ((pixel >> 16) & 0xFF));
					buffer.put((byte) ((pixel >> 8) & 0xFF));
					buffer.put((byte) ((pixel >> 0) & 0xFF));
					if (hasAlpha)
						buffer.put((byte) ((pixel >> 24) & 0xFF));
					else
						buffer.put((byte) (0xFF));
				}
			}

			buffer.flip();

			//                        this.width = image.getWidth();
			//                        this.height = image.getHeight();
			//                        this.id = Engine.getRenderer().createTexture(width, height, buffer, true, true);
			//                        this.frameBuffer = 0;
			//                        this.pixels = null;

			int texture = glGenTextures();
			glBindTexture(GL_TEXTURE_2D, texture);

			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

			glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
			glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
			glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, image.getWidth(), image.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);

			return texture;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.exit(1);
		}

		return 0;
	}

	public int flipY()
	{
		try
		{
			boolean hasAlpha = image.getColorModel().hasAlpha();

			pixels = image.getRGB(0, 0, image.getWidth(),
					image.getHeight(), null, 0, image.getWidth());

			ByteBuffer buffer = Util.createByteBuffer(image.getWidth() * image.getHeight() * 4);

			for (int y = 0; y < image.getHeight(); y++)
			{
				for (int x = 0; x < image.getWidth(); x++)
				{
					int pixel = pixels[x + (image.getHeight() - y - 1) * image.getWidth()];

					buffer.put((byte) ((pixel >> 16) & 0xFF));
					buffer.put((byte) ((pixel >> 8) & 0xFF));
					buffer.put((byte) ((pixel >> 0) & 0xFF));
					if (hasAlpha)
						buffer.put((byte) ((pixel >> 24) & 0xFF));
					else
						buffer.put((byte) (0xFF));
				}
			}

			buffer.flip();

			//                        this.width = image.getWidth();
			//                        this.height = image.getHeight();
			//                        this.id = Engine.getRenderer().createTexture(width, height, buffer, true, true);
			//                        this.frameBuffer = 0;
			//                        this.pixels = null;

			int texture = glGenTextures();
			glBindTexture(GL_TEXTURE_2D, texture);

			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

			glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
			glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
			glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, image.getWidth(), image.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);

			return texture;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.exit(1);
		}

		return 0;
	}

	public int getWidth()
	{
		return width;
	}

	public int getHeight()
	{
		return height;
	}

	public void setID(int id)
	{
		this.id = id;
	}

	public Vector2i getBluePixelIndex(int b)
	{
		pixels = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
		for(int x = 0; x < width; x++)
			for(int y = 0; y < height; y++)
			{
				if((pixels[x + y * width] & 0x0000FF) == b) return new Vector2i(x, y);
			}
		return Vector2i.NEG;
	}

	public int setPixels(int[] id, int size)
	{
		try
		{
			width = image.getWidth();
			height = image.getHeight();
			boolean hasAlpha = image.getColorModel().hasAlpha();

			ByteBuffer buffer = Util.createByteBuffer(image.getWidth() * image.getHeight() * 4);

			for (int y = 0; y < image.getHeight(); y++)
			{
				for (int x = 0; x < image.getWidth(); x++)
				{
					int pixel = id[x + y * size];

					buffer.put((byte) ((pixel >> 16) & 0xFF));
					buffer.put((byte) ((pixel >> 8) & 0xFF));
					buffer.put((byte) ((pixel >> 0) & 0xFF));
					if (hasAlpha)
						buffer.put((byte) ((pixel >> 24) & 0xFF));
					else
						buffer.put((byte) (0xFF));
				}
			}
			buffer.flip();
			int texture = glGenTextures();
			glBindTexture(GL_TEXTURE_2D, texture);

			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

			glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
			glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
			glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, image.getWidth(), image.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);

			return texture;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.exit(1);
		}
		return 0;
	}
	
	public int[] getPixels()
	{
		return pixels;
	}
	
	public void setPixels(int[] pix)
	{
		pixels = pix;
	}
	
	public String getFileName()
	{
		return fileName;
	}
}