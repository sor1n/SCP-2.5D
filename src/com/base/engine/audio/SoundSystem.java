package com.base.engine.audio;

import org.newdawn.slick.util.ResourceLoader;

import paulscode.sound.Library;
import paulscode.sound.SoundSystemConfig;
import paulscode.sound.SoundSystemException;
import paulscode.sound.Vector3D;
import paulscode.sound.codecs.CodecJOrbis;
import paulscode.sound.libraries.LibraryJavaSound;
import paulscode.sound.libraries.LibraryLWJGLOpenAL;

import com.base.engine.Camera;
import com.base.engine.MainComponent;
import com.base.engine.Vector3f;

public class SoundSystem 
{
	private static paulscode.sound.SoundSystem soundSystem;

	public static final String SCP096_CRY = "096_cry", SCP096_SCREAM = "096_scream", SCP106LAUGH = "oldmandrag";
	private static final String[] sounds = {SCP096_CRY, SCP096_SCREAM, SCP106LAUGH};
	
	public static void create()
	{
		try 
		{
			Class<?> library;
			boolean aLCompatible = SoundSystemConfig.libraryCompatible(LibraryLWJGLOpenAL.class);
			boolean JCompatible = SoundSystemConfig.libraryCompatible(LibraryJavaSound.class);
			if(aLCompatible) library = LibraryLWJGLOpenAL.class;
			else if(JCompatible) library = LibraryJavaSound.class;
			else library = Library.class;
			SoundSystemConfig.addLibrary(library);
			SoundSystemConfig.setCodec("ogg", CodecJOrbis.class);
		}
		catch(SoundSystemException e)
		{
			e.printStackTrace();
		}
		soundSystem = new paulscode.sound.SoundSystem();
		initSounds();
	}
	
	private static void initSounds()
	{
		for(String s : sounds) addSound(s, s + ".ogg");
	}
	
	public static void playBackgroundMusic(String path, boolean loop)
	{
		soundSystem.backgroundMusic(path, path, loop);
	}
	
	public static Vector3D getListenerPosition()
	{
		return soundSystem.getListenerData().position;
	}
	
	private static void initSound(String path, String name)
	{
		soundSystem.loadSound(ResourceLoader.getResource("audio/" + path), path);
		soundSystem.newSource(false, name, ResourceLoader.getResource("audio/" + path), path, false, 0, 0, 0, SoundSystemConfig.ATTENUATION_LINEAR, 0.1f);
	}
	
	public static void quickPlaySound(Vector3f pos, String path) //Ambient sound only
	{
		soundSystem.quickPlay(false, MainComponent.class.getClass().getClassLoader().getResource("/audio/" + path + ".ogg"), path, false, pos.getX(), pos.getY(), pos.getZ(), SoundSystemConfig.ATTENUATION_NONE, SoundSystemConfig.getDefaultRolloff());
	}
	
	public static void playSound(String sourcename, Vector3f pos, float intensity)
	{
		setAttenuation(sourcename, intensity);
		setPosition(sourcename, pos);
		soundSystem.setPitch(sourcename, 1f);
		soundSystem.play(sourcename);
	}
	
	public static void playSound(String sourcename, Vector3f pos, float intensity, float pitch)
	{
		setAttenuation(sourcename, intensity);
		setPosition(sourcename, pos);
		soundSystem.setPitch(sourcename, pitch);
		soundSystem.play(sourcename);
	}
	
	public static void setAttenuation(String source, float value)
	{
		soundSystem.setAttenuation(source, SoundSystemConfig.ATTENUATION_LINEAR);
		soundSystem.setDistOrRoll(source, value);
	}
	
	public static void pauseSound(String sourcename)
	{
		soundSystem.pause(sourcename);
	}
	
	public static void stopSound(String sourcename)
	{
		soundSystem.stop(sourcename);
	}
	
	public static void unloadSound(String source)
	{
		soundSystem.unloadSound(source);
	}
	
	public static void updateListener(Camera camera)
	{
		Vector3f pos = camera.getPos();
		Vector3f up = camera.getUp();
		Vector3f forward = camera.getForward();
		soundSystem.setListenerPosition(pos.getX(), pos.getY(), pos.getZ());
		soundSystem.setListenerOrientation(forward.getX(), forward.getY(), forward.getZ(), up.getX(), up.getY(), up.getZ());
	}
	
	public static void setPosition(String sourcename, Vector3f pos)
	{
		soundSystem.setPosition(sourcename, pos.getX(), pos.getY(), pos.getZ());
	}
	
	public static void setVolume(String sourcename, float value)
	{
		soundSystem.setVolume(sourcename, value);
	}
	
	public static void setMasterVolume(float value)
	{
		soundSystem.setMasterVolume(value);
	}
	
	public static void playSoundInQueue(String sourcename)
	{
		soundSystem.queueSound(sourcename, sourcename + ".ogg");
	}
	
//	public static void fadeSound(String sourcename, long milis)
//	{
//		soundSystem.fadeOut(sourcename, sourcename + ".ogg", milis);
//	}
	
	public static void destroy()
	{
		soundSystem.cleanup();
	}
	
	public static void addSound(String sourcename, String path)
	{
		initSound(path, sourcename);
	}
	
	public static boolean isPlaying(String source)
	{
		return soundSystem.playing(source);
	}
}
