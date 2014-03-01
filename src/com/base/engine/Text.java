package com.base.engine;

import org.newdawn.slick.*;

public class Text
{	
	private UnicodeFont font;
	private String text;
	private Color color;

	public Text(String text, int size, Color color)
	{
		this.color = color;
		this.text = text;
		init(size);
	}

	//	public void drawText(Vector2i pos, boolean center)
	//	{
	//		if(center) uFont.drawString(pos.getX() - (uFont.getWidth(text) / 2), pos.getY(), text, color);
	//		else uFont.drawString(pos.getX(), pos.getY(), text, color);
	//	}

	public void init(int size)
	{
		// load a default java font
		java.awt.Font awtFont = new java.awt.Font("Serif", java.awt.Font.BOLD, size);
		font = new UnicodeFont(awtFont);
	}

	public void drawText(Vector2i pos)
	{
		RenderUtil.pushMatrix();
		font.drawString(pos.getX(), pos.getY(), text, color);
		RenderUtil.popMatrix();
	}
}
