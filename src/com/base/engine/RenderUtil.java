package com.base.engine;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL32.GL_DEPTH_CLAMP;

import java.io.IOException;

import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

public class RenderUtil
{
	public static void clearScreen()
	{
		//TODO: Stencil Buffer
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	}

	public static void setTextures(boolean enabled)
	{
		if(enabled) glEnable(GL_TEXTURE_2D);
		else glDisable(GL_TEXTURE_2D);
	}
	
	public static void bindTexture(int tex)
	{
		setTextures(true);
		glBindTexture(GL_TEXTURE_2D, tex);
	}
	
	public static void unbindTextures()
	{
		glBindTexture(GL_TEXTURE_2D, 0);
	}
	
	public static void setClearColor(Vector3f color)
	{
		glClearColor(color.getX(), color.getY(), color.getZ(), 1.0f);
	}
	
	public static void initGraphics()
	{
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0, 800, 0, 600, 1, -1);
		glMatrixMode(GL_MODELVIEW);
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		
		glFrontFace(GL_CW);
		glCullFace(GL_BACK);
		glEnable(GL_CULL_FACE);
		glEnable(GL_ALPHA_TEST); //
		glEnable(GL_DEPTH_TEST);
		
		glEnable(GL_BLEND);
		glAlphaFunc(GL_GREATER, 0); //
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		
		glEnable(GL_DEPTH_CLAMP);
		
		glEnable(GL_TEXTURE_2D);
	}
	
	public static String getOpenGLVersion()
	{
		return glGetString(GL_VERSION);
	}
	
	public static void drawRectangle(float x, float y, int sizeX, int sizeY, float red, float green, float blue)
	{
		glPushMatrix();
		setTextures(false);
		glColor3f(red, green, blue);
		glBegin(GL_QUADS);
		glTexCoord2f(0, 0); // top left
		glVertex2f(x, y);
		glTexCoord2f(0, 1); // bottom left 
		glVertex2f(x, sizeY + y);
		glTexCoord2f(1, 1); // bottom right
		glVertex2f(sizeX + x, sizeY + y);
		glTexCoord2f(1, 0); // top right
		glVertex2f(sizeX + x, y);
		glEnd();
		glPopMatrix();
	}
	
	public static void drawTriangle(float x, float y, int sizeX, int sizeY, float r, float g, float b, float angle, float rX, float rY, float rZ)
	{
		glPushMatrix();
		setTextures(false);
		glColor3f(r, g, b);
		glBegin(GL_QUADS);
		glTranslatef(1f, 1f, 1f);
		glLoadIdentity();
		glRotatef(angle, rX, rY, rZ);
		glTexCoord2f(0, 0); // top left
		glVertex2f(x + (sizeX / 2), y);
		glTexCoord2f(0, 1); // bottom left 
		glVertex2f(x, sizeY + y);
		glTexCoord2f(1, 1); // bottom right
		glVertex2f(sizeX + x, sizeY + y);
		glTexCoord2f(1, 0); // top right
		glVertex2f(x + (sizeX / 2), y);
		glEnd();
		glPopMatrix();
	}
	
	public static void drawRectangle(float x, float y, int sizeX, int sizeY, int red, int green, int blue)
	{
		glPushMatrix();
		setTextures(false);
		new Color(red, green, blue).bind();
		glBegin(GL_QUADS);
		glTexCoord2f(0, 0); // top left
		glVertex2f(x, y);
		glTexCoord2f(0, 1); // bottom left 
		glVertex2f(x, sizeY + y);
		glTexCoord2f(1, 1); // bottom right
		glVertex2f(sizeX + x, sizeY + y);
		glTexCoord2f(1, 0); // top right
		glVertex2f(sizeX + x, y);
		glEnd();
		glPopMatrix();
	}
	
	public static void drawTexturedRectangle(float x, float y, org.newdawn.slick.opengl.Texture tex)
	{
		glPushMatrix();
		setTextures(true);
		bindTexture(tex.getTextureID());
		glBegin(GL_QUADS);
		glTexCoord2f(0, 0); // top left
		glVertex2f(x, y);
		glTexCoord2f(0, 1); // bottom left 
		glVertex2f(x, y + tex.getImageHeight());
		glTexCoord2f(1, 1); // bottom right
		glVertex2f(x + tex.getImageWidth(), y + tex.getImageHeight());
		glTexCoord2f(1, 0); // top right
		glVertex2f(x + tex.getImageWidth(), y);
		glEnd();
		glPopMatrix();
	}
	
	public static void drawScaledTexturedRectangle(float x, float y, float width, float height, org.newdawn.slick.opengl.Texture tex)
	{
		glPushMatrix();
		setTextures(true);
		bindTexture(tex.getTextureID());
		glColor3f(1f, 1f, 1f);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glScalef(width, height, 0f);
		glBegin(GL_QUADS);
		glTexCoord2f(0, 0); // top left
		glVertex2f(x, y);
		glTexCoord2f(0, 1); // bottom left 
		glVertex2f(x, y + tex.getImageHeight());
		glTexCoord2f(1, 1); // bottom right
		glVertex2f(x + tex.getImageWidth(), y + tex.getImageHeight());
		glTexCoord2f(1, 0); // top right
		glVertex2f(x + tex.getImageWidth(), y);
		glEnd();
		glPopMatrix();
	}
	
	public static void drawScaledTexturedRectangle(float x, float y, float width, float height, Texture tex)
	{
		glPushMatrix();
		setTextures(true);
		bindTexture(tex.getID());
		glColor3f(1f, 1f, 1f);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glScalef(width, height, 0f);
		glBegin(GL_QUADS);
		glTexCoord2f(0, 0); // top left
		glVertex2f(x, y);
		glTexCoord2f(0, 1); // bottom left 
		glVertex2f(x, y + tex.getHeight());
		glTexCoord2f(1, 1); // bottom right
		glVertex2f(x + tex.getWidth(), y + tex.getHeight());
		glTexCoord2f(1, 0); // top right
		glVertex2f(x + tex.getWidth(), y);
		glEnd();
		glPopMatrix();
	}
	
	public static org.newdawn.slick.opengl.Texture loadPNG(String name)
	{
		try
		{
			org.newdawn.slick.opengl.Texture texture = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("res/" + name));
			return texture;
		} 
		catch(IOException e) {}
		return null;
	}
}
