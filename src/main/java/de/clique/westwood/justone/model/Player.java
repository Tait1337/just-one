package de.clique.westwood.justone.model;

import de.clique.westwood.justone.event.GameEvent;
import de.clique.westwood.justone.event.GameEventListener;
import de.clique.westwood.justone.event.GameEventType;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * A person that participate on a {@link Game}.
 */
public class Player {

    private final String name;
    private final Map<GameEventType, List<GameEventListener>> listeners;

    /**
     * Constructor
     * @param name the player name
     */
    public Player(String name) {
        this.name = name;
        this.listeners = new ConcurrentHashMap<>();
    }

    /**
     * Get the player name
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Subscribe to a {@link GameEventType}
     * @param gameEventType the type to listen for
     * @param gameEventListener the {@link GameEventListener} that want to be notified
     */
    public void addGameEventListener(GameEventType gameEventType, GameEventListener gameEventListener) {
        listeners.putIfAbsent(gameEventType, new CopyOnWriteArrayList<>());
        listeners.get(gameEventType).add(gameEventListener);
    }

    /**
     * Unsubscribe a {@link GameEventListener}
     * @param gameEventType the type where the listener was registered on
     * @param gameEventListener the {@link GameEventListener} that should not be notified anymore
     */
    public void removeGameEventListener(GameEventType gameEventType, GameEventListener gameEventListener) {
        if (listeners.containsKey(gameEventType)) {
            listeners.get(gameEventType).remove(gameEventListener);
            if (listeners.get(gameEventType).isEmpty()) {
                listeners.remove(gameEventType);
            }
        }
    }

    /**
     * Send a game event to all subscribed {@link GameEventListener}
     * @param gameEvent the game event to send
     */
    public void notifyAboutGameEvent(GameEvent gameEvent) {
        List<GameEventListener> listenersWithInterest = listeners.get(gameEvent.getType());
        if (listenersWithInterest != null) {
            for (GameEventListener gameEventListener : listenersWithInterest) {
                gameEventListener.onGameEvent(gameEvent);
            }
        }
    }

}
