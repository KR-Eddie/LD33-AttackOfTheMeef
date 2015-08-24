package com.kr_eddie.ld33.entities;


import static com.kr_eddie.ld33.screens.GameScreen.B2W;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.kr_eddie.ld33.Assets;
import com.kr_eddie.ld33.LD33Game.GameKey;
import com.kr_eddie.ld33.Util;
import com.kr_eddie.ld33.levels.Level;

public class Player extends Entity {	
	static TextureRegion sprite_side[][], sprite_up[][], sprite_down[][];
	static TextureRegion sprite_death[];
	static boolean initSprites = true;
	static void initSprites() {		
		sprite_side = new TextureRegion[3][];
		sprite_up = new TextureRegion[3][];
		sprite_down = new TextureRegion[3][];
		
		for(int i = 0; i < 2; i++) {
			sprite_side[i] = Util.fromSpriteSheet(Assets.spritesheet, 4*i, 0, 4);
			sprite_down[i] = Util.fromSpriteSheet(Assets.spritesheet, 4*i, 1, 4);
			sprite_up[i] = Util.fromSpriteSheet(Assets.spritesheet, 4*i, 2, 4);	
		}
		sprite_side[2] = Util.fromSpriteSheet(Assets.spritesheet, 0, 4, 4);
		sprite_down[2] = Util.fromSpriteSheet(Assets.spritesheet, 0, 5, 4);
		sprite_up[2] = Util.fromSpriteSheet(Assets.spritesheet, 0, 6, 4);
		
		sprite_death = Util.fromSpriteSheet(Assets.spritesheet, 0, 3, 5);
			
		initSprites = false;
	}
	public static float punchRange = 0.5f;
	public static float punchDamage = 25f;
	public static float punchThrow = 10f;
	public static float ramDamage = 5f;
	
	public static float normalSpeed = 5f;
	public static float sprintSpeed = 5f;
	public static float sprintAcc = 5f / 0.75f;
	
	public static final int max_health = 250;
	
	
	Direction direction;
	boolean punching;
	float punching_anim_timer, punching_anim_delay;
	float sprint;
	float dead_timer;
	
	public Player(Level level) {
		super(level);
		
		if (initSprites) initSprites();	

		size = 0.8f;
		health = max_health;
		direction = Direction.RIGHT; sprite = sprite_side[0];
		
		punching = false;
		punching_anim_timer = 0;
		punching_anim_delay = 0.1f;
		
		sprint = 0;
		
		anim_delay = 1/8f;
		
		BodyDef bdef = new BodyDef();
		FixtureDef fdef = new FixtureDef();
		CircleShape shape = new CircleShape();
		shape.setRadius(size/2);
				
		bdef.type = BodyType.DynamicBody;
		fdef.shape = shape;
		
		body = world.createBody(bdef);
		body.setUserData(this);
		body.createFixture(fdef);
		
		shape.dispose();
	}
	

	Vector2 vector = new Vector2();
	@Override
	public void update(float delay) {
		
		//Logic
		vector.set(v);
		v.set(0,0);
				
		if (dead) dead_timer+=delay;
		
		if (!dead && Util.isKeyPressed(GameKey.SPRINT))
			sprint = Util.stepTo(sprint, sprintSpeed, sprintAcc*delay);
		else 
			sprint = Util.stepTo(sprint, 0, 2*sprintAcc*delay);
		
		float s = normalSpeed + sprint;
		boolean sprinting = (sprint > 0.75f*sprintSpeed);
		
		if (!dead) {
			Vector2 p = body.getPosition();			
					
			if (Util.isKeyPressed(GameKey.UP)) {v.y = s; direction = Direction.UP;} 
			if (Util.isKeyPressed(GameKey.DOWN)) {v.y = -s; direction = Direction.DOWN;}
			if (Util.isKeyPressed(GameKey.LEFT)) {v.x = -s; direction = Direction.LEFT;}
			if (Util.isKeyPressed(GameKey.RIGHT)) {v.x = s; direction = Direction.RIGHT;}			
			if (vector.angle() != v.angle()) {s = normalSpeed; sprint=0; sprinting=false;}
			
			// Punching
			punching_anim_timer = Util.stepTo(punching_anim_timer, 0, delay);
			if (Util.isKeyPressed(GameKey.PUNCH) && punching == false && sprinting==false) {
				punching = true; punching_anim_timer = punching_anim_delay;
				// Brute forcing it like a boss
				float pd2 = (punchRange + size/2); pd2 *= pd2;
				for(Entity e : level.entities) {
					if (e == this) continue;
					vector.set(e.body.getPosition());
					vector.sub(p);
					if (vector.len2() > (pd2 + e.size*e.size*0.25f)) continue;
					float ad = Math.abs(Util.modularDifference(vector.angle(), direction.dir, 360f));
					if (ad > 60) continue;
					
					
					e.damage(punchDamage);
					if (!e.dead){
						e.ev.add(punchThrow*direction.x,punchThrow*direction.y);
						Util.play(Assets.punch_snd);
					}
				}
				
			} else if (!Util.isKeyPressed(GameKey.PUNCH)) {
				punching = false;
			}
			
			if (!v.isZero()) anim_speed = s/normalSpeed; else {anim_speed = 0; anim_index = 0;}
			
		} else if (dead_timer > 4f && level.fade_out_timer < 0) {
			level.fade_out_timer = level.fade_out_dur;
		} 
			
			
		// Graphic		
		int state;
		if (!sprinting) state = punching_anim_timer == 0 ? 0 : 1;
		else state = 2;
		
		switch(direction) {
		case UP: sprite = sprite_up[state]; break;
		case DOWN: sprite = sprite_down[state]; break;
		default: sprite = sprite_side[state];
		}
		if (dead) sprite = sprite_death;
		
		super.update(delay);
	}
	
	@Override
	public void die() {
		super.die();
		anim_index = 0;
		anim_speed = 1;
		anim_loop = false;
		
		dead_timer = 0;
	}
	
	@Override
	public void damage(float value) {
		super.damage(value);
		
		if (!dead) Util.play(Assets.hit_snd);
	}
	
	
	@Override
	public void collision(Collideable o) {
		if (o instanceof AngryVillager) {
			AngryVillager v = (AngryVillager) o;
			
			if (sprint > 0.75f*sprintSpeed) {
				v.damage(body.getLinearVelocity().len() * ramDamage);
				if (!v.dead) v.ev.add(punchThrow*direction.x,punchThrow*direction.y);
				Util.play(Assets.punch_snd);
			}
			sprint = Util.stepTo(sprint, 0, sprintAcc/10f);
		}
		
	};
	
	Affine2 t = new Affine2();
	Color color = new Color();
	@Override
	public void render(SpriteBatch batch) {
		
		Vector2 p = body.getPosition();

		TextureRegion s = null;
		float scaleX = (direction == Direction.LEFT) ?  -1 : 1;
	
		s = sprite[anim_index];
		
		t.idt();		
		t.translate((p.x - size/2) * B2W, (p.y - size/2) * B2W);
		t.scale(scaleX, 1);
		t.translate(-(1-scaleX)/2 * 16, 0);
		
		color.set(Color.WHITE);
		float hi = 1- hurt_anim_timer/hurt_anim_delay;
		color.mul(1, hi , hi, 1);
		batch.setColor(color);
		batch.draw(s, 16, 16, t);
		batch.setColor(Color.WHITE);
		
	}
	
}
