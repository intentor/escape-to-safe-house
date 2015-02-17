package scenes;

import java.awt.Color;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.gui.AbstractComponent;
import org.newdawn.slick.gui.ComponentListener;
import org.newdawn.slick.gui.MouseOverArea;

import core.AssetsUtil;
import core.BaseGameScene;

/**
 * Cena do menu do jogo.
 */
public class MenuScene extends BaseGameScene implements ComponentListener {
	
	/**
	 * Modo da cena.
	 */
	private enum SceneMode {
		  Loading
		, MainMenu
		, Credits
	}
	
	/**
	 * Quantidade de itens a serem carregados em tela.
	 */
	private static final int TOTAL_LOADING_RESOURCES = 6;	
	
	/**
	 * Modo atual da tela.
	 */
	private SceneMode currentMode = SceneMode.Loading;
	
	/**
	 * Índice de carregamento de assets.
	 */
	private int loadingIndex = 0;
	
	/**
	 * Quantidade frames já executados.
	 */
	private int frameCount = 0;

	/**
	 * Logo do Astirina.
	 */
	private Image logoAstirina;
	
	/**
	 * Logo do Slick.
	 */
	private Image logoSlick;
	
	/**
	 * Imagem dos créditos.
	 */
	private Image credits;
	
	/**
	 * Música de fundo da cena.
	 */
	private Music musBackground;
	
	/**
	 * Fonte do texto de loading.
	 */
	private UnicodeFont fontLoading; 
	
	/**
	 * Fonte do logo do jogo.
	 */
	private UnicodeFont fontLogoGame;
	
	/**
	 * Áreas de mouse do menu.
	 */
	private MouseOverArea[] areasMenu;
	
	
	public MenuScene() {
		super();
	}
	
	@Override
	public void init(GameContainer gc) throws SlickException {
		Loader.Container.setMouseCursor("assets/sprites/cursor-empty.png", 0, 0);
		this.fontLoading = AssetsUtil.loadFont("MotorwerkOblique.ttf", 25, true, false, Color.white);
	}
	
	@Override
	public void dispose() throws SlickException {
		this.logoSlick.destroy();
		this.logoAstirina.destroy();
		this.credits.destroy();
		this.fontLogoGame.destroy();
		this.areasMenu = null;
		this.musBackground.stop();
		this.musBackground = null;
	}

	@Override
	public void update(GameContainer gc, int delta) throws SlickException {
		switch (this.currentMode) {
			case Loading:
				this.updateLoading(gc, delta);
			break;
			case MainMenu:
				this.updateMainMenu(gc, delta);
			break;
			case Credits:
				this.updateCredits(gc, delta);
			break;
		}
	}

	@Override
	public void render(GameContainer gc, Graphics g) throws SlickException {
		switch (this.currentMode) {
			case Loading:
				this.renderLoading(gc, g);
			break;
			case MainMenu:
				this.renderMainMenu(gc, g);
			break;
			case Credits:
				this.renderCredits(gc, g);
			break;
		}		
	}
	
	//MÉTODOS DE UPDATE================================================================
	
	public void updateLoading(GameContainer gc, int delta) throws SlickException {
		switch(++this.loadingIndex) {
			case 1:
				this.logoSlick = new Image("assets/scenes/menu/slick_logo.png");
			break;
			case 2:
				this.logoAstirina = new Image("assets/scenes/menu/producer_logo.jpg");
			break;
			case 3:
				this.credits = new Image("assets/scenes/menu/credits.jpg");
			break;
			case 4:
				this.fontLogoGame = AssetsUtil.loadFont("MotorwerkOblique.ttf", 120, true, false, Color.yellow, Color.gray, 3);
			break;
			case 5:
				//Cria as áreas do menu.
				this.areasMenu = new MouseOverArea[3];
				this.areasMenu[0] = new MouseOverArea(gc, new Image("assets/scenes/menu/menu_play.png"), 300, 300, 200, 50, this);
				this.areasMenu[1] = new MouseOverArea(gc, new Image("assets/scenes/menu/menu_credits.png"), 300, 350, 200, 50, this);
				this.areasMenu[2] = new MouseOverArea(gc, new Image("assets/scenes/menu/menu_exit.png"), 300, 400, 200, 50, this);
				for (MouseOverArea area : this.areasMenu) {
					area.setNormalColor(new org.newdawn.slick.Color(1,1,1,0.8f));
					area.setMouseOverColor(new org.newdawn.slick.Color(1,1,1,0.9f));
				}
			break;
			case 6:
				this.musBackground = new Music("assets/scenes/menu/menu.ogg");
			break;
			default:
				this.fontLoading.destroy();
				this.musBackground.loop();
				this.currentMode = SceneMode.MainMenu;
			break;
		}
	}
	
	public void updateMainMenu(GameContainer gc, int delta) throws SlickException {
		if (this.frameCount++ == 210) { 
			Loader.Container.setMouseCursor("assets/sprites/cursor.png", 0, 0);
		} else this.frameCount++;
	}
	
	public void updateCredits(GameContainer gc, int delta) throws SlickException {
		if (gc.getInput().isMousePressed(Input.MOUSE_LEFT_BUTTON)) this.currentMode = SceneMode.MainMenu;
	}

	//MÉTODOS DE RENDERIZAÇÃO=========================================================

	public void renderLoading(GameContainer gc, Graphics g) throws SlickException {
		String textLoading = "Loading...";
	    int x = 720 - this.fontLoading.getWidth(textLoading);
	    this.fontLoading.drawString(x, 550, textLoading);
	    
	    int percentage = (int) ((this.loadingIndex / (float)TOTAL_LOADING_RESOURCES) * 100);
	    this.fontLoading.drawString(730, 550, String.valueOf(percentage) + "%");
	}
	
	public void renderMainMenu(GameContainer gc, Graphics g) throws SlickException {
		if (this.frameCount > 210) {
			this.fontLogoGame.drawString(10, 10, "Escape to");
			this.fontLogoGame.drawString(100, 100, "Safe House");
			for (MouseOverArea area : this.areasMenu) area.render(gc, g);
		} else {
			if (this.frameCount > 10 && this.frameCount < 100) this.logoAstirina.draw(287, 168);
			if (this.frameCount > 110 && this.frameCount < 200) this.logoSlick.draw(300, 267);
		}
	}
	
	public void renderCredits(GameContainer gc, Graphics g) throws SlickException {
		this.credits.draw(0,0);
	}
	
	//MÉTODOS DE ComponentListener====================================================
	
	public void componentActivated(AbstractComponent source) {		
		switch (this.currentMode) {
			case MainMenu:
				if (source == this.areasMenu[0]) {
					this.fireEndSceneEvent();
				} else if (source == this.areasMenu[1]) {
					this.currentMode = SceneMode.Credits;
				} else if (source == this.areasMenu[2]) {
					System.exit(0);
				}
			break;
		}
		
		Loader.Container.getInput().clearControlPressedRecord();
		Loader.Container.getInput().clearMousePressedRecord();
	}
}

