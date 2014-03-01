package com.base.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import com.base.engine.entities.Entity;
import com.base.engine.itementities.ItemEntity;
import com.base.engine.itementities.Medkit;

public class Level 
{
	private static List<Level> levels = new CopyOnWriteArrayList<Level>();
	private static List<ExitPoint> path = new ArrayList<ExitPoint>();

	public static final float SPOT_WIDTH = 1;
	public static final float SPOT_LENGTH = 1;
	public static final float SPOT_HEIGHT = 1;
	public static final float PLAYER_HEIGHT = 0.4378f;
	public static final int NUM_TEX_EXP = 4, NUM_TEXTURES = (int)Math.pow(2, NUM_TEX_EXP);

	private static MiniMap miniMap;
	private Mesh mesh;
	private Bitmap levelFloor, levelWalls, levelObjects;
	private Shader shader;
	private Material material;
	private Transform transform;
	private Player player;
	private int levelID;

	private static boolean playerSpawn = false;

	private String lvlName;
	private TexturePack texPack;

	private List<Door> doors;
	private List<Entity> monsters;
	private List<ItemEntity> itemEntities;
	private List<ExitPoint> exitPoints;
	private List<Particle> particles;

	private ArrayList<Vector2f> collisionPosStart, collisionPosEnd;

	public Level(String lvlName, TexturePack texture, boolean init)
	{
		levelID = new Random().nextInt();
		this.lvlName = lvlName;
		texPack = texture;
		if(init) init(texture);
	}

	public Level(String lvlName, TexturePack texture)
	{
		this(lvlName, texture, true);
	}

	private void init(TexturePack texture)
	{
		levelFloor = new Bitmap(lvlName + "/" + lvlName + "_Floor.png").flipY();
		levelWalls = new Bitmap(lvlName + "/" + lvlName + "_Walls.png").flipY();
		levelObjects = new Bitmap(lvlName + "/" + lvlName + "_Objects.png").flipY();
		material = new Material(texture.getTextureSheet());
		transform = new Transform();
		shader = PhongShader.getInstance();
		generateLevel();

		PhongShader.setAmbientLight(new Vector3f(.8f, .8f, .8f));

		getLevels().add(this);
		if(playerSpawn && player == null)
		{
			Level prevLevel = getLevels().get(path.size() - 1);
			if(prevLevel == null) return;
			for(ExitPoint exit : getExitPoints())
				for(ExitPoint points : prevLevel.getExitPoints())
				{
					if(exit.getLevel() == points.getLevel())
					{
						Inventory inv = Game.getLevel().getPlayer().getInventory();
						Vector3f solid = findSolidPlace(exit.getExitPoint().getXZ().toInt());
						spawnPlayer(new Vector3f((solid.getX() + 0.5f) * SPOT_WIDTH, PLAYER_HEIGHT, (solid.getZ() + 0.5f) * SPOT_LENGTH), points, exit, inv);
						break;
					}
				}
		}
	}

	public void openDoors(Vector3f pos, boolean exit)
	{
		for(Door door : doors) 
		{
			if(door.getTransform().getTranslation().sub(pos).length() < Door.OPEN_DISTANCE) door.open();
		}
		if(exit)
		{
			for(ExitPoint eP : getExitPoints())
			{
				if(eP.getExitPoint().sub(pos).length() < Door.OPEN_DISTANCE)
				{
					path.add(eP);
					if(eP.getLevel() == Game.levelNum) Game.nextLevel(eP.getLevel() - 1);
					else Game.nextLevel(eP.getLevel());
				}
			}
		}
	}

	public void removeDoor(Vector3f pos)
	{
		for(Door door : doors) 
		{
			if(door.getTransform().getTranslation().sub(pos).length() < Door.OPEN_DISTANCE) getDoors().remove(door);
		}
	}

	public void input(float delta)
	{
		if(player != null) player.input(delta);
		for(ItemEntity med : getItemEntities()) med.input(delta);
	}

	public void update(float delta)
	{
		for(Door door : getDoors()) door.update(delta);
		for(Particle particle : getParticles()) particle.update(delta);
		for(Entity monster : getMonsters()) monster.update(delta);
		for(ItemEntity med : getItemEntities()) med.update(delta);
		if(player != null) player.update(delta);
		if(miniMap != null) miniMap.update(delta);
	}

	public void render()
	{
		shader.bind();
		shader.updateUniforms(transform.getTransformation(), transform.getProjectedTransformation(), material);
		mesh.draw();
		for(Door door : getDoors()) door.render();
		for(Particle particle : getParticles()) particle.render();
		for(Entity monster : getMonsters()) monster.render();
		for(ItemEntity med : getItemEntities()) med.render();
		if(player != null) player.render();
	}

	public void renderGUI()
	{
		shader.unbind();
		RenderUtil.unbindTextures();
		RenderUtil.setTextures(true);
		for(ItemEntity med : getItemEntities()) med.render2D();
		if(player != null) player.renderGUI();
		if(miniMap != null) miniMap.render();
	}

	private void addFace(ArrayList<Integer> indices, int startLocation, boolean dir)
	{
		if(dir)
		{
			indices.add(startLocation + 2);
			indices.add(startLocation + 1);
			indices.add(startLocation + 0);
			indices.add(startLocation + 3);
			indices.add(startLocation + 2);
			indices.add(startLocation + 0);
		}
		else
		{
			indices.add(startLocation + 0);
			indices.add(startLocation + 1);
			indices.add(startLocation + 2);
			indices.add(startLocation + 0);
			indices.add(startLocation + 2);
			indices.add(startLocation + 3);
		}
	}

	private float[] calcTexCoords(int value)
	{
		int texX = value / NUM_TEXTURES, texY = texX % NUM_TEX_EXP;
		texX /= NUM_TEX_EXP;
		float[] res = new float[4];
		res[0] = 1f - (float)texX / (float)NUM_TEX_EXP;
		res[1] = res[0] - 1f / (float)NUM_TEX_EXP;
		res[3] = 1f - (float)texY / (float)NUM_TEX_EXP;
		res[2] = res[3] - 1f / (float)NUM_TEX_EXP;
		return res;
	}

	private void addVertices(ArrayList<Vertex> vertices, int i, int j, boolean x, boolean y, boolean z, float offset, float[] texCoords)
	{
		if(x && z)
		{
			vertices.add(new Vertex(new Vector3f(i * SPOT_WIDTH, offset * SPOT_HEIGHT, j * SPOT_LENGTH), new Vector2f(texCoords[1], texCoords[3])));
			vertices.add(new Vertex(new Vector3f((i + 1) * SPOT_WIDTH, offset * SPOT_HEIGHT, j * SPOT_LENGTH), new Vector2f(texCoords[0], texCoords[3])));
			vertices.add(new Vertex(new Vector3f((i + 1) * SPOT_WIDTH, offset * SPOT_HEIGHT, (j + 1) * SPOT_LENGTH), new Vector2f(texCoords[0], texCoords[2])));
			vertices.add(new Vertex(new Vector3f(i * SPOT_WIDTH, offset * SPOT_HEIGHT, (j + 1) * SPOT_LENGTH), new Vector2f(texCoords[1], texCoords[2])));
		}
		else if(y && z)
		{
			vertices.add(new Vertex(new Vector3f(offset * SPOT_WIDTH, i * SPOT_HEIGHT, j * SPOT_LENGTH), new Vector2f(texCoords[1], texCoords[3])));
			vertices.add(new Vertex(new Vector3f(offset * SPOT_WIDTH, i * SPOT_HEIGHT, (j + 1) * SPOT_LENGTH), new Vector2f(texCoords[0], texCoords[3])));
			vertices.add(new Vertex(new Vector3f(offset * SPOT_WIDTH, (i + 1) * SPOT_HEIGHT, (j + 1) * SPOT_LENGTH), new Vector2f(texCoords[0], texCoords[2])));
			vertices.add(new Vertex(new Vector3f(offset * SPOT_WIDTH, (i + 1) * SPOT_HEIGHT, j * SPOT_LENGTH), new Vector2f(texCoords[1], texCoords[2])));
		}
		else if(x && y)
		{
			vertices.add(new Vertex(new Vector3f(i * SPOT_WIDTH, j * SPOT_HEIGHT, offset * SPOT_LENGTH), new Vector2f(texCoords[1], texCoords[3])));
			vertices.add(new Vertex(new Vector3f((i + 1) * SPOT_WIDTH, j * SPOT_HEIGHT, offset * SPOT_LENGTH), new Vector2f(texCoords[0], texCoords[3])));
			vertices.add(new Vertex(new Vector3f((i + 1) * SPOT_WIDTH, (j + 1) * SPOT_HEIGHT, offset * SPOT_LENGTH), new Vector2f(texCoords[0], texCoords[2])));
			vertices.add(new Vertex(new Vector3f(i * SPOT_WIDTH, (j + 1) * SPOT_HEIGHT, offset * SPOT_LENGTH), new Vector2f(texCoords[1], texCoords[2])));
		}
		else Game.crashGame("Invalid plane used in level generator.");
	}

	private void addDoor(int x, int y)
	{
		Transform doorTrans = new Transform();
		boolean xDoor = (levelFloor.getPixel(x, y - 1) & 0xFFFFFF) == 0 && (levelFloor.getPixel(x, y + 1) & 0xFFFFFF) == 0;
		boolean yDoor = (levelFloor.getPixel(x - 1, y) & 0xFFFFFF) == 0 && (levelFloor.getPixel(x + 1, y) & 0xFFFFFF) == 0;
		if(!(xDoor ^ yDoor)) Game.crashGame("Door directions are wrong @ (" + x + ", " + y + ")");
		Vector3f openPosition = null;
		if(yDoor)
		{
			doorTrans.setTranslation(x, 0, y + SPOT_LENGTH / 2);
			openPosition = doorTrans.getTranslation().sub(new Vector3f(Door.DOOR_MOVE_AMOUNT, 0f, 0f));
		}
		if(xDoor)
		{
			doorTrans.setTranslation(x + SPOT_WIDTH / 2, 0, y);
			doorTrans.setRotation(0, 90, 0);
			openPosition = doorTrans.getTranslation().sub(new Vector3f(0f, 0f, Door.DOOR_MOVE_AMOUNT));
		}
		getDoors().add(new Door(doorTrans, material, openPosition));
	}

	//FIXME: Specials
	private void addSpecial(int red, int green, int blue, int x, int y)
	{
		if(blue == RGB.DOOR_BLUE) addDoor(x, y);
		else if(blue == RGB.PLAYER_BLUE && !playerSpawn) spawnPlayer(new Vector3f((x + 0.5f) * SPOT_WIDTH, PLAYER_HEIGHT, (y + 0.5f) * SPOT_LENGTH));
		else if(blue == RGB.MEDKIT_BLUE) getItemEntities().add(new Medkit(new Vector3f((x + 0.5f) * SPOT_WIDTH, 0, (y + 0.5f) * SPOT_LENGTH)));
		else if(blue == RGB.EXIT_BLUE)
		{
			boolean xDoor1 = (levelFloor.getPixel(x, y - 1) & 0xFFFFFF) == 0;
			boolean yDoor1 = (levelFloor.getPixel(x - 1, y) & 0xFFFFFF) == 0;
			boolean xDoor2 = (levelFloor.getPixel(x, y + 1) & 0xFFFFFF) == 0;
			boolean yDoor2 = (levelFloor.getPixel(x + 1, y) & 0xFFFFFF) == 0;
			//			if(!xDoor1 && !xDoor2 && !yDoor1 && !yDoor2) Game.crashGame("One or more misplaced ExitPoints at: (" + x + "," + y + ") in level: " + getLevelName());
			int dir = 0;
			if(xDoor1) dir = 0;
			else if(xDoor2) dir = 1;
			else if(yDoor1) dir = 2;
			else if(yDoor2) dir = 3;
			getExitPoints().add(new ExitPoint(new Vector3f((x + 0.5f) * SPOT_WIDTH, 0, (y + 0.5f) * SPOT_LENGTH), green / 256, dir));
		}
		else if(blue >= RGB.ENTITY_MIN)
		{
			for(int i = 0; i < Entity.monsters.length; i++)
			{
				try 
				{
					Entity ent = (Entity) Entity.monsters[i].newInstance();
					if(ent.getRGBValue()[0] == red && ent.getRGBValue()[1] == green && ent.getRGBValue()[2] == blue)
					{
						ent.getTransform().setScale(Vector3f.ONE);
						ent.getTransform().setTranslation(new Vector3f((x + 0.5f) * SPOT_WIDTH, 0, (y + 0.5f) * SPOT_LENGTH));
						getMonsters().add(ent);
						break;
					}
				}
				catch(Exception e) {e.printStackTrace();}
			}
		}
	}

	//FIXME: Generation
	private void generateLevel()
	{
		doors = new CopyOnWriteArrayList<Door>();
		monsters = new CopyOnWriteArrayList<Entity>();
		itemEntities = new CopyOnWriteArrayList<ItemEntity>();
		exitPoints = new CopyOnWriteArrayList<ExitPoint>();
		particles = new CopyOnWriteArrayList<Particle>();
		collisionPosStart = new ArrayList<Vector2f>();
		collisionPosEnd = new ArrayList<Vector2f>();
		ArrayList<Vertex> vertices = new ArrayList<Vertex>();
		ArrayList<Integer> indices = new ArrayList<Integer>();

		for(int i = 0; i < levelFloor.getWidth(); i++)
			for(int j = 0; j < levelFloor.getHeight(); j++)
			{
				if((levelFloor.getPixel(i, j) & 0xFFFFFF) == 0) continue;
				float[] texCoords = calcTexCoords((levelFloor.getPixel(i, j) & 0x00FF00) >> 8);

				addSpecial((levelObjects.getPixel(i, j) & 0xFF0000), (levelObjects.getPixel(i, j) & 0x00FF00), (levelObjects.getPixel(i, j) & 0x0000FF), i, j);

				//Generate Floor
				addFace(indices, vertices.size(), true);
				addVertices(vertices, i, j, true, false, true, 0, texCoords);

				//Generate Ceiling
				addFace(indices, vertices.size(), false);
				addVertices(vertices, i, j, true, false, true, 1, texCoords);

				//Generate Walls
				texCoords = calcTexCoords((levelWalls.getPixel(i, j) & 0xFF0000) >> 16);
				if((levelWalls.getPixel(i, j - 1) & 0xFFFFFF) == 0)
				{
					collisionPosStart.add(new Vector2f(i * SPOT_WIDTH, j * SPOT_LENGTH));
					collisionPosEnd.add(new Vector2f((i + 1) * SPOT_WIDTH, j * SPOT_LENGTH));
					addFace(indices, vertices.size(), false);
					addVertices(vertices, i, 0, true, true, false, j, texCoords);
				}
				if((levelWalls.getPixel(i, j + 1) & 0xFFFFFF) == 0)
				{
					collisionPosStart.add(new Vector2f(i * SPOT_WIDTH, (j + 1) * SPOT_LENGTH));
					collisionPosEnd.add(new Vector2f((i + 1) * SPOT_WIDTH, (j + 1) * SPOT_LENGTH));
					addFace(indices, vertices.size(), true);
					addVertices(vertices, i, 0, true, true, false, (j + 1), texCoords);
				}
				if((levelWalls.getPixel(i - 1, j) & 0xFFFFFF) == 0)
				{
					collisionPosStart.add(new Vector2f(i * SPOT_WIDTH, j * SPOT_LENGTH));
					collisionPosEnd.add(new Vector2f(i * SPOT_WIDTH, (j + 1) * SPOT_LENGTH));
					addFace(indices, vertices.size(), true);
					addVertices(vertices, 0, j, false, true, true, i, texCoords);
				}
				if((levelWalls.getPixel(i + 1, j) & 0xFFFFFF) == 0)
				{
					collisionPosStart.add(new Vector2f((i + 1) * SPOT_WIDTH, j * SPOT_LENGTH));
					collisionPosEnd.add(new Vector2f((i + 1) * SPOT_WIDTH, (j + 1) * SPOT_LENGTH));
					addFace(indices, vertices.size(), false);
					addVertices(vertices, 0, j, false, true, true, (i + 1), texCoords);
				}
			}
		Vertex[] vertArray = new Vertex[vertices.size()];
		Integer[] intArray = new Integer[indices.size()];
		vertices.toArray(vertArray);
		indices.toArray(intArray);
		mesh = new Mesh(vertArray, Util.toIntArray(intArray));
	}

	public Vector3f checkCollisions(Vector3f oldPos, Vector3f newPos, float objectWidth, float objectLength)
	{
		Vector2f collisionVector = new Vector2f(1, 1);
		Vector3f movementVector = newPos.sub(oldPos);
		if(movementVector.length() > 0)
		{
			Vector2f blockSize = new Vector2f(SPOT_WIDTH, SPOT_LENGTH);
			Vector2f objectSize = new Vector2f(objectWidth, objectLength);
			Vector2f oldPos2 = new Vector2f(oldPos.getX(), oldPos.getZ());
			Vector2f newPos2 = new Vector2f(newPos.getX(), newPos.getZ());
			for(int i = 0; i < levelFloor.getWidth(); i++)
				for(int j = 0; j < levelFloor.getHeight(); j++)
					if((levelFloor.getPixel(i, j) & 0xFFFFFF) == 0) collisionVector = collisionVector.mul(rectCollide(oldPos2, newPos2, objectSize, blockSize.mul(new Vector2f(i, j)), blockSize));
			for(Door door : doors)
			{
				Vector2f doorSize = door.getDoorSize();
				Vector3f doorPos3 = door.getTransform().getTranslation();
				Vector2f doorPos2 = new Vector2f(doorPos3.getX(), doorPos3.getZ());
				collisionVector = collisionVector.mul(rectCollide(oldPos2, newPos2, objectSize, doorPos2, doorSize));
			}
		}
		return new Vector3f(collisionVector.getX(), 0, collisionVector.getY());
	}

	public Vector3f checkCollisions(Vector3f oldPos, Vector3f newPos, float objectWidth, float objectLength, boolean ignoreDoors)
	{
		Vector2f collisionVector = new Vector2f(1, 1);
		Vector3f movementVector = newPos.sub(oldPos);
		if(movementVector.length() > 0)
		{
			Vector2f blockSize = new Vector2f(SPOT_WIDTH, SPOT_LENGTH);
			Vector2f objectSize = new Vector2f(objectWidth, objectLength);
			Vector2f oldPos2 = new Vector2f(oldPos.getX(), oldPos.getZ());
			Vector2f newPos2 = new Vector2f(newPos.getX(), newPos.getZ());
			for(int i = 0; i < levelFloor.getWidth(); i++)
				for(int j = 0; j < levelFloor.getHeight(); j++)
					if((levelFloor.getPixel(i, j) & 0xFFFFFF) == 0) collisionVector = collisionVector.mul(rectCollide(oldPos2, newPos2, objectSize, blockSize.mul(new Vector2f(i, j)), blockSize));
			if(!ignoreDoors)
			{
				for(Door door : doors)
				{
					Vector2f doorSize = door.getDoorSize();
					Vector3f doorPos3 = door.getTransform().getTranslation();
					Vector2f doorPos2 = new Vector2f(doorPos3.getX(), doorPos3.getZ());
					collisionVector = collisionVector.mul(rectCollide(oldPos2, newPos2, objectSize, doorPos2, doorSize));
				}
			}
		}
		return new Vector3f(collisionVector.getX(), 0, collisionVector.getY());
	}

	public Entity isLookingAtEntity(Vector2f lineStart, Vector2f lineEnd)
	{
		Vector2f nearestIntersection = null;
		for(int i = 0; i < collisionPosStart.size(); i++)
		{
			Vector2f collisionVector = lineIntersect(lineStart, lineEnd, collisionPosStart.get(i), collisionPosEnd.get(i));
			nearestIntersection = findNearestVector2f(nearestIntersection, collisionVector, lineStart);
		}

		Vector2f nearestMonsterIntersect = null;
		Entity nearestMonster = null;
		for(Entity monster : monsters)
		{
			Vector2f monsterSize = monster.getSize();
			Vector3f monsterPos3 = monster.getTransform().getTranslation();
			Vector2f monsterPos2 = new Vector2f(monsterPos3.getX(), monsterPos3.getZ());

			Vector2f collisionVector = lineIntersectRect(lineStart, lineEnd, monsterPos2, monsterSize);
			Vector2f lastMonsterIntersect = nearestMonsterIntersect;
			nearestMonsterIntersect = findNearestVector2f(nearestMonsterIntersect, collisionVector, lineStart);
			if(lastMonsterIntersect != nearestMonsterIntersect) nearestMonster = monster;
		}
		if(nearestMonsterIntersect != null && (nearestIntersection == null || nearestMonsterIntersect.sub(lineStart).length() < nearestIntersection.sub(lineStart).length()))
		{
			if(nearestMonster != null) return nearestMonster;
		}

		return null;
	}

	public Vector2f checkIntersections(Vector2f lineStart, Vector2f lineEnd, boolean hurtMonsters)
	{
		Vector2f nearestIntersection = null;

		for(int i = 0; i < collisionPosStart.size(); i++)
		{
			Vector2f collisionVector = lineIntersect(lineStart, lineEnd, collisionPosStart.get(i), collisionPosEnd.get(i));
			nearestIntersection = findNearestVector2f(nearestIntersection, collisionVector, lineStart);
		}

		for(Door door : doors)
		{
			Vector2f doorSize = door.getDoorSize();
			Vector3f doorPos3 = door.getTransform().getTranslation();
			Vector2f doorPos2 = new Vector2f(doorPos3.getX(), doorPos3.getZ());

			Vector2f collisionVector = lineIntersectRect(lineStart, lineEnd, doorPos2, doorSize);
			nearestIntersection = findNearestVector2f(nearestIntersection, collisionVector, lineStart);
		}

		if(hurtMonsters)
		{
			Vector2f nearestMonsterIntersect = null;
			Entity nearestMonster = null;
			for(Entity monster : monsters)
			{
				Vector2f monsterSize = monster.getSize();
				Vector3f monsterPos3 = monster.getTransform().getTranslation();
				Vector2f monsterPos2 = new Vector2f(monsterPos3.getX(), monsterPos3.getZ());

				Vector2f collisionVector = lineIntersectRect(lineStart, lineEnd, monsterPos2, monsterSize);
				Vector2f lastMonsterIntersect = nearestMonsterIntersect;
				nearestMonsterIntersect = findNearestVector2f(nearestMonsterIntersect, collisionVector, lineStart);
				if(lastMonsterIntersect != nearestMonsterIntersect) nearestMonster = monster;
			}
			if(nearestMonsterIntersect != null && (nearestIntersection == null || nearestMonsterIntersect.sub(lineStart).length() < nearestIntersection.sub(lineStart).length()))
			{
				if(nearestMonster != null) nearestMonster.damage(player.getDamage());
			}
		}

		return nearestIntersection;
	}

	public Vector2f findNearestVector2f(Vector2f a, Vector2f b, Vector2f positionRelativeTo)
	{
		if(b != null && (a == null || a.sub(positionRelativeTo).length() > b.sub(positionRelativeTo).length())) return b;
		return a;
	}

	private float vector2fCross(Vector2f a, Vector2f b)
	{
		return a.getX() * b.getY() - a.getY() * b.getX();
	}

	private Vector2f lineIntersect(Vector2f lineStart1, Vector2f lineEnd1, Vector2f lineStart2, Vector2f lineEnd2)
	{
		Vector2f line1 = lineEnd1.sub(lineStart1);
		Vector2f line2 = lineEnd2.sub(lineStart2);
		float cross = vector2fCross(line1, line2);
		if(cross == 0) return null;
		Vector2f distanceBetweenLineStarts = lineStart2.sub(lineStart1);
		float a = vector2fCross(distanceBetweenLineStarts, line2) / cross;
		float b = vector2fCross(distanceBetweenLineStarts, line1) / cross;
		if(0.0f < a && a < 1.0f && 0.0f < b && b < 1.0f) return lineStart1.add(line1.mul(a));
		return null;
	}

	private Vector2f rectCollide(Vector2f oldPos, Vector2f newPos, Vector2f size1, Vector2f pos2, Vector2f size2)
	{
		Vector2f res = new Vector2f(0, 0);
		if(newPos.getX() + size1.getX() < pos2.getX() ||
				newPos.getX() - size1.getX() > pos2.getX() + size2.getX() * size2.getX() ||
				oldPos.getY() + size1.getY() < pos2.getY() ||
				oldPos.getY() - size1.getY() > pos2.getY() + size2.getY() * size2.getY()) res.setX(1);

		if(oldPos.getX() + size1.getX() < pos2.getX() ||
				oldPos.getX() - size1.getX() > pos2.getX() + size2.getX() * size2.getX() ||
				newPos.getY() + size1.getY() < pos2.getY() ||
				newPos.getY() - size1.getY() > pos2.getY() + size2.getY() * size2.getY()) res.setY(1);
		return res;
	}

	public Shader getShader()
	{
		return shader;
	}

	public Vector2f lineIntersectRect(Vector2f lineStart, Vector2f lineEnd, Vector2f pos, Vector2f size)
	{
		Vector2f res = null;
		Vector2f collisionVector = lineIntersect(lineStart, lineEnd, pos, new Vector2f(pos.getX() + size.getX(), pos.getY()));
		res = findNearestVector2f(res, collisionVector, lineStart);
		collisionVector = lineIntersect(lineStart, lineEnd, pos, new Vector2f(pos.getX(), pos.getY() + size.getY()));
		res = findNearestVector2f(res, collisionVector, lineStart);
		collisionVector = lineIntersect(lineStart, lineEnd, new Vector2f(pos.getX(), pos.getY() + size.getY()), pos.add(size));
		res = findNearestVector2f(res, collisionVector, lineStart);
		collisionVector = lineIntersect(lineStart, lineEnd, new Vector2f(pos.getX() + size.getX(), pos.getY()), pos.add(size));
		res = findNearestVector2f(res, collisionVector, lineStart);
		return res;
	}

	public Player getPlayer()
	{
		return player;
	}

	public synchronized List<Door> getDoors()
	{
		return doors;
	}

	public synchronized List<Entity> getMonsters()
	{
		return monsters;
	}

	public synchronized List<ItemEntity> getItemEntities()
	{
		return itemEntities;
	}

	public synchronized List<ExitPoint> getExitPoints()
	{
		return exitPoints;
	}

	public synchronized List<Particle> getParticles()
	{
		return particles;
	}

	public int getCoord(int i, int j)
	{
		return levelFloor.getPixel(i / (int)SPOT_WIDTH, j / (int)SPOT_HEIGHT);
	}

	public boolean isAir(int x, int y)
	{
		return isID(x, y, 0);
	}

	public boolean isDoor(int x, int y)
	{
		return isBlueID(x, y, 1);
	}

	public boolean isBlueID(int x, int y, int id)
	{
		return (getCoord(x, y) & 0x0000FF) == id;
	}

	public boolean isRedID(int x, int y, int id)
	{
		return (getCoord(x, y) & 0xFF0000) == id;
	}

	public boolean isGreenID(int x, int y, int id)
	{
		return (getCoord(x, y) & 0x00FF00) == id;
	}

	public boolean isID(int x, int y, int id)
	{
		return (getCoord(x, y) & 0xFFFFFF) == id;
	}

	public String getLevelMap()
	{
		return levelFloor.getPath();
	}

	public String getObjectsMap()
	{
		return levelObjects.getPath();
	}

	public String getLevelName()
	{
		return lvlName;
	}

	public TexturePack getTexturePack()
	{
		return texPack;
	}

	public static MiniMap getMap()
	{
		return miniMap;
	}

	public static synchronized List<Level> getLevels()
	{
		return levels;
	}

	public Vector3f findSolidPlace(Vector2i vec)
	{
		int number = 0;
		if(!isAir(vec.getX(), vec.getY())) return new Vector3f(vec.getX(), 0, vec.getY());
		number++;
		for(int i = -number; i < number; i++)
			for(int j = -number; j < number; j++)
			{
				if(!isAir(vec.getX() + i, vec.getY() + j)) return new Vector3f(vec.getX() + i, 0, vec.getY() + j);
			}
		return findSolidPlace(vec, number + 1);
	}

	private Vector3f findSolidPlace(Vector2i vec, int number)
	{
		for(int i = -number; i < number; i++)
			for(int j = -number; j < number; j++)
			{
				if(!isAir(vec.getX() + i, vec.getY() + j)) return new Vector3f(vec.getX() + i, 0, vec.getY() + j);
			}
		if(number < 8) return findSolidPlace(vec, number + 1);
		else return new Vector3f(vec.getX(), 0, vec.getY());
	}

	private void spawnPlayer(Vector3f vec)
	{
		player = new Player(vec, null);
		Transform.setProjection(70, Window.getWidth(), Window.getHeight(), 0.01f, 1000f);
		Transform.setCamera(player.getCamera());
		if(miniMap == null) miniMap = new MiniMap(this);
		playerSpawn = true;
	}

	private void spawnPlayer(Vector3f vec, ExitPoint oldP, ExitPoint newP, Inventory inventory)
	{
		Vector3f forw = Transform.getCamera().getForward();
		Vector3f up = Transform.getCamera().getUp();
		player = new Player(vec, inventory);
		Transform.setProjection(70, Window.getWidth(), Window.getHeight(), 0.01f, 1000f);
		Transform.setCamera(player.getCamera());
		Transform.getCamera().setForward(forw);
		Transform.getCamera().setUp(up);
		miniMap.addMapPart(this, oldP, newP);
		playerSpawn = true;
	}

	public static boolean doesLevelExist(int i)
	{
		try
		{
			new Bitmap("Room_" + i + "/Room_" + i + "_Floor.png");
			return true;
		}
		catch(Exception e)
		{
			return false;
		}
	}

	public static List<ExitPoint> getCurrentPath()
	{
		return path;
	}

	public static ExitPoint getCurrentExitPoint()
	{
		if(path.size() > 0) return path.get(path.size() - 1);
		else return null;
	}

	public int getID()
	{
		return levelID;
	}
}
