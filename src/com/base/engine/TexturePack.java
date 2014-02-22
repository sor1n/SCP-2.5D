package com.base.engine;

public class TexturePack 
{
	private String path;
	private Texture texture;
	
	public static final TexturePack TEX_DEFAULT = new TexturePack("WolfCollection.png");
	
	public TexturePack(String path)
	{
		this.path = path;
		this.texture = new Texture(path);
	}
	
	public String getPath()
	{
		return path;
	}
	
	public Texture getTextureSheet()
	{
		return texture;
	}
}
