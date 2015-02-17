package core.listeners;

import core.events.SceneEndedEvent;

/**
 * Listener de eventos de cenas.
 */
public interface SceneEventsListener {
	/**
	 * Listener de evento de t�rmino de cena.
	 * @param e Dados do evento ocorrido.
	 */
	void sceneEndedReceived(SceneEndedEvent e);
}