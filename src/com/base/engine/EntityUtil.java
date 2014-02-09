package com.base.engine;

public class EntityUtil
{
	public static void faceCamera(Transform transform)
	{
		Vector3f directionToCamera = Transform.getCamera().getPos().sub(transform.getTranslation());
		float angleToFaceTheCamera = (float)Math.toDegrees(Math.atan(directionToCamera.getZ() / directionToCamera.getX()));
		if(directionToCamera.getX() < 0) angleToFaceTheCamera += 180;
		transform.setRotation(0, angleToFaceTheCamera + 90, 0);
	}
	
	public static void faceCameraWithY(Transform transform)
	{
		Vector3f directionToCamera = Transform.getCamera().getPos().sub(transform.getTranslation());
		float angleToFaceTheCamera = (float)Math.toDegrees(Math.atan(directionToCamera.getZ() / directionToCamera.getX()));
		if(directionToCamera.getX() < 0) angleToFaceTheCamera += 180;
		transform.setRotation(-Transform.getCamera().getForward().getY() * 100, angleToFaceTheCamera + 90, 0);
		transform.getTranslation().setY(transform.getTranslation().getY() + Transform.getCamera().getForward().normalized().getY() / 10);
	}
}
