package com.base.engine;

public class Vector3i
{
	private int x;
	private int y;
	private int z;
	
	public static final Vector3i ZERO = new Vector3i(0, 0, 0), ONE = new Vector3i(1, 1, 1);
	
	public Vector3i(int x, int y, int z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public int length()
	{
		return (int)Math.sqrt(x * x + y * y + z * z);
	}
	
	public int max()
	{
		return Math.max(x, Math.max(y, z));
	}
	
	public int dot(Vector3i r)
	{
		return x * r.getX() + y * r.getY() + z * r.getZ();
	}
	
	public Vector3i cross(Vector3i r)
	{
		int x_ = y * r.getZ() - z * r.getY();
		int y_ = z * r.getX() - x * r.getZ();
		int z_ = x * r.getY() - y * r.getX();
		
		return new Vector3i(x_, y_, z_);
	}
	
	public Vector3i normalized()
	{
		int length = length();
		
		return new Vector3i(x / length, y / length, z / length);
	}
	
	public Vector3i rotate(int angle, Vector3i axis)
	{
		int sinHalfAngle = (int)Math.sin(Math.toRadians(angle / 2));
		int cosHalfAngle = (int)Math.cos(Math.toRadians(angle / 2));
		
		int rX = axis.getX() * sinHalfAngle;
		int rY = axis.getY() * sinHalfAngle;
		int rZ = axis.getZ() * sinHalfAngle;
		int rW = cosHalfAngle;
		
		Quaternion rotation = new Quaternion(rX, rY, rZ, rW);
		Quaternion conjugate = rotation.conjugate();
		
		Quaternion w = rotation.mul(this).mul(conjugate);
		
		return new Vector3i((int)w.getX(), (int)w.getY(), (int)w.getZ());
	}
	
	public Vector3i rotate(Quaternion rotation)
	{
		Quaternion conjugate = rotation.conjugate();
		Quaternion w = rotation.mul(this).mul(conjugate);
		return new Vector3i((int)w.getX(), (int)w.getY(), (int)w.getZ());
	}
	
	public Vector3i lerp(Vector3i dst, int lerpFactor)
	{
		return dst.sub(this).mul(lerpFactor).add(this);
	}
	
	public boolean equals(Vector3i other)
	{
		return x == other.getX() && y == other.getY() && z == other.getZ();
	}
	
	public Vector3i add(Vector3i r)
	{
		return new Vector3i(x + r.getX(), y + r.getY(), z + r.getZ());
	}
	
	public Vector3i add(int r)
	{
		return new Vector3i(x + r, y + r, z + r);
	}
	
	public Vector3i sub(Vector3i r)
	{
		return new Vector3i(x - r.getX(), y - r.getY(), z - r.getZ());
	}
	
	public Vector3i sub(int r)
	{
		return new Vector3i(x - r, y - r, z - r);
	}
	
	public Vector3i mul(Vector3i r)
	{
		return new Vector3i(x * r.getX(), y * r.getY(), z * r.getZ());
	}
	
	public Vector3i mul(int r)
	{
		return new Vector3i(x * r, y * r, z * r);
	}
	
	public Vector3i div(Vector3i r)
	{
		return new Vector3i(x / r.getX(), y / r.getY(), z / r.getZ());
	}
	
	public Vector3i div(int r)
	{
		return new Vector3i(x / r, y / r, z / r);
	}
	
	public Vector3i abs()
	{
		return new Vector3i(Math.abs(x), Math.abs(y), Math.abs(z));
	}
	
	public String toString()
	{
		return "(" + x + " " + y + " " + z + ")";
	}
	
	public int getX() 
	{
		return x;
	}

	public void setX(int x) 
	{
		this.x = x;
	}

	public int getY() 
	{
		return y;
	}

	public void setY(int y) 
	{
		this.y = y;
	}
	
	public void addX(int x)
	{
		this.x += x;
	}
	
	public void addY(int y)
	{
		this.y += y;
	}
	
	public void addZ(int z)
	{
		this.z += z;
	}

	public int getZ() 
	{
		return z;
	}

	public void setZ(int z) 
	{
		this.z = z;
	}
	
	public Vector2f getXY()
	{
		return new Vector2f(x, y);
	}
	
	public Vector2f getYZ()
	{
		return new Vector2f(y, z);
	}
	
	public Vector2f getXZ()
	{
		return new Vector2f(x, z);
	}

	public Vector3i set(Vector3i translation)
	{
		this.x = translation.getX();
		this.y = translation.getY();
		this.z = translation.getZ();
		return this;
	}
}
