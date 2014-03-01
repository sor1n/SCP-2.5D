package com.base.engine.itementities;

import org.newdawn.slick.Color;

import com.base.engine.Game;
import com.base.engine.Input;
import com.base.engine.MeshRenderer;
import com.base.engine.Options;
import com.base.engine.Text;
import com.base.engine.Transform;
import com.base.engine.Vector3f;
import com.base.engine.entities.Entity;
import com.base.engine.items.Item;

public abstract class ItemEntity
{
	public static final float PICKUP_DISTANCE = 0.75f;
	protected Transform transform;
	protected MeshRenderer meshRenderer;
	protected String name;
	protected Item item;
	protected boolean isNear = false;
	protected Text pickupText;
	
	protected int[] miniMapColor;

	public ItemEntity(Item item, String name)
	{
		this.item = item;
		this.name = name;
		pickupText = new Text(Input.getKeyName(Options.KEY_INTERACT) + " to pick up " + name, 50, Color.white);
	}

	public void input(float delta)
	{
		if(meshRenderer != null) meshRenderer.input(transform, delta);
		
		if(transform != null)
		{
			Vector3f dirToCam = Transform.getCamera().getPos().sub(transform.getTranslation());
			if(dirToCam.length() < PICKUP_DISTANCE && Input.getKeyDown(Input.KEY_E)) onPickUp();
		}
	}

	public void update(float delta)
	{
		if(meshRenderer != null) meshRenderer.update(transform, delta);
		if(transform != null)
		{
			Entity.faceCamera(transform);
			Vector3f dirToCam = Transform.getCamera().getPos().sub(transform.getTranslation());
			if(dirToCam.length() < PICKUP_DISTANCE) onNear();
			else isNear = false;
		}
		else isNear = false;
	}

	public void render()
	{
		if(meshRenderer != null && transform != null) meshRenderer.render(transform);
	}
	
	public void render2D()
	{
//		if(isNear) pickupText.drawText(0, 0); //FIXME
	}

	public void onNear()
	{
		isNear = true;
	}

	public void onPickUp()
	{
		Game.getLevel().getPlayer().getInventory().addItem(getItem());
		removeItem();
	}

	public String getName()
	{
		return name;
	}

	public Item getItem()
	{
		return item;
	}
	
	public void removeItem()
	{
		Game.getLevel().getItemEntities().remove(this);
	}

	public Transform getTransform()
	{
		return transform;
	}
	
	public int[] getMinimapColor()
	{
		return miniMapColor;
	}
}
