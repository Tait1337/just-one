package de.clique.westwood.justone.model;

import de.clique.westwood.justone.event.GameEvent;
import de.clique.westwood.justone.event.GameEventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Boardgame with {@link Player}s and {@link CardDeck}.
 */
public class Game {

    private static final Logger LOGGER = LoggerFactory.getLogger(Game.class);

    private static final int MIN_PLAYER_COUNT = 3;
    private static final int MAX_PLAYER_COUNT = 7;

    private final String gameId;
    private GameState state;
    private boolean roundWon;
    private int points;
    private final List<Player> players;
    private int playerNoOnTurn;
    private CardDeck deck;
    private Card activeCard;
    private int chosenWordNo;
    private Map<Player, String> hints;
    private Map<Player, Boolean> hintAcceptedOrRejected;
    private String guess;

    /**
     * Constructor
     * @param gameId the game id
     */
    public Game(String gameId) {
        LOGGER.info("Game " + gameId + " created");
        this.gameId = gameId;
        this.state = GameState.WAITING;
        this.roundWon = false;
        this.points = 0;
        this.players = new CopyOnWriteArrayList<>();
        this.playerNoOnTurn = -1;
        this.deck = null;
        this.activeCard = null;
        this.chosenWordNo = -1;
        this.hints = new HashMap<>();
        this.hintAcceptedOrRejected = new HashMap<>();
        this.guess = null;
    }

    /**
     * Join the game
     * @param player player that want to join
     */
    public void join(Player player) {
        boolean playerAlreadyJoined = players.stream().anyMatch(existingPlayer -> existingPlayer.getName().equals(player.getName()));
        if (playerAlreadyJoined) {
            throw new IllegalArgumentException("Player with this name already joined");
        }
        players.add(player);
        sendGameEvent(new GameEvent(GameEventType.PLAYER_CHANGED));
    }

    /**
     * Leaf the game
     * @param player that want to leaf
     */
    public void leave(Player player) {
        if (players.remove(player)){
            sendGameEvent(new GameEvent(GameEventType.PLAYER_CHANGED));
        }
    }

    /**
     * Get all player on the game
     * @return all player
     */
    public List<Player> getPlayers() {
        return players;
    }

    /**
     * Start the game
     */
    public void start() {
        if (!state.equals(GameState.WAITING)) {
            throw new IllegalStateException("Game is already started");
        }
        if (players.size() < MIN_PLAYER_COUNT) {
            throw new IllegalStateException("Game requires at least " + MIN_PLAYER_COUNT + " Players");
        }
        if (players.size() > MAX_PLAYER_COUNT) {
            throw new IllegalStateException("Game allows a maximum of " + MAX_PLAYER_COUNT + " Players");
        }

        // create card deck
        deck = new CardDeck();

        // choose random starter
        playerNoOnTurn = (int) Math.round(Math.random() * (this.players.size() - 1));

        // take first card
        takeACard();

        state = GameState.STARTED;
        sendGameEvent(new GameEvent(GameEventType.GAME_STARTED));
    }

    /**
     * Start next round of the game
     */
    public void nextRound() {
        // reset state
        roundWon = false;
        chosenWordNo = -1;
        hints = new HashMap<>();
        hintAcceptedOrRejected = new HashMap<>();
        guess = null;

        // select next player
        if (playerNoOnTurn == (players.size() - 1)){
            playerNoOnTurn = 0;
        }else{
            playerNoOnTurn = playerNoOnTurn + 1;
        }

        // take next card
        takeACard();

        // check if end of game reached
        if (activeCard != null) {
            sendGameEvent(new GameEvent(GameEventType.NEXT_ROUND_STARTED));
        } else {
            state = GameState.ENDED;
            sendGameEvent(new GameEvent(GameEventType.GAME_ENDED));
        }
    }

    /**
     * Get the player who is on turn
     * @return the player on turn
     */
    public int getPlayerNoOnTurn() {
        return playerNoOnTurn;
    }

    /**
     * Get the card deck
     * @return the deck
     */
    public CardDeck getCardDeck() {
        return deck;
    }

    /**
     * Get the game points
     * @return the points
     */
    public int getPoints() {
        return points;
    }

    /**
     * Get all hints
     * @return all provided hints
     */
    public Map<Player, String> getHints() {
        return hints;
    }

    /**
     * Get the guess
     * @return the player guess
     */
    public String getGuess() {
        return guess;
    }

    /**
     * Get the active card
     * @return the card
     */
    public Card getActiveCard() {
        return activeCard;
    }

    /**
     * Get the result of the round
     * @return true when won, false otherwise
     */
    public boolean isRoundWon() {
        return roundWon;
    }

    /**
     * Get the chosen word number
     * @return the chosen word number (1-5)
     */
    public int getChosenWordNo() {
        return chosenWordNo;
    }

    /**
     * Get a list of flags keyed by player; the flag indicates if the hint was accepted (true) or rejected (false)
     * @return the list of player with flags
     */
    public Map<Player, Boolean> getHintAcceptedOrRejected() {
        return hintAcceptedOrRejected;
    }

    /**
     * Set the chosen word number
     * @param no the chosen word number (1-5)
     */
    public void setChosenWordNo(int no) {
        if (no < 1 || no > 5) {
            throw new IllegalArgumentException("Word number " + no + " does not exists");
        }
        chosenWordNo = no;
        sendGameEvent(new GameEvent(GameEventType.ACTION_WORD_CHOSEN));
    }

    /**
     * Set the guess
     * @param word the guess
     */
    public void setGuess(String word) {
        if (word == null || word.isEmpty()) {
            throw new IllegalArgumentException(word + " is no valid guess");
        }
        guess = word;
        roundEnd();
        sendGameEvent(new GameEvent(GameEventType.ACTION_GUESS_PROVIDED));
    }

    /**
     * Set a hint
     * @param player the player who provided the hint
     * @param hint the hint
     */
    public void setHint(Player player, String hint) {
        if (player == null){
            throw new IllegalArgumentException("null is no valid player");
        }
        if (hint == null || hint.isEmpty()) {
            throw new IllegalArgumentException(hint + " is no valid hint");
        }
        hints.put(player, hint);
        sendGameEvent(new GameEvent(GameEventType.ACTION_HINT_PROVIDED));
        if (hints.size() == players.size() - 1) {
            sendGameEvent(new GameEvent(GameEventType.ACTION_ALL_HINTS_PROVIDED));
        }
    }

    /**
     * Set the flag that indicates if the hint was accepted or rejected
     * @param player the player who accepted or rejected the hint
     * @param accepted the flag that indicates if accepted (true), otherwise false
     */
    public void setHintAcceptedOrRejected(Player player, boolean accepted){
        if (player == null){
            throw new IllegalArgumentException("null is no valid player");
        }
        hintAcceptedOrRejected.put(player, accepted);
        sendGameEvent(new GameEvent(GameEventType.ACTION_HINT_ACCEPTED_OR_DECLINED));
        if (hintAcceptedOrRejected.size() == players.size() - 1){
            sendGameEvent(new GameEvent(GameEventType.ACTION_ALL_HINTS_EVALUATED));
        }
    }

    private void roundEnd(){
        // verify guess
        String correctWord = activeCard.getWord(chosenWordNo);
        if (correctWord.equalsIgnoreCase(guess)){
            roundWon = true;
            points = points + 1;
        }
    }

    private void takeACard() {
        activeCard = deck.takeACard();
    }

    private void sendGameEvent(GameEvent gameEvent) {
        if (gameEvent == null){
            throw new IllegalArgumentException("null is no valid game event");
        }
        players.forEach(player -> player.notifyAboutGameEvent(gameEvent));
    }

}
