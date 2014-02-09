package com.base.engine;

public class Map
{
	public static final float START = 0, SCALE = 0.0625f, SIZEY = SCALE, SIZEX = (float)((double)SIZEY / (1.0379746835443037974683544303797 * 2.0));
	public static final float OFFSET_X = -0.1f, OFFSET_Y = 0f;
	public static final float TEX_MIN_X = -OFFSET_X, TEX_MAX_X = -1 - OFFSET_X, TEX_MIN_Y = -OFFSET_Y, TEX_MAX_Y = 1 - OFFSET_Y, GUN_DISTANCE = 0.105f, GUN_OFFSET = -0.0775f;

	private Transform transform;
	private Level level;
	private Mesh mesh;
	protected Material material;
	protected float width, height;
	protected Texture texture;

	public Map(Transform transform, Level level, float width, float height)
	{
		this.width = width;
		this.height = height;
		this.level = level;
		Vertex[] vertices = new Vertex[]{new Vertex(new Vector3f(-SIZEX, START, START), new Vector2f(TEX_MAX_X, TEX_MAX_Y)),
				new Vertex(new Vector3f(-SIZEX, SIZEY, START), new Vector2f(TEX_MAX_X, TEX_MIN_Y)),
				new Vertex(new Vector3f(SIZEX, SIZEY, START), new Vector2f(TEX_MIN_X, TEX_MIN_Y)),
				new Vertex(new Vector3f(SIZEX, START, START), new Vector2f(TEX_MIN_X, TEX_MAX_Y))};
		int[] indices = new int[]{0, 1, 2,
				0, 2, 3};
		mesh = new Mesh(vertices, indices);
		material = new Material(new Texture("test.png"));
		this.transform = transform;
		texture = new Texture("test.png");
	}

	public void update()
	{
		
	}

	public void render()
	{
		if(level != null)
		{
			//Shader shader = level.getShader();

			mesh.draw();
		}
	}

	public Transform getTransform()
	{
		return transform;
	}

	public Level getLevel()
	{
		return level;
	}
}
