package core;

import org.newdawn.slick.Animation;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.geom.Vector2f;

/**
 * Representa o jogador.
 */
public class GamePlayer implements GameEntity {
	
	/**
	 * Largura do jogador, em tiles.
	 */
	public static final float PLAYER_WIDTH = 1;
	
	/**
	 * Altura do jogador, em tiles.
	 */
	public static final float PLAYER_HEIGHT = 2;

	/**
	 * Velocidade do pulo.
	 */
	private static final float JUMP_SPEED = 0.35f;
	
	/**
	 * Acelera��o do pulo.
	 */
	private static final float JUMP_ACCELERATION = 0.02f;
	                                   
	/**
	 * Velocidade da movimenta��o.
	 */
	private static final float MOVE_SPEED = 0.2f;
	
	/**
	 * M�ximo tamanho de uma queda sem causar morte, em tiles.
	 */
	private static final float MAX_FALL_SIZE = 6;	
	
	/**
	 * Som de pulo.
	 */
    private Sound soundJump;
    
	/**
	 * Som de queda no solo.
	 */
    private Sound soundFall;
	
	/**
	 * Posi��o do jogador em rela��o ao in�cio do cen�rio.
	 */
	public Vector2f location;
	
	/**
	 * Mapa de colis�o.
	 */
	public boolean[][] collision;
	
	/**
	 * Indica se o personagem morreu.
	 */
	public boolean isDead;
	
	/**
	 * Largura do mapa..
	 */
	private int mapWidth;
	
	/**
	 * Altura do mapa.
	 */
	private int mapHeight;
	
	/**
	 * Velocidade do pulo.
	 */
	private float jumpSpeed;
	
	/**
	 * Tamanho da queda.
	 */
	private float fallSize;
	
	/**
	 * Anima��o de personagem parado.
	 */
	private Animation stopped;

	/**
	 * Anima��o de personagem andando.
	 */
	private Animation walking;
	
	/**
	 * Anima��o de personagem pulando.
	 */
	private Animation jumping;
	
	/**
	 * Anima��o atual do personagem.
	 */
	private Animation currentAnimation;
	
	/**
	 * Indica se se deve flipar o personagem na horizontal.
	 */
	private boolean flipHorizontal;
	
	/**
	 * Indica se o personagem est� andando.
	 */
	private boolean isWalking;
	
	/**
	 * Cria um novo jogador.
	 * @param mapWidth	Largura do mapa de tiles.
	 * @param mapHeight Altura do mapa de tiles.
	 * @throws SlickException 
	 */
	public GamePlayer(int mapWidth, int mapHeight) throws SlickException {
		this.mapWidth = mapWidth;
		this.mapHeight = mapHeight;
		
		this.soundJump = new Sound("assets/soundfx/jump.ogg");
		this.soundFall = new Sound("assets/soundfx/fall.ogg");
		this.stopped = AssetsUtil.loadAnimation("assets/sprites/characters/main/stopped.def", "stopped", 70);
		this.walking = AssetsUtil.loadAnimation("assets/sprites/characters/main/walking.def", "walking", 20);
		int jumpDuration[] = { 15, 15, 15, 15, 15, 15, 15, 15, 15, 900, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20};
		this.jumping = AssetsUtil.loadAnimation("assets/sprites/characters/main/jumping.def", "jumping", jumpDuration);
		
		this.currentAnimation = this.walking;
	}
	
	@Override
	public void dispose() throws SlickException {
		this.soundJump.stop();
		this.soundJump = null;
		this.soundFall.stop();
		this.soundFall = null;
	}

	@Override
	public void update(GameContainer gc, int delta) throws SlickException {
		/* Caso o tile abaixo do jogador n�o esteja bloqueado, permite que
		 * ele caia at� o primeiro tile bloqueado que encontrar.
		 * Havendo velocidade de movimento, prossegue com sua execu��o.
		 */
		if (this.jumpSpeed != 0 ||
			((int)this.location.y < this.mapHeight && !this.isBlocked(this.location.x + 0.5f, this.location.y + 1))) {
			this.jumpSpeed += JUMP_ACCELERATION; //Sempre soma pois ele vir� negativo se for para subir.
			this.location.y += this.jumpSpeed;
			//Se estiver em queda, soma a acelera��o tamb�m ao tamanho da queda.
			if (this.jumpSpeed > 0) this.fallSize += JUMP_ACCELERATION;
		}
		
		//Verifica se o jogador caiu.
		if ((int)this.location.y == (this.mapHeight - 1)) {
			this.isDead = true;
		} else if (this.jumpSpeed != 0 && !this.isDead && this.isBlocked(this.location.x + 0.5f, this.location.y + 1)) {
			//Caso o jogador esteja em uma regi�o de colis�o no eixo Y, trava sua posi��o nesse eixo.
			this.jumpSpeed = 0;
			this.location.y = (int)this.location.y;
			this.soundFall.play();
			
			//Verifica se o jogador ultrapssou o m�ximo tamanho de um pulo.
			if (this.fallSize * 10 > MAX_FALL_SIZE) this.isDead = true;
			else this.fallSize = 0;
		}
		
		//Verifica qual anima��o deve ser exibida.
		if (this.jumpSpeed != 0)
			this.currentAnimation = this.jumping;
		else if (this.isWalking)
			this.currentAnimation = this.walking;
		else
			this.currentAnimation = this.stopped;
		
		//Indica que o personagem n�o est� mais andando.
		this.isWalking = false;
		
		//Atualiza a anima��o atual.
		this.currentAnimation.update(delta);
	}

	@Override
	public void render(GameContainer gc, Graphics g, Vector2f position) throws SlickException {
		g.setColor(org.newdawn.slick.Color.red);
		
		Image img = this.currentAnimation.getCurrentFrame().getFlippedCopy(this.flipHorizontal, false);
		img.draw(position.x, position.y);
	}
	
	//M�TODOS DE APOIO=================================================================
	
	/**
	 * Executa pulo do jogador.
	 */
	public void Jump() {
		if (this.jumpSpeed == 0) {
			this.soundJump.play();
			this.fallSize = 0;
			this.jumpSpeed = -JUMP_SPEED; //Negativa para permitir que suba.
			this.jumping.setLooping(false);
			this.jumping.restart();
		}
	}
	
	/**
	 * Move o jogador para a esquerda.
	 */
	public void moveLeft() {
		this.location.x -= MOVE_SPEED;
		this.flipHorizontal = true;
		//Verifica se h� colis�o.
		if (this.location.x < 0) {
			this.location.x = 0;
		} else if (this.location.x >= 1 && this.isBlocked(this.location.x, this.location.y)){
			this.location.x = (int)this.location.x + 1;
		}

		//Indica que h� movimento para troca da anima��o.
		this.isWalking = true;
	}
	
	/**
	 * Move o jogador para a direita.
	 */
	public void moveRight() {
		this.location.x += MOVE_SPEED;
		this.flipHorizontal = false;
		//Verifica se h� colis�o.
		if (this.location.x > this.mapWidth - 1) {
			this.location.x = this.mapWidth - 1;
		} else if (this.location.x < (this.mapWidth - 1) && this.isBlocked(this.location.x + 1, this.location.y)){
			this.location.x = (int)this.location.x;
		}

		//Indica que h� movimento para troca da anima��o.
		this.isWalking = true;
	}	
	
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
