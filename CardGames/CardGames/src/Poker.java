import java.util.*;
import java.util.stream.Collectors;

import Cards.*;
import org.jetbrains.annotations.TestOnly;

public class Poker {
    ArrayList<Card> deck;
    LinkedList<Card> onTable;
    static double balance = 100;
    ArrayList<Card> playerHand;
    Opponent[] opponents;
    ArrayList<Opponent> opponentsPerRound;
    final int numOpponents = 2;
    public enum Option{
        BET, CALL, RAISE, FOLD, CHECK, ALLIN
    }

    public enum HandType{
        ROYALFLUSH, STRAIGHTFLUSH, FOUROFAKIND, FULLHOUSE, FLUSH, STRAIGHT, THREEOFAKIND, TWOPAIR, ONEPAIR, HIGHCARD
    }

    private String[] opponentNames = new String[]{
            "Dan", "Fedor", "Doyle", "Phil", "Tom", "Erik", "Bryn", "Daniel", "Justin", "Stephen", "David", "Antonio"
    };

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
            Option[] options = Poker.Option.values();
            for(int i = 0; i < options.length - 1; i++){//Print all but ALLIN
                System.out.print(options[i]);
            }
            for(Option option : Poker.Option.values()){
                System.out.print(option.toString() + "  ");
            }
            try {
                choice = Option.valueOf(scanner.nextLine().toUpperCase());
                if(choice == Option.ALLIN){
                    throw new IllegalArgumentException();
                }
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
        LinkedList<String> opponentNamesPoppable = Arrays.stream(opponentNames).collect(Collectors.toCollection(LinkedList::new));
        for(int i = 0; i < opponents.length; i++){
            opponents[i] = new Opponent(opponentNamesPoppable.remove((int) (Math.random() * opponentNamesPoppable.size())), Position.BUTTON, deck.remove((int)(Math.random() * deck.size())), deck.remove((int)(Math.random() * deck.size())));//To be changed
        }

        opponentsPerRound = new ArrayList<>();
        opponentsPerRound.addAll(Arrays.asList(opponents));

        for(int i = 0; i < 2; i++) {
            Card randomCard = deck.remove((int)(Math.random() * deck.size()));
            playerHand.add(randomCard);
        }
        System.out.println("\nYour Hand");
        System.out.println(Card.toStringAbreast(playerHand.toArray(Card[]::new)));
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
            }  else if(round == 3) {
                onTable.add(deck.remove((int) (Math.random() * deck.size())));
                activeGame = false;
            }
            round++;
            System.out.println(Card.toStringAbreast(onTable.toArray(Card[]::new)));
            boolean playersTurn = true;
            boolean activeRound = true;
            while (activeRound) {
                activeRound = false;
                while (playersTurn) {
                    System.out.println("You have " + balance + ", with " + pot + " in the pot.");
                    if(balance == 0){
                        System.out.println("You are all in!");
                        playersTurn = false;
                        continue;
                    }
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
                            if (highestBet == 0) {
                                System.out.println("You can't do that!");
                            } else{
                                if(balance < highestBet){
                                    System.out.println("You're all in!");
                                    pot += balance;
                                    balance = 0;
                                } else {
                                    System.out.println("You called the bet.");
                                    pot += highestBet;
                                    balance -= highestBet;
                                }
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
                    Option move = opp.getMove(pot, highestBet, round);
                    if (move == Option.BET || move == Option.RAISE) {
                        System.out.println("Opponent " + opp.name + ": " + move + " to " + opp.bet);
                        highestBet = opp.bet;
                        pot += opp.bet;
                        activeRound = true;
                    } else if(move == Option.FOLD){
                        System.out.println("Opponent " + opp.name + ": " + move);
                        for(int i = 0; i < opponents.length; i++){
                            if(opp.equals(opponents[i])){
                                opponents[i].balance = opp.balance;
                                break;
                            }
                        }
                        opponentsPerRound.remove(opp);
                    } else if(move == Option.CALL) {
                        pot += highestBet;
                        System.out.println("Opponent " + opp.name + ": " + move);
                    }else{
                        System.out.println("Opponent " + opp.name + ": " + move); //checking
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
            return this.handType + ", with the highest card " + highestCard.getCardSmall();
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
        int handRank;
        private final Card[] hand;
        private enum Style{
            AGGRESSIVE, PASSIVE, SNEAKY, BLUFFING, REALLYBAD;
        }
        Style playStyle;

        private static final Map<char[], Integer> cardValuesUnpairedUnsuited = Map.<char[], Integer>ofEntries(
                Map.entry(new char[]{'A', 'K'}, 11),
                Map.entry(new char[]{'A', 'Q'}, 18),
                Map.entry(new char[]{'A', 'J'}, 27),
                Map.entry(new char[]{'A', 'T'}, 42),
                Map.entry(new char[]{'A', '9'}, 19),
                Map.entry(new char[]{'A', '8'}, 91),
                Map.entry(new char[]{'A', '7'}, 97),
                Map.entry(new char[]{'A', '6'}, 113),
                Map.entry(new char[]{'A', '5'}, 96),
                Map.entry(new char[]{'A', '4'}, 104),
                Map.entry(new char[]{'A', '3'}, 109),
                Map.entry(new char[]{'A', '2'}, 117),
                Map.entry(new char[]{'K', 'Q'}, 20),
                Map.entry(new char[]{'K', 'J'}, 31),
                Map.entry(new char[]{'K', 'T'}, 45),
                Map.entry(new char[]{'K', '9'}, 81),
                Map.entry(new char[]{'K', '8'}, 112),
                Map.entry(new char[]{'K', '7'}, 122),
                Map.entry(new char[]{'K', '6'}, 125),
                Map.entry(new char[]{'K', '5'}, 128),
                Map.entry(new char[]{'K', '4'}, 132),
                Map.entry(new char[]{'K', '3'}, 133),
                Map.entry(new char[]{'K', '2'}, 134),
                Map.entry(new char[]{'Q', 'J'}, 35),
                Map.entry(new char[]{'Q', 'T'}, 15),
                Map.entry(new char[]{'Q', '9'}, 83),
                Map.entry(new char[]{'Q', '8'}, 115),
                Map.entry(new char[]{'Q', '7'}, 131),
                Map.entry(new char[]{'Q', '6'}, 137),
                Map.entry(new char[]{'Q', '5'}, 141),
                Map.entry(new char[]{'Q', '4'}, 143),
                Map.entry(new char[]{'Q', '3'}, 144),
                Map.entry(new char[]{'Q', '2'}, 145),
                Map.entry(new char[]{'J', 'T'}, 47),
                Map.entry(new char[]{'J', '9'}, 80),
                Map.entry(new char[]{'J', '8'}, 108),
                Map.entry(new char[]{'J', '7'}, 129),
                Map.entry(new char[]{'J', '6'}, 147),
                Map.entry(new char[]{'J', '5'}, 149),
                Map.entry(new char[]{'J', '4'}, 152),
                Map.entry(new char[]{'J', '3'}, 153),
                Map.entry(new char[]{'J', '2'}, 155),
                Map.entry(new char[]{'T', '9'}, 73),
                Map.entry(new char[]{'T', '8'}, 100),
                Map.entry(new char[]{'T', '7'}, 124),
                Map.entry(new char[]{'T', '6'}, 140),
                Map.entry(new char[]{'T', '5'}, 157),
                Map.entry(new char[]{'T', '4'}, 158),
                Map.entry(new char[]{'T', '3'}, 160),
                Map.entry(new char[]{'T', '2'}, 162),
                Map.entry(new char[]{'9', '8'}, 99),
                Map.entry(new char[]{'9', '7'}, 119),
                Map.entry(new char[]{'9', '6'}, 135),
                Map.entry(new char[]{'9', '5'}, 150),
                Map.entry(new char[]{'9', '4'}, 164),
                Map.entry(new char[]{'9', '3'}, 165),
                Map.entry(new char[]{'9', '2'}, 166),
                Map.entry(new char[]{'8', '7'}, 114),
                Map.entry(new char[]{'8', '6'}, 126),
                Map.entry(new char[]{'8', '5'}, 139),
                Map.entry(new char[]{'8', '4'}, 156),
                Map.entry(new char[]{'8', '3'}, 167),
                Map.entry(new char[]{'8', '2'}, 168),
                Map.entry(new char[]{'7', '6'}, 121),
                Map.entry(new char[]{'7', '5'}, 130),
                Map.entry(new char[]{'7', '4'}, 146),
                Map.entry(new char[]{'7', '3'}, 161),
                Map.entry(new char[]{'7', '2'}, 169),
                Map.entry(new char[]{'6', '5'}, 123),
                Map.entry(new char[]{'6', '4'}, 136),
                Map.entry(new char[]{'6', '3'}, 148),
                Map.entry(new char[]{'6', '2'}, 163),
                Map.entry(new char[]{'5', '4'}, 127),
                Map.entry(new char[]{'5', '3'}, 138),
                Map.entry(new char[]{'5', '2'}, 151),
                Map.entry(new char[]{'4', '3'}, 142),
                Map.entry(new char[]{'4', '2'}, 154),
                Map.entry(new char[]{'3', '2'}, 159)
        );

        private static final Map<char[], Integer> cardValuesUnpairedSuited = Map.<char[], Integer>ofEntries(
                Map.entry(new char[]{'A', 'K'}, 4),
                Map.entry(new char[]{'A', 'Q'}, 6),
                Map.entry(new char[]{'A', 'J'}, 8),
                Map.entry(new char[]{'A', 'T'}, 12),
                Map.entry(new char[]{'A', '9'}, 19),
                Map.entry(new char[]{'A', '8'}, 24),
                Map.entry(new char[]{'A', '7'}, 30),
                Map.entry(new char[]{'A', '6'}, 34),
                Map.entry(new char[]{'A', '5'}, 28),
                Map.entry(new char[]{'A', '4'}, 32),
                Map.entry(new char[]{'A', '3'}, 33),
                Map.entry(new char[]{'A', '2'}, 39),
                Map.entry(new char[]{'K', 'Q'}, 7),
                Map.entry(new char[]{'K', 'J'}, 9),
                Map.entry(new char[]{'K', 'T'}, 14),
                Map.entry(new char[]{'K', '9'}, 22),
                Map.entry(new char[]{'K', '8'}, 37),
                Map.entry(new char[]{'K', '7'}, 44),
                Map.entry(new char[]{'K', '6'}, 53),
                Map.entry(new char[]{'K', '5'}, 55),
                Map.entry(new char[]{'K', '4'}, 58),
                Map.entry(new char[]{'K', '3'}, 60),
                Map.entry(new char[]{'K', '2'}, 59),
                Map.entry(new char[]{'Q', 'J'}, 13),
                Map.entry(new char[]{'Q', 'T'}, 49),
                Map.entry(new char[]{'Q', '9'}, 25),
                Map.entry(new char[]{'Q', '8'}, 43),
                Map.entry(new char[]{'Q', '7'}, 61),
                Map.entry(new char[]{'Q', '6'}, 66),
                Map.entry(new char[]{'Q', '5'}, 69),
                Map.entry(new char[]{'Q', '4'}, 71),
                Map.entry(new char[]{'Q', '3'}, 72),
                Map.entry(new char[]{'Q', '2'}, 75),
                Map.entry(new char[]{'J', 'T'}, 16),
                Map.entry(new char[]{'J', '9'}, 26),
                Map.entry(new char[]{'J', '8'}, 41),
                Map.entry(new char[]{'J', '7'}, 64),
                Map.entry(new char[]{'J', '6'}, 79),
                Map.entry(new char[]{'J', '5'}, 82),
                Map.entry(new char[]{'J', '4'}, 86),
                Map.entry(new char[]{'J', '3'}, 87),
                Map.entry(new char[]{'J', '2'}, 89),
                Map.entry(new char[]{'T', '9'}, 23),
                Map.entry(new char[]{'T', '8'}, 38),
                Map.entry(new char[]{'T', '7'}, 57),
                Map.entry(new char[]{'T', '6'}, 74),
                Map.entry(new char[]{'T', '5'}, 93),
                Map.entry(new char[]{'T', '4'}, 95),
                Map.entry(new char[]{'T', '3'}, 96),
                Map.entry(new char[]{'T', '2'}, 98),
                Map.entry(new char[]{'9', '8'}, 40),
                Map.entry(new char[]{'9', '7'}, 54),
                Map.entry(new char[]{'9', '6'}, 68),
                Map.entry(new char[]{'9', '5'}, 88),
                Map.entry(new char[]{'9', '4'}, 106),
                Map.entry(new char[]{'9', '3'}, 107),
                Map.entry(new char[]{'9', '2'}, 111),
                Map.entry(new char[]{'8', '7'}, 48),
                Map.entry(new char[]{'8', '6'}, 62),
                Map.entry(new char[]{'8', '5'}, 78),
                Map.entry(new char[]{'8', '4'}, 94),
                Map.entry(new char[]{'8', '3'}, 116),
                Map.entry(new char[]{'8', '2'}, 118),
                Map.entry(new char[]{'7', '6'}, 56),
                Map.entry(new char[]{'7', '5'}, 67),
                Map.entry(new char[]{'7', '4'}, 85),
                Map.entry(new char[]{'7', '3'}, 103),
                Map.entry(new char[]{'7', '2'}, 120),
                Map.entry(new char[]{'6', '5'}, 63),
                Map.entry(new char[]{'6', '4'}, 70),
                Map.entry(new char[]{'6', '3'}, 90),
                Map.entry(new char[]{'6', '2'}, 100),
                Map.entry(new char[]{'5', '4'}, 65),
                Map.entry(new char[]{'5', '3'}, 77),
                Map.entry(new char[]{'5', '2'}, 92),
                Map.entry(new char[]{'4', '3'}, 84),
                Map.entry(new char[]{'4', '2'}, 97),
                Map.entry(new char[]{'3', '2'}, 105)
        );

        private static final Map<Character, Integer> cardValuesPaired = Map.ofEntries(
                Map.entry('A', 1),
                Map.entry('K', 2),
                Map.entry('Q', 3),
                Map.entry('J', 5),
                Map.entry('T', 10),
                Map.entry('9', 17),
                Map.entry('8', 21),
                Map.entry('7', 29),
                Map.entry('6', 36),
                Map.entry('5', 46),
                Map.entry('4', 50),
                Map.entry('3', 52),
                Map.entry('2', 51)
                );
        public Opponent(String name, Poker.Position position, Card card1, Card card2){
            this.name = name;
            this.position = position;
            this.balance = 100;
            hand = new Card[]{card1, card2};
            handRank = evaluateHand();
            if(handRank <= 100){
                if(Math.random() <= 0.8){
                    this.playStyle = Style.REALLYBAD;
                } else{
                    this.playStyle = Style.BLUFFING;
                }
            } else{
                if(handRank >= 25){
                    if(Math.random() <= 0.5){
                        this.playStyle = Style.AGGRESSIVE;
                    } else{
                        this.playStyle = Style.SNEAKY;
                    }
                } else{
                    if(Math.random() <= 0.75){
                        this.playStyle = Style.PASSIVE;
                    } else{
                        this.playStyle = Style.BLUFFING;
                    }
                }
            }
        }

        protected Card[] getHand(){
            return this.hand;
        }


        private boolean isSuited(){
            return hand[0].getSuit() == hand[1].getSuit();
        }

        private boolean isPaired(){
            return hand[0].getRank() == hand[1].getRank();
        }

        /**
         * Evaluation of a given players hand to determine playstyles
         * Evaluation given by "<a href="https://www.gamblingsites.org/poker/texas-holdem/starting-hand-rankings/#top">...</a>"
         * Switch statements used to
         * @return an integer representing the value of the hand (1 - 169, 1 being best)
         */
        private int evaluateHand() {
            Card[] hand = Arrays.stream(this.hand).sorted().toArray(Card[]::new);
            if(isPaired()){
                return cardValuesPaired.get(hand[0].getRank());
            } else{
                if(isSuited()){
                    return cardValuesUnpairedSuited.get(new char[]{hand[0].getRank(), hand[1].getRank()});
                } else{
                    return cardValuesUnpairedUnsuited.get(new char[]{hand[0].getRank(), hand[1].getRank()});
                }
            }
        }

        /**
         * To make sure all values are entered correctly and distinctly in the hand ranks
         * @return boolean to make sure all is in order
         */
        @TestOnly
        public static boolean cardValueMapTest(){
            boolean truth = (cardValuesUnpairedUnsuited.size() + cardValuesPaired.size() + cardValuesUnpairedSuited.size()) == 169;
            LinkedList<Integer> values = new LinkedList<>(cardValuesPaired.values());
            values.addAll(cardValuesUnpairedSuited.values());
            values.addAll(cardValuesUnpairedUnsuited.values());
            values = values.stream().sorted().collect(Collectors.toCollection(LinkedList::new));
            return truth && values.size() == 169;
        }


        public Option getMove(double pot, double highestTableBet, int roundNumber){//rnd 3 is river
            switch(playStyle){
                case SNEAKY:
                    if(highestTableBet > this.bet){//Always call
                        double difference = highestTableBet - this.bet;
                        if(difference >= balance){
                            balance = 0;
                            return Option.ALLIN;
                        } else{
                            return Option.CALL;
                        }
                    } else{
                        switch(roundNumber){
                            case 1:
                                if(pot == 0){
                                    return Option.CHECK;
                                } else{
                                    if(highestTableBet > 0){

                                    }
                                }
                        }
                    }
                    break;
                case PASSIVE:
                    break;
                case BLUFFING:
                    break;
                case REALLYBAD:
                    break;
                case AGGRESSIVE:
                    break;
                case null:
                    System.err.println("Invalid playStyle for opponent " + this.name);
            }
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