package Cards;

import java.util.*;
public class Deck {
    private ArrayList<Card> deck;
    final static char[] ranks = new char[]{'A', '2', '3', '4', '5', '6', '7', '8', '9', 'T', 'J', 'Q', 'K'};
    public final static HashMap<Character, Integer> cardValues;
    static {
        cardValues = new HashMap<>(13);
        for (int i = 1; i < 9; i++) {
            cardValues.put(ranks[i], Integer.parseInt(String.valueOf(ranks[i])));
        }
        for (int i = 9; i < 13; i++) {
            cardValues.put(ranks[i], 10);
        }
    }
    public Deck() {
        deck = new ArrayList<>(52);
        for (Card.Suit suit : Card.Suit.values()) {
            for (char rank : ranks) {
                deck.add(new Card(suit, String.valueOf(rank)));
            }
        }
    }

    public void shuffle(){
        ArrayList<Card> shuffledDeck = new ArrayList<>(52);
        while(!(deck.size() <= 0)){
            shuffledDeck.add(deck.remove((int)(Math.random() * deck.size())));
        }
        deck = shuffledDeck;
    }

    public ArrayList<Card> getCards(){
        return deck;
    }
}
