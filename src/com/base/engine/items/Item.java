package com.base.engine.items;

import java.util.ArrayList;
import java.util.List;

import com.base.engine.RenderUtil;
import com.base.engine.Slot;
import com.base.engine.Texture;

public class Item 
{
	protected Texture texture = null;
	protected int id;
	
	protected static final List<Item> items = new ArrayList<Item>();
	
	public static final Item air = new Item(0);
	public static final Item medkit = new Item(1).setTexture(new Texture("MEDIA0.png"));
	
	public Item(int id)
	{
		this.id = id;
		items.add(this);
	}
	
	public void update(float delta)
	{
		
	}
	
	public void input(float delta)
	{
		
	}
	
	public void render(int x, int y)
	{
		if(texture != null) RenderUtil.drawTexturedRectangle(x, y, texture);
	}
	
	public int getID()
	{
		return id;
	}
	
	public Item setTexture(Texture texture)
	{
		this.texture = texture;
		return this;
	}
	
	public Texture getTexture()
	{
		return texture;
	}
	
	public static Item getItem(int id)
	{
		if(id < 0) id = 0;
		if(id > items.size()) id = items.size();
		return items.get(id);
	}
	
	public void onUse(Slot slot)
	{
		slot.setItem(air);
	}
}
