package core.listeners;

import core.events.SceneEndedEvent;

/**
 * Listener de eventos de cenas.
 */
public interface SceneEventsListener {
	/**
	 * Listener de evento de término de cena.
	 * @param e Dados do evento ocorrido.
	 */
	void sceneEndedReceived(SceneEndedEvent e);
}