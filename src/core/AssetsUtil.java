package core;

import java.awt.Color;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Image;
import org.newdawn.slick.PackedSpriteSheet;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;
import org.newdawn.slick.font.effects.OutlineEffect;

import scenes.Loader;

/**
 * Procedimentos de apoio na manipulação de assets.
 */
public final class AssetsUtil {
	
    /**
     * Realiza carregamento de sprites de um pack.
     * @param name	Nome-base das imagens no sprite a serem carregadas.
     * @param pack	Pacote de sprites. 
     * @param flip	Indica se se deve inverter o sprite horizontalmente.
     * @return Array contendo as imagens carregadas.
     */
    public static Image[] loadSprites(String name, PackedSpriteSheet pack) {
    	Image[] img = new Image[20];
    	for (int i = 1; i <= 20; i++) 
			img[i - 1] = pack.getSprite(name + "_" + String.valueOf(i));      
    	
    	return img;
    }

	/**
	 * Realiza carregamento de uma animação a partir das informações do pacote de sprites.
	 * @param path		Caminho do arquivo de definação do pacote de sprites.			
	 * @param itensName	Nome dos itens a serem carregados.
	 * @param duration	Duração dos quadros da animação.
	 * @return Objeto representando a animação solicitada.
	 * @throws SlickException
	 */
    public static Animation loadAnimation(String path, String itensName, int duration) throws SlickException {
    	int[] dur = { duration }; 
    	return loadAnimation(path, itensName, dur);
    }
	
	/**
	 * Realiza carregamento de uma animação a partir das informações do pacote de sprites.
	 * @param path		Caminho do arquivo de definação do pacote de sprites.			
	 * @param itensName	Nome dos itens a serem carregados.
	 * @param duration	Duração para cada quadro da animação.
	 * @return Objeto representando a animação solicitada.
	 * @throws SlickException
	 */
	public static Animation loadAnimation(String path, String itensName, int[] duration) throws SlickException {
    	Animation anim;
    	PackedSpriteSheet pack = new PackedSpriteSheet(path, Image.FILTER_NEAREST);
    	
    	if (duration.length == 1) {
    		anim = new Animation(AssetsUtil.loadSprites(itensName, pack), duration[0]);
    	} else {
        	anim = new Animation(AssetsUtil.loadSprites(itensName, pack), duration);
    	}
    	
    	return anim;
    }
    
    /**
	 * Carrega uma fonte TrueType.
	 * A fonte deverá estar opbrigatoriamente na pasta assets/fonts.
	 * @param fontName		Nome da fonte a ser carregada.
	 * @param size			Tamanho da fonte.
	 * @param bold			Indica se deve ser renderizada como negrito.
	 * @param italic		Indica se deve ser renderizada como itálico.
	 * @param fontColor		Cor da fonte.
	 * @param outlineColor	Cor da borda da fonte.
	 * @throws SlickException
	 */
	public static UnicodeFont loadFont(String fontName, int size, boolean bold, boolean italic, Color fontColor) throws SlickException {
		return loadFont(fontName, size, bold, italic, fontColor, null, 0);
	}
    
	/**
	 * Carrega uma fonte TrueType com borda.
	 * A fonte deverá estar opbrigatoriamente na pasta assets/fonts.
	 * @param fontName		Nome da fonte a ser carregada.
	 * @param size			Tamanho da fonte.
	 * @param bold			Indica se deve ser renderizada como negrito.
	 * @param italic		Indica se deve ser renderizada como itálico.
	 * @param fontColor		Cor da fonte.
	 * @param outlineColor	Cor da borda da fonte.
	 * @param outlineWidth	Tamanho da borda da fonte.
	 * @return Objeto UnicodeFont representando a fonte.
	 * @throws SlickException
	 */
	@SuppressWarnings("unchecked")
	public static UnicodeFont loadFont(String fontName, int size, boolean bold, boolean italic, Color fontColor, Color outlineColor, int outlineWidth) throws SlickException {
		UnicodeFont f = new UnicodeFont("assets/fonts/" + fontName, size, bold, italic);
		f.getEffects().add(new ColorEffect(fontColor));
		if (outlineColor != null) f.getEffects().add(new OutlineEffect(outlineWidth, outlineColor));
		
	    f.addAsciiGlyphs();
	    f.loadGlyphs(); 
	    
	    return f;
	}
	
	/**
	 * Escreve uma string centralizada.
	 * @param font	Fonte a ser utilizada para escrita.
	 * @param s		Texto a ser escrito.
	 * @param y		Posição no eixo Y a ser utilizada para escrita.
	 */
	public static void drawStringCenter(UnicodeFont font, String s, int y) {
		int size = font.getWidth(s);
		font.drawString((Loader.SCREEN_WIDTH - size) / 2, y, s);
	}
}
