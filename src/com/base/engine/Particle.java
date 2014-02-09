package com.base.engine;

public abstract class Particle 
{
	public static final float START = 0, SCALE = 0.7f, SIZEY = SCALE, SIZEX = (float)((double)SIZEY / (1.9310344827586206896551724137931 * 2.0));
	public static final float OFFSET_X = 0f, OFFSET_Y = 0f;
	public static final float TEX_MIN_X = -OFFSET_X, TEX_MAX_X = -1 - OFFSET_X, TEX_MIN_Y = -OFFSET_Y, TEX_MAX_Y = 1 - OFFSET_Y;
	
	protected Transform transform;
	protected Mesh mesh;
	protected Material material;
	
	public Particle(Vector3f pos)
	{
		transform = new Transform(pos, Vector3f.ZERO, Vector3f.ONE);
		Vertex[] vertices = new Vertex[]{new Vertex(new Vector3f(-SIZEX, START, START), new Vector2f(TEX_MAX_X, TEX_MAX_Y)),
				new Vertex(new Vector3f(-SIZEX, SIZEY, START), new Vector2f(TEX_MAX_X, TEX_MIN_Y)),
				new Vertex(new Vector3f(SIZEX, SIZEY, START), new Vector2f(TEX_MIN_X, TEX_MIN_Y)),
				new Vertex(new Vector3f(SIZEX, START, START), new Vector2f(TEX_MIN_X, TEX_MAX_Y))};
		int[] indices = new int[]{0, 1, 2,
				0, 2, 3};
		mesh = new Mesh(vertices, indices);
		material = new Material(new Texture("test.png"));
	}
	
	public void update(float delta)
	{
	}
	
	public void render()
	{
		Shader shader = Game.getLevel().getShader();
		shader.updateUniforms(transform.getTransformation(), transform.getProjectedTransformation(), material);
		mesh.draw();
	}
	
	public void spawn(Level level)
	{
		level.getParticles().add(this);
	}
	
	public void despawn(Level level)
	{
		level.getParticles().remove(this);
	}
	
	public Transform getTransform()
	{
		return transform;
	}
}
