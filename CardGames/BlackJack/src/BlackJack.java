import java.util.*;
import Cards.*;
public class BlackJack {
    ArrayList<Card> deck;
    static double balance = 100;
    LinkedList<ArrayList<Card>> playerHands;
    ArrayList<Card> dealerHand;
    enum Choice{
        HIT, STAND, DOUBLE, SPLIT;
    }
    public BlackJack() {
        Deck newDeck = new Deck();
        newDeck.shuffle();
        deck = newDeck.getCards();
    }

    private static int getValue(List<Card> cards){
        int aces = 0;
        int value = 0;
        for(Card card : cards){
            try {
                value += Deck.cardValues.get(card.getRank());
            } catch (Exception e){
                aces++;
            }
        }
        if(aces > 0){
            for(int i = 0; i < aces; i++){
                if(value + 11 <= 21){
                    value += 11;
                } else{
                    value++;
                }
            }
        }
        return value;
    }

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
    private Choice getChoice(){
        Scanner scanner = new Scanner(System.in);
        Choice choice = null;
        while(choice == null) {
            System.out.println("Choose one of the following:");
            for(Choice option : BlackJack.Choice.values()){
                System.out.print(option.toString() + "  ");
            }
            try {
                choice = Choice.valueOf(scanner.nextLine().toUpperCase());
            } catch(IllegalArgumentException e){
                System.out.println("Invalid choice. Please enter a valid option.");
            }
        }
        return choice;
    }

    private double getBet(){
        Scanner scanner = new Scanner(System.in);
        double choice = -1;
        while(choice == -1) {
            System.out.println("Enter a quantity for your bet (0 - " + balance + ")");
            try {
                choice = Double.parseDouble(scanner.nextLine());
                if(choice < 0 || choice > balance) throw new IllegalArgumentException();
            } catch(IllegalArgumentException e){
                choice = -1;
                System.out.println("Out of required range. Please enter a valid number.");
            } catch(Exception e){
                choice = -1;
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
        return choice;
    }

    public void play(){
        System.out.println("You have " + balance);
        double bet = getBet();
        playerHands = new LinkedList<ArrayList<Card>>();
        playerHands.add(new ArrayList<Card>(2));
        dealerHand = new ArrayList<Card>(2);

        System.out.println("\nYour Hand");
        for(int i = 0; i < 2; i++) {
            Card randomCard = deck.remove((int)(Math.random() * deck.size()));
            playerHands.getFirst().add(randomCard);
            System.out.print(randomCard);
        }

        System.out.println("\nDealer's Hand:");
        for(int i = 0; i < 2; i++) {
            Card randomCard = deck.remove((int)(Math.random() * deck.size()));
            dealerHand.add(randomCard);
            if(i % 2 == 1) {
                System.out.print(randomCard.hiddenCard());
            } else{
                System.out.print(randomCard);
            }
        }
        boolean playerCanPlay = true;
        while(playerCanPlay) {
            for(int i = 0; i < playerHands.size(); i++) {
                ArrayList<Card> playerHand = playerHands.get(i);
                if(playerHands.size() > 1){
                    System.out.println("For hand: ");
                    System.out.println(playerHand);
                }

                BlackJack.Choice playerChoice = getChoice();
                Card newCard;
                switch (playerChoice) {
                    case HIT:
                        newCard = deck.remove((int) (Math.random() * deck.size()));
                        System.out.println("\n You drew: " + newCard);
                        playerHands.get(i).add(newCard);
                        if (getValue(playerHands.get(i)) > 21) {
                            playerCanPlay = false;
                        }
                        break;
                    case SPLIT:
                        if (playerHand.get(0).getRank() != playerHand.get(1).getRank() || playerHand.size() > 2) {
                            System.out.println("You can't do that!");
                        } else {
                            playerHands.add(new ArrayList<>());
                            playerHands.getFirst().add(playerHands.get(i).remove(1));
                        }
                        break;
                    case STAND:
                        playerCanPlay = false;
                        break;
                    case DOUBLE:
                        if(balance < (bet * 2)) {
                            System.out.println("You can't do that!");
                        } else {
                            bet *= 2;
                            newCard = deck.remove((int) (Math.random() * deck.size()));
                            System.out.println("\n You drew: " + newCard);
                            playerHands.get(i).add(newCard);
                            playerCanPlay = false;
                        }
                        break;
                }
            }
        }
        System.out.println("\nDealer's Hand: ");
        System.out.println(dealerHand);
        while(getValue(dealerHand) < 17){
            Card newCard = deck.remove((int) (Math.random() * deck.size()));
            dealerHand.add(newCard);
            System.out.println(dealerHand);
        }

        int dealerValue = getValue(dealerHand);
        System.out.println("Dealer had: " + dealerValue);
        for(ArrayList<Card> hand : playerHands) {
            int playerValue = getValue(hand);
            System.out.println("You had: " + playerValue + "\n");
            if ((playerValue >= dealerValue || dealerValue > 21) && playerValue <= 21 ) {
                System.out.println("You win!");
                balance += bet;
            } else {
                System.out.println("You lose!");
                balance -= bet;
            }
        }
        if (balance > 0) {
            System.out.println("New balance: " + balance);
            System.out.println("\nPlay again? (Yes/No)");
            if(getYesNo()) {
                this.play();
            }
        } else{
            System.out.println("You ran out of money!");
        }
        System.out.println("Bye!");
    }
}
