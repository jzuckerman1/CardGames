package Cards;
public class Card {
    Suit suit;
    Rank rank;

    public enum Suit {
        DIAMOND, HEART, SPADE, CLUB;
    }

    public Card(Suit suit, String rank){
        try {
            this.rank = new Rank(rank);
        }catch(IllegalArgumentException e){
            this.rank = null;
        }
        this.suit = suit;
    }

    public char getRank(){
        return this.rank.getRank();
    }


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
        ----------------
                        """;
    }

    static class Rank{
        private enum HighRank{
            J, Q, K, A
        }
        private char rank;
        public Rank(String value) {
            try{
                int integerValue = Integer.parseInt(value);
                if (integerValue > 1 && integerValue < 10) {
                    rank = value.charAt(0);
                }
            }catch(NumberFormatException e){
                try {
                    HighRank.valueOf(value);
                    rank = value.charAt(0);
                }catch(IllegalArgumentException itsATen){
                    rank = 'T';
                }
            }
        }
        public char getRank(){
            return this.rank;
        }
    }
}
