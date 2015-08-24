package com.kr_eddie.ld33.levels;

import static com.kr_eddie.ld33.screens.GameScreen.W2B;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.kr_eddie.ld33.entities.Collideable;
import com.kr_eddie.ld33.entities.Player;
import com.kr_eddie.ld33.screens.GameScreen;

public class Trigger implements Collideable {

	Level level;
	Body body;
	String name,targetLevel, targetSpawn;
	boolean win;
	
	public int count;
	
	public Trigger(Level level, MapObject o) {
		this.level = level;
		
		name = o.getName();
		targetLevel = (String) o.getProperties().get("targetLevel");
		targetSpawn = (String) o.getProperties().get("targetSpawn");
		win = o.getProperties().containsKey("win");
		
		count = 0;
	
		PolylineMapObject polyline = (PolylineMapObject) o;
		float points[] = polyline.getPolyline().getVertices();
		for(int i = 0; i < points.length; i++) points[i] *= W2B;
					
		float x = ((Float) polyline.getProperties().get("x")) * W2B;
		float y = ((Float) polyline.getProperties().get("y")) * W2B;
			
		ChainShape shape = new ChainShape();
		shape.createChain(points);
			
		BodyDef bdef = new BodyDef();
		FixtureDef fdef = new FixtureDef();
		
		bdef.type = BodyType.StaticBody;
		bdef.position.set(x,y);
		fdef.shape = shape;
		fdef.isSensor = true;
			
		body = level.getWorld().createBody(bdef);
		body.setUserData(this);
		body.createFixture(fdef);
			
		shape.dispose();
		
	}
	
	
	public void activate() {
		count++;
		
		if (win && level.game.meat >= GameScreen.targetMeat) level.gotoLevel("win", null);
		
		if (targetLevel != null) level.gotoLevel(targetLevel, targetSpawn);
	}


	@Override
	public void collision(Collideable o) {
		if (o instanceof Player) activate();		
	} 
	
}
