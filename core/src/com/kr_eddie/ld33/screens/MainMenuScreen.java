package com.kr_eddie.ld33.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.kr_eddie.ld33.Assets;
import com.kr_eddie.ld33.LD33Game;
import com.kr_eddie.ld33.Util;

public class MainMenuScreen extends ScreenAdapter {

	private SpriteBatch batch;
	private OrthographicCamera camera;
	private FitViewport viewport;
	
	float timer;
	LD33Game game;
	private static final float fade_in_dur = 2f;

	public MainMenuScreen(LD33Game game, float timer) {this.game = game; this.timer = timer;}

	@Override
	public void show() {
		camera = new OrthographicCamera();
		viewport = new FitViewport(LD33Game.V_WIDTH, LD33Game.V_HEIGHT, camera);
		
		batch = new SpriteBatch();
	}

	@Override
	public void render(float delta) {
		if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)) Gdx.app.exit();
		
		timer+=delta;
		
		if (Gdx.input.isKeyJustPressed(Keys.ANY_KEY) && timer < 4f) timer = 4f;
		else if (Gdx.input.isKeyJustPressed(Keys.ENTER)) { game.setScreen(new GameScreen(game)); return;}
		else if (Gdx.input.isKeyJustPressed(Keys.BACKSPACE)) { game.setScreen(new SettingsScreen(game)); return;}
		
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		
		viewport.apply();
		camera.update();
		
		int w = LD33Game.V_WIDTH, h = LD33Game.V_HEIGHT;	
		float fade_in = Util.clamp(timer/fade_in_dur, 0,1);
		float meef_fade_in = Util.clamp((timer-2f)/fade_in_dur, 0,1);
		
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
			
			batch.draw(Assets.titleTexture, -7*16, 0, 14*16, 4*16);
			int bl = (timer % 1f < 0.5f) ? 2 : 0;
			batch.setColor(1, 1, 1, meef_fade_in);
			batch.draw(Assets.minotaur_texture, -w*0.32f -32, -h*0.15f -32 + bl, 64, 64);
			
			if (timer > 4f) { 
				Assets.defaultFont.getData().setScale(0.5f);
				if (timer % 1f < 0.5f) Assets.defaultFont.draw(batch, "Press Enter to play", -16, -24);
				Assets.defaultFont.draw(batch, "Press BackSpace to configure controls", -10, -70);
			}
			
			batch.setColor(0, 0, 0, 1-fade_in);
			batch.draw(Assets.fillTexture, -w/2, -h/2, w, h);
			batch.setColor(Color.WHITE);
		batch.end();
		
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
		
	}


	@Override
	public void hide() {
		dispose();
	}

	@Override
	public void dispose() {	
		batch.dispose();
	}

}
