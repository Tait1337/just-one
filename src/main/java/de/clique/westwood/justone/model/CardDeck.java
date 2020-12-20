package de.clique.westwood.justone.model;

import java.util.*;

/**
 * A Deck of Cards for a {@link Game}.
 */
public class CardDeck {

    private static final int DECK_SIZE = 13;
    private static final String CARD_DECK_FILENAME = "carddeck.csv";
    private static final Card[] POSSIBLE_CARDS = readCardDeck();

    private Card[] unusedCards;
    private Card[] usedCards;

    /**
     * Constructor
     */
    public CardDeck() {
        unusedCards = new Card[DECK_SIZE];
        usedCards = new Card[0];
        mixCards();
    }

    /**
     * Get the number of unused cards in deck
     * @return the number
     */
    public int getNumberOfUnusedCardsInDeck(){
        return unusedCards.length;
    }

    /**
     * Take a card from the deck
     * @return the card or null if no more cards exists
     */
    public Card takeACard(){
        if (unusedCards.length > 0){
            Card takenCard = unusedCards[0];
            usedCards = Arrays.copyOf(usedCards, usedCards.length + 1);
            usedCards[usedCards.length -1] = takenCard;
            unusedCards = Arrays.copyOfRange(unusedCards, 1, unusedCards.length);
            return takenCard;
        }
        return null;
    }

    private void mixCards(){
        Set<Integer> randomDeckIds = new HashSet<>();
        while (randomDeckIds.size() < DECK_SIZE) {
            int randomNumber = (int) Math.floor(Math.random() * Math.floor(POSSIBLE_CARDS.length));
            randomDeckIds.add(randomNumber);
        }
        int i = 0;
        for (int randomNumber: randomDeckIds) {
            unusedCards[i++] = POSSIBLE_CARDS[randomNumber];
        }
    }

    private static Card[] readCardDeck(){
        List<Card> deck = new ArrayList<>();
        try (Scanner scanner = new Scanner(CardDeck.class.getResourceAsStream("/" + CARD_DECK_FILENAME))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] splitLine = line.split(";");
                if (splitLine.length == 5){
                    deck.add(new Card(splitLine[0], splitLine[1], splitLine[2], splitLine[3], splitLine[4]));
                }else{
                    throw new IllegalStateException("Invalid card definition found in " + CARD_DECK_FILENAME + ": " + line);
                }
            }
        }
        return deck.toArray(new Card[0]);
    }

}
