package de.clique.westwood.justone.event;

import de.clique.westwood.justone.model.Game;

/**
 * Event of the {@link Game}.
 */
public class GameEvent {

    private final GameEventType type;

    /**
     * Constructor
     * @param type the {@link GameEventType}
     */
    public GameEvent(GameEventType type) {
        this.type = type;
    }

    /**
     * Get the {@link GameEventType} of the Event
     *
     * @return the type
     */
    public GameEventType getType() {
        return type;
    }

}

