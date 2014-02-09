package com.base.engine;

public class MeshRenderer
{
	private Mesh mesh;
	private Material material;
	
	public MeshRenderer(Mesh mesh, Material material)
	{
		this.mesh = mesh;
		this.material = material;
	}
	
	public void render(Transform transform)
	{
		Shader shader = Game.getLevel().getShader();
		shader.updateUniforms(transform.getTransformation(), transform.getProjectedTransformation(), material);
		mesh.draw();
	}

	public void update(Transform transform, float delta) {}
	public void input(Transform transform, float delta) {}
}
