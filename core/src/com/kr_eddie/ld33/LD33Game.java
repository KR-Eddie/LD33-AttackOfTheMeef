package com.kr_eddie.ld33;

import java.util.HashMap;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Input.Keys;
import com.kr_eddie.ld33.screens.MainMenuScreen;

public class LD33Game extends Game {
	public static final int V_WIDTH = 240;
	public static final int V_HEIGHT = 160;
	public static final int SCALE = 3;
	
	public static boolean music_on, sound_on;
	
	public static enum GameKey { 
		UP, DOWN, LEFT, RIGHT, PUNCH, SPRINT
	}
	public static HashMap<GameKey, Integer> keybindings;
	static {
		keybindings = new HashMap<LD33Game.GameKey, Integer>();
		keybindings.put(GameKey.UP, Keys.UP);
		keybindings.put(GameKey.DOWN, Keys.DOWN);
		keybindings.put(GameKey.LEFT, Keys.LEFT);
		keybindings.put(GameKey.RIGHT, Keys.RIGHT);
		keybindings.put(GameKey.PUNCH, Keys.SPACE);
		keybindings.put(GameKey.SPRINT, Keys.SHIFT_LEFT);
	}
	
	
	
	@Override
	public void create () {
		music_on = true;
		sound_on = true;
		
		Assets.createAssets();
		
		setScreen(new MainMenuScreen(this,0));
	}

}
