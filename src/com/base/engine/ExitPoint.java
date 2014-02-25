package com.base.engine;

public class ExitPoint
{
	private Vector3f exitPoint;
	private int level, dir;
	
	public ExitPoint(Vector3f point, int level, int dir)
	{
		this.dir = dir;
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
	
	public int getDir()
	{
		if(dir <= 0) return 0;
		else if(dir == 1) return 90;
		else if(dir == 2) return 180;
		else return 270;
	}
}
