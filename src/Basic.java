import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.stream.Stream;

public class Basic {
    public static String fire(char[][] board) {
        /*
            ‘.’ – no guess yet
            ‘O’ – miss
            ‘X’ – hit
            ‘1’ – Patrol Boat, has length 2, and has been sunk
            ‘2’ – Submarine, has length 3, and has been sunk
            ‘3’ – Destroyer, has length 3, and has been sunk
            ‘4’ – Battleship, has length 4, and has been sunk
            ‘5’ – Aircraft carrier, has length 5, and has been sunk
         */

        boolean containsWoundedShip = false;
        HashMap deadShips = new HashMap<Character, Integer>();
        deadShips.put('1', 2);
        deadShips.put('2', 3);
        deadShips.put('3', 3);
        deadShips.put('4', 4);
        deadShips.put('5', 5);

        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[row].length; col++) {
                if (board[row][col] == 'X') {
                    containsWoundedShip = true;
                    break;
                } else if (Character.isDigit(board[row][col])) {
                    deadShips.remove(board[row][col]);
                }
            }
        }

        int[] shot;

        if (containsWoundedShip) {
            shot = kill(board);
        } else {
            shot = randomShot(board, deadShips);
        }

        return formatGuess(shot[0], shot[1]);
    }

    public static int[] hunt(char[][] board, boolean[] deadShips) {
        return new int[]{0, 0};
    }

    //try and finish off a wounded ship
    public static int[] kill(char[][] board) {
        int[] coords = {0, 0};
        int[][] wounds = new int[15][2];
        int index = 0;


        for (int row = 0; row < board.length; row++){
            for (int col = 0; col < board[row].length; col++) {
                if (board[row][col] == 'X') {
                    wounds[index][0] = row;
                    wounds[index][1] = col;
                    index++;
                }
            }
        }

        for (int[] wound : wounds) {
            //check above
            if (wound[0] > 0 && board[wound[0] - 1][wound[1]] == '.') {
                coords[0] = wound[0] - 1;
                coords[1] = wound[1];
                break;

                //check right
            } else if (wound[1] < board[0].length - 1 && board[wound[0]][wound[1] + 1] == '.') {
                coords[0] = wound[0];
                coords[1] = wound[1] + 1;
                break;

                //check down
            } else if (wound[0] < board.length - 1 && board[wound[0] + 1][wound[1]] == '.') {
                coords[0] = wound[0] + 1;
                coords[1] = wound[1];
                break;

                //check left
            } else if (wound[1] > 0 && board[wound[0]][wound[1] - 1] == '.') {
                coords[0] = wound[0];
                coords[1] = wound[1] - 1;
                break;
            }
        }

        return coords;
    }

    public static int[] randomShot(char[][] board, HashMap deadShips) {
        int[] coords = {0, 0};

        int smallestShip = (int) Collections.min(deadShips.values());
        smallestShip = 2;
        //TODO(fix this)

        while (board[coords[0]][coords[1]] != '.') {
            if (coords[1] + smallestShip <= 9) {
                coords[1] += smallestShip;
            } else {
                coords[0]++;
                coords[1] = coords[0] % 2;
            }

            //we may end up with unchecked vertical strips
            //currently unreachable because the smallestShip is broken
            if (coords[1] > 9 || coords[0] > 9) {
                coords[0] = 0;
                coords[1] = 0;

                while (board[coords[0]][coords[1]] != '.') {
                    if (coords[0] + smallestShip <= 9) {
                        coords[0] += smallestShip;
                    } else {
                        coords[1]++;
                        coords[0] = coords[1] % 2;
                    }
                }

            }

        }

        return coords;
    }


    //change indices into proper guess format
    public static String formatGuess(int row, int col) {
        String guess = "";
        guess += (char) ('A' + row);
        guess += String.valueOf(col + 1);
        return guess;
    }
}
