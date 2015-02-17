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
	 * Aceleração do pulo.
	 */
	private static final float JUMP_ACCELERATION = 0.02f;
	                                   
	/**
	 * Velocidade da movimentação.
	 */
	private static final float MOVE_SPEED = 0.2f;
	
	/**
	 * Máximo tamanho de uma queda sem causar morte, em tiles.
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
	 * Posição do jogador em relação ao início do cenário.
	 */
	public Vector2f location;
	
	/**
	 * Mapa de colisão.
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
	 * Animação de personagem parado.
	 */
	private Animation stopped;

	/**
	 * Animação de personagem andando.
	 */
	private Animation walking;
	
	/**
	 * Animação de personagem pulando.
	 */
	private Animation jumping;
	
	/**
	 * Animação atual do personagem.
	 */
	private Animation currentAnimation;
	
	/**
	 * Indica se se deve flipar o personagem na horizontal.
	 */
	private boolean flipHorizontal;
	
	/**
	 * Indica se o personagem está andando.
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
		/* Caso o tile abaixo do jogador não esteja bloqueado, permite que
		 * ele caia até o primeiro tile bloqueado que encontrar.
		 * Havendo velocidade de movimento, prossegue com sua execução.
		 */
		if (this.jumpSpeed != 0 ||
			((int)this.location.y < this.mapHeight && !this.isBlocked(this.location.x + 0.5f, this.location.y + 1))) {
			this.jumpSpeed += JUMP_ACCELERATION; //Sempre soma pois ele virá negativo se for para subir.
			this.location.y += this.jumpSpeed;
			//Se estiver em queda, soma a aceleração também ao tamanho da queda.
			if (this.jumpSpeed > 0) this.fallSize += JUMP_ACCELERATION;
		}
		
		//Verifica se o jogador caiu.
		if ((int)this.location.y == (this.mapHeight - 1)) {
			this.isDead = true;
		} else if (this.jumpSpeed != 0 && !this.isDead && this.isBlocked(this.location.x + 0.5f, this.location.y + 1)) {
			//Caso o jogador esteja em uma região de colisão no eixo Y, trava sua posição nesse eixo.
			this.jumpSpeed = 0;
			this.location.y = (int)this.location.y;
			this.soundFall.play();
			
			//Verifica se o jogador ultrapssou o máximo tamanho de um pulo.
			if (this.fallSize * 10 > MAX_FALL_SIZE) this.isDead = true;
			else this.fallSize = 0;
		}
		
		//Verifica qual animação deve ser exibida.
		if (this.jumpSpeed != 0)
			this.currentAnimation = this.jumping;
		else if (this.isWalking)
			this.currentAnimation = this.walking;
		else
			this.currentAnimation = this.stopped;
		
		//Indica que o personagem não está mais andando.
		this.isWalking = false;
		
		//Atualiza a animação atual.
		this.currentAnimation.update(delta);
	}

	@Override
	public void render(GameContainer gc, Graphics g, Vector2f position) throws SlickException {
		g.setColor(org.newdawn.slick.Color.red);
		
		Image img = this.currentAnimation.getCurrentFrame().getFlippedCopy(this.flipHorizontal, false);
		img.draw(position.x, position.y);
	}
	
	//MÉTODOS DE APOIO=================================================================
	
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
		//Verifica se há colisão.
		if (this.location.x < 0) {
			this.location.x = 0;
		} else if (this.location.x >= 1 && this.isBlocked(this.location.x, this.location.y)){
			this.location.x = (int)this.location.x + 1;
		}

		//Indica que há movimento para troca da animação.
		this.isWalking = true;
	}
	
	/**
	 * Move o jogador para a direita.
	 */
	public void moveRight() {
		this.location.x += MOVE_SPEED;
		this.flipHorizontal = false;
		//Verifica se há colisão.
		if (this.location.x > this.mapWidth - 1) {
			this.location.x = this.mapWidth - 1;
		} else if (this.location.x < (this.mapWidth - 1) && this.isBlocked(this.location.x + 1, this.location.y)){
			this.location.x = (int)this.location.x;
		}

		//Indica que há movimento para troca da animação.
		this.isWalking = true;
	}	
	
	/**
	 * Verifica se uma determinada posição está bloqueada.
	 * 
	 * @param x Coordenada X da posição a ser analisadaddd.
	 * @param y Coordenada Y da posição a ser analisada.
	 * @return Valor booleano indicando se a posição está bloqueada.
	 */
	private boolean isBlocked(float x, float y) {
		return this.collision[(int)x][(int)y];
	}
}
