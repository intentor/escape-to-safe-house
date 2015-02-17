package core;

import org.newdawn.slick.Animation;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

/**
 * Representa o jogador.
 */
public class Enemy implements GameEntity {
	
	/**
	 * Largura do inimigo, em tiles.
	 */
	public static final float ENEMY_WIDTH = 1;
	
	/**
	 * Altura do inimigo, em tiles.
	 */
	public static final float ENEMY_HEIGHT = 2;
	                                   
	/**
	 * Velocidade da movimenta��o.
	 */
	private static final float MOVE_SPEED = 0.025f;
	
	/**
	 * Posi��o do inimigo em rela��o ao in�cio do cen�rio.
	 */
	public Vector2f location;
	
	/**
	 * Mapa de colis�o.
	 */
	public boolean[][] collision;
	
	/**
	 * Indica se o inimigo morreu.
	 */
	public boolean isDead;
	
	/**
	 * Largura do mapa..
	 */
	private int mapWidth;
	
	/**
	 * Fator de movimenta��o, utilizado para mover para esquerda ou direita.
	 */
	private int movementFactor;

	/**
	 * Anima��o do inimigo andando.
	 */
	private Animation walking;
	
	/**
	 * Indica se se deve flipar o personagem na horizontal.
	 */
	private boolean flipHorizontal;
	
	/**
	 * Cria um novo jogador.
	 * @param mapWidth	Largura do mapa de tiles.
	 * @param location	Localiza��o do inimigo em tela.
	 * @throws SlickException 
	 */
	public Enemy(int mapWidth, Vector2f location) throws SlickException {
		this.mapWidth = mapWidth;
		this.location = location;
		
		this.movementFactor = 1; //Move inicialmente para a esquerda.
		
		this.walking = AssetsUtil.loadAnimation("assets/sprites/characters/zombie/walking.def", "walking", 60);
	}
	
	@Override
	public void dispose() throws SlickException {
		
	}

	@Override
	public void update(GameContainer gc, int delta) throws SlickException {
		/*Move o inimigo de um lado para outro, avaliando colis�es e impedindo quedas.
		 * Inicialmente o inimigo sempre se move para a direita
		 */
		
		this.location.x += this.movementFactor * MOVE_SPEED;
		
		if (this.movementFactor > 0) { //Movimento para a direita.
			if (this.location.x > this.mapWidth - 1) {
				this.location.x = this.mapWidth - 1;
				this.movementFactor = -this.movementFactor;
				this.flipHorizontal = true;
				this.walking.restart();
			} else if (this.location.x < (this.mapWidth - 1) && 
				(this.isBlocked(this.location.x + 1, this.location.y) || //Checa se a posi��o atual � colis�o.
				!this.isBlocked(this.location.x + 1, this.location.y + 1))){ //Checa se a posi��o abaixo � livre.
				this.location.x = (int)this.location.x;
				this.movementFactor = -this.movementFactor;
				this.flipHorizontal = true;
				this.walking.restart();
			}
		} else { //Movimento para a esquerda. 
			if (this.location.x < 0) {
				this.location.x = 0;
				this.movementFactor = -this.movementFactor;
				this.flipHorizontal = false;
				this.walking.restart();
			} else if (this.location.x >= 1 && 
				(this.isBlocked(this.location.x, this.location.y) || //Checa se a posi��o atual � colis�o.
				!this.isBlocked(this.location.x, this.location.y + 1))){ //Checa se a posi��o abaixo � livre.
				this.location.x = (int)this.location.x + 1;
				this.movementFactor = -this.movementFactor;
				this.flipHorizontal = false;
				this.walking.restart();
			}
		}
			
		this.walking.update(delta);
	}

	@Override
	public void render(GameContainer gc, Graphics g, Vector2f position) throws SlickException {
		g.setColor(org.newdawn.slick.Color.green);

		Image img = this.walking.getCurrentFrame().getFlippedCopy(this.flipHorizontal, false);
		img.draw(position.x, position.y);
	}
	
	//M�TODOS DE APOIO=================================================================
		
	/**
	 * Verifica se uma determinada posi��o est� bloqueada.
	 * 
	 * @param x Coordenada X da posi��o a ser analisadaddd.
	 * @param y Coordenada Y da posi��o a ser analisada.
	 * @return Valor booleano indicando se a posi��o est� bloqueada.
	 */
	private boolean isBlocked(float x, float y) {
		return this.collision[(int)x][(int)y];
	}
}
