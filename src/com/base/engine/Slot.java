package com.base.engine;

import com.base.engine.items.Item;

public class Slot
{
	private Vector2i pos;
	public static final int BORDER_SIZE = 4;
	private static final float scale = 3.5f, ITEM_SIZE = 2f;
	private static final int size = 32;
	private static Texture texture, select;
	private Texture tex;
	private boolean hideable, show;
	private int id;
	private int offset = 0;

	public Slot(Vector2i pos, boolean hideable)
	{
		this.hideable = hideable;
		this.pos = pos;
		if(texture == null) texture = new Texture("Tile_Cell.png");
		if(select == null) select = new Texture("Tile_Select.png");
		tex = texture;
		id = 0;
	}

	public void input(float delta)
	{
		show = Input.getKey(Options.KEY_OPENINV);
		if(((show && hideable) || !hideable) && isSelected() && Input.getMouseDown(1)) Item.getItem(id).onUse(this);
	}

	public void update(float delta)
	{
		if(isSelected()) offset = -1;
		else offset = 0;
	}

	public void render()
	{
		if((show && hideable) || !hideable)
		{
			if(id != 0) RenderUtil.drawScaledTexturedRectangle((pos.getX() / ITEM_SIZE) + (size / 2) - (BORDER_SIZE * ITEM_SIZE), (pos.getY() / ITEM_SIZE) + (size / 2) - (BORDER_SIZE * ITEM_SIZE), ITEM_SIZE, ITEM_SIZE, Item.getItem(id).getTexture());
			RenderUtil.drawScaledTexturedRectangle(pos.getX() / scale, pos.getY() / scale + offset, scale, scale, tex);
		}
	}

	public Vector2i getPosition()
	{
		return pos;
	}

	public static int getSize()
	{
		return (int) (size * scale);
	}

	public void setItem(Item item)
	{
		id = item.getID();
	}
	
	public void setItem(int id)
	{
		this.id = id;
	}

	public Item getHoldingItem()
	{
		return Item.getItem(id);
	}

	public void setSelected(boolean b)
	{
		if(b) tex = select;
		else tex = texture;
	}

	public boolean isSelected()
	{
		return (tex.getFileName().equalsIgnoreCase(select.getFileName()));
	}
}
