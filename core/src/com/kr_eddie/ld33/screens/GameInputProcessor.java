package com.kr_eddie.ld33.screens;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.kr_eddie.ld33.LD33Game;
import com.kr_eddie.ld33.Util;

public class GameInputProcessor implements InputProcessor {

	private OrthographicCamera camera;

	public GameInputProcessor(OrthographicCamera camera) {
		this.camera = camera;
	}

	@Override
	public boolean keyDown(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	Vector3 v = new Vector3();
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if (button == Input.Buttons.LEFT) {
			int w = LD33Game.V_WIDTH, h = LD33Game.V_HEIGHT;
			v.set(screenX, screenY, 0);
			camera.position.set(w/2,h/2,0);
			camera.update();
			camera.unproject(v);
			
			if (Util.between(v.y,h-10,h)) {
				if (Util.between(v.x, w-10, w)) LD33Game.music_on ^= true;
				if (Util.between(v.x, w-20, w-10)) LD33Game.sound_on ^= true;
			}
		}
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}

}
