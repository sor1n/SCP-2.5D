package com.base.engine;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

public class Util
{	
	public static float clamp(float a, float min, float max)
	{
		if(a < min) return min;
		if(a > max) return max;
		else return a;
	}
	
	public static void captureScreenshot()
	{
		GL11.glReadBuffer(GL11.GL_FRONT);
		int width = Display.getDisplayMode().getWidth();
		int height= Display.getDisplayMode().getHeight();
		int bpp = 4; // Assuming a 32-bit display with a byte each for red, green, blue, and alpha.
		ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * bpp);
		GL11.glReadPixels(0, 0, width, height, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
		new File("C:/Users/" + System.getProperty("user.name") + "/PixelBolt/").mkdir();	
		new File("C:/Users/" + System.getProperty("user.name") + "/PixelBolt/" + Window.getTitle() + "/").mkdir();	
		File file = new File("C:/Users/" + System.getProperty("user.name") + "/PixelBolt/" + Window.getTitle() + "/SCP-TE - " + getDate() + ".png"); // The file to save to.
		try
		{
			file.createNewFile();
		}
		catch(IOException e1) {e1.printStackTrace();}
		String format = "PNG"; // Example: "PNG" or "JPG"
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		for(int x = 0; x < width; x++)
			for(int y = 0; y < height; y++)
			{
				int i = (x + (width * y)) * bpp;
				int r = buffer.get(i) & 0xFF;
				int g = buffer.get(i + 1) & 0xFF;
				int b = buffer.get(i + 2) & 0xFF;
				image.setRGB(x, height - (y + 1), (0xFF << 24) | (r << 16) | (g << 8) | b);
			}
		try
		{
			ImageIO.write(image, format, file);
			Game.consoleMessage("Screenshot made: " + file.getAbsolutePath());
		} 
		catch(IOException e) {e.printStackTrace();}
	}
	
	public static String getDate()
	{
		Calendar cal = Calendar.getInstance();
		cal.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("y-MMM-d-HH.mm.s.S");
		return String.valueOf(sdf.format(cal.getTime()));
	}
	
	public static Object[] reverse(Object[] arr)
	{
        List <Object> list = Arrays.asList(arr);
        Collections.reverse(list);
        return list.toArray();
    }
	
	public static int clamp(int a, int min, int max)
	{
		if(a > max) return max;
		else if(a < min) return min;
		else return a;
	}
	
	public static int getRandomInt()
	{
		return new Random().nextInt();
	}
	
	public static FloatBuffer createFloatBuffer(int size)
	{
		return BufferUtils.createFloatBuffer(size);
	}

	public static IntBuffer createIntBuffer(int size)
	{
		return BufferUtils.createIntBuffer(size);
	}

	public static ByteBuffer createByteBuffer(int size)
	{
		return BufferUtils.createByteBuffer(size);
	}

	public static IntBuffer createFlippedBuffer(int... values)
	{
		IntBuffer buffer = createIntBuffer(values.length);
		buffer.put(values);
		buffer.flip();

		return buffer;
	}

	public static FloatBuffer createFlippedBuffer(Vertex[] vertices)
	{
		FloatBuffer buffer = createFloatBuffer(vertices.length * Vertex.SIZE);

		for(int i = 0; i < vertices.length; i++)
		{
			buffer.put(vertices[i].getPos().getX());
			buffer.put(vertices[i].getPos().getY());
			buffer.put(vertices[i].getPos().getZ());
			buffer.put(vertices[i].getTexCoord().getX());
			buffer.put(vertices[i].getTexCoord().getY());
			buffer.put(vertices[i].getNormal().getX());
			buffer.put(vertices[i].getNormal().getY());
			buffer.put(vertices[i].getNormal().getZ());
		}

		buffer.flip();

		return buffer;
	}

	public static FloatBuffer createFlippedBuffer(Matrix4f value)
	{
		FloatBuffer buffer = createFloatBuffer(4 * 4);

		for(int i = 0; i < 4; i++)
			for(int j = 0; j < 4; j++)
				buffer.put(value.get(i, j));

		buffer.flip();

		return buffer;
	}

	public static String[] removeEmptyStrings(String[] data)
	{
		ArrayList<String> result = new ArrayList<String>();

		for(int i = 0; i < data.length; i++)
			if(!data[i].equals(""))
				result.add(data[i]);

		String[] res = new String[result.size()];
		result.toArray(res);

		return res;
	}

	public static int[] toIntArray(Integer[] data)
	{
		int[] result = new int[data.length];

		for(int i = 0; i < data.length; i++)
			result[i] = data[i].intValue();

		return result;
	}
	
	public static int getZeroOneOrMinus(int i)
	{
		if(i == 0) return 0;
		if(i > 0) return 1;
		else return -1;
	}
	
	public static boolean intBetween(int a, int min, int max)
	{
		if(a > min && a < max) return true;
		else return false;
	}
	
	public static boolean floatBetween(float a, float min, float max)
	{
		if(a > min && a < max) return true;
		else return false;
	}
}
