package com.kr_eddie.ld33.levels;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.kr_eddie.ld33.entities.Collideable;

public class LevelContactListener implements ContactListener{

	Level level;

	public LevelContactListener(Level level) {
		this.level = level;
	}
	
	@Override
	public void beginContact(Contact contact) {
		
		Fixture fa = contact.getFixtureA();
		Fixture fb = contact.getFixtureB();
				
		Body ba = fa.getBody();
		Body bb = fb.getBody();
		
		if (ba == null || bb == null) return;
		if (ba.getUserData() == null || bb.getUserData() == null) return;
		
		Collideable ca = (Collideable) ba.getUserData();
		Collideable cb = (Collideable) bb.getUserData();
		
		ca.collision(cb);
		cb.collision(ca);
	}

	@Override
	public void endContact(Contact contact) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		// TODO Auto-generated method stub
		
	}

}
