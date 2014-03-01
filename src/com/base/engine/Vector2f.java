package com.base.engine;

import java.util.Random;

public class Vector2f 
{
	private float x;
	private float y;
	
	public static final Vector2f ZERO = new Vector2f(0, 0), ONE = new Vector2f(1, 1), NEG = new Vector2f(-1, -1);
	
	public Vector2f(float x, float y)
	{
		this.x = x;
		this.y = y;
	}

	public float length()
	{
		return (float)Math.sqrt(x * x + y * y);
	}
	
	public float dot(Vector2f r)
	{
		return x * r.getX() + y * r.getY();
	}
	
	public Vector2f normalized()
	{
		float length = length();
		
		return new Vector2f(x / length, y / length);
	}
	
	public Vector2f rotate(float angle)
	{
		double rad = Math.toRadians(angle);
		double cos = Math.cos(rad);
		double sin = Math.sin(rad);
		
		return new Vector2f((float)(x * cos - y * sin),(float)(x * sin + y * cos));
	}
	
	public boolean equals(Vector2f r)
	{
		return x == r.getX() && y == r.getY();
	}
	
	public Vector2f lerp(Vector2f dst, float lerpFactor)
	{
		return dst.sub(this).mul(lerpFactor).add(this);
	}
	
	public Vector2f add(Vector2f r)
	{
		return new Vector2f(x + r.getX(), y + r.getY());
	}
	
	public Vector2f add(float r)
	{
		return new Vector2f(x + r, y + r);
	}
	
	public Vector2f sub(Vector2f r)
	{
		return new Vector2f(x - r.getX(), y - r.getY());
	}
	
	public Vector2f sub(float r)
	{
		return new Vector2f(x - r, y - r);
	}
	
	public Vector2f mul(Vector2f r)
	{
		return new Vector2f(x * r.getX(), y * r.getY());
	}
	
	public Vector2f mul(float r)
	{
		return new Vector2f(x * r, y * r);
	}
	
	public Vector2f div(Vector2f r)
	{
		return new Vector2f(x / r.getX(), y / r.getY());
	}
	
	public Vector2f div(float r)
	{
		return new Vector2f(x / r, y / r);
	}
	
	public Vector2f abs()
	{
		return new Vector2f(Math.abs(x), Math.abs(y));
	}
	
	public float cross(Vector2f r)
	{
		return x * r.getY() - y * r.getX();
	}
	
	public String toString()
	{
		return "(" + x + " " + y + ")";
	}
	
	public float getX() 
	{
		return x;
	}

	public void setX(float x) 
	{
		this.x = x;
	}

	public float getY() 
	{
		return y;
	}

	public void setY(float y)
	{
		this.y = y;
	}
	
	public static Vector3f getVector3f(Vector2f a)
	{
		return new Vector3f(a.getX(), a.getY(), 0);
	}
	
	public Vector2i toInt()
	{
		return new Vector2i((int)x, (int)y);
	}

	public static Vector2f random() 
	{
		return new Vector2f(new Random().nextFloat(), new Random().nextFloat());
	}
	
	public boolean bigger(Vector2f object)
	{
		boolean a = getX() > object.getX();
		boolean b = getY() > object.getY();
		return a & b;
	}
	
	public boolean smaller(Vector2f object)
	{
		boolean a = getX() < object.getX();
		boolean b = getY() < object.getY();
		return a & b;
	}

//	public Vector2f positive()
//	{
//		float xx = x;
//		float yy = y;
//		if(xx < 0) xx = -xx;
//		if(yy < 0) yy = -yy;
//		return new Vector2f(xx, yy);
//	}
}
