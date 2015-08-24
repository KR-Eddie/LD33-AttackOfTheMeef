package com.kr_eddie.ld33.entities;

import java.util.Comparator;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.kr_eddie.ld33.Util;
import com.kr_eddie.ld33.levels.Level;

public class Entity implements Collideable {
	public static enum Direction {
		UP(0,1,90f), DOWN(0,-1,270f), LEFT(-1,0,180f), RIGHT(1,0,0f); 
		public int x, y; 
		public float dir;
		Direction(int xx, int yy, float d) {x = xx; y = yy; dir = d;}
	};
	
	private static int idCount = 1;
	public final int id;
	
	Level level;
	World world;
	public Body body;
	public boolean remove;
	
	TextureRegion[] sprite;
	public float anim_timer, anim_delay, anim_speed; int anim_index; boolean anim_loop;
	public float hurt_anim_timer, hurt_anim_delay;
	
	float size;
	public float health; boolean dead;
	
	Vector2 v, ev; float efriction;
	
	public Entity(Level level) {
		this.id = idCount++;
		
		this.level = level;
		this.world = level.getWorld();
		
		remove = false;
		
		size = 1;
		health = 100;
		dead = false;
		
		anim_loop = true;
		anim_timer = 0;
		anim_delay = -1;
		anim_index = 0;
		anim_speed = 1;
		
		hurt_anim_timer = 0f;
		hurt_anim_delay = 1f;
		
		v = new Vector2(0,0);
		ev = new Vector2(0,0);
		efriction = 20f;
	}
	
	public void update(float delay) {

		// Animation
		if (anim_delay > 0) {
			anim_timer += delay * anim_speed;
			while (anim_timer > anim_delay) {
				anim_index++;			
				anim_timer -= anim_delay;
			}
			if (!anim_loop && anim_index >= sprite.length) anim_index = sprite.length-1;
		} else {
			anim_index = 0;
		}
		anim_index %= sprite.length;
		
		hurt_anim_timer = Util.stepTo(hurt_anim_timer, 0, delay);
		
		// Health
		if (health <= 0 && !dead) die();
		
		//Speed
		body.setLinearVelocity(v.x + ev.x, v.y + ev.y);
		float el = ev.len();
		el = Util.stepTo(el, 0, efriction * delay);
		ev.setLength(el);
	}
	
	public void damage(float value) {
		if (health > 0) hurt_anim_timer = hurt_anim_delay;
		health = Util.stepTo(health, 0, value);
	}
	
	public void die() {
		dead = true;		
	}
	
	public void render(SpriteBatch batch) {}
	
	public void destroy() {}

	public void collision(Collideable o) {		
	}
	
	public boolean isDead() {return dead;}
	
	
	public static Comparator<Entity> SortByY = new Comparator<Entity>() {
		@Override
		public int compare(Entity o1, Entity o2) {
			return Float.compare(o2.body.getPosition().y, o1.body.getPosition().y);
		}		
	};
	
}
