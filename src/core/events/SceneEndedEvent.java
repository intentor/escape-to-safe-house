package core.events;

import java.util.EventObject;

/**
 * Evento de término de cena.
 */
@SuppressWarnings("serial")
public class SceneEndedEvent extends EventObject {
	private String sceneName;
	private Object[] params;
	
	public SceneEndedEvent(Object source) {
        this(source, null);
    }
	
	public SceneEndedEvent(Object source, Object[] params) {
        super(source);
        
        this.sceneName = source.getClass().getName();
        this.params = params;
    }
	
	/**
	 * Obtém o nome da cena sendo encerrada.
	 */
	public String getSceneName() {
		return this.sceneName;
	}
	
	/**
	 * Obtém os parâmetros do término de cena.
	 */
	public Object[] getParams() {
		return this.params;
	}
}