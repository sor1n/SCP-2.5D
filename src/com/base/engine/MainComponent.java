package com.base.engine;

public class MainComponent 
{
	public static final int WIDTH = 800;
	public static final int HEIGHT = 600;
	public static final String TITLE = "SCP - The Escape 2.0";
	public static final double FRAME_CAP = 5000.0;

	private boolean isRunning;
	private Game game;

	public MainComponent()
	{
		Game.consoleMessage("OpenGL: " + RenderUtil.getOpenGLVersion());
		RenderUtil.initGraphics();
		isRunning = false;
		game = new Game();
	}

	public void start()
	{
		if(isRunning) return;
		run();
	}

	public void stop()
	{
		if(!isRunning) return;
		isRunning = false;
	}

	private void run()
	{
		isRunning = true;

		int frames = 0;
		long frameCounter = 0;

		final double frameTime = 1.0 / FRAME_CAP;

		double lastTime = Time.getTime();
		double unprocessedTime = 0;

		while(isRunning)
		{
			boolean render = false;

			double startTime = Time.getTime();
			double passedTime = startTime - lastTime;
			lastTime = startTime;

			unprocessedTime += passedTime;
			frameCounter += passedTime;

			while(unprocessedTime > frameTime)
			{
				render = true;
				unprocessedTime -= frameTime;
				if(Window.isCloseRequested()) stop();
				game.input((float)frameTime);
				Input.update();
				game.update((float)frameTime);
				if(frameCounter >= 1.0)
				{
					Window.setTitle(TITLE + " | FPS: " + frames);
					frames = 0;
					frameCounter = 0;
				}
			}
			if(render)
			{
				render();
				frames++;
			}
			else
			{
				try 
				{
					Thread.sleep(1);
				} 
				catch (InterruptedException e) 
				{
					e.printStackTrace();
				}
			}
		}
		cleanUp();
	}

	private void render()
	{
		RenderUtil.clearScreen();
		game.render();
		game.renderGUI();
		Window.render();
	}

	private void cleanUp()
	{
		Window.dispose();
	}

	public static void main(String[] args)
	{
		Window.createWindow(WIDTH, HEIGHT, TITLE);
		MainComponent game = new MainComponent();
		game.start();
	}
}
