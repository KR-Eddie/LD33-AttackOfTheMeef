package com.kr_eddie.ld33.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.kr_eddie.ld33.Assets;
import com.kr_eddie.ld33.LD33Game;
import com.kr_eddie.ld33.Util;

public class EndGameScreen extends ScreenAdapter {
	LD33Game game;
	private boolean win;	
	public EndGameScreen(LD33Game game, boolean win) { this.game = game; this.win = win; }	
	
	Texture background;
	TextureRegion message;
	SpriteBatch batch;

	OrthographicCamera camera;
	Viewport viewport;
	
	float timer;
	private static final float fade_in_dur = 4f;
	private static final float message_delay = fade_in_dur;
	private static final float message_fade_in_dur = 3f;
	private static final float play_again_delay = message_delay + message_fade_in_dur + 1f;
	private static final float play_again_in_dur = 1f;
	
	@Override
	public void show() {
		camera = new OrthographicCamera();
		viewport = new FitViewport(LD33Game.V_WIDTH, LD33Game.V_HEIGHT, camera);
		
		batch = new SpriteBatch();
		
		if (!win) {
			background = new Texture(Gdx.files.internal("death_scene.png"));
			message = Assets.lose_textTexture;
		} else {
			background = new Texture(Gdx.files.internal("win_scene.png"));
			message = Assets.win_textTexture;
		}
		
		timer = 0;
	}

	Color color = new Color();
	@Override
	public void render(float delta) {
		timer+=delta;
		
		if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {game.setScreen(new MainMenuScreen(game, 10));}
		else if (Gdx.input.isKeyJustPressed(Keys.ANY_KEY)) {
			if (timer < play_again_delay) timer = play_again_delay;
			else game.setScreen(new GameScreen(game));
		}
		
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		
		viewport.apply();
		camera.update();
		
		int w = LD33Game.V_WIDTH, h = LD33Game.V_HEIGHT;		
		float fade_in = Util.clamp((timer-0)/fade_in_dur, 0,1);
		float message_fade_in = Util.clamp((timer- message_delay)/message_fade_in_dur, 0,1);
		
		
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
			batch.setColor(1, 1, 1, fade_in);
			batch.draw(background, -w/2, -h/2, w, h);
			batch.setColor(1, 1, 1, message_fade_in);
			batch.draw(message, 0, 0);
			
			if (timer > play_again_delay) {
				if (timer % play_again_in_dur < play_again_in_dur*0.5f) {
				Assets.defaultFont.getData().setScale(0.6f);				
				Assets.defaultFont.draw(batch, "Press any key to play again",8 , -10);
				}					
			}
			
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
		// TODO Auto-generated method stub
		
	}
	
	

}
