package de.clique.westwood.justone.model;

/**
 * A Card out of the {@link CardDeck}.
 */
public class Card {

    private static final String CIPHER = "**********";
    private final String word1;
    private final String word2;
    private final String word3;
    private final String word4;
    private final String word5;

    /**
     * Constructor
     * @param word1 first word on the card
     * @param word2 second word on the card
     * @param word3 third word on the card
     * @param word4 fourth word on the card
     * @param word5 fifth word on the card
     */
    public Card(String word1, String word2, String word3, String word4, String word5) {
        this.word1 = word1;
        this.word2 = word2;
        this.word3 = word3;
        this.word4 = word4;
        this.word5 = word5;
    }

    /**
     * Get a word from the card
     * @param no number of word to fetch (1-5)
     * @return the word
     */
    public String getWord(int no){
        switch (no){
            case 1:
                return word1;
            case 2:
                return word2;
            case 3:
                return word3;
            case 4:
                return word4;
            case 5:
                return word5;
            default:
                throw new IllegalArgumentException("Word number " + no + " does not exists");
        }
    }

    /**
     * Get the ciphered word
     * @return the unreadable word
     */
    public static String getCipheredWord(){
        return CIPHER;
    }

}
