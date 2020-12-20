package de.clique.westwood.justone.service;

import de.clique.westwood.justone.model.*;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service to handle {@link Game} logic.
 */
@Service
public class GameService {

    private final Map<String, Game> games;

    /**
     * Constructor
     */
    public GameService() {
        games = new HashMap<>();
    }

    /**
     * Create a new or join a existing game
     * @param gameId the game id
     * @param playerName the player name
     * @param sessionStorageService the session
     */
    public void createOrJoinGame(String gameId, String playerName, SessionStorageService sessionStorageService) {
        sessionStorageService.setGameId(gameId);
        sessionStorageService.setPlayer(new Player(playerName));
        if (!games.containsKey(gameId)) {
            games.put(gameId, new Game(gameId));
        }
        games.get(gameId).join(sessionStorageService.getPlayer());
    }

    /**
     * Leave a game
     * @param sessionStorageService the session
     */
    public void leave(SessionStorageService sessionStorageService) {
        String gameId = getGameId(sessionStorageService);
        String playername = sessionStorageService.getPlayer().getName();
        games.get(gameId).getPlayers().stream()
                .filter(player -> player.getName().equals(playername))
                .findAny()
                .ifPresent(player -> games.get(gameId).leave(player));
        if (games.get(gameId).getPlayers().isEmpty()){
            games.remove(gameId);
        }
    }

    /**
     * Get all player of the joined game
     * @param sessionStorageService the session
     * @return the list of player
     */
    public List<Player> getPlayersForGame(SessionStorageService sessionStorageService) {
        String gameId = getGameId(sessionStorageService);
        return games.get(gameId).getPlayers();
    }

    /**
     * Start the game
     * @param sessionStorageService the session
     */
    public void startGame(SessionStorageService sessionStorageService) {
        String gameId = getGameId(sessionStorageService);
        games.get(gameId).start();
    }

    /**
     * Get the player who is on turn
     * @param sessionStorageService the session
     * @return the player
     */
    public Player getPlayerOnTurn(SessionStorageService sessionStorageService){
        String gameId = getGameId(sessionStorageService);
        return games.get(gameId).getPlayers().get(games.get(gameId).getPlayerNoOnTurn());
    }

    /**
     * Get the number of unused cards in deck
     * @param sessionStorageService the session
     * @return the number of unused cards
     */
    public int getNumberOfUnusedCardsInDeck(SessionStorageService sessionStorageService){
        String gameId = getGameId(sessionStorageService);
        return games.get(gameId).getCardDeck().getNumberOfUnusedCardsInDeck();
    }

    /**
     * Get the game points
     * @param sessionStorageService the session
     * @return the points
     */
    public int getPoints(SessionStorageService sessionStorageService) {
        String gameId = getGameId(sessionStorageService);
        return games.get(gameId).getPoints();
    }

    /**
     * Get the active card
     * @param sessionStorageService the session
     * @return the card
     */
    public Card getActiveCard(SessionStorageService sessionStorageService){
        String gameId = getGameId(sessionStorageService);
        return games.get(gameId).getActiveCard();
    }

    /**
     * Set the chosen word number
     * @param sessionStorageService the session
     * @param wordNoOnCard the chosen word number (1-5)
     */
    public void chooseWord(SessionStorageService sessionStorageService, int wordNoOnCard){
        String gameId = getGameId(sessionStorageService);
        games.get(gameId).setChosenWordNo(wordNoOnCard);
    }

    /**
     * Get the chosen word number
     * @param sessionStorageService the session
     * @return the chosen word number (1-5)
     */
    public int getChosenWordNo(SessionStorageService sessionStorageService){
        String gameId = getGameId(sessionStorageService);
        return games.get(gameId).getChosenWordNo();
    }

    /**
     * Set a hint
     * @param sessionStorageService the session
     * @param hint the hint
     */
    public void provideHint(SessionStorageService sessionStorageService, String hint){
        String gameId = getGameId(sessionStorageService);
        games.get(gameId).setHint(sessionStorageService.getPlayer(), hint);
    }

    /**
     * Set the guess
     * @param sessionStorageService the session
     * @param word the guess
     */
    public void provideGuess(SessionStorageService sessionStorageService, String word){
        String gameId = getGameId(sessionStorageService);
        games.get(gameId).setGuess(word);
    }

    /**
     * Get the guess
     * @param sessionStorageService the session
     * @return the player guess
     */
    public String getGuess(SessionStorageService sessionStorageService){
        String gameId = getGameId(sessionStorageService);
        return games.get(gameId).getGuess();
    }

    /**
     * Get the hint of a player
     * @param sessionStorageService the session
     * @param player the player to search for
     * @return the hint of a player
     */
    public String getHint(SessionStorageService sessionStorageService, Player player) {
        String gameId = getGameId(sessionStorageService);
        return games.get(gameId).getHints().get(player);
    }

    /**
     * Set the flag that indicates if the hint was accepted or rejected
     * @param sessionStorageService the session
     * @param accepted the flag that indicates if accepted (true), otherwise false
     */
    public void provideHintAcceptedOrRejected(SessionStorageService sessionStorageService, Boolean accepted) {
        String gameId = getGameId(sessionStorageService);
        games.get(gameId).setHintAcceptedOrRejected(sessionStorageService.getPlayer(), accepted);
    }

    /**
     * Get a list of flags keyed by player; the flag indicates if the hint was accepted (true) or rejected (false)
     * @param sessionStorageService the session
     * @return the list of player with flags
     */
    public Map<Player, Boolean> getProvidedHintAcceptedOrRejected(SessionStorageService sessionStorageService) {
        String gameId = getGameId(sessionStorageService);
        return games.get(gameId).getHintAcceptedOrRejected();
    }

    /**
     * Get the result of the round
     * @param sessionStorageService the session
     * @return true when won, false otherwise
     */
    public boolean isRoundWon(SessionStorageService sessionStorageService) {
        String gameId = getGameId(sessionStorageService);
        return games.get(gameId).isRoundWon();
    }

    /**
     * Start next round of the game
     * @param sessionStorageService the session
     */
    public void startNextRound(SessionStorageService sessionStorageService) {
        String gameId = getGameId(sessionStorageService);
        games.get(gameId).nextRound();
    }

    private String getGameId(SessionStorageService sessionStorageService) {
        String gameId = sessionStorageService.getGameId();
        if (!games.containsKey(gameId)) {
            throw new IllegalArgumentException("Game with ID " + gameId + " not found");
        }
        return gameId;
    }

}
