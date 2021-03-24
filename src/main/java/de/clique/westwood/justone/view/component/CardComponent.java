package de.clique.westwood.justone.view.component;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Image;
import de.clique.westwood.justone.model.Card;
import de.clique.westwood.justone.model.Player;
import de.clique.westwood.justone.service.GameService;
import de.clique.westwood.justone.service.SessionStorageService;
import de.clique.westwood.justone.view.GameRoomView;
import de.clique.westwood.justone.view.layout.AbsoluteLayout;

/**
 * UI representation of {@link Card} on {@link GameRoomView}.
 */
public class CardComponent extends AbsoluteLayout {

    private final transient GameService gameService;
    private final transient SessionStorageService sessionStorageService;
    private final transient Player player;
    private transient Card card;

    private Image cardImg;
    private Button word1Btn;
    private Button word2Btn;
    private Button word3Btn;
    private Button word4Btn;
    private Button word5Btn;
    private static final String COLOR_KEY = "color";
    private static final String COLOR_VALUE = "green";

    /**
     * Constructor
     * @param sessionStorageService the session
     * @param gameService the game
     * @param player the related player
     */
    public CardComponent(SessionStorageService sessionStorageService, GameService gameService, Player player) {
        this.sessionStorageService = sessionStorageService;
        this.gameService = gameService;
        this.player = player;

        boolean playerIsOnTurn = this.player.equals(this.gameService.getPlayerOnTurn(this.sessionStorageService));
        if (playerIsOnTurn) {
            card = gameService.getActiveCard(sessionStorageService);
            if (card != null){
                cardImg = new Image("imgs/card.png", "Card");
                word1Btn = new Button();
                word2Btn = new Button();
                word3Btn = new Button();
                word4Btn = new Button();
                word5Btn = new Button();
                boolean playerIsCurrentUser = this.player.equals(this.sessionStorageService.getPlayer());
                if (playerIsCurrentUser) {
                    word1Btn.addClickListener(e -> this.chooseWord(1));
                    word2Btn.addClickListener(e -> this.chooseWord(2));
                    word3Btn.addClickListener(e -> this.chooseWord(3));
                    word4Btn.addClickListener(e -> this.chooseWord(4));
                    word5Btn.addClickListener(e -> this.chooseWord(5));
                    word1Btn.setText(Card.getCipheredWord());
                    word2Btn.setText(Card.getCipheredWord());
                    word3Btn.setText(Card.getCipheredWord());
                    word4Btn.setText(Card.getCipheredWord());
                    word5Btn.setText(Card.getCipheredWord());
                } else {
                    word1Btn.setText(card.getWord(1));
                    word2Btn.setText(card.getWord(2));
                    word3Btn.setText(card.getWord(3));
                    word4Btn.setText(card.getWord(4));
                    word5Btn.setText(card.getWord(5));
                    word1Btn.setEnabled(false);
                    word2Btn.setEnabled(false);
                    word3Btn.setEnabled(false);
                    word4Btn.setEnabled(false);
                    word5Btn.setEnabled(false);
                }

                layoutAbsolute(cardImg, "15px", "0px", null ,null);
                layoutAbsolute(word1Btn, "15px", "112px", null ,null);
                layoutAbsolute(word2Btn, "45px", "109px", null ,null);
                layoutAbsolute(word3Btn, "75px", "105px", null ,null);
                layoutAbsolute(word4Btn, "105px", "100px", null ,null);
                layoutAbsolute(word5Btn, "135px", "90px", null ,null);
                add(cardImg, word1Btn, word2Btn, word3Btn, word4Btn, word5Btn);
            }
        }
    }

    /**
     * Mark the chosen word
     * @param wordNo the word number
     */
    public void markChosenWord(int wordNo) {
        getUI().ifPresent(ui -> ui.access(() -> {
            word1Btn.setVisible(false);
            word2Btn.setVisible(false);
            word3Btn.setVisible(false);
            word4Btn.setVisible(false);
            word5Btn.setVisible(false);
            switch (wordNo) {
                case 1:
                    word1Btn.setVisible(true);
                    word1Btn.getStyle().set(COLOR_KEY, COLOR_VALUE);
                    break;
                case 2:
                    word2Btn.setVisible(true);
                    word2Btn.getStyle().set(COLOR_KEY, COLOR_VALUE);
                    break;
                case 3:
                    word3Btn.setVisible(true);
                    word3Btn.getStyle().set(COLOR_KEY, COLOR_VALUE);
                    break;
                case 4:
                    word4Btn.setVisible(true);
                    word4Btn.getStyle().set(COLOR_KEY, COLOR_VALUE);
                    break;
                case 5:
                    word5Btn.setVisible(true);
                    word5Btn.getStyle().set(COLOR_KEY, COLOR_VALUE);
                    break;
                default:
                    throw new IllegalArgumentException("Word number " + wordNo + " does not exists");
            }
        }));
    }

    private void chooseWord(int wordNo) {
        gameService.chooseWord(sessionStorageService, wordNo);
        word1Btn.setEnabled(false);
        word2Btn.setEnabled(false);
        word3Btn.setEnabled(false);
        word4Btn.setEnabled(false);
        word5Btn.setEnabled(false);
    }

}
