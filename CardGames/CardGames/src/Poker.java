import java.util.*;
import Cards.*;

public class Poker {
    ArrayList<Card> deck;
    LinkedList<Card> onTable;
    static double balance = 100;
    ArrayList<Card> playerHand;
    Opponent[] opponents;
    ArrayList<Opponent> opponentsPerRound;
    final int numOpponents = 2;
    public enum Option{
        BET, CALL, RAISE, FOLD, CHECK
    }

    public enum HandType{
        ROYALFLUSH, STRAIGHTFLUSH, FOUROFAKIND, FULLHOUSE, FLUSH, STRAIGHT, THREEOFAKIND, TWOPAIR, ONEPAIR, HIGHCARD
    }

    public enum Position{
        BIGBLIND, SMALLBLIND, CUTOFF, BUTTON, UNDERTHEGUN
    }
    public Poker() {
        onTable = new LinkedList<>();
        Deck newDeck = new Deck();
        playerHand = new ArrayList<>(2);
        newDeck.shuffle();
        deck = newDeck.getCards();
    }

    public static int getValue(Card card) {
        if(card.getRank() == 'A'){
            return 14;
        } else{
            try {
                return Card.getValue(card);
            }catch(Exception e){//Should never get here
                return 0;
            }
        }
    }

    public static Card hasFlush(Card[] cards){
         HashMap<Card.Suit, Integer> numOfSuit = new HashMap<>(4);
         for(Card.Suit suit : Card.Suit.values()){
             numOfSuit.put(suit, 0);
         }
         for(Card card : cards){
             Card.Suit cardSuit = card.getSuit();
             numOfSuit.put(cardSuit, numOfSuit.get(cardSuit) + 1);
         }
        for(Card.Suit suit : Card.Suit.values()){
            if(numOfSuit.get(suit) >= 5){
                for(int i = cards.length - 1; i > 0; i--){
                    if(cards[i].getSuit() == suit){
                        return cards[i];
                    }
                }
            }
        }
        return null;
    }


    public static Card firstCardInStraight(Card[] sortedCards){
        Card highestStraight = null;
        int pointerLeft = 0;
        int pointerRight = 1;
        int inARow = 1;
        Card[] newSortedCards = removeDuplicates(sortedCards);
        if(newSortedCards[newSortedCards.length - 1].getRank() == 'A'){//Set aces to high-explicit check to A,2,3,4,5
            for(int i = 0; i < 4; i++){
                if(getValue(newSortedCards[i]) != (i + 2)){
                    break;
                } else{
                    inARow++;
                }
            }
        }
        if(inARow >= 5){highestStraight = newSortedCards[3];}
        while(pointerRight < newSortedCards.length){
            if(getValue(newSortedCards[pointerRight]) - getValue(newSortedCards[pointerLeft]) == pointerRight - pointerLeft){
                inARow = pointerRight - pointerLeft;
                if(inARow >= 4){
                    highestStraight = newSortedCards[pointerRight];
                    pointerRight = ++pointerLeft; //Reset the pointer in case the next card starts a straight
                }
            } else{
                pointerLeft = pointerRight;
            }
            pointerRight++;
        }
        return highestStraight;
    }

    public FinalHandRank PairsAndSets(Card[] cards){
        HashMap<Character, Integer> numOfEachCard = new HashMap<>(13);
        FinalHandRank bestHand = null;
        for(char rank : Card.ranks){
            numOfEachCard.put(rank, 0);
        }
        for(Card card : cards){
            char rankOfCard = card.getRank();
            numOfEachCard.put(rankOfCard, numOfEachCard.get(rankOfCard) + 1);
        }
        for(char bucket : numOfEachCard.keySet()){
            if(numOfEachCard.get(bucket) == 0){continue;}
            if(numOfEachCard.get(bucket) >= 5){ //Take care of multiple decks
                numOfEachCard.put(bucket, 4);
            }
            Card cardAtHand = null;
            for (int i = 0; i < cards.length; i++) {
                if (cards[i].equals(new Card(Card.Suit.CLUB, String.valueOf(bucket)))) {
                    cardAtHand = cards[i];
                    break;
                }
            }
            switch(numOfEachCard.get(bucket)){
                case 4: //
                    return new FinalHandRank(HandType.FOUROFAKIND, cardAtHand);
                case 3:
                    if(bestHand != null) {
                        switch (bestHand.handType) {
                            case HandType.ONEPAIR, HandType.THREEOFAKIND, HandType.TWOPAIR:
                                if (bestHand.highestCard.compareTo(cardAtHand) > 0) {
                                    bestHand = new FinalHandRank(HandType.FULLHOUSE, bestHand.highestCard);
                                } else {
                                    bestHand = new FinalHandRank(HandType.FULLHOUSE, cardAtHand);
                                }
                                break;
                            default:
                                bestHand = new FinalHandRank(HandType.THREEOFAKIND, cardAtHand);
                        }
                    } else{
                        bestHand = new FinalHandRank(HandType.THREEOFAKIND, cardAtHand);
                    }
                    break;
                case 2:
                    if(bestHand != null){
                        switch (bestHand.handType){
                            case HandType.HIGHCARD:
                                bestHand = new FinalHandRank(HandType.ONEPAIR, cardAtHand);
                                break;
                            case HandType.ONEPAIR:
                                if (bestHand.highestCard.compareTo(cardAtHand) > 0) {
                                    bestHand =new FinalHandRank(HandType.TWOPAIR, bestHand.highestCard);
                                } else {
                                    bestHand = new FinalHandRank(HandType.TWOPAIR, cardAtHand);
                                }
                                break;
                            case HandType.THREEOFAKIND:
                                if (bestHand.highestCard.compareTo(cardAtHand) > 0) {
                                    bestHand = new FinalHandRank(HandType.FULLHOUSE, bestHand.highestCard);
                                } else {
                                    bestHand = new FinalHandRank(HandType.FULLHOUSE, cardAtHand);
                                }
                                break;
                            case HandType.TWOPAIR:
                                if (bestHand.highestCard.compareTo(cardAtHand) < 0) {
                                    bestHand = new FinalHandRank(HandType.TWOPAIR, cardAtHand);
                                }
                                break;
                        }
                    } else{
                        bestHand = new FinalHandRank(HandType.ONEPAIR, cardAtHand);
                    }
                    break;
                case 1:
                    if(bestHand == null){
                        bestHand = new FinalHandRank(HandType.HIGHCARD, cardAtHand);
                    } else if(bestHand.handType == HandType.HIGHCARD && bestHand.highestCard.compareTo(cardAtHand) < 0) {
                        bestHand = new FinalHandRank(HandType.HIGHCARD, bestHand.highestCard);
                    }
                    break;
                default:
                    break;
            }
        }
        return bestHand;
    }

    public static Card[] removeDuplicates(Card[] cards){
        ArrayList<Card> cardArrayList = new ArrayList<>();
        for(Card card : cards){
            if(!cardArrayList.contains(card)){
                cardArrayList.add(card);
            }
        }
        return cardArrayList.toArray(Card[]::new);
    }

    public FinalHandRank evaluateHand(Card[] givenHand) throws IllegalArgumentException{
        LinkedList<Card> evaluatingHand = (LinkedList<Card>) onTable.clone();
        evaluatingHand.addAll(List.of(givenHand));
        Card[] cards = evaluatingHand.toArray(Card[]::new);
        if(cards.length != 7){throw new IllegalArgumentException();}
        else{
            Card[] sorted = (Arrays.stream(cards).sorted(Card::compareTo).toArray(Card[]::new));
            Card lowestStraight = firstCardInStraight(sorted);
            Card highestFlush = hasFlush(sorted);
            if(lowestStraight != null){
                if(highestFlush != null){//Straight and flush can't have pairs/sets
                    if(lowestStraight.getRank() == 'T'){
                        return new FinalHandRank(HandType.ROYALFLUSH, lowestStraight);
                    } else{
                        return new FinalHandRank(HandType.STRAIGHTFLUSH, lowestStraight);
                    }
                } else{
                    return new FinalHandRank(HandType.STRAIGHT, lowestStraight);
                }
            }
            return PairsAndSets(sorted);
        }
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

    private double getBet(double lowerBound){
        Scanner scanner = new Scanner(System.in);
        double choice = -1;
        while(choice == -1) {
            System.out.println("Enter a quantity for your bet (" + lowerBound + " - " + balance + ")");
            try {
                choice = Double.parseDouble(scanner.nextLine());
                if(choice <= lowerBound || choice > balance) throw new IllegalArgumentException();
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
        double highestBet = 0;
        double pot = 0;
        opponents = new Opponent[numOpponents];

        for(int i = 0; i < opponents.length; i++){
            opponents[i] = new Opponent(String.valueOf(i), Position.BUTTON, deck.remove((int)(Math.random() * deck.size())), deck.remove((int)(Math.random() * deck.size())));//To be changed
        }

        opponentsPerRound = new ArrayList<>();
        opponentsPerRound.addAll(Arrays.asList(opponents));

        System.out.println("\nYour Hand");
        for(int i = 0; i < 2; i++) {
            Card randomCard = deck.remove((int)(Math.random() * deck.size()));
            playerHand.add(randomCard);
            System.out.print(randomCard);
        }
        int round = 0;
        boolean activeGame = true;
        while(activeGame) {
            highestBet = 0;
            if(round > 0) System.out.println("\nThe table shows: ");
            if (round == 1) {
                for (int i = 0; i < 3; i++) {
                    onTable.add(deck.remove((int) (Math.random() * deck.size())));
                }
            } else if (round == 2) { //turn and river
                onTable.add(deck.remove((int) (Math.random() * deck.size())));
            }  else if(round >= 3) {
                onTable.add(deck.remove((int) (Math.random() * deck.size())));
                activeGame = false;
            }
            round++;
            System.out.println(onTable);
            boolean playersTurn = true;
            boolean activeRound = true;
            while (activeRound) {
                activeRound = false;
                while (playersTurn) {
                    System.out.println("You have " + balance + ", with " + pot + " in the pot.");
                    Poker.Option playerChoice = getChoice();
                    switch (playerChoice) {
                        case CHECK:
                            if (highestBet != 0) {
                                System.out.println("You can't do that!");
                            }else{
                                playersTurn = false;
                            }
                            break;
                        case BET:
                            if (highestBet != 0) {
                                System.out.println("You can't do that!");
                            }else {
                                System.out.println("How large of a bet would you like to place?");
                                highestBet = getBet(0.0);
                                balance -= highestBet;
                                pot += highestBet;
                                playersTurn = false;
                            }
                            break;
                        case RAISE:
                            if (highestBet == 0) {
                                System.out.println("You can't do that!");
                            }else {
                                System.out.println("How large of a bet would you like to place?");
                                highestBet = getBet(highestBet);
                                balance -= highestBet;
                                playersTurn = false;
                            }
                            break;
                        case CALL:
                            if (highestBet == 0 || balance < highestBet) {
                                System.out.println("You can't do that!");
                            } else{
                                playersTurn = false;
                            }
                            break;
                        case FOLD:
                            playersTurn = false;
                            activeGame = false;
                            playerHand = new ArrayList<>();
                            break;
                    }
                }
                for (Opponent opp : opponentsPerRound) {
                    Option move = opp.getMove(highestBet);
                    if (move == Option.BET || move == Option.RAISE) {
                        System.out.println("Opponent " + move + " to " + opp.bet);
                        highestBet = opp.bet;
                        pot += opp.bet;
                        activeRound = true;
                    } else if(move == Option.FOLD){
                        System.out.println("Opponent " + move);
                        for(int i = 0; i < opponents.length; i++){
                            if(opp.equals(opponents[i])){
                                opponents[i].balance = opp.balance;
                                break;
                            }
                        }
                        opponentsPerRound.remove(opp);
                    } else if(move == Option.CALL) {
                        pot += highestBet;
                        System.out.println("Opponent " + move);
                    }else{
                        System.out.println("Opponent " + move); //check/call
                    }
                }
            }
        }

        if(!playerHand.isEmpty()){
            ArrayList<FinalHandRank> opponentsRank = new ArrayList<>(opponentsPerRound.size());
            for(int i = 0; i < opponentsPerRound.size(); i++){
                opponentsRank.add(evaluateHand(opponentsPerRound.get(i).getHand()));
                System.out.println("Opponent " + opponentsPerRound.get(i).name + " had " + opponentsRank.get(i));
            }
            FinalHandRank opponentBestHand = opponentsRank.stream().sorted().toList().getLast();
            int opponentIndex = -1;
            for(int i = 0; i < opponentsPerRound.size(); i++){
                if(opponentsRank.get(i).equals(opponentBestHand)){
                    opponentIndex = i;
                }
            }
            FinalHandRank playerHad = evaluateHand(playerHand.toArray(Card[]::new));
            System.out.println("You had " + playerHad);
            System.out.println("The best hand the opponent had was " + opponentBestHand);
            if(opponentBestHand.compareTo(playerHad) > 0){//opp wins
                System.out.println("You lose!");
                opponents[opponentIndex].balance += pot;
            } else{
                System.out.println("You win!");
                balance += pot;
            }
        }

        if (balance > 0) {
            System.out.println("New balance: " + balance);
            System.out.println("\nPlay again? (Yes/No)");
            if(getYesNo()) {
                Poker poker = new Poker();
                poker.play();
            }
        } else{
            System.out.println("You ran out of money!");
        }
        System.out.println("Bye!");
    }

    public class FinalHandRank implements Comparable<FinalHandRank>{
        HandType handType;
        Card highestCard;
        FinalHandRank(HandType handType, Card lowestBestCard){
            this.handType = handType;
            this.highestCard = lowestBestCard;
        }

        public final static HashMap<HandType, Integer> handRanks;
        static {
            handRanks = new HashMap<>(13);
            for (int i = 0; i < HandType.values().length ; i++) {
               handRanks.put(HandType.values()[i], 10 - i);
            }
        }

        @Override
        public String toString(){
            return this.handType + ", with highcard " + highestCard.getCardSmall();
        }

        @Override
        public int compareTo(FinalHandRank other) {
            int difference = handRanks.get(this.handType) - handRanks.get(other.handType);
            if (difference == 0) {
                return this.highestCard.compareTo(other.highestCard);
            }
            return difference / Math.abs(difference);
        }
    }

    public class Opponent{
        String name;
        Poker.Position position;
        double bet;
        double balance;
        private final Card[] hand;
        private enum Style{
            AGGRESSIVE, PASSIVE;
        }
        public Opponent(String name, Poker.Position position, Card card1, Card card2){
            this.name = name;
            this.position = position;
            this.balance = 100;
            hand = new Card[]{card1, card2};
        }

        protected Card[] getHand(){
            return this.hand;
        }

        public Option getMove(double highestTableBet){
            if(highestTableBet != 0){
                if(balance >= highestTableBet){
                    balance -= highestTableBet;
                    return Option.CALL;
                } else{
                    return Option.FOLD;
                }
            } else{
                return Option.CHECK;
            }
        }
    }
}