package Cards;

import java.util.ArrayList;

public class Deck {
    private ArrayList<Card> deck;

    /**
     * Constructor for using multiple decks
     * @param numDecks the number of decks desired
     */

    public Deck(int numDecks) {
        deck = new ArrayList<>(52 * numDecks);
        for(int i = 0; i < numDecks; i++) {
            for (Card.Suit suit : Card.Suit.values()) {
                for (char rank : Card.ranks) {
                    deck.add(new Card(suit, String.valueOf(rank)));
                }
            }
        }
    }

    /**
     * Default constructor using one deck
     */
    public Deck() {
        this(1);
    }

    /**
     * Uses Math.random() to shuffle the deck
     */
    public void shuffle(){
        ArrayList<Card> shuffledDeck = new ArrayList<>(52);
        while(!deck.isEmpty()){
            shuffledDeck.add(deck.remove((int)(Math.random() * deck.size())));
        }
        deck = shuffledDeck;
    }

    /**
     * Allows the deck to be a private variable
     * @return the deck as an ArrayList
     */
    public ArrayList<Card> getCards(){
        return deck;
    }
}
