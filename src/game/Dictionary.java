package game;

public class Dictionary {
    private static final char[] first = new char[] {'А', 'Б', 'И', 'Т', 'У', 'Р', 'И', 'Е', 'Н', 'Т'};
    private static final char[] second = new char[] {'В', 'О', 'Л', 'Ш', 'Е', 'Б', 'С', 'Т', 'В', 'О'};
    private static final char[] third = new char[] {'Д', 'А', 'Г', 'Е', 'С', 'Т', 'А', 'Н', 'Е', 'Ц'};
    private static final char[] fourth = new char[] {'Г', 'У', 'Б', 'Е', 'Р', 'Н', 'А', 'Т', 'О', 'Р'};
    private static final char[] fiveth = new char[] {'И', 'З', 'Б', 'И', 'Р', 'А', 'Т', 'Е', 'Л', 'Ь'};
    private static final char[] sixth = new char[] {'И', 'Н', 'Ф', 'О', 'Р', 'М', 'А', 'Т', 'И', 'К'};
    private static final char[] seventh = new char[] {'К', 'И', 'Н', 'О', 'П', 'Л', 'Е', 'Н', 'К', 'А'};
    private static final char[] eighth = new char[] {'Л', 'Е', 'Г', 'И', 'Т', 'И', 'М', 'И', 'З', 'М'};
    private static final char[] nineth = new char[] {'П', 'О', 'Т', 'О', 'Ч', 'Н', 'О', 'С', 'Т', 'Ь'};
    private static final char[] tenth = new char[] {'М', 'И', 'Р', 'О', 'Т', 'В', 'О', 'Р', 'Е', 'Ц'};


    public static char[] getRandomWordForGame(int key) {
        switch (key)
        {
            case 48:
            case 0:
            {
                return first;
            }

            case 49:
            case 1:
            {
                return second;
            }
            case 50:
            case 2:
            {
                return third;
            }

            case 51:
            case 3:
            {
                return fourth;
            }

            case 52:
            case 4:
            {
                return fiveth;
            }

            case 53:
            case 5:
            {
                return sixth;
            }

            case 54:
            case 6:
            {
                return seventh;
            }

            case 55:
            case 7:
            {
                return eighth;
            }

            case 56:
            case 8:
            {
                return nineth;
            }

            case 57:
            case 9:
            {
                return tenth;
            }

            default:
                return new char[0];
        }
    }
}
