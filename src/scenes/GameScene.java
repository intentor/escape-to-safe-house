package scenes;

import java.awt.Color;
import java.util.LinkedList;
import java.util.List;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.tiled.TiledMap;

import core.AssetsUtil;
import core.BaseGameScene;
import core.Enemy;
import core.GamePlayer;

public class GameScene extends BaseGameScene {

	/**
	 * Identifica o modo da cena.
	 */
	private enum SceneMode {
		  Loading
		, Starting
		, Playing
		, GameOver
		, GoToNextLevel
	}
	
	/**
	 * Quantidade de itens a serem carregados em tela.
	 */
	private static final int TOTAL_LOADING_RESOURCES = 11;
	
	/** 
	 * Tamanho dos tiles na textura.
	 */
	private static final int STAGE_TIME_LIMIT = 60;
	
	/** 
	 * Tamanho dos tiles na textura.d
	 */
	private static int TILE_SIZE;
	
	/**
	 * Vidas do jogador.
	 */
	public static int LIFES;

	/**
	 * Modo atual da tela.
	 */
	private SceneMode currentMode = SceneMode.Loading;
	
	/**
	 * Indica se o modo de debug está habiitado.
	 */
	private boolean isDebug = false;
	
	/**
	 * Caminho em disco do nível.
	 */
	private String levelPath;
	
	/**
	 * ID do nível.
	 */
	private int levelID;
	
	/**
	 * Índice de carregamento de assets.
	 */
	private int loadingIndex;
	
	/**
	 * Imagem de fundo do cenário.
	 */
	private Image background;
	
	/**
	 * Música de fundo da cena.
	 */
	private Music musBackground;
	
	/**
	 * Música de término de nível.
	 */
	private Music musEndLevel;
	
	/**
	 * Música de fim de jogo.
	 */
	private Music musGameOver;
	
	/**
	 * Fonte do texto de loading.
	 */
	private UnicodeFont fontLoading; 
	
	/**
	 * Fonte de informações do jogo.
	 */
	private UnicodeFont fontInfoBig;
	
	/**
	 * Fonte de nome do nível do jogo.
	 */
	private UnicodeFont fontLevelName;
	
	/**
	 * Fonte de limite de tempo do jogo.
	 */
	private UnicodeFont fontTimeLimit;
	
	/**
	 * Fonte de debug.
	 */
	private UnicodeFont fontDebug;
	
	/**
	 * Jogador.
	 */
	private GamePlayer player;
	
	/**
	 * Inimigos do jogo.
	 */
	private List<Enemy> enemies;
	
	/**
	 * Posição de saída do nível, em tiles.
	 */
	private Vector2f endLevel;
	
	/**
	 * Largura da tela em tiles.
	 */
	private int viewportWidth;

	/**
	 * Altura da tela em tiles.
	 */
	private int viewportHeight;	
	
	/**
	 * Posição central do viewport no eixo X, em tiles.
	 */
	private int viewportCenterX;
	
	/**
	 * Posição central do viewport no eixo Y, em tiles.
	 */
	private int viewportCenterY;

	/**
	 * Mapa de texturas a ser utilizado.
	 */
	private TiledMap map;
	
	/**
	 * Mapa de colisão dos tiles.
	 */
	private boolean[][] collision;
	
	/**
	 * Tempo anterior do sistema, em milisegundos.
	 */
	private long lastSystemTime;
	
	/**
	 * Contador para uso na exibição de textos.
	 */
	private int counter;
	
	/**
	 * Limite de tempo do jogo.
	 */
	private int timeLimit;
	
	/**
	 * Cria uma nova cena de jogo.
	 * @param levelPath Caminho dos arquivos do nível.
	 */
	public GameScene(String levelPath, int levelID) {
		this.levelPath = levelPath;
		this.levelID = levelID;
		LIFES = Loader.LIFES_NUMBER; //Inicia sempre com o número máxmo de vidas.
	}
	
	@Override
	public void init(GameContainer gc) throws SlickException {
		Loader.Container.setMouseCursor("assets/sprites/cursor-empty.png", 0, 0);
		this.fontLoading = AssetsUtil.loadFont("MotorwerkOblique.ttf", 25, true, false, Color.white);
		this.loadingIndex = 0;
		this.counter = 0;
	}
	@Override
	public void dispose() throws SlickException {
		this.background.destroy();
		this.musBackground.stop();
		this.musBackground = null;
		this.musGameOver.stop();
		this.musGameOver = null;
		this.musEndLevel.stop();
		this.musEndLevel = null;
		this.fontLoading.destroy();
		this.fontInfoBig.destroy();
		this.fontLevelName.destroy();
		this.fontTimeLimit.destroy();
		this.fontDebug.destroy();
	}

	@Override
	public void update(GameContainer gc, int delta) throws SlickException {
		switch (this.currentMode) {
			case Loading:
				this.updateLoading(gc, delta);
			break;
			case Starting:
			case Playing:
			case GoToNextLevel:
				this.updateGame(gc, delta);
			break;
			case GameOver:
				this.updateGameOver(gc, delta);
			break;
		}
	}

	@Override
	public void render(GameContainer gc, Graphics g) throws SlickException {
		switch (this.currentMode) {
			case Loading:
				this.renderLoading(gc, g);
			break;
			case Starting:
			case Playing:
			case GoToNextLevel:
				this.renderGame(gc, g);
			break;
			case GameOver:
				this.renderGameOver(gc, g);
			break;
		}		
	}
	
//MÉTODOS DE UPDATE================================================================
	
	public void updateLoading(GameContainer gc, int delta) throws SlickException {
		switch(++this.loadingIndex) {
			case 1:
				this.fontInfoBig = AssetsUtil.loadFont("MotorwerkOblique.ttf", 120, true, false, Color.yellow, Color.gray, 3);
			break;
			case 2:
				this.fontLevelName = AssetsUtil.loadFont("Peric.ttf", 30, true, false, Color.white, Color.gray, 1);
			break;
			case 3:
				this.fontTimeLimit = AssetsUtil.loadFont("Peric.ttf", 45, false, false, Color.white, Color.black, 1);
			break;
			case 4:
				this.fontDebug = AssetsUtil.loadFont("Peric.ttf", 15, false, false, Color.white);
			break;
			case 5:
				this.background = new Image(this.levelPath + "/level.jpg");
			case 6:
				this.map = new TiledMap(this.levelPath + "/level.tmx", "assets/scenes/game");
				TILE_SIZE = this.map.getTileHeight();
			break;
			case 7:
				this.player = new GamePlayer(this.map.getWidth(), this.map.getHeight());
			break;
			case 8:				
				//Configura o mapa de texturas.
				
				/* Propriedades das camadas de tiles:
				 * 	- Camada 0 (Entities): begin (começo do jogo), end (fim do jogo), enemy (inimigo);
				 * 	- Camada 1 (Collisions): todos os tiles que aqui estiverem serão de colisão;
				 * 	- Camada 2 (Scenario): apenas elementos de cenário sem influência no jogo. 
				 */
				
				//Cria o mapa de colisão.
				this.collision = new boolean[this.map.getWidth()][this.map.getHeight()];
				//Cria a lista de inimigos.
				this.enemies = new LinkedList<Enemy>();
				
				for (int x = 0; x < this.map.getWidth(); x++) {
					for (int y = 0; y < this.map.getHeight(); y++) {
						//Camada 0====================================================================
						int tileID = this.map.getTileId(x, y, this.map.getLayerIndex("Entities"));
						//Verifca se a propriedade "begin" é true.
						if (this.map.getTileProperty(tileID, "begin", "false").equals("true")) {
							this.player.location = new Vector2f(x, y);
						}
						//Verifca se a propriedade "end" é true.
						if (this.map.getTileProperty(tileID, "end", "false").equals("true")) {
							this.endLevel = new Vector2f(x, y);
						}
						//Verifca se a propriedade "enemy" é true.
						if (this.map.getTileProperty(tileID, "enemy", "false").equals("true")) {
							Enemy newEnemy = new Enemy(this.map.getWidth(), new Vector2f(x, y));
							this.enemies.add(newEnemy);
						}
						
						//Camada 1====================================================================
						//Verifica e há imagem na camada de colisão.
						if (this.map.getTileImage(x, y, this.map.getLayerIndex("Collisions")) != null) {
							this.collision[x][y] = true;
						}
					}
				}
				
				//Configura o mapa de colisão do jogador.
				this.player.collision = this.collision;

				//Configura o mapa de colisão de cada inimigo.
				for (Enemy e : this.enemies) e.collision = this.collision;
				
				//Obtém o tamanho dos tiles (os tiles devem ser sempre quadrados de tamanho fixo).
				TILE_SIZE = this.map.getTileHeight();
				
				//Define o tamanho da tela em tiles.
				this.viewportWidth = Loader.SCREEN_WIDTH / TILE_SIZE;
				this.viewportHeight = Loader.SCREEN_HEIGHT / TILE_SIZE;
				
				//Obtém o centro da tela em tiles.
				this.viewportCenterX = this.viewportWidth / 2; 
				this.viewportCenterY = this.viewportHeight / 2;
			break;
			case 9:
				this.musBackground = new Music(this.levelPath + "/level.ogg", true);
			break;
			case 10:
				this.musEndLevel = new Music("assets/music/end_level.ogg", true);
			break;
			case 11:
				this.musGameOver = new Music("assets/music/game_over.ogg", true);
			break;
			default:
				this.timeLimit = STAGE_TIME_LIMIT;
				this.lastSystemTime = gc.getTime();
				this.fontLoading.destroy();
				this.musBackground.loop();
				this.musBackground.setVolume(0.5f);
				this.currentMode = SceneMode.Starting;
			break;
		}
	}
	
	public void updateGame(GameContainer gc, int delta) throws SlickException {
		switch (this.currentMode) {
			case Playing:
				Input kb = gc.getInput();
				
				//Verifica se é para habilitar o modo de debug.
				if (kb.isKeyDown(Input.KEY_F1)) this.isDebug = !this.isDebug;
				
				//Poderão ser utilizados tanto as setas quanto WASD para movimentação.
				if (kb.isKeyDown(Input.KEY_LEFT) || kb.isKeyDown(Input.KEY_A)) this.player.moveLeft();
				if (kb.isKeyDown(Input.KEY_RIGHT) || kb.isKeyDown(Input.KEY_D)) this.player.moveRight();
				if (kb.isKeyDown(Input.KEY_UP) || kb.isKeyDown(Input.KEY_SPACE) || kb.isKeyDown(Input.KEY_W)) this.player.Jump();
				
				this.player.update(gc, delta);
				
				if (this.player.isDead) {
					this.currentMode = SceneMode.GameOver;
				} else if ((int)this.player.location.x == this.endLevel.x &&
							(int)this.player.location.y == this.endLevel.y) {
					this.musBackground.stop();
					this.musEndLevel.play();
					this.currentMode = SceneMode.GoToNextLevel;
				}
				
				//Atualiza o contador de tempo da fase.
				long time = gc.getTime();
				if (time - this.lastSystemTime >= 1000) { //Passou 1 segundo
					this.lastSystemTime = time;
					//Verifica se o tempo acabou, o que indica fim de jogo.
					if (--this.timeLimit == 0) {
						this.currentMode = SceneMode.GameOver;
					}
				}
				
				//Sendo GameOver, pára a música do jogo e toca música de fim de jogo.
				if (this.currentMode == SceneMode.GameOver) {
					this.musBackground.stop();
					this.musGameOver.play();
				}
				
				//Atualiza os inimigos, verificando se algum deles morreu.
				for (int i = 0; i < this.enemies.size(); i++) {
					Enemy e = this.enemies.get(i);
					e.update(gc, delta);
					if (e.isDead) {
						e.dispose();
						this.enemies.remove(i--);
					}
				}
			break;
			case GoToNextLevel:
				if (gc.getInput().isMousePressed(Input.MOUSE_LEFT_BUTTON) ||
					gc.getInput().isKeyDown(Input.KEY_ENTER)) {
					Object[] params = new Object[2];
					params[0] = this.levelID; 	//ID do nível.
					params[1] = true;			//Indica se se deve mudar de nível.
					this.fireEndSceneEvent(params);
				}
			break;
		}		
	}
	
	public void updateGameOver(GameContainer gc, int delta) throws SlickException {
		//Verifica se o jogador ainda tem vidas.
		if (--LIFES > 0) {
			//Tendo vidas, reindddddddica a fase.
			this.dispose();
			this.currentMode = SceneMode.Loading;
			this.init(gc);
		} else {
			//Não tendo vidas, indica GameOver.
			if (gc.getInput().isMousePressed(Input.MOUSE_LEFT_BUTTON) ||
				gc.getInput().isKeyDown(Input.KEY_ENTER)) {
				Object[] params = new Object[2];
				params[0] = this.levelID; 	//ID do nível.
				params[1] = false;			//Indica se se deve mudar de nível (no caso, fim de jogo).
				this.fireEndSceneEvent(params);
			}
		}
	}

	//MÉTODOS DE RENDERIZAÇÃO=========================================================

	public void renderLoading(GameContainer gc, Graphics g) throws SlickException {
		String textLoading = "Loading...";
	    int x = 720 - this.fontLoading.getWidth(textLoading);
	    this.fontLoading.drawString(x, 550, textLoading);
	    
	    int percentage = (int) ((this.loadingIndex / (float)TOTAL_LOADING_RESOURCES) * 100);
	    this.fontLoading.drawString(730, 550, String.valueOf(percentage) + "%");
	}
	
	public void renderGame(GameContainer gc, Graphics g) throws SlickException {
		switch (this.currentMode) {
			case Starting:
				if (this.counter++ < 50) {
					AssetsUtil.drawStringCenter(this.fontInfoBig, "Level " + String.valueOf(this.levelID), 180);
					AssetsUtil.drawStringCenter(this.fontLevelName, Loader.NAME_LEVELS[this.levelID - 1], 310);
				} else {
					this.currentMode = SceneMode.Playing;
				}
			break;
			case Playing:
				//Desenha o plano de fundo do nível.
				this.background.draw(0, 0);
				
				int playerTileOffsetX; //Offset em X para suavização de movimento.
				int playerTileOffsetY; //Offset em Y para suavização de movimento.			
				int tilePositionX; //Tile em X a ser renderizado.
				int tilePositionY; //Tile em Y a ser renderizado.
				float playerPositionX; //Posição do jogador em relação ao viewport (em tiles).
				float playerPositionY; //Posição do jogador em relação ao viewport (em tiles).
				
				//Avalia as posição de renderização dos tiles e do jogador.
				
				//Posições no eixo X.
				if (this.player.location.x < this.viewportCenterX) { //Canto esquerdo.
					tilePositionX = 0;
					playerPositionX = this.player.location.x;
					playerTileOffsetX  = 0;
				} else if (this.player.location.x > (this.map.getWidth() - this.viewportCenterX)) { //Canto direito.
					tilePositionX = this.map.getWidth() - this.viewportWidth;
					playerPositionX = (this.viewportWidth - (this.map.getWidth() - this.player.location.x));
					playerTileOffsetX = 0;
				} else { //Movimento centralizado do personagem na tela.
					tilePositionX = (int)(this.player.location.x - this.viewportCenterX);
					playerPositionX = this.viewportCenterX;
					playerTileOffsetX = (int) (((int)this.player.location.x - this.player.location.x) * TILE_SIZE);
				}

				//Posições no eixo Y.
				if (this.player.location.y  < this.viewportCenterY) { //Topo da tela.
					tilePositionY = 0;
					playerPositionY = this.player.location.y;
					playerTileOffsetY  = 0;
				} else if (this.player.location.y > (this.map.getHeight() - this.viewportCenterY)) { //Base da tela
					tilePositionY = this.map.getHeight() - this.viewportHeight;
					playerPositionY = (this.viewportHeight - (this.map.getHeight() - this.player.location.y));
					playerTileOffsetY = 0;
				} else { //Movimento centralizado do personagem na tela.
					tilePositionY = (int)(this.player.location.y - this.viewportCenterY);
					playerPositionY = this.viewportCenterY;
					playerTileOffsetY = (int) (((int)this.player.location.y - this.player.location.y) * TILE_SIZE);
				}
				
				/* Parâmetros:
				 	x - Localização no eixo X para renderização do mapa.
				    y - Localização no eixo Y para renderização do mapa.
				    sx - Localização do tile no eixo X para início de renderização.
				    sy - Localização do tile no eixo Y para início de renderização.
				    width - Largura da seção de tiles a ser renderizada (em tiles).
				    height - Altura da seção de tiles a ser renderizada (em tiles).
				 */
				this.map.render(playerTileOffsetX
					, playerTileOffsetY
					, tilePositionX
					, tilePositionY
					, this.viewportWidth + 2
					, this.viewportHeight + 2); /* Considera o viewport 1 tile maior
												por conta da suavização do movimento.*/
				
				//Obtém a posição do jogador.
				playerPositionX = playerPositionX * TILE_SIZE;
				playerPositionY = (playerPositionY - GamePlayer.PLAYER_HEIGHT + 1) * TILE_SIZE + 2;
				
				//Renderiza os inimigos.
				for (Enemy e : this.enemies) {
					Vector2f pos = new Vector2f();
					
					//Calcula a posição do inimigo com base na posição e no offset do jogador.
					pos.x = ((e.location.x - tilePositionX) * TILE_SIZE) + playerTileOffsetX;
					pos.y = (((e.location.y - tilePositionY) - Enemy.ENEMY_HEIGHT + 1) * TILE_SIZE) + playerTileOffsetY + 2;
					
					//Verifica se o personagem colidiu com o inimigo.
					if (playerPositionX + ((GamePlayer.PLAYER_WIDTH * TILE_SIZE) / 2) >= pos.x &&
						playerPositionX + ((GamePlayer.PLAYER_WIDTH * TILE_SIZE) / 2) <= pos.x + Enemy.ENEMY_WIDTH * TILE_SIZE &&
						playerPositionY >= pos.y - Enemy.ENEMY_HEIGHT * TILE_SIZE &&
						playerPositionY <= pos.y) {
						this.player.isDead = true;
						break;
					}
					
					e.render(gc, g, pos);
				}
				
				//Renderiza o jogador.
				this.player.render(gc, g, new Vector2f(playerPositionX, playerPositionY));
				
				//HUD
				String timeLimitText = String.valueOf(this.timeLimit) + "s";
				int x = Loader.SCREEN_WIDTH - this.fontTimeLimit.getWidth(timeLimitText) - 20;
				this.fontTimeLimit.drawString(x, 10, timeLimitText); 			//Limite de tempo.
				this.fontTimeLimit.drawString(20, 10, String.valueOf(LIFES));	//Vidas.
				
				//Modo de debug.
				if (this.isDebug) this.renderDebug(playerPositionX, playerPositionY);
			break;
			case GoToNextLevel:
				AssetsUtil.drawStringCenter(this.fontInfoBig, "Complete", 220);
			break;
		}	
	}
	
	public void renderGameOver(GameContainer gc, Graphics g) throws SlickException {
		AssetsUtil.drawStringCenter(this.fontInfoBig, "Game Over", 220);
	}
	
	/**
	 * Renderiza elementos de debug.
	 * @param playerPositionX Posição do jogador no eixo X.
	 * @param playerPositionY Posição do jogador no eixo Y.
	 */
	public void renderDebug(float playerPositionX, float playerPositionY) {
		this.fontDebug.drawString(10, 10, "Debug mode");
		
		this.fontDebug.drawString(10, 30, String.format("Collision Y: (%d, %d)"
				, (int)this.player.location.x
				, (int)this.player.location.y + 1));
		
		this.fontDebug.drawString(playerPositionX
				, playerPositionY
				, "x: " + String.valueOf(this.player.location.x));

		this.fontDebug.drawString(playerPositionX
				, playerPositionY + 15
				, "y: " + String.valueOf(this.player.location.y));
	}
	
	//MÉTODOS DE APOIO================================================================
}