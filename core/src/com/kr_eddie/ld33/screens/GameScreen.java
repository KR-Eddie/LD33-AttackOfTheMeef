package com.kr_eddie.ld33.screens;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.kr_eddie.ld33.Assets;
import com.kr_eddie.ld33.LD33Game;
import com.kr_eddie.ld33.entities.Player;
import com.kr_eddie.ld33.levels.Level;
//import static com.kr_eddie.ld33.LD33Game.SCALE;


public class GameScreen extends ScreenAdapter {
	LD33Game game;	
	public GameScreen(LD33Game game) { this.game = game; }
	
	// Logic
	public static final float B2W = 16f;
	public static final float W2B = 1/16f;
	
	HashMap<String, Level> levels;
	Level level;
	
	public float meat;
	public static final float targetMeat = 50;
	
	// Render
	OrthographicCamera camera;
	Viewport viewport;
	
	SpriteBatch batch;
	Box2DDebugRenderer debugRenderer; boolean drawDebug = false;
	
	
	Texture texture;
	
	@Override
	public void show() {		
		// Logic		
		levels = new HashMap<String, Level>();
		gotoLevel("level1", null);
		level.message_fade_timer = 15f;
		
		meat = 0;
		
		// Render
		camera = new OrthographicCamera();
		viewport = new FitViewport(LD33Game.V_WIDTH, LD33Game.V_HEIGHT, camera);
		
		batch = new SpriteBatch();
		debugRenderer = new Box2DDebugRenderer();

		 
		texture = new Texture(Gdx.files.internal("badlogic.jpg"));
		
		// Music
		Assets.main_music.setLooping(true);
		Assets.main_music.play();
		
		Gdx.input.setInputProcessor(new GameInputProcessor(camera));
	}

	private void gotoLevel(String name, String spawn) {
		float health = Player.max_health;
		// TODO if player goes to same level
		if (level != null) {
			health = level.player.health;
			if (!level.persistent) {
				levels.remove(level.name);
				level.destroy();	
			}
		}
		
		if (levels.containsKey(name))
			level = levels.get(name);
		else {
			level = new Level(this, name);
			levels.put(name, level);
		}
		
		level.gotoLevel(null, null);
		level.gotoSpawn(spawn, health);
	}

	
	float accum = 0;
	public static final float tickdelay = 1/60f;
	@Override
	public void render(float delta) {
		accum += delta;
		
		if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)) Gdx.app.exit();
		if (Gdx.input.isKeyJustPressed(Keys.O)) drawDebug ^= true;
	
		
		// Level change
		float vol = LD33Game.music_on ? 1 : 0;		
		if (level.levelChange) {
			if (level.targetLevel.equals("win")) {
				vol *= level.fade_out_timer;
				if (level.fade_out_timer == 0) {game.setScreen(new EndGameScreen(game, true)); return;}
			}
			else if (level.fade_out_timer == 0) gotoLevel(level.targetLevel, level.targetSpawn);
		}
		
		if (level.player.isDead()) {
			vol *= level.fade_out_timer;
			if (level.fade_out_timer == 0) {game.setScreen(new EndGameScreen(game, false)); return;}
		}
		Assets.main_music.setVolume(vol);
		
		
		// Logic			
		while(accum > tickdelay) {
			level.update(tickdelay);			
			accum -= tickdelay;
		}		
		
		
		
		// Render
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		
		viewport.apply();
		
		camera.position.set(level.getCamera(),0);
		camera.update();
		
		level.renderTiles(camera);
		
		batch.setProjectionMatrix(camera.combined);
		batch.begin(); 
			level.render(batch);
		batch.end();	
		
		camera.position.set(LD33Game.V_WIDTH/2, LD33Game.V_HEIGHT/2, 0);
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		batch.begin(); 
			level.renderHud(batch);
		batch.end();	
		
				
		if (drawDebug) {
			Matrix4 b2dProjection = camera.combined.cpy();
			b2dProjection.scl(B2W);
			debugRenderer.render(level.getWorld(), b2dProjection);
		}
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
	}


	
	@Override
	public void hide() {
		Assets.main_music.stop();
		dispose();
	}

	@Override
	public void dispose() {
		for(Level level : levels.values()) {
			level.destroy();
		}
		batch.dispose();
	}

}
