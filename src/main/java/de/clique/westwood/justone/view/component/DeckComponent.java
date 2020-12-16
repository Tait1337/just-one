package de.clique.westwood.justone.view.component;

import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import de.clique.westwood.justone.model.CardDeck;
import de.clique.westwood.justone.service.GameService;
import de.clique.westwood.justone.service.SessionStorageService;
import de.clique.westwood.justone.view.GameRoomView;
import de.clique.westwood.justone.view.layout.AbsoluteLayout;

/**
 * UI representation of {@link CardDeck} on {@link GameRoomView}.
 */
public class DeckComponent extends AbsoluteLayout {

    private final SessionStorageService sessionStorageService;
    private final GameService gameService;

    private final Image deckImg;
    private final Paragraph numberOfUnusedCardsLbl;

    /**
     * Constructor
     * @param sessionStorageService the session
     * @param gameService the game
     */
    public DeckComponent(SessionStorageService sessionStorageService, GameService gameService) {
        this.gameService = gameService;
        this.sessionStorageService = sessionStorageService;

        deckImg = new Image("imgs/deck.png", "Deck");
        numberOfUnusedCardsLbl = new Paragraph(gameService.getNumberOfUnusedCardsInDeck(sessionStorageService) + " Cards");

        layoutAbsolute(deckImg, "0px", "0px", null, null);
        layoutAbsolute(numberOfUnusedCardsLbl, "60px", "90px", null, null);

        add(deckImg, numberOfUnusedCardsLbl);
    }

    /**
     * Update the number of cards in deck
     */
    public void updateNumberOfUnusedCards() {
        getUI().ifPresent(ui -> ui.access(() -> numberOfUnusedCardsLbl.setText(gameService.getNumberOfUnusedCardsInDeck(sessionStorageService) + " Cards")));
    }

}