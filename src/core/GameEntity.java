package core;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

/**
 * Entidade de jogo.
 */
public interface GameEntity {
	void dispose() throws SlickException;

	void update(GameContainer gc, int delta) throws SlickException;

	void render(GameContainer gc, Graphics g, Vector2f position) throws SlickException;
}
