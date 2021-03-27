package de.clique.westwood.justone.service;

import com.vaadin.flow.spring.annotation.VaadinSessionScope;
import de.clique.westwood.justone.model.Player;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * {@link Player} Session.
 */
@Component
@VaadinSessionScope
public class SessionStorageService implements Serializable {

    private String gameId;
    private Player player;

    /**
     * Get the player that is related to the session
     * @return the player
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Set the player that is related to the session
     * @param player the player
     */
    public void setPlayer(Player player) {
        this.player = player;
    }

    /**
     * Get the game id
     * @return the game id
     */
    public String getGameId() {
        return gameId;
    }

    /**
     * Set the game id
     * @param gameId the game id
     */
    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

}
