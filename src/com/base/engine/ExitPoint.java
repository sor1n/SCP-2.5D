package com.base.engine;

public class ExitPoint
{
	private Vector3f exitPoint;
	private int level;
	
	public ExitPoint(Vector3f point, int level)
	{
		this.level = level;
		this.exitPoint = point;
	}
	
	public Vector3f getExitPoint()
	{
		return exitPoint;
	}

	public int getLevel()
	{
		return level;
	}
}
