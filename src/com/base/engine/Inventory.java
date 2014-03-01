package com.base.engine;

import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;

import com.base.engine.items.Item;

public class Inventory
{
	private Slot[] invBag = new Slot[3];
	private Slot[] invBar = new Slot[3];
	private int selection = 2;

	public Inventory()
	{
		int baseX = (int)Window.centerPosition.getX() - (Window.getWidth() / (invBar.length) - (Slot.getSize() / 2 - 15)), baseY = Slot.getSize() + 10;
		for(int i = 0; i < invBar.length; i++) invBar[i] = new Slot(new Vector2i(baseX - (i * (Slot.getSize() - 12)), baseY - (Slot.getSize())), false);
		for(int i = 0; i < invBag.length; i++) invBag[i] = new Slot(new Vector2i(baseX - (i * (Slot.getSize() - 12)), baseY - 10), true);
		invBar = (Slot[])Util.reverse(invBar);
		invBag = (Slot[])Util.reverse(invBag);
	}

	public void input(float delta)
	{
		for(Slot s : invBar) s.input(delta);
		for(Slot s : invBag) s.input(delta);
		if(Input.getKeyDown(Input.KEY_RIGHT) && selection < invBar.length - 1) selection++;
		if(Input.getKeyDown(Input.KEY_LEFT) && selection > 0) selection--;
		if(Input.getKeyDown(Input.KEY_1)) selection = 0;
		if(Input.getKeyDown(Input.KEY_2)) selection = 1;
		if(Input.getKeyDown(Input.KEY_3)) selection = 2;
	}

	public void update(float delta)
	{
		for(Slot s : invBar) s.update(delta);
		for(Slot s : invBag) s.update(delta);

		for(int i = 0; i < invBar.length; i++)
		{
			if(i == selection) invBar[i].setSelected(true);
			else invBar[i].setSelected(false);
		}
		if(Input.getMouseWheel() != 0)
		{
			selection--;
			if(selection < 0) selection = invBar.length - 1;
		}
	}

	public void render()
	{
		glPushMatrix();
		for(Slot s : invBar) s.render();
		for(Slot s : invBag) s.render();
		glPopMatrix();
	}

	public void addItem(Item item)
	{
		for(int i = 0; i < invBar.length; i++)
		{
			if(invBar[i].getHoldingItem().getID() == 0)
			{
				invBar[i].setItem(item.getID());
				return;
			}
		}
		for(int i = 0; i < invBag.length; i++)
		{
			if(invBag[i].getHoldingItem().getID() == 0)
			{
				invBag[i].setItem(item.getID());
				return;
			}
		}
	}
}
