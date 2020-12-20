package de.clique.westwood.justone.view.component;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.TextField;
import de.clique.westwood.justone.model.Card;
import de.clique.westwood.justone.model.Player;
import de.clique.westwood.justone.service.GameService;
import de.clique.westwood.justone.service.SessionStorageService;
import de.clique.westwood.justone.view.GameRoomView;
import de.clique.westwood.justone.view.layout.AbsoluteLayout;

/**
 * UI representation of Panel with {@link Card} on {@link GameRoomView}.
 */
public class PlayerComponent extends AbsoluteLayout {

    private final SessionStorageService sessionStorageService;
    private final GameService gameService;
    private final Player player;

    private final H4 playerLbl;
    private final Image panelImg;
    private final CardComponent cardCmp;
    private final TextField guessOrHintInp;
    private final Button acceptBtn;
    private final Button declineBtn;

    /**
     * Constructor
     * @param sessionStorageService the session
     * @param gameService the game
     * @param player the player
     */
    public PlayerComponent(SessionStorageService sessionStorageService, GameService gameService, Player player) {
        this.sessionStorageService = sessionStorageService;
        this.gameService = gameService;
        this.player = player;

        boolean playerIsCurrentUser = this.player.equals(this.sessionStorageService.getPlayer());
        boolean playerIsOnTurn = this.player.equals(this.gameService.getPlayerOnTurn(this.sessionStorageService));
        playerLbl = new H4(player.getName());
        panelImg = new Image("imgs/panel.png", "Panel");
        cardCmp = new CardComponent(sessionStorageService, gameService, player);
        guessOrHintInp = new TextField();
        if (playerIsOnTurn) {
            guessOrHintInp.setLabel("Guess");
        } else {
            guessOrHintInp.setLabel("Hint");
        }
        guessOrHintInp.setRequired(true);
        guessOrHintInp.setVisible(false);
        acceptBtn = new Button("Accept (✔)");
        acceptBtn.setVisible(false);
        declineBtn = new Button("Decline (❌)");
        declineBtn.setVisible(false);
        if (playerIsCurrentUser) {
            guessOrHintInp.addKeyDownListener(Key.ENTER, keyPressEvent -> {
                if (guessOrHintInp.isInvalid()) {
                    Notification.show("Please enter a word!");
                } else {
                    if (playerIsOnTurn) {
                        submitGuess(guessOrHintInp.getValue());
                    } else {
                        submitHint(guessOrHintInp.getValue());
                    }
                }
            });
            if (!playerIsOnTurn) {
                acceptBtn.addClickListener(buttonClickEvent -> submitAccept());
                declineBtn.addClickListener(buttonClickEvent -> submitDecline());
            }
        }

        layoutAbsolute(playerLbl, "0px", "0px", null, null);
        layoutAbsolute(panelImg, "50px", "0px", null, null);
        layoutAbsolute(cardCmp, "-50px", "0px", null, null);
        layoutAbsolute(guessOrHintInp, "60px", "40px", null, null);
        layoutAbsolute(acceptBtn, "150px", "0px", null, null);
        layoutAbsolute(declineBtn, "150px", "170px", null, null);

        add(playerLbl, panelImg, cardCmp, guessOrHintInp, acceptBtn, declineBtn);
    }

    /**
     * Get the related {@link CardComponent}
     * @return the card component
     */
    public CardComponent getCardCmp() {
        return cardCmp;
    }

    /**
     * Show the hint/guess Input
     */
    public void markShowHintOrGuessInp() {
        getUI().ifPresent(ui -> ui.access(() -> {
            cardCmp.setVisible(false);
            guessOrHintInp.setVisible(true);
            guessOrHintInp.focus();
        }));
    }

    /**
     * Mark the guess/hint as provided
     * @param value the hint/guess value
     */
    public void markGuessOrHintProvided(String value) {
        getUI().ifPresent(ui -> ui.access(() -> {
            cardCmp.setVisible(false);
            guessOrHintInp.setVisible(true);
            guessOrHintInp.setValue(value);
            guessOrHintInp.getStyle().set("color", "green");
        }));
    }

    /**
     * Show the accept and decline Buttons
     */
    public void markShowAcceptAndDeclineButtons() {
        getUI().ifPresent(ui -> ui.access(() -> {
            guessOrHintInp.getStyle().remove("color");
            acceptBtn.setVisible(true);
            declineBtn.setVisible(true);
        }));
    }

    /**
     * Mark the guess/hint as accepted
     */
    public void markHintAsAccepted() {
        getUI().ifPresent(ui -> ui.access(() -> {
            guessOrHintInp.getStyle().set("color", "green");
            acceptBtn.setVisible(false);
            declineBtn.setVisible(false);
        }));
    }

    /**
     * Mark the guess/hint as declined
     */
    public void markHintAsDeclined() {
        getUI().ifPresent(ui -> ui.access(() -> {
            panelImg.setSrc("imgs/panel-dropped.png");
            acceptBtn.setVisible(false);
            declineBtn.setVisible(false);
            guessOrHintInp.clear();
        }));
    }

    private void submitAccept() {
        gameService.provideHintAcceptedOrRejected(sessionStorageService, true);
    }

    private void submitDecline() {
        gameService.provideHintAcceptedOrRejected(sessionStorageService, false);
    }

    private void submitGuess(String value) {
        gameService.provideGuess(sessionStorageService, value);
        getUI().ifPresent(ui -> ui.access(() -> guessOrHintInp.setEnabled(false)));
    }

    private void submitHint(String value) {
        gameService.provideHint(sessionStorageService, value);
        getUI().ifPresent(ui -> ui.access(() -> guessOrHintInp.setEnabled(false)));
    }

}