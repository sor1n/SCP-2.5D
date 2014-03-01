package com.base.engine.itementities;

import com.base.engine.Material;
import com.base.engine.Mesh;
import com.base.engine.MeshRenderer;
import com.base.engine.Texture;
import com.base.engine.Transform;
import com.base.engine.Vector2f;
import com.base.engine.Vector3f;
import com.base.engine.Vertex;
import com.base.engine.items.Item;

public class Medkit extends ItemEntity
{
	public static final int HEAL_AMOUNT = 25;

	public static final float START = 0, SCALE = 0.25f, SIZEY = SCALE, SIZEX = (float)((double)SIZEY / (1.0379746835443037974683544303797 * 2.0));
	public static final float OFFSET_X = 0f, OFFSET_Y = 0f;
	public static final float TEX_MIN_X = -OFFSET_X, TEX_MAX_X = -1 - OFFSET_X, TEX_MIN_Y = -OFFSET_Y, TEX_MAX_Y = 1 - OFFSET_Y;

	private static Mesh mesh;
	private static Material material;

	public Medkit(Vector3f pos)
	{
		super(Item.medkit, "Medkit");
		if(material == null) material = new Material(new Texture("MEDIA0.png"));
		if(mesh == null)
		{
			Vertex[] vertices = new Vertex[]{new Vertex(new Vector3f(-SIZEX, START, START), new Vector2f(TEX_MAX_X, TEX_MAX_Y)),
					new Vertex(new Vector3f(-SIZEX, SIZEY, START), new Vector2f(TEX_MAX_X, TEX_MIN_Y)),
					new Vertex(new Vector3f(SIZEX, SIZEY, START), new Vector2f(TEX_MIN_X, TEX_MIN_Y)),
					new Vertex(new Vector3f(SIZEX, START, START), new Vector2f(TEX_MIN_X, TEX_MAX_Y))};
			int[] indices = new int[]{0, 1, 2,
					0, 2, 3};
			mesh = new Mesh(vertices, indices);
		}
		meshRenderer = new MeshRenderer(mesh, material);
		transform = new Transform();
		transform.setTranslation(pos);
		miniMapColor = new int[]{255, 0, 0};
	}
	
	public void onPickUp()
	{
		super.onPickUp();
//		Player player = Game.getLevel().getPlayer();
//		if(player.getHealth() < Player.MAX_HEALTH)
//		{
//			player.damage(-HEAL_AMOUNT);
//			removeItem();
//		}
	}
}
