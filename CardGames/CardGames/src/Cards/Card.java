package Cards;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
public class Card implements Comparable<Card>{
    private final Suit suit;
    private Rank rank;

    final static public char[] ranks = new char[]{'A', '2', '3', '4', '5', '6', '7', '8', '9', 'T', 'J', 'Q', 'K'};

    public final static HashMap<Character, Integer> cardValues;
    static {
        cardValues = new HashMap<>(13);
        for (int i = 1; i < 9; i++) {
            cardValues.put(ranks[i], Integer.parseInt(String.valueOf(ranks[i])));
        }
        for (int i = 9; i < 13; i++) {
            cardValues.put(ranks[i], i + 1);
        }
    }

    public enum Suit {
        DIAMOND, HEART, SPADE, CLUB;
    }

    /**
     * Constructor for a given playing card
     * @param suit The suit of the card
     * @param rank The rank of the card
     */

    public Card(Suit suit, String rank){
        try {
            this.rank = new Rank(rank);
        }catch(IllegalArgumentException e){
            this.rank = null;
        }
        this.suit = suit;
    }

    /**
     * Allows the rank to be a private variable in the nested class
     * @return a character representation of the rank.
     */
    public char getRank(){
        return this.rank.getRank();
    }

    @Override
    public boolean equals(Object o){
        if(o == this){
            return true;
        }
        if(!(o instanceof Card)){
            return false;
        }

        Card other = (Card)(o);
        return this.rank.getRank() == other.rank.getRank();
    }

    /**
     * Allows the suit to be a private (and final) variable
     * @return an enumerated type of the suit.
     */
    public Suit getSuit(){
        return this.suit;
    }

    /**
     * Compares to cards based on the rank of them, respectively. Aces are high cards.
     * @param other the object to be compared.
     * @return an integer representing  if the card goes before or after the 'other' card
     */
    @Override
    public int compareTo(Card other) {
        int value;
        try {
            value = cardValues.get(this.getRank()) - cardValues.get(other.getRank());
            if(value != 0){value = value / Math.abs(value);}//push it to 1 / -1.
        }catch(Exception e){//For aces
            if(this.getRank() == 'A'){
                if(other.getRank() == 'A'){
                    value = 0;
                } else{
                    value = 1;
                }
            } else{
                value = -1;
            }
        }
        return value;
    }

    public static int getValue(Card card) throws AceException {
        try {
            return Card.cardValues.get(card.getRank());
        } catch (Exception e){
            throw new AceException();
        }
    }



    /**
     * Gives a card in a two string format rather than ascii. Makes it easier to read.
     * @return A two character string with the rank and suit of the card
     */
    public String getCardSmall(){
        String retval = "";
        if (this.getRank() == 'T') {
            retval += "10";
        } else {
            retval += String.valueOf(this.getRank());
        }
        switch(this.suit){
            case Suit.DIAMOND:
                retval += "◆";
                break;
            case Suit.SPADE:
                retval += "♠";
                break;
            case Suit.CLUB:
                retval += "♣";
                break;
            case Suit.HEART:
                retval += "♥";
                break;
        }
        return retval;
    }

    /**
     * Overrides the two string method to give an ascii card.
     * @return An ascii card with the rank and suit of the card.
     */
    @Override
    public String toString(){
        String retval = "\n----------------\n";
        if (getRank() == 'T') {
            retval += "| 10           |\n";
        } else {
            retval += "| " + getRank() + "            |\n";
        }
        retval += "|              |\n";
        switch(this.suit){
            case CLUB:
                retval += """
                        |     ____     |
                        |   __\\  /__   |
                        |  /        \\  |
                        |  \\__    __/  |
                        |     |__|     |
                        """;
                break;
            case SPADE:
                retval += """
                        |     _/\\_     |
                        |   _/    \\_   |
                        |  /        \\  |
                        |  \\_/|  |\\_/  |
                        |     /__\\     |
                        """;
                break;
            case HEART:
                retval += """
                        |   ___  ___   |
                        |  /   \\/   \\  |
                        |  \\        /  |
                        |    \\    /    |
                        |      \\/      |
                        """;
                break;
            case DIAMOND:
                retval += """
                        |       /\\     |
                        |      /  \\    |
                        |     |    |   |
                        |      \\  /    |
                        |       \\/     |
                        """;
                break;
        }
        retval += "|              |\n";
        if (this.getRank() == 'T') {
            retval += "|           10 |\n";
        } else {
            retval += "|            " + this.getRank() + " |\n";
        }
        retval += "----------------";
        return retval;
    }

    /**
     * Uses the toString to print cards next to each other.
     * @param cards Array of cards to print next to each other
     * @param displayCards Optional input for showing hidden cards
     * @return a string of all the cards next to one another.
     */
    public static String toStringAbreast(Card[] cards, int[] ...displayCards){
        if(cards == null){return "";}
        if(cards.length < 2){return "";}
        String[] cardStrings = Arrays.stream(cards.clone()).map(Card::toString).toArray(String[]::new);
        String[][] cardLineStrings = new String[cards.length][];
        for(int i = 0; i < cards.length; i++){
            if(displayCards.length == 0) {
                cardLineStrings[i] = cardStrings[i].split("\n");
            }
            else if(!(displayCards[0][i] == 0)) {//Only look in the first array segment rather than passing many inputs
                cardLineStrings[i] = cardStrings[i].split("\n");
            } else{
                cardLineStrings[i] = cards[i].hiddenCard().split("\n");
            }
        }
        StringBuilder retval = new StringBuilder();
        for(int i = 0; i < cardLineStrings[0].length; i++){//Number of rows in a card
            for(int j = 0; j < cards.length; j++){
                retval.append(cardLineStrings[j][i]).append("\t");
            }
            retval.append("\n");
        }
        return retval.toString();
    }

    /**
     * Shows a blank card if the card is hidden
     * @return an empty card
     */
    public String hiddenCard(){
        return "\n" + """
        ----------------
        | ############ |
        | ############ |
        | ############ |
        | ############ |
        | ############ |
        | ############ |
        | ############ |
        | ############ |
        | ############ |
        ----------------
                        """ + "\n";
    }

    static class Rank{
        private char rank;

        /**
         * Constructor for the rank of a card. Takes a string
         * @param value
         */
        public Rank(String value) {
            try{
                Integer.parseInt(value);
                rank = value.charAt(0);
            }catch(NumberFormatException e){ //Something other than a number
                rank = value.charAt(0);
            }
        }

        /**
         * Allows the rank to be a private variable
         * @return the character of the rank
         */
        public char getRank(){
            return this.rank;
        }
    }
}
