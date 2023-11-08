import java.util.Arrays;
public class RunGame{
    public static void main(String[] args){
        int sum = 0;
        int games = 10000;
        int fastest = 100;
        int slowest = 0;
        for(int i = 0; i < games; i++){
            int gameTime = play();
            sum += gameTime;
            if (fastest > gameTime) {
                fastest = gameTime;
            } else if (slowest < gameTime) {
                slowest = gameTime;
            }
            System.out.println(i);
        }
        double avg = (double)sum/games;
        System.out.println("Avg is " + avg);
        System.out.println("Fastest is " + fastest);
        System.out.println("Slowest is " + slowest);
    }

    public static int play(){
        final int DIMENSIONS = 10;
        char[][] s1Guesses = new char[DIMENSIONS][DIMENSIONS];
        String[] history = new String[DIMENSIONS * DIMENSIONS];

        for(int row = 0; row < DIMENSIONS; row++){
            for(int col = 0; col < DIMENSIONS; col++){
                s1Guesses[row][col] = '.';
            }
        }

        int[][] gameBoard = new int[DIMENSIONS][DIMENSIONS];
        BattleShipTools.randomBoard(gameBoard);
        // BattleShipTools.problemBoard(gameBoard);
        //BattleShipTools.printBoard(gameBoard);
        int moves;

        for(moves = 1; moves < DIMENSIONS*DIMENSIONS+1; moves++){
            //copy array before passing to students
            char[][] s1Copy = Arrays.copyOf(s1Guesses, s1Guesses.length);

            String guess1 = Admiral.fire(s1Copy);
            history[moves - 1] = guess1;
            boolean p1 = BattleShipTools.updateGuess(s1Guesses,guess1,gameBoard);
            //break if the player won
            if(p1){
                break;
            }
            // System.out.println("----------------------------------------------------");
            // BattleShipTools.printBoard(s1Guesses);
        }

        if(moves == DIMENSIONS * DIMENSIONS + 1){
            System.out.println("Out of Moves");
        }

        //System.out.println("Completed in " + moves + " moves");
        /*
        for(int i = 0; i < history.length; i++){
        if(i % 10 == 0)
        System.out.println();
        if(history[i] != null){
        System.out.print(history[i] + ", ");
        }

        }
         */
        //System.out.println();
        //  BattleShipTools.printBoard(s1Guesses);

        return moves;
    }

}
