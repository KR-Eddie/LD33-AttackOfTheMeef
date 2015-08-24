package com.kr_eddie.ld33;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Vector2;
import com.kr_eddie.ld33.LD33Game.GameKey;

public class Util {
	
	public static Random random = new Random();

	public static Vector2 getMapObjectPosition(MapObject object) {
		Vector2 r = new Vector2();
		MapProperties p = object.getProperties();
		r.set((Float) p.get("x"),(Float) p.get("y"));
		float w = (Float) p.get("width");
		float h = (Float) p.get("height");
		r.add(w/2, h/2 + 16);
		
		return r;
	}
	
	public static TextureRegion[] fromSpriteSheet(TextureRegion[][] spriteSheet, int x, int y, int length) {
		TextureRegion r[] = new TextureRegion[length];		
		for(int i = 0; i < length; i++)	r[i] = spriteSheet[y][x+i];		
		return r;
	}

	public static float stepTo(float a, float b, float s) {
		float d = b-a;
		if (Math.abs(d) < s) return b;
		else return a + Math.signum(d)*s;
	}
	
	public static float modularDifference(float a, float b, float m) {
		float d = b-a;
		while(d < m*0.5) d += m;
		while(d > m*0.5) d -= m;
		return d;
	}

	public static float clamp(float x, float m, float M) {
		return Math.min(M, Math.max(x, m));
	}

	public static float randomRange(float min, float max) {
		return min + random.nextFloat()*(max - min);
	}

	public static boolean between(float x, int a, int b) {
		return x >= a && x <= b;
	}

	public static void play(Sound snd) {
		if (LD33Game.sound_on) snd.play();		
	}

	public static boolean isKeyJustPressed(GameKey key) {
		return Gdx.input.isKeyJustPressed(LD33Game.keybindings.get(key));
	}
	
	public static boolean isKeyPressed(GameKey key) {
		return Gdx.input.isKeyPressed(LD33Game.keybindings.get(key));
	}

}
