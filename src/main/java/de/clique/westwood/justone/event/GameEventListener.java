package de.clique.westwood.justone.event;

/**
 * Listener for {@link GameEvent}s.
 */
public interface GameEventListener {

    /**
     * Handle a {@link GameEvent}
     * @param gameEvent the incoming GameEvent
     */
    void onGameEvent(GameEvent gameEvent);

}
