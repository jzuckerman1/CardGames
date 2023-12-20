import java.util.*;
import Cards.*;

public class Poker {
    ArrayList<Card> deck;
    static double balance = 100;
    ArrayList<Card> playerHand;
    Opponent[] opponents;
    final int numOpponents = 2;
    enum Option{
        BET, CALL, RAISE, FOLD, CHECK
    }

    private enum Position{
        BIGBLIND, SMALLBLIND, CUTOFF, BUTTON, UNDERTHEGUN
    }
    public Poker() {
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
    private Option getChoice(){
        Scanner scanner = new Scanner(System.in);
        Option choice = null;
        while(choice == null) {
            System.out.println("Choose one of the following:");
            for(Option option : Poker.Option.values()){
                System.out.print(option.toString() + "  ");
            }
            try {
                choice = Option.valueOf(scanner.nextLine().toUpperCase());
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

        double playerInPot = getBet();
        double opponentInPot = 0;
        playerHand = new ArrayList<Card>(2);
        opponents = new Opponent[numOpponents];

        for(Opponent opp : opponents){
            opp = new Opponent(Position.BUTTON);//To be changed
        }

        System.out.println("\nYour Hand");
        for(int i = 0; i < 2; i++) {
            Card randomCard = deck.remove((int)(Math.random() * deck.size()));
            playerHand.add(randomCard);
            System.out.print(randomCard);
        }
        boolean activeGame = true;
        while(activeGame) {
            boolean playersTurn = true;
            while (playersTurn) {
                System.out.println("You have " + balance + ", with " + playerInPot + "already in the pot.");
                Poker.Option playerChoice = getChoice();
                switch (playerChoice) {
                    case CHECK:
                        if (opponentInPot != 0) System.out.println("You can't do that!");
                        break;
                    case BET:
                        if (opponentInPot != 0) System.out.println("You can't do that!");
                        break;
                    case RAISE:
                        if (opponentInPot == 0) System.out.println("You can't do that!");
                        break;
                    case CALL:
                        if (opponentInPot == 0) System.out.println("You can't do that!");
                        break;
                    case FOLD:
                        activeGame = false;
                        break;

                }
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

    private class Opponent{
//        private final static HashMap<Card[], Integer> startingHandRank;
//        static {
//            startingHandRank = new HashMap<>(52*51);
//            for (int i = 1; i < 9; i++) {
//                startingHandRank.put(ranks[i], Integer.parseInt(String.valueOf(ranks[i])));
//            }
//            for (int i = 9; i < 13; i++) {
//                startingHandRank.put(ranks[i], 10);
//            }
//        }
        Poker.Position position;
        double balance;
        private enum Style{
            AGGRESSIVE, PASSIVE,
        }
        public Opponent(Poker.Position position){
            this.position = position;
        }

        public Option getMove(){
//            switch (position) {
//                case BUTTON:
//                    break;
//                case CUTOFF:
//                    break;
//                case BIGBLIND:
//            }
            return Option.values()[(int)(Math.random() * Option.values().length)];
        }
    }
}
