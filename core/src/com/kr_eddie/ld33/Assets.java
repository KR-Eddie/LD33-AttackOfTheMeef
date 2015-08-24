package com.kr_eddie.ld33;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Assets {

	public static Texture spritesheetTexture;
	public static Texture fillTexture;
	
	public static final int ssWidth = 16, ssHeight = 16, spriteSize = 16;
	
	public static TextureRegion spritesheet[][];
	public static TextureRegion smallspritesheet[][]; 
	public static TextureRegion win_textTexture;
	public static TextureRegion lose_textTexture;
	public static TextureRegion titleTexture;
	public static TextureRegion minotaur_texture;
	
	public static BitmapFont defaultFont;
	
	
	public static Sound punch_snd, hit_snd; 
	public static Music main_music;
	
	
	public static void createAssets() {
		spritesheetTexture = new Texture(Gdx.files.internal("spritesheet.png"));
		
		int l = spriteSize, ls = l/2;
		
		
		spritesheet = TextureRegion.split(spritesheetTexture, spriteSize, spriteSize);
		
		int ssw = 6, ssh = 2;
		smallspritesheet = new TextureRegion[ssh][];		
		for(int j = 0; j < ssh; j++) {
			smallspritesheet[j] = new TextureRegion[ssw];
			for(int i = 0; i < ssw; i++)
				smallspritesheet[j][i] = new TextureRegion(spritesheetTexture, 5*l + i*ls, 3*l + j*ls, ls, ls);
		}		
		
		
		win_textTexture = new TextureRegion(spritesheetTexture, 0 , 7*l, 7*l, 2*l);
		lose_textTexture = new TextureRegion(spritesheetTexture, 0 , 9*l, 7*l, 2*l);
		titleTexture = new TextureRegion(spritesheetTexture, 7*l , 9*l, 7*l, 2*l);
		minotaur_texture = new TextureRegion(spritesheetTexture, 14*l , 3*l, 2*l, 2*l);
		
		Pixmap p = new Pixmap(LD33Game.V_WIDTH, LD33Game.V_HEIGHT, Format.RGBA8888);
		p.setColor(1, 1, 1, 1);
		p.fill();
		fillTexture = new Texture(p);
		
		defaultFont = new BitmapFont();
		
		
		punch_snd = Gdx.audio.newSound(Gdx.files.internal("sounds/Punch.wav"));
		hit_snd = Gdx.audio.newSound(Gdx.files.internal("sounds/Hit_Hurt.wav"));
		
		main_music = Gdx.audio.newMusic(Gdx.files.internal("sounds/BeingAMonster.mp3"));
		
	}
	
}
