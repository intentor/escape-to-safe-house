package scenes;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;

import core.BaseGameScene;
import core.events.SceneEndedEvent;
import core.listeners.SceneEventsListener;

/**
 * Carregador do jogo.
 */
public class Loader extends BasicGame implements SceneEventsListener {

	/**
	 * Altura da tela de jogo.
	 */
	public final static int SCREEN_WIDTH = 800;
	
	/**
	 * Altura da tela de jogo.
	 */
	public final static int SCREEN_HEIGHT = 600;
	
	/**
	 * M�xima taxa de quadros por segundo.
	 */
	public final static int MAX_FRAME_RATE = 30;
		
	/**
	 * Nome dos n�veis do jogo.
	 */
	public final static String[] NAME_LEVELS = { 
										"Prologue"
										, "Escape to City Hall"
										, "Escape to Safe House" };
	
	/**
	 * Caminho padr�o para acesso aos arquivos dos n�veis do jogo.
	 */
	public final static String LEVEL_BASE_PATH = "assets/scenes/game/level";
	
	/**
	 * N�mero de vidas do jogador.
	 */
	public final static int LIFES_NUMBER = 3;
	
	/**
	 * Container da aplica��o.
	 */
	public static AppGameContainer App;
	
	/**
	 * Container do jogo.
	 */
	public static GameContainer Container;
	
	/**
	 * Cena atual em exibi��o.
	 */
	private BaseGameScene currentScene;
	
	/**
	 * Indica qual o n�vel atual.
	 */
	private int currentLevel;
	
	/**
	 * Construtor.
	 */
	public Loader() {
		super("Escape to Safe House");
	}

	/**
	 * Carrega uma cena de jogo.
	 * @param scene Cena a ser carregada.
	 */
	private void loadScene(BaseGameScene scene) {
		this.currentScene = scene;
		this.currentScene.addListener(this);
		try {
			this.currentScene.init(Loader.Container);
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void init(GameContainer gc) throws SlickException {
		Loader.App = (AppGameContainer) gc;
		Loader.App.setIcon("assets/sprites/icon-mini.png");
		Loader.Container = gc;
		this.loadScene(new MenuScene());
	}

	@Override
	public void update(GameContainer gc, int delta) throws SlickException {
		//Verifica se � para exibir FULL SCREEN.
		if (gc.getInput().isKeyPressed(Input.KEY_F3)) {
			gc.setFullscreen(!gc.isFullscreen());
		}
		
		this.currentScene.update(gc, delta);
	}

	@Override
	public void render(GameContainer gc, Graphics delta) throws SlickException {
		this.currentScene.render(gc, delta);
	}

	@Override
	public void sceneEndedReceived(SceneEndedEvent e) {
		try {
			this.currentScene.dispose();
		} catch (SlickException e1) {
			e1.printStackTrace();
		}
		this.currentScene = null;

		String scene = e.getSceneName();
		if (scene.equals("scenes.MenuScene")) {
			//Carrega o n�vel 1.
			this.currentLevel = 1;
			this.loadScene(new GameScene(LEVEL_BASE_PATH + "1", this.currentLevel));
		} else if (scene.equals("scenes.GameScene")) {
			/* Ao t�rmino de uma cena de jogo, verifica se se deve carregar
			 * um novo n�vel ou voltar ao menu.
			 * Ser� retornando ao menu tamb�m se ocorrer Game Over no jogo
			 * (quando o par�metro 1 enviado pelo evento for true).
			 * Um novo n�vel somente ser� carregado se o n�vel atual + 1
			 * n�o for maior que a quantidade de n�veis estipulada no jogo. */
			if (!(Boolean)e.getParams()[1] || ++this.currentLevel > NAME_LEVELS.length) {
				this.currentLevel = 0;
				this.loadScene(new MenuScene());
			} else {
				this.loadScene(new GameScene(LEVEL_BASE_PATH + String.valueOf(this.currentLevel), this.currentLevel));
			}
		}
	}
	
	public static void main(String[] args) throws SlickException {
		AppGameContainer app = new AppGameContainer(new Loader());
		app.setIcon("assets/sprites/icon-large.png");
		app.setDisplayMode(SCREEN_WIDTH, SCREEN_HEIGHT, false);
		app.setAlwaysRender(true);
		app.setVSync(false);
		app.setSmoothDeltas(false);
		app.setShowFPS(false);
		app.setTargetFrameRate(MAX_FRAME_RATE);
		app.setFullscreen(false);
		app.start();
	}
}
