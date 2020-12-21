package de.clique.westwood.justone.view;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.router.Route;
import de.clique.westwood.justone.event.GameEvent;
import de.clique.westwood.justone.event.GameEventListener;
import de.clique.westwood.justone.model.Card;
import de.clique.westwood.justone.model.Game;
import de.clique.westwood.justone.model.Player;
import de.clique.westwood.justone.service.GameService;
import de.clique.westwood.justone.service.SessionStorageService;
import de.clique.westwood.justone.view.component.CardComponent;
import de.clique.westwood.justone.view.component.DeckComponent;
import de.clique.westwood.justone.view.component.PlayerComponent;
import de.clique.westwood.justone.view.layout.AbsoluteLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static de.clique.westwood.justone.event.GameEventType.*;

/**
 * Game room where the {@link Game} is played.
 */
@Push
@Route("gameroom")
public class GameRoomView extends AbsoluteLayout implements GameEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(GameRoomView.class);

    private final SessionStorageService sessionStorageService;
    private final GameService gameService;

    private final H1 titleLbl;
    private final H3 subtitleLbl;
    private final H3 pointsLbl;
    private final Button leaveBtn;
    private final DeckComponent deckCmp;
    private final Map<Player, PlayerComponent> playerCmps;
    private Dialog confirmDialog;

    /**
     * Construct
     * @param sessionStorageService the session
     * @param gameService the game
     */
    public GameRoomView(SessionStorageService sessionStorageService, GameService gameService) {
        this.sessionStorageService = sessionStorageService;
        this.gameService = gameService;

        titleLbl = new H1("Game " + sessionStorageService.getGameId());
        subtitleLbl = new H3("It's " + gameService.getPlayerOnTurn(sessionStorageService).getName() + "'s turn");
        pointsLbl = new H3("Points: " + gameService.getPoints(sessionStorageService));
        leaveBtn = new Button("Leave");
        leaveBtn.addClickListener(this::leaveGame);
        deckCmp = new DeckComponent(sessionStorageService, gameService);
        playerCmps = new HashMap<>();

        Point[] positions = new Point[]{
                new Point(15, 15),
                new Point(35, 5),
                new Point(55, 15),
                new Point(65, 35),
                new Point(52, 55),
                new Point(30, 60),
                new Point(15, 40)
        };
        List<Player> players = this.gameService.getPlayersForGame(this.sessionStorageService);
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            PlayerComponent playerComponent = new PlayerComponent(sessionStorageService, gameService, player);
            layoutAbsolute(playerComponent, positions[i].y + "%", positions[i].x + "%", null, null);
            playerCmps.put(player, playerComponent);
        }

        layoutAbsolute(titleLbl, "0px", "50px", null, null);
        layoutAbsolute(pointsLbl, "100px", "50px", null, null);
        layoutAbsolute(subtitleLbl, "130px", "50px", null, null);
        layoutAbsolute(leaveBtn, "10px", null, "10px", null);
        layoutAbsolute(deckCmp, "30%", "40%", null, null);

        add(titleLbl, pointsLbl, subtitleLbl, leaveBtn, deckCmp);
        add(playerCmps.values().toArray(new PlayerComponent[0]));

        addGameEventListener();
        if (sessionStorageService.getPlayer() != this.gameService.getPlayerOnTurn(this.sessionStorageService)) {
            Notification.show("New round! It's " + gameService.getPlayerOnTurn(sessionStorageService).getName() + "'s turn.");
        } else {
            Notification.show("New round! It's your turn.");
        }
    }

    /**
     * @see GameEventListener
     */
    @Override
    public void onGameEvent(GameEvent gameEvent) {
        LOGGER.info(sessionStorageService.getPlayer().getName() + " received GameEvent: " + gameEvent.getType());
        switch (gameEvent.getType()) {
            case PLAYER_CHANGED: // Player left the game
                getUI().ifPresent(ui -> ui.access(() ->
                        Notification.show("Player has left the game! Game must be canceled!")
                ));
                removeGameEventListener();
                getUI().ifPresent(ui -> ui.access(() ->
                        ui.navigate("")
                ));
                break;
            case ACTION_WORD_CHOSEN: // -> disable selection for player on turn, highlight chosen word to all players, enable hint input for all player that are not on turn
                // mark the selected word for the player on turn
                PlayerComponent playerComponentForPlayerOnTurn = playerCmps.get(gameService.getPlayerOnTurn(sessionStorageService));
                CardComponent cardComponentForPlayerOnTurn = playerComponentForPlayerOnTurn.getCardCmp();
                cardComponentForPlayerOnTurn.markChosenWord(gameService.getChosenWordNo(sessionStorageService));
                // enable hint input for player that is not on turn
                for (Player player : gameService.getPlayersForGame(sessionStorageService)) {
                    boolean playerIsCurrentUser = player.equals(this.sessionStorageService.getPlayer());
                    boolean playerIsOnTurn = player.equals(this.gameService.getPlayerOnTurn(this.sessionStorageService));
                    if (playerIsCurrentUser && !playerIsOnTurn) {
                        PlayerComponent playerCmp = playerCmps.get(player);
                        playerCmp.markShowHintOrGuessInp();
                    }
                }
                break;
            case ACTION_HINT_PROVIDED: // -> disable hint input for the player that provided the input
                // mark the provided hint for the player
                for (Player player : gameService.getPlayersForGame(sessionStorageService)) {
                    String hint = gameService.getHint(sessionStorageService, player);
                    if (hint != null) {
                        PlayerComponent playerCmp = playerCmps.get(player);
                        playerCmp.markGuessOrHintProvided(Card.getCipheredWord());
                    }
                }
                break;
            case ACTION_ALL_HINTS_PROVIDED:  // -> show hints to all player that are not on turn
                // make the hints readable for all player that are not on turn
                if (sessionStorageService.getPlayer() != this.gameService.getPlayerOnTurn(this.sessionStorageService)) {
                    for (Player player : gameService.getPlayersForGame(sessionStorageService)) {
                        String hint = gameService.getHint(sessionStorageService, player);
                        if (hint != null) {
                            PlayerComponent playerCmp = playerCmps.get(player);
                            playerCmp.markGuessOrHintProvided(hint);
                            if (player == sessionStorageService.getPlayer()) {
                                playerCmp.markShowAcceptAndDeclineButtons();
                            }
                        }
                    }
                }
                break;
            case ACTION_HINT_ACCEPTED_OR_DECLINED: // -> hide declined hint
                Map<Player, Boolean> hintDecisions = gameService.getProvidedHintAcceptedOrRejected(sessionStorageService);
                for (Player player : gameService.getPlayersForGame(sessionStorageService)) {
                    Boolean hint = hintDecisions.get(player);
                    if (hint != null) {
                        PlayerComponent playerCmp = playerCmps.get(player);
                        if (Boolean.TRUE.equals(hint)) {
                            playerCmp.markHintAsAccepted();
                        } else {
                            playerCmp.markHintAsDeclined();
                        }
                    }
                }
                break;
            case ACTION_ALL_HINTS_EVALUATED: // -> show non declined hints to player on turn
                if (sessionStorageService.getPlayer() == this.gameService.getPlayerOnTurn(this.sessionStorageService)) {
                    hintDecisions = gameService.getProvidedHintAcceptedOrRejected(sessionStorageService);
                    for (Player player : gameService.getPlayersForGame(sessionStorageService)) {
                        if (player != this.gameService.getPlayerOnTurn(this.sessionStorageService)) {
                            if (Boolean.TRUE.equals(hintDecisions.get(player))) {
                                String hint = gameService.getHint(sessionStorageService, player);
                                PlayerComponent playerCmp = playerCmps.get(player);
                                playerCmp.markGuessOrHintProvided(hint);
                            }
                        }
                    }
                    PlayerComponent playerCmp = playerCmps.get(sessionStorageService.getPlayer());
                    playerCmp.markShowHintOrGuessInp();
                }
                break;
            case ACTION_GUESS_PROVIDED: // ->  disable guess input for player on turn, show guess to all players, verify guess, update points, check for game end
                boolean won = this.gameService.isRoundWon(sessionStorageService);
                String guess = gameService.getGuess(sessionStorageService);
                String correct = gameService.getActiveCard(sessionStorageService).getWord(gameService.getChosenWordNo(sessionStorageService));
                if (sessionStorageService.getPlayer() != this.gameService.getPlayerOnTurn(this.sessionStorageService)) {
                    Player player = gameService.getPlayerOnTurn(sessionStorageService);
                    PlayerComponent playerCmp = playerCmps.get(player);
                    playerCmp.markGuessOrHintProvided(guess);
                    confirmDialog = new Dialog();
                    confirmDialog.setModal(true);
                    confirmDialog.setCloseOnEsc(false);
                    confirmDialog.setCloseOnOutsideClick(false);
                    confirmDialog.setWidth("400px");
                    confirmDialog.setHeight("180px");
                    Paragraph wonText;
                    if (won) {
                        wonText = new Paragraph("Very good, your team won a point!");
                    } else {
                        wonText = new Paragraph("What a pity, your team lost. "+player.getName()+" chose '"+guess+"' but correct was '"+ correct+"'.");
                    }
                    Paragraph confirmText = new Paragraph("Round ended. Next round starts in a few seconds...");
                    AbsoluteLayout dialogLayout = new AbsoluteLayout(wonText, confirmText);
                    dialogLayout.getStyle().set("margin", "10px");
                    confirmDialog.add(dialogLayout);
                    getUI().ifPresent(ui -> ui.access(() ->
                            confirmDialog.open()
                    ));
                } else {
                    confirmDialog = new Dialog();
                    confirmDialog.setModal(true);
                    confirmDialog.setCloseOnEsc(false);
                    confirmDialog.setCloseOnOutsideClick(false);
                    confirmDialog.setWidth("400px");
                    confirmDialog.setHeight("180px");
                    Paragraph wonText;
                    if (won) {
                        wonText = new Paragraph("Very good, your team won a point!");
                    } else {
                        wonText = new Paragraph("What a pity, your team lost. You chose '"+guess+"' but correct was '"+ correct+"'.");
                    }
                    Paragraph confirmText = new Paragraph("Round ended. Click OK to start the next round.");
                    Button confirmButton = new Button("OK", event -> gameService.startNextRound(sessionStorageService));
                    AbsoluteLayout dialogLayout = new AbsoluteLayout(wonText, confirmText, confirmButton);
                    dialogLayout.getStyle().set("margin", "10px");
                    dialogLayout.layoutAbsolute(confirmButton, null, null, "10px", "10px");
                    confirmDialog.add(dialogLayout);
                    getUI().ifPresent(ui -> ui.access(() -> {
                        confirmDialog.open();
                        confirmButton.focus();
                    }));
                }
                break;
            case NEXT_ROUND_STARTED: // -> choose next player, take a card, reset UI (clear and disable all inputs, show card options to all player, hide text and activate selection for player on turn)
                getUI().ifPresent(ui -> ui.access(() -> {
                    confirmDialog.close();
                    if (sessionStorageService.getPlayer() != this.gameService.getPlayerOnTurn(this.sessionStorageService)) {
                        Notification.show("New round! It's " + gameService.getPlayerOnTurn(sessionStorageService).getName() + "'s turn.");
                    } else {
                        Notification.show("New round! It's your turn.");
                    }
                }));
                updatePointsAndPlayerOnTurn();
                markResetUI();
                break;
            case GAME_ENDED: // -> show points and redirect to start page
                getUI().ifPresent(ui -> ui.access(() -> confirmDialog.close()));
                updatePointsAndPlayerOnTurn();
                removeGameEventListener();
                Dialog gameEndConfirmDialog = new Dialog();
                gameEndConfirmDialog.setModal(true);
                gameEndConfirmDialog.setCloseOnEsc(false);
                gameEndConfirmDialog.setCloseOnOutsideClick(false);
                gameEndConfirmDialog.setWidth("400px");
                gameEndConfirmDialog.setHeight("150px");
                Text confirmText = new Text("Game end! Your team earned " + gameService.getPoints(sessionStorageService) + " point(s).");
                Button confirmButton = new Button("OK", event -> getUI().ifPresent(ui -> ui.access(() -> {
                    gameEndConfirmDialog.close();
                    gameService.leave(sessionStorageService);
                    ui.navigate("");
                })));
                AbsoluteLayout dialogLayout = new AbsoluteLayout(confirmText, confirmButton);
                dialogLayout.getStyle().set("margin", "10px");
                dialogLayout.layoutAbsolute(confirmButton, null, null, "10px", "10px");
                gameEndConfirmDialog.add(dialogLayout);
                getUI().ifPresent(ui -> ui.access(() -> {
                    gameEndConfirmDialog.open();
                    confirmButton.focus();
                }));
                break;
            default:
                // do not handle
        }
    }

    /**
     * Update the labels for points and player on turn
     */
    public void updatePointsAndPlayerOnTurn() {
        getUI().ifPresent(ui -> ui.access(() -> {
            subtitleLbl.setText("It's " + gameService.getPlayerOnTurn(sessionStorageService).getName() + "'s turn");
            pointsLbl.setText("Points: " + gameService.getPoints(sessionStorageService));
            deckCmp.updateNumberOfUnusedCards();
        }));
    }

    private void addGameEventListener() {
        sessionStorageService.getPlayer().addGameEventListener(PLAYER_CHANGED, this);
        sessionStorageService.getPlayer().addGameEventListener(NEXT_ROUND_STARTED, this);
        sessionStorageService.getPlayer().addGameEventListener(ACTION_WORD_CHOSEN, this);
        sessionStorageService.getPlayer().addGameEventListener(ACTION_HINT_PROVIDED, this);
        sessionStorageService.getPlayer().addGameEventListener(ACTION_ALL_HINTS_PROVIDED, this);
        sessionStorageService.getPlayer().addGameEventListener(ACTION_HINT_ACCEPTED_OR_DECLINED, this);
        sessionStorageService.getPlayer().addGameEventListener(ACTION_ALL_HINTS_EVALUATED, this);
        sessionStorageService.getPlayer().addGameEventListener(ACTION_GUESS_PROVIDED, this);
        sessionStorageService.getPlayer().addGameEventListener(GAME_ENDED, this);
    }

    private void removeGameEventListener() {
        sessionStorageService.getPlayer().removeGameEventListener(PLAYER_CHANGED, this);
        sessionStorageService.getPlayer().removeGameEventListener(NEXT_ROUND_STARTED, this);
        sessionStorageService.getPlayer().removeGameEventListener(ACTION_WORD_CHOSEN, this);
        sessionStorageService.getPlayer().removeGameEventListener(ACTION_HINT_PROVIDED, this);
        sessionStorageService.getPlayer().removeGameEventListener(ACTION_ALL_HINTS_PROVIDED, this);
        sessionStorageService.getPlayer().removeGameEventListener(ACTION_HINT_ACCEPTED_OR_DECLINED, this);
        sessionStorageService.getPlayer().removeGameEventListener(ACTION_ALL_HINTS_EVALUATED, this);
        sessionStorageService.getPlayer().removeGameEventListener(ACTION_GUESS_PROVIDED, this);
        sessionStorageService.getPlayer().removeGameEventListener(GAME_ENDED, this);
    }

    private void markResetUI() {
        getUI().ifPresent(ui -> ui.access(() -> {
            remove(playerCmps.values().toArray(new PlayerComponent[0]));
            Point[] positions = new Point[]{
                    new Point(15, 15),
                    new Point(35, 5),
                    new Point(55, 15),
                    new Point(65, 35),
                    new Point(52, 55),
                    new Point(30, 60),
                    new Point(15, 40)
            };
            List<Player> players = this.gameService.getPlayersForGame(this.sessionStorageService);
            for (int i = 0; i < players.size(); i++) {
                Player player = players.get(i);
                PlayerComponent playerComponent = new PlayerComponent(sessionStorageService, gameService, player);
                layoutAbsolute(playerComponent, positions[i].y + "%", positions[i].x + "%", null, null);
                playerCmps.put(player, playerComponent);
            }
            add(playerCmps.values().toArray(new PlayerComponent[0]));
        }));
    }

    private void leaveGame(ClickEvent<Button> buttonClickEvent) {
        removeGameEventListener();
        gameService.leave(sessionStorageService);
        getUI().ifPresent(ui -> ui.access(() ->
                ui.navigate("")
        ));
    }

}
