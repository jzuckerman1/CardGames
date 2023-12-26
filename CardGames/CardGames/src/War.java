import java.util.*;

import Cards.*;
public class War {
    private ArrayList<Card> deck;
    LinkedList<Card> playerHand;
    LinkedList<Card> opponentHand;

    /**
     * Prompts the player to pick Yes or No
     * @return a boolean representing player input Yes or No (T/F)
     */
    private boolean getYesNo(){
        Scanner scanner = new Scanner(System.in);
        String choice = "";
        while(choice.isEmpty()) {
            System.out.println("Pick either \"Yes\" or \"No\"");
            try {
                choice = scanner.nextLine().toUpperCase();
                if((!choice.equals("YES") && !choice.equals("NO"))){
                    choice = "";
                    throw new IllegalArgumentException("Invalid Input");
                }
            } catch(IllegalArgumentException e){
                System.out.println("Invalid choice. Please enter a valid option.");
            }
        }
        return choice.equals("YES");
    }

    /**
     * The constructor for the game
     */
    public War(){
        deck = new Deck(1).getCards();// Can customize how many decks
        playerHand = new LinkedList<>();
        opponentHand = new LinkedList<>();
        while(deck.size() / 2 != 0){//while an even number of cards
            playerHand.add(deck.remove((int)(Math.random() * deck.size())));
            opponentHand.add(deck.remove((int)(Math.random() * deck.size())));
        }
    }

    /**
     * Where code to play the game is stored
     */
    public void play(){
        System.out.println("Type anything to flip a card!");
        Scanner scanner = new Scanner(System.in);
        boolean tie;
        while(!(playerHand.isEmpty() || opponentHand.isEmpty())){
            tie = true;
            while(tie) {
                System.out.println("You have: " + playerHand.size() + " cards. Your opponent has " + opponentHand.size());
                scanner.next();
                Card playerCard = playerHand.removeFirst();
                Card oppCard = opponentHand.removeFirst();
                deck.add(playerCard);
                deck.add(oppCard);
                System.out.println("You flipped: " + playerCard);
                System.out.println("They flipped: " + oppCard);
                System.out.println(playerCard.getCardSmall() + " vs. " + oppCard.getCardSmall());
                int compareValue = playerCard.compareTo(oppCard);
                if (compareValue == 0) {
                    System.out.println("It's a tie. Let there be war! Flip three cards, face down.");
                    if(playerHand.size() >= 4 && opponentHand.size() >= 4) {
                        for (int i = 0; i < 3; i++) { // Place 3
                        scanner.next();
                            deck.add(playerHand.getFirst());
                            deck.add(opponentHand.getFirst());
                            System.out.println(playerHand.removeFirst().hiddenCard() + opponentHand.removeFirst().hiddenCard());
                        }
                    } else if(playerHand.size() <= 4) { // Not enough cards to continue
                        System.out.println("You don't have enough cards to continue!");
                        while(!playerHand.isEmpty()){
                            opponentHand.add(playerHand.remove());
                        }
                    } else{
                        System.out.println("Your opponent doesn't have enough cards to continue!");
                        while(!opponentHand.isEmpty()){
                            playerHand.add(opponentHand.remove());
                        }
                    }
                    break;
                } else{
                    tie = false;
                    if(compareValue > 0){ //If the player wins
                        System.out.println("You win!");
                        while(!deck.isEmpty()){
                            playerHand.add(deck.getFirst());
                            deck.removeFirst();
                        }
                    } else{
                        System.out.println("You lose!");
                        while(!deck.isEmpty()){
                            opponentHand.add(deck.getFirst());
                            deck.removeFirst();
                        }
                    }
                }
            }
        }
        if(playerHand.isEmpty()){
            System.out.println("Game over! You lost!");
        } else{
            System.out.println("Game over! You win!");
        }
        System.out.println("Play again?");
        if(getYesNo()){
            deck = new Deck().getCards();
            playerHand = new LinkedList<>();
            opponentHand = new LinkedList<>();
            while(deck.size() / 2 != 0){//while an even number of cards
                playerHand.add(deck.remove((int)(Math.random() * deck.size())));
                opponentHand.add(deck.remove((int)(Math.random() * deck.size())));
            }
            this.play();
        } else{
            System.out.println("bye!");
        }
    }
}