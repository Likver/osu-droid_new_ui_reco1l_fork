package org.anddev.andengine.engine.handler;

import org.anddev.andengine.util.IMatcher;

/**
 * (c) 2010 Nicolas Gramlich 
 * (c) 2011 Zynga Inc.
 * 
 * @author Nicolas Gramlich
 * @since 12:24:09 - 11.03.2010
 */
public interface IUpdateHandler {
	// ===========================================================
	// Final Fields
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	void onUpdate(final float pSecondsElapsed);
	default void reset() {}
	
	// TODO Maybe add onRegister and onUnregister. (Maybe add SimpleUpdateHandler that implements all methods, but onUpdate)

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
	
	public interface IUpdateHandlerMatcher extends IMatcher<IUpdateHandler> {
		// ===========================================================
		// Constants
		// ===========================================================

		// ===========================================================
		// Methods
		// ===========================================================
	}
}
