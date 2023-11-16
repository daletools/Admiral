import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

public class Oracle {

    public static String makeGuess(char[][] board) {
        //Hold coordinates to parse
        int[] coordinates = {0, 0};

        //Assemble fleet
        HashMap<Character, Integer> fleet = new HashMap<>();
        fleet.put('1', 2);
        fleet.put('2', 3);
        fleet.put('3', 3);
        fleet.put('4', 4);
        fleet.put('5', 5);

        int[][] heatMap = buildHeatMap(board, fleet);

        return formatGuess(coordinates[0], coordinates[1]);
    }

    public static String formatGuess(int row, int col) {
        //just changes my x,y zero-indexed coordinates into corresponding battleship ones
        String guess = "";
        guess += (char) ('A' + row);
        guess += String.valueOf(col + 1);
        return guess;
    }

    //The closer to 100 or 0% a cell has of being occupied, the higher the certainty of the cell
    public static double certainty(int possibilities, int totalPossibilities) {
        double percent = (double) possibilities / totalPossibilities * 100;
        return (Math.pow(percent, 2) / 25) - (4 * percent) + 100;
    }

    public static int[][] buildHeatMap(char[][] board, HashMap<Character, Integer> ships) {
        int[][] probabilityBoard = new int[10][10];

        //For each ship, find all valid placements and increment the corresponding cells on the probability board.
        for (int ship : ships.values().stream().sorted(Comparator.reverseOrder()).toList()) {
            for (int row = 0; row < board.length; row++) {
                //Just join to string each row and filter out the bits we don't want
                String fullRow = Arrays.toString(board[row]).replaceAll("[, \\[\\]]", "");

                for (int col = 0; col < board[row].length - ship + 1; col++) {
                    //looking for substrings that are only comprised of '.', of the length of our ship.
                    String space = fullRow.substring(col, col + ship);

                    if (space.matches("^\\.*$")) {
                        for (int i = 0; i < ship; i++) {
                            //increment each space the ship would cover
                            probabilityBoard[row][col + i]++;
                            //probabilityBoard[row][col + i] *= ship;
                        }
                    }
                }
            }

            //do it all again but vertically
            for (int col = 0; col < board.length; col++) {
                String fullCol = "";
                //little trickier to get the column string, but all the rest is the same
                for (int row = 0; row < board.length; row++) {
                    fullCol += board[row][col];
                }
                for (int row = 0; row < board.length - ship + 1; row++) {
                    String space = fullCol.substring(row, row + ship);
                    if (space.matches("^\\.*$")) {
                        for (int i = 0; i < ship; i++) {
                            probabilityBoard[row + i][col]++;
                        }
                    }
                }
            }
        }
        return probabilityBoard;
    }
}
