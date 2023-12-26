import Cards.Card;
import Cards.Deck;

import java.util.ArrayList;
import java.util.Scanner;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    enum Game{
        BLACKJACK, WAR
    }
    /**
     * Prompts the player to pick one of the enumerated Choice options
     * @return the choice selected
     */
    private static Game getGame(){
        Scanner scanner = new Scanner(System.in);
        Game game = null;
        while(game == null) {
            System.out.println("Choose one of the following:");
            for(Game option : Game.values()){
                System.out.print(option.toString() + "  ");
            }
            try {
                game = Game.valueOf(scanner.nextLine().toUpperCase());
            } catch(IllegalArgumentException e){
                System.out.println("Invalid choice. Please enter a valid option.");
            }
        }
        return game;
    }
    public static void main(String[] args) {
//        Game desiredGame = getGame();
//        switch (desiredGame) {
//            case Game.BLACKJACK:
//                BlackJack bj = new BlackJack();
//                bj.play();
//                break;
//            case Game.WAR:
//                War war = new War();
//                war.play();
//                break;
//        }
//        Poker poker = new Poker();
        Card[] hand = new Card[]{
                new Card(Card.Suit.SPADE,"T"),
                new Card(Card.Suit.HEART,"T"),
                new Card(Card.Suit.CLUB,"6"),
                new Card(Card.Suit.DIAMOND,"+K"),
                new Card(Card.Suit.DIAMOND,"K"),
                new Card(Card.Suit.SPADE,"6"),
                new Card(Card.Suit.DIAMOND,"6")
        };

        Poker poker = new Poker();
        poker.play();

    }
}