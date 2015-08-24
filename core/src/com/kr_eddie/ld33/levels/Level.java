package com.kr_eddie.ld33.levels;

import static com.kr_eddie.ld33.screens.GameScreen.B2W;
import static com.kr_eddie.ld33.screens.GameScreen.W2B;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.kr_eddie.ld33.Assets;
import com.kr_eddie.ld33.LD33Game;
import com.kr_eddie.ld33.Util;
import com.kr_eddie.ld33.entities.AngryVillager;
import com.kr_eddie.ld33.entities.AngryVillager.Type;
import com.kr_eddie.ld33.entities.Entity;
import com.kr_eddie.ld33.entities.Player;
import com.kr_eddie.ld33.screens.GameScreen;

public class Level {	
	
	public final GameScreen game;
	World world;

	public final String name;	
	public final TiledMap map;
	
	public final boolean persistent;
	HashMap<String, Vector2> spawns;
	HashMap<String, Trigger> triggers;
	
	public boolean levelChange;
	public String targetLevel, targetSpawn;
	public float fade_in_timer, fade_out_timer;
	public float fade_in_dur, fade_out_dur;
	public float message_fade_timer;
	
	
	ArrayList<Body> bodies;
	public Player player;
	public ArrayList<Entity> entities;
	
	Vector2 camera;	
	float minX, minY, maxX, maxY;
	
	OrthogonalTiledMapRenderer tileRenderer;
	
	public Level(GameScreen game, String name) {
		this.game = game;
		this.name = name;
		
		world = new World(new Vector2(0,0), true);
		world.setContactListener(new LevelContactListener(this));
		
		bodies = new ArrayList<Body>();
		entities = new ArrayList<Entity>();
		camera = new Vector2();
		map = new TmxMapLoader(new InternalFileHandleResolver()).load("levels/"+name+".tmx");
		
		levelChange = false;
		targetLevel = null; targetSpawn = null;
		
		fade_in_timer = 0; fade_out_timer = -1;
		fade_in_dur = 2.5f; 
		fade_out_dur = 2.5f;
		
		message_fade_timer = 0f;
		
		
		// Load Settings
		int inf = 100000;
		minX = minY -inf; maxX = maxY = inf;
		persistent = (map.getProperties().containsKey("persistent"));
		
		// Load Event Objects
		spawns = new HashMap<String, Vector2>();
		triggers = new HashMap<String, Trigger>();
		loadEventObjects();
		
		// Solid Edges
		createSolidEdges();		
		
		// Player
		player = new Player(this);
		entities.add(player);
		
		gotoSpawn("default", Player.max_health);
		
		// Spawn Mobs
		spawnMobs();
		
			
		tileRenderer = new OrthogonalTiledMapRenderer(map);		
	}
	

	private void loadEventObjects() {
		Vector2 pd = null; 
		if(map.getLayers().get("EventObjects") == null) return;
		for (MapObject o : map.getLayers().get("EventObjects").getObjects()) {
			String type = (String) o.getProperties().get("type");
			if (type == null) continue;
			Vector2 p =  Util.getMapObjectPosition(o).scl(W2B);
			
			// Spawn
			if (type.equals("spawn")) {
				spawns.put(o.getName(), p);
				if (pd == null) pd = p;
			} else 
			// Doors
			if (type.equals("trigger")) {			
				Trigger trigger = new Trigger(this, o);
				triggers.put(trigger.name, trigger);				
			} else
			// Camera limit
			if (type.equals("limit")) {			
				String ltype = (String) o.getProperties().get("ltype");
				if (ltype == null || ltype.equals("minY")) 
					minY = Util.getMapObjectPosition(o).y;
				else if (ltype.equals("maxY"))
					maxY = Util.getMapObjectPosition(o).y;
				else if (ltype.equals("minX"))
					minX = Util.getMapObjectPosition(o).x;
				else if (ltype.equals("maxX"))
					maxX = Util.getMapObjectPosition(o).x;
			} 	
		}
		
		if (pd == null) spawns.put("default", new Vector2(0,0)); 
		else spawns.put("default", pd);		
	}

	private void spawnMobs() {
		if (map.getLayers().get("Mobs") == null) return;
		for(MapObject t : map.getLayers().get("Mobs").getObjects()) {
			Entity e;
			String type = (String) t.getProperties().get("type");
			
			if (type == null) continue;
				 if (type.equals("v")) e = new AngryVillager(this, Type.NORMAL);
			else if (type.equals("vp")) e = new AngryVillager(this, Type.PITCHFORK);
			else if (type.equals("vt")) e = new AngryVillager(this, Type.TORCH);
			else continue;
			
			e.body.setTransform(Util.getMapObjectPosition(t).scl(W2B), 0);
			entities.add(e);
		}
	}

	private void createSolidEdges() {
		BodyDef bdef = new BodyDef();
		FixtureDef fdef = new FixtureDef();
		bdef.type = BodyType.StaticBody;
	
		if (map.getLayers().get("SolidEdges") == null) return;
		for(MapObject edge : map.getLayers().get("SolidEdges").getObjects()) {
			PolylineMapObject polyline = (PolylineMapObject) edge;
			float points[] = polyline.getPolyline().getVertices();
			for(int i = 0; i < points.length; i++) points[i] *= W2B;
					
			float x = ((Float) polyline.getProperties().get("x")) * W2B;
			float y = ((Float) polyline.getProperties().get("y")) * W2B;
			
			ChainShape shape = new ChainShape();
			shape.createChain(points);
			
			fdef.shape = shape;			
			bdef.position.set(x,y);
			
			Body edgeBody = world.createBody(bdef);
			edgeBody.createFixture(fdef);
			
			bodies.add(edgeBody);
			
			shape.dispose();
		}
	}
	
	
	float timer = 0;
	public void update(float delay) {
		timer += delay;
		
		fade_in_timer = Util.stepTo(fade_in_timer, 0, delay);
		message_fade_timer = Util.stepTo(message_fade_timer, 0, delay);

		
		if (fade_out_timer < 0) {
			
			world.step(delay, 6, 2);
			
			for(Entity e : entities) e.update(delay);
			
			Vector2 pp = player.body.getPosition();
			camera.set(Util.clamp(pp.x * B2W, minX, maxX), Util.clamp(pp.y * B2W, minY, maxY));
			float rumble = 1.5f*(player.hurt_anim_timer / player.hurt_anim_delay);
			camera.add(Util.randomRange(-rumble,rumble), Util.randomRange(-rumble,rumble));
			
			removeEntities();
		} else
			fade_out_timer = Util.stepTo(fade_out_timer, 0, delay);
	}
	
	public void render(SpriteBatch batch) {
		Collections.sort(entities, Entity.SortByY);
		for(Entity e : entities) e.render(batch);
	}
	
	public void renderHud(SpriteBatch batch) {
		int w = LD33Game.V_WIDTH, h = LD33Game.V_HEIGHT;
		
		// Draw Health
		int hbl = (player.health <= Player.max_health*0.2f && timer % 0.5f > 0.25f) ? 1 : 0;
		float health = player.health, mh = Player.max_health / 20;
		int heart = (int) Math.ceil(health / mh);
		TextureRegion hf = Assets.smallspritesheet[0][0];
		TextureRegion hh = Assets.smallspritesheet[0][1];
		TextureRegion he = Assets.smallspritesheet[0][2], t;
		for(int i = 0; i < 10; i++) {
			if (heart > 2*i+1) t = hf;
			else if (heart < 2*i+1) t = he;
			else t = hh;
			
			batch.draw(t, 2+i*6, 4+hbl);
		}
		// Draw meat bar (lol)
		int mbl = (game.meat >= GameScreen.targetMeat && timer % 0.5f > 0.25f) ? 1 : 0;
		for(int i = 0; i < 4; i++) {
			batch.draw(Assets.smallspritesheet[1][i], i*8 +2, h - 10 + mbl);
		}
		batch.draw(Assets.smallspritesheet[1][4], 4*8 + 4, h - 10 + mbl, 48, 8);
		if (game.meat > 0)
			batch.draw(Assets.smallspritesheet[1][5], 4*8 + 5, h - 10 + mbl, 46 * Util.clamp(game.meat / GameScreen.targetMeat,0,1), 8);
		
		// Damage indication
		batch.setColor(0.7f,0,0,0.5f*(player.hurt_anim_timer/player.hurt_anim_delay));
		batch.draw(Assets.fillTexture,0,0);
		
		// Draw initial message
		if (message_fade_timer > 0) {
			float a = Util.clamp(message_fade_timer, 0, 1f);
			Assets.defaultFont.setColor(1, 1, 1, a);
			Assets.defaultFont.getData().setScale(0.61f);
			Assets.defaultFont.draw(batch, "Get meat and return to cave", w/2, h - 10);
			Assets.defaultFont.setColor(Color.WHITE);
		}
		
		// Draw sound/music buttons
		if (LD33Game.music_on) batch.setColor(Color.WHITE); else batch.setColor(Color.LIGHT_GRAY);
		batch.draw(Assets.smallspritesheet[0][4], w-8-1, h-8-1);
		if (LD33Game.sound_on) batch.setColor(Color.WHITE); else batch.setColor(Color.LIGHT_GRAY);
		batch.draw(Assets.smallspritesheet[0][3], w-16-3, h-8-1);
		
		if (fade_in_timer > 0 || fade_out_timer >= 0) {
			float a = (fade_in_timer)/fade_in_dur + ((fade_out_timer < 0) ? 0 : 1.0f-(fade_out_timer/fade_out_dur)); 
			batch.setColor(0,0,0,a);
			batch.draw(Assets.fillTexture,0,0);
		}
		batch.setColor(Color.WHITE);
	}

	private void removeEntities() {
		Iterator<Entity> it = entities.iterator();
		while(it.hasNext()) {
			Entity e = it.next();
			if (e.remove) it.remove();
			e.destroy();	
		}		
	}
	
	public void renderTiles(OrthographicCamera camera) {
		tileRenderer.setView(camera);
		tileRenderer.render();
	}

	public void destroy() {
		world.dispose();		
	}

	public World getWorld() { return world; }
	public Vector2 getCamera() { return camera; }


	public void gotoLevel(String tl, String ts) {
		if (tl == null) {
			levelChange = false;
			targetLevel = null;	targetSpawn = null;
			fade_out_timer = -1;
		} else {
			fade_out_timer = fade_out_dur;
			levelChange = true;
			targetLevel = tl; targetSpawn = ts;
		}
	}
	
	public void gotoSpawn(String spawn, float health) {
		if (spawn == null ) {gotoSpawn("default", health); return;}
		if (!spawns.containsKey(spawn)) {System.out.println("Invalid spawn " + spawn +". Using default."); return;}
		
		fade_in_timer = fade_in_dur;
		
		player.health = health;
		player.body.setTransform(spawns.get(spawn), 0);
		camera.set(spawns.get(spawn));
	}
	
}
