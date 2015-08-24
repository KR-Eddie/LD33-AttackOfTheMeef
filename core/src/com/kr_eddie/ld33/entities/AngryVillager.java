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
import com.kr_eddie.ld33.Util;
import com.kr_eddie.ld33.levels.Level;

public class AngryVillager extends Entity {
	public static enum Type { 
		NORMAL(0, -1, -1, 0), 
		TORCH(1, 0.15f, 0.75f, 10), 
		PITCHFORK(2, 0.25f, 0.5f, 7); 
		
		int i; float range; float attack_delay; float damage;
		Type(int ii, float rr, float ad, float dmg) {i = ii; range = rr; attack_delay = ad; damage = dmg;}
	}
	public static enum State { IDLE(0), MOVE(1), ATTACK(2); int i; State(int ii) {i = ii;}}
	
	public static float alert_range = 5f;
	public static float safe_range = 15f;
	
	
	Type type;
	State state;
	float attack_timer;
	
	Direction direction;	
	// sprite[state][type][index]
	static TextureRegion[][][] sprite_side, sprite_up, sprite_down;
	static TextureRegion[] sprite_dead;
	
	
	static boolean initSprites = true;
	static void initSprites() {		
		int nt = Type.values().length, ns = State.values().length;
		sprite_side = new TextureRegion[ns][][];
		sprite_up = new TextureRegion[ns][][];
		sprite_down = new TextureRegion[ns][][];
		
		for(int i = 0; i < ns; i++) {
			sprite_side[i] = new TextureRegion[nt][];
			sprite_up[i] = new TextureRegion[nt][];
			sprite_down[i] = new TextureRegion[nt][];
		}
		
		for(Type type : Type.values()) {
			initSprites(sprite_down[State.IDLE.i], sprite_down[State.MOVE.i], sprite_down[State.ATTACK.i], type, 0);
			initSprites(sprite_up[State.IDLE.i], sprite_up[State.MOVE.i], sprite_up[State.ATTACK.i], type, 1);
			initSprites(sprite_side[State.IDLE.i], sprite_side[State.MOVE.i], sprite_side[State.ATTACK.i], type, 2);
		}
		
		sprite_dead = new TextureRegion[1];
		sprite_dead[0] = Assets.spritesheet[0][12];
		
		
		initSprites = false;
	}
	static void initSprites(TextureRegion[][] idle, TextureRegion[][] move, TextureRegion[][] attack, Type type, int sh) {
		int i = type.i, x = 8, y = i*3 + sh;
		
		move[i] = Util.fromSpriteSheet(Assets.spritesheet, 8, type.i * 3, 4);
		
		idle[i] = new TextureRegion[2];
		idle[i][0] = Assets.spritesheet[y][x];
		idle[i][1] = Assets.spritesheet[y][x+2];
		
		attack[i] = new TextureRegion[2];
		attack[i][0] = Assets.spritesheet[y][x+4];
		attack[i][1] = Assets.spritesheet[y][x];
	}
		
	
	
	public AngryVillager(Level level, Type t) {
		super(level);
		
		type = t;
		state = State.IDLE;
		
		size = 0.6f;
		
		if (initSprites) initSprites();
		
		direction = Direction.RIGHT; sprite = sprite_side[state.i][type.i];	
		
		anim_timer = Util.random.nextFloat();
		anim_delay = 1/8f;
		
		attack_timer = 0f;
		
		BodyDef bdef = new BodyDef();
		FixtureDef fdef = new FixtureDef();
		CircleShape shape = new CircleShape();
		shape.setRadius(size/2);
				
		bdef.type = BodyType.DynamicBody;
		bdef.fixedRotation = true;
		fdef.shape = shape;
		fdef.density = 0.1f;
		body = world.createBody(bdef);
		body.setUserData(this);
		body.createFixture(fdef);
		
		shape.dispose();
	}

	@Override
	public void update(float delta) {	
		
		// Logic
		if (!dead) {
			Player player = level.player;
			v.set(player.body.getPosition()).sub(body.getPosition());
			float pdist = v.len();
			v.nor().scl(1.5f);
			
			// Change State
			float move_limit = (size + player.size)/2 + 0.05f;
			float attack_limit = (size + player.size)/2 + type.range;
				
			if (pdist > alert_range) state = State.IDLE;
			else if (type.range > 0 && pdist < attack_limit) {
				if (state != State.ATTACK) attack_timer = type.attack_delay;
				state = State.ATTACK;
			}
			else state = State.MOVE;
			
			// Rammed by player
			float ramdist = 0.95f*(size + player.size)/2;
			if (pdist < ramdist ) damage((1 - pdist/ramdist) * Player.ramDamage);
			
			// Attack player
			attack_timer = Util.stepTo(attack_timer, 0, delta);
			if (state == State.ATTACK) {
				if (attack_timer == 0 && pdist < attack_limit) {
					player.damage(type.damage);
					attack_timer = type.attack_delay;
				}
			}
			
			// Turn towards player
			if (state != State.IDLE) {
				float a = v.angle();
				if (a >= 45 && a < 135) direction = Direction.UP;
				else if (a >= 135 && a < 225) direction = Direction.LEFT;
				else if (a >= 225 && a < 315) direction = Direction.DOWN;
				else direction = Direction.RIGHT;
			}
			
			// Move towards player
			if (state == State.MOVE || (state == State.ATTACK && pdist > move_limit)) {
				if (type == Type.NORMAL) v.scl(-1);
			} else
				v.set(0,0);
		} else {
			v.set(0,0);
		}
		
		// Graphic
		switch(direction) {
		case UP: sprite = sprite_up[state.i][type.i]; break;
		case DOWN: sprite = sprite_down[state.i][type.i]; break;
		default: sprite = sprite_side[state.i][type.i];
		}
		if (dead) sprite = sprite_dead;
		
		if (state == State.ATTACK) anim_delay = type.attack_delay / 2;
		else anim_delay = 1/8f;
		
		super.update(delta);
	}
		
	@Override
	public void die() {
		super.die();
		
		level.game.meat++;
	}



	Affine2 t = new Affine2();
	Color color = new Color();
	@Override
	public void render(SpriteBatch batch) {
		
		Vector2 p = body.getPosition();

		TextureRegion s = null;
		float scaleX = (direction == Direction.LEFT) ?  -1 : 1;
	
		s = sprite[anim_index];
		
		t.idt();		
		t.translate((p.x - size/2) * B2W - 3, (p.y - size/2) * B2W);
		t.scale(scaleX, 1);
		t.translate((-(1-scaleX)/2 * 16), 0);
		color.set(Color.WHITE);
		float hi = 1- hurt_anim_timer/hurt_anim_delay;
		color.mul(1, hi , hi, 1);
		batch.setColor(color);
		batch.draw(s, 16, 16, t);
		batch.setColor(Color.WHITE);
		
	}

}
