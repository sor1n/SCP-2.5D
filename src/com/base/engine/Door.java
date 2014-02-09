package com.base.engine;

public class Door
{
	public static final float LENGTH = 1, WIDTH = 0.125f, HEIGHT = 1, START = 0;
	public static final double TIME_TO_OPEN = 0.3, CLOSE_DELAY = 2.0;
	public static final float OPEN_DISTANCE = 1.3f, DOOR_MOVE_AMOUNT = 0.9f;

	private Vector3f openPos, closePos;

	private Transform transform;
	private MeshRenderer meshRenderer;
	
	private boolean isOpening = false;
	private double openingStartTime;
	private double openTime;
	private double closeTime;
	private double closingStartTime;

	public Door(Transform transform, Material material, Vector3f openPos)
	{
		closePos = transform.getTranslation().mul(1);
		this.openPos = openPos;
		Vertex[] vertices = new Vertex[]{new Vertex(new Vector3f(START, START, START), new Vector2f(0.5f, 1)),
				new Vertex(new Vector3f(START, HEIGHT, START), new Vector2f(0.5f, 0.75f)),
				new Vertex(new Vector3f(LENGTH, HEIGHT, START), new Vector2f(0.75f, 0.75f)),
				new Vertex(new Vector3f(LENGTH, START, START), new Vector2f(0.75f, 1)),

				new Vertex(new Vector3f(START, START, START), new Vector2f(0.73f, 1)),
				new Vertex(new Vector3f(START, HEIGHT, START), new Vector2f(0.73f, 0.75f)),
				new Vertex(new Vector3f(START, HEIGHT, WIDTH), new Vector2f(0.75f, 0.75f)),
				new Vertex(new Vector3f(START, START, WIDTH), new Vector2f(0.75f, 1)),

				new Vertex(new Vector3f(START, START, WIDTH), new Vector2f(0.5f, 1)),
				new Vertex(new Vector3f(START, HEIGHT, WIDTH), new Vector2f(0.5f, 0.75f)),
				new Vertex(new Vector3f(LENGTH, HEIGHT, WIDTH), new Vector2f(0.75f, 0.75f)),
				new Vertex(new Vector3f(LENGTH, START, WIDTH), new Vector2f(0.75f, 1)),

				new Vertex(new Vector3f(LENGTH, START, START), new Vector2f(0.73f, 1)),
				new Vertex(new Vector3f(LENGTH, HEIGHT, START), new Vector2f(0.73f, 0.75f)),
				new Vertex(new Vector3f(LENGTH, HEIGHT, WIDTH), new Vector2f(0.75f, 0.75f)),
				new Vertex(new Vector3f(LENGTH, START, WIDTH), new Vector2f(0.75f, 1))};
		int[] indices = new int[]{0, 1, 2,
				0, 2, 3,

				6, 5, 4,
				7, 6, 4,

				10, 9, 8,
				11, 10, 8,

				12, 13, 14,
				12, 14, 15};
		Mesh mesh = new Mesh(vertices, indices);
		Material mat = material;
		meshRenderer = new MeshRenderer(mesh, mat);
		this.transform = transform;
	}

	public void open()
	{
		if(isOpening) return;
		openingStartTime = (double)Time.getTime();
		openTime = openingStartTime + TIME_TO_OPEN;
		closingStartTime = openTime + CLOSE_DELAY;
		closeTime = closingStartTime + TIME_TO_OPEN;
		isOpening = true;
	}

	public void update(float delta)
	{
		meshRenderer.update(transform, delta);
		if(isOpening)
		{
			double time = (double)Time.getTime();
			if(time < openTime) transform.setTranslation(closePos.lerp(openPos, (float)((time - openingStartTime) / TIME_TO_OPEN)));
			else if(time < closingStartTime) transform.setTranslation(openPos);
			else if(time < closeTime) transform.setTranslation(openPos.lerp(closePos, (float)((time - closingStartTime) / TIME_TO_OPEN)));
			else 
			{
				transform.setTranslation(closePos);
				isOpening = false;
			}
		}
	}

	public void render()
	{
		meshRenderer.render(transform);
	}

	public Transform getTransform()
	{
		return transform;
	}

	public Vector2f getDoorSize()
	{
		if(getTransform().getRotation().getY() == 90) return new Vector2f(Door.WIDTH, Door.LENGTH);
		else return new Vector2f(Door.LENGTH, Door.WIDTH);
	}
}
