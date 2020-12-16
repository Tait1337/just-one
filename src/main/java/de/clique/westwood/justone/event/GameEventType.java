package de.clique.westwood.justone.event;

/**
 * Types of {@link GameEvent}s.
 */
public enum GameEventType {

    // on waiting room
    PLAYER_CHANGED, // -> Player joined or left the game
    GAME_STARTED, // -> Game was started (cards mixed and random player chosen, first card chosen)

    // on game room
    ACTION_WORD_CHOSEN, // -> disable selection for player on turn, highlight chosen word to all players, enable hint input for all player that are not on turn
    ACTION_HINT_PROVIDED, // -> disable hint input for the player that provided the input
    ACTION_ALL_HINTS_PROVIDED, // -> show hints to all player that are not on turn
    ACTION_HINT_ACCEPTED_OR_DECLINED, // -> hide declined hint
    ACTION_ALL_HINTS_EVALUATED, // -> show non declined hints to player on turn
    ACTION_GUESS_PROVIDED, // ->  disable guess input for player on turn, show guess to all players, verify guess, update points, check for game end
    NEXT_ROUND_STARTED, // -> choose next player, take a card, reset UI (clear and disable all inputs, show card options to all player, hide text and activate selection for player on turn)
    GAME_ENDED // -> show points and redirect to start page

}
